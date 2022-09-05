package com.easemob.mua.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author easemob_developer
 * @date 2022/6/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenBean {
    public String access_token;
    public String expires_in;
    public String application;
}
