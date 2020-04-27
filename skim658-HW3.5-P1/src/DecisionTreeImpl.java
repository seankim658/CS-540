import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * 
 * You must add code for the 1 member and 5 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
	private DecTreeNode root;
	//ordered list of class labels
	private List<String> labels; 
	//ordered list of attributes
	private List<String> attributes; 
	//map to ordered discrete values taken by attributes
	private Map<String, List<String>> attributeValues; 
	private Map<DecTreeNode, String> dictionary;
	
	private List<Instance> instances;

	String majLabel;

	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary this is void purposefully
	}

	/**
	 * Build a decision tree given only a training set.
	 * 
	 * @param train: the training set
	 */
	DecisionTreeImpl(DataSet train) {

		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;

		List<Instance> instances = train.instances;
		List<String> attributesCopy =  new ArrayList<String>(train.attributes);

		float labelOneCount = 0;
		float labelTwoCount = 0;
		String labelOne = train.instances.get( 0 ).label;
		String labelTwo = "";

		for ( int i = 0; i < train.instances.size(); i++ ) {
			if ( train.instances.get( i ).equals( labelOne ) ) {
				labelOneCount++;
			} else {
				labelTwo = train.instances.get( i ).label;
				labelTwoCount++;
			}
		}


		if ( labelOneCount == labelTwoCount ) {
			majLabel = labelOne;
		} else if ( labelOneCount > labelTwoCount ) {
			majLabel = labelOne;
		} else {
			majLabel = labelTwo;
		}

		//Get the attribute with the max info gain
		//String max = maxInfoGain( instances, attributesCopy );
		//root = new DecTreeNode ( null, max, null, false );

		//for(String attrVal : this.attributeValues.get( max ) ){

		//Divide the examples
		//List<Instance> newInstanceList  = updateInstanceList(instances , max , attrVal );
		//Might have to remove max from attribute List

		root = buildTree( train.instances, attributesCopy, majLabel, "" );

		//}
	}

	/**
	 * Build a decision tree given a training set then prune it using a tuning set.
	 * 
	 * @param train: the training set
	 * @param tune: the tuning set
	 */
	DecisionTreeImpl(DataSet train, DataSet tune) {

		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		this.instances = train.instances;
		// TODO: add code here
		//Build the tree
		//List<Instance> instances = train.instances;
		List<String> attributesCopy =  new ArrayList<String>(train.attributes);

		float labelOneCount = 0;
		float labelTwoCount = 0;
		String labelOne = train.instances.get( 0 ).label;
		String labelTwo = "";

		for ( int i = 0; i < train.instances.size(); i++ ) {
			if ( train.instances.get( i ).equals( labelOne ) ) {
				labelOneCount++;
			} else {
				labelTwo = train.instances.get( i ).label;
				labelTwoCount++;
			}
		}
		if ( labelOneCount == labelTwoCount ) {
			majLabel = labelOne;
		} else if ( labelOneCount > labelTwoCount ) {
			majLabel = labelOne;
		} else {
			majLabel = labelTwo;
		}

		root = buildTree( train.instances, attributesCopy, majLabel, "" );

		root = prune(root, tune);

	}

	//Method to prune the tree, unsure about return
	public DecTreeNode prune (DecTreeNode subTree , DataSet tune){
		
		//intitalize  tempSubTree accuracy
		double tempSubTreeAccuracy = 1;
		//While accuracy continues to improve
		double baseAccuracy = getAccuracy(tune);
		while(baseAccuracy < tempSubTreeAccuracy ){
			
			//Compute the inital accuracy
		//	double currAccuracy  = getAccuracy(tune);
			
			//Calculate the amount of internal nodes
			List<DecTreeNode> everyInternal = calcInternalNodes( subTree );
			//for every internalNode of subTree remove nodes child Copy
			this.root = everyInternal.get(0);
			for(int i = 1; i < everyInternal.size(); i++){
				//Copy the root
				DecTreeNode rootCopy = new DecTreeNode(subTree.label, subTree.attribute, subTree.parentAttributeValue, subTree.terminal);
				rootCopy.children = new ArrayList<DecTreeNode>(subTree.children);
				//Copy the subTree
				everyInternal.get(i).terminal = true;
				
				//everyInternal.get(i).label = majLabel;
				
				
			//	everyInternal.get(i).label = majLabel;
				//Calculate the newSubTreeAccuracy
				tempSubTreeAccuracy = getAccuracy(tune);				
		//		System.out.println("tempSubTreeAccuracy: " + tempSubTreeAccuracy + " subTreeAccuracy: " + baseAccuracy);
				//Get the max of TN's and T
				if(tempSubTreeAccuracy >= baseAccuracy){
					baseAccuracy = tempSubTreeAccuracy;
				//	rootCopy = root;
					//rootCopy.children = root.children;
					root = rootCopy;
					root.children = rootCopy.children;
					everyInternal.get( i ).terminal = false;
				} else {
				//	everyInternal.get(i).terminal = true;
					break;
				}
			}
		}

		return root;
	}
	//DFS TREE WITH STACK
	public List<DecTreeNode> calcInternalNodes(DecTreeNode node){
		//Create a queue for traversal and list
		List<DecTreeNode> nonTerminal = new ArrayList<DecTreeNode>();
		Queue<DecTreeNode> queue = new LinkedList<DecTreeNode>();

		//Add the root queue
		queue.add(node);
		//While queue is not emtpy
		while(!queue.isEmpty()){
			//Take the front of queue
			DecTreeNode curr = queue.poll();
			//If the node is not terminal add it to the list
			if(curr.terminal == false){
				nonTerminal.add(curr);
			}
			//If no children move to next iteration
			else{
				continue;
			}
			//Add all children

			for(int i = 0; i < curr.children.size(); i++){
				//check if the children is null or nah before adding
				if(curr.children.get(i) != null){
					queue.add(curr.children.get(i));
				}
			}
		}
		return nonTerminal;
	}

	public DecTreeNode buildTree( List<Instance> examples, List<String> attributes, String defaultLabel, 
			String parentAttributeVal ) {


		if ( examples.isEmpty() ) {
			DecTreeNode ret = new DecTreeNode( defaultLabel, null, parentAttributeVal, true );
			return ret;
		}

		boolean allSameLabel = true;
		String label = examples.get( 0 ).label;
		for ( int i = 1; i < examples.size(); i++ ) {
			if ( !( examples.get( i ).label.equals( examples.get( 0 ).label ) ) ) {
				allSameLabel = false;
				break;
			}
		}
		if ( allSameLabel ) {
			DecTreeNode ret = new DecTreeNode( examples.get(0).label, null, parentAttributeVal, true );  // UNSURE 
			//			System.out.println("------------LEAF NODE----------------");
			//			System.out.println("Leaf label being returned: " + ret.label );
			//			//System.out.println("Leaf Node Parent: " + parent.attribute + " Parent Attribute Val: " + parent.parentAttributeValue);
			//			System.out.println("Examples Size: " + examples.size());
			//			System.out.println("-----------allSameLabel---------------");
			//			System.out.println();
			return ret;
		}

		float labelOneCount = 0;
		float labelTwoCount = 0;

		String labelOne = examples.get(0).label;
		String labelTwo = " ";

		for(int i =  0; i < examples.size(); i++){
			if(examples.get(i).label.equals(labelOne) ) {
				labelOneCount++;
			} else {
				labelTwoCount++;
				labelTwo = examples.get(i).label;
			}
		}

		if(labelOneCount == labelTwoCount ){
			majLabel = labelOne;

		}

		if(labelOneCount > labelTwoCount){
			majLabel = labelOne;

			//majLabelCount = labelOneCount;
		}
		if(labelTwoCount > labelOneCount){
			majLabel = labelTwo;
			//majLabelCount = labelTwoCount;
		}


		if ( attributes.isEmpty() ) {
			DecTreeNode ret = new DecTreeNode( majLabel, null, parentAttributeVal, true );  // UNSURE 
			//			System.out.println("------------LEAF NODE----------------");
			//			System.out.println("Leaf label being returned: " + ret.label );
			//			//System.out.println("Leaf Node Parent: " + parent.attribute + " Parent Attribute Val: " + parent.parentAttributeValue);
			//			System.out.println("--------------Attrubtes Empty--------------------");
			//			System.out.println();
			return ret;
		}
		//Get Max Info
		String maxInfo = maxInfoGain( examples, attributes );

		//Gets the values of maxInfo Atrribute
		List<String> valuesOfMaxInfo = attributeValues.get(maxInfo);

		DecTreeNode currNode = new DecTreeNode( majLabel, maxInfo, parentAttributeVal, false );


		//For each attribute value
		//for(String attrVal : this.attributeValues.get( maxInfo ) ){
		for(int t = 0; t < valuesOfMaxInfo.size(); t++){

			//	List<Instance> newInstanceList  = updateInstanceList(examples , maxInfo ,maxAttributeValue );
			List<Instance> newInstanceList  = updateInstanceList(examples , maxInfo , valuesOfMaxInfo.get(t) );
			//If the examples size is zero return the current Node

			//System.out.println("");
			//System.out.println("Attribute List Size: " +  attributes.size());
			List<String> attributesCopy = new ArrayList<String>(attributes);

			attributesCopy.remove(maxInfo);


			currNode.addChild( buildTree( newInstanceList, attributesCopy , majLabel , valuesOfMaxInfo.get(t)));

		}

		return currNode;
		//return null;
	}

	public String maxInfoGain( List<Instance> examples, List<String> attributes ) {

		// not calculating actual info gain, just smallest conditional entropy ( AKA largest info gain )
		float leastCondEntropy = 1000;
		float currCondEntropy;
		String attribute = "";
		for ( int i = 0; i < attributes.size(); i++ ) {
			currCondEntropy = calcCondEntropy( examples, attributes.get( i ) );
			if ( currCondEntropy < leastCondEntropy ) {
				leastCondEntropy = currCondEntropy;
				attribute = attributes.get( i );
			}
		}

		//attributes.remove( attribute );

		return attribute;
	}

	public float calcCondEntropy( List<Instance> instances,   String attribute ){

		int index = this.getAttributeIndex(attribute);

		ArrayList<featureData> attrVales = new ArrayList<featureData>();

		for(int i = 0; i < instances.size(); i++){
			Instance currInst = instances.get(i);

			String val = currInst.attributes.get(index); 

			boolean foundAttribute = false;

			for(int r = 0; r < attrVales.size(); r++){
				if(attrVales.get(r).getFeature_Name().equals(val)){
					foundAttribute = true;
					attrVales.get(r).incrementFeatureAmt();
					if(currInst.label.equals(majLabel)){
						attrVales.get(r).Feature_Label_increment();

					}
					else{

						attrVales.get(r).notFeature_Label_increment();
					}
				}
			}

			if(foundAttribute == false){
				featureData newFeature = new featureData();

				newFeature.setFeature_Name(val);

				newFeature.incrementFeatureAmt();

				if(currInst.label.equals(majLabel)){
					newFeature.Feature_Label_increment();	
					newFeature.setLabel(majLabel);
				}else{
					newFeature.notFeature_Label_increment();

					newFeature.setLabel(currInst.label);
				}
				attrVales.add(newFeature);
			}
		}

		float totalEntropy = 0;

		for(int y = 0; y < attrVales.size(); y++){
			float featAmt = attrVales.get(y).featureAmt;
			float featLabel = attrVales.get(y).feature_Label;
			float featNotLabel = attrVales.get(y).feature_NotLabel;	

			float hasLabelEntropy = 0;
			float noLabelEntropy = 0;

			if(!(featLabel == 0)){
				hasLabelEntropy = (float) -((featLabel/featAmt)*((Math.log(featLabel/featAmt) / Math.log(2))));	
			}
			if(!(featNotLabel == 0)){
				noLabelEntropy = (float)  -((featNotLabel/featAmt) * ((Math.log(featNotLabel/featAmt) / Math.log(2))));
			}
			totalEntropy = (totalEntropy + ((featAmt/instances.size())*(hasLabelEntropy + noLabelEntropy)));
		}


		return totalEntropy;
	}

	public String calcMinCondEntropyValue( List<Instance> instances,   String attribute , List<String> valuesOfMaxInfo){

		//Find the index of the attribute for each instance
		int index = this.getAttributeIndex(attribute);

		//Create a new list of feature DAta
		ArrayList<featureData> attrVales = new ArrayList<featureData>();

		//Go through all the instances and create and update featureData
		for(int i = 0; i < instances.size(); i++){
			Instance currInst = instances.get(i);

			String val = currInst.attributes.get(index); 

			boolean foundAttribute = false;

			for(int r = 0; r < attrVales.size(); r++){
				if(attrVales.get(r).value.equals(val)){
					foundAttribute = true;
					attrVales.get(r).incrementFeatureAmt();
					if(currInst.label.equals(majLabel)){
						attrVales.get(r).Feature_Label_increment();

					}
					else{

						attrVales.get(r).notFeature_Label_increment();
					}
				}
			}
			//Create a new feature data if the attribute value has not been found 
			if(foundAttribute == false){

				featureData newFeature = new featureData();

				//Set the feature name
				newFeature.setFeature_Name(attribute);

				newFeature.setValue(val);

				newFeature.incrementFeatureAmt();

				if(currInst.label.equals(majLabel)){
					newFeature.Feature_Label_increment();	
					newFeature.setLabel(majLabel);
				}else{
					newFeature.notFeature_Label_increment();

					newFeature.setLabel(currInst.label);
				}
				attrVales.add(newFeature);
			}
		}

		//float totalEntropy = 0;
		//Calculate the minEntropy
		float minEntropy = 1000;
		String attributeVal = "Penis!!!";
		for(int y = 0; y < attrVales.size(); y++){
			float featAmt = attrVales.get(y).featureAmt;
			float featLabel = attrVales.get(y).feature_Label;
			float featNotLabel = attrVales.get(y).feature_NotLabel;	

			float hasLabelEntropy = 0;
			float noLabelEntropy = 0;

			if(!(featLabel == 0)){
				hasLabelEntropy = (float) -((featLabel/featAmt)*((Math.log(featLabel/featAmt) / Math.log(2))));	
			}
			if(!(featNotLabel == 0)){
				noLabelEntropy = (float)  -((featNotLabel/featAmt) * ((Math.log(featNotLabel/featAmt) / Math.log(2))));
			}
			//Gets the value with the lowest conditional Entropy
			float currCondEntropy = ((featAmt/instances.size())*(hasLabelEntropy + noLabelEntropy));
			if(currCondEntropy < minEntropy){
				minEntropy = currCondEntropy;
				attributeVal = attrVales.get(y).value;
			}
			//totalEntropy = (totalEntropy + );
		}




		return attributeVal;
	}

	@Override
	public String classify( Instance instance) {
		//Create a copy of the root
		//	DecTreeNode currNode = new DecTreeNode(root.label, root.attribute, root.parentAttributeValue, root.terminal);
		DecTreeNode currNode = root;
		//Loop through the currNodes children
		while(currNode.children.size() != 0){
			boolean terminal = false;
			for(int i = 0; i < currNode.children.size(); i++ ){
				//Get the index of the current nodes attribute
				int index  = this.getAttributeIndex(currNode.attribute);
				//Attribute value of the instance
				String attributeVal = instance.attributes.get(index);
				//Get instance attribute value of instance
				if(attributeVal.equals((currNode.children.get(i).parentAttributeValue))){

					currNode = currNode.children.get(i);		//break;
					if(currNode.terminal == true){
						terminal = true;
						break;
					}
				}
			}
			if(terminal){
				break;
			}
		}

		return currNode.label;
		// TODO: add code here
	}

	@Override
	public void rootInfoGain(DataSet train) {
		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;

		if(train.labels.isEmpty()){
			System.out.println("Training set was null, return default label");
		}

		boolean allSame = true;
		for(int i = 0; i < train.instances.size(); i++){
			if(train.instances.get(i).label != train.instances.get(0).label){
				allSame = false;		
			}
			if(allSame == false){
				break;
			}
		}

		if(allSame == true){
			classify(train.instances.get(0));
		}

		float labelOneCount = 0;
		float labelTwoCount = 0;
		String labelOne = train.labels.get(0);
		String labelTwo = " ";

		for(int i =  0; i < train.instances.size(); i++){
			if(train.instances.get(i).label.equals(labelOne) ) {
				labelOneCount++;
			} else {
				labelTwoCount++;
				labelTwo = train.instances.get(i).label;
			}
		}

		float majLabelCount;

		if(labelOneCount == labelTwoCount ){
			majLabel = labelOne;
			majLabelCount = labelOneCount;
		}

		if(labelOneCount > labelTwoCount){
			majLabel = labelOne;

			majLabelCount = labelOneCount;
		}
		else{
			majLabel = labelTwo;
			majLabelCount = labelTwoCount;

		}


		//System.out.print("Label one count: " + labelOneCount + "  " + labelOne + "  " );
		//System.out.println("Label Two count: " + labelTwoCount + "  " + labelTwo );

		//If empty classify feature as maj label
		//FIX THIS
		if(train.attributes.isEmpty()){
			//classify();
		}

		int labelListSize = train.instances.size();

		float label1Entropy = (float) -((majLabelCount/labelListSize) * (Math.log(majLabelCount/labelListSize) / Math.log(2)));
		float label2Entropy = (float) -(((labelListSize - majLabelCount)/labelListSize) * ((Math.log((labelListSize - majLabelCount)/labelListSize)) / Math.log(2)));

		float totalEntropy = label1Entropy + label2Entropy;


		for ( int i = 0; i < this.attributes.size(); i++ ) {

			float conditionalEntropy = calcCondEntropy(train.instances, this.attributes.get(i));

			float infoGain = totalEntropy - conditionalEntropy;
			System.out.print( this.attributes.get(i) + " ");
			System.out.format("%.5f", infoGain);
			System.out.println();

		}
	}

	@Override
	/**
	 * Print the decision tree in the specified format
	 */
	public void print() {

		printTreeNode(root, null, 0);
	}

	/**
	 * Prints the subtree of the node with each line prefixed by 4 * k spaces.
	 */
	public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < k; i++) {
			sb.append("    ");
		}
		String value;
		if (parent == null) {
			value = "ROOT";
		} else {
			int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
			value = attributeValues.get(parent.attribute).get(attributeValueIndex);
		}
		sb.append(value);
		if (p.terminal) {
			sb.append(" (" + p.label + ")");
			System.out.println(sb.toString());
		} else {
			sb.append(" {" + p.attribute + "?}");
			System.out.println(sb.toString());
			for (DecTreeNode child : p.children) {
				printTreeNode(child, p, k + 1);
			}
		}
	}

	//Loops through a list of instances , if  instance has 
	//a feature with featureVale that does not equal value of parent delete it from instances
	public List<Instance> updateInstanceList(List<Instance> instanceList , String feature , String featureVal ){
		//index of the attribute
		//Patch work fix
		//System.out.println("Feature: " + feature  + " Removing Instances with out: " + featureVal);
		int index = this.getAttributeIndex(feature);
		//List to return
		List<Instance> returnList = new ArrayList<Instance>();
		//Loop through the instance list and remove any instances with the currFeatures attributeVall
		//Loop to test Attribute size

		//+ "  Attribute Value (Path Being Taken): " + featureVal);
		//System.out.println("List Size before removal: " + instanceList.size() );
		int amt = 0;
		for(int r = 0; r < instanceList.size(); r++){
			if((instanceList.get(r).attributes.get(index)).equals(featureVal)){
				amt++;		
				//instanceList.remove(r);
				returnList.add(instanceList.get(r));
			}
		}
		//System.out.println("Amount that does match: " + amt );
		//System.out.println("List Size after removal: " + instanceList.size() );
		//System.out.println("ReturnList Size: " + returnList.size());
		//System.out.println("");
		//actual code
		//		for(int i = 0; i < instanceList.size(); i++){
		//			if(!instanceList.get(i).attributes.get(index).equals(featureVal)){
		//				instanceList.remove(i);				
		//			}
		//		}	

		return returnList;
	}

	/**
	 * Helper function to get the index of the label in labels list
	 */
	private int getLabelIndex(String label) {
		for (int i = 0; i < this.labels.size(); i++) {
			if (label.equals(this.labels.get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Helper function to get the index of the attribute in attributes list
	 */
	private int getAttributeIndex(String attr) {
		for (int i = 0; i < this.attributes.size(); i++) {
			if (attr.equals(this.attributes.get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Helper function to get the index of the attributeValue in the list for the attribute key in the attributeValues map
	 */
	private int getAttributeValueIndex(String attr, String value) {
		for (int i = 0; i < attributeValues.get(attr).size(); i++) {
			if (value.equals(attributeValues.get(attr).get(i))) {
				return i;
			}
		}
		return -1;
	}


	/**
   /* Returns the accuracy of the decision tree on a given DataSet.
	 */
	@Override
	public double getAccuracy(DataSet ds){
		//TODO, compute accuracy
		//Loop though all the instances
		double numCorrect = 0;
		for(int i = 0; i < ds.instances.size(); i++){

			//Get the tree classify label
			String label = classify(ds.instances.get(i));
			//if it equals the the actual instance label inc numCorrect
			if(label.equals(ds.instances.get(i).label)){
				numCorrect++;
			}
		}
		double accuracy = numCorrect / (double)(ds.instances.size());
		//System.out.println("numCorrect: " + numCorrect + " Total Instances: " + ds.instances.size());

		return accuracy;
	}
}
