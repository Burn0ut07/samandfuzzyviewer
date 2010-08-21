package com.comic.viewer;

import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.comic.globals.Globals;


public class MainPage extends Activity {
	private ImageButton intro;
	private Button volFive;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		intro = (ImageButton) findViewById(R.id.intro);
		intro.setImageDrawable(getImage(Globals.IntroImage));
		volFive = (Button) findViewById(R.id.volfive);
	}
	private Drawable getImage(String imageURL){
		try{
			InputStream is = (InputStream) new URL(imageURL).getContent();
			return Drawable.createFromStream(is, "comic image from SMBC");
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
