package com.citaq.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundPoolManager 
{
	private Context pContext;
	private SoundPool sndPool;

	
	// Constructor, setup the audio manager and store the app context
	public SoundPoolManager(Context appContext)
	{
	  sndPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 100);
 	  pContext = appContext;
	}
	
	// Load up a sound and return the id
	public int load(int sound_id)
	{
		return sndPool.load(pContext, sound_id, 1);
	}
	
	/*sp.play(spMap.get(sound),     //声音资源
	        volumnRatio,         //左声道
	        volumnRatio,         //右声道
	        1,             //优先级，0最低
	        number,         //循环次数，0是不循环，-1是永远循环
	        1);           //回放速度，0.5-2.0之间。1为正常速度
	 */
	// Play a sound
	public int playLeft(int sound_id)
	{
		return sndPool.play(sound_id, 1.0f, 0, 1, -1, 1); 	
	}	
	
	public int playRight(int sound_id)
	{
		return sndPool.play(sound_id, 0, 1.0f, 1, -1, 1); 
		
		
	}
	
	
	// Free ALL the things!
	public void unloadAll()
	{
		sndPool.release();		
	}
	
	public void pause(int id)
	{
		sndPool.pause(id);
	}
	
	public void resume(int id)
	{
		sndPool.resume(id);
	}

}
