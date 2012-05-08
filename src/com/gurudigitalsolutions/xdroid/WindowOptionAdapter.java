package com.gurudigitalsolutions.xdroid;

import java.text.AttributedCharacterIterator.Attribute;

import android.R.string;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WindowOptionAdapter extends ArrayAdapter<String>
{
	private final Context context;
	private final String[] values;


	public WindowOptionAdapter(Context context, String[] values)
	{
		//super();
		super(context, R.layout.windowoption, values);
		//super(context, R.layout.windowoption, values);
		//super(context, R.layout.windowoption);
		
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		//Log.w("dbg", "WindowOptionAdapter getView");
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(R.layout.windowoption, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.tvMenuOption);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.ivMenuOption);
		
		textView.setText(values[position]);
		textView.setTextSize(24);

		
		
		//LayoutParams params = new LayoutParams(context, );
		//params.width = 32;
		//params.height = 32;
		
		
		String s = values[position];
		if (s.startsWith("Move")) {
			imageView.setImageResource(R.drawable.inputmouse);
			//imageView.setLayoutParams(params);
		}
		else if(s.startsWith("Close")) {
			imageView.setImageResource(R.drawable.windowclose);
			//imageView.setLayoutParams(params);
		}
		else if(s.startsWith("Focus"))
		{
			imageView.setImageResource(R.drawable.emblemimportant);
			//imageView.setLayoutParams(params);
		}
		else
		{
			imageView.setImageResource(R.drawable.viewfullscreen);
			//imageView.setLayoutParams(params);
		}

		return rowView;
	}
}
