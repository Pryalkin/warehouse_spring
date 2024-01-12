package com.security.app.enumeration;

public enum Status {

    ACCEPTED("Принята"),
    DENIED("Отклонена"),
    COMPLETED("Завершена");

    private String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }


}
