package com.community.mua.callkit.event;

import com.community.mua.callkit.utils.EaseCallAction;

/**
 * author lijian
 * email: Allenlee@easemob.com
 * date: 01/12/2021
 */
public class CallCancelEvent extends BaseEvent {
    public CallCancelEvent(){
        callAction = EaseCallAction.CALL_CANCEL;
    }
    public boolean cancel = true;
    public boolean remoteTimeout = false;
}
