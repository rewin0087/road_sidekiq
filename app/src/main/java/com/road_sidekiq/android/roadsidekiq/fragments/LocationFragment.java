package com.road_sidekiq.android.roadsidekiq.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.road_sidekiq.android.roadsidekiq.R;
import com.road_sidekiq.android.roadsidekiq.utilities.GPSTracker;
import com.road_sidekiq.android.roadsidekiq.utilities.MapRouting;

/**
 * Created by rewin0087 on 4/18/15.
 */
public class LocationFragment extends BaseFragment {

    public static String TITLE = "MAP";

    GoogleMap googleMap;

    MapView mapView;

    private GPSTracker gpsTracker;

    public static LocationFragment newInstance(int sectionNumber) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public LocationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        rootView = inflater.inflate(R.layout.fragment_location, container, false);
        gpsTracker = new GPSTracker(context);

        if(gpsTracker.canGetLocation()) {
            MapsInitializer.initialize(getActivity());

            mapView = (MapView) rootView.findViewById(R.id.map);
            mapView.onCreate(savedInstanceState);

            setUpMapIfNeeded();
        } else {
            gpsTracker.showSettingsAlert();
        }

        return rootView;
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            if (googleMap == null) {
                googleMap = ((MapView) rootView.findViewById(R.id.map)).getMap();
            }
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMap() {
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude())).title(gpsTracker.getAddressLine(context)));
        MapRouting mapRouting = new MapRouting(googleMap);
        LatLng origin = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        LatLng destination = new LatLng(14.5351, 120.9822);
        mapRouting.plot(origin, destination);
    }
}
