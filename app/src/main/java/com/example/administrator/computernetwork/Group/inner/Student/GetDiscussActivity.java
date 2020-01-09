package com.example.administrator.computernetwork.Group.inner.Student;

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

public class GetDiscussActivity extends BaseActivity {

    private static final String TAG = "GetResourceActivity";
    @BindView(R.id.get_back_image)
    ImageView GetBackImage;
    @BindView(R.id.get_discuss_avator)
    ImageView getDiscussAvator;
    @BindView(R.id.discuss_get_name_text)
    TextView discussGetNameText;
    @BindView(R.id.discuss_get_time_text)
    TextView discussGetTimeText;

    @BindView(R.id.get_discuss_text)
    TextView getDiscussText;
    @BindView(R.id.get_discuss_edit)
    EditText getDiscussEdit;
    @BindView(R.id.get_discuss_image)
    ImageView getDiscussImage;
    @BindView(R.id.get_discuss_recycler)
    ListView getDiscussRecycler;
    private String discuss_id;

    @Override
    protected void logicActivity(Bundle savedInstanceState) {
        discuss_id = getIntent().getExtras().getString("random_number");
        final AVQuery<AVObject> discussGet = new AVQuery<>("Discuss");
        discussGet.getInBackground(discuss_id, new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    if (avObject != null) {
                        final AVQuery<AVUser> avQuery = new AVQuery<>("_User");
                        avQuery.getInBackground(avObject.getString("owner"), new GetCallback<AVUser>() {
                            @Override
                            public void done(AVUser avUser, AVException e) {
                                if (e == null) {
                                    discussGetNameText.setText(avUser.getUsername().toString());
                                    discussGetTimeText.setText(avObject.getString("time").toString());
                                    getDiscussText.setText(avObject.getString("description").toString());
                                    Map<String,String>map = (Map<String, String>) avObject.get("discuss_discuss");
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
                                            getDiscussRecycler.setAdapter(adapter);
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
        return R.layout.activity_discuss;
    }

    @OnClick(R.id.get_back_image)
    public void onGetBackImageClicked() {
        activity.finish();
    }

    @OnClick(R.id.get_discuss_image)
    public void onGetDiscussImageClicked() {
        if (!getDiscussEdit.getText().toString().equals("")) {
            final AVQuery<AVObject> discussGet = new AVQuery<>("Discuss");
            discussGet.getInBackground(discuss_id, new GetCallback<AVObject>() {
                @Override
                public void done(final AVObject avObject, AVException e) {
                    if (e == null) {
                        if (avObject != null) {
                            Map<String, String> map = (Map<String, String>) avObject.get("discuss_discuss");
                            if (map == null) {
                                map = new HashMap<>();
                            }
                            Date day = new Date();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            System.out.println(df.format(day));
                            String dis = getDiscussEdit.getText().toString();
                            getDiscussEdit.setText("");
                            map.put(AVUser.getCurrentUser().getUsername() + " " + current_user.get("school_number") +"       时间: "+ df.format(day),dis);
                            final List<HashMap<String, String>> mapList = new ArrayList<>();
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                HashMap<String, String> map1 = new HashMap<>();
                                map1.put("text1", entry.getKey());
                                map1.put("text2", entry.getValue());
                                mapList.add(map1);
                            }
                            avObject.put("discuss_discuss", map);
                            avObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        Log.i(TAG, "not done:");
                                        getDiscussEdit.setText("");
                                        SimpleAdapter adapter = new SimpleAdapter(activity, mapList, android.R.layout.simple_list_item_2,
                                                new String[]{"text1", "text2"}, new int[]{android.R.id.text1, android.R.id.text2});
                                        getDiscussRecycler.setAdapter(adapter);
                                        Toast.makeText(GetDiscussActivity.this, "回答成功", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(GetDiscussActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
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
