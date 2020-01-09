package com.example.administrator.computernetwork.Group.inner.Resource;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ResourceOwnerAdapter extends RecyclerView.Adapter<ResourceOwnerAdapter.ViewHolder> {
    private Context context;
    private OnClickerListener onClickerListener;
    private List<String>resource_list = new ArrayList<>();
    public ResourceOwnerAdapter(Context context, OnClickerListener onClickerListener, List<String>list) {
        this.context = context;
        this.onClickerListener = onClickerListener;
        resource_list = list;
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
            final AVQuery<AVObject> resource_query = new AVQuery<>("Resource");
            resource_query.whereEqualTo("objectId",resource_list.get(position));
            resource_query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    if ( e == null){
                        if (avObject  != null){
                            holder.doItemTitle.setText(avObject.getString("type")+"/"+avObject.getString("title"));
                            holder.doItemJoinnum.setText(resource_list.size()+" 人参与");
                            holder.doItemType.setText(avObject.getString("type"));


                        }
                    }
                }
            });


    }

    @Override
    public int getItemCount() {
        return resource_list.size();
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
