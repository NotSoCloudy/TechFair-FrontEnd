
package com.ford.cpp.android.contract;

import java.io.Serializable;
import java.util.List;

public class ChargingStationList implements Serializable {

    public List<ChargingStationContract> getStationList() {
        return stationList;
    }

    public void setStationList(List<ChargingStationContract> stationList) {
        this.stationList = stationList;
    }

    private List<ChargingStationContract> stationList;
}
