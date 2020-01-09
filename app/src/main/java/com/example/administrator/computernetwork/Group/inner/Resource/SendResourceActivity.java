package com.example.administrator.computernetwork.Group.inner.Resource;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SendResourceActivity extends BaseActivity {

    private static final String TAG = "SendResourceActivity";
    @BindView(R.id.resource_send_back_img)
    ImageView resourceSendBackImg;
    @BindView(R.id.resource_edit_show)
    EditText resourceEditShow;
    @BindView(R.id.resource_send_progressbar)
    ProgressBar resourceSendProgressbar;
    @BindView(R.id.resource_send_btn)
    Button resourceSendBtn;
    @BindView(R.id.resource_title)
    EditText resourceTitle;
    private String random_number;

    @Override
    protected void logicActivity(Bundle savedInstanceState) {
        random_number = getIntent().getExtras().getString("random_number");
        resourceSendBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });

    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_create_resource;
    }

    @OnClick(R.id.resource_send_btn)
    public void onViewClicked() {
        final AVObject resource = new AVObject("Resource");
        String description_edit = resourceEditShow.getText().toString();
        String title_edit = resourceTitle.getText().toString();
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(day));
        resource.put("time", df.format(day).toString());
        resource.put("description", description_edit);
        resource.put("owner", AVUser.getCurrentUser().getObjectId().toString());
        resource.put("type","学习资源");
        resource.put("title",title_edit);
        resource.put("group_random_number",random_number);
        AVQuery<AVObject> query = new AVQuery<>("GroupBean");
        query.whereEqualTo("group_random_number", random_number);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    resource.put("groupbean_id",avObject.getObjectId());
                    resource.saveInBackground(new SaveCallback() {
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
                                                List<String> resource_list = avObject.getList("resource_arr");
                                                if (resource_list == null) {
                                                    resource_list = new ArrayList<>();
                                                }
                                                resource_list.add(resource.getObjectId());
                                                avObject.put("resource_arr", resource_list);
                                                avObject.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(AVException e) {
                                                        if (e == null) {
                                                            Toast.makeText(SendResourceActivity.this, "资源发布成功", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(activity, "小组不存在", Toast.LENGTH_SHORT).show();
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


    }

}
