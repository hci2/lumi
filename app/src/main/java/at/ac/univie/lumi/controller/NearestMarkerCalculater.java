package at.ac.univie.lumi.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.ac.univie.lumi.model.MapMarker;
import at.ac.univie.lumi.model.SlopeAreaMapMarker;
import at.ac.univie.lumi.view.SplashLoadingActivity;

/**
 * Created by phili on 6/1/2017.
 *
 * The class is used to get nearest map marker objects based on the user´s current gps position.
 */

public class NearestMarkerCalculater implements PermissionsListener{

    private Context currentContext;
    private Activity currentActivity;

    public static LatLng myPosition;
    public static double myPositionLongitude;
    public static double myPositionLatitude;


    private static LocationEngine locationEngine;
    private static LocationEngineListener locationEngineListener;
    private static PermissionsManager permissionsManager;

    /**
     * The constructor set all later needed variables, initiate the location engine and start searching for the current position of the user and return the nearest object.
     * @param currentContext The context of the invoking activity.
     * @param currentActivity The activity of the invoking activity.
     */

    public NearestMarkerCalculater(Context currentContext, Activity currentActivity) {
        this.currentContext = currentContext;
        this.currentActivity = currentActivity;


        // Get the location engine object for later use.
        locationEngine = LocationSource.getLocationEngine(currentContext);
        locationEngine.activate();

        startGps(true);

        if(myPosition!=null && myPosition.getLatitude()!=0.0 && myPosition.getLongitude()!= 0.0){
            if(currentActivity instanceof SplashLoadingActivity){
                ((SplashLoadingActivity) currentActivity).asyncMyPositionFound=true;
            }
        }else{
                //take coordinates from Innsbruck
                myPosition = new LatLng(47.266191, 11.403656);
                myPositionLatitude =myPosition.getLatitude();
                myPositionLongitude =myPosition.getLongitude();
            if(currentActivity instanceof SplashLoadingActivity){
                ((SplashLoadingActivity) currentActivity).asyncMyPositionFound=true;
            }
        }
    }

    /**
     * This method calculate the nearest map marker based on the type which is searched for.
     * @param listMapMarkers The list of map markers.
     * @param type Specifies the type of map marker where the nearest should be searched.
     * @return Returns the slopeareamapmarker object of the nearest marker depending on the type.
     */

    public SlopeAreaMapMarker calculateNearestMapMarker( ArrayList<SlopeAreaMapMarker> listMapMarkers, String type){
        List<Float> listDistancesInMeters= new ArrayList<>();
        List<SlopeAreaMapMarker> currentSearchedMapMarkersList = new ArrayList<>();
        for(int i =0; i<listMapMarkers.size();i++){
            if(listMapMarkers.get(i).getType().equalsIgnoreCase(type)){
                listDistancesInMeters.add(getDistance(myPosition,listMapMarkers.get(i).getLatLng()));
                currentSearchedMapMarkersList.add(listMapMarkers.get(i));
            }
        }
        float nearestValue = Collections.min(listDistancesInMeters);
        for(int i =0; i<listDistancesInMeters.size();i++){
            if(nearestValue==listDistancesInMeters.get(i)){
                //the nearest map Marker
                return currentSearchedMapMarkersList.get(i);
            }
        }
        return null;
    }


    /**
     * This method return the distance between the user location and the searched marker.
     * @param my_latlong The latitude and longitude of the user position.
     * @param frnd_latlong The latitude and longitude of the searched marker.
     * @return Returns the distance between both coordinates in meters.
     */

    //return distance in meters
    private float getDistance(LatLng my_latlong, LatLng frnd_latlong) {
        Location l1 = new Location("MyLocation");
        l1.setLatitude(my_latlong.getLatitude());
        l1.setLongitude(my_latlong.getLongitude());

        Location l2 = new Location("SearchedMarker");
        l2.setLatitude(frnd_latlong.getLatitude());
        l2.setLongitude(frnd_latlong.getLongitude());

        float distance = l1.distanceTo(l2);
        String dist = distance + " m";

        /* reduce to km for easier comparing
        if (distance > 1000.0f) {
            distance = distance / 1000.0f;
            dist = distance + " km";
        }*/
        return distance; //in meters
    }

    /**
     * This method starts the searching for the user gps position.
     * @param enableGps True if we are interested in the searching.
     */

    private void startGps(boolean enableGps) {
        if (enableGps) {
            // Check if user has granted location permission
            permissionsManager = new PermissionsManager(this);
            if (!PermissionsManager.areLocationPermissionsGranted(currentContext)) {
                permissionsManager.requestLocationPermissions(currentActivity);
            } else {
                enableLocation(true);
            }
        } else {
            enableLocation(false);
        }

    }

    /**
     * This method enable the location and return the last location coordinates.
     * @param enabled True if the permission is granted.
     */

    private void enableLocation(boolean enabled) {
        if (enabled) {
            // If we have the last location of the user, we can move the camera to that position.
            if (ActivityCompat.checkSelfPermission(currentContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(currentContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                ActivityCompat.requestPermissions(currentActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},   //request specific permission from user
                        9);
                ActivityCompat.requestPermissions(currentActivity,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},   //request specific permission from user
                        8);
                return;
            }

            Location lastLocation = locationEngine.getLastLocation();
            if(lastLocation!=null){
                myPosition = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                myPositionLatitude = lastLocation.getLatitude();
                myPositionLongitude = lastLocation.getLongitude();
            }

            locationEngineListener = new LocationEngineListener() {
                @Override
                public void onConnected() {
                    // No action needed here.
                }

                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        // Move the map camera to where the user location is and then remove the
                        // listener so the camera isn't constantly updating when the user location
                        // changes. When the user disables and then enables the location again, this
                        // listener is registered again and will adjust the camera once again.
                        LatLngJsonXmlExtractor.latitude = location.getLatitude();
                        LatLngJsonXmlExtractor.longitude = location.getLongitude();
                        LatLngJsonXmlExtractor.altitude = location.getAltitude();

                        //This prevents the whole time to focus to your location, and guarantee that it is only executed once
                        locationEngine.removeLocationEngineListener(this);
                    }
                }
            };
            locationEngine.addLocationEngineListener(locationEngineListener);
        } else {
            //ignore disable location
        }
    }

    /**
     * If an explanation for the permission request is needed.
     * @param permissionsToExplain A list of the permission which is needed to explain.
     */

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(currentContext, "Diese App benötigt die Erlaubnis auf GPS Daten um ihre Position zu ermitteln.",
                Toast.LENGTH_LONG).show();

    }

    /**
     * This method handle the result of the user reaction.
     * @param granted This variable contains if the resalt was true or not.
     */

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation(true);
        } else {
            Toast.makeText(currentContext, "Sie haben keine Erlaubnis auf die Verwendung von GPS Daten vergeben.",
                    Toast.LENGTH_LONG).show();
            currentActivity.finish();
        }

    }

    /**
     * This methode ensure that the location engine is removed after the onPause() and onDestroy() methods of an activity.
     */

    //invoke this in the onPause() and onDestroy() methods of every activtiy which uses this
    public void removeLocationUpdates(){
        //locationManager.removeUpdates(locationListener);
        // Ensure no memory leak occurs if we register the location listener but the call hasn't
        // been made yet.
        if (locationEngineListener != null) {
            locationEngine.removeLocationEngineListener(locationEngineListener);
        }

    }
}
