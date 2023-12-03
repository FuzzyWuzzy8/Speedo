package pl.pue.air.speedo;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {

    private MainActivity mainActivity;

    public MyLocationListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private static final int UPDATE_DISTANCE_THRESHOLD = 5;

    //old method
    /*
    @Override
    public void onLocationChanged(Location loc) {

        if (loc != null) {
            Double d1;
            Long t1;
            Double speed = 0.0;
            d1 = 0.0;
            t1 = 0L;
            mainActivity.positions[mainActivity.counter][0] = loc.getLatitude();
            mainActivity.positions[mainActivity.counter][1] = loc.getLongitude();
            mainActivity.times[mainActivity.counter] = loc.getTime();

            try {
                d1 = distance(mainActivity.positions[mainActivity.counter][0],
                        mainActivity.positions[mainActivity.counter][1],
                        mainActivity.positions[(mainActivity.counter + (mainActivity.data_points - 1)) %
                                mainActivity.data_points][0],
                        mainActivity.positions[(mainActivity.counter + (mainActivity.data_points - 1)) %
                                mainActivity.data_points][1]);
                t1 = mainActivity.times[mainActivity.counter] -
                        mainActivity.times[(mainActivity.counter + (mainActivity.data_points - 1)) %
                                mainActivity.data_points];
            } catch (NullPointerException e) {
                // Do nothing, not enough data yet.
            }

            if (loc.hasSpeed()) {
                speed = loc.getSpeed() * 1.0;
            } else {
                speed = d1 / t1; // m/s
            }

            mainActivity.counter = (mainActivity.counter + 1) % mainActivity.data_points;
            speed = speed * 3.6d;

            mainActivity.setAndDisplayCurrentSpeed(speed);
            mainActivity.addAndDisplayCurrentDistance(d1);


        } else {
            mainActivity.setAndDisplayCurrentSpeed(-1.0);
        }

    }
    */

    //new method
    //additional tests needed
    @Override
    public void onLocationChanged(Location loc) {
        Double distance = 0.0;

        if (loc != null) {
            Double d1;
            Long t1;
            Double speed = 0.0;
            d1 = 0.0;
            t1 = 0L;
            mainActivity.positions[mainActivity.counter][0] = loc.getLatitude();
            mainActivity.positions[mainActivity.counter][1] = loc.getLongitude();
            mainActivity.times[mainActivity.counter] = loc.getTime();

            try {
                // get the distance and time between the current position,
                // and the previous position.
                // using (counter - 1) % data_points doesn't wrap properly
                d1 = distance(
                        mainActivity.positions[mainActivity.counter][0],
                        mainActivity.positions[mainActivity.counter][1],
                        mainActivity.positions[(mainActivity.counter + (mainActivity.data_points - 1)) % mainActivity.data_points][0],
                        mainActivity.positions[(mainActivity.counter + (mainActivity.data_points - 1)) % mainActivity.data_points][1]
                );
                t1 = mainActivity.times[mainActivity.counter] - mainActivity.times[(mainActivity.counter + (mainActivity.data_points - 1)) % mainActivity.data_points];
            } catch (NullPointerException e) {
                // all good, just not enough data yet.
            }

            if (loc.hasSpeed()) {
                speed = loc.getSpeed() * 1.0;
            } else {
                speed = d1 / t1; // m/s
            }

            mainActivity.counter = (mainActivity.counter + 1) % mainActivity.data_points;
            // convert from m/s to km/h
            speed = speed * 3.6d;

            mainActivity.setAndDisplayCurrentSpeed(speed);

            // Update distance only if it exceeds the threshold
            if (d1 >= UPDATE_DISTANCE_THRESHOLD) {
                mainActivity.addAndDisplayCurrentDistance(d1);
                // Reset distance after updating
                distance = 0.0;
            }
        } else {
            mainActivity.setAndDisplayCurrentSpeed(-1.0);
        }
    }


    @Override
    public void onProviderDisabled(String provider) {
        mainActivity.onProviderDisabled(provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        mainActivity.onProviderEnabled(provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Implementation of the location provider status change
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                        Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60;
        dist = dist * 1852;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
