package com.comic.viewer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * A clickable WebView
 * 
 * @author Tom Coxon
 *
 */
//Used under the terms of GPLv2 
//source: http://github.com/tcoxon/XkcdViewer/blob/master/src/net/bytten/xkcdviewer/ClickableWebView.java
//For full license please view http://www.gnu.org/licenses/gpl-2.0.html
public class ClickableWebView extends WebView {
	
	private float lastTouchX, lastTouchY;
	private boolean hasMoved = false;
	
	public ClickableWebView(Context cxt) {
		super(cxt);
	}

	public ClickableWebView(Context cxt, AttributeSet attrs) {
		super(cxt, attrs);
	}

	public ClickableWebView(Context cxt, AttributeSet attrs, int defStyle) {
		super(cxt, attrs, defStyle);
	}

	private boolean moved(MotionEvent evt) {
		return hasMoved || Math.abs(evt.getX() - lastTouchX) > 10.0
				|| Math.abs(evt.getY() - lastTouchY) > 10.0;
	}

	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		boolean consumed = super.onTouchEvent(evt);

		if (isClickable()) {
			switch (evt.getAction()) {
			case MotionEvent.ACTION_DOWN:
				lastTouchX = evt.getX();
				lastTouchY = evt.getY();
				hasMoved = false;
				break;
			case MotionEvent.ACTION_MOVE:
				hasMoved = moved(evt);
				break;
			case MotionEvent.ACTION_UP:
				if (!moved(evt))
					performClick();
				break;
			}
		}

		return consumed;
	}

}