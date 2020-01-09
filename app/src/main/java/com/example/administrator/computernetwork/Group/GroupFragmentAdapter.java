package com.example.administrator.computernetwork.Group;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.example.administrator.computernetwork.R;
import com.example.administrator.computernetwork.base.OnClickerListener;

import java.util.List;


public class GroupFragmentAdapter extends RecyclerView.Adapter<GroupFragmentAdapter.ViewHolder> {
    private Context context;
    private List<String> groupBeanList;
    private OnClickerListener onClickerListener;

    public GroupFragmentAdapter(Context context, OnClickerListener onClickerListener, List<String>groupBeanList) {
        this.context = context;
        this.onClickerListener = onClickerListener;
        this.groupBeanList = groupBeanList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_recycler_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        AVQuery<AVObject> query = new AVQuery<>("GroupBean");
        query.whereEqualTo("group_random_number",groupBeanList.get(position));
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null){
                    if (avObject != null){
                        final AVQuery<AVUser> avQuery = new AVQuery<>("_User");
                        avQuery.getInBackground(avObject.getString("group_owner_user"), new GetCallback<AVUser>() {
                            @Override
                            public void done(AVUser avUser, AVException e) {
                                holder.groupclass.setText(avObject.getString("group_class"));
                                holder.groupName.setText(avObject.getString("group_name"));
                                holder.groupOwner.setText("创建组的老师： "+avUser.getUsername());
                                if (avUser.getUsername().equals(AVUser.getCurrentUser().getUsername())){
                                    holder.groupRandomNumber.setText(avObject.getString("group_random_number"));
                                }
                            }
                        });

                    }else {
                        Toast.makeText(context, "小组不存在或者已经被删除", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        if (onClickerListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickerListener.click(holder.getLayoutPosition(),holder.itemView);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return groupBeanList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView groupImage;
        TextView groupRandomNumber;
        TextView groupclass;
        TextView groupName;
        TextView groupOwner;

        public ViewHolder(View itemView) {
            super(itemView);
            groupImage = itemView.findViewById(R.id.group_avator_item);
            groupRandomNumber = itemView.findViewById(R.id.group_random_number_item);
            groupclass = itemView.findViewById(R.id.group_class_item);
            groupName = itemView.findViewById(R.id.group_name_item);
            groupOwner = itemView.findViewById(R.id.group_owner_name);
        }

    }

}
