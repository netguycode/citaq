package com.citaq.view;

import com.citaq.citaqfactory.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter{  
     private LayoutInflater inflater;
     TypedArray iconsColor;
     
    //菜单选项 
    public Integer[] menusStrings = {  
            R.string.title_led
            ,R.string.title_print
            ,R.string.title_touch
            ,R.string.title_display
            ,R.string.title_music
            ,R.string.title_pd
            ,R.string.title_msr
            ,R.string.title_microphone
            ,R.string.title_network
            ,R.string.title_fskCaller
            ,R.string.title_ageing
            ,R.string.title_info 
    }; 
        private Context mContext;  
  
        public ImageAdapter(Context context) {  
            this.mContext=context;  
            inflater = LayoutInflater.from(mContext);
            iconsColor = mContext.getResources().obtainTypedArray(R.array.plain_arr);
        }  
  
        @Override  
        public int getCount() {  
            return menusStrings.length;  
        }  
  
        @Override  
        public Object getItem(int position) {  
            return menusStrings[position];  
        }  
  
        @Override  
        public long getItemId(int position) {  
            // TODO Auto-generated method stub  
            return 0;  
        }  
  
        @Override  
        public View getView(int position, View convertView, ViewGroup parent) {  
           
        	ViewHolder viewHolder = null;
   
            if(convertView==null){  
            	
            	 viewHolder = new ViewHolder();
                 convertView = inflater.inflate(R.layout.main_grid_item, null);
                 viewHolder.img = (ImageView) convertView.findViewById(R.id.img_bg);
                 viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
                 convertView.setTag(viewHolder);

            }else{  
            	 viewHolder = (ViewHolder) convertView.getTag();
            }  
            int itemId = (int) (Math.random() * (iconsColor.length()-1));
            viewHolder.img.setBackgroundDrawable(iconsColor.getDrawable(itemId));
            viewHolder.title.setText(menusStrings[position]);
            
            
            return convertView;  
        }

		
        class ViewHolder {
            ImageView img;
            TextView title;
        }
 
    }
    