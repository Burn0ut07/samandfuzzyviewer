package com.comic.viewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ComicViewer extends Activity implements OnClickListener {
	private final String startURL = "http://www.smbc-comics.com/index.php?db=comics&id=";
	private String comicNumber = "";
	private final String endURL = "#comic";
	private EditText comicID;
	private Button submit;
	private ImageView results;
	private ProgressDialog loadingDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		comicID = (EditText) findViewById(R.id.comicid);
		submit = (Button) findViewById(R.id.submit);
		results = (ImageView) findViewById(R.id.result);

		submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == submit) {
			showLoading();
			comicNumber = comicID.getText().toString();
			if (comicNumber.trim().equals("") || comicNumber.trim().equals(" ")) {
				return;
			}
			getHTTPSource(startURL + comicNumber + endURL);
		}
	}

	private void getHTTPSource(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(request);

			String imageURL = "";
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			StringBuilder str = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.contains("[IMG]")){
					str.append(line.substring(line.indexOf("[IMG]")));
					str.append(reader.readLine());
					break;
				}
			}
			in.close();
			imageURL = str.toString();
			imageURL = str.substring(5, str.indexOf("[/IMG]"));
			displayImage(imageURL);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void displayImage(String imageURL){
		try{
			InputStream is = (InputStream) new URL(imageURL).getContent();
			Drawable image = Drawable.createFromStream(is, "comic image from SMBC");
			results.setImageDrawable(image);
			doneLoading();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void showLoading() {
		loadingDialog = ProgressDialog
				.show(this, "", "Loading. Please wait...");
	}

	public void doneLoading() {
		loadingDialog.dismiss();
	}
}