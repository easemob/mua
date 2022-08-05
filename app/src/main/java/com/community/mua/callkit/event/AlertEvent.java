package com.community.mua.callkit.event;
import com.community.mua.callkit.utils.EaseCallAction;


/**
 * author lijian
 * email: Allenlee@easemob.com
 * date: 01/12/2021
 */
public class AlertEvent extends com.community.mua.callkit.event.BaseEvent {
   public AlertEvent(){
        callAction = EaseCallAction.CALL_ALERT;
    }
}
