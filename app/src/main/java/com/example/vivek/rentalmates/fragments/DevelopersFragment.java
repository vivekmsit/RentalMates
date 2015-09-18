package com.example.vivek.rentalmates.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.FirstActivity;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.interfaces.OnDeleteRemoteDataReceiver;
import com.example.vivek.rentalmates.tasks.DeleteRemoteDataAsyncTask;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 */
public class DevelopersFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "ExpenseList_Debug";

    private Button deleteLocalDataButton;
    private Button deleteRemoteDataButton;
    private Button testButton;
    private AppData appData;
    private Context context;
    private SharedPreferences prefs;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreate");
        super.onCreate(savedInstanceState);
        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();
        prefs = getActivity().getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_developers, container, false);

        deleteLocalDataButton = (Button) layout.findViewById(R.id.deleteLocalDataButton);
        deleteLocalDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLocalData();
            }
        });

        deleteRemoteDataButton = (Button) layout.findViewById(R.id.deleteRemoteDataButton);
        deleteRemoteDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRemoteData();
            }
        });

        testButton = (Button) layout.findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTestButtonClicked();
            }
        });

        return layout;
    }

    public void deleteLocalData() {
        //clear SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        //Reset AppData contents
        appData.clearAppData(context);

        //Start FirstActivity
        Intent intent = new Intent(context, FirstActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void deleteRemoteData() {
        DialogFragment deleteRemoteDataDialog = new DialogFragment() {

            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Confirm");
                alertDialogBuilder.setMessage("Do you really want to delete remote data?");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteRemoteDataAsyncTask task = new DeleteRemoteDataAsyncTask(context);
                        task.setOnDeleteRemoteDataReceiver(new OnDeleteRemoteDataReceiver() {
                            @Override
                            public void onDeleteRemoteDataSuccessful() {
                                progressDialog.cancel();
                                Toast.makeText(context, "Remote Data Deleted Successfully", Toast.LENGTH_SHORT).show();
                                deleteLocalData();
                            }

                            @Override
                            public void onDeleteRemoteDataFailed() {
                                progressDialog.cancel();
                            }
                        });
                        task.execute();
                        progressDialog.setMessage("Deleting Remote Data ");
                        progressDialog.show();
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Cancel: onClick");
                        dialog.dismiss();
                    }
                });
                return alertDialogBuilder.create();
            }
        };
        deleteRemoteDataDialog.show(getFragmentManager(), "DeleteRemoteDataDialog");
    }

    public void onTestButtonClicked() {
        showMapView();
    }

    private void showMapView() {
        final DialogFragment mapViewDialog = new android.support.v4.app.DialogFragment() {
            private MapView mapView;
            private GoogleMap map;
            private double currentLatitude;
            private double currentLongitude;
            private boolean currentLocationUpdated;


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

                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Cancel: onClick");
                        dialog.dismiss();
                    }
                });
                return alertDialogBuilder.create();
            }
        };
        mapViewDialog.show(getFragmentManager(), "Fragment");
    }
}

