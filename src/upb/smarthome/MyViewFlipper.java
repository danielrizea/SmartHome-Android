/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * The Class MyViewFlipper.
 * This Class is used to implement flip gesture 
 */
public class MyViewFlipper extends ViewFlipper {

    /** The Constant logTag. */
    static final String logTag = "ViewFlipper";
    
    /** The Constant MIN_DISTANCE. */
    static final int MIN_DISTANCE = 30;
    
    /** The up y. */
    private float downX, downY, upX, upY;
    
    /** The slide left in. */
    Animation slideLeftIn;
    
    /** The slide left out. */
    Animation slideLeftOut;
    
    /** The slide right in. */
    Animation slideRightIn;
    
    /** The slide right out. */
    Animation slideRightOut;
    
    /** The context. */
    Context context;
    
    /** The view flipper. */
    ViewFlipper viewFlipper;

    /**
     * Instantiates a new my view flipper.
     *
     * @param context the context
     */
    public MyViewFlipper(Context context) {
        super(context);
        viewFlipper=this;
        this.context=context;
        System.out.println("I am in MyFlipper() counstructor...");
    }

    /**
     * Instantiates a new my view flipper.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public MyViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        viewFlipper=this;
        System.out.println("I am in MyFlipper() counstructor...");
        slideLeftIn =
            AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
        slideLeftOut =
            AnimationUtils.loadAnimation(context, R.anim.slide_left_out);
        slideRightIn =
            AnimationUtils.loadAnimation(context, R.anim.slide_right_in);
        slideRightOut =
            AnimationUtils.loadAnimation(context, R.anim.slide_right_out);
    }

    /* (non-Javadoc)
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // swipe horizontal?
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        this.onLeftToRightSwipe();
                        return true;
                    }
                    if (deltaX > 0) {
                        this.onRightToLeftSwipe();
                        return true;
                    }
                } else {
                    if(Math.abs(deltaX)<15){
                        onClickEvent();
                    }
                    Log.i("swipe", "Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
                }
                // swipe vertical?
                if (Math.abs(deltaY) > MIN_DISTANCE) {
                    // top or down
                    if (deltaY < 0) {
                        this.onTopToBottomSwipe();
                        return true;
                    }
                    if (deltaY > 0) {
                        this.onBottomToTopSwipe();
                        return true;
                    }
                } else {
                    Log.i(logTag, "Swipe was only " + Math.abs(deltaX)
                        + " long, need at least " + MIN_DISTANCE);
                }

                return true;
            }
        }
        return false;

    }

    /**
     * On right to left swipe.
     */
    public void onRightToLeftSwipe() {

        viewFlipper.setInAnimation(slideLeftIn);
        viewFlipper.setOutAnimation(slideLeftOut);
        viewFlipper.showNext();
    }

    /**
     * On left to right swipe.
     */
    public void onLeftToRightSwipe() {

        viewFlipper.setInAnimation(slideRightIn);
        viewFlipper.setOutAnimation(slideRightOut); 
        viewFlipper.showPrevious();
    }

    /**
     * On top to bottom swipe.
     */
    public void onTopToBottomSwipe() {
        Log.i(logTag, "onTopToBottomSwipe!");
        // activity.doSomething();
    }

    /**
     * On bottom to top swipe.
     */
    public void onBottomToTopSwipe() {
        Log.i(logTag, "onBottomToTopSwipe!");
        // activity.doSomething();
    }

    /**
     * On click event.
     */
    public void onClickEvent(){
        Toast.makeText(context, "Click",Toast.LENGTH_SHORT);
    }

    /* (non-Javadoc)
     * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;    // Here if true then Flipping done.
                        // And if false then click event done. 
    }

}