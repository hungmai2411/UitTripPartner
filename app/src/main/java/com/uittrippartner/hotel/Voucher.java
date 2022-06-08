package com.uittrippartner.hotel;

import java.util.Date;

public class Voucher {
    private String code;
    private long number;
    private String description;
    private Date startDate;
    private Date endDate;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Voucher() {
    }

    public Voucher(String code, long number, String description, Date startDate, Date endDate) {
        this.code = code;
        this.number = number;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
