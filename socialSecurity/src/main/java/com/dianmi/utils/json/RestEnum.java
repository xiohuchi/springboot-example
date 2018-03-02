package com.dianmi.utils.json;

/**
 * created by www
 * 2017/9/29 17:08
 */
public enum RestEnum {
    SUCCESS(200, "成功"),
    FAILD(201,"操作失败"),
    //400错误
    PARAMETER_ERROR(4000, "参数错误"),
    DO_NOT_HAVE_PERMISSION(4001, "您无权限访问"),
    USER_TOKEN_ERROR(4002, "登录已失效，请重新登录"),
    //用户登录
    USERNAME_NOT_EXISTS(4003, "用户账号不存在"),
    PASSWORD_ERROR(4004, "密码错误"),
    USERNAME_OR_PASSWORD_ISEMPTY(4005, "用户名或密码不能为空"),
    //修改用户信息
    PASSWORD_ISEMPTY(4006, "原密码或新密码不能为空"),
    OLDPASSWORD_ISERROR(4007, "原密码错误"),
    //文件
    FILE_NOT_EXISTS(4008, "文件不存在"),
    FILE_SIZE_EXCEEDS_LIMIT(4009, "文件大小超出限制"),
    FILE_FORMATS_ERROR(4010, "上传文件格式错误"),
    FILE_DATA_IS_NULL(4011,"文件数据为空"),
 
    //500错误
    SERVICE_ERROR(5000, "服务器错误"),
    READFILE_ERROR(5001, "文件读取失败，请重新选择文件"),
    DELETE_FAIL(5002, "删除失败"),
    UPDATE_FAIL(5003, "修改失败"),
    SAVE_FALL(5004, "保存错误"),
    ENTITY_IS_EMPTY(5005,"信息不存在或已删除"),
    ;

    private int status;
    private String message;

    RestEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}