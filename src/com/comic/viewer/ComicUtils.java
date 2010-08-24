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

import com.comic.globals.Globals;

import android.graphics.drawable.Drawable;

public class ComicUtils {

	/**
	 * Gets the source of a webpage as a String
	 * 
	 * @param url
	 *            The url of the webpage as a String
	 * @return A String of the source of the webpage
	 */
	public static String getHTTPSource(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		StringBuilder httpSource = new StringBuilder();
		try {
			response = client.execute(request);
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			for (String i = reader.readLine(); reader.read() != -1; i = reader
					.readLine())
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

	/**
	 * Gets the image at the given url
	 * 
	 * @param imageURL
	 *            The url, as a String, to fetch the image from
	 * @return The image in the form of drawable
	 */
	public static Drawable getImage(String imageURL) {
		try {
			InputStream is = (InputStream) new URL(imageURL).getContent();
			return Drawable.createFromStream(is, "Sam and Fuzzy");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
	 * Returns a String of the range for the latest volume
	 * @param currVol TODO
	 * 
	 * @return String range of latest volume
	 */
	public static String lastVolumeRange(int currVol) {
		String volRange = Globals.VOL_RANGES[currVol],
		src = ComicUtils.getHTTPSource("http://samandfuzzy.com");
		Matcher m = Pattern.compile(Globals.StartImageURL + "0+([0-9]+)")
													.matcher(src);
		m.find();
		volRange += ("-" + m.group(1));
		return volRange;
	}

}
