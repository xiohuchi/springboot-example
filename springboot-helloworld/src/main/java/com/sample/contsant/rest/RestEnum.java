package com.sample.contsant.rest;


/**
 * Restful 枚举异常
 *
 * @author www
 */
public enum RestEnum {
    SUCCESS(200, "成功"),

    /**
     * @4000-5000都是客户端错误
     */
    PARAMETER_INVALID(4000, "亲，参数错误哦，请联系前端小哥哥"),
    DO_NOT_HAVE_PERMISSION(4001, "您无权限访问"),
    LOGIN_INVALID(4002, "登录已失效，请重新登录"),

    EMAIL_INVALID(4003, "邮箱格式非法"),
    USER_EMAIL_NOT_EXIST(4004, "用户邮箱不存在"),
    USER_ACCOUNT_ERROR(4005, "用户账号错误"),
    USER_PASSWORD_ERROR(4006, "用户密码错误"),
    SHEET_TITLES_NOT_COMPARE(4007, "导入Excel表头与模板不匹配"),
    EXCEL_PARSE_FAIL(4008, "EXCEL解析失败,请检查文件是否上传错误"),
    SHEET_NOT_EXIST(4009, "SHEET不存在"),
    PARAM_CHECK_ERROR(4010, "数据校验错误"),
    AUTH_HEADER_ERROR(4011, "请求头错误"),
    EXCEL_CHECK_FAIL(4012, "excel数据有误"),
    SELECT_RESULT_NULL(4013, "查询结果为空"),

    USER_ACCOUNT_HAS_EXIST(4014, "用户账号已经存在"),
    EXCEL_FORMULA_FAIL(4015, "excel公式读取出错"),
    PARAMETER_ERROR(4016, "亲，参数格式非法"),

    //客户模块
    CUSOMER_EXISTED(4100, "客户公司已经存在"),

    IMPORT_FAIL(4101, "导入失败"),
    IMPORT_WARN(4102, "导入有异常数据"),

    LIMIT_WARN(4201, "限制操作"),

    /**
     * @5000以上都是服务端错误
     */
    SERVICE_ERROR(5000, "服务器错误"),
    READFILE_ERROR(5001, "文件读取失败，请重新选择文件"),

    DELETE_FAIL(5002, "删除失败"),
    UPDATE_FAIL(5003, "修改失败"),


    OTHERS(9999, "其他异常");//自定义异常


    private int status;
    private String message;

    RestEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
