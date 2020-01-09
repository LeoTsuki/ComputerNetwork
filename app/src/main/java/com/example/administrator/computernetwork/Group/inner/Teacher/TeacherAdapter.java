package com.example.administrator.computernetwork.Group.inner.Teacher;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.example.administrator.computernetwork.R;
import com.example.administrator.computernetwork.base.OnClickerListener;
import com.google.protobuf.MapEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;



public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder> {
    private Context context;
    private OnClickerListener onClickerListener;
    private List<Map.Entry<String, Integer>> entries;
    private List<String>task_list = new ArrayList<>();
    public TeacherAdapter(Context context, OnClickerListener onClickerListener, List<String>list) {
        this.context = context;
        this.onClickerListener = onClickerListener;
        task_list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.do_recycler_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (onClickerListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickerListener.click(holder.getLayoutPosition(),holder.itemView);
                }
            });
        }
            final AVQuery<AVObject> task_query = new AVQuery<>("Task");
            task_query.whereEqualTo("objectId",task_list.get(position));
            task_query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    if ( e == null){
                        if (avObject  != null){

                            holder.doItemTitle.setText(avObject.getString("type")+"/"+avObject.getString("title"));
                            holder.doItemJoinnum.setText(task_list.size()+" 人参与");
                            holder.doItemType.setText(avObject.getString("type"));
                            holder.doItemScore.setText(avObject.getString("score")+" 分");

                        }
                    }
                }
            });


    }

    @Override
    public int getItemCount() {
        return task_list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.do_item_image)
        ImageView doItemImage;
        @BindView(R.id.do_item_joinnum)
        TextView doItemJoinnum;
        @BindView(R.id.do_item_title)
        TextView doItemTitle;
        @BindView(R.id.do_item_type)
        TextView doItemType;
        @BindView(R.id.do_item_score)
        TextView doItemScore;



        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
