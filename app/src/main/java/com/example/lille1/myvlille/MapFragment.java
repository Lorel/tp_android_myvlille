package com.example.lille1.myvlille;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lille1.myvlille.utils.Station;
import com.example.lille1.myvlille.utils.StationsWrapper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by havet on 22/09/14.
 */
public class MapFragment extends Fragment{

    private MapView mapView;

    private Station[] stations;
    private Location origin;
    private Station target;

    public MapFragment() {
        super();
    }

    public MapView getMapView() {
        return mapView;
    }

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public Station getTarget() {
        return target;
    }

    public void setTarget(Station target) {
        this.target = target;
    }

    public void setLocations(StationsWrapper stationsWrapper) {
        this.stations = stationsWrapper.getStationsArray();
        this.origin = stationsWrapper.getOrigin();
        this.target = stationsWrapper.getTarget();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_maps, container, false);

        this.mapView = (MapView) v.findViewById(R.id.mapview);
        this.mapView.onCreate(savedInstanceState);

        MapsInitializer.initialize(this.getActivity());

        GoogleMap map = this.mapView.getMap();

        // create marker origin
        MarkerOptions originMarker = new MarkerOptions()
                .title("You are here")
                .position(new LatLng(this.origin.getLatitude(), this.origin.getLongitude()))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.emo_im_cool)); // use emoticon for current position

        // create marker target
        MarkerOptions targetMarker = new MarkerOptions()
                .title(this.target.getName())
                .position(this.target.getLatLng())
                .snippet(this.target.getSnippetInfos());

        // adding marker origin
        map.addMarker(originMarker);

        // adding marker target
        map.addMarker(targetMarker).showInfoWindow(); // show infos with address for the target

        // adding marker for other stations
        MarkerOptions otherStationMarker;
        for (Station s : this.stations) {
            if (s.getId() == this.target.getId())
                continue;

            otherStationMarker = new MarkerOptions()
                    .title(s.getName())
                    .position(s.getLatLng())
                    .snippet(s.getSnippetInfos())
                    .icon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

            map.addMarker(otherStationMarker);
        }

        // go to the map, move from origin to target
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(this.origin.getLatitude(), this.origin.getLongitude())));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(this.target.getLatLng(), 16), 2000, null);

        return v;
    }

    @Override
    public void onResume() {
        GoogleMap map = this.mapView.getMap();

        this.mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        this.mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        this.mapView.onLowMemory();
        super.onLowMemory();
    }
}
