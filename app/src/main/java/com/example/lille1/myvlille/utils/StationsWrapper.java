package com.example.lille1.myvlille.utils;

import android.location.Location;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Lorel on 24/09/2014.
 */
public class StationsWrapper {

    private static final XMLTools xmlTools ;

    static {
        xmlTools = new XMLTools();
    }

    private Station[] stations;
    private Station target;
    private Location origin;

    public StationsWrapper(Location origin) {
        this.stations = xmlTools.getStations(origin);

        // sort stations by distance to origin with getDistance() method
        Arrays.sort(this.stations, new Comparator<Station>() {
            @Override
            public int compare(Station station, Station station2) {
                return station.getDistance().compareTo(station2.getDistance());
            }
        });

        this.origin = origin;
    }

    public Station[] getStationsArray() {
        return this.stations;
    }

    public void setTargetByPosition(int position) {
        this.target = stations[position];
    }

    public Station getTarget() {
        return this.target;
    }

    public Location getOrigin() {
        return this.origin;
    }
}