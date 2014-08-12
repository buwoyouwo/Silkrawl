package com.buwoyouwo.silkrawl.pen;

import java.util.ArrayList;
import java.util.List;

import com.buwoyouwo.util.vector.Integer2;


public class SilkPenScrawl implements IScrawl {
	List<Path> paths = new ArrayList<Path>();
	
	//Symmetric
	boolean mirrorVertical = false;		//Vertical mirror symmetric
	boolean mirrorHorizontal = false;	//Horizontal mirror symmetric
	int rotate = 1;						//Rotation symmetric. 1 for invalid
	int spiral = 1;						//Spiral symmetric. 1 for invalid

	@Override
	public void draw(IScrawlDrawer drawer) {
		// TODO Auto-generated method stub
		drawer.drawSilkPenScrawl(this);
	}
	
	
	public void addPath(Path path){
		paths.add(path);
	}

	public List<Path> getPaths() { return paths; }
	public void setPaths(List<Path> paths) { this.paths = paths; }

	public boolean isMirrorVertical() { return mirrorVertical; }
	public void setMirrorVertical(boolean mirrorVertical) { this.mirrorVertical = mirrorVertical; }

	public boolean isMirrorHorizontal() { return mirrorHorizontal; }
	public void setMirrorHorizontal(boolean mirrorHorizontal) { this.mirrorHorizontal = mirrorHorizontal; }

	public int getRotate() { return rotate; }
	public void setRotate(int rotate) { this.rotate = rotate; }

	public int getSpiral() { return spiral; }
	public void setSpiral(int spiral) { this.spiral = spiral; }
	
	public static class Path{
		//Nodes
		Integer2[] nodeList;
		
		//Color
		int colorR = 255;
		int colorG = 255;
		int colorB = 255;
		int alpha = 255;
		
		public Integer2[] getNodeList() { return nodeList;	}
		public void setNodeList(Integer2[] nodeList) { this.nodeList = nodeList;	}

		public int getColorR() { return colorR; }
		public void setColorR(int colorR) { this.colorR = colorR; }

		public int getColorG() { return colorG; }
		public void setColorG(int colorG) { this.colorG = colorG; }

		public int getColorB() { return colorB; }
		public void setColorB(int colorB) { this.colorB = colorB; }

		public int getAlpha() { return alpha; }
		public void setAlpha(int alpha) { this.alpha = alpha; }
	}
	
}
