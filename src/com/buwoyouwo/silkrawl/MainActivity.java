package com.buwoyouwo.silkrawl;

import java.io.Serializable;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buwoyouwo.util.graphics.FPSController;
import com.buwoyouwo.util.graphics.IFPSController;
import com.buwoyouwo.util.graphics.IFPSListener;
import com.buwoyouwo.util.sys.IStopwatch;
import com.buwoyouwo.util.sys.Stopwatch;
import com.buwoyouwo.util.thread.StoppableThread;


public class MainActivity extends Activity {
	//整体界面
	private LinearLayout container;	//界面容器
	private Handler handler;		//界面刷新Handler
	
	//画布界面
	CanvasView canvasView;				//画布View
	StoppableThread freshThread;	//画布定时刷新线程
	
	//fps界面
	TextView fpsDisp;				//显示fps
	
	//组件
	IStopwatch watch;				//计时
	IFPSController fpsController;	//FPS控制器
	IFPSListener fpsListener;		//fps监听器
	
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        handler = new Handler();
       
        watch = new Stopwatch();
        initFPSController();
        initFPSListenr();
        
        resetView();
        
//        container = new LinearLayout(this);
//        canvasView = new CanvasView(this, fpsController);
//        fpsDisp = new TextView(this);
//        fpsDisp.setText("FPS");
//        
//        container.setOrientation(LinearLayout.VERTICAL);
//        container.addView(fpsDisp, LayoutParams.WRAP_CONTENT);
//        container.addView(canvasView, LayoutParams.MATCH_PARENT);
//        setContentView(container);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	watch.start();
    	fpsController.enable();
    	initFreshThread();
    	//resetView();
    }
    
    @Override
    protected void onPause() {
    	watch.pause();
    	fpsController.disable();
    	destroyFreshThread();
    	
    	super.onPause();
    }
    
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public IStopwatch getWatch(){
    	return watch;
    }
    
    public void updateCanvas(){
    	handler.post(updateCanvasView);
    }
    
    public void updateFPS(){
    	handler.post(updateFPSView);
    }
    
    private void resetView(){
    	container = new LinearLayout(this);
        canvasView = new CanvasView(this, fpsController);
        fpsDisp = new TextView(this);
        fpsDisp.setText("FPS");
        
        container.setOrientation(LinearLayout.VERTICAL);
        container.addView(fpsDisp, LayoutParams.WRAP_CONTENT);
        container.addView(canvasView, LayoutParams.MATCH_PARENT);
        setContentView(container);
    }
    
    Runnable updateCanvasView = new Runnable(){
    	public void run(){
    		canvasView.invalidate();
    	}
    };
    
    
    Runnable updateFPSView = new Runnable(){
    	public void run(){
    		fpsDisp.setText("FPS:"+ fpsController.getFPSNow());
    		fpsDisp.invalidate();
    	}
    };
    
    private void initFreshThread(){
    	freshThread  = new StoppableThread(){
    		public void doInLoop(){
    			updateCanvas();
//    			fpsController.onFrameEnd();
    			
//    			fpsDisp.setText("FPS:"+ "[" + updateCount++ + "]" + 
//						fpsController.getFPSNow());
//    			fpsDisp.postInvalidate();
    		}
    	};
    	freshThread.start();
    }
    
    private void destroyFreshThread(){
    	if(freshThread != null){
    		freshThread.terminal();
    		freshThread = null;
    	}
    }
    
    private void initFPSController(){
    	fpsController = new FPSController(watch);
    	fpsController.enable();
    	fpsController.setTargetFPS(60);
    	fpsController.setUpdateInverse(1000);
    }
    
    private void initFPSListenr(){
    	fpsListener = new IFPSListener(){
    		private int updateCount = 0;

			@Override
			public void afterSleep() {			}

			@Override
			public void beforeSleep() {			}

			@Override
			public void onDisable() {			}

			@Override
			public void onEnable() {			}

			@Override
			public void onFPSUpdate() {
				updateFPS();
			}
    		
    	};
    	
    	fpsController.addListener(fpsListener);
    }
    
}
