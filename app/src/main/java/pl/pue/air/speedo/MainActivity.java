package pl.pue.air.speedo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.LocationListener;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

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
    private TextView textViewNoSatellites;
    private Button buttonResetDistance;
    private Button buttonResetSpeed;
    private int speed = -1;
    private float maxSpeed = -1.0F;
    private double distance = 0.0;
    private int satellites = 0;

    // location permission
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static final String LOG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View blankView = new View(this);  //leave for later
        setContentView(R.layout.activity_main);

        // Initialize TextView and Button objects here
        textViewAppName = findViewById(R.id.textSpeedo);
        textViewCurrentSpeed = findViewById(R.id.textCurrent_speed);
        textViewMaxSpeed = findViewById(R.id.textMax_speed);
        textViewCurrentDistance = findViewById(R.id.textDistance);
        textViewNoSatellites = findViewById(R.id.textNoSatellites);
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
            Log.e(String.valueOf(LOG), getString(R.string.accessRightsNotGranted));  // Log.e(LOG, getString(R.string.accessRightsNOTgranted));
        }


        //locale language
        loadLocale();

        // Change actionbar title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.app_name));
        } else {
            Log.e("MainActivity", "ActionBar is null");
        }

        //language
        TextView changeLang = findViewById(R.id.language);
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });
        //

        // Check and request location permission
        if (checkLocationPermission()) {
            // Permission granted, initialize location listener
            initLocationListener();
        } else {
            // Permission not granted, request it
            requestLocationPermission();
        }


    }
/*
    // Check if the ACCESS_FINE_LOCATION permission is granted
    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
*/
    private boolean checkLocationPermission() {
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    // Request the ACCESS_FINE_LOCATION permission
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    // Initialize location listener
    private void initLocationListener() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener(this);

        lm.removeUpdates(locationListener);

        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            Log.e(String.valueOf(LOG), getString(R.string.accessRightsNotGranted));
        }
    }

    /*
    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, initialize location listener
                initLocationListener();
            } else {
                // Permission denied, handle accordingly (show a message, disable features, etc.)
                Log.e(LOG, getString(R.string.accessRightsNotGranted));
                // You might want to show a message or disable location-related features here.
            }
        }
    }

     */



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
                Log.e(String.valueOf(LOG), getString(R.string.accessRightsNotGranted) + ": " + p);  //Log.e(LOG, getString(R.string.accessRightsNOTgranted) + ": " + p);
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

    //old distance method
    /*
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
    */

    void addAndDisplayCurrentDistance(Double distance) {
        if (distance != null) {
            this.distance += distance; //Add exact distance value
            if (textViewCurrentDistance != null) {
                textViewCurrentDistance.setText(
                        getString(R.string.current_distance) +
                                ": " + this.distance + ' ' + getString(R.string.m));
            }
        }
    }

    public void setSatellitesAndDisplayTheirNumber(int satellites) {
        this.satellites = satellites;
        if (textViewNoSatellites != null) {
            textViewNoSatellites.setText(
                    getString(R.string.noSatellites) + ": " + this.satellites);
        }
    }
    // Other methods

    // Method to reset distance
    private void resetDistance() {
        distance = 0;
        if (textViewCurrentDistance != null) {
            textViewCurrentDistance.setText(getString(R.string.current_distance) + ": 0.0 " + getString(R.string.m));
        }
    }

    // Method to reset speed
    private void resetSpeed() {
        maxSpeed = -1.0F;
        if (textViewMaxSpeed != null) {
            textViewMaxSpeed.setText(getString(R.string.max_speed) + ": - " + getString(R.string.kmh));
        }
    }

    //
    private void setLocaleAndTheme(String lang) {
        // Set locale
        setLocale(lang);

        // Set theme based on language, fallback to default theme
        int themeId = getResources().getIdentifier("AppTheme_" + lang, "style", getPackageName());
        if (themeId == 0) {
            themeId = R.style.Base_Theme_Speedo; // Default theme
        }

        setTheme(themeId);
        recreate();
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"English", "Polish", "French", "German", "Spanish", "Turkish", "Russian"};
        // Use ContextThemeWrapper to apply the custom style
        //ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(MainActivity.this, R.style.LanguageDialog);
        //AlertDialog.Builder mBuilder = new AlertDialog.Builder(contextThemeWrapper, R.style.LanguageDialog);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this, R.style.LanguageDialog);
        mBuilder.setTitle("Choose language");

        //Add custom theme later
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_list_item, R.id.text1, listItems);
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                switch (i){
                    case 0:
                        // English
                        //setLocaleAndTheme("en");
                        setLocale("en");
                        recreate();
                        break;
                    case 1:
                        // Polish
                        setLocaleAndTheme("pl");
                        //setLocale("pl");
                        recreate();
                        break;
                    case 2:
                        // French
                        setLocale("fr");
                        recreate();
                        break;
                    case 3:
                        // German
                        setLocale("de");
                        recreate();
                        break;
                    case 4:
                        // Spanish
                        setLocale("es");
                        recreate();
                        break;
                    case 5:
                        // Turkish
                        setLocale("tr");
                        recreate();
                        break;
                    case 6:
                        // Russian
                        setLocale("ru");
                        recreate();
                        break;
                }

                dialogInterface.dismiss();   //dismiss alert dialog when language is selected
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();                     //show alert dialog
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        //save data to preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    //load language from shared preference
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }
}