package com.example.vivek.rentalmates.dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.example.vivek.rentalmates.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

public class CurrentLocationMapDialog extends DialogFragment {
    private Context context;
    private MapView mapView;
    private GoogleMap map;
    private double currentLatitude;
    private double currentLongitude;
    private boolean currentLocationUpdated;
    private OnDialogResultListener listener;

    public interface OnDialogResultListener {
        void onPositiveResult(double longitude, double latitude);

        void onNegativeResult();
    }

    public void setOnDialogResultListener(OnDialogResultListener listener) {
        this.listener = listener;
    }

    private void makeUseOfNewLocation() {
        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), (float) 17.23);
        map.animateCamera(cameraUpdate);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_map_view, null);
        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

        currentLatitude = 12.8486324; //velankini
        currentLongitude = 77.657392; //velankini
        currentLocationUpdated = false;

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), (float) 17.23);
        map.animateCamera(cameraUpdate);

        //map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //LatLng ll = map.getCameraPosition().target;
        //double zoom = map.getCameraPosition().zoom;
        //Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)));

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                    if (!currentLocationUpdated) {
                        makeUseOfNewLocation();
                        currentLocationUpdated = true;
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Select Flat Location");
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton("Update Location", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onPositiveResult(currentLatitude, currentLongitude);
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onNegativeResult();
                }
            }
        });
        return alertDialogBuilder.create();
    }
}
