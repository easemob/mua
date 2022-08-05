package com.community.mua.imkit.modules.conversation.adapter;

import com.community.mua.R;
import com.community.mua.imkit.adapter.EaseBaseDelegateAdapter;
import com.community.mua.imkit.modules.conversation.model.EaseConversationInfo;


public class EaseConversationListAdapter extends EaseBaseDelegateAdapter<EaseConversationInfo> {
    private int emptyLayoutId;

    @Override
    public int getEmptyLayoutId() {
        return emptyLayoutId != 0 ? emptyLayoutId : R.layout.ease_layout_default_no_conversation_data;
    }

    /**
     * set empty layout
     * @param layoutId
     */
    public void setEmptyLayoutId(int layoutId) {
        this.emptyLayoutId = layoutId;
        notifyDataSetChanged();
    }

}

