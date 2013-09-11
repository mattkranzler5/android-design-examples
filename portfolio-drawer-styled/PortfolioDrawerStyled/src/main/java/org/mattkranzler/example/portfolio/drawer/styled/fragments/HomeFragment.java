package org.mattkranzler.example.portfolio.drawer.styled.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.mattkranzler.example.portfolio.drawer.styled.R;

public class HomeFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    public static interface HomeFragmentCallbacks {
        void onFamilySelected();
        void onWeddingSelected();
        void onChildrenSelected();
        void onBabiesSelected();
    }

    private static final long ANIMATION_INTERVAL = 5000l;
    private static final int PAGE_FAMILY = 0;
    private static final int PAGE_WEDDING = 1;
    private static final int PAGE_CHILDREN = 2;
    private static final int PAGE_BABIES = 3;

    private final int[] mCategoryImages = new int[] {
            R.drawable.family_1,
            R.drawable.wedding_1,
            R.drawable.children_1,
            R.drawable.babies_1
    };

    private final int[] mCategoryTitles = new int[] {
            R.string.category_family_title,
            R.string.category_weddings_title,
            R.string.category_children_title,
            R.string.category_babies_title
    };

    private final int[] mCategoryDescriptions = new int[] {
            R.string.category_family_description,
            R.string.category_wedding_description,
            R.string.category_children_description,
            R.string.category_babies_description
    };

    // views
    private ViewPager mViewPager;
    private TextView mCategoryTitle;
    private TextView mCategoryDescription;

    private Handler mViewPagerAnimationHandler;
    private Runnable mAnimateViewPagerRunnable;
    private HomeFragmentCallbacks mCallbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (HomeFragmentCallbacks) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, null);

        // inflate the views
        mViewPager = (ViewPager) root.findViewById(R.id.home_view_pager);
        mCategoryTitle = (TextView) root.findViewById(R.id.home_category_title);
        mCategoryDescription = (TextView) root.findViewById(R.id.home_category_description);

        mCategoryTitle.setOnClickListener(this);
        mCategoryDescription.setOnClickListener(this);

        // setup pager adapter
        mViewPager.setAdapter(new HomePagerAdapter(mCategoryImages));
        mViewPager.setOnPageChangeListener(this);

        // add a touch listener and stop automatically animating if it is being interacted with
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        stopAnimatingViewPager();
                        break;
                    case MotionEvent.ACTION_UP:
                        startAnimatingViewPager();
                        break;
                }
                return false;
            }
        });

        onPageSelected(mViewPager.getCurrentItem());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        startAnimatingViewPager();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAnimatingViewPager();
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {/* no-op */}

    @Override
    public void onPageSelected(int i) {

        // update category text
        mCategoryTitle.setText(mCategoryTitles[i]);
        mCategoryDescription.setText(mCategoryDescriptions[i]);

        // animate the view pager
        startAnimatingViewPager();
    }

    @Override
    public void onPageScrollStateChanged(int i) {/* no-op */}

    /**
     * Begins to animate the view pager on a scheduled interval
     */
    private void startAnimatingViewPager() {

        // create a handler if necessary
        if (mViewPagerAnimationHandler == null) {
            mViewPagerAnimationHandler = new Handler(Looper.getMainLooper());
        }

        // cancel the last runnable if necessary
        stopAnimatingViewPager();

        // create a new runnable to animate to the next page
        mAnimateViewPagerRunnable = new Runnable() {
            @Override
            public void run() {
                final int totalHeroes = mViewPager.getAdapter().getCount();
                final int current = mViewPager.getCurrentItem();
                final int index = current == (totalHeroes - 1) ? 0 : current + 1;
                mViewPager.setCurrentItem(index, index != 0); // only animate if we are going forward so it's not jarring
            }
        };

        // schedule it
        mViewPagerAnimationHandler.postDelayed(mAnimateViewPagerRunnable, ANIMATION_INTERVAL);
    }

    /**
     * Stops animating the view pager if it is currently being animated
     */
    private void stopAnimatingViewPager() {
        if (mAnimateViewPagerRunnable != null) {
            mViewPagerAnimationHandler.removeCallbacks(mAnimateViewPagerRunnable);
            mAnimateViewPagerRunnable = null;
        }
    }

    @Override
    public void onClick(View v) {
        final int currentItem = mViewPager.getCurrentItem();
        switch (currentItem) {
            case PAGE_FAMILY:
                mCallbacks.onFamilySelected();
                break;
            case PAGE_WEDDING:
                mCallbacks.onWeddingSelected();
                break;
            case PAGE_CHILDREN:
                mCallbacks.onChildrenSelected();
                break;
            case PAGE_BABIES:
                mCallbacks.onBabiesSelected();
                break;
        }
    }

    private class HomePagerAdapter extends PagerAdapter {

        private final int[] mImages;

        private HomePagerAdapter(int[] images) {
            this.mImages = images;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View root = LayoutInflater.from(container.getContext()).inflate(R.layout.home_pager_item, null);
            ImageView imageView = (ImageView) root.findViewById(R.id.image);
            View imageBtn = root.findViewById(R.id.image_btn);

            Picasso.with(container.getContext())
                    .load(mImages[position])
                    .into(imageView);

            container.addView(root);
            imageBtn.setOnClickListener(HomeFragment.this);

            return root;
        }

        @Override
        public void destroyItem(final ViewGroup container, int position, final Object object) {
            container.post(new Runnable() {
                @Override
                public void run() {
                    container.removeView((View) object);
                }
            });
        }

        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }
    }
}
