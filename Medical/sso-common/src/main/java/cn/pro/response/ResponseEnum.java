package cn.pro.response;

public enum  ResponseEnum {


    SUCCESS("0", "OK"),


    ERROR("900001", "ERROR");

    ResponseEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private String code;

    private String message;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
