import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifierImpl implements NaiveBayesClassifier {

	//THESE VARIABLES ARE OPTIONAL TO USE, but HashMaps will make your life much, much easier on this assignment.

	//dictionaries of form word:frequency that store the number of times word w has been seen in documents of type label
	//for example, comedyCounts["mirth"] should store the total number of "mirth" tokens that appear in comedy documents
	private HashMap<String, Integer> tragedyCounts = new HashMap<String, Integer>();
	private HashMap<String, Integer> comedyCounts = new HashMap<String, Integer>();
	private HashMap<String, Integer> historyCounts = new HashMap<String, Integer>();

	//prior probabilities, ie. P(T), P(C), and P(H)
	//use the training set for the numerator and denominator
	private double tragedyPrior;
	private double comedyPrior;
	private double historyPrior;

	//int for total size
	int trainingSize = -1;

	//total number of word TOKENS for each type of document in the training set, ie. the sum of the length of all documents with a given label
	private int tTokenSum;
	private int cTokenSum;
	private int hTokenSum;

	//full vocabulary, update in training, cardinality is necessary for smoothing
	private HashSet<String> vocabulary = new HashSet<String>();


	/**
	 * Trains the classifier with the provided training data
   Should iterate through the training instances, and, for each word in the documents, update the variables above appropriately.
   The dictionary of frequencies and prior probabilites can then be used at classification time.
	 */
	@Override
	public void train(Instance[] trainingData) {
		// TODO : Implement
		trainingSize = trainingData.length;
		//for each instance in the training data
		for( int i = 0;  i < trainingData.length; i++){
			//Current Instance
			Instance currInstance = trainingData[i];

			if(currInstance.label.name().equals("COMEDY")){
				comedyPrior++;
			}else if(currInstance.label.name().equals("TRAGEDY")){
				tragedyPrior++;
			}else{
				historyPrior++;
			}

			//for each word in the document
			for(int r = 0; r  < currInstance.words.length; r++){

				if(currInstance.label.name().equals("TRAGEDY")){
					tTokenSum++;
					//If hashmap contains the key update
					if(tragedyCounts.containsKey( currInstance.words[r] )){
						//Get the value at the mapped key
						int update = tragedyCounts.get(currInstance.words[r]).intValue();
						//Put an incremented value in its place
						tragedyCounts.put(currInstance.words[r], ++update);
					}//Else put it in the map
					else{
						tragedyCounts.put(currInstance.words[r], 1);

						//Check and add to vocabulary
						if(vocabulary.contains(currInstance.words[r]) == false){
							vocabulary.add(currInstance.words[r]);
						}
					}

				}
				else if(currInstance.label.name().equals("HISTORY")){
					hTokenSum++;
					if(historyCounts.containsKey( currInstance.words[r] )){
						//Get the value at the mapped key
						int update = historyCounts.get(currInstance.words[r]).intValue();
						//Put an incremented value in its place
						historyCounts.put(currInstance.words[r], ++update);
					}//Else put it in the map
					else{
						historyCounts.put(currInstance.words[r], 1);
						//Check and add to vocabulary
						if(vocabulary.contains(currInstance.words[r]) == false){
							vocabulary.add(currInstance.words[r]);
						}
					}
				}
				else if(currInstance.label.name().equals("COMEDY")){
					cTokenSum++;
					if(comedyCounts.containsKey( currInstance.words[r] )){
						//Get the value at the mapped key
						int update = comedyCounts.get(currInstance.words[r]).intValue();
						//Put an incremented value in its place
						comedyCounts.put(currInstance.words[r], ++update);
					}//Else put it in the map
					else{
						comedyCounts.put(currInstance.words[r], 1);
						//Check and add to vocabulary
						if(vocabulary.contains(currInstance.words[r]) == false){
							vocabulary.add(currInstance.words[r]);
						}
					}
				}
			}
		}
		//Set the probabilities
		comedyPrior = (comedyPrior/trainingSize);
		historyPrior = (historyPrior/trainingSize);
		tragedyPrior = (tragedyPrior/trainingSize);
	}

	/*
	 * Prints out the number of documents for each label
	 * A sanity check method
	 */
	public void documents_per_label_count(){
		// TODO : Implement
		System.out.println("TRAGEDY = " + tragedyPrior * trainingSize);
		System.out.println("COMEDY = " + comedyPrior * trainingSize);
		System.out.println("HISTORY = " + historyPrior * trainingSize);

	}

	/*
	 * Prints out the number of words for each label
	Another sanity check method
	 */
	public void words_per_label_count(){
		// TODO : Implement
		System.out.println( "TRAGEDY = " + tTokenSum );
		System.out.println( "COMEDY = " +  cTokenSum );
		System.out.println( "HISTORY = " + hTokenSum );
	}

	/**
	 * Returns the prior probability of the label parameter, i.e. P(COMEDY) or P(TRAGEDY)
	 */
	@Override
	public double p_l(Label label) {
		// TODO : Implement
		double result = 0.0;
		if ( label.name().equals("COMEDY") )
			result = comedyPrior;
		else if ( label.name().equals("TRAGEDY"))
			result = tragedyPrior;
		else if ( label.name().equals("HISTORY"))
			result = historyPrior;
		return result;
	}

	/**
	 * Returns the smoothed conditional probability of the word given the label, i.e. P(word|COMEDY) or
	 * P(word|HISTORY)
	 */
	@Override
	public double p_w_given_l(String word, Label label) {
		//summation term 
		double summationTerm = -1;
		//Result to return 
		double retur = -1;
		//Token number of the word
		double token = 0;
		if(label.name().equals("COMEDY")){
			//Check if contains to avoid null pointer
			if(comedyCounts.containsKey(word)){
				token = comedyCounts.get(word);
			}
			summationTerm = cTokenSum;
		}else if (label.name().equals("HISTORY")){
			if(historyCounts.containsKey(word)){
				token = historyCounts.get(word);
			}
			summationTerm = hTokenSum;
		}else if(label.name().equals("TRAGEDY")){
			if(tragedyCounts.containsKey(word)){
				token = tragedyCounts.get(word);
			}	
			summationTerm = tTokenSum;
		}
		//Total vocab size
		int vocabSize = vocabulary.size();

		retur = (token + 0.00001) / ((vocabSize * 0.00001) + summationTerm);

		return retur;
	}

	/**
	 * Classifies a document as either a Comedy, History, or Tragedy.
   Break ties in favor of labels with higher prior probabilities.
	 */
	@Override
	public Label classify(Instance ins) {

		// TODO : Implement

		//Initialize sum probabilities for each label
		double history = 0;
		double comedy = 0;
		double tragedy = 0;
		//For each word w in document ins
		for(int i = 0; i < ins.words.length; i++){
			//compute the log (base e or default java log) probability of w|label for all labels (COMEDY, TRAGEDY, HISTORY)
			history += Math.log(p_w_given_l(ins.words[i] , Label.HISTORY));

	
			comedy +=  Math.log(p_w_given_l(ins.words[i] , Label.COMEDY));	

			tragedy +=  Math.log(p_w_given_l(ins.words[i] , Label.TRAGEDY));
		}
		//Add the other probability
		history += (Math.log(p_l(Label.HISTORY)));
		comedy += (Math.log(p_l(Label.COMEDY)));
		tragedy += (Math.log(p_l(Label.TRAGEDY)));
		//Return the Label of the maximal sum probability
		if( Math.max(comedy, Math.max(tragedy, history)) == comedy){
			return Label.COMEDY;
		}
		if( Math.max(comedy, Math.max(tragedy, history)) == tragedy){
			return Label.TRAGEDY;
		}
		else 
			return Label.HISTORY;
	}


}
