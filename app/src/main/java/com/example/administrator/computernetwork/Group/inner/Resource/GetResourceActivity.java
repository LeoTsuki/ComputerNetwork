package com.example.administrator.computernetwork.Group.inner.Resource;

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

public class GetResourceActivity extends BaseActivity {

    private static final String TAG = "GetResourceActivity";
    @BindView(R.id.get_back_image)
    ImageView GetBackImage;
    @BindView(R.id.get_resource_avator)
    ImageView getResourceAvator;
    @BindView(R.id.resource_get_name_text)
    TextView resourceGetNameText;
    @BindView(R.id.resource_get_time_text)
    TextView resourceGetTimeText;

    @BindView(R.id.get_resource_text)
    TextView getResourceText;
    @BindView(R.id.get_resource_edit)
    EditText getResourceEdit;
    @BindView(R.id.get_resource_image)
    ImageView getResourceImage;
    @BindView(R.id.get_resource_recycler)
    ListView getResourceRecycler;
    private String resource_id;

    @Override
    protected void logicActivity(Bundle savedInstanceState) {
        resource_id = getIntent().getExtras().getString("random_number");
        final AVQuery<AVObject> resourceGet = new AVQuery<>("Resource");
        resourceGet.getInBackground(resource_id, new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    if (avObject != null) {
                        final AVQuery<AVUser> avQuery = new AVQuery<>("_User");
                        avQuery.getInBackground(avObject.getString("owner"), new GetCallback<AVUser>() {
                            @Override
                            public void done(AVUser avUser, AVException e) {
                                if (e == null) {
                                    resourceGetNameText.setText(avUser.getUsername().toString());
                                    resourceGetTimeText.setText(avObject.getString("time").toString());
                                    getResourceText.setText(avObject.getString("description").toString());
                                    Map<String,String>map = (Map<String, String>) avObject.get("resource_discuss");
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
                                            getResourceRecycler.setAdapter(adapter);
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
        return R.layout.activity_resource;
    }

    @OnClick(R.id.get_back_image)
    public void onGetBackImageClicked() {
        activity.finish();
    }

    @OnClick(R.id.get_resource_image)
    public void onGetResourceImageClicked() {
        if (!getResourceEdit.getText().toString().equals("")) {
            final AVQuery<AVObject> resourceGet = new AVQuery<>("Resource");
            resourceGet.getInBackground(resource_id, new GetCallback<AVObject>() {
                @Override
                public void done(final AVObject avObject, AVException e) {
                    if (e == null) {
                        if (avObject != null) {
                            Map<String, String> map = (Map<String, String>) avObject.get("resource_discuss");
                            if (map == null) {
                                map = new HashMap<>();
                            }
                            Date day = new Date();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            System.out.println(df.format(day));
                            String dis = getResourceEdit.getText().toString();
                            getResourceEdit.setText("");
                            map.put(AVUser.getCurrentUser().getUsername() + " " + current_user.get("school_number") +"       时间: "+ df.format(day),dis);
                            final List<HashMap<String, String>> mapList = new ArrayList<>();
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                HashMap<String, String> map1 = new HashMap<>();
                                map1.put("text1", entry.getKey());
                                map1.put("text2", entry.getValue());
                                mapList.add(map1);
                            }
                            avObject.put("resource_discuss", map);
                            avObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        Log.i(TAG, "not done:");
                                        getResourceEdit.setText("");
                                        SimpleAdapter adapter = new SimpleAdapter(activity, mapList, android.R.layout.simple_list_item_2,
                                                new String[]{"text1", "text2"}, new int[]{android.R.id.text1, android.R.id.text2});
                                        getResourceRecycler.setAdapter(adapter);
                                        Toast.makeText(GetResourceActivity.this, "评论成功", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(GetResourceActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
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
