package com.comic.viewer;

import java.io.InputStream;
import java.net.URL;

import com.comic.globals.Globals;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class ComicViewer extends Activity implements OnClickListener {
	private ProgressDialog loadingDialog;
	private ViewFlipper viewFlipper;
	private Button start, back, mainMenu, next, last;
	private int firstVolPage, lastVolPage, currentPage;
	private ImageView currentImage;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set custom title bar
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.comicviewertitlebar);
		setup(); //sets up objects in view
		//get volume range
		Bundle bundle = getIntent().getExtras();
		//sets the range of this volume
		setThisVolumeRange((String) bundle.getString("volumeRange"));
		setupInitialView();
	}

	public void setup(){
		//get instances of xml objects and set listeners
		viewFlipper = (ViewFlipper) findViewById(R.id.ViewFlipper);
		start = (Button) findViewById(R.id.start);
		start.setOnClickListener(this);
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		mainMenu = (Button) findViewById(R.id.mainmenu);
		mainMenu.setOnClickListener(this);
		next = (Button) findViewById(R.id.next);
		next.setOnClickListener(this);
		last = (Button) findViewById(R.id.current);
		last.setOnClickListener(this);
	}
	/**
	 * calculates the range for this specific volume
	 * @param range: the specified range in String format
	 */
	public void setThisVolumeRange(String range){
		firstVolPage = Integer.valueOf(range.substring(0, range.indexOf("-"))).intValue();
		lastVolPage = Integer.valueOf(range.substring(range.indexOf("-") + 1)).intValue();
	}
	/**
	 * Left pads a number with zeros
	 * 
	 * @param num The number to pad with zeros
	 * @param zeros The amount of zeros to pad the number with
	 * @return A String with the number properly padded
	 */
	public static String zfill(int num, int zeros) {
		String n = String.valueOf(num), z_filled = n;
		for(int i = 0; i < (zeros - n.length()); i++)
		    z_filled = "0" + z_filled;
		return z_filled;
	}

	private Drawable getImage(String imageURL){
		try{
			InputStream is = (InputStream) new URL(imageURL).getContent();
			return Drawable.createFromStream(is, "Sam and Fuzzy");
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private void setupInitialView() {
		currentPage = firstVolPage;
		currentImage = new ImageView(this);
		currentImage.setBackgroundDrawable(getImage(Globals.StartImageURL + 
				zfill(currentPage, Globals.numZeros) + Globals.EndImageURL));
		viewFlipper.addView(currentImage);
		viewFlipper.showNext();
	}
	
	@Override
	public void onClick(View v) {

	}

	/*private void getHTTPSource(String url) {
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
	}*/

	public void showLoading() {
		loadingDialog = ProgressDialog
				.show(this, "", "Loading. Please wait...");
	}

	public void doneLoading() {
		loadingDialog.dismiss();
	}
}