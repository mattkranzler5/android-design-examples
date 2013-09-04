package org.mattkranzler.example.portfolio.spinner.activities;


import android.app.ActionBar;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ArrayAdapter;

import org.mattkranzler.example.portfolio.spinner.R;
import org.mattkranzler.example.portfolio.spinner.fragments.AboutFragment;
import org.mattkranzler.example.portfolio.spinner.fragments.ContactFragment;
import org.mattkranzler.example.portfolio.spinner.fragments.GalleryFragment;
import org.mattkranzler.example.portfolio.spinner.fragments.HomeFragment;

public class HomeActivity extends FragmentActivity implements ActionBar.OnNavigationListener, HomeFragment.HomeFragmentCallbacks {

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
            R.drawable.wedding_1,
            R.drawable.wedding_2,
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
            null,
            FAMILY_IMAGES,
            WEDDING_IMAGES,
            CHILDREN_IMAGES,
            BABY_IMAGES
    };

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setDisplayShowHomeEnabled(false);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[] {
                                getString(R.string.title_section1),
                                getString(R.string.title_section2),
                                getString(R.string.title_section3),
                                getString(R.string.title_section4),
                                getString(R.string.title_section5),
                                getString(R.string.title_section6),
                        }),
                this);

    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onNavigationItemSelected(final int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        getSupportFragmentManager().popBackStack();

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

        Fragment fragment = null;
        switch (position) {
            case PAGE_HOME:
                fragment = new HomeFragment();
                break;
            case PAGE_FAMILY:
            case PAGE_WEDDING:
            case PAGE_CHILDREN:
            case PAGE_BABIES:
                fragment = GalleryFragment.newInstance(GALLERY_FRAGMENT_IMAGES[position]);
                transaction.addToBackStack(GalleryFragment.class.getName());
                break;
            case PAGE_CONTACT:
                fragment = new ContactFragment();
                transaction.addToBackStack(ContactFragment.class.getName());
                break;
        }

        transaction.replace(R.id.container, fragment).commit();

        return true;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getActionBar().setSelectedNavigationItem(PAGE_HOME);
        }

        super.onBackPressed();
    }

    @Override
    public void onFamilySelected() {
        getActionBar().setSelectedNavigationItem(PAGE_FAMILY);
    }

    @Override
    public void onWeddingSelected() {
        getActionBar().setSelectedNavigationItem(PAGE_WEDDING);
    }

    @Override
    public void onChildrenSelected() {
        getActionBar().setSelectedNavigationItem(PAGE_CHILDREN);
    }

    @Override
    public void onBabiesSelected() {
        getActionBar().setSelectedNavigationItem(PAGE_BABIES);
    }
}
