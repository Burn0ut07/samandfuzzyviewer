package com.comic.misc;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NavBarListener implements AnimationListener {
	
	private RelativeLayout navbar;
	private TextView navReplace;
	
	public NavBarListener(View navbar, View navReplace) {
		super();
		this.navbar = (RelativeLayout)navbar;
		this.navReplace = (TextView)navReplace;
	}

	/* (non-Javadoc)
	 * @see AnimationListener#onAnimationEnd(android.view.animation.Animation)
	 */
	@Override
	public void onAnimationEnd(Animation animation) {
		navbar.setVisibility(View.GONE);
		navReplace.setVisibility(View.VISIBLE);
	}

	/* (non-Javadoc)
	 * @see AnimationListener#onAnimationRepeat(android.view.animation.Animation)
	 */
	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see AnimationListener#onAnimationStart(android.view.animation.Animation)
	 */
	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

}
