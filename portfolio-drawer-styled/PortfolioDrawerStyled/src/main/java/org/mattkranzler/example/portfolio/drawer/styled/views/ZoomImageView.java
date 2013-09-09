package org.mattkranzler.example.portfolio.drawer.styled.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class ZoomImageView extends ImageView {
    Matrix matrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Are we at a limit?
    boolean limitX = false;
    boolean limitY = false;

    // Remember some things for zooming
    PointF last = new PointF();
    PointF start = new PointF();
    float minScale = 1f;
    float maxScale = 3f;
    float[] m;

    float redundantXSpace, redundantYSpace;

    float width, height;
    static final int CLICK = 3;
    float saveScale = 1f;
    float right, bottom, origWidth, origHeight, bmWidth, bmHeight;

    private OnTapListener onTapListener = null;

    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureListener;

    private boolean isTouchDisabled = Boolean.FALSE;

    public ZoomImageView(Context context) {
        super(context);
        sharedConstructing(context);
    }

    @SuppressWarnings({"unused"}) // This is used for XML
    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
    }

    public void setOnSingleTapListener(OnTapListener onTapListener) {
        this.onTapListener = onTapListener;
    }

    /**
     * If you set this to true, then
     * @param disabled
     */
    public void setTouchDisabled(boolean disabled){
        this.isTouchDisabled = disabled;
    }

    public float getCurrentScale(){
        return this.saveScale;
    }

    public boolean isAtXLimit(){
        return limitX;
    }

    public boolean isAtYLimit(){
        return limitY;
    }

    private void sharedConstructing(Context context) {
        super.setClickable(true);

        mGestureListener = new GestureDetector(context, new GestureListener());
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        matrix.setTranslate(1f, 1f);
        m = new float[9];
        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // The gesture detector gets all the touch events, because it might need to click through
        mGestureListener.onTouchEvent(event);

        // If touch is disabled, leave here
        if( isTouchDisabled ){
            return super.onTouchEvent(event);
        }

        // Check to see if we're scaling
        mScaleDetector.onTouchEvent(event);

        matrix.getValues(m);
        float x = m[Matrix.MTRANS_X];
        float y = m[Matrix.MTRANS_Y];
        PointF curr = new PointF(event.getX(), event.getY());

        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                last.set(event.getX(), event.getY());
                start.set(last);
                mode = DRAG;

                limitX = Boolean.FALSE;
                limitY = Boolean.FALSE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG && !mScaleDetector.isInProgress()) {
                    float deltaX = curr.x - last.x;
                    float deltaY = curr.y - last.y;
                    float scaleWidth = Math.round(origWidth * saveScale);
                    float scaleHeight = Math.round(origHeight * saveScale);

                    if (scaleWidth < width) {
                        deltaX = 0;
                        if (y + deltaY > 0) {
                            deltaY = -y;
                        } else if (y + deltaY < -bottom) {
                            deltaY = -(y + bottom);
                        }
                    } else if (scaleHeight < height) {
                        deltaY = 0;
                        if (x + deltaX > 0) {
                            limitX = Boolean.TRUE;
                            deltaX = -x;
                        } else if (x + deltaX < -right) {
                            limitX = Boolean.TRUE;
                            deltaX = -(x + right);
                        }
                    } else {
                        if (x + deltaX > 0) {
                            limitX = Boolean.TRUE;
                            deltaX = -x;
                        } else if (x + deltaX < -right) {
                            limitX = Boolean.TRUE;
                            deltaX = -(x + right);
                        }

                        if (y + deltaY > 0) {
                            limitY = Boolean.TRUE;
                            deltaY = -y;
                        } else if (y + deltaY < -bottom) {
                            limitY = Boolean.TRUE;
                            deltaY = -(y + bottom);
                        }
                    }

                    matrix.postTranslate(deltaX, deltaY);
                    last.set(curr.x, curr.y);
                }
                break;

            case MotionEvent.ACTION_UP:
                mode = NONE;
                int xDiff = (int) Math.abs(curr.x - start.x);
                int yDiff = (int) Math.abs(curr.y - start.y);
                if (xDiff < CLICK && yDiff < CLICK)
                    performClick();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }

        setImageMatrix(matrix);
        invalidate();

        return super.onTouchEvent(event);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);

        if (bm != null) {
            bmWidth = bm.getWidth();
            bmHeight = bm.getHeight();
        }
    }

    public void setMaxZoom(float x) {
        maxScale = x;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = ZOOM;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float origScale = saveScale;
            saveScale = saveScale * scaleFactor;
            if (saveScale > maxScale) {
                saveScale = maxScale;
                scaleFactor = maxScale / origScale;
            } else if (saveScale < minScale) {
                saveScale = minScale;
                scaleFactor = minScale / origScale;
            }
            right = width * saveScale - width - (2 * redundantXSpace * saveScale);
            bottom = height * saveScale - height - (2 * redundantYSpace * saveScale);
            if (origWidth * saveScale <= width || origHeight * saveScale <= height) {
                matrix.postScale(scaleFactor, scaleFactor, width / 2, height / 2);
                if (scaleFactor < 1) {
                    matrix.getValues(m);
                    float x = m[Matrix.MTRANS_X];
                    float y = m[Matrix.MTRANS_Y];
                    if (scaleFactor < 1) {
                        if (Math.round(origWidth * saveScale) < width) {
                            if (y < -bottom)
                                matrix.postTranslate(0, -(y + bottom));
                            else if (y > 0)
                                matrix.postTranslate(0, -y);
                        } else {
                            if (x < -right)
                                matrix.postTranslate(-(x + right), 0);
                            else if (x > 0)
                                matrix.postTranslate(-x, 0);
                        }
                    }
                }
            } else {
                matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
                matrix.getValues(m);
                float x = m[Matrix.MTRANS_X];
                float y = m[Matrix.MTRANS_Y];
                if (scaleFactor < 1) {
                    if (x < -right)
                        matrix.postTranslate(-(x + right), 0);
                    else if (x > 0)
                        matrix.postTranslate(-x, 0);
                    if (y < -bottom)
                        matrix.postTranslate(0, -(y + bottom));
                    else if (y > 0)
                        matrix.postTranslate(0, -y);
                }
            }

            return true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        fitAndCenterView();
    }

    public boolean canScrollHorizontally(int direction) {
        matrix.getValues(m);
        float x = Math.abs(m[Matrix.MTRANS_X]);
        float scaleWidth = Math.round(origWidth * saveScale);

        if (scaleWidth < width) {
            return false;

        } else {
            if (x - direction <= 0)
                return false; // reach left edge
            else if (x + width - direction >= scaleWidth)
                return false; // reach right edge

            return true;
        }
    }

    public void resetZoom() {
        Matrix m = getImageMatrix();
        RectF drawableRect = new RectF(0, 0, bmWidth, bmHeight);
        RectF viewRect = new RectF(0, 0, getWidth(), getHeight());
        m.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);

        matrix = m;
        setImageMatrix(m);

        fitAndCenterView();

        invalidate();
    }

    private void fitAndCenterView() {
        //Fit to screen.
        float scale;
        float scaleX = width / bmWidth;
        float scaleY = height / bmHeight;
        scale = Math.min(scaleX, scaleY);
        matrix.setScale(scale, scale);
        setImageMatrix(matrix);
        saveScale = 1f;

        // Center the image
        redundantYSpace = height - (scale * bmHeight);
        redundantXSpace = width - (scale * bmWidth);
        redundantYSpace /= (float) 2;
        redundantXSpace /= (float) 2;

        matrix.postTranslate(redundantXSpace, redundantYSpace);

        origWidth = width - 2 * redundantXSpace;
        origHeight = height - 2 * redundantYSpace;
        right = width * saveScale - width - (2 * redundantXSpace * saveScale);
        bottom = height * saveScale - height - (2 * redundantYSpace * saveScale);
        setImageMatrix(matrix);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if( null != onTapListener ){
                onTapListener.onTap(ZoomImageView.this);
            }

            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if( null != onTapListener ){
                onTapListener.onTap(ZoomImageView.this);
            }

            return true;
        }
    }



    public interface OnTapListener {
        void onTap(View view);
    }
}