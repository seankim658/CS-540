/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 * 
 */

import java.util.*;


public class NNImpl{
	public ArrayList<Node> inputNodes=null;//list of the output layer nodes.
	public ArrayList<Node> hiddenNodes=null;//list of the hidden layer nodes
	public Node outputNode=null;// single output node that represents the result of the regression

	public ArrayList<Instance> trainingSet=null;//the training set

	Double learningRate=1.0; // variable to store the learning rate
	int maxEpoch=1; // variable to store the maximum number of epochs


	/**
	 * This constructor creates the nodes necessary for the neural network
	 * Also connects the nodes of different layers
	 * After calling the constructor the last node of both inputNodes and  
	 * hiddenNodes will be bias nodes. 
	 */

	public NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch, Double [][]hiddenWeights, Double[] outputWeights)
	{
		this.trainingSet=trainingSet;
		this.learningRate=learningRate;
		this.maxEpoch=maxEpoch;

		//input layer nodes
		inputNodes=new ArrayList<Node>();
		int inputNodeCount=trainingSet.get(0).attributes.size();
		int outputNodeCount=1;
		for(int i=0;i<inputNodeCount;i++)
		{
			Node node=new Node(0);
			inputNodes.add(node);
		}

		//bias node from input layer to hidden
		Node biasToHidden=new Node(1);
		inputNodes.add(biasToHidden);

		//hidden layer nodes
		hiddenNodes=new ArrayList<Node> ();
		for(int i=0;i<hiddenNodeCount;i++)
		{
			Node node=new Node(2);
			//Connecting hidden layer nodes with input layer nodes
			for(int j=0;j<inputNodes.size();j++)
			{
				NodeWeightPair nwp=new NodeWeightPair(inputNodes.get(j),hiddenWeights[i][j]);
				node.parents.add(nwp);
			}
			hiddenNodes.add(node);
		}

		//bias node from hidden layer to output
		Node biasToOutput=new Node(3);
		hiddenNodes.add(biasToOutput);



		Node node=new Node(4);
		//Connecting output node with hidden layer nodes
		for(int j=0;j<hiddenNodes.size();j++)
		{
			NodeWeightPair nwp=new NodeWeightPair(hiddenNodes.get(j), outputWeights[j]);
			node.parents.add(nwp);
		}	
		outputNode = node;

	}

	/**
	 * Get the output from the neural network for a single instance. That is, set the values of the training instance to
	the appropriate input nodes, percolate them through the network, then return the activation value at the single output
	node. This is your estimate of y. 
	 */

	public double calculateOutputForInstance(Instance inst)
	{
		//Set the  vals of training instance to input nodes
		for(int i = 0; i < inst.attributes.size(); i++){	
			//Set each input node the appropraite attribue
			inputNodes.get(i).setInput(inst.attributes.get(i));
		}

		//Calculate for each hidden layer node
		for(int i = 0; i < hiddenNodes.size(); i++){	

			hiddenNodes.get(i).calculateOutput();		

		}

		outputNode.calculateOutput();

		//return, may need to change to 1 or 0, based on output
		return outputNode.getOutput();
	}





	/**
	 * Trains a neural network with the parameters initialized in the constructor for the number of epochs specified in the instance variable maxEpoch.
	 * The parameters are stored as attributes of this class, namely learningRate (alpha) and trainingSet.
	 * Implement stochastic graident descent: update the network weights using the deltas computed after each the error of each training instance is computed.
	 * An single epoch looks at each instance training set once, so you should update weights n times per epoch if you have n instances in the training set.
	 */

	public void train()
	{
		//List<Double> hiddenWeights = new ArrayList <Double>();
		double [][] hiddenWeights = new double [hiddenNodes.size()] [inputNodes.size()];
		
		//List for the output weights
	//	List<Double> outputWeights = new ArrayList <Double>();
		double [] outputWeights = new double[hiddenNodes.size()];
		
		double biasHidden = 0.0;
		double [] inputBias = new double[hiddenNodes.size() - 1];
		
		//Set the number of iterations to maxEpoch
		int i = 0;
		while( i < maxEpoch){	

			//TEST CODE
		//	System.out.println("--------------------Epoch Number: " + i + " ----------------------" );
			//for each example in the training set
			for(int r = 0;  r < trainingSet.size(); r++){
				outputWeights = new double[hiddenNodes.size()];
			
				hiddenWeights = new double [hiddenNodes.size()] [inputNodes.size()];
				
				inputBias = new double[hiddenNodes.size() - 1];
				biasHidden = 0.0;
			
				//Current instance
				Instance currInst = trainingSet.get(r);
				//Compute the NNs output, and targetOutput calcOutputforInstance updates inputs	
				double outputVal =  calculateOutputForInstance( currInst );
				//Calculate the error
				double error =  currInst.output - outputVal;
				//For output to hidden update weight
				for(int x = 0; x < outputNode.parents.size(); x++){
					double deltaOne = deltaHiddenToOutput(error, outputNode.parents.get(x).node );
					outputWeights[x] = deltaOne;
			//		outputWeights.add(deltaOne);
			//		outputNode.parents.get(x).weight += deltaOne;
				}
				double outputReLu = 0.0;
				if ( outputNode.getSum() > 0) {
					outputReLu = 1;
				}
				biasHidden = learningRate * error * outputReLu; 
				//For each hidden node, minus one for bias
				for(int q = 0; q < hiddenNodes.size() - 1; q++){
					//Hidden to update
					Node currHidden = hiddenNodes.get(q);
					//Rectifed Linear derivative for hidden
					double currHiddenReLu = 0.0;
					if(currHidden.getSum() > 0){
						currHiddenReLu = 1;
					}
					
					double outputWeight = outputNode.parents.get(q).weight;
					
					for(int w = 0; w < currHidden.parents.size(); w++){
						//Update the weight of the current hidden with its parent	
						double delta = deltaInputToHidden(error, currHidden.parents.get(w).node, outputWeight, currHiddenReLu); 
						
						hiddenWeights[q][w] = delta;
		//				currHidden.parents.get(w).weight = (currHidden.parents.get(w).weight + delta);
					}
					
					inputBias[q] = learningRate * error * currHiddenReLu * outputReLu * outputWeight;
				}

				//Update the output nodes
				for(int f = 0; f < outputNode.parents.size(); f++){
					outputNode.parents.get(f).weight = outputNode.parents.get(f).weight + outputWeights[f]; 
				}
				
				//Update the hidden node weights
				for(int d = 0; d < hiddenNodes.size() -1 ; d++){
					for(int h = 0; h < hiddenNodes.get(d).parents.size(); h++){
						//Update the weight
						hiddenNodes.get(d).parents.get(h).weight = hiddenNodes.get(d).parents.get(h).weight + hiddenWeights[d][h];
					}
				}
				
	/*			outputNode.parents.get( hiddenNodes.size() - 1 ).weight += biasHidden; 
				
				for ( int k = 0; k < hiddenNodes.size() - 1; k++ ) {
					hiddenNodes.get( k ).parents.get( inputNodes.size() - 1 ).weight += inputBias[k];
				} */
			}	 
			i++;
		} 
	}
	//Computes delta for hidden to outputs
	public double deltaHiddenToOutput(double error, Node parent){
		//intalize delta
		double delta = 0.0;
		//compute relu
		double OutputReLu = 0.0;
		//Change from parent.getOutput to outputNode.getSum
		if(outputNode.getSum() >  0){
			OutputReLu = 1;
		}
		parent.calculateOutput();
		//compute delta
		delta = learningRate * error * parent.getOutput() * OutputReLu;		
		return delta;

	}
	//computes delta for Input to hidden 
	public double deltaInputToHidden(double error, Node parent, double outputWeight, double hiddenReLu){
		//initalize delta
		double delta = 0.0;
		//compute ReLu
		double OutputReLu  = 0.0;
		//change from parent.getOutput to output.getSum
		if(outputNode.getSum() > 0){
			OutputReLu = 1;
		}
		
		//Calculate delta
		delta = learningRate * parent.getOutput() * hiddenReLu * outputWeight * error * OutputReLu;
		return delta;
	}




	/**
	 * Returns the mean squared error of a dataset. That is, the sum of the squared error (T-O) for each instance
	in the dataset divided by the number of instances in the dataset.
	 */
	public double getMeanSquaredError(List<Instance> dataset){

		double squaredError = 0.0;
		//For each instance in the dataset
		for(int i = 0; i < dataset.size(); i++){

			double targetVal = dataset.get(i).output;
			double outputVal = calculateOutputForInstance(dataset.get(i));		

			//Target minus output sqaured
			squaredError += Math.pow( (targetVal - outputVal) , 2);

		}

		//Compute the mean
		double meanSqauredError = squaredError / dataset.size();

		return meanSqauredError;
	}
}
