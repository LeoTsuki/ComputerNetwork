package com.example.administrator.computernetwork.Group.inner.Student;

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

public class SendDiscussActivity extends BaseActivity {

    private static final String TAG = "SendResourceActivity";
    @BindView(R.id.discuss_send_back_img)
    ImageView discussSendBackImg;
    @BindView(R.id.discuss_edit_show)
    EditText discussEditShow;
    @BindView(R.id.discuss_send_progressbar)
    ProgressBar discussSendProgressbar;
    @BindView(R.id.discuss_send_btn)
    Button discussSendBtn;
    @BindView(R.id.discuss_title)
    EditText discussTitle;
    private String random_number;

    @Override
    protected void logicActivity(Bundle savedInstanceState) {
        random_number = getIntent().getExtras().getString("random_number");
        discussSendBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });

    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_create_discuss;
    }

    @OnClick(R.id.discuss_send_btn)
    public void onViewClicked() {
        final AVObject discuss = new AVObject("Discuss");
        String description_edit = discussEditShow.getText().toString();
        String title_edit = discussTitle.getText().toString();
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(day));
        discuss.put("time", df.format(day).toString());
        discuss.put("description", description_edit);
        discuss.put("owner", AVUser.getCurrentUser().getObjectId().toString());
        discuss.put("type","学生提问");
        discuss.put("title",title_edit);
        discuss.put("group_random_number",random_number);
        AVQuery<AVObject> query = new AVQuery<>("GroupBean");
        query.whereEqualTo("group_random_number", random_number);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    discuss.put("groupbean_id",avObject.getObjectId());
                    discuss.saveInBackground(new SaveCallback() {
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
                                                List<String> discuss_list = avObject.getList("discuss_arr");
                                                if (discuss_list == null) {
                                                    discuss_list = new ArrayList<>();
                                                }
                                                discuss_list.add(discuss.getObjectId());
                                                avObject.put("discuss_arr", discuss_list);
                                                avObject.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(AVException e) {
                                                        if (e == null) {
                                                            Toast.makeText(SendDiscussActivity.this, "提问发送成功", Toast.LENGTH_SHORT).show();
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
