package fr.enssat.lanniontech.roadconceptandroid;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.ImageFactory;

public class MapSimulationListActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.backgroundImageView) ImageView mImageView;
    @BindView(R.id.swipeMapSimulationList) SwipeRefreshLayout swipeRefreshLayout;
    private int mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_simulation_list);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(intent.getStringExtra(HomeActivity.INTENT_MAP_NAME));
        if (intent.getStringExtra(HomeActivity.INTENT_MAP_IMAGE) != null && !Objects.equals(intent.getStringExtra(HomeActivity.INTENT_MAP_IMAGE), "")){
            mImageView.setImageBitmap(ImageFactory.getBitmapWithBase64(intent.getStringExtra(HomeActivity.INTENT_MAP_IMAGE)));
        }
        mId = intent.getIntExtra(HomeActivity.INTENT_MAP_ID,-1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
