package com.example.administrator.computernetwork.About;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.example.administrator.computernetwork.R;
import com.example.administrator.computernetwork.base.BaseFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutFragment extends BaseFragment {

    private static final String TAG = "AboutFragment";
    @BindView(R.id.icon_cuit)
    ImageView MyIcon;
    @BindView(R.id.user_name)
    TextView UserName;
    @BindView(R.id.user_major)
    TextView UserMajor;
    @BindView(R.id.login)
    ImageView Login;

    @BindView(R.id.user_join_group_count)
    TextView UserJoinGroupCount;
    @BindView(R.id.logout_button)
    Button LogoutButton;

    public static AboutFragment getInstance() {
        return new AboutFragment();
    }
    @Override
    public void onStart() {
        Log.i(TAG, "onStart: success");
        current_user = AVUser.getCurrentUser();
        if (current_user != null){

            UserName.setText(current_user.getUsername().toString());
            UserMajor.setText(current_user.getString("school")+" "+current_user.get("major"));

            final int[] create = {0};
            final int[] join = {0};
            AVQuery<AVUser> avUserAVQuery = new AVQuery<>("_User");
            avUserAVQuery.getInBackground(current_user.getObjectId(), new GetCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if (e == null){
                        List<String> list_create = avUser.getList("create_group");
                        List<String> list_join = avUser.getList("join_group");
                        if (list_create != null){
                            create[0] = list_create.size();
                            Log.i(TAG, "done: "+list_create.size()+" "+create[0]);
                        }
                        if (list_join != null){
                            join[0] = list_join.size();
                            Log.i(TAG, "done: "+list_join.size()+" "+join[0]);
                        }
                        UserJoinGroupCount.setText(create[0] + join[0] + "");
                    }
                }
            });
        }else {
            setNull();
        }
        super.onStart();
    }

    @Override
    protected void logic() {

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: onresume");
    }

    private void setNull() {
        UserName.setText("请点击右侧箭头登录");
        UserMajor.setText("");

        UserJoinGroupCount.setText("");
    }
    @Override
    protected void init(View mView, Bundle mSavedInstanceState) {}

    @Override
    protected int getResourcesLayout() {
        return R.layout.fragment_aboutme;
    }


    @OnClick(R.id.icon_cuit)
    public void onMyIconClicked() {
    }

    @OnClick(R.id.login)
    public void onLoginClicked() {
        Intent intent = new Intent(getContext(),LoginActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.logout_button)
    public void onLogoutButtonClicked() {
        if (current_user != null){
            AVUser.logOut();// 清除缓存用户对象
            current_user = null;
            Log.i(TAG, "onLogoutBtnClicked: "+AVUser.getCurrentUser());
        }
        setNull();
    }
}
