package com.example.administrator.computernetwork.Group;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.administrator.computernetwork.Group.inner.GroupActivity;
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

public class GroupFragment extends BaseFragment implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    private GroupFragmentAdapter groupFragmentAdapter;
    private LinearLayoutManager linearLayoutManager;
    @BindView(R.id.group_fragment_recycler)
    RecyclerView groupFragmentRecycler;
    @BindView(R.id.fragment_group_rfab)
    RapidFloatingActionButton FragmentGroupRfab;
    @BindView(R.id.fragment_group_rfal)
    RapidFloatingActionLayout FragmentGroupRfal;
    private RapidFloatingActionHelper rfabHelper;
    private List list_create = new ArrayList<>();
    private List list_join = new ArrayList<>();
    private List<String>list_all = new ArrayList<>();
    private static final String TAG = "GroupFragment";

    public static GroupFragment getInstance() {
        return new GroupFragment();
    }

    @Override
    protected void logic() {
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(getContext());
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("创建小组")
                .setResId(R.drawable.ic_add_black_24dp)
                .setIconNormalColor(0xffd84315)
                .setIconPressedColor(0xffbf360c)
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("加入小组")
                .setResId(R.drawable.ic_add_black_24dp)
                .setIconNormalColor(0xffd84315)
                .setIconPressedColor(0xffbf360c)
                .setWrapper(1)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(5)
                .setIconShadowColor(0xff999999)
                .setIconShadowDy(5)
        ;
        rfabHelper = new RapidFloatingActionHelper(
                getContext(),
                FragmentGroupRfal,
                FragmentGroupRfab,
                rfaContent
        ).build();
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
    }

    //从网络上获取数据更新数据
    private void updateData() {
        if (list_all.size()!= 0){
            list_all.clear();
        }
        if (current_user != null){
            AVQuery<AVUser>avUserAVQuery = new AVQuery<>("_User");
            avUserAVQuery.getInBackground(current_user.getObjectId(), new GetCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if (e == null){
                        Log.i(TAG, "done: "+avUser.getUsername()+avUser.get("create_group"));
                        list_create = avUser.getList("create_group");
                        list_join = avUser.getList("join_group");
                        if (list_create != null){
                            
                            list_all.addAll(list_create);
                            Log.i(TAG, "done: "+list_create.size());
                        }
                        if (list_join != null){
                            for (int i = list_join.size() - 1; i >= 0; i--) {
                                AVQuery<AVObject> query = new AVQuery<>("GroupBean");
                                query.whereEqualTo("group_random_number",list_join.get(i));
                                final int finalI = i;
                                query.getFirstInBackground(new GetCallback<AVObject>() {
                                    @Override
                                    public void done(final AVObject avObject, AVException e) {
                                        if (e == null){
                                            if (avObject != null){
                                                if (avObject.getBoolean("isend") == true){//已经结束的
                                                    list_join.remove(finalI);
                                                    Log.i(TAG, "done: this group has end ");
                                                }else {
                                                }
                                            }else {
                                            }
                                        }
                                    }
                                });
                                avUser.put("join_group",list_join);
                                avUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null){
                                            Log.i(TAG, "done: 删除已解散的小组去除"+list_join.size());
                                            list_all.addAll(list_join);
                                            linearLayoutManager = new LinearLayoutManager(getActivity());
                                            groupFragmentRecycler.setLayoutManager(linearLayoutManager);
                                            groupFragmentAdapter = new GroupFragmentAdapter(getContext(),
                                                    new OnClickerListener() {
                                                        @Override
                                                        public void click(int position, View view) {
                                                            Intent intent = new Intent(getContext(),GroupActivity.class);
                                                            Bundle bundle = new Bundle();
                                                            bundle.putString("group_random_number",list_all.get(position));
                                                            intent.putExtras(bundle);
                                                            startActivity(intent);
                                                        }
                                                    },list_all);
                                            Log.i(TAG, "updateData: "+list_all.size());
                                            groupFragmentRecycler.setAdapter(groupFragmentAdapter);
                                            groupFragmentAdapter.notifyDataSetChanged();
                                        }else {
                                            Log.e(TAG, "done: "+e.getMessage() );
                                        }


                                    }
                                });
                            }
                        }
                        linearLayoutManager = new LinearLayoutManager(getActivity());
                        groupFragmentRecycler.setLayoutManager(linearLayoutManager);
                        groupFragmentAdapter = new GroupFragmentAdapter(getContext(),
                                new OnClickerListener() {
                                    @Override
                                    public void click(int position, View view) {
                                        Intent intent = new Intent(getContext(),GroupActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("group_random_number",list_all.get(position));
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                },list_all);
                        Log.i(TAG, "updateData: "+list_all.size());
                        groupFragmentRecycler.setAdapter(groupFragmentAdapter);
                        groupFragmentAdapter.notifyDataSetChanged();

                    }
                }
            });

        }
    }

    @Override
    protected void init(View mView, Bundle mSavedInstanceState) {
    }
    @Override
    protected int getResourcesLayout() {
        return R.layout.fragment_group;
    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        startActivityByPosition(position);
    }
    private void startActivityByPosition(int position) {
        if (position == 0){
            Intent intent = new Intent(getContext(), CreateGroupActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getContext(), JoinGroupActivity.class);
            startActivity(intent);
        }
    }
    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        startActivityByPosition(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }
}
