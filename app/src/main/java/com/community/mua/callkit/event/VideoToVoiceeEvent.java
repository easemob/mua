package com.community.mua.callkit.event;

import com.community.mua.callkit.utils.EaseCallAction;

/**
 * author lijian
 * email: Allenlee@easemob.com
 * date: 01/18/2021
 */
public class VideoToVoiceeEvent extends BaseEvent {
    public VideoToVoiceeEvent(){
        callAction = EaseCallAction.CALL_VIDEO_TO_VOICE;
    }
}
