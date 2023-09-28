package com.screens.activity.motion;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.screens.R;
import com.screens.adapter.AdapterGridMusicCardAlbums;
import com.screens.data.DataGenerator;
import com.screens.model.MusicAlbum;
import com.screens.utils.Tools;
import com.screens.widget.SpacingItemDecoration;

import java.util.List;

public class MotionCard extends AppCompatActivity {

    private View parent_view;
    private RecyclerView recyclerView;
    private AdapterGridMusicCardAlbums mAdapter;
    private boolean slow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

            // Set up shared element transition and disable overlay so views don't show above system bars
            setExitSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
            getWindow().setSharedElementsUseOverlay(false);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_card);
        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Album");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    private void initComponent() {
        parent_view = findViewById(R.id.parent_view);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 4), true));
        recyclerView.setHasFixedSize(true);

        List<MusicAlbum> items = DataGenerator.getMusicAlbum(this);

        //set data and list adapter
        mAdapter = new AdapterGridMusicCardAlbums(this, items);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterGridMusicCardAlbums.OnItemClickListener() {
            @Override
            public void onItemClick(View view, MusicAlbum obj, int position) {
                onItemGridClicked(view, obj);
            }
        });
    }

    private void onItemGridClicked(View sharedElement, MusicAlbum obj) {
        Intent intent = new Intent(this, MotionPageCardDetails.class);
        intent.putExtra("EXTRA_IMAGE", obj.image);
        intent.putExtra("EXTRA_NAME", obj.name);
        intent.putExtra("EXTRA_BRIEF", obj.brief);
        intent.putExtra("EXTRA_DURATION", slow ? 1200L : 400L);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(this, slow ? "Slow" : "Fast", Toast.LENGTH_SHORT).show();
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, sharedElement, "EXTRA_VIEW");
            startActivity(intent, options.toBundle());
        } else {
            ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedElement, "EXTRA_VIEW");
            // Now we can start the Activity, providing the activity options as a bundle
            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
        }

        slow = !slow;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_setting, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}