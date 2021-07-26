package com.citaq.citaqfactory;


import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.MediaController;



public class VideoAcivity extends Activity {

	Context context;
    FullScreenVideo  videoView;
    String mVideoPath;
    
    boolean hasExternalVideo =false;
	
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_video); 
        
        context = this;
        Intent intent = getIntent();
        mVideoPath = intent.getStringExtra("path");
        
        hasExternalVideo= intent.getBooleanExtra("hasExternalVideo",false);
        
        
        videoView = (FullScreenVideo) findViewById(R.id.supervideo);
        
        MediaController  controller = new MediaController(this);//实例化控制器
//        mVideoPath = "http://oleeed73x.bkt.clouddn.com/me.mp4";
        
        if(hasExternalVideo){
        	 videoView.setVideoPath(mVideoPath); //设置媒体路径，网络媒体和本地媒体路径都使用此方法设置  
        }else{
        	 videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.testvideo)); 
        }

        videoView.start();
        
        
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
              mp.start();
              mp.setLooping(true);
            }
          });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                	videoView.setVideoPath(mVideoPath);
                	videoView.start();
                }
              });
        videoView.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				showNormalDialog();
				return true;
			}
		});
        
    }  
	
	  private void showNormalDialog(){    
	        final AlertDialog.Builder normalDialog = 
	            new AlertDialog.Builder(context);
	        normalDialog.setMessage(R.string.video_error);
	        normalDialog.setPositiveButton(R.string.str_OK, 
	            new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	VideoAcivity.this.finish();
	            }
	        });
	        
	        // show
	        normalDialog.show();
	    }
  
    @Override  
    protected void onPause() {  
        super.onPause();  
        videoView.pause();  
    }  
  
    @Override  
    protected void onResume() {  
        super.onResume();  
        videoView.resume();  
    } 
    

}
