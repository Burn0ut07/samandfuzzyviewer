/**
 * @author Yixin Zhu, Joel Jauregui
 */
package com.comic.viewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.comic.globals.Globals;

public class ComicViewer extends Activity implements OnClickListener {
	private ProgressDialog loadingDialog;
	private Button first, back, mainMenu, next, last;
	private int firstVolPage, lastVolPage, currentPage, currentVol;
	private WebView myWebView;
	private TextView comicTitleView;
	private View zoom;
	private static Pattern comicTitle = 
		Pattern.compile("http://samandfuzzy.com/comics/.+?alt=\"(.+?)\"");
	
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
		// get volume range
		Bundle bundle = getIntent().getExtras();
		// sets up the range of this volume
		setThisVolumeRange((String) bundle.getString("volumeRange"));
		currentVol = (int) bundle.getInt("volumeNumber");
		//new instance
		if (savedInstanceState == null) {
			//sets up first view to display
			setupInitialView();
		} else { //destroyed and recreated
			currentPage = savedInstanceState.getInt("currentPage");
			displayNewView();
		}
	}

	/**
	 * Initial startup consisting of setting up of xml instances and zoom controls
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
		comicTitleView = (TextView) findViewById(R.id.comictitle);
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
	 * 				The specified range in String format
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
	 * Gets the image at the given url
	 * 
	 * @param imageURL
	 *            The url, as a String, to fetch the image from
	 * @return The image in the form of drawable
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
	 * Sets up the initial view, called when launched from main menu
	 */
	private void setupInitialView() {
		showLoading();
		first.setEnabled(false);
		first.setFocusable(false);
		back.setEnabled(false);
		back.setFocusable(false);
		currentPage = firstVolPage;
		myWebView.clearView();
		myWebView.loadUrl(Globals.StartImageURL
				+ zfill(currentPage, Globals.numZeros) + Globals.EndImageURL);
		setComicTitle();
		doneLoading();
	}

	/**
	 * Displays a new comic image to the view
	 */
	private void displayNewView() {
		showLoading();
		adjustControls();
		myWebView.clearView();
		myWebView.loadUrl(Globals.StartImageURL
				+ zfill(currentPage, Globals.numZeros) + Globals.EndImageURL);
		setComicTitle();
		doneLoading();
	}
	
	/**
	 * Sets the comic title to the current page
	 */
	private void setComicTitle() {
		String comicSource = getHTTPSource("http://samandfuzzy.com/" + currentPage);
		Matcher m = comicTitle.matcher(comicSource);
		m.find();
		comicTitleView.setText("Volume " + currentVol + " - " + m.group(1));
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
	 * Setups for displaying of next immediate view
	 */
	private void setupNextView() {
		++currentPage;
		displayNewView();
	}

	/**
	 * Setups for displaying of previous immediate view
	 */
	private void setupPrevView() {
		--currentPage;
		displayNewView();
	}

	/**
	 * Setups for displaying of the first view of the volume
	 */
	private void setupFirstView() {
		currentPage = firstVolPage;
		displayNewView();
	}

	/**
	 * Setups for displaying of the last view of the volume
	 */
	private void setupLastView() {
		currentPage = lastVolPage;
		displayNewView();
	}

	/**
	 * Adjusts the clickable controls based on displayed page
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
		outState.putInt("currentPage", currentPage);
		super.onSaveInstanceState(outState);
	}

	/**
	 * Displays loading dialog
	 */
	public void showLoading() {
		loadingDialog = ProgressDialog
				.show(this, "", "Loading. Please wait...");
	}

	/**
	 * Disables loading dialog
	 */
	public void doneLoading() {
		loadingDialog.dismiss();
	}
	
	/**
	 * Gets the source of a webpage as a String
	 * 
	 * @param url The url of the webpage as a String
	 * @return A String of the source of the webpage
	 */
	private String getHTTPSource(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		StringBuilder httpSource = new StringBuilder();
		try {
			response = client.execute(request);
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			for(String i = reader.readLine(); reader.read() != -1; i = reader.readLine())
				httpSource.append(i);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return httpSource.toString();
	}
}