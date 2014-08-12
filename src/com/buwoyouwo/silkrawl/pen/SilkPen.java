package com.buwoyouwo.silkrawl.pen;

import java.util.Random;

import com.buwoyouwo.silkrawl.pen.SilkPenScrawl.Path;
import com.buwoyouwo.util.vector.Integer2;

public class SilkPen implements IPen {
	
	Config config;
	
	//Status
	int colorR;
	int colorG;
	int colorB;
	int alpha;
	Node[] nodeList;
	SilkPenScrawl scrawl = new SilkPenScrawl();
	
	double rotateSpeed;

	@Override	//params must be a instance of SilkPenConfig
	public IScrawl scrawl(Object operation) {
		if(operation == null){
			return null;
		}
		if(operation instanceof Operation){
			Operation op = (Operation)operation;
			
			if(op.isDown()){
				//A new scrawl
				initNodes(op.getPosition());
				applyNoises();
				
				scrawl = new SilkPenScrawl();
				scrawl.setMirrorHorizontal(config.mirrorHorizontal);
				scrawl.setMirrorVertical(config.mirrorVertical);
				scrawl.setRotate(config.rotate);
				scrawl.setSpiral(config.spiral);
				
				SilkPenScrawl.Path path = generateNewPath();
				
				scrawl.addPath(path);
				return scrawl;
			} else if(op.isUp()){
				return null;
			} else if(op.isPushed()){
				if(nodeList == null) { initNodes(op.getPosition()) ;}
				setNibPosition(op.getPosition());
				applyChainEffect();
				applyNoises();
				
				SilkPenScrawl.Path path = generateNewPath();
				scrawl.addPath(path);
				return scrawl;
			} else{
				return null;
			}
			
		} else{
			return null;
		}
		
	}



	@Override
	public void config(Object configuration) {
		if(configuration instanceof Config){
			this.config = (Config) configuration;
			colorR = config.colorR;
			colorG = config.colorG;
			colorB = config.colorB;
			alpha = config.alpha;
		}
		return ;
	}
	
	
	
	public Config getConfig() {
		return config;
	}
	public void setConfig(Config config) {
		this.config = config;
	}
	public int getColorR() {
		return colorR;
	}
	public void setColorR(int colorR) {
		this.colorR = colorR;
	}
	public int getColorG() {
		return colorG;
	}
	public void setColorG(int colorG) {
		this.colorG = colorG;
	}
	public int getColorB() {
		return colorB;
	}
	public void setColorB(int colorB) {
		this.colorB = colorB;
	}
	public int getAlpha() {
		return alpha;
	}
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}



	private void initNodes(Integer2 position){
		this.nodeList = new Node[config.getNodeNum()];
		nodeList[0] = new Node();
		nodeList[0].setPosition(position);
		int segLength = config.chainLength / nodeList.length;
		for(int i = 1; i < nodeList.length; i++){
			nodeList[i] = new Node();
			nodeList[i].getPosition().setX(nodeList[i-1].getPosition().getX() + segLength);
			nodeList[i].getPosition().setY(position.getY());
		}
		Random random = new Random();
		double rtt = 2*(random.nextDouble()-0.5)*2*Math.PI;
		rotateNodes(rtt);
		
		int averageSegLength = config.chainLength / config.nodeNum;
		double dispatchRatio = 0.5;
		int min = (int) (averageSegLength * dispatchRatio);
		int max = averageSegLength + averageSegLength - min;
		int stage = (max - min) / (config.nodeNum - 2);
		for(int i = 1; i < nodeList.length; i++){
			nodeList[i].segLength = min + (i-1)*stage;
		}
		
		for(int i = 1; i < nodeList.length; i++){
			int newValue = nodeList[i].position.getX();
			newValue = (int) generateNoise(newValue, newValue, nodeList[i].segLength, nodeList[i].segLength, Double.MAX_VALUE, -Double.MAX_VALUE, random);
			nodeList[i].position.setX(newValue);
			newValue = nodeList[i].position.getY();
			newValue = (int) generateNoise(newValue, newValue, nodeList[i].segLength, nodeList[i].segLength, Double.MAX_VALUE, -Double.MAX_VALUE, random);
			nodeList[i].position.setY(newValue);
		}
	}
	
	private Integer2[] generateScrawlPath(){
		Integer2[] path = new Integer2[nodeList.length];
		
		for(int i = 0; i < path.length; i++){
			path[i] = new Integer2();
			path[i].copy(nodeList[i].getPosition());
		}
		
		return path;
	}
	
	private Path generateNewPath(){
		SilkPenScrawl.Path path = new SilkPenScrawl.Path();
		path.setAlpha(alpha);
		path.setColorR(colorR);
		path.setColorG(colorG);
		path.setColorB(colorB);
		path.setNodeList(this.generateScrawlPath());
		
		return path;
	}
	
	
	private void setNibPosition(Integer2 position){
		nodeList[0].setPosition(position);
	}
	
//	private void applyInertiaEffect(){
//		for(Node node : nodeList){
//			node.velocity.multiply(config.inertia);
//		}
//	}
	
	private void applyChainEffect(){
//		int segLength = config.chainLength / nodeList.length;
		for(int i = 1; i < nodeList.length; i++){
			int segLength = nodeList[i].segLength;
			//Apply ineria effect
			nodeList[i].velocity.multiply(config.inertia);
			
			//Apply elastic effect
			double length = Integer2.distance(nodeList[i].position, nodeList[i-1].position);
			Integer2 diff = Integer2.minus(nodeList[i-1].position, nodeList[i].position);
//			if(length > segLength){
				diff.multiply((length - segLength) / length);
				diff.multiply(config.elastic);
				nodeList[i].velocity.plus(diff);
//			}
			
			//Apply velocity limit
			if(nodeList[i].velocity.getX() > config.velocityUpperBound){
				nodeList[i].velocity.setX(config.velocityUpperBound);
			}
			else if(nodeList[i].velocity.getX() < -config.velocityUpperBound){
				nodeList[i].velocity.setX(-config.velocityUpperBound);
			}
			if(nodeList[i].velocity.getY() > config.velocityUpperBound){
				nodeList[i].velocity.setY(config.velocityUpperBound);
			}
			else if(nodeList[i].velocity.getY() < -config.velocityUpperBound){
				nodeList[i].velocity.setY(-config.velocityUpperBound);
			}
			
			//Apply velocity effect
			nodeList[i].position.plus(nodeList[i].velocity);
			
			//Apply shrink effect
			length = Integer2.distance(nodeList[i].position, nodeList[i-1].position);
			if(length > segLength){
				diff = Integer2.minus(nodeList[i-1].position, nodeList[i].position);
				diff.multiply((length - segLength) / length);
				diff.multiply(config.shrink);
				nodeList[i].position.plus(diff);
			}
		}
	}
	
	private void applyNoises(){
		//Color noise;
		Random random = new Random();
		
		colorR = (int) generateNoise(colorR, config.colorR, config.colorNoiseStep, config.colorNoiseRange, 255, 0, random);
		colorG = (int) generateNoise(colorG, config.colorG, config.colorNoiseStep, config.colorNoiseRange, 255, 0, random);
		colorB = (int) generateNoise(colorB, config.colorB, config.colorNoiseStep, config.colorNoiseRange, 255, 0, random);
		
		//RotateNoise;
		rotateSpeed = generateNoise(rotateSpeed, 0, config.rotateNoiseStep, config.rotateNoiseRange, Double.MAX_VALUE, -Double.MAX_VALUE, random);
		rotateNodes(rotateSpeed);
		
		//Node noise
		for(int i = 1; i < nodeList.length; i++){
			int newValue = nodeList[i].position.getX();
			newValue = (int) generateNoise(newValue, newValue, config.nodeNoiseStep, config.nodeNoiseRange, Double.MAX_VALUE, -Double.MAX_VALUE, random);
			nodeList[i].position.setX(newValue);
			newValue = nodeList[i].position.getY();
			newValue = (int) generateNoise(newValue, newValue, config.nodeNoiseStep, config.nodeNoiseRange, Double.MAX_VALUE, -Double.MAX_VALUE, random);
			nodeList[i].position.setY(newValue);
		}
	}
	
	private double generateNoise(double currentValue, 
								double configValue,
								double step,
								double range,
								double upperBound,
								double lowerBound,
								Random random){
		if(random == null){
			random = new Random();
		}
		double offset = random.nextDouble();
		offset = (offset - 0.5) * 2;
		offset *= step;
		double newValue = offset + currentValue;
//		System.out.println("New value:" + newValue);
		if(newValue - configValue > range){
			newValue = configValue + range;
		} else if(newValue - configValue < -range){
			newValue = configValue - range;
		}
		
		if(newValue > upperBound){
			newValue = upperBound;
		} else if(newValue < lowerBound){
			newValue = lowerBound;
		}
		
		return newValue;
	}
	
	
	private void rotateNodes(double theta){
		for(int i = 1; i < nodeList.length; i++){
			nodeList[i].position.rotate(nodeList[0].position, theta);
		}
	}
	
	
	/**
	 * Configuration bean of SilkPen
	 * @author buwoyouwo
	 *
	 */
	public static class Config{
		//Pen chain structure
		int nodeNum = 7;					//node number of the pen chain, a value above 2
		int chainLength = 300;				//total length of pen chain
		float elastic = 0.6f;				//A value above 0 and below 1.0f. 
											//The higher elastic coefficient is , the stronger elasticity the chain generates
		float inertia = 0.7f;				//A value above 0 and below 1.0f.
											//The higher inertia is , the harder to changer the velocity of nodes
		float shrink = 0.3f;				//A value above 0 and below 1.0f.
											//The higher shrink is , the faster the chain can shrink to its original length
		int velocityUpperBound = 70;		//
		
		//Pen chain color
		int colorR = 0;	//ARGB color red,	a value between 0 and 255
		int colorG = 255;	//ARGB color green,	a value between 0 and 255
		int colorB = 255;	//ARGB color blue,	a value between 0 and 255
		int alpha = 60;	//ARGB color alpha,	a value between 0 and 255
		
		//Noise
		int nodeNoiseRange = 10;		//Node position noise effects in this range
		int nodeNoiseStep = 3;			//Node position noise effects by this step		
		int colorNoiseRange = 255;		//Color noise effects in this range
		int colorNoiseStep = 30;			//Color noise effects by this step
		double rotateNoiseRange = 0.02;	//Path rotate noise effects in this range
		double rotateNoiseStep = 0.003;	//Path rotate noise effects by this step
		
		//Symmetric
		boolean mirrorVertical = false;		//Vertical mirror symmetric
		boolean mirrorHorizontal = false;	//Horizontal mirror symmetric
		int rotate = 1;						//Rotation symmetric. 1 for invalid
		int spiral = 1;						//Spiral symmetric. 1 for invalid
		
		//Getters and Setters
		public int getNodeNum() {
			return nodeNum;
		}
		public void setNodeNum(int nodeNum) {
			this.nodeNum = nodeNum;
			if(this.nodeNum < 3){ this.nodeNum = 3; }
		}
		public int getChainLength() {
			return chainLength;
		}
		public void setChainLength(int chainLength) {
			this.chainLength = chainLength;
			if(this.chainLength < 0) { this.chainLength = 10; }
		}
		public float getElastic() {
			return elastic;
		}
		public void setElastic(float elastic) {
			this.elastic = elastic;
			if(this.elastic < 0) { this.elastic = 0; }
			if(this.elastic > 1) { this.elastic = 1; }
		}
		public float getInertia() {
			return inertia;
		}
		public void setInertia(float inertia) {
			this.inertia = inertia;
			if(this.inertia < 0) { this.inertia = 0; }
			if(this.inertia > 1) { this.inertia = 1; }
		}
		public float getShrink() {
			return shrink;
		}
		public void setShrink(float shrink) {
			this.shrink = shrink;
			if(this.shrink < 0) { this.shrink = 0; }
			if(this.shrink > 1) { this.shrink = 1; }
		}
		public int getColorR() {
			return colorR;
		}
		public void setColorR(int colorR) {
			this.colorR = colorR;
		}
		public int getColorG() {
			return colorG;
		}
		public void setColorG(int colorG) {
			this.colorG = colorG;
		}
		public int getColorB() {
			return colorB;
		}
		public void setColorB(int colorB) {
			this.colorB = colorB;
		}
		public int getAlpha() {
			return alpha;
		}
		public void setAlpha(int alpha) {
			this.alpha = alpha;
		}
		public int getNodeNoiseRange() {
			return nodeNoiseRange;
		}
		public void setNodeNoiseRange(int nodeNoiseRange) {
			this.nodeNoiseRange = nodeNoiseRange;
			if(this.nodeNoiseRange < 0) { this.nodeNoiseRange = 0;}
		}
		public int getNodeNoiseStep() {
			return nodeNoiseStep;
		}
		public void setNodeNoiseStep(int nodeNoiseStep) {
			this.nodeNoiseStep = nodeNoiseStep;
		}
		public int getColorNoiseRange() {
			return colorNoiseRange;
		}
		public void setColorNoiseRange(int colorNoiseRange) {
			this.colorNoiseRange = colorNoiseRange;
			if(this.colorNoiseRange < 0) { this.colorNoiseRange = 0; }
		}
		public int getColorNoiseStep() {
			return colorNoiseStep;
		}
		public void setColorNoiseStep(int colorNoiseStep) {
			this.colorNoiseStep = colorNoiseStep;
		}
		public double getRotateNoiseRange() {
			return rotateNoiseRange;
		}
		public void setRotateNoiseRange(double rotateNoiseRange) {
			this.rotateNoiseRange = rotateNoiseRange;
		}
		public double getRotateNoiseStep() {
			return rotateNoiseStep;
		}
		public void setRotateNoiseStep(double rotateNoiseStep) {
			this.rotateNoiseStep = rotateNoiseStep;
		}
		public boolean isMirrorVertical() {
			return mirrorVertical;
		}
		public void setMirrorVertical(boolean mirrorVertical) {
			this.mirrorVertical = mirrorVertical;
		}
		public boolean isMirrorHorizontal() {
			return mirrorHorizontal;
		}
		public void setMirrorHorizontal(boolean mirrorHorizontal) {
			this.mirrorHorizontal = mirrorHorizontal;
		}
		public int getRotate() {
			return rotate;
		}
		public void setRotate(int rotate) {
			this.rotate = rotate;
			if(this.rotate < 1) { this.rotate = 1; }
		}
		public int getSpiral() {
			return spiral;
		}
		public void setSpiral(int spiral) {
			this.spiral = spiral;
			if(this.spiral < 1) { this.spiral = 1; }
		}
		
		
	}
	
	/**
	 * User operation bean desired for SilkPen to scrawl
	 * @author buwoyouwo
	 *
	 */
	public static class Operation{
		boolean down;						//If is push down operation
		boolean up;							//If is release up operation
		
		boolean pushed;						//if the pointer is pushed. For mouse
		Integer2 position = new Integer2();	//position of the pointer
		
		
		public boolean isDown() { return down; }
		public void setDown(boolean down) { this.down = down; }
		public boolean isUp() { return up; }
		public void setUp(boolean up) { this.up = up; }
		public boolean isPushed() { return pushed; }
		public void setPushed(boolean pushed) { this.pushed = pushed; }
		public Integer2 getPosition() { return position; }
		public void setPosition(Integer2 position) { this.position.copy(position); }
		
	}
	
	class Node{
		Integer2 position = new Integer2();
		Integer2 velocity = new Integer2();
		int segLength;
		
		public Integer2 getPosition() { return position; }
		public void setPosition(Integer2 position) { 
			this.position.copy(position);
		}
		public Integer2 getVelocity() { return velocity; }
		public void setVelocity(Integer2 velocity) { 
			this.velocity.copy(velocity);
		}
		
		
	}

}
