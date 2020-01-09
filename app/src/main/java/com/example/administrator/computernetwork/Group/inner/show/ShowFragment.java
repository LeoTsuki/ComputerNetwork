package com.example.administrator.computernetwork.Group.inner.show;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.administrator.computernetwork.R;
import com.example.administrator.computernetwork.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class ShowFragment extends BaseFragment {


    private static final String TAG = "ShowFragment";
    @BindView(R.id.back_from_show_image)
    ImageView backFromShowImage;
    @BindView(R.id.show_title_text)
    TextView showTitleText;
    @BindView(R.id.show_image)
    ImageView showImage;
    @BindView(R.id.show_group_number_text)
    TextView showGroupNumberText;
    @BindView(R.id.show_name_text)
    TextView showNameText;
    @BindView(R.id.show_description_text)
    TextView showDescriptionText;
    @BindView(R.id.show_bankehao_text)
    TextView showBankehaoText;

    @BindView(R.id.checkbox)
    CheckBox checkbox;
    private static String random_number;
    @BindView(R.id.end_group_btn)
    Button endGroupBtn;

    public static ShowFragment getInstance(String group_random_number) {
        // Required empty public constructor
        random_number = group_random_number;
        return new ShowFragment();
    }


    @Override
    protected void logic() {

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
                                //判断管理者和当前用户是否为同一人
                                showBankehaoText.setText(avObject.getString("group_random_number"));
                                showDescriptionText.setText(avUser.getUsername() + " " + avUser.getString("school") + " " + avUser.getString("major"));
                                showNameText.setText(avObject.getString("group_name"));
                                showTitleText.setText(avObject.getString("group_name"));
                                checkbox.setChecked(avObject.getBoolean("canjoin"));
                                if (avUser.getUsername().equals(AVUser.getCurrentUser().getUsername())) {
                                    checkbox.setVisibility(View.VISIBLE);
                                    endGroupBtn.setText("解散小组");
                                } else {
                                    checkbox.setVisibility(View.VISIBLE);
                                    checkbox.setClickable(false);
                                    endGroupBtn.setText("退出小组");
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "小组不存在", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    setIsChecked(true);
                }else {
                    setIsChecked(false);
                }
            }
        });
    }

    private void setIsChecked(final boolean b) {
        AVQuery<AVObject> query = new AVQuery<>("GroupBean");
        query.whereEqualTo("group_random_number", random_number);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    Log.i(TAG, "done: 获取小组信息成功");
                    if (avObject != null) {
                        avObject.put("canjoin",b);
                        Log.i(TAG, "done: boolean = " + b);
                        avObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if ( e == null){
                                    Log.i(TAG, "done: success change check");
                                }else {
                                    Log.e(TAG, "done: "+e.getMessage() );
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "小组不存在", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    @Override
    protected void init(View mView, Bundle mSavedInstanceState) {

    }

    @Override
    protected int getResourcesLayout() {
        return R.layout.fragment_show;
    }


    @OnClick(R.id.back_from_show_image)
    public void onViewClicked() {
        getActivity().finish();
    }

    @OnClick(R.id.end_group_btn)
    public void onViewClicked2() {
        if (endGroupBtn.getText().equals("解散小组")) {
            // 执行 CQL 语句实现删除一个 Todo 对象
            //从自己创建列表中删除
            AVQuery<AVUser> avUserAVQuery = new AVQuery<>("_User");
            avUserAVQuery.getInBackground(AVUser.getCurrentUser().getObjectId(), new GetCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    List<String> list = avUser.getList("create_group");
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    if (list.contains(random_number)) {
                        list.remove(random_number);
                    }
                    avUser.put("create_group", list);
                    avUser.saveInBackground(new SaveCallback() {
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
                                                avObject.put("isend",true);
                                                avObject.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(AVException e) {
                                                        Toast.makeText(getContext(), "解散成功", Toast.LENGTH_SHORT).show();
                                                        getActivity().finish();
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(getActivity(), "小组不存在", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });

        } else {//退出
            AVQuery<AVObject> query = new AVQuery<>("GroupBean");
            query.whereEqualTo("group_random_number", random_number);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(final AVObject avObject, AVException e) {
                    if (e == null) {
                        List<String> list_user = (List<String>) avObject.get("user_list");
                        if (list_user != null) {
                            if (list_user.contains(AVUser.getCurrentUser().getObjectId())) {
                                list_user.remove(AVUser.getCurrentUser().getObjectId());
                            }
                        }
                        Map<String, Integer> map = avObject.getMap("group_user_rank");
                        if (map != null) {
                            if (map.containsKey(AVUser.getCurrentUser().getObjectId())) {
                                map.remove(AVUser.getCurrentUser().getObjectId());
                            }
                        }
                        avObject.put("user_list", list_user);
                        avObject.put("group_user_rank", map);
                        avObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                final AVQuery<AVUser> avQuery_user = new AVQuery<>("_User");
                                avQuery_user.getInBackground(current_user.getObjectId(), new GetCallback<AVUser>() {
                                    @Override
                                    public void done(final AVUser avUser, AVException e) {

                                        List<String> list = avUser.getList("join_group");
                                        if (list == null){
                                            Log.i(TAG, "done: list is null");
                                            list = new ArrayList<>();
                                        }
                                        if (list.contains(random_number)) {
                                            list.remove(random_number);
                                        }
                                        avUser.put("join_group", list);
                                        avUser.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                Toast.makeText(getActivity(), "您已退出该小组！", Toast.LENGTH_SHORT).show();
                                                getActivity().finish();
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                }
            });
        }
    }
}
