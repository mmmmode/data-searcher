package com.uestc.mode.modetest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends BaseAdapter{

    Context context;
    List<TitleBean> titleBeans;
    public MainAdapter(Context context, List<TitleBean> titleBeans){
        this.context = context;
        this.titleBeans = titleBeans;
    }

    int mode = 0;//显示模式

    private void setMode(int mode){
        this.mode = mode;
    }

    public void setTitleBeans(List<TitleBean> titleBeans){
        this.titleBeans = titleBeans;
    }
    @Override
    public int getCount() {
        return titleBeans.size();
    }

    @Override
    public Object getItem(int i) {
        return titleBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        final TitleBean titleBean = titleBeans.get(i);

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_object,null);
            holder = new ViewHolder();
            holder.titleTv = (TextView) view.findViewById(R.id.title_tv);
            holder.itemView =  view.findViewById(R.id.item_ll);
            holder.timeTv = view.findViewById(R.id.content_tv);
            holder.favorite = view.findViewById(R.id.favorite);
            holder.titleBean = titleBean;
            holder.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(holder.titleBean!=null){
                        holder.titleBean.setFavorite(!holder.titleBean.isFavorite());
                        notifyDataSetChanged();
                    }
                }
            });
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        if(mode == 0){
            holder.itemView.setBackgroundResource(R.drawable.shape_time_back);
        }else {
        }

        if(titleBean.isFavorite()){
            holder.favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.full_star));
        }else {
            holder.favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.empty_star));
        }
        holder.titleTv.setText(titleBean.getTitle());
        holder.timeTv.setText(titleBean.getSubContent());
        return view;
    }

    class ViewHolder{
        View itemView;
        TextView titleTv;
        TextView timeTv;
        ImageView favorite;
        TitleBean titleBean;
    }
}
