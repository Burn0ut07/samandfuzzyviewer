package com.comic.misc;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

public class NavBarListener implements AnimationListener {
	
	private RelativeLayout navbar;
	
	public NavBarListener(View navbar) {
		super();
		this.navbar = (RelativeLayout)navbar;
	}

	/* (non-Javadoc)
	 * @see AnimationListener#onAnimationEnd(android.view.animation.Animation)
	 */
	@Override
	public void onAnimationEnd(Animation animation) {
		navbar.setVisibility(View.GONE);
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
