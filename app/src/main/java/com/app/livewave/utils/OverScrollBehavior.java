package com.app.livewave.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

public class OverScrollBehavior extends CoordinatorLayout.Behavior<View>{

    private int OVER_SCROLL_AREA = 5;

    private int overScrollY = 0;

    public OverScrollBehavior() {
    }

    public OverScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        overScrollY = 0;
        return true;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        if(dyUnconsumed==0) {
            return;
        }
        overScrollY -=(dyUnconsumed/OVER_SCROLL_AREA);
        ViewGroup group = (ViewGroup) target;
        int count=group.getChildCount();
        for(int i = 0; i < count; i++){
            View view=group.getChildAt(i);
            view.setTranslationY(overScrollY);
        }
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int type) {
        moveToDefPosition(target);
    }

    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, float velocityX, float velocityY) {
        if(overScrollY == 0){
            return false;
        }
        // Smooth animate to 0 when user fling view
        moveToDefPosition(target);
        return true;
    }

    private void moveToDefPosition(View target){
        ViewGroup group= (ViewGroup) target;
        int count=group.getChildCount();
        for(int i = 0; i<count; i++){
            View view=group.getChildAt(i);
            ViewCompat.animate(view)
                    .translationY(0f)
                    .start();
        }
    }
}

