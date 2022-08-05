package com.community.mua.imkit.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.mua.R;
import com.community.mua.imkit.interfaces.OnItemClickListener;
import com.community.mua.imkit.modules.chat.EaseChatExtendMenu;
import com.community.mua.utils.SoundUtil;


public class EaseChatExtendMenuAdapter extends EaseBaseChatExtendMenuAdapter<EaseChatExtendMenuAdapter.ViewHolder, EaseChatExtendMenu.ChatMenuItemModel> {
    private OnItemClickListener itemListener;

    @Override
    protected int getItemLayoutId() {
        return R.layout.ease_chat_menu_item;
    }

    @Override
    protected ViewHolder easeCreateViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EaseChatExtendMenu.ChatMenuItemModel item = mData.get(position);
        holder.imageView.setBackgroundResource(item.image);
        holder.textView.setText(item.name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                if(item.clickListener != null){
                    item.clickListener.onChatExtendMenuItemClick(item.id, v);
                }
                if(itemListener != null) {
                    itemListener.onItemClick(v, position);
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            textView = (TextView) itemView.findViewById(R.id.text);
        }
    }
}

