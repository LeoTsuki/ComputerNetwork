package com.example.administrator.computernetwork.Group.inner.member;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.administrator.computernetwork.Group.inner.Teacher.GetTaskActivity;
import com.example.administrator.computernetwork.R;
import com.example.administrator.computernetwork.base.BaseFragment;
import com.example.administrator.computernetwork.base.OnClickerListener;
import com.example.administrator.computernetwork.utils.MapSortUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MemberFragment extends BaseFragment {

    private static final String TAG = "ScoreActivity";
    @BindView(R.id.appbar_title_text)
    TextView appbarTitleText;
    @BindView(R.id.rank)
    TextView rank;
    @BindView(R.id.score_text)
    TextView scoreText;
    @BindView(R.id.group_member_recycler)
    RecyclerView groupMemberRecycler;
    @BindView(R.id.sum_member_text)
    TextView sumMemberText;
    @BindView(R.id.back_from_member_image)
    ImageView backFromMemberImage;
    Unbinder unbinder;
    private MemberAdapter groupFragmentAdapter;
    private static String random_number;
    private Map<String, Integer> map;
    private List<Map.Entry<String, Integer>> entries;

    public static MemberFragment getInstance(String group_random_number) {
        random_number = group_random_number;
        return new MemberFragment();

    }

    @Override
    protected void logic() {

    }

    @Override
    protected void init(View mView, Bundle mSavedInstanceState) {
        Log.i(TAG, "init: " + random_number);
        backFromMemberImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }

    @Override
    protected int getResourcesLayout() {
        return R.layout.fragment_member_layout;
    }



    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    private void updateData() {

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
                                if (avUser.getUsername().equals(AVUser.getCurrentUser().getUsername())) {
                                    rank.setText("学生得分排行");
                                    scoreText.setText("");
                                    groupMemberRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                                    groupFragmentAdapter = new MemberAdapter(getContext(), new OnClickerListener() {
                                        @Override
                                        public void click(int position, View view) {

                                            startActivityTo(GetMemberActivity.class,entries.get(position).getKey(),entries.get(position).getValue());
                                        }
                                    }, entries);
                                    groupMemberRecycler.setAdapter(groupFragmentAdapter);
                                } else {

                                }
                            }

                        });
                        map = avObject.getMap("group_user_rank");
                        if (map == null) {
                            map = new HashMap<>();
                        }
                        entries = MapSortUtils.sort(map);
                        Log.i(TAG, "done: " + entries.size());


                        //绘制界面
                        appbarTitleText.setText(avObject.getString("group_name"));
                        if (avObject.getList("user_list") != null){
                            sumMemberText.setText(avObject.getList("user_list").size()-1+" 人");
                        }else {
                            sumMemberText.setText("0 人");
                        }
                        int index = 0;
                        for (Map.Entry<String, Integer> entry : entries) {
                            final AVObject score = new AVObject("Score");
                            score.put("owner", entry.getKey());
                            score.put("score", entry.getValue());
                            score.put("group_random_number",random_number);
                            AVQuery<AVObject> query = new AVQuery<>("GroupBean");
                            query.whereEqualTo("group_random_number", random_number);
                            query.getFirstInBackground(new GetCallback<AVObject>() {
                                @Override
                                public void done(final AVObject avObject, AVException e) {
                                    if (e == null) {
                                        score.put("groupbean_id",avObject.getObjectId());
                                        score.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                if (e == null) {
                                                    AVQuery<AVObject> query = new AVQuery<>("GroupBean");
                                                    query.whereEqualTo("group_random_number", random_number);
                                                    query.getFirstInBackground(new GetCallback<AVObject>() {
                                                        @Override
                                                        public void done(final AVObject avObject, AVException e) {
                                                            if (e == null) {
                                                                Log.i(TAG, "done: 获取小组信息成功");
                                                                if (avObject != null) {
                                                                    List<String> user_list = avObject.getList("user_list");
                                                                    if (user_list == null) {
                                                                        user_list = new ArrayList<>();
                                                                    }
                                                                    user_list.add(score.getObjectId());
                                                                    avObject.put("score_arr", user_list);
                                                                    avObject.saveInBackground(new SaveCallback() {
                                                                        @Override
                                                                        public void done(AVException e) {
                                                                            if (e == null) {
                                                                            }
                                                                        }
                                                                    });
                                                                } else {
                                                                    Toast.makeText(getActivity(), "小组不存在", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }

                                                        }
                                                    });

                                                }
                                            }
                                        });
                                    }

                                }
                            });
                            index ++;
                            if (entry.getKey().equals(AVUser.getCurrentUser().getObjectId())){
                                Log.i(TAG, "done: hi i find you");


                                int rank_index = index;//获取排名
                                int score_index = entry.getValue();//获取分数
                                rank.setText("第 "+rank_index+" 名");
                                scoreText.setText("获得 "+score_index+" 分");
                            }
                        }
                        index = 0;

                    } else {
                        Toast.makeText(getActivity(), "小组不存在", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
