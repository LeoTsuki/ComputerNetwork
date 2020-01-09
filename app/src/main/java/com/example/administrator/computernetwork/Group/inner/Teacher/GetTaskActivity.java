package com.example.administrator.computernetwork.Group.inner.Teacher;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.administrator.computernetwork.R;
import com.example.administrator.computernetwork.base.BaseActivity;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class GetTaskActivity extends BaseActivity {

    private static final String TAG = "GetTaskActivity";
    @BindView(R.id.get_back_image)
    ImageView GetBackImage;
    @BindView(R.id.get_task_avator)
    ImageView gettaskAvator;
    @BindView(R.id.task_get_name_text)
    TextView taskGetNameText;
    @BindView(R.id.task_get_time_text)
    TextView taskGetTimeText;
    @BindView(R.id.task_get_score)
    TextView taskGetScore;
    @BindView(R.id.get_task_text)
    TextView getTaskText;
    @BindView(R.id.get_task_edit)
    EditText getTaskEdit;
    @BindView(R.id.get_task_image)
    ImageView getTaskImage;
    @BindView(R.id.get_task_recycler)
    ListView getTaskRecycler;
    private String task_id;

    @Override
    protected void logicActivity(Bundle savedInstanceState) {
        task_id = getIntent().getExtras().getString("random_number");
        final AVQuery<AVObject> taskGet = new AVQuery<>("Task");
        taskGet.getInBackground(task_id, new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    if (avObject != null) {
                        final AVQuery<AVUser> avQuery = new AVQuery<>("_User");
                        avQuery.getInBackground(avObject.getString("owner"), new GetCallback<AVUser>() {
                            @Override
                            public void done(AVUser avUser, AVException e) {
                                if (e == null) {
                                    taskGetNameText.setText(avUser.getUsername().toString());
                                    taskGetTimeText.setText(avObject.getString("time").toString());
                                    taskGetScore.setText(avObject.getString("score").toString()+" 分");
                                    getTaskText.setText(avObject.getString("description").toString());
                                    Map<String,String>map = (Map<String, String>) avObject.get("task_discuss");
                                    if (map == null){
                                        map = new HashMap<>();
                                    }
                                    final List<HashMap<String,String>>mapList = new ArrayList<>();
                                    for (Map.Entry<String,String> entry: map.entrySet()){
                                        HashMap<String,String>map1 = new HashMap<>();
                                        map1.put("text1",entry.getKey());
                                        map1.put("text2",entry.getValue());
                                        mapList.add(map1);
                                    }

                                    avObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            SimpleAdapter adapter = new SimpleAdapter(activity, mapList,android.R.layout.simple_list_item_2,
                                                    new String[]{"text1","text2"},new int[]{android.R.id.text1,android.R.id.text2});
                                            getTaskRecycler.setAdapter(adapter);
                                        }
                                    });
                                } else {
                                    Log.i(TAG, "done: 查找失败");
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_task;
    }

    @OnClick(R.id.get_back_image)
    public void GetBackImageClicked() {
        activity.finish();
    }

    @OnClick(R.id.get_task_image)
    public void onGetTaskImageClicked() {
        if (!getTaskEdit.getText().toString().equals("")) {
            final AVQuery<AVObject> taskGet = new AVQuery<>("Task");
            taskGet.getInBackground(task_id, new GetCallback<AVObject>() {
                @Override
                public void done(final AVObject avObject, AVException e) {
                    if (e == null) {
                        if (avObject != null) {
                            Map<String, String> map = (Map<String, String>) avObject.get("task_discuss");
                            if (map == null) {
                                map = new HashMap<>();
                            }
                            Date day = new Date();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            System.out.println(df.format(day));
                            String dis = getTaskEdit.getText().toString();
                            getTaskEdit.setText("");
                            map.put(AVUser.getCurrentUser().getUsername() + " " + current_user.get("school_number") +"       时间: "+ df.format(day),dis);
                            final List<HashMap<String, String>> mapList = new ArrayList<>();
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                HashMap<String, String> map1 = new HashMap<>();
                                map1.put("text1", entry.getKey());
                                map1.put("text2", entry.getValue());
                                mapList.add(map1);
                            }
                            avObject.put("task_discuss", map);
                            avObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        Log.i(TAG, "not done:");
                                        getTaskEdit.setText("");
                                        SimpleAdapter adapter = new SimpleAdapter(activity, mapList, android.R.layout.simple_list_item_2,
                                                new String[]{"text1", "text2"}, new int[]{android.R.id.text1, android.R.id.text2});
                                        getTaskRecycler.setAdapter(adapter);
                                        Toast.makeText(GetTaskActivity.this, "回答成功", Toast.LENGTH_SHORT).show();
                                        AVQuery<AVObject>avQuery = new AVQuery<>("GroupBean");

                                        avQuery.getInBackground(avObject.getString("groupbean_id"), new GetCallback<AVObject>() {
                                            @Override
                                            public void done(AVObject avObject2, AVException e) {
                                                if (e == null ){

                                                    Map<String,Integer>map1 = (Map<String, Integer>) avObject2.get("group_user_rank");
                                                    Log.i(TAG, "done: "+map1.size());
                                                    if (map1.get(AVUser.getCurrentUser().getObjectId()) != null){

                                                        int rank = map1.get(AVUser.getCurrentUser().getObjectId());
                                                        final int rankthis = new Integer((String) avObject.get("score"));
                                                        rank += rankthis;
                                                        map1.put(AVUser.getCurrentUser().getObjectId(),rank);

                                                        avObject2.put("group_user_rank",map1);
                                                        AVQuery<AVUser> avUserAVQuery = new AVQuery<>("_User");
                                                        avUserAVQuery.getInBackground(current_user.getObjectId(), new GetCallback<AVUser>() {
                                                            @Override
                                                            public void done(AVUser avUser, AVException e) {
                                                                if (e == null){

                                                                    avUser.saveInBackground(new SaveCallback() {
                                                                        @Override
                                                                        public void done(AVException e) {
                                                                            Log.i(TAG, "done: all score update");
                                                                        }
                                                                    });
                                                                }else {
                                                                    Log.e(TAG, "done: failde" );
                                                                }
                                                            }
                                                        });
                                                        avObject2.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(AVException e) {
                                                                if (e == null){
                                                                    Log.i(TAG, "done: success update score");
                                                                }
                                                            }
                                                        });
                                                    }
                                                }else {
                                                    Log.e(TAG, "done: "+e.getMessage() );
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(GetTaskActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "done: " + e.getMessage());
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }else {
            Toast.makeText(activity, "评论不能为空", Toast.LENGTH_SHORT).show();
        }
    }
}
