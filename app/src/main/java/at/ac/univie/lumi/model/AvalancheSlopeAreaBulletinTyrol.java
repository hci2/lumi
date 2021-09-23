package at.ac.univie.lumi.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by phili on 6/12/2017.
 *
 *  This class is part of the model and contains the avalanche slope area information of the avalanche warning service Tyrol.
 *  Moreover, it is part of the avalanche bulletin model. It will be also serialized on the smartphone of the user.
 */

public class AvalancheSlopeAreaBulletinTyrol implements Serializable {

    private static final long serialVersionUID = 0L;

    private String regionName;
    private int id;

    private int slopeDangerLvl;

    private int slopeAmountDangerZones;

    private int minSH;
    private int maxSH;

    private int expositionNumber;
    private String expositionString;

    private int mapboxId;

    private int slopeAngleNumber;
    private String slopeAngleString;

    private ArrayList<String> listAvalancheProblems;
    private ArrayList<String> listAvalancheProblemsExposition;


    private String additionalLoad;

    /**
     * The constructor initiate the different arraylist objects.
     */

    public AvalancheSlopeAreaBulletinTyrol(){
        listAvalancheProblems = new ArrayList<>();
        listAvalancheProblemsExposition = new ArrayList<>();
    }

    /**
     * The different getter and setter methods of every variable of the avalanche slope area model.
     *
     */

    public int getSlopeAmountDangerZones() {
        return slopeAmountDangerZones;
    }

    public void setSlopeAmountDangerZones(int slopeAmountDangerZones) {
        this.slopeAmountDangerZones = slopeAmountDangerZones;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public int getSlopeDangerLvl() {
        return slopeDangerLvl;
    }

    public void setSlopeDangerLvl(int slopeDangerLvl) {
        this.slopeDangerLvl = slopeDangerLvl;
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
                this.expositionString="-";
                break;
        }
    }

    public String getExpositionString() {
        return expositionString;
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
                this.slopeAngleString="mässig steil (<30°)";
                break;
        }
    }

    public String getSlopeAngleString() {
        return slopeAngleString;
    }

    public ArrayList<String> getListAvalancheProblems() {
        return listAvalancheProblems;
    }

    public void setListAvalancheProblems(ArrayList<String> listAvalancheProblems) {
        this.listAvalancheProblems = listAvalancheProblems;
    }

    public ArrayList<String> getListAvalancheProblemsExposition() {
        return listAvalancheProblemsExposition;
    }

    public void setListAvalancheProblemsExposition(ArrayList<String> listAvalancheProblemsExposition) {
        this.listAvalancheProblemsExposition = listAvalancheProblemsExposition;
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
