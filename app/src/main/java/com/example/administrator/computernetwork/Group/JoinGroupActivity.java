package com.example.administrator.computernetwork.Group;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.administrator.computernetwork.R;
import com.example.administrator.computernetwork.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class JoinGroupActivity extends BaseActivity {

    private static final String TAG = "JoinGroupActivity";
    @BindView(R.id.GroupNumberEdit)
    EditText GroupNumberEdit;
    @BindView(R.id.JoinGroupButton)
    Button JoinGroupButton;

    @Override
    protected void logicActivity(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_join_group;
    }

    @OnClick(R.id.JoinGroupButton)
    public void onViewClicked() {
        final String group_random_number = GroupNumberEdit.getText().toString().trim();
        AVQuery<AVObject> query = new AVQuery<>("GroupBean");
        query.whereEqualTo("group_random_number",group_random_number);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null){
                    if (avObject != null){
                        if (avObject.get("group_owner_user").equals(current_user.getObjectId())){
                            Toast.makeText(activity, "您已经是该小组的管理员", Toast.LENGTH_SHORT).show();
                        }else {
                            final AVQuery<AVUser> avQuery_user = new AVQuery<>("_User");
                            avQuery_user.getInBackground(current_user.getObjectId(), new GetCallback<AVUser>() {
                                @Override
                                public void done(final AVUser avUser, AVException e) {

                                    List<String> list = avUser.getList("join_group");
                                    if (list == null){
                                        Log.i(TAG, "done: list is null");
                                        list = new ArrayList<>();
                                    }
                                    if (avObject.getBoolean("canjoin")){
                                        if (!list.contains(group_random_number)){
                                            list.add(group_random_number);
                                            avUser.put("join_group",list);
                                            avUser.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(AVException e) {
                                                    if (e == null){
                                                        Toast.makeText(activity, "加入成功,快去学习吧", Toast.LENGTH_SHORT).show();
                                                        activity.finish();
                                                        Log.i(TAG, "done: success update join_group");
                                                        List<String>user_list = avObject.getList("user_list");

                                                        Map<String,Integer> user_rank = (Map<String,Integer>) avObject.get("group_user_rank");
                                                        if (user_list == null){
                                                            user_list = new ArrayList<>();
                                                            user_rank = new HashMap<>();
                                                        }
                                                        Log.i(TAG, "done: "+user_list.size());
                                                        user_list.add(current_user.getObjectId());
                                                        user_rank.put(current_user.getObjectId(),0);
                                                        Log.i(TAG, "done: "+user_list.size());
                                                        avObject.put("user_list",user_list);
                                                        avObject.put("group_user_rank",user_rank);

                                                        avObject.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(AVException e) {
                                                                if ( e == null){
                                                                    Log.i(TAG, "done: success add to groupbean");
                                                                }else {
                                                                    Log.e(TAG, "done: "+e.getMessage() );
                                                                }
                                                            }

                                                        });
                                                    }
                                                }
                                            });

                                        }else {
                                            Toast.makeText(activity, "你已经加入了该小组", Toast.LENGTH_SHORT).show();
                                        }

                                    }else {
                                        Toast.makeText(JoinGroupActivity.this, "该小组现在不允许加入", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            
                            
                        }
                    }else {
                        Toast.makeText(activity, "小组不存在,请检查组号", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
