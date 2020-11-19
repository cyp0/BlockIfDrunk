package com.example.byd.aplication.models;

import java.util.Date;

public class History {

    private String hour;
    private String localization;

    public History() {
    }

    public History(String hour, String localization) {
        this.hour = hour;
        this.localization = localization;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getLocalization() {
        return localization;
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }

    @Override
    public String toString() {
        return "Fecha : " + hour + "\nUbicacion:\n" + localization;
    }



}
