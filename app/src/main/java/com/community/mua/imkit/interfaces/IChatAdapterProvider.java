package com.community.mua.imkit.interfaces;

import com.hyphenate.chat.EMMessage;
import com.community.mua.imkit.adapter.EaseBaseMessageAdapter;


public interface IChatAdapterProvider {
    /**
     * provide chat message's adapter
     * if is null , will use default {@link com.community.mua.imkit.adapter.EaseMessageAdapter}
     * @return
     */
    EaseBaseMessageAdapter<EMMessage> provideMessageAdaper();
}
