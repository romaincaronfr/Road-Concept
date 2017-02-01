package fr.enssat.lanniontech.roadconceptandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.enssat.lanniontech.roadconceptandroid.AbstractActivities.NavigationDrawerActivity;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Map;
import fr.enssat.lanniontech.roadconceptandroid.Components.MapAdapter;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.Constants;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.OnNeedLoginListener;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RecyclerViewClickListener;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RetrofitInterfaces.RoadConceptMapInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends NavigationDrawerActivity implements OnNeedLoginListener, SwipeRefreshLayout.OnRefreshListener, RecyclerViewClickListener {

    private static final int GET_MAP_LIST_REQUEST_CODE = 1500;
    public static final String INTENT_MAP_ID = "MAP_ID";
    public static final String INTENT_MAP_NAME = "MAP_NAME";
    public static final String INTENT_MAP_IMAGE = "MAP_IMAGE";
    public static final String INTENT_MAP_DESCRIPTION = "MAP_DESCRIPTION";

    //@BindView(R.id.swipeRefreshLayoutHome) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.itemsRecyclerViewHome) RecyclerView mRecyclerView;
    @BindView(R.id.swiperefreshHome) SwipeRefreshLayout mRefreshLayout;
    RoadConceptMapInterface roadConceptMapInterface;
    MapAdapter mMapAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        roadConceptMapInterface = getRetrofitService(RoadConceptMapInterface.class);
        mRecyclerView.setLayoutManager(getLayoutManager(false));
        List<Map> test = new ArrayList<>();
        mMapAdapter = new MapAdapter(test,this);
        mRecyclerView.setAdapter(mMapAdapter);
        setTitle("Mes cartes");
        mRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMapList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        if (!getDispositionPref()){
            menu.findItem(R.id.action_disposition).setIcon(R.drawable.ic_view_module_white);
        } else {
            menu.findItem(R.id.action_disposition).setIcon(R.drawable.ic_view_stream_white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_disposition) {
            GridLayoutManager gridLayoutManager = getLayoutManager(true);
            int size = gridLayoutManager.getSpanCount();
            mRecyclerView.setLayoutManager(gridLayoutManager);
            if (size == 1){
                item.setIcon(R.drawable.ic_view_module_white);
            } else {
                item.setIcon(R.drawable.ic_view_stream_white);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getMapList(){
        mRefreshLayout.setRefreshing(true);
        Call<List<Map>> mapList = roadConceptMapInterface.getMapList();
        mapList.enqueue(new Callback<List<Map>>() {
            @Override
            public void onResponse(Call<List<Map>> call, Response<List<Map>> response) {
                if (response.isSuccessful()){
                    for (Map map:
                         response.body()) {
                        Log.d(TAG,map.toString());
                    }
                    mMapAdapter.setmMapList(response.body());
                    //mMapAdapter.notifyDataSetChanged();
                    //mRecyclerViewSimulationOver.swapAdapter(new MapAdapter(response.body()),false);
                } else {
                    if (response.code() == 401){
                        refreshLogin(HomeActivity.this,GET_MAP_LIST_REQUEST_CODE);
                    } else {
                        displayNetworkErrorDialog();
                    }
                }
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Map>> call, Throwable t) {
                mRefreshLayout.setRefreshing(false);
                displayNetworkErrorDialog();
            }
        });
    }


    @Override
    public void onNeedLogin(int code, boolean result) {
        switch (code){
            case GET_MAP_LIST_REQUEST_CODE:
                if (result) {
                    getMapList();
                } else {
                    goToLogin();
                }
        }
    }

    @Override
    public void onRefresh() {
        getMapList();
    }

    private GridLayoutManager getLayoutManager(Boolean changePref){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARE_PREF_NAME,MODE_PRIVATE);
        Boolean dispo = sharedPreferences.getBoolean(Constants.SHARE_DISPOSITION, false);
        if (changePref){
            dispo = !dispo;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.SHARE_DISPOSITION,dispo);
            editor.apply();
        }
        int size;
        if (!dispo){
            size = 1;
        } else {
            size = 2;
        }
        return new GridLayoutManager(this,size);
    }

    private Boolean getDispositionPref(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARE_PREF_NAME,MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constants.SHARE_DISPOSITION, false);
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        //mMapAdapter.getmMapList().get(position);
        Intent intent = new Intent(this,MapSimulationListActivity.class);
        intent.putExtra(INTENT_MAP_NAME, mMapAdapter.getmMapList().get(position).getName());
        intent.putExtra(INTENT_MAP_ID, mMapAdapter.getmMapList().get(position).getId());
        intent.putExtra(INTENT_MAP_IMAGE,mMapAdapter.getmMapList().get(position).getImageURL());
        String mapDescription = "Aucune description";
        if (mMapAdapter.getmMapList().get(position).getDescription() != null && !Objects.equals(mMapAdapter.getmMapList().get(position).getDescription(), "")){
            mapDescription = mMapAdapter.getmMapList().get(position).getDescription();
        }
        intent.putExtra(INTENT_MAP_DESCRIPTION,mapDescription);
        startActivity(intent);
        //Toast.makeText(this, mMapAdapter.getmMapList().get(position).getName(),Toast.LENGTH_LONG).show();

    }
}
