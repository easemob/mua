package com.community.mua.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.community.mua.R;
import com.community.mua.imkit.EaseIM;
import com.community.mua.imkit.ui.EaseShowBigImageActivity;
import com.community.mua.imkit.utils.EaseFileUtils;
import com.community.mua.utils.SoundUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.util.EMLog;

import java.util.List;

public class ChatPicHistoryAdapter extends RecyclerView.Adapter<ChatPicHistoryAdapter.Holder> {
    private Context mContext;
    private List<EMMessage> mList;

    public ChatPicHistoryAdapter(Context context, List<EMMessage> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public ChatPicHistoryAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_pic_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatPicHistoryAdapter.Holder holder, int position) {
        EMMessage message = mList.get(position);
        EMMessageBody messageBody = message.getBody();
        String url = "";
        if (messageBody instanceof EMImageMessageBody) {
            url = ((EMImageMessageBody) messageBody).getThumbnailUrl();
            if (TextUtils.isEmpty(url)) {
                url = ((EMImageMessageBody) messageBody).getLocalUrl();
            }
        } else if (messageBody instanceof EMVideoMessageBody) {
            url = ((EMVideoMessageBody) messageBody).getThumbnailUrl();
            if (TextUtils.isEmpty(url)) {
                url = ((EMVideoMessageBody) messageBody).getLocalUrl();
            }
        }

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.unload)
                .error(R.drawable.unload);
        Glide.with(mContext).applyDefaultRequestOptions(options).load(url).into(holder.mIvCover);

        holder.mIvCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundUtil.getInstance().playBtnSound();
                if (!(messageBody instanceof EMImageMessageBody)) {
                    return;
                }
                EMImageMessageBody imgBody = (EMImageMessageBody) message.getBody();
                if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                    if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED) {
                        // retry download with click event of user
                        EMClient.getInstance().chatManager().downloadThumbnail(message);
                    }
                } else {
                    if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                            imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING ||
                            imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED) {
                        // retry download with click event of user
                        EMClient.getInstance().chatManager().downloadThumbnail(message);
                        return;
                    }
                }
                Intent intent = new Intent(mContext, EaseShowBigImageActivity.class);
                Uri imgUri = imgBody.getLocalUri();
                //检查Uri读权限
                EaseFileUtils.takePersistableUriPermission(mContext, imgUri);
                EMLog.e("Tag", "big image uri: " + imgUri + "  exist: " + EaseFileUtils.isFileExistByUri(mContext, imgUri));
                if (EaseFileUtils.isFileExistByUri(mContext, imgUri)) {
                    intent.putExtra("uri", imgUri);
                } else {
                    // The local full size pic does not exist yet.
                    // ShowBigImage needs to download it from the server
                    // first
                    String msgId = message.getMsgId();
                    intent.putExtra("messageId", msgId);
                    intent.putExtra("filename", imgBody.getFileName());
                }
                if (!EaseIM.getInstance().getConfigsManager().enableSendChannelAck()) {
                    //此处不再单独发送read_ack消息，改为进入聊天页面发送channel_ack
                    //新消息在聊天页面的onReceiveMessage方法中，排除视频，语音和文件消息外，发送read_ack消息
                    if (message != null && message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked()
                            && message.getChatType() == EMMessage.ChatType.Chat) {
                        try {
                            EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class Holder extends RecyclerView.ViewHolder {

        protected ImageView mIvCover;

        public Holder(@NonNull View itemView) {
            super(itemView);

            mIvCover = itemView.findViewById(R.id.iv_cover);
        }
    }
}
