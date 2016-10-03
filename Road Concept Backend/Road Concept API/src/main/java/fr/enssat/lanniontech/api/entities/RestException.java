package fr.enssat.lanniontech.api.entities;

public class RestException implements Entity {

    private int code;
    private String cause;

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
