
package com.ford.cpp.android.contract;

import java.io.Serializable;
import java.util.List;

public class ChargingStationList implements Serializable {

    private List<ChargingStationContract> stationList;
    private List<ChargingStationHeatMapContract> heatMapStationList;


    public List<ChargingStationHeatMapContract> getHeatMapStationList() {
        return heatMapStationList;
    }

    public void setHeatMapStationList(List<ChargingStationHeatMapContract> heatMapStationList) {
        this.heatMapStationList = heatMapStationList;
    }

    public List<ChargingStationContract> getStationList() {
        return stationList;
    }

    public void setStationList(List<ChargingStationContract> stationList) {
        this.stationList = stationList;
    }
}
