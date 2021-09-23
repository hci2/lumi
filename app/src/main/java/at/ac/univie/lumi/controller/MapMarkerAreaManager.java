package at.ac.univie.lumi.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import at.ac.univie.lumi.model.AvalancheBulletinTyrol;
import at.ac.univie.lumi.model.AvalancheSlopeAreaBulletinTyrol;
import at.ac.univie.lumi.model.MapMarker;
import at.ac.univie.lumi.R;
import at.ac.univie.lumi.model.SlopeAreaMapMarker;
import at.ac.univie.lumi.view.AvalancheBulletinActivity;
import at.ac.univie.lumi.view.MapActivity;
import at.ac.univie.lumi.view.OverviewActivity;
import at.ac.univie.lumi.view.SplashLoadingActivity;

import static at.ac.univie.lumi.view.MapActivity.getMap;

/**
 * Created by phili on 5/21/2017.
 *
 * The class is used to create map marker model objects and draw them on the map.
 */

public class MapMarkerAreaManager extends AsyncTask<Context,Void,Void>{

    private IconFactory iconFactory;

    private int weatherstationId =0;
    private Icon iconWeatherStation;
    //private List<MapMarker> listWeatherStation;

    private int webcamId =0;
    private Icon iconWebcam;
    //private List<MapMarker> listWebcam;

    private int observationId =0;
    private Icon iconObservation;
    //private List<MapMarker> listObservation;

    private AvalancheBulletinTyrol avalancheBulletinTyrol;
    private int avalancheBulletinAreaDangerLvlId =1;
    private Icon iconAvalancheDangerUnknown;
    private Icon iconAvalancheDangerLvl1;
    private Icon iconAvalancheDangerLvl2;
    private Icon iconAvalancheDangerLvl3;
    private Icon iconAvalancheDangerLvl4;
    private Icon iconAvalancheDangerLvl5;

    private Icon iconGeneralAvalancheDangerLvl1;
    private Icon iconGeneralAvalancheDangerLvl2;
    private Icon iconGeneralAvalancheDangerLvl3;
    private Icon iconGeneralAvalancheDangerLvl4;
    private Icon iconGeneralAvalancheDangerLvl5;
    private ArrayList<SlopeAreaMapMarker> listMapMarkers;

    ArrayList<Marker> removedSlopeDangerLvlMarkers;

    private Activity currentActivity;

    /**
     * The constructor is used to set the current activity. In this case for the map activity.
     * @param currentActivity The map activity.
     */

    public MapMarkerAreaManager(Activity currentActivity) { //MapActivity approach
        this.currentActivity = currentActivity;
        iconFactory = IconFactory.getInstance(currentActivity.getApplicationContext());
    }

    /**
     * The constructor is used to set the current activity and the avalanche bulletin.
     * @param currentActivity The current invoking activity.
     * @param avalancheBulletinTyrol The current avalanche bulletin.
     */

    public MapMarkerAreaManager(Activity currentActivity, AvalancheBulletinTyrol avalancheBulletinTyrol) {
        this.currentActivity = currentActivity;
        this.avalancheBulletinTyrol = avalancheBulletinTyrol;
    }

    /**
     * The method is used to execute the creating of the markers and drawing in a background thread.
     * @param params The context of the invoking activity which is used to create the icons.
     * @return empty
     */

