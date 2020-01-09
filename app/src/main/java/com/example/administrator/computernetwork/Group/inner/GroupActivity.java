package com.example.administrator.computernetwork.Group.inner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.administrator.computernetwork.Group.inner.Resource.ResourceFragment;
import com.example.administrator.computernetwork.Group.inner.Student.StudentFragment;
import com.example.administrator.computernetwork.R;
import com.example.administrator.computernetwork.base.BaseActivity;
import com.example.administrator.computernetwork.Group.inner.Teacher.TeacherFragment;
import com.example.administrator.computernetwork.Group.inner.member.MemberFragment;
import com.example.administrator.computernetwork.Group.inner.show.ShowFragment;
import com.example.administrator.computernetwork.utils.ActivityUtils;
import com.example.administrator.computernetwork.utils.BottomNavigationViewHelper;

import butterknife.BindView;

public class GroupActivity extends BaseActivity {

    private static final String TAG = "GroupActivity";
    @BindView(R.id.group_frame)
    FrameLayout groupFrame;
    @BindView(R.id.group_bottom_menu)
    BottomNavigationView groupBottomMenu;
    private String group_random_number;


    @Override
    protected void logicActivity(Bundle savedInstanceState) {
        Intent intent =getIntent();
        group_random_number = intent.getExtras().getString("group_random_number");

        if (savedInstanceState != null){
            Log.i(TAG, "GroupActivity: "+savedInstanceState.getInt("bottom_id_group"));
            showFragment(savedInstanceState.getInt("bottom_id_group"));
        }else {
            ActivityUtils.replaceFragmentToActivity(fragmentManager, MemberFragment.getInstance(group_random_number),R.id.group_frame);
        }
        BottomNavigationViewHelper.disableShiftMode(groupBottomMenu);
        groupBottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
            case R.id.bottom_person:
                ActivityUtils.replaceFragmentToActivity(fragmentManager,MemberFragment.getInstance(group_random_number),R.id.group_frame);
                break;
            case R.id.bottom_resource:
                ActivityUtils.replaceFragmentToActivity(fragmentManager,ResourceFragment.getInstance(group_random_number),R.id.group_frame);
                break;
            case R.id.bottom_student:
                ActivityUtils.replaceFragmentToActivity(fragmentManager,StudentFragment.getInstance(group_random_number),R.id.group_frame);
                break;
            case R.id.bottom_teacher:
                ActivityUtils.replaceFragmentToActivity(fragmentManager, TeacherFragment.getInstance(group_random_number),R.id.group_frame);
                break;
            case R.id.bottom_show:
                ActivityUtils.replaceFragmentToActivity(fragmentManager, ShowFragment.getInstance(group_random_number),R.id.group_frame);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("bottom_id_group",groupBottomMenu.getSelectedItemId());
    }


    @Override
    protected int getLayoutView() {
        return R.layout.activity_group;
    }

}
