package ca.ipredict.predictor.Markov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Predictor;

/**
 * First-order markov model
 */
public class MarkovFirstOrderPredictor implements Predictor {

	
	private HashMap<Integer, MarkovState> mDictionary; //contains a list of unique items and their state in the Markov model
	
	private List<Sequence> mTrainingSequences; //list of sequences to test
	
	private int count;
	
	@Override
	public void setTrainingSequences(List<Sequence> trainingSequences) {
		mTrainingSequences = trainingSequences;
	}

	@Override
	public Boolean Preload() {
		count = 0;
		mDictionary = new HashMap<Integer, MarkovState>();
		
		//for each sequence in the training set
		for(Sequence seq : mTrainingSequences) {
			
			//for each items in this sequence, but the last one
			List<Item> items = seq.getItems();
			for(int i = 0 ; i < (items.size() - 1); i++) {
				
				//Getting or creating the state associated with this item
				MarkovState state = mDictionary.get(items.get(i).val);
				if(state == null) {
					state = new MarkovState();
				}
				
				//Adding the transition to the next item
				state.addTransition(items.get(i + 1).val);
				
				//Saving the changes into the dictionary
				mDictionary.put(items.get(i).val, state);
			}
			
		}
		//TODO: change the return value
		return true;
	}

	@Override
	public Sequence Predict(Sequence target) {
		
		//Getting the last item in the target sequence
		Item lastItem = target.get(target.size() - 1);
		
		
		MarkovState state = mDictionary.get(lastItem.val);
		if(state == null) {
			return new Sequence(-1);
		}

		Integer nextState = state.getBestNextState();
		Sequence predicted = new Sequence(-1);
		predicted.addItem(new Item(nextState));

		return predicted;
	}

	@Override
	public String getTAG() {
		return "1Mark";
	}
	
	public long size() {
		return mDictionary.keySet().size();
	}
	
	public static void main(String[] args) {
		
		
		MarkovFirstOrderPredictor predictor = new MarkovFirstOrderPredictor();
		
		//Training sequences
		List<Sequence> training = new ArrayList<Sequence>();
		//1 2 3
		Sequence seq1 = new Sequence(-1);
		seq1.addItem(new Item(1));
		seq1.addItem(new Item(2));
		seq1.addItem(new Item(3));
		training.add(seq1);
		
		//1 3 4
		Sequence seq2 = new Sequence(-1);
		seq2.addItem(new Item(1));
		seq2.addItem(new Item(3));
		seq2.addItem(new Item(4));
		training.add(seq2);
		
		//2 3 4
		Sequence seq3 = new Sequence(-1);
		seq3.addItem(new Item(2));
		seq3.addItem(new Item(3));
		seq3.addItem(new Item(4));
		training.add(seq3);
		
		//4 3 1
		Sequence seq4 = new Sequence(-1);
		seq4.addItem(new Item(4));
		seq4.addItem(new Item(3));
		seq4.addItem(new Item(1));
		training.add(seq4);
		
		predictor.setTrainingSequences(training);
		predictor.Preload();
		
		//Testing
		Sequence seqT = new Sequence(-1);
		seqT.addItem(new Item(8));
		seqT.addItem(new Item(3));
		
		Sequence result = predictor.Predict(seqT);
		
		System.out.println(result.toString());
	}
	
	
}
