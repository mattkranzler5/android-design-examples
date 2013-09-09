package org.mattkranzler.example.portfolio.drawer.styled.fragments;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.mattkranzler.example.portfolio.drawer.styled.R;
import org.mattkranzler.example.portfolio.drawer.styled.activities.ZoomImageViewActivity;

public class GalleryFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String ARG_IMAGES = "images";

    private GridView mGridView;

    public static GalleryFragment newInstance(int[] images) {
        Bundle args = new Bundle();
        args.putIntArray(ARG_IMAGES, images);
        GalleryFragment fragment = new GalleryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mGridView = new GridView(getActivity());
        mGridView.setNumColumns(2);
        mGridView.setDrawSelectorOnTop(true);
        int[] imageArgs = getArguments().getIntArray(ARG_IMAGES);
        Integer[] images = new Integer[imageArgs.length];
        for (int i = 0; i < imageArgs.length; i++) {
            images[i] = imageArgs[i];
        }
        mGridView.setAdapter(new ImageAdapter(getActivity(), images));
        mGridView.setOnItemClickListener(this);
        return mGridView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bitmap b = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        view.draw(c);
        ActivityOptions options = ActivityOptions.makeThumbnailScaleUpAnimation(view, b, 0, 0);
        getActivity().startActivity(
                new Intent(getActivity(), ZoomImageViewActivity.class)
                        .putExtra(ZoomImageViewActivity.EXTRA_IMAGE_RESOURCE_ID, (int)id),
                options.toBundle()
        );
    }

    private class ImageAdapter extends ArrayAdapter<Integer> {

        private final LayoutInflater mLayoutInflater;

        public ImageAdapter(Context context, Integer[] imageResourceIds) {
            super(context, 0, imageResourceIds);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.gallery_item, null);
            }

            Picasso.with(parent.getContext())
                    .load(getItem(position))
                    .resizeDimen(R.dimen.gallery_image_height_width, R.dimen.gallery_image_height_width)
                    .centerCrop()
                    .into((ImageView) convertView);

            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return getItem(position);
        }
    }
}
