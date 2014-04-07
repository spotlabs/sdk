/*******************************************************************************
 * Copyright 2013 Spot Labs Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.spotlabs.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ListAdapter;
import android.widget.Scroller;

import java.util.LinkedList;

public class NVListView extends AdapterView<ListAdapter> {

    public static enum Orientation {HORIZONTAL, VERTICAL};

    public static enum Position {FRONT, BACK, CENTER};

    private static enum Edge {FRONT, BACK}
    
    

    private ListAdapter mAdapter;
    private Orientation mOrientation = Orientation.HORIZONTAL;
    private Position mSnapMode = Position.CENTER;

    private View mCenterItem;

    private final LinkedList<View> mCachedItemViews = new LinkedList<View>();

    private final GestureDetector mGestureDetector;

    private final Scroller mScroller;

	HorizontalScrollView blah;

    private int mBackEdge;

    private boolean isSnapping = false;
    private boolean inFling = false;

    private final static String TAG = "NVListView";

    private Handler mHandler;

    private DataSetObserver mDataSetObserver = new DataSetObserver() {

        public void onChanged() {
            reset();
            Log.w(TAG, "got adapter change");
        }

        public void onInvalidated() {
            removeAllViewsInLayout();
            requestLayout();
        }

    };

    private final Runnable mAnimator = new Runnable() {

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {
                inFling = true;
                post(mAnimator);
                scrollTo(mScroller.getCurrX(), 0);
            } else {
                if (!isSnapping) snapToView(Position.CENTER);
                else {
                    inFling = false;
                    isSnapping = false;
                    if (getOnItemSelectedListener() != null) {
                        View view = getSelectedView();
                        getOnItemSelectedListener().onItemSelected(NVListView.this, view, getPositionForView(view), view.getId());
                    }
                }
            }
        }

    };

    public NVListView(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(context, new GestureListener());
        mScroller = new Scroller(context);
        mHandler = new Handler();
    }

    public NVListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, new GestureListener());
        mScroller = new Scroller(context);
        mHandler = new Handler();
    }


    public NVListView(Context context, AttributeSet attrs, int inDefStyle) {
        super(context, attrs, inDefStyle);
        mGestureDetector = new GestureDetector(context, new GestureListener());
        mScroller = new Scroller(context);
        mHandler = new Handler();
    }

    private boolean mInAutoMove;

    public void startAutoMove(int moveInterval, int moveDuration) {
        if (!mInAutoMove){
            int delay = 5000;
            mInAutoMove = true;
            mHandler.postDelayed(new FlipRunnable(moveDuration,moveInterval),delay);
        }
    }
    
    public void stopAutoMove() {
    	mInAutoMove = false;
    }
    
    
    private class FlipRunnable implements Runnable {
    	private int moveDuration;
        private int moveDelay;
    	
    	//adding enums detailing how the end behavior is like is also possible.
    	//e.g. start back to beginning, or go backwards.
		public FlipRunnable(int moveDuration, int moveDelay) {
			this.moveDuration = moveDuration;
            this.moveDelay = moveDelay;
		}

		public void run() {
            if (mInAutoMove){
                if (getLastChild() == getCenterItem()) {
                    stopAutoMove();
                }
                else {
                    moveNext(moveDuration);
                    mHandler.postDelayed(this,moveDelay);
                }
            }
		}
    }


    protected void reset() {

        removeAllViewsInLayout();
        if (mAdapter.isEmpty()) return;

        // loop through until we fill the screen or hit the end of the list
        int position = 0;
        int edge = 0;

        while (edge < getLayoutDimension(this) && position < mAdapter.getCount()) {
            View newBottomChild = mAdapter.getView(position, mCachedItemViews.poll(), this);
            addAndMeasureChild(newBottomChild, Edge.BACK);
            edge += getLayoutMeasuredDimension(newBottomChild);
            position++;
        }
        mBackEdge = edge;
        positionItems();
        snapToView(mSnapMode, getFirstChild(), 0);
        notifyPositionChange();
    }


    protected void notifyPositionChange() {
        float listSize = getLayoutDimension(this);
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof onPositionChangedListener) {
                float position = (getLayoutCenter(view) - getScrollPosition()) / listSize;
                ((onPositionChangedListener) view).onPositionChanged(position);
            }
        }
    }

    @Override
    public void removeAllViewsInLayout() {
        for (int i = 0; i < this.getChildCount(); i++) {
            mCachedItemViews.add(getChildAt(i));
        }
        super.removeAllViewsInLayout();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getChildCount() == 0) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_UP && !inFling) {
            snapToView(mSnapMode);
        }

        mGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        notifyPositionChange();
        addViews();
        //	if(/*removeViews() || */addViews()) requestLayout();

    }

    private void snapToView(Position snapMode) {
        snapToView(snapMode, getSelectedView(), 250);
    }

    
    public void moveNext(int moveDuration) {
    	View nextView = getChildAt(getSelectedItemPosition() + 1);
    	snapToView(mSnapMode, nextView, moveDuration);
    }
    
    private void snapToView(Position snapMode, View view, int duration) {
        float delta;

        switch (snapMode) {
            case FRONT:
                delta = getLayoutPosition(view) - getScrollPosition();
                break;

            case CENTER:
                delta = getLayoutCenter(view) - (getScrollPosition() + getLayoutCenter(this));
                break;

            case BACK:
                delta = getLayoutEdge(view) - (getScrollPosition() + getLayoutDimension(this));
                break;

            default:
                delta = 0;
                break;
        }

        isSnapping = true;
        mScroller.startScroll((int) getScrollPosition(), 0, (int) delta, 0, duration);
        post(mAnimator);

    }

    /**
     * Adds a view as a child view and takes care of measuring it
     *
     * @param child The view to add
     */
    private void addAndMeasureChild(View child, Edge edge) {

        // if we don't have params, default them out
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
        addViewInLayout(child, edge == Edge.BACK ? -1 : 0, params, true);

        // measure the child
        switch (mOrientation) {
            case VERTICAL:
                child.measure(MeasureSpec.EXACTLY | getWidth(), MeasureSpec.UNSPECIFIED);
                break;
            case HORIZONTAL:
                child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY | getHeight());
                break;
        }


    }

    /**
     * Positions the children at the correct position
     */
    private void positionItems() {

        int edge = 0;
        float center = getLayoutDimension(this) / 2 + getScrollPosition();

        for (int index = 0; index < getChildCount(); index++) {
            View child = getChildAt(index);

            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            int left = (getWidth() - width) / 2;
            int top = (getHeight() - height) / 2;

            switch (mOrientation) {
                case VERTICAL:
                    child.layout(left, edge, left + width, edge + height);
                    break;

                case HORIZONTAL:
                    child.layout(edge, top, edge + width, top + height);
                    break;
            }

            if (getLayoutPosition(child) < center && getLayoutEdge(child) > center) mCenterItem = child;

            edge += getLayoutDimension(child);
        }

    }


    private boolean addViews() {

        if (getChildCount() == 0) return false;

        boolean needsLayout = false;

        // add to front
        float edge = getLayoutPosition(getFirstChild());
        int position = getPositionForView(getFirstChild()) - 1;
        float extent = getScrollPosition();

        while (edge > extent && position > 0) {
            View newView = mAdapter.getView(position, getCachedView(), this);
            addAndMeasureChild(newView, Edge.FRONT);
            edge -= getLayoutMeasuredDimension(newView);
            position--;
        }

        // add to end
        // get the position in the adapter and the edge for the last item
        edge = getLayoutEdge(getLastChild());
        position = getPositionForView(getLastChild()) + 1;
        extent = getScrollPosition() + getLayoutDimension(this);

        while (edge < extent && position < mAdapter.getCount()) {
            View newView = mAdapter.getView(position, getCachedView(), this);
            addAndMeasureChild(newView, Edge.BACK);
            edge += getLayoutMeasuredDimension(newView);
            position++;
            needsLayout = true;
        }

        positionItems();
        return needsLayout;
    }


    private boolean removeViews() {
        if (getChildCount() == 0) return false;

        boolean needsLayout = false;

        // remove from front
        float edge = getLayoutEdge(getFirstChild());
        float extent = getScrollPosition();

        while (edge < extent) {
            View view = getFirstChild();
            removeViewInLayout(view);
            mCachedItemViews.add(view);
            edge += getLayoutMeasuredDimension(view);
            needsLayout = true;
        }

	/*	// from the end
        edge = getLayoutPostion(getLastChild(), mOrientation);
        extent = getScrollPosition() + getLayoutDimension(this, mOrientation);

		while (edge > extent) {
			View view = getLastChild();
			removeViewInLayout(view);
			mCachedItemViews.add(view);
			edge -= getLayoutMeasuredDimension(view, mOrientation);
			needsLayout = true;
		}*/

        positionItems();
		
		/*int childCount = getChildCount();		
		View view;
		
		// remove from front
		view = getChildAt(0);
		while (getChildCount() > 0 && getLayoutEdge(view, mOrientation) < getScrollPosition()) {
			TextView textView = (TextView) view;
			Log.w("Carousel", "removing: " + textView.getText() + " adapterIndex:" + mAdapterIndex );
			removeViewInLayout(view);
			mCachedItemViews.add(view);
			view = getChildAt(0);			
			mAdapterIndex++;
			needsLayout = true;
		}
		
		// remove from end
		childCount = getChildCount();
		view = getLastChild();
		
		while (view != null && getLayoutPostion(view, mOrientation) > getScrollPosition() + getLayoutEdge(this, mOrientation)) {
			removeViewInLayout(view);
			mCachedItemViews.add(view);
			childCount--;
			mBackEdge -= getLayoutMeasuredDimension(view, mOrientation);
			view = childCount > 1 ? getLastChild() : null;
		}
		*/
        return needsLayout;
    }

    private View getCachedView() {
        if (mCachedItemViews.size() != 0) {
            return mCachedItemViews.removeFirst();
        }
        return null;
    }

    @Override
    public int getSelectedItemPosition() {
    	if (getSelectedView() == null) return -1;
        return getPositionForView(getSelectedView());
    }

    protected View getFirstVisibleItem() {
        int index = 0;
        View view = getChildAt(index);

        while (getLayoutCenter(view) < getScrollPosition()) {
            index++;
            view = getChildAt(index);
        }
        return view;
    }

    protected View getLastVisibleItem() {
        int index = getChildCount() - 1;
        View view = getChildAt(index);

        while (getLayoutCenter(view) > getScrollPosition() + getLayoutDimension(this)) {
            index--;
            view = getChildAt(index);
        }
        return view;
    }

    
    protected View getCenterItem() {
        float center = getScrollPosition() + getLayoutCenter(this);

        View view = null;
        int i = 0;

        while (i < getChildCount()) {
            view = getChildAt(i);
            if (i == 0 && getScrollPosition() + getStartPosition() - getLayoutDimension(view)/2 < 0) break;
            if (getLayoutPosition(view) < center && getLayoutEdge(view) > center) break;
            i++;
        }

        return view;
    }


    protected View getLastChild() {
        if (getChildCount() == 0) return null;
        return getChildAt(getChildCount() - 1);
    }

    protected View getFirstChild() {
        if (getChildCount() == 0) return null;
        return getChildAt(0);
    }


    protected float getLastPosition() {
        if (getPositionForView(getLastChild()) == mAdapter.getCount() - 1) {
            return getLayoutEdge(getLastChild());
        }
        return getScrollPosition() + 2 * getLayoutDimension(this);
    }


    protected boolean edgeInBounds(float edge) {
        float center = getLayoutDimension(this) / 2;
        return edge < center && getLayoutEdge(getChildAt(getChildCount() - 1)) > center;
    }


    protected float getLayoutDimension(View view) {
        switch (mOrientation) {
            case VERTICAL:
                return view.getHeight();
            case HORIZONTAL:
                return view.getWidth();
            default:
                return -1.0f;
        }
    }

    protected float getLayoutMeasuredDimension(View view) {
        switch (mOrientation) {
            case VERTICAL:
                return view.getMeasuredHeight();
            case HORIZONTAL:
                return view.getMeasuredWidth();
            default:
                return -1.0f;
        }
    }

    protected float getLayoutPosition(View view) {
        switch (mOrientation) {
            case VERTICAL:
                return view.getTop();
            case HORIZONTAL:
                return view.getLeft();
            default:
                return -1.0f;
        }
    }

    protected float getLayoutEdge(View view) {
        switch (mOrientation) {
            case VERTICAL:
                return view.getBottom();
            case HORIZONTAL:
                return view.getRight();
            default:
                return -1.0f;
        }
    }

    protected float getStartPosition() {
        switch (mSnapMode) {
            case FRONT:
                return 0;
            case BACK:
                return 0;
            case CENTER:
                return getLayoutCenter(this);
            default:
                return -1;
        }
    }

    protected float getEndPosition() {
        switch (mSnapMode) {
            case FRONT:
                return getLayoutDimension(this);
            case BACK:
                return getLayoutDimension(this);
            case CENTER:
                return getLayoutCenter(this);
            default:
                return -1;
        }
    }

    protected float getLayoutCenter(View view) {
        return getLayoutPosition(view) + getLayoutDimension(view) / 2;
    }

    protected float getScrollPosition() {
        switch (mOrientation) {
            case VERTICAL:
                return getScrollY();
            case HORIZONTAL:
                return getScrollX();
            default:
                return -1.0f;
        }
    }


    public ListAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(ListAdapter adapter) {

        mAdapter = adapter;
        mAdapter.registerDataSetObserver(mDataSetObserver);

        removeAllViewsInLayout();
        requestLayout();
    }

    public void setSelection(int position) {
        //TODO: implement
    }

    public View getSelectedView() {
        switch (mSnapMode) {
            case FRONT:
                return getFirstVisibleItem();
            case BACK:
                return getLastVisibleItem();
            case CENTER:
                return getCenterItem();
            default:
                return null;
        }
    }

    public Orientation getOrientation() {
        return mOrientation;
    }

    public void setOrientation(Orientation orientation) {
        mOrientation = orientation;
    }

    //expects coordinate position! Not by index.
    protected View getViewByPosition(float position) {
        View view = null;
        for (int i = 0; i < getChildCount(); i++) {
            view = getChildAt(i);
            if (getLayoutPosition(view) - getScrollPosition() < position && getLayoutEdge(view) - getScrollPosition() > position)
                break;
        }
        return view;
    }


    private class GestureListener extends SimpleOnGestureListener {
    	
    	@Override
    	public boolean onDown(MotionEvent event) {
    		stopAutoMove();
			return true;
    	}

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            View view = getViewByPosition(event.getX());
            if (view != null) performItemClick(view, getPositionForView(view), view.getId());

            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            View view = getViewByPosition(event.getX());
            if (view != null && getOnItemLongClickListener() != null)
                getOnItemLongClickListener().onItemLongClick(NVListView.this, view, getPositionForView(view), view.getId());

        }

        @Override
        public boolean onScroll(MotionEvent downEvent, MotionEvent moveEvent, float distanceX, float distanceY) {
            if (!mScroller.isFinished()) mScroller.forceFinished(true);
            if (getScrollPosition() + distanceX > -getStartPosition()) {
                scrollBy((int) distanceX, 0);
            }
            return true;
        }


        @Override
        public boolean onFling(MotionEvent downEvent, MotionEvent flingEvent, float velocityX, float velocityY) {
            mScroller.fling((int) getScrollPosition(), 0, (int) -velocityX, 0, (int) -getStartPosition(), (int) getLastPosition(), 0, 0);
            post(mAnimator);

            return true;
        }

    }

    public interface onPositionChangedListener {
        public void onPositionChanged(float position);
    }

}
