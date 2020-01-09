package com.example.administrator.computernetwork.Group;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.administrator.computernetwork.R;
import com.example.administrator.computernetwork.base.BaseActivity;
import com.example.administrator.computernetwork.Group.inner.GroupActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateGroupActivity extends BaseActivity {

    private static final String TAG = "CreateGroupActivity";
    @BindView(R.id.create_image)
    ImageView createImage;
    @BindView(R.id.group_name_edit)
    EditText groupNameEdit;
    @BindView(R.id.group_major_edit)
    EditText groupMajorEdit;
    @BindView(R.id.create_group_btn)
    Button createGroupBtn;
    @BindView(R.id.group_longtime_edit)
    EditText groupLongtimeEdit;

    @Override
    protected void logicActivity(Bundle savedInstanceState) {
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_create_group;
    }

    //换图标
    @OnClick(R.id.create_image)
    public void onCreateImageClicked() {
    }

    //创建
    @OnClick(R.id.create_group_btn)
    public void onCreateGroupBtnClicked() {
        if (current_user != null){
            String group_class = groupMajorEdit.getText().toString().trim();
            String group_name = groupNameEdit.getText().toString().trim();
            final String group_number = (int)Math.rint((Math.random() * 9 + 1) * 10000)+"";
            String group_owner_user = current_user.getObjectId();
            AVObject computernetwork =new AVObject("GroupBean");
            computernetwork.put("group_class",group_class);
            computernetwork.put("group_name",group_name);
            computernetwork.put("group_owner_user",group_owner_user);
            computernetwork.put("group_random_number",group_number);
            computernetwork.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null){
                        Snackbar.make(findViewById(R.id.create_group_btn),"创建成功",Snackbar.LENGTH_LONG)
                                .setAction("进入小组", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(activity, GroupActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("group_random_number",group_number);
                                        intent.putExtras(bundle);
                                        startActivity(intent);

                                    }
                                }).show();
                        final AVQuery<AVUser> avQuery = new AVQuery<>("_User");
                        avQuery.getInBackground(current_user.getObjectId(), new GetCallback<AVUser>() {
                            @Override
                            public void done(AVUser avUser, AVException e) {
                                List<String>list = avUser.getList("create_group");
                                if (list == null){
                                    list = new ArrayList<>();
                                }
                                list.add(group_number);
                                avUser.put("create_group",list);
                                avUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null){
                                            Log.i(TAG, "done: success update");
                                        }
                                    }
                                });
                            }
                        });
                    }else {
                        Toast.makeText(activity, "创建失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(activity,"请先登录",Toast.LENGTH_SHORT).show();
        }

    }

}
