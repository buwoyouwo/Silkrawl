package com.buwoyouwo.silkrawl;

import java.io.Serializable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;

import com.buwoyouwo.silkrawl.pen.IScrawlDrawer;
import com.buwoyouwo.silkrawl.pen.SilkPenScrawl;
import com.buwoyouwo.util.graphics2d.shapes.Curve2D;
import com.buwoyouwo.util.vector.Integer2;

public class AndroidDrawer implements IScrawlDrawer,Serializable{
	Canvas canvas;
	Paint paint;
	
	public AndroidDrawer(){
		paint = new Paint();
	}

	@Override
	public boolean drawSilkPenScrawl(SilkPenScrawl scrawl) {
		if(canvas == null || scrawl == null) return false;
		
		paint.setStrokeWidth(1);
		paint.setStrokeCap(Cap.ROUND);
		paint.setStyle(Style.STROKE);
		paint.setStrokeJoin(Join.BEVEL);
		
		
//		for(SilkPenScrawl.Path path : scrawl.getPaths()){
//			//绘制每一条路径
//			paint.setARGB(path.getAlpha(), path.getColorR(), path.getColorG(), path.getColorB());
//			for(int i = 1; i < path.getNodeList().length; i++){
//				canvas.drawLine(path.getNodeList()[i-1].getX(), 
//								path.getNodeList()[i-1].getY(), 
//								path.getNodeList()[i].getX(), 
//								path.getNodeList()[i].getY(), 
//								paint);
//			}
//		}
		
//		drawSilkPenScrawlSingle(scrawl);
//		drawSilkPenScrawlMirrorVertical(scrawl);
//		drawSilkPenScrawlMirrorHorizontal(scrawl);
//		drawSilkPenScrawlRotate(scrawl);
		drawSilkPenScrawlSpiral(scrawl);
		
		return true;
		
//		canvas.drawLines(lines, paint);
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}
	
	
	private void drawSilkPenScrawlSingle(SilkPenScrawl scrawl){
		for(SilkPenScrawl.Path path : scrawl.getPaths()){
			//绘制每一条路径
			paint.setARGB(path.getAlpha(), path.getColorR(), path.getColorG(), path.getColorB());
			
//			for(int i = 1; i < path.getNodeList().length; i++){
//				canvas.drawLine(path.getNodeList()[i-1].getX(), 
//								path.getNodeList()[i-1].getY(), 
//								path.getNodeList()[i].getX(), 
//								path.getNodeList()[i].getY(), 
//								paint);
//			}
			
			Integer2[] controlPoints = path.getNodeList();
			Integer2[] points = Curve2D.bezier(controlPoints, 0.05f);
			for(int i = 1; i < points.length; i++){
				canvas.drawLine(points[i-1].getX(), 
								points[i-1].getY(), 
								points[i].getX(), 
								points[i].getY(), 
								paint);
			}
		}
	}
	
	private void drawSilkPenScrawlMirrorVertical(SilkPenScrawl scrawl){
		if(scrawl.isMirrorVertical()){
			canvas.save();
			canvas.scale(1, -1);
			canvas.translate(0, -canvas.getHeight());
			drawSilkPenScrawlSingle(scrawl);
			canvas.restore();
		}
		drawSilkPenScrawlSingle(scrawl);
	}
	
	private void drawSilkPenScrawlMirrorHorizontal(SilkPenScrawl scrawl){
		if(scrawl.isMirrorHorizontal()){
			canvas.save();
			canvas.scale(-1, 1);
			canvas.translate(-canvas.getWidth(), 0);
			drawSilkPenScrawlMirrorVertical(scrawl);
			canvas.restore();
		}
		drawSilkPenScrawlMirrorVertical(scrawl);
	}
	
	private void drawSilkPenScrawlRotate(SilkPenScrawl scrawl){
		int rotate = scrawl.getRotate();
		double theta = 360 / rotate;
		canvas.save();
		for(int i = 0; i < rotate; i++){
			canvas.translate(canvas.getWidth()/2, canvas.getHeight()/2);
			canvas.rotate((float) theta );
			canvas.translate(-canvas.getWidth()/2, -canvas.getHeight()/2);
			
			drawSilkPenScrawlMirrorHorizontal(scrawl);
			//canvas.rotate((float) theta);
			
		}
		canvas.restore();
		
	}
	
	private void drawSilkPenScrawlSpiral(SilkPenScrawl scrawl){
		int rotate = scrawl.getSpiral();
		double theta = 360 / rotate;
		double scale = 1 - 0.01 * (rotate - 10) * (rotate - 10);
		
		canvas.save();
		for(int i = 0; i < rotate; i++){
			drawSilkPenScrawlRotate(scrawl);
			canvas.translate(canvas.getWidth()/2, canvas.getHeight()/2);
			canvas.scale((float)scale, (float)scale);
			canvas.rotate((float) theta);
			canvas.translate(-canvas.getWidth()/2, -canvas.getHeight()/2);
		}
		canvas.restore();
	}
}
