package com.ford.cpp.android.contract;

import java.io.Serializable;

public class ChargingStationContract implements Serializable{

    private String name;
    private Long id;
    private boolean status;
    private double latitude;
    private double longitude;
    private long usageCounter;
    private String city;
    private String vin;
    private double chargePct;

    public long getUsageCounter() {
        return usageCounter;
    }

    public void setUsageCounter(long usageCounter) {
        this.usageCounter = usageCounter;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {    return city;   }

    public void setCity(String city) {    this.city = city;   }

    public String getVin() {   return vin;  }

    public void setVin(String vin) {  this.vin = vin;  }

    public double getChargePct() {   return chargePct;  }

    public void setChargePct(double chargePct) {  this.chargePct = chargePct;  }
}
