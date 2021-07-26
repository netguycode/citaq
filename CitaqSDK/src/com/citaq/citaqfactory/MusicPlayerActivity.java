package com.citaq.citaqfactory;


import java.io.IOException;

import com.citaq.util.LEDControl;
import com.citaq.util.SoundPoolManager;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MusicPlayerActivity extends Activity {
	
	private MediaPlayer mMediaPlayer = null;
	Context mContext;
	Button bt_volume_decrease;
	Button bt_volume_increase;
	CheckBox bt_play_pause;
	
	TextView tv_current_vol;
	
	ToggleButton bt_left;
	ToggleButton bt_right;
	
	int  soundID_left =-1;
	int  soundID_right =-1;
	
	int laser;
	
	SoundPoolManager mSoundPoolManager;
	
	//音量控制,初始化定义    
	AudioManager mAudioManager;   
	int maxVolume;
	
	int defaultVolume, currentVolume;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.music_control);
		mContext = this;
		
		mMediaPlayer = MediaPlayer.create(this, R.raw.delivery);
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mMediaPlayer.setLooping(true);
		mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){  
	            @Override  
	            public void onCompletion(MediaPlayer arg0) {  
	                Toast.makeText(MusicPlayerActivity.this, R.string.end, Toast.LENGTH_SHORT).show();  
	                mMediaPlayer.release();  
	            }  
	        });
		initView();
	}
	
	
	private void initView(){
		bt_volume_decrease = (Button) findViewById(R.id.volume_decrease);
		bt_volume_increase = (Button) findViewById(R.id.volume_increase);
		bt_play_pause = (CheckBox) findViewById(R.id.play_pause);
		
		bt_left = (ToggleButton) findViewById(R.id.left_vol);
		bt_right = (ToggleButton) findViewById(R.id.right_vol);
		
		tv_current_vol = (TextView) findViewById(R.id.tv_current_vol);
		
		bt_volume_decrease.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				currentVolume--;
				setVolume(currentVolume);
			}
		});
		
		bt_volume_increase.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				currentVolume++;
				setVolume(currentVolume);
				
			}
		});
		
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);   
		
		//最大音量    
		maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); 
		
		
		
		bt_play_pause.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// 点击 播放png  isChecked true;
				// 暂停png  isChecked true;
				 if(isChecked){	 
					 //选择暂停时
					 if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
						 mMediaPlayer.start();
					}else{
						try {
							mMediaPlayer.prepare();
						} catch (IllegalStateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            	mMediaPlayer.start();
					}
						
		         }else{
		        	 if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
						 mMediaPlayer.pause();
					}else{  
		            	
		            }  
		         }
				
			}
		});
		
		mSoundPoolManager = new SoundPoolManager(getApplicationContext());
		laser = mSoundPoolManager.load(R.raw.laser);
		
		bt_left.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					if(soundID_left == -1){
						soundID_left = mSoundPoolManager.playLeft(laser);
					}else{
						mSoundPoolManager.resume(soundID_left);
					}
				}else{
					if(soundID_left!=-1){
						mSoundPoolManager.pause(soundID_left);
					}
				}
			}
		});
		
		
		
		bt_right.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					if(soundID_right == -1){
						soundID_right = mSoundPoolManager.playRight(laser);
					}else{
						mSoundPoolManager.resume(soundID_right);
					}
				}else{
					if(soundID_right!=-1){
						mSoundPoolManager.pause(soundID_right);
					}
				}
			
			}
		});
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		defaultVolume = currentVolume;
		
		tv_current_vol.setText(String.valueOf(currentVolume));
	}
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, defaultVolume, 0); 
	}
	
	private void setVolume(int volume){
	    if(volume >=0 && volume<=maxVolume){
	    	mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0); 
	    	
	    	tv_current_vol.setText(String.valueOf(volume));
	    }else{
	    	
	    	if(volume <= 0){
	    		Toast.makeText(MusicPlayerActivity.this, mContext.getResources().getString(R.string.minimum_volume),Toast.LENGTH_SHORT).show();  
	    		currentVolume++;
	    	}else{
	    		Toast.makeText(MusicPlayerActivity.this, mContext.getResources().getString(R.string.max_volume),Toast.LENGTH_SHORT).show();  
	    		currentVolume--;
	    	}
	    }
		
	}
	
	@Override
	protected void onDestroy() {
		if(mMediaPlayer != null ){
			mMediaPlayer.release();
		}
		mSoundPoolManager.unloadAll();
		super.onDestroy();
	}

}
