package org.mattkranzler.example.portfolio.drawer.styled.activities;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import org.mattkranzler.example.portfolio.drawer.styled.R;

public class ZoomImageViewActivity extends Activity {

    public static final String EXTRA_IMAGE_RESOURCE_ID = "image_resource_id";

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image_view);
        mImageView = (ImageView) findViewById(R.id.zoom_image_view);
        mImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), getIntent().getIntExtra(EXTRA_IMAGE_RESOURCE_ID, 0)));

        // hide the status bar
        mImageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }
}
