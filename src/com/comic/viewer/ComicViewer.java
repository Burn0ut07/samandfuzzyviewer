/**
 * @author Yixin Zhu
 */
package com.comic.viewer;

import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.comic.globals.Globals;

public class ComicViewer extends Activity implements OnClickListener {
	private ProgressDialog loadingDialog;
	private ViewFlipper viewFlipper;
	private Button first, back, mainMenu, next, last;
	private int firstVolPage, lastVolPage, currentPage;
	private ImageView currentImage;
	private WebView myWebView;
	private static final FrameLayout.LayoutParams ZOOM_PARAMS =
		new FrameLayout.LayoutParams(
		ViewGroup.LayoutParams.FILL_PARENT,
		ViewGroup.LayoutParams.WRAP_CONTENT,
		Gravity.BOTTOM);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set custom title bar
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.comicviewer);
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
		myWebView = (WebView) this.findViewById(R.id.webView);
		first = (Button) findViewById(R.id.start);
		first.setOnClickListener(this);
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		mainMenu = (Button) findViewById(R.id.mainmenu);
		mainMenu.setOnClickListener(this);
		next = (Button) findViewById(R.id.next);
		next.setOnClickListener(this);
		last = (Button) findViewById(R.id.current);
		last.setOnClickListener(this);
		/*FrameLayout mContentView = (FrameLayout) getWindow().
	    getDecorView().findViewById(android.R.id.content);
		final View zoom = this.myWebView.getZoomControls();
	    mContentView.addView(zoom, ZOOM_PARAMS);
	    myWebView.getSettings().setBuiltInZoomControls(true);*/
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
	
	/**
	 * gets the image at the given string url
	 * @param imageURL the url to fetch image
	 * @return the image in the form of drawable
	 */
	private Drawable getImage(String imageURL){
		try{
			InputStream is = (InputStream) new URL(imageURL).getContent();
			return Drawable.createFromStream(is, "Sam and Fuzzy");
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * sets up the initial view, called when launched from main menu
	 */
	private void setupInitialView() {
		first.setEnabled(false);
		first.setFocusable(false);
		back.setEnabled(false);
		back.setFocusable(false);
		currentPage = firstVolPage;
		currentImage = new ImageView(this);
		currentImage.setScaleType(ImageView.ScaleType.CENTER);
		currentImage.setImageDrawable(getImage(Globals.StartImageURL + 
				zfill(currentPage, Globals.numZeros) + Globals.EndImageURL));
		viewFlipper.addView(currentImage);
		viewFlipper.showNext();
	}
	
	private void displayNewView(){
		adjustControls();
		currentImage.setScaleType(ImageView.ScaleType.CENTER);
		currentImage.setImageDrawable(getImage(Globals.StartImageURL + 
				zfill(currentPage, Globals.numZeros) + Globals.EndImageURL));
		viewFlipper.removeAllViews();
		viewFlipper.addView(currentImage);
		viewFlipper.showNext();
	}

	@Override
	public void onClick(View v) {
		if (v == next){
			setupNextView();
		} else if (v == back){
			setupPrevView();
		} else if (v == first){
			setupFirstView();
		} else if (v == last){
			setupLastView();
		} else if (v == mainMenu){
			finish();
		}
	}

	private void setupNextView(){
		++currentPage;
		displayNewView();
	}
	
	private void setupPrevView(){
		--currentPage;
		displayNewView();
	}
	
	private void setupFirstView() {
		currentPage = firstVolPage;
		displayNewView();
	}
	
	private void setupLastView() {
		currentPage = lastVolPage;
		displayNewView();
	}
	
	/**
	 * adjusts the clickable controls based on displayed page
	 */
	private void adjustControls(){
		if (currentPage > firstVolPage && currentPage < lastVolPage){
			first.setEnabled(true);
			first.setFocusable(true);
			back.setEnabled(true);
			back.setFocusable(true);
			next.setEnabled(true);
			next.setFocusable(true);
			last.setEnabled(true);
			last.setFocusable(true);
		} else if (currentPage == firstVolPage){
			first.setEnabled(false);
			first.setFocusable(false);
			back.setEnabled(false);
			back.setFocusable(false);
			next.setEnabled(true);
			next.setFocusable(true);
			last.setEnabled(true);
			last.setFocusable(true);
		} else if (currentPage == lastVolPage){
			first.setEnabled(true);
			first.setFocusable(true);
			back.setEnabled(true);
			back.setFocusable(true);
			next.setEnabled(false);
			next.setFocusable(false);
			last.setEnabled(false);
			last.setFocusable(false);
		} else {
			first.setEnabled(false);
			first.setFocusable(false);
			back.setEnabled(false);
			back.setFocusable(false);
			next.setEnabled(false);
			next.setFocusable(false);
			last.setEnabled(false);
			last.setFocusable(false);
		}
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