package com.community.mua.ui.diary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.community.mua.R;
import com.community.mua.bean.GvFeelBean;
import com.community.mua.utils.SoundUtil;

import java.util.List;

public class GvFeelAdapter extends BaseAdapter {

    private List<GvFeelBean> mFeelBeans;

    private LayoutInflater inflater;
    private OnBtnClickListener mBtnClickListener;

    public GvFeelAdapter(List<GvFeelBean> feelBeans, Context context) {
        mFeelBeans = feelBeans;
        this.inflater = LayoutInflater.from(context);;
    }

    @Override
    public int getCount() {
        return mFeelBeans.size();
    }

    @Override
    public GvFeelBean getItem(int position) {
        return mFeelBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setOnBtnClickListener(OnBtnClickListener listenser) {
        mBtnClickListener = listenser;
    }
    public interface OnBtnClickListener {
        void onBtnClick(int position,View v);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.gv_feel_item, null);
        ViewHolder viewHolder = new ViewHolder();

        GvFeelBean feelBean = getItem(position);
        viewHolder.name = convertView.findViewById(R.id.tv_feel);
        viewHolder.name.setText(feelBean.getName());
        viewHolder.feel = convertView.findViewById(R.id.iv_feel);
        viewHolder.feel.setImageResource(feelBean.getDesId());
        viewHolder.feel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                mBtnClickListener.onBtnClick(position,v);
            }
        });
        if(feelBean.isSelect()){
            viewHolder.feel.setBackgroundResource(R.drawable.feel_bg_selecter);
        } else {
            viewHolder.feel.setBackgroundResource(R.drawable.feel_bg_default);
        }

        return convertView;
    }

    class ViewHolder{
        TextView name;
        ImageView feel;
    }
}
