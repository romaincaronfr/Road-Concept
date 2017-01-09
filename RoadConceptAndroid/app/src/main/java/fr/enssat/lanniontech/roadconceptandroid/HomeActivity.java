package fr.enssat.lanniontech.roadconceptandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Map;
import fr.enssat.lanniontech.roadconceptandroid.HomeComponents.MapAdapter;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.OnNeedLoginListener;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RoadConceptMapInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends NavigationDrawerActivity implements OnNeedLoginListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int GET_MAP_LIST_REQUEST_CODE = 1500;

    //@BindView(R.id.swipeRefreshLayoutHome) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.itemsRecyclerViewHome) RecyclerView recyclerView;
    @BindView(R.id.swiperefreshHome) SwipeRefreshLayout refreshLayout;
    RoadConceptMapInterface roadConceptMapInterface;
    MapAdapter mapAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"Oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        roadConceptMapInterface = getRetrofitService(RoadConceptMapInterface.class);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        List<Map> test = new ArrayList<>();
        mapAdapter = new MapAdapter(test);
        recyclerView.setAdapter(mapAdapter);
        setTitle("Mes cartes");
        //refreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        refreshLayout.setOnRefreshListener(this);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            recyclerView.setLayoutManager(new GridLayoutManager(this,2));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getMapList(){
        refreshLayout.setRefreshing(true);
        Call<List<Map>> mapList = roadConceptMapInterface.getMapList();
        mapList.enqueue(new Callback<List<Map>>() {
            @Override
            public void onResponse(Call<List<Map>> call, Response<List<Map>> response) {
                if (response.isSuccessful()){
                    for (Map map:
                         response.body()) {
                        Log.d(TAG,map.toString());
                    }
                    mapAdapter.setMapList(response.body());
                    //mapAdapter.notifyDataSetChanged();
                    //recyclerView.swapAdapter(new MapAdapter(response.body()),false);
                } else {
                    if (response.code() == 401){
                        Log.d(TAG,"401,try");
                        refreshLogin(HomeActivity.this,GET_MAP_LIST_REQUEST_CODE);
                    } else {
                        displayNetworkErrorDialog();
                    }
                }
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Map>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                displayNetworkErrorDialog();
            }
        });
    }


    @Override
    public void onNeedLogin(int code, boolean result) {
        switch (code){
            case GET_MAP_LIST_REQUEST_CODE:
                Log.d(TAG,"onNeedLogin, GET_MAP_LIST_REQUEST_CODE");
                if (result) {
                    Log.d(TAG,"RESULT OK");
                    getMapList();
                } else {
                    Intent intent = new Intent(this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
        }
    }

    @Override
    public void onRefresh() {
        Log.d(TAG,"onRefresh");
        getMapList();
    }
}
