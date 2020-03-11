package com.example.safetynet.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.os.Process;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.safetynet.MainActivity;
import com.example.safetynet.R;
import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.security.PrivateKey;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class HomeFragment extends Fragment {

    private String serverAddress = "todo-ngcg7jl7oa-uk.a.run.app";
    private LocationManager locationManager;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) getActivity();


        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        /* Custom code */
        Button helpButton = (Button) root.findViewById(R.id.emergency_request);
        System.out.println(helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AskForHelp helpRequest = new AskForHelp("help");
                Thread helpThread = new Thread(helpRequest);
                helpThread.start();
            }
        });
        return root;
    }

    private class AskForHelp implements Runnable {

        private String helpMessage;

        private double latitude = Double.MAX_VALUE;
        private double longitude = Double.MAX_VALUE;
        private double altitude = Double.MAX_VALUE;

        AskForHelp(String helpMessage) {
            this.helpMessage = helpMessage;
        }

        /* Permission checking code from https://www.androdocs.com/java/getting-current-location-latitude-longitude-in-android-using-java.html */
        private boolean checkLocationPermissions() {
            if ((ActivityCompat.checkSelfPermission((MainActivity) getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                    (ActivityCompat.checkSelfPermission((MainActivity) getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                return true;
            }
            return false;
        }

        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            try {
                FirebaseInstanceId id = FirebaseInstanceId.getInstance();
                String userId = id.getId();

                InetAddress server = InetAddress.getByName(serverAddress);
                URL url = new URL("https://" + server.getHostName() + "/help");

                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setNumUpdates(1);
                locationRequest.setInterval((long) 0);
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                /* No error checking for permissions not being granted to access location */
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);

                /* Source: https://www.valleyprogramming.com/blog/android-fused-location-provider-api-example-broomfield-co */
                LocationSettingsRequest locationSettingsRequest = builder.build();


                LocationCallback locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        if (locationResult == null) {
                            return;
                        }
                        Location location = locationResult.getLastLocation();

                        if (location != null) {
                            AskForHelp.this.latitude = location.getLatitude();
                            AskForHelp.this.longitude = location.getLongitude();
                            AskForHelp.this.altitude = location.getAltitude();
                        }
                    }
                };

                FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));

                client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            AskForHelp.this.latitude = location.getLatitude();
                            AskForHelp.this.longitude = location.getLongitude();
                            AskForHelp.this.altitude = location.getAltitude();
                        }
                    }
                });

                Looper.prepare();

                client.requestLocationUpdates(locationRequest, locationCallback, null);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("user", userId);
                connection.setRequestProperty("desc", this.helpMessage);

                String notAvailable = "NOT AVAILABLE";
                if (this.altitude != Double.MAX_VALUE) {
                    connection.setRequestProperty("alt", Double.toString(this.altitude));
                } else {
                    connection.setRequestProperty("alt", notAvailable);
                }
                if (this.longitude != Double.MAX_VALUE) {
                    connection.setRequestProperty("longitude", Double.toString(this.longitude));
                } else {
                    connection.setRequestProperty("longitude", notAvailable);
                }
                if (this.latitude != Double.MAX_VALUE) {
                    connection.setRequestProperty("latitude", Double.toString(this.latitude));
                } else {
                    connection.setRequestProperty("latitude", notAvailable);
                }
                connection.setRequestProperty("contacts", "user2,user3");
                connection.connect();
                int code = connection.getResponseCode();
                if (code != HttpURLConnection.HTTP_OK) {
                   System.out.println("Error sending help!");
                }
                connection.disconnect();
            } catch (Exception e) {
                System.out.println("Error sending help");
            }
        }
    }

    private class CustomLocationListener implements LocationListener {
        CustomLocationListener() {
            super();
        }

        @Override
        public void onLocationChanged(Location location) {

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
    }

}