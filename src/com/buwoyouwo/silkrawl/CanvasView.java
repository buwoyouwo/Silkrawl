package com.buwoyouwo.silkrawl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;

import com.buwoyouwo.silkrawl.pen.IPen;
import com.buwoyouwo.silkrawl.pen.IScrawl;
import com.buwoyouwo.silkrawl.pen.SilkPen;
import com.buwoyouwo.util.graphics.IFPSController;
import com.buwoyouwo.util.vector.Integer2;

public class CanvasView extends View implements Serializable{
	Paint paint;
	AndroidDrawer drawer;
	Canvas canvas;
	IPen pen;
	IScrawl lastScrawl;
	List<IScrawl> scrawls = new ArrayList<IScrawl>();
	
	Bitmap bufferBitmap;
	Canvas bufferCanvas;
	
	IFPSController fpsController;

//	float x;
//	float y;
//	float l = 50f;
//	float delta = 0.5f;
//	float deltaX = delta;
//	float deltaY = delta;
	

	public CanvasView(Context context) {
		super(context);
		paint = new Paint();
		paint.setARGB(255, 200, 0, 120);
		paint.setStrokeCap(Cap.ROUND);
		paint.setStyle(Style.FILL);
		
		drawer = new AndroidDrawer();
		
		pen = new SilkPen();
		pen.config(new SilkPen.Config());
	}
	
	public CanvasView(Context context, IFPSController fpsCtrl){
		this(context);
		fpsController = fpsCtrl;
	}

	@Override
	public void onDraw(Canvas canvas) {
		this.canvas = canvas;
//		if(bufferBitmap != null){
			canvas.drawBitmap(bufferBitmap, 0, 0, paint);
//		}
		if(lastScrawl != null){
			lastScrawl.draw(drawer);
		}
		if(fpsController!= null){
			fpsController.onFrameEnd();
		}
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		invalidate();
		SilkPen.Operation op = new SilkPen.Operation();
		Integer2 position = new Integer2();
		
		switch(event.getAction() & MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:
			op.setDown(true);
			op.setUp(false);
			op.setPushed(true);
			position.setX((int) event.getX());
			position.setY((int) event.getY());
			op.setPosition(position);
			break;
		case MotionEvent.ACTION_MOVE:
			op.setDown(false);
			op.setUp(false);
			op.setPushed(true);
			position.setX((int) event.getX());
			position.setY((int) event.getY());
			op.setPosition(position);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_OUTSIDE:
			op.setDown(false);
			op.setUp(true);
			op.setPushed(false);
			position.setX((int) event.getX());
			position.setY((int) event.getY());
			op.setPosition(position);
			break;
		default:
			return false;
		}
		
		drawer.setCanvas(canvas);
		IScrawl scrawl = pen.scrawl(op);
		scrawlOccur(scrawl);
		
		return true;
	}
	
	
	
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		if(bufferBitmap == null){
			bufferBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		} else{
			//bufferBitmap = Bitmap.createBitmap(bufferBitmap);
		}
		bufferCanvas = new Canvas(bufferBitmap);
		
		updateBufferCanvas();
		
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	private void scrawlOccur(IScrawl scrawl){
//		if(scrawl != null){
//			lastScrawl = scrawl;
//		} else if(lastScrawl != null){
//			scrawls.add(lastScrawl);
//			lastScrawl = null;
//			updateBufferCanvas();
//		}
		
		if(scrawl != lastScrawl && scrawl != null){
			lastScrawl = scrawl;
			scrawls.add(scrawl);
			updateBufferCanvas();
		}
	}
	
	private void updateBufferCanvas(){
		bufferCanvas.drawColor(Color.WHITE);
		if(bufferCanvas != null){
			drawer.setCanvas(bufferCanvas);
			for(IScrawl scrawl : scrawls){
				scrawl.draw(drawer);
			}
		}
		if(lastScrawl != null){
			lastScrawl.draw(drawer);
		}
	}

	public IPen getPen() {
		return pen;
	}

	public void setPen(IPen pen) {
		this.pen = pen;
	}

	public List<IScrawl> getScrawls() {
		return scrawls;
	}

	public void setScrawls(List<IScrawl> scrawls) {
		this.scrawls = scrawls;
	}

	public IScrawl getLastScrawl() {
		return lastScrawl;
	}

	public void setLastScrawl(IScrawl lastScrawl) {
		this.lastScrawl = lastScrawl;
	}



}
