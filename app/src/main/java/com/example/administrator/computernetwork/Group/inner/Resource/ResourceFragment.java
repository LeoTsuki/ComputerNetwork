package com.example.administrator.computernetwork.Group.inner.Resource;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.example.administrator.computernetwork.R;
import com.example.administrator.computernetwork.base.BaseFragment;
import com.example.administrator.computernetwork.base.OnClickerListener;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ResourceFragment extends BaseFragment implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    private static final String TAG = "ResourceFragment";
    @BindView(R.id.resource_title)
    TextView ResourceTitleText;
    @BindView(R.id.resource_recycler_view)
    RecyclerView ResourceRecyclerView;
    @BindView(R.id.resource_Fbtn)
    RapidFloatingActionButton ResourceFbtn;
    @BindView(R.id.resource_Fbtn_layout)
    RapidFloatingActionLayout ResourceFbtnLayout;
    private RapidFloatingActionHelper rfabHelper;
    private ResourceOwnerAdapter adapter;
    private static String random_number;

    private List<String>resource_list =  new ArrayList<>();

    public static ResourceFragment getInstance(String group_random_number) {
        random_number = group_random_number;
        return new ResourceFragment();
    }

    @Override
    protected void logic() {
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(getContext());
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("发布学习资源")
                .setResId(R.drawable.ic_add_black_24dp)
                .setIconNormalColor(0xffd84315)
                .setIconPressedColor(0xffbf360c)
                .setWrapper(1)
        );

        rfaContent
                .setItems(items)
                .setIconShadowRadius(5)
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(5)
        ;
        rfabHelper = new RapidFloatingActionHelper(
                getContext(),
                ResourceFbtnLayout,
                ResourceFbtn,
                rfaContent
        ).build();
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);


    }

    @Override
    protected void init(View mView, Bundle mSavedInstanceState) {
    }

    @Override
    protected int getResourcesLayout() {
        return R.layout.fragment_resource;
    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        startActivityByPosition(position);
    }

    private void startActivityByPosition(int position) {

        if (position == 0){
            startActivityTo(SendResourceActivity.class,random_number);
        }

    }


    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        startActivityByPosition(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        AVQuery<AVObject> query = new AVQuery<>("GroupBean");
        query.whereEqualTo("group_random_number", random_number);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    Log.i(TAG, "done: 获取小组信息成功");
                    if (avObject != null) {

                        final AVQuery<AVUser> avQuery = new AVQuery<>("_User");
                        avQuery.getInBackground(avObject.getString("group_owner_user"), new GetCallback<AVUser>() {
                            @Override
                            public void done(AVUser avUser, AVException e) {
                                //判断管理者和当前用户是否为同一人
                                if (avUser.getUsername().equals(AVUser.getCurrentUser().getUsername())) {
                                    ResourceFbtn.setVisibility(View.VISIBLE);
                                } else {
                                    ResourceFbtn.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        resource_list = avObject.getList("resource_arr");
                        if (resource_list == null){
                            resource_list = new ArrayList<>();
                        }
                        ResourceTitleText.setText(avObject.getString("group_name")+"学习资源");
                        ResourceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        adapter = new ResourceOwnerAdapter(getContext(), new OnClickerListener() {
                            @Override
                            public void click(int position, View view) {
                                startActivityTo(GetResourceActivity.class,resource_list.get(position));
                            }
                        },resource_list);
                        ResourceRecyclerView.setAdapter(adapter);


                    } else {
                        Toast.makeText(getContext(), "小组不存在", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



    }
}
