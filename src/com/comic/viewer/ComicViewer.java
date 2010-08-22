/**
 * @author Yixin Zhu, Joel Jauregui
 */
package com.comic.viewer;

import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.comic.globals.Globals;

public class ComicViewer extends Activity implements OnClickListener {
	private ProgressDialog loadingDialog;
	private Button first, back, mainMenu, next, last;
	private int firstVolPage, lastVolPage, currentPage;
	private WebView myWebView;
	private View zoom;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// sets up custom title bar
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.comicviewer);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.comicviewertitlebar);
		// sets up objects in view
		setup();
		//new instance
		if (savedInstanceState == null){
			showLoading();
			// get volume range
			Bundle bundle = getIntent().getExtras();
			// sets up the range of this volume
			setThisVolumeRange((String) bundle.getString("volumeRange"));
			//sets up first view to display
			setupInitialView();
		} else { //destroyed and recreated
			firstVolPage = savedInstanceState.getInt("firstVolPage");
			lastVolPage = savedInstanceState.getInt("lastVolPage");
			currentPage = savedInstanceState.getInt("currentPage");
			displayNewView();
		}
	}

	/**
	 * initial startup consisting of setting up of xml instances and zoom controls
	 */
	public void setup() {
		//gets instances from xml
		myWebView = (WebView) findViewById(R.id.webView);
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
		//sets up image display and zoom
		myWebView.setClickable(true);
		final Activity activity = this;
		myWebView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(activity, "Oh no! " + description,
						Toast.LENGTH_SHORT).show();
			}
		});
		myWebView.requestFocus();
		zoom = myWebView.getZoomControls();
		zoom.setVisibility(View.VISIBLE);
		myWebView.getSettings().setBuiltInZoomControls(true);
	}

	/**
	 * Calculates the range for this specific volume
	 * 
	 * @param range
	 *            : the specified range in String format
	 */
	public void setThisVolumeRange(String range) {
		firstVolPage = Integer.valueOf(range.substring(0, range.indexOf("-")))
				.intValue();
		lastVolPage = Integer.valueOf(range.substring(range.indexOf("-") + 1))
				.intValue();
	}

	/**
	 * Left pads a number with zeros
	 * 
	 * @param num
	 *            The number to pad with zeros
	 * @param zeros
	 *            The amount of zeros to pad the number with
	 * @return A String with the number properly padded
	 */
	public static String zfill(int num, int zeros) {
		String n = String.valueOf(num), z_filled = n;
		for (int i = 0; i < (zeros - n.length()); i++)
			z_filled = "0" + z_filled;
		return z_filled;
	}

	/**
	 * gets the image at the given string url
	 * 
	 * @param imageURL
	 *            the url to fetch image
	 * @return the image in the form of drawable
	 */
	private Drawable getImage(String imageURL) {
		try {
			InputStream is = (InputStream) new URL(imageURL).getContent();
			return Drawable.createFromStream(is, "Sam and Fuzzy");
		} catch (Exception e) {
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
		myWebView.clearView();
		myWebView.loadUrl(Globals.StartImageURL
				+ zfill(currentPage, Globals.numZeros) + Globals.EndImageURL);
		doneLoading();
	}

	/**
	 * displays a new comic image to the view
	 */
	private void displayNewView() {
		showLoading();
		adjustControls();
		myWebView.clearView();
		myWebView.loadUrl(Globals.StartImageURL
				+ zfill(currentPage, Globals.numZeros) + Globals.EndImageURL);
		doneLoading();
	}

	@Override
	public void onClick(View v) {
		if (v == next) {
			setupNextView();
		} else if (v == back) {
			setupPrevView();
		} else if (v == first) {
			setupFirstView();
		} else if (v == last) {
			setupLastView();
		} else if (v == mainMenu) {
			finish();
		}
	}

	/**
	 * setups for displaying of next immediate view
	 */
	private void setupNextView() {
		++currentPage;
		displayNewView();
	}

	/**
	 * setups for displaying of previous immediate view
	 */
	private void setupPrevView() {
		--currentPage;
		displayNewView();
	}

	/**
	 * setups for displaying of the first view of the volume
	 */
	private void setupFirstView() {
		currentPage = firstVolPage;
		displayNewView();
	}

	/**
	 * setups for displaying of the last view of the volume
	 */
	private void setupLastView() {
		currentPage = lastVolPage;
		displayNewView();
	}

	/**
	 * adjusts the clickable controls based on displayed page
	 */
	private void adjustControls() {
		if (currentPage > firstVolPage && currentPage < lastVolPage) {
			first.setEnabled(true);
			first.setFocusable(true);
			back.setEnabled(true);
			back.setFocusable(true);
			next.setEnabled(true);
			next.setFocusable(true);
			last.setEnabled(true);
			last.setFocusable(true);
		} else if (currentPage == firstVolPage) {
			first.setEnabled(false);
			first.setFocusable(false);
			back.setEnabled(false);
			back.setFocusable(false);
			next.setEnabled(true);
			next.setFocusable(true);
			last.setEnabled(true);
			last.setFocusable(true);
		} else if (currentPage == lastVolPage) {
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("firstVolPage", firstVolPage);
		outState.putInt("lastVolPage", lastVolPage);
		outState.putInt("currentPage", currentPage);
		super.onSaveInstanceState(outState);
	}

	/**
	 * displays loading dialog
	 */
	public void showLoading() {
		loadingDialog = ProgressDialog
				.show(this, "", "Loading. Please wait...");
	}

	/**
	 * disables loading dialog
	 */
	public void doneLoading() {
		loadingDialog.dismiss();
	}
}