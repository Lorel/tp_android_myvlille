package com.example.lille1.myvlille.utils;


import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by havet on 15/09/14.
 */
public class Station extends Location {
    private int id;
    private Location origin;
    private float distance;
    private String name;
    private String adress;
    private int status;
    private int bikes;
    private int attachs;
    private String paiement;
    private String lastupd;

    private boolean retrieved;

    private static final XMLTools xmlTools ;

    static {
        xmlTools = new XMLTools();
    }

    public Station(int id, double lat, double lng, String name, Location origin) {
        super("station_" + id);
        this.id = id;
        this.setLatitude(lat);
        this.setLongitude(lng);
        this.origin = origin;
        this.distance = this.distanceTo(origin);
        this.name = name;

        this.retrieved = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public Float getDistance() {
        return Float.valueOf(distance);
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return (adress == null) ? "" : adress;
    }

    public void setAdress(String value) {
        this.adress = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(String value) {
        try {
            this.status = Integer.parseInt(value);
        } catch (IllegalArgumentException iae) {
            System.out.println("Error parsing status value for station id " + this.id);
            this.status = 0;
        }
    }

    public int getBikes() {
        return bikes;
    }

    public void setBikes(String value) {
        try {
            this.bikes = Integer.parseInt(value);
        } catch (IllegalArgumentException iae) {
            System.out.println("Error parsing bikes value for station id " + this.id);
            this.bikes = 0;
        }
    }

    public int getAttachs() {
        return attachs;
    }

    public void setAttachs(String value) {
        try {
            this.attachs = Integer.parseInt(value);
        } catch (IllegalArgumentException iae) {
            System.out.println("Error parsing attachs value for station id " + this.id);
            this.attachs = 0;
        }
    }

    public String getPaiement() {
        return (paiement == null) ? "" : paiement;
    }

    public void setPaiement(String value) {
        this.paiement = value;
    }

    public String getLastupd() {
        return (lastupd == null) ? "" : lastupd;
    }

    public void setLastupd(String value) {
        this.lastupd = value;
    }

    public LatLng getLatLng() {
        return new LatLng(this.getLatitude(), this.getLongitude());
    }

    public String getInfos() {
        return this.getHumanReadableDistance()
                + " - V : "
                + ( this.retrieved ? this.getBikes() : "-" )
                + " - P : "
                + ( this.retrieved ? this.getAttachs() : "-" )
                + this.getPaiementInfo();
    }

    public String getSnippetInfos() {
        return this.getAdress()
                + " - V : "
                + ( this.retrieved ? this.getBikes() : "-" )
                + " - P : "
                + ( this.retrieved ? this.getAttachs() : "-" )
                + this.getPaiementInfo();
    }

    private String getPaiementInfo() {
        if (this.getPaiement().equals("AVEC_TPE"))
            return " - CB";

        return "";
    }


    @Override
    public String toString() {
        return this.getName();
    }

    public void retrieveInformations() {
        xmlTools.getStationWithInfos(this);

        this.retrieved = true;
    }

    private String getHumanReadableDistance() {
        if (this.getDistance() < 1000)
            return String.format("%1$,.0f", this.getDistance()) + " m";

        return String.format("%1$,.2f", this.getDistance() / 1000) + " km";
    }


}