    @Override
    protected Void doInBackground(Context... params) {
        // Create an Icon object for the marker to use
        iconFactory = IconFactory.getInstance(params[0]);
        iconWeatherStation = iconFactory.fromResource(R.drawable.marker_weatherstation);
        iconWebcam = iconFactory.fromResource(R.drawable.marker_webcam);
        iconObservation = iconFactory.fromResource(R.drawable.marker_avalanchebulletinobservation);
        iconAvalancheDangerUnknown = iconFactory.fromResource(R.drawable.marker_slopedangerlvl_unknown);
        iconAvalancheDangerLvl1 = iconFactory.fromResource(R.drawable.marker_slopedangerlvl1);
        iconAvalancheDangerLvl2 = iconFactory.fromResource(R.drawable.marker_slopedangerlvl2);
        iconAvalancheDangerLvl3 = iconFactory.fromResource(R.drawable.marker_slopedangerlvl3);
        iconAvalancheDangerLvl4 = iconFactory.fromResource(R.drawable.marker_slopedangerlvl4);
        iconAvalancheDangerLvl5 = iconFactory.fromResource(R.drawable.marker_slopedangerlvl5);

        iconGeneralAvalancheDangerLvl1 = iconFactory.fromResource(R.drawable.marker_gefahrenstufen_1);
        iconGeneralAvalancheDangerLvl2 = iconFactory.fromResource(R.drawable.marker_gefahrenstufen_2);
        iconGeneralAvalancheDangerLvl3 = iconFactory.fromResource(R.drawable.marker_gefahrenstufen_3);
        iconGeneralAvalancheDangerLvl4 = iconFactory.fromResource(R.drawable.marker_gefahrenstufen_4);
        iconGeneralAvalancheDangerLvl5 = iconFactory.fromResource(R.drawable.marker_gefahrenstufen_5);

        if(currentActivity instanceof MapActivity){
            listMapMarkers = new ArrayList<>();
            File fileListMapMarkers= new File(currentActivity.getFilesDir(),ActivityConstants.MapMarkerListFileName);
            if(fileListMapMarkers.exists()){
                listMapMarkers = DAOSerialisationManager.readSerializedFile(fileListMapMarkers.getAbsolutePath());
            }else{
                listMapMarkers=createAllSlopeAreaMapMarkers();
            }
        }else {
            listMapMarkers=createAllSlopeAreaMapMarkers();
            //ArrayList<SlopeAreaMapMarker>

            //serialize MapMarker list
            File fileListMapMarkers= new File(currentActivity.getFilesDir(),ActivityConstants.MapMarkerListFileName);

            DAOSerialisationManager.removeSerializedFile(fileListMapMarkers.getAbsolutePath());
            DAOSerialisationManager.saveSerializedFile(listMapMarkers, fileListMapMarkers.getAbsolutePath());
            if(currentActivity instanceof OverviewActivity){
                ((OverviewActivity)currentActivity).setMapMarkers(listMapMarkers);
            } else if(currentActivity instanceof SplashLoadingActivity){
                SplashLoadingActivity.asyncMapMarkerAreaManager=true;
            }
        }
        return null;
    }

