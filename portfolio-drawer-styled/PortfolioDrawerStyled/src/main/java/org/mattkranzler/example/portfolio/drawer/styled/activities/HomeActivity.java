package org.mattkranzler.example.portfolio.drawer.styled.activities;


import android.animation.LayoutTransition;
import android.app.ActionBar;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mattkranzler.example.portfolio.drawer.styled.R;
import org.mattkranzler.example.portfolio.drawer.styled.fragments.ContactFragment;
import org.mattkranzler.example.portfolio.drawer.styled.fragments.GalleryFragment;
import org.mattkranzler.example.portfolio.drawer.styled.fragments.HomeFragment;

public class HomeActivity extends FragmentActivity implements HomeFragment.HomeFragmentCallbacks {

    private static final int PAGE_HOME = 0;
    private static final int PAGE_FAMILY = 1;
    private static final int PAGE_WEDDING = 2;
    private static final int PAGE_CHILDREN = 3;
    private static final int PAGE_BABIES = 4;
    private static final int PAGE_CONTACT = 5;

    private final int[] FAMILY_IMAGES = new int[] {
            R.drawable.family_1,
            R.drawable.family_2,
            R.drawable.family_3,
            R.drawable.family_4,
            R.drawable.family_5,
            R.drawable.family_6,
    };

    private final int[] WEDDING_IMAGES = new int[] {
            R.drawable.wedding_2,
            R.drawable.wedding_1,
            R.drawable.wedding_3,
            R.drawable.wedding_4,
    };

    private final int[] CHILDREN_IMAGES = new int[] {
            R.drawable.children_1,
            R.drawable.children_2,
            R.drawable.children_3,
            R.drawable.children_4,
            R.drawable.children_5,
            R.drawable.children_6,
    };

    private final int[] BABY_IMAGES = new int[] {
            R.drawable.babies_1,
            R.drawable.babies_2,
            R.drawable.babies_3,
            R.drawable.babies_4,
            R.drawable.babies_5,
            R.drawable.babies_6,
            R.drawable.babies_7,
            R.drawable.babies_8,
            R.drawable.babies_9,
            R.drawable.babies_10,
            R.drawable.babies_11,
            R.drawable.babies_12,
            R.drawable.babies_13,
    };

    private final int[][] GALLERY_FRAGMENT_IMAGES = new int[][] {
            new int[0],
            FAMILY_IMAGES,
            WEDDING_IMAGES,
            CHILDREN_IMAGES,
            BABY_IMAGES
    };

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ViewGroup mLeftDrawer;
    private TextView mProjects;
    private TextView mFamilyNav;
    private TextView mWeddingNav;
    private TextView mChildrenNav;
    private TextView mBabiesNav;
    private ViewGroup mAvatarCont;
    private TextView mAboutText;
    private TextView mMoreText;

