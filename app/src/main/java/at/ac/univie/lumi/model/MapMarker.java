package at.ac.univie.lumi.model;

//import com.mapbox.mapboxsdk.annotations.Icon;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by phili on 5/21/2017.
 *
 * This class is part of the model and contains the map marker model.  It will be also serialized on the smartphone of the user.
 */

public class MapMarker implements Serializable {

    private static final long serialVersionUID = 0L;

    private int id;
    private String name;
    private String type;
    private transient LatLng latLng;
    private double latitude;
    private double longitude;
    private transient Bitmap bitmap;
    private byte[] bitmapByteArray;

    /**
     * This constructor is used to initialize an empty map marker.
     */

    public MapMarker() {
    }

    /**
     * This constructor is used to initialize a map marker where all variables are set.
     */

    public MapMarker(int id, String name, String type, LatLng latLng, Bitmap bitmap) {
        this.id = id;
        this.name = name;
        this.type = type;
        setLatLng(latLng);
        setBitmap(bitmap);
    }

    /**
     * The different getter and setter methods of every variable of the map marker model.
     *
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LatLng getLatLng() {
        return latLng = new LatLng(latitude,longitude);
    }

    public void setLatLng(LatLng latLng) {
        this.latitude=latLng.getLatitude();
        this.longitude=latLng.getLongitude();
        this.latLng = latLng;
    }

    public Bitmap getBitmap() {
        bitmap = BitmapFactory.decodeByteArray(bitmapByteArray, 0, bitmapByteArray.length);

        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;

        //set byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        bitmapByteArray = stream.toByteArray();
    }
}