    /**
     * The method is only used when the map activity is invoking the this class. It draws the markers on the map and set the marker onClick method.
     * @param aVoid empty
     */

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(currentActivity instanceof MapActivity){
            drawAllMapMarker();
            setMapMarkerOnClick();
        }
    }

    /**
     * The method draws the map markers on the map.
     */

    public void drawAllMapMarker(){
        for(int i=0;i<listMapMarkers.size();i++) {
            if(listMapMarkers.get(i).getType().equalsIgnoreCase("avalanchebulletin")){
                getMap().addMarker(new MarkerOptions()
                        .position(listMapMarkers.get(i).getLatLng())
                        .title(listMapMarkers.get(i).getName())
                        .snippet(listMapMarkers.get(i).getType())
                        .icon(iconFactory.fromBitmap(iconAvalancheDangerUnknown.getBitmap())));
                //.icon(iconFactory.fromBitmap(listMapMarkers.get(i).getBitmap())));
            } else{
                getMap().addMarker(new MarkerOptions()
                        .position(listMapMarkers.get(i).getLatLng())
                        .title(listMapMarkers.get(i).getName())
                        .snippet(listMapMarkers.get(i).getType())
                        .icon(iconFactory.fromBitmap(listMapMarkers.get(i).getBitmap())));
            }
        }
    }

    /**
     * This method set the the marker on click action.
     */

    public void setMapMarkerOnClick(){
        //set Marker on click Listener
        getMap().setOnMarkerClickListener((MapActivity)currentActivity);
        ((MapActivity)currentActivity).asyncTaskMapMarkerAreaManager=true;
    }

    /**
     * This method updates the map markers with the general danger lvl icons for the first view of the map.
     * @param abt The avalanche bulletin which is needed for setting the icons.
     */

    public void updateGeneralMapMarkerDangerLvl(AvalancheBulletinTyrol abt) {
        for(int u=0;u<listMapMarkers.size();u++){
            if (listMapMarkers.get(u).getType().equalsIgnoreCase("avalanchebulletin")) {
                for (int o = 0; o < getMap().getMarkers().size(); o++) {
                    if (getMap().getMarkers().get(o).getSnippet().equalsIgnoreCase("avalanchebulletin")) {
                        if(getMap().getMarkers().get(o).getTitle().equalsIgnoreCase(listMapMarkers.get(u).getName())){
                            switch (listMapMarkers.get(u).getDangerLvl()){
                                case 1:
                                    getMap().getMarkers().get(o).setIcon(iconGeneralAvalancheDangerLvl1);
                                    getMap().updateMarker(getMap().getMarkers().get(o));
                                    break;
                                case 2:
                                    getMap().getMarkers().get(o).setIcon(iconGeneralAvalancheDangerLvl2);
                                    getMap().updateMarker(getMap().getMarkers().get(o));
                                    break;
                                case 3:
                                    getMap().getMarkers().get(o).setIcon(iconGeneralAvalancheDangerLvl3);
                                    getMap().updateMarker(getMap().getMarkers().get(o));
                                    break;
                                case 4:
                                    getMap().getMarkers().get(o).setIcon(iconGeneralAvalancheDangerLvl4);
                                    getMap().updateMarker(getMap().getMarkers().get(o));
                                    break;
                                case 5:
                                    getMap().getMarkers().get(o).setIcon(iconGeneralAvalancheDangerLvl5);
                                    getMap().updateMarker(getMap().getMarkers().get(o));
                                    break;
                                default:
                                    break;
                            }

                        }

                    }
                }

            }
        }

        //draw observation markers
        for(int i=0;i<listMapMarkers.size();i++) {
            if(listMapMarkers.get(i).getType().equalsIgnoreCase("observation")){
                getMap().addMarker(new MarkerOptions()
                        .position(listMapMarkers.get(i).getLatLng())
                        .title(listMapMarkers.get(i).getName())
                        .snippet(listMapMarkers.get(i).getType())
                        .icon(iconFactory.fromBitmap(listMapMarkers.get(i).getBitmap())));
            }
        }

    }

    /**
     * This method updates the map markers with the current slope specific icons.
     * @param abt The avalanche bulletin which is needed for setting the icons.
     */

    public void updateMapMarkerDangerLvl(AvalancheBulletinTyrol abt){
        for(int u=0;u<listMapMarkers.size();u++){
            if (listMapMarkers.get(u).getType().equalsIgnoreCase("avalanchebulletin")) {
                for (int o = 0; o < getMap().getMarkers().size(); o++) {
                    if (getMap().getMarkers().get(o).getSnippet().equalsIgnoreCase("avalanchebulletin")) {
                        if(getMap().getMarkers().get(o).getTitle().equalsIgnoreCase(listMapMarkers.get(u).getName())){
                            switch (listMapMarkers.get(u).getDangerLvl()){
                                case 1:
                                    getMap().getMarkers().get(o).setIcon(iconAvalancheDangerLvl1);
                                    getMap().updateMarker(getMap().getMarkers().get(o));
                                    break;
                                case 2:
                                    getMap().getMarkers().get(o).setIcon(iconAvalancheDangerLvl2);
                                    getMap().updateMarker(getMap().getMarkers().get(o));
                                    break;
                                case 3:
                                    getMap().getMarkers().get(o).setIcon(iconAvalancheDangerLvl3);
                                    getMap().updateMarker(getMap().getMarkers().get(o));
                                    break;
                                case 4:
                                    getMap().getMarkers().get(o).setIcon(iconAvalancheDangerLvl4);
                                    getMap().updateMarker(getMap().getMarkers().get(o));
                                    break;
                                case 5:
                                    getMap().getMarkers().get(o).setIcon(iconAvalancheDangerLvl5);
                                    getMap().updateMarker(getMap().getMarkers().get(o));
                                    break;
                                default:
                                    break;
                            }

                        }

                    }
                }

            }
        }
    }


    /**
     * This method removes all avalanche bulletin map markers which are lower than the highest ones.
     */

    public void removeAvalancheBulletinsBelowHighestValue(){
        removedSlopeDangerLvlMarkers= new ArrayList<>();
        int maxDangerlvl=0;

        //get highest available danger lvl
        for(int i= 0; i< getMap().getMarkers().size(); i++){
            if(getMap().getMarkers().get(i).getSnippet().equalsIgnoreCase("avalanchebulletin")){
                for(int y=0; y<listMapMarkers.size();y++ ){
                    if(listMapMarkers.get(y).getType().equalsIgnoreCase("avalanchebulletin")){
                        if(getMap().getMarkers().get(i).getTitle().equalsIgnoreCase(listMapMarkers.get(y).getType())){
                            if(maxDangerlvl<listMapMarkers.get(y).getDangerLvl()){
                                maxDangerlvl=listMapMarkers.get(y).getDangerLvl();
                            }
                        }
                    }
                }

            }
        }

        //remove all map icons other below max danger lvl
        for(int i= 0; i< getMap().getMarkers().size(); i++){
            if(getMap().getMarkers().get(i).getSnippet().equalsIgnoreCase("avalanchebulletin")){
                for(int y=0; y<listMapMarkers.size();y++ ){
                    if(listMapMarkers.get(y).getType().equalsIgnoreCase("avalanchebulletin")){
                        if(getMap().getMarkers().get(i).getTitle().equalsIgnoreCase(listMapMarkers.get(y).getType())){
                            if(listMapMarkers.get(y).getDangerLvl()<maxDangerlvl){
                                removedSlopeDangerLvlMarkers.add(getMap().getMarkers().get(i));
                                getMap().getMarkers().get(i).remove();
                            }
                        }
                    }
                }

            }
        }

    }

    /**
     * This method adds all removed map markers.
     */

    public void addAvalancheBulletinsBelowHighestValue(AvalancheBulletinTyrol abt){
        Log.i("UPDATE", "add removed Markers");
        if(removedSlopeDangerLvlMarkers.size()!=0){
            for(int i=0; i<removedSlopeDangerLvlMarkers.size();i++){
                getMap().addMarker(new MarkerOptions()
                        .position(removedSlopeDangerLvlMarkers.get(i).getPosition())
                        .title(removedSlopeDangerLvlMarkers.get(i).getTitle())
                        .snippet(removedSlopeDangerLvlMarkers.get(i).getSnippet())
                        .icon(iconFactory.fromBitmap(removedSlopeDangerLvlMarkers.get(i).getIcon().getBitmap())));
            }
        }
    }

    /**
     * This method create the model for the map markers.
     * @return Returns an arraylist of slopeareamapmarker objects.
     */

    private ArrayList<SlopeAreaMapMarker> createAllSlopeAreaMapMarkers(){
        ArrayList<SlopeAreaMapMarker> listMapMarkers = new ArrayList<>();

        //create weather station markers
        listMapMarkers.add(new SlopeAreaMapMarker(weatherstationId++,"Wetterstation Snowpillow","weatherstation", new LatLng(47.168194444444445, 11.638499999999999), iconWeatherStation.getBitmap()));
        listMapMarkers.add(new SlopeAreaMapMarker(weatherstationId++,"Wetterstation Tarntalerboden","weatherstation", new LatLng(47.15311111111111, 11.622), iconWeatherStation.getBitmap()));

        //create webcam markers
        listMapMarkers.add(new SlopeAreaMapMarker(webcamId++,"Tarntaler Köpfe Westausblick","webcam", new LatLng(47.156789, 11.614394), iconWebcam.getBitmap()));

        //create user observation markers depending on scenario
        if(avalancheBulletinTyrol!=null){
            String currentScenario=avalancheBulletinTyrol.getDateTimeString().substring(0,4)+"-"+avalancheBulletinTyrol.getDateTimeString().substring(4,6)+"-"+avalancheBulletinTyrol.getDateTimeString().substring(6,8);
            switch (currentScenario){
                case ActivityConstants.Scenario1:
                    listMapMarkers.add(new SlopeAreaMapMarker(observationId++,"Lawinensprengung Pluderling","observation", new LatLng(47.140220, 11.640266), iconObservation.getBitmap()));
                    break;
                case ActivityConstants.Scenario2:
                    break;
                case ActivityConstants.Scenario3:
                    break;
                case ActivityConstants.Scenario3Fake:
                    break;
                default:
                    break;
            }
        }

        listMapMarkers.addAll(createAvalancheBulletinSlopeAreaMapMarkers());

        return listMapMarkers;

    }

    /**
     * This method creates the model object for all avalanche bulletin map markers.
     * @return Returns an arraylist of the model objects.
     */

    private ArrayList<SlopeAreaMapMarker> createAvalancheBulletinSlopeAreaMapMarkers(){
        ArrayList<SlopeAreaMapMarker> slopeAreaMapMarkerArrayList = new ArrayList<>();
        SlopeAreaMapMarker tempSlopeAreaMapMarker;
        InputStream inputStream = null;
        avalancheBulletinAreaDangerLvlId=1;
        try {
            inputStream = currentActivity.getAssets().open("slopeareas_data.dat");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            String[] inputLineArray;
            while ((inputLine = reader.readLine()) != null) {
                //skip commentar lines in .dat file
                if(inputLine.startsWith("//")){
                    continue;
                }

                tempSlopeAreaMapMarker = new SlopeAreaMapMarker();
                inputLineArray = inputLine.split(" ");

                tempSlopeAreaMapMarker.setLatLng(new LatLng(Double.parseDouble(inputLineArray[0]),Double.parseDouble(inputLineArray[1])));
                tempSlopeAreaMapMarker.setExpositionNumber(Integer.parseInt(inputLineArray[2]));
                tempSlopeAreaMapMarker.setMapboxId(Integer.parseInt(inputLineArray[3]));
                tempSlopeAreaMapMarker.setSlopeAngleNumber(Integer.parseInt(inputLineArray[4]));
                tempSlopeAreaMapMarker.setMaxSH(Integer.parseInt(inputLineArray[5]));
                tempSlopeAreaMapMarker.setMinSH(Integer.parseInt(inputLineArray[6]));
                tempSlopeAreaMapMarker.setId(avalancheBulletinAreaDangerLvlId);
                tempSlopeAreaMapMarker.setName("Gebiet "+avalancheBulletinAreaDangerLvlId);
                tempSlopeAreaMapMarker.setType("avalanchebulletin");

                int iconType=0;
                if(avalancheBulletinTyrol!=null){
                    //set slope danger lvl
                    Calendar currentTime = Calendar.getInstance();


                    if(currentTime.get(Calendar.HOUR_OF_DAY)>=0 && currentTime.get(Calendar.HOUR_OF_DAY)<=11){ //is morning
                        if(avalancheBulletinTyrol.getRegion6DangerLevelMorningBorder()>tempSlopeAreaMapMarker.getMaxSH() && avalancheBulletinTyrol.getRegion6DangerLevelMorningBorder()>tempSlopeAreaMapMarker.getMinSH()){
                            tempSlopeAreaMapMarker.setDangerLvl(avalancheBulletinTyrol.getRegion6DangerLevelMorningBorderBelow());
                            iconType=tempSlopeAreaMapMarker.getDangerLvl();
                        } else{ //slope area is above border value
                            tempSlopeAreaMapMarker.setDangerLvl(avalancheBulletinTyrol.getRegion6DangerLevelMorningBorderAbove());
                            iconType=tempSlopeAreaMapMarker.getDangerLvl();
                        }
                    } else { // afternoon
                        if(avalancheBulletinTyrol.getRegion6DangerLevelAfternoonBorder()>tempSlopeAreaMapMarker.getMaxSH() && avalancheBulletinTyrol.getRegion6DangerLevelAfternoonBorder()>tempSlopeAreaMapMarker.getMinSH()){
                            tempSlopeAreaMapMarker.setDangerLvl(avalancheBulletinTyrol.getRegion6DangerLevelAfternoonBorderBelow());
                            iconType=tempSlopeAreaMapMarker.getDangerLvl();
                        } else{ //slope area is above border value
                            tempSlopeAreaMapMarker.setDangerLvl(avalancheBulletinTyrol.getRegion6DangerLevelAfternoonBorderAbove());
                            iconType=tempSlopeAreaMapMarker.getDangerLvl();
                        }
                    }

                    //set slope specific values
                    SlopeDangerLvlProblemsCalculater slopeDangerLvlProblemsCalculater = new SlopeDangerLvlProblemsCalculater(avalancheBulletinTyrol);
                    for(int i=0;i<avalancheBulletinTyrol.getAvalancheSlopeAreaBulletinTyrolArrayList().size();i++){
                        if(avalancheBulletinTyrol.getAvalancheSlopeAreaBulletinTyrolArrayList().get(i).getRegionName().equalsIgnoreCase(tempSlopeAreaMapMarker.getName())){
                            tempSlopeAreaMapMarker.setSlopeAmountDangerZones(slopeDangerLvlProblemsCalculater.calculateSlopeAmountDangerZones(avalancheBulletinTyrol.getAvalancheSlopeAreaBulletinTyrolArrayList().get(i)));
                            tempSlopeAreaMapMarker.setAdditionalLoad(slopeDangerLvlProblemsCalculater.calculateSlopeAdditionalLoad());
                        }
                    }
                }


                //set image
                switch (iconType){
                    case 0:
                        tempSlopeAreaMapMarker.setBitmap(iconAvalancheDangerUnknown.getBitmap());
                        tempSlopeAreaMapMarker.setDangerLvl(0);
                        break;
                    case 1:
                        tempSlopeAreaMapMarker.setBitmap(iconAvalancheDangerLvl1.getBitmap());
                        tempSlopeAreaMapMarker.setDangerLvl(1);
                        break;
                    case 2:
                        tempSlopeAreaMapMarker.setBitmap(iconAvalancheDangerLvl2.getBitmap());
                        tempSlopeAreaMapMarker.setDangerLvl(2);
                        break;
                    case 3:
                        tempSlopeAreaMapMarker.setBitmap(iconAvalancheDangerLvl3.getBitmap());
                        tempSlopeAreaMapMarker.setDangerLvl(3);
                        break;
                    case 4:
                        tempSlopeAreaMapMarker.setBitmap(iconAvalancheDangerLvl4.getBitmap());
                        tempSlopeAreaMapMarker.setDangerLvl(4);
                        break;
                    case 5:
                        tempSlopeAreaMapMarker.setBitmap(iconAvalancheDangerLvl5.getBitmap());
                        tempSlopeAreaMapMarker.setDangerLvl(5);
                        break;
                    default:
                        tempSlopeAreaMapMarker.setBitmap(iconAvalancheDangerUnknown.getBitmap());
                        tempSlopeAreaMapMarker.setDangerLvl(0);
                        break;
                }

                slopeAreaMapMarkerArrayList.add(tempSlopeAreaMapMarker);
                avalancheBulletinAreaDangerLvlId++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return slopeAreaMapMarkerArrayList;
    }


}
