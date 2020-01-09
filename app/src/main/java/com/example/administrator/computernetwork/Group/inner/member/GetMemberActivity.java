package com.example.administrator.computernetwork.Group.inner.member;

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
import java.util.Map.Entry;
import butterknife.BindView;
import butterknife.OnClick;
public class GetMemberActivity extends BaseActivity {

    private static final String TAG = "GetMemberActivity";
    @BindView(R.id.get_back_image)
    ImageView GetBackImage;
    @BindView(R.id.get_score_avator)
    ImageView getscoreAvator;
    @BindView(R.id.score_get_name_text)
    TextView scoreGetNameText;
    @BindView(R.id.score_get_number_text)
    TextView scoreGetNumberText;
    @BindView(R.id.score_get_score)
    TextView scoreGetScore;
    @BindView(R.id.score_edit)
    EditText ScoreEdit;
    @BindView(R.id.score_reason_edit)
    EditText ScoreReasonEdit;
    @BindView(R.id.get_score_image)
    ImageView getScoreImage;
    @BindView(R.id.get_score_recycler)
    ListView getScoreRecycler;
    private String owner;
    private int user_score;


    @Override
    protected void logicActivity(Bundle savedInstanceState) {
        owner = getIntent().getExtras().getString("number");
        user_score = getIntent().getExtras().getInt("score");
        final AVQuery<AVObject> score_query = new AVQuery<>("Score");
        score_query.whereEqualTo("owner",owner);
        score_query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    if (avObject != null) {
                        final AVQuery<AVUser> avQuery = new AVQuery<>("_User");
                        avQuery.getInBackground(avObject.getString("owner"), new GetCallback<AVUser>() {
                            @Override
                            public void done(AVUser avUser, AVException e) {

                                if (e == null) {
                                    scoreGetNameText.setText(avUser.getUsername().toString());
                                    scoreGetNumberText.setText(avUser.get("school_number").toString());
                                    scoreGetScore.setText("当前分数:" + user_score + " 分");
                                    Map<String, String> map = (Map<String, String>) avObject.get("score_discuss");
                                    if (map == null) {
                                        map = new HashMap<>();
                                    }
                                    final List<HashMap<String, String>> mapList = new ArrayList<>();
                                    for (Map.Entry<String, String> entry : map.entrySet()) {
                                        HashMap<String, String> map1 = new HashMap<>();
                                        map1.put("text1", entry.getKey());
                                        map1.put("text2", entry.getValue());
                                        mapList.add(map1);
                                    }

                                    avUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            SimpleAdapter adapter = new SimpleAdapter(activity, mapList, android.R.layout.simple_list_item_2,
                                                    new String[]{"text1", "text2"}, new int[]{android.R.id.text1, android.R.id.text2});
                                            getScoreRecycler.setAdapter(adapter);
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
        return R.layout.activity_score;
    }

    @OnClick(R.id.get_back_image)
    public void GetBackImageClicked() {
        activity.finish();
    }

    @OnClick(R.id.get_score_image)
    public void onGetScoreImageClicked() {
        if (!ScoreEdit.getText().toString().equals("")) {
            final AVQuery<AVObject> scoreGet = new AVQuery<>("Score");
            scoreGet.whereEqualTo("owner",owner);
            scoreGet.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(final AVObject avObject, AVException e) {
                    if (e == null) {
                        if (avObject != null) {
                            Map<String, String> map = (Map<String, String>) avObject.get("score_discuss");
                            if (map == null) {
                                map = new HashMap<>();
                            }
                            Date day = new Date();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            System.out.println(df.format(day));
                            String dis = ScoreReasonEdit.getText().toString();
                            final String addscore = ScoreEdit.getText().toString();

                            ScoreEdit.setText("");
                            ScoreReasonEdit.setText("");
                            map.put(AVUser.getCurrentUser().getUsername() +"       时间:"+" "+ df.format(day)+"       分值: "+addscore,dis);
                            final List<HashMap<String, String>> mapList = new ArrayList<>();
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                HashMap<String, String> map1 = new HashMap<>();
                                map1.put("text1", entry.getKey());
                                map1.put("text2", entry.getValue());
                                mapList.add(map1);
                            }
                            avObject.put("score_discuss", map);
                            avObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        ScoreEdit.setText("");
                                        SimpleAdapter adapter = new SimpleAdapter(activity, mapList, android.R.layout.simple_list_item_2,
                                                new String[]{"text1", "text2"}, new int[]{android.R.id.text1, android.R.id.text2});
                                        getScoreRecycler.setAdapter(adapter);

                                       Toast.makeText(GetMemberActivity.this, "加分成功", Toast.LENGTH_SHORT).show();
                                        AVQuery<AVObject>avQuery = new AVQuery<>("GroupBean");

                                        avQuery.getInBackground(avObject.getString("groupbean_id"), new GetCallback<AVObject>() {
                                            @Override
                                            public void done(AVObject avObject2, AVException e) {
                                                if (e == null ){

                                                    Map<String,Integer>map1 = (Map<String, Integer>) avObject2.get("group_user_rank");
                                                    Log.i(TAG, "done: "+map1.size());
                                                    if (map1.get(owner) != null){

                                                        int score = map1.get(owner);

                                                        final int addscorei = new Integer((String) addscore);
                                                        score += addscorei;
                                                        map1.put(owner,score);

                                                        avObject2.put("group_user_rank",map1);
                                                        AVQuery<AVObject> ScoreAVQuery = new AVQuery<>("Score");
                                                        ScoreAVQuery.whereEqualTo("owner",owner);
                                                        ScoreAVQuery.getFirstInBackground(new GetCallback<AVObject>() {
                                                            @Override
                                                            public void done(AVObject ScoreAVQuery, AVException e) {
                                                                if (e == null){
                                                                    final int addscorei2 = new Integer((String) addscore);
                                                                            int score=ScoreAVQuery.getInt("score");
                                                                        ScoreAVQuery.put("score",score+addscorei2);
                                                                    ScoreAVQuery.saveInBackground(new SaveCallback() {
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
                                        Toast.makeText(GetMemberActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
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
