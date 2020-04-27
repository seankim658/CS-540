

public class featureData {
	
	public String feature;
	public String label;
	public int featureAmt = 0;
	public int feature_Label = 0;
	public int feature_NotLabel = 0;
	public float infoGain = 0;
	public String value;
	
	public featureData() {
		// TODO Auto-generated constructor stub
		
	}
	
	public void setValue(String value){
		this.value = value;
	}
	public String getValue(){
		return this.value;
	}
	public void setLabel(String Label){
		this.label = Label;
	}
	public String getLabel(){
		return this.label;
	}
	public void setFeature_Name(String name){
		this.feature = name;
	}
	
	public void incrementFeatureAmt(){
		this.featureAmt++;
	}
	public void Feature_Label_increment(){
		 this.feature_Label++;
	}
	public void notFeature_Label_increment(){
		this.feature_NotLabel++;
	}
	public String getFeature_Name(){
		return this.feature;
	}
	public void setInfoGain(float infoGain){
		this.infoGain = infoGain;
	}
	public float getInfoGain(){
		return this.infoGain;
	}
	
	
//	public int compareTo(featureData other) {
//		int ret;
//		if(this.infoGain > other.getInfoGain()){
//			ret = 1;
//		}
//		else if(this.getInfoGain() == other.getInfoGain()){
//			ret = 0;
//		}
//		else{
//			ret = -1;
//		}
//		return ret;
//	     
//	}

}
