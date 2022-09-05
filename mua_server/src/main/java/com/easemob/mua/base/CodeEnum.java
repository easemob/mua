package com.easemob.mua.base;

/**
 * @author easemob_developer
 * @date 2022/2/18
 */
public enum CodeEnum implements IDataCode {
    // 请求状态
    SUCCESS("0000", "操作成功"),
    SYSTEM_ERROR("5001", "服务器内部错误"),
    INVALID_PARAM("1001", "无效参数"),
    NO_RESULT("1002", "无对应数据"),
    MISS_PARAM("1003", "缺少参数"),
    UN_MATCH("1004", "用户未匹配"),
    ATTESTATION_FALL("1000", "验签失败"),
    REMOTE_TIMEOUT("1100", "调用超时"),
    REP_ERROR("1200", "重复提交"),
    MISSING_TOKEN("1301", "缺少token"),
    ILLEGALITY_TOKEN("1300", "非法token"),
    LOGIN_EXPIRATION("1400", "登陆过期"),
    VALIDATION_ERROR("1500", "验证错误"),
    IMGCODE_ERROR("1600", "图片验证码错误"),
    SMSCODE_INTERVAL("1700","发送验证码过于频繁，请稍后再试"),
    PHONENUM_EXISTS("1800","该手机号已绑定其他账户"),

    ACCESS_DENIED("4005", "超过验证数量"),
    GETUSERID_ERROR("4006", "获取用户id失败"),
    DELETE_DEPT_ERROR("4007", "部门下有成员，请先移除成员后再解散部门"),
    ADD_ERROR("4008", "添加失败"),
    EXPORT_EXCEL_ERROR("4009", "导出excel异常"),
    REDIS_ERROR("4010", "redis存储失败"),
    IMPORT_EXCEL_ERROR("4011", "导入excel异常,请上传指定模板"),
    ORGINFO_ERROR("4012", "未查到企业信息"),
    NON_EXISTENT_ERROR("4013", "不存在"),
    PHONE_NUM_REPEAT("4014", "手机号重复"),
    IM_ERROR("4015", "IM请求异常"),
    SFTP_ERROR("4016", "SFTP上传异常"),
    VALIDATION_REGISTER_INFO("4017", "手机号已注册,请扫码登录"),
    CHANGE_ORGMASTER_ERROR("4018","不支持更改成未激活用户"),
    PHONE_NUM_ERROR("4019","手机号异常"),
    ORG_EXISTENT_ERROR("4020", "部门层级有误"),
    DEPT_EXIST_ERROR("4020","部门名重复"),ORG_NON_EXISTENT_ERROR("4021","企业名称不存在"),ERROR("9999", "处理异常");

    private final String code;
    private final String msg;

    /**
     * 构造
     */
    CodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