    private boolean mAvatarExpanded;
    private LinearLayout.LayoutParams mCompressedParams;
    private LinearLayout.LayoutParams mExpandedParams;
    private int mExpandProjectsIconResId;
    private int mCollapseProjectsIconResId;
    private int mExpandMoreIconResId;
    private int mCollapseMoreIconResId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mCompressedParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.home_avatar_height_collapsed));
        mExpandedParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        TypedArray ta = obtainStyledAttributes(R.styleable.Theme);
        mExpandProjectsIconResId = ta.getResourceId(R.styleable.Theme_iconOpenDark, 0);
        mCollapseProjectsIconResId = ta.getResourceId(R.styleable.Theme_iconCloseDark, 0);
        mExpandMoreIconResId = ta.getResourceId(R.styleable.Theme_iconOpenLight, 0);
        mCollapseMoreIconResId = ta.getResourceId(R.styleable.Theme_iconCloseLight, 0);
        ta.recycle();

        bindViews();

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.ic_logo_black);
    }

    private void bindViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                0,
                0
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mLeftDrawer = (ViewGroup) findViewById(R.id.left_drawer);
        mLeftDrawer.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        mProjects = (TextView) findViewById(R.id.nav_projects);
        mFamilyNav = (TextView) findViewById(R.id.nav_family);
        mWeddingNav = (TextView) findViewById(R.id.nav_weddings);
        mChildrenNav = (TextView) findViewById(R.id.nav_children);
        mBabiesNav = (TextView) findViewById(R.id.nav_babies);
        mAboutText = (TextView) findViewById(R.id.nav_about);
        mMoreText = (TextView) findViewById(R.id.nav_more);
        mAvatarCont = (ViewGroup) findViewById(R.id.home_avatar_cont);
        findViewById(R.id.home_about_cont).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // expand the avatar container to show the about text
                mAvatarCont.setLayoutParams(
                        mAvatarExpanded ? mCompressedParams : mExpandedParams
                );
                mAvatarCont.requestLayout();

                // show or hide the about text
                mAboutText.setVisibility(
                        mAvatarExpanded ? View.GONE : View.VISIBLE
                );

                // update the more icon
                mMoreText.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        mAvatarExpanded ? mExpandMoreIconResId : mCollapseMoreIconResId, 0
                );

                // update the more text
                mMoreText.setText(
                        mAvatarExpanded ? R.string.nav_more_collapsed : R.string.nav_more_expanded
                );
                mAvatarExpanded = !mAvatarExpanded;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            navigationItemClicked(R.id.nav_home);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onNavigationItemClicked(View view) {
        navigationItemClicked(view.getId());
    }

    private void navigationItemClicked(final int id) {

        // pop the back stack if we have any fragments on there
        getSupportFragmentManager().popBackStack();

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

        Fragment fragment = null;
        int[] galleryImages = null;
        switch (id) {
            case R.id.nav_projects:
                final int newVisibility = View.VISIBLE == mFamilyNav.getVisibility() ? View.GONE : View.VISIBLE;
                mFamilyNav.setVisibility(newVisibility);
                mWeddingNav.setVisibility(newVisibility);
                mChildrenNav.setVisibility(newVisibility);
                mBabiesNav.setVisibility(newVisibility);
                mProjects.setCompoundDrawablesWithIntrinsicBounds(0, 0, newVisibility == View.VISIBLE ? mCollapseProjectsIconResId : mExpandProjectsIconResId, 0);
                break;
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_family:
                galleryImages = GALLERY_FRAGMENT_IMAGES[PAGE_FAMILY];
                break;
            case R.id.nav_weddings:
                galleryImages = GALLERY_FRAGMENT_IMAGES[PAGE_WEDDING];
                break;
            case R.id.nav_children:
                galleryImages = GALLERY_FRAGMENT_IMAGES[PAGE_CHILDREN];
                break;
            case R.id.nav_babies:
                galleryImages = GALLERY_FRAGMENT_IMAGES[PAGE_BABIES];
                break;
            case R.id.nav_contact:
                fragment = new ContactFragment();
                transaction.addToBackStack(ContactFragment.class.getName());
                break;
        }

        if (galleryImages != null) {
            fragment = GalleryFragment.newInstance(galleryImages);
            transaction.addToBackStack(GalleryFragment.class.getName());
        }

        if (fragment != null) {
            transaction.replace(R.id.container, fragment).commit();
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onBackPressed() {

        // if there's a fragment on the back stack (gallery or contact) pop it off and go home
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            navigationItemClicked(R.id.nav_home);
            getSupportFragmentManager().popBackStack();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onFamilySelected() {
        navigationItemClicked(R.id.nav_family);
    }

    @Override
    public void onWeddingSelected() {
        navigationItemClicked(R.id.nav_weddings);
    }

    @Override
    public void onChildrenSelected() {
        navigationItemClicked(R.id.nav_children);
    }

    @Override
    public void onBabiesSelected() {
        navigationItemClicked(R.id.nav_babies);
    }
}
