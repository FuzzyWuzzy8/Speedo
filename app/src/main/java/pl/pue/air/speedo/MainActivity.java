package pl.pue.air.speedo;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import androidx.appcompat.app.AppCompatActivity;

import android.location.LocationListener;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {


    public Integer data_points = 2; // how many data points to calculate for
    public Double[][] positions;
    public Long[] times;
    public Integer counter = 0;
    private boolean dataInitiated=false;


    private LocationManager lm;
    private LocationListener locationListener;

    private TextView textViewAppName;
    private TextView textViewCurrentSpeed;
    private TextView textViewMaxSpeed;
    private TextView textViewCurrentDistance;
    private TextView textViewNoOfSatellites;
    private Button buttonResetDistance;
    private Button buttonResetSpeed;
    private int speed = -1;
    private float maxSpeed = -1.0F;
    private int distance = 0;
    private int satellites = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View blankView = new View(this);
        setContentView(R.layout.activity_main);

        // Initialize TextView and Button objects here
        textViewAppName = findViewById(R.id.textSpeedo);
        textViewCurrentSpeed = findViewById(R.id.textCurrent_speed);
        textViewMaxSpeed = findViewById(R.id.textMax_speed);
        textViewCurrentDistance = findViewById(R.id.textDistance);
        // textViewNoOfSatellites = findViewById(R.id.textNoOfSatellites);
        buttonResetDistance = findViewById(R.id.reset_distance_button);
        buttonResetSpeed = findViewById(R.id.reset_max_speed_button);

        // Set click listeners for reset buttons
        buttonResetDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle distance reset button click
                resetDistance();
            }
        });

        buttonResetSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle speed reset button click
                resetSpeed();
            }
        });



        if (!dataInitiated) {
            positions = new Double[data_points][2];
            times = new Long[data_points];
            dataInitiated = true;
            displayMessage(getString(R.string.onDataInit));
        }

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener(this);

        lm.removeUpdates(locationListener);

        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            Log.e(String.valueOf(LOG), getString(R.string.accessRightsNOTgranted));  // Log.e(LOG, getString(R.string.accessRightsNOTgranted));
        }


    }



    public void onProviderDisabled(String provider) {
        Log.i(getResources().getString(R.string.app_name), "Provider Disabled: " + provider);
    }

    public void onProviderEnabled(String provider) {
        Log.i(getResources().getString(R.string.app_name), "Provider Enabled: " + provider);
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int p = 0; p < permissions.length; p++) {
            if (checkSelfPermission(permissions[p]) == PackageManager.PERMISSION_GRANTED) {
                Log.d(String.valueOf(LOG), getString(R.string.accessRightsGranted) + ": " + p);    //Log.d(LOG, getString(R.string.accessRightsGranted) + ": " + p);
            } else {
                Log.e(String.valueOf(LOG), getString(R.string.accessRightsNOTgranted) + ": " + p);  //Log.e(LOG, getString(R.string.accessRightsNOTgranted) + ": " + p);
            }
        }
    }

    private void displayMessage(String message) {
        // Display message implementation
    }

    public void setAndDisplayCurrentSpeed(Double speed) {
        if (speed != null) {
            this.speed = speed.intValue();
            boolean maxSpeedChanged = false;
            if (this.speed >= maxSpeed) {
                maxSpeed = this.speed;
                maxSpeedChanged = true;
            }
            if (textViewCurrentSpeed != null) {
                textViewCurrentSpeed.setText(
                        getString(R.string.current_speed) + ": " + this.speed + ' '
                                + getString(R.string.kmh));
            }
            if (textViewMaxSpeed != null && maxSpeedChanged) {
                textViewMaxSpeed.setText(
                        getString(R.string.max_speed) + ": " + this.maxSpeed + ' '
                                + getString(R.string.kmh));
            }
        }
    }

    public void addAndDisplayCurrentDistance(Double distance) {
        if (distance != null) {
            this.distance = this.distance + distance.intValue();
            if (textViewCurrentDistance != null) {
                textViewCurrentDistance.setText(
                        getString(R.string.current_distance) +          // findViewById(R.id.textDistance)
                                ": " + this.distance + ' ' + getString(R.string.m));
            }
        }
    }

    public void setSatellitesAndDisplayTheirNumber(int satellites) {
        this.satellites = satellites;
        if (textViewNoOfSatellites != null) {
            textViewNoOfSatellites.setText(
                    getString(R.string.noOfSatellites) + ": " + this.satellites);
        }
    }
    // other methods

    // Method to reset distance
    private void resetDistance() {
        distance = 0;
        if (textViewCurrentDistance != null) {
            textViewCurrentDistance.setText(getString(R.string.current_distance) + ": 0 " + getString(R.string.m));
        }
    }

    // Method to reset speed
    private void resetSpeed() {
        maxSpeed = -1.0F;
        if (textViewMaxSpeed != null) {
            textViewMaxSpeed.setText(getString(R.string.max_speed) + ": - " + getString(R.string.kmh));
        }
    }
}