package at.ac.univie.lumi.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by phili on 5/11/2017.
 *
 * This class is part of the model and contains the avalanche problem of the avalanche warning service Tyrol. Moreover, it is part of the avalanche bulletin model. It will be also serialized on the smartphone of the user.
 */

public class AvalancheProblemTyrol implements Serializable {

    private static final long serialVersionUID = 0L;

    private String avalancheProblem;
    private ArrayList<Integer> expositions;
    // transient to not seriazable bitmaps https://stackoverflow.com/questions/11010386/send-bitmap-using-intent-android
    private transient Bitmap expositionImage;
    private byte[] expositionImageByteArray;
    private transient Bitmap elevationImage;
    private byte[] elevationImageByteArray;
    private int border;
    private boolean avalancheProblemLevelBelow;
    private boolean avalancheProblemLevelAbove;
    private String description;

    /**
     * The constructor initiate the different arraylist objects, set some boolean to the value false and set the avalanche problem type.
     */

    public AvalancheProblemTyrol(String avalancheProblemType) {
        expositions =new ArrayList<>();
        avalancheProblemLevelBelow =false;
        avalancheProblemLevelAbove = false;
        setAvalancheProblem(avalancheProblemType);
    }

    /**
     * The different getter and setter methods of every variable of the avalanche problem model.
     *
     */

    public Bitmap getExpositionImage() {
        //create bitmap of byte array
        expositionImage = BitmapFactory.decodeByteArray(expositionImageByteArray, 0, expositionImageByteArray.length);

        return expositionImage;
    }

    public void setExpositionImage(Bitmap expositionImage) {
        this.expositionImage = expositionImage;

        //set byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        expositionImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        expositionImageByteArray = stream.toByteArray();
    }

    public Bitmap getElevationImage() {
        //create bitmap of byte array
        elevationImage = BitmapFactory.decodeByteArray(elevationImageByteArray, 0, elevationImageByteArray.length);

        return elevationImage;
    }

    public void setElevationImage(Bitmap elevationImage) {
        this.elevationImage = elevationImage;

        //set byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        elevationImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        elevationImageByteArray = stream.toByteArray();
    }

    public String getAvalancheProblem() {
        return avalancheProblem;
    }

    public void setAvalancheProblem(String avalancheProblem) {
        this.avalancheProblem = avalancheProblem;
    }

    public ArrayList<Integer> getExpositions() {
        return expositions;
    }

    public void setExpositions(ArrayList<Integer> expositions) {
        this.expositions = expositions;
    }

    public int getBorder() {
        return border;
    }

    public void setBorder(int border) {
        this.border = border;
    }

    public boolean isAvalancheProblemLevelBelow() {
        return avalancheProblemLevelBelow;
    }

    public void setAvalancheProblemLevelBelow(boolean avalancheProblemLevelBelow) {
        this.avalancheProblemLevelBelow = avalancheProblemLevelBelow;
    }

    public boolean isAvalancheProblemLevelAbove() {
        return avalancheProblemLevelAbove;
    }

    public void setAvalancheProblemLevelAbove(boolean avalancheProblemLevelAbove) {
        this.avalancheProblemLevelAbove = avalancheProblemLevelAbove;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
