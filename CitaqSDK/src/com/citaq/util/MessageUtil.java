package com.citaq.util;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

public class MessageUtil {
	public static void alert(Context context,String title,String message)
	{
		new AlertDialog.Builder(context).setTitle(title).setMessage(message).create().show();
	}
	
    public static void toast(Context context,String paramString)
    {
	    Toast.makeText(context, paramString, Toast.LENGTH_LONG).show();
    }
}
