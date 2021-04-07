package com.example.byd.aplication.models;

import java.util.Date;

public class History {

    private String hour;
    private String localization;
    private String lifeguard;

    public History() {
    }

    public History(String hour, String localization, String lifeguard) {
        this.hour = hour;
        this.localization = localization;
        this.lifeguard = lifeguard;
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

    public String getLifeguard() {
        return lifeguard;
    }

    public void setLifeguard(String lifeguard) {
        this.lifeguard = lifeguard;
    }

    @Override
    public String toString() {
        return "Fecha: " + hour + "\nUbicacion:\n" + localization + "\n" + "Contacto designado: " + lifeguard;
    }



}
