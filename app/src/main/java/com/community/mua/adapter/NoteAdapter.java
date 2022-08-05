package com.community.mua.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.mua.R;
import com.community.mua.bean.NoteBean;
import com.community.mua.bean.UserBean;
import com.community.mua.utils.DateUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.UserUtils;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.Holder> {

    private List<NoteBean> mList;
    private Context mContext;
    private OnTrashListener mOnTrashListener;

    public NoteAdapter(Context context, OnTrashListener onTrashListener) {
        mContext = context;
        mOnTrashListener = onTrashListener;
    }

    public void setData(List<NoteBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.Holder holder, int position) {
        NoteBean noteBean = mList.get(position);

        setNote(holder, noteBean);

        holder.mIvTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnTrashListener != null)
                    mOnTrashListener.onDelete(noteBean, holder.getAbsoluteAdapterPosition());
            }
        });

        holder.mIvTopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnTrashListener != null)
                    mOnTrashListener.onTopping(noteBean, holder.getAbsoluteAdapterPosition());
            }
        });
    }

    private void setNote(Holder holder, NoteBean bean) {
        holder.mIvTopping.setRotation(bean.getToppingTimeStamp() == 0 ? 0 : 180f);

        List<UserBean> userRsp = SharedPreferUtil.getInstance().getPairBean().getUserRsp();
        UserBean uBean = SharedPreferUtil.getInstance().getUserBean();
        for (UserBean userBean : userRsp) {
            if (TextUtils.equals(userBean.getUserid(), bean.getUserId())) {
                uBean = userBean;
                break;
            }
        }
        holder.mTvNote.setText(bean.getContent());
        holder.mTvTime.setText(DateUtil.stampToDay(bean.getNoteTimeStamp()) + " " + DateUtil.getWeek(bean.getNoteTimeStamp()));
        UserUtils.setUserInfo(mContext, holder.mTvName, true, holder.mFlAvatar, holder.mIvAvatar, uBean);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void notifyItemMovedAll(int from, int to) {
        notifyItemMoved(from, to);
        notifyItemRangeChanged(Math.min(from, to), Math.abs(from - to) + 1);
    }

    public void notifyItemRemovedAll(int pos) {
        notifyItemRemoved(pos);
        notifyItemRangeChanged(Math.min(pos, 0), pos + 1);
    }

    protected class Holder extends RecyclerView.ViewHolder {

        protected TextView mTvNote;
        protected ImageView mIvTrash;
        protected FrameLayout mFlAvatar;
        protected ImageView mIvAvatar;
        protected TextView mTvName;
        protected TextView mTvTime;
        protected View mLlRoot;
        protected ImageView mIvTopping;

        public Holder(@NonNull View item) {
            super(item);

            mTvNote = item.findViewById(R.id.tv_note);
            mIvTrash = item.findViewById(R.id.iv_trash);
            mIvTopping = item.findViewById(R.id.iv_topping);

            mFlAvatar = item.findViewById(R.id.fl_avatar);
            mIvAvatar = item.findViewById(R.id.iv_avatar);
            mTvName = item.findViewById(R.id.tv_name);
            mTvTime = item.findViewById(R.id.tv_time);
            mLlRoot = mTvNote.getRootView();
        }
    }

    public interface OnTrashListener {
        void onDelete(NoteBean bean, int pos);

        void onTopping(NoteBean bean, int pos);
    }
}
