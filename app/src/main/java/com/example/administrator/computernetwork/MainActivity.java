package com.example.administrator.computernetwork;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.example.administrator.computernetwork.About.AboutFragment;
import com.example.administrator.computernetwork.Group.GroupFragment;
import com.example.administrator.computernetwork.base.BaseActivity;
import com.example.administrator.computernetwork.utils.ActivityUtils;
import com.example.administrator.computernetwork.utils.BottomNavigationViewHelper;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    @BindView(R.id.bottom_menu)
    BottomNavigationView bottomMenu;
    private static final String TAG = "MainActivity";

    @Override
    protected void logicActivity(Bundle savedInstanceState) {

        if (savedInstanceState != null){
            Log.i(TAG, "logicActivity: "+savedInstanceState.getInt("bottom_id"));
            showFragment(savedInstanceState.getInt("bottom_id"));
        }else {
            ActivityUtils.replaceFragmentToActivity(fragmentManager,GroupFragment.getInstance(),R.id.main_frame);
        }
        BottomNavigationViewHelper.disableShiftMode(bottomMenu);
        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                showFragment(item.getItemId());
                return true;
            }
        });
    }
    /**
     * 根据id显示相应的页面
     * @param menu_id
     */
    private void showFragment(int menu_id) {
        switch (menu_id){
            case R.id.bottom_menu_group:
                ActivityUtils.replaceFragmentToActivity(fragmentManager,GroupFragment.getInstance(),R.id.main_frame);
                break;
            case R.id.bottom_menu_about:
                ActivityUtils.replaceFragmentToActivity(fragmentManager, AboutFragment.getInstance(),R.id.main_frame);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("bottom_id",bottomMenu.getSelectedItemId());
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_main;
    }

}
