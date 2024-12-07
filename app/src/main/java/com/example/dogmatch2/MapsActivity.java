package com.example.dogmatch2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.dogmatch2.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_maps);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_maps) {
                return true;
            } else if (itemId == R.id.bottom_chat) {
                startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_user) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Load the map fragment dynamically
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, mapFragment);
        fragmentTransaction.commit();

        mapFragment.getMapAsync(this);

        // Find the search button and set its click listener
        ImageButton buttonSearchHospitals = findViewById(R.id.buttonSearchHospitals);
        buttonSearchHospitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastLocation != null) {
                    LatLng currentLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    searchNearbyHospitals(currentLatLng);
                } else {
                    Log.d("MapsActivity", "Current location is null. Cannot search nearby hospitals.");
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getDeviceLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            lastLocation = task.getResult();
                            if (lastLocation != null) {
                                LatLng currentLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                                mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                            }
                        } else {
                            Log.d("MapsActivity", "Current location is null. Using defaults.");
                            Log.e("MapsActivity", "Exception: %s", task.getException());
                            LatLng defaultLocation = new LatLng(-34.0, 151.0);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f));
                            mMap.addMarker(new MarkerOptions().position(defaultLocation).title("Default Location"));
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void searchNearbyHospitals(LatLng currentLatLng) {
        String apiKey = getString(R.string.google_maps_key);
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + currentLatLng.latitude + "," + currentLatLng.longitude +
                "&radius=1500&type=hospital&key=" + apiKey;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                if (response.body() != null) {
                    String responseData = response.body().string();
                    Log.d("MapsActivity", "Response: " + responseData); // Log response
                    try {
                        JSONObject json = new JSONObject(responseData);
                        JSONArray results = json.getJSONArray("results");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMap.clear(); // Clear existing markers
                                for (int i = 0; i < results.length(); i++) {
                                    try {
                                        JSONObject place = results.getJSONObject(i);
                                        JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
                                        double lat = location.getDouble("lat");
                                        double lng = location.getDouble("lng");
                                        String name = place.getString("name");
                                        LatLng hospitalLatLng = new LatLng(lat, lng);
                                        mMap.addMarker(new MarkerOptions()
                                                .position(hospitalLatLng)
                                                .title(name)
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDeviceLocation();
            }
        }
    }
}
