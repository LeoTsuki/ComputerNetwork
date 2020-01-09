package com.example.administrator.computernetwork.Group.inner.Teacher;

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

public class SendTaskActivity extends BaseActivity {

    private static final String TAG = "SendTaskActivity";
    @BindView(R.id.task_send_back_img)
    ImageView taskSendBackImg;
    @BindView(R.id.task_edit_show)
    EditText taskEditShow;
    @BindView(R.id.task_send_score_edit)
    EditText taskSendScoreEdit;
    @BindView(R.id.task_send_progressbar)
    ProgressBar taskSendProgressbar;
    @BindView(R.id.task_send_btn)
    Button taskSendBtn;
    @BindView(R.id.task_title)
    EditText taskTitle;
    private String random_number;

    @Override
    protected void logicActivity(Bundle savedInstanceState) {
        random_number = getIntent().getExtras().getString("random_number");
        taskSendBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });

    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_create_task;
    }

    @OnClick(R.id.task_send_btn)
    public void onViewClicked() {
        final AVObject task = new AVObject("Task");
        String description_edit = taskEditShow.getText().toString();
        String score_edit = taskSendScoreEdit.getText().toString();
        String title_edit = taskTitle.getText().toString();
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(day));
        task.put("time", df.format(day).toString());
        task.put("description", description_edit);
        task.put("score", score_edit);
        task.put("owner", AVUser.getCurrentUser().getObjectId().toString());
        task.put("type","学习任务");
        task.put("title",title_edit);
        task.put("group_random_number",random_number);
        AVQuery<AVObject> query = new AVQuery<>("GroupBean");
        query.whereEqualTo("group_random_number", random_number);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    task.put("groupbean_id",avObject.getObjectId());
                    task.saveInBackground(new SaveCallback() {
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
                                                List<String> task_list = avObject.getList("task_arr");
                                                if (task_list == null) {
                                                    task_list = new ArrayList<>();
                                                }
                                                task_list.add(task.getObjectId());
                                                avObject.put("task_arr", task_list);
                                                avObject.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(AVException e) {
                                                        if (e == null) {
                                                            Toast.makeText(SendTaskActivity.this, "任务发送成功", Toast.LENGTH_SHORT).show();
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
