package com.pedestriamc.strings.api.channels.data;

public class ProximityChannelData extends WorldChannelData {

    private double distance;

    public ProximityChannelData(){
        super();
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

}
