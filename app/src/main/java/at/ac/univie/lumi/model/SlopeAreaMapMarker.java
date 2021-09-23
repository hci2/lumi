package at.ac.univie.lumi.model;

import android.app.Activity;
import android.graphics.Bitmap;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by phili on 6/9/2017.
 *
 * This class is part of the model, extends the map marker model and contains the slope area marker model for the different slope areas.  It will be also serialized on the smartphone of the user.
 */

public class SlopeAreaMapMarker extends MapMarker {
    private int dangerLvl;
    private int slopeAmountDangerZones;
    private int expositionNumber;
    private String expositionString;
    private int mapboxId;
    private int slopeAngleNumber;
    private String slopeAngleString;
    private int minSH;
    private int maxSH;
    private String additionalLoad;

    /**
     * This constructor fully initialize the map marker model.
     * @param id The unique id:
     * @param name The name of the marker.
     * @param type The category/type of the marker.
     * @param latLng The latitude and longitude of the marker.
     * @param bitmap The icon of the marker.
     */

    public SlopeAreaMapMarker(int id, String name, String type, LatLng latLng, Bitmap bitmap) {
        super(id, name, type, latLng, bitmap);
    }

    /**
     * This constructor initialize an empty slope area map marker.
     */

    public SlopeAreaMapMarker(){

    }

    /**
     * The different getter and setter methods of every variable of the slope area map marker model.
     *
     */

    public int getSlopeAmountDangerZones() {
        return slopeAmountDangerZones;
    }

    public void setSlopeAmountDangerZones(int slopeAmountDangerZones) {
        this.slopeAmountDangerZones = slopeAmountDangerZones;
    }

    public String getExpositionString() {
        return expositionString;
    }

    public int getExpositionNumber() {
        return expositionNumber;
    }

    public void setExpositionNumber(int expositionNumber) {
        this.expositionNumber = expositionNumber;
        switch (expositionNumber){
            case 1:
                this.expositionString="Nord";
                break;
            case 2:
                this.expositionString="Nord-Ost";
                break;
            case 3:
                this.expositionString="Ost";
                break;
            case 4:
                this.expositionString="Süd-Ost";
                break;
            case 5:
                this.expositionString="Süd";
                break;
            case 6:
                this.expositionString="Süd-West";
                break;
            case 7:
                this.expositionString="West";
                break;
            case 8:
                this.expositionString="Nord-West";
                break;
            default:
                break;
        }
    }

    public int getMapboxId() {
        return mapboxId;
    }

    public void setMapboxId(int mapboxId) {
        this.mapboxId = mapboxId;
    }

    public int getSlopeAngleNumber() {
        return slopeAngleNumber;
    }

    public void setSlopeAngleNumber(int slopeAngleNumber) {
        this.slopeAngleNumber = slopeAngleNumber;
        switch (slopeAngleNumber){
            case 1:
                this.slopeAngleString="mässig steil (<30°)";
                break;
            case 2:
                this.slopeAngleString="steil (30-35°)";
                break;
            case 3:
                this.slopeAngleString="sehr steil (35-40°)";
                break;
            case 4:
                this.slopeAngleString="extrem steil (>40°)";
                break;
            default:
                break;
        }
    }

    public String getSlopeAngleString() {
        return slopeAngleString;
    }

    public int getDangerLvl() {
        return dangerLvl;
    }

    public void setDangerLvl(int dangerLvl) {
        this.dangerLvl = dangerLvl;
    }

    public int getMinSH() {
        return minSH;
    }

    public void setMinSH(int minSH) {
        this.minSH = minSH;
    }

    public int getMaxSH() {
        return maxSH;
    }

    public void setMaxSH(int maxSH) {
        this.maxSH = maxSH;
    }

    public String getAdditionalLoad() {
        return additionalLoad;
    }

    public void setAdditionalLoad(int additionalLoad) {
        switch (additionalLoad){
            case 1:
                this.additionalLoad ="groß";
                break;
            case 2:
                this.additionalLoad ="mittel";
                break;
            case 3:
                this.additionalLoad ="gering";
                break;
            default:
                this.additionalLoad ="groß";
                break;

        }
    }
}
