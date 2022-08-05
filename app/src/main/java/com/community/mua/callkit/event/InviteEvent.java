package com.community.mua.callkit.event;

import com.community.mua.callkit.utils.EaseCallAction;
import com.community.mua.callkit.base.EaseCallType;

/**
 * author lijian
 * email: Allenlee@easemob.com
 * date: 01/16/2021
 */
public class InviteEvent extends BaseEvent {
    public InviteEvent(){
        callAction = EaseCallAction.CALL_INVITE;
    }
    public EaseCallType type;
}
