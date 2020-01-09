package com.example.administrator.computernetwork.Group.inner.Student;


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

public class StudentFragment extends BaseFragment implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    private static final String TAG = "ResourceFragment";
    @BindView(R.id.student_title)
    TextView StudentTitleText;
    @BindView(R.id.student_recycler_view)
    RecyclerView StudentRecyclerView;
    @BindView(R.id.student_Fbtn)
    RapidFloatingActionButton StudentFbtn;
    @BindView(R.id.student_Fbtn_layout)
    RapidFloatingActionLayout StudentFbtnLayout;
    private RapidFloatingActionHelper rfabHelper;
    private StudentAdapter adapter;
    private static String random_number;

    private List<String>discuss_list =  new ArrayList<>();

    public static StudentFragment getInstance(String group_random_number) {
        random_number = group_random_number;
        return new StudentFragment();
    }

    @Override
    protected void logic() {
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(getContext());
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("发布学习提问")
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
                StudentFbtnLayout,
                StudentFbtn,
                rfaContent
        ).build();
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);


    }

    @Override
    protected void init(View mView, Bundle mSavedInstanceState) {
    }

    @Override
    protected int getResourcesLayout() {
        return R.layout.fragment_student;
    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        startActivityByPosition(position);
    }

    private void startActivityByPosition(int position) {

        if (position == 0){
            startActivityTo(SendDiscussActivity.class,random_number);
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
                                if (avUser.getUsername().equals(AVUser.getCurrentUser().getUsername())) {
                                    StudentFbtn.setVisibility(View.GONE);
                                } else {
                                    StudentFbtn.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        discuss_list = avObject.getList("discuss_arr");
                        if (discuss_list == null){
                            discuss_list = new ArrayList<>();
                        }
                        StudentTitleText.setText(avObject.getString("group_name")+"学生交流");
                        StudentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        adapter = new StudentAdapter(getContext(), new OnClickerListener() {
                            @Override
                            public void click(int position, View view) {
                                startActivityTo(GetDiscussActivity.class,discuss_list.get(position));
                            }
                        },discuss_list);
                        StudentRecyclerView.setAdapter(adapter);


                    } else {
                        Toast.makeText(getContext(), "小组不存在", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



    }
}
