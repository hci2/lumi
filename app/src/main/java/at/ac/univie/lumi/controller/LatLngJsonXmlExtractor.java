package at.ac.univie.lumi.controller;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.services.android.telemetry.location.LocationEngine;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

import at.ac.univie.lumi.model.AvalancheBulletinTyrol;
import at.ac.univie.lumi.model.AvalancheProblemTyrol;
import at.ac.univie.lumi.model.AvalancheSlopeAreaBulletinTyrol;
import at.ac.univie.lumi.view.EmergencyCallActivity;
import at.ac.univie.lumi.view.MapActivity;

/**
 * Created by phili on 5/19/2017.
 *
 * The class is made of two different subclasses. The RestApiJSONAltitudeGetter static class is used to get the current altitude of the user´s position via google elevation api get request. the response contains a json file with the altitude.
 * The RestApiXmlLWDTyrolGetter downloads the avalanche bulletin of the avalanche warning service Tyrol and then extracts the fields of the xml and create an avalanchebulletin model.
 */

public class LatLngJsonXmlExtractor {

    //for json
    //in degrees
    public static double latitude;
    //in degrees
    public static double longitude;
    public static double altitude;
    public static Location lastLocation;
    public static final String GOOGLE_ACCESS_KEY="TODO:INSERT_GOOGLE_ACCESS_KEY";
    public static StringBuffer responseJSON;
    private static EmergencyCallActivity emergencyCallActivity;
    private static LocationEngine locationEngine;

    //for xml extraction
    private static XmlPullParserFactory xmlParserFactory;
    private static XmlPullParser xmlParser;
    private static InputStream stream;
    private static final String URL_LWD_TIROL_DE="https://lawine.tirol.gv.at/rest/bulletin/latest/xml/de";//"lawine.tirol.gv.at/rest/bulletin/latest/xml/de";
    private static ProgressDialog pDialog;
    private static AvalancheBulletinTyrol avalancheBulletinTyrol = new AvalancheBulletinTyrol();
    private static ArrayList<String> lwdReportList=new ArrayList<>();


    //helper vars
    private static Calendar calendar;
    private static int commentCounter=0;
    private static int timePositionCounter=0;
    private static int mainValueCounter=0;
    private static boolean timePositionSkipFirstTime=false;
    private static boolean morningRegionalDangerRating=false;
    private static String tempBorder;
    private static boolean belowBorderRegionalDangerRating=false;
    private static AvalancheProblemTyrol tempAvalancheProblem1;
    private static AvalancheProblemTyrol tempAvalancheProblem2;
    private static AvalancheProblemTyrol tempAvalancheProblem3;
    private static int problemCounter=0;
    private static String avalancheProblemType="";

    /**
     * This method starts the location engine to start searching for the gps position of the user and creates an instance of the RestApiJSONAltitudeGetter to get the altitude.
     * @param context The context is needed to check if the location request permission is granted.
     * @param activity The activity is needed for the request of the permission if it is not already granted.
     */

    public void requestLatLngAltitude(Context context, Activity activity){
        locationEngine = LocationSource.getLocationEngine(context);
        locationEngine.activate();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},   //request specific permission from user
                    9);
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},   //request specific permission from user
                    8);
        } else{

            lastLocation =locationEngine.getLastLocation();
            emergencyCallActivity = (EmergencyCallActivity) activity;
            tryPositionCheckStartElevationAPI();


        }
    }

    /**
     * This method starts a thread which check if the last location is not null and then start to initiate a class object of RestApiJSONAltitudeGetter to get the altitude.
     */

    private void tryPositionCheckStartElevationAPI() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // restart action after 2 seconds
                if (lastLocation != null) {
                    latitude = lastLocation.getLatitude();
                    longitude = lastLocation.getLongitude();
                    new RestApiJSONAltitudeGetter(emergencyCallActivity).execute();
                } else {
                    Intent intent= new Intent(emergencyCallActivity.getApplicationContext(), EmergencyCallActivity.class);
                    emergencyCallActivity.startActivity(intent);
                }
            }
        }, 1000);
    }

    /**
     * The class sends a get request via http to the google elevation api to get a json response containing the altitude.
     */

    private static class RestApiJSONAltitudeGetter extends AsyncTask<Void, Void, String>{

        private EmergencyCallActivity emergencyCallActivity;
        private boolean hasInternet =false;

        /**
         * The constructor initialize the emergencycall activity for later usage.
         * @param emergencyCallActivity The emergencycall activity object.
         */

        public RestApiJSONAltitudeGetter(EmergencyCallActivity emergencyCallActivity) {
            this.emergencyCallActivity = emergencyCallActivity;
        }

        /**
         * The main method of the AsyncTask thread which is not executed on the ui thread. The method check if there is an internet connection and then make a http request.
         * @param params empty
         * @return Returns the http response object as string.
         */

        @Override
        protected String doInBackground(Void... params) {
            hasInternet=isNetworkAvailable();
            if(hasInternet){
                try {
                    //double altitude = 0.0;

                    String urlString = "https://maps.googleapis.com/maps/api/elevation/"
                            + "json?locations=" + String.valueOf(latitude)
                            + "," + String.valueOf(longitude)
                            + "&key="
                            + GOOGLE_ACCESS_KEY;

                    URL url = new URL(urlString);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String inputLine;
                    responseJSON =  new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        responseJSON.append(inputLine);
                    }
                    in.close();
                } catch (Exception e) {

                    e.printStackTrace();
                }
                return responseJSON.toString();
            }else{ // no Internet
                return "nointernet";
            }
        }

        /**
         * The method which is called after the doinbackground method and extracts the altitude of the json object.
         * @param s The http response as string.
         */

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            double elevation =0.0;
            if(hasInternet){
                try {
                    JSONObject jsonObj = new JSONObject(s);
                    JSONArray resultEl = jsonObj.getJSONArray("results");
                    JSONObject current = resultEl.getJSONObject(0);
                    elevation = Double.parseDouble(current.getString("elevation"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                altitude= elevation;

                //emergencyCallActivity.getHeightContent().setText(String.format("%.2f", altitude)+" m");
                // emergencyCallActivity.getLatitudeContent().setText(String.valueOf(latitude)+"°");
                //emergencyCallActivity.getLongitudeContent().setText(String.valueOf(longitude)+"°");
            } else { // no Internet
                altitude= elevation;
            }
            emergencyCallActivity.updateLatLngAltitude();
        }

        /**
         * The method checks if an internet connection is available or not.
         * @return True or not depending on the internet connection.
         */

        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) emergencyCallActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    /**
     * The class which extracts the requested avalanche bulletin of the avalanche warning service Tyrol.
     */

    public static class RestApiXmlLWDTyrolGetter extends AsyncTask<String, Void, Void>{
        String urlString;
        Activity activity;
        private float windDirection;

        /**
         * The interface is used to send the avalanche bulletin back to the requested class.
         */

        public interface AsyncResponseXmlLWDTyrol{
            void getAsyncResult(AvalancheBulletinTyrol result, float windDirection);
        }

        public AsyncResponseXmlLWDTyrol delegate =null;

        /**
         * The constructor is used to set the varialbes and the activity.
         * @param delegate The variable delegates the avalanche bulletin to the requested class.
         * @param activity The activity object which is used for opening the slope data.
         */

        public RestApiXmlLWDTyrolGetter(AsyncResponseXmlLWDTyrol delegate, Activity activity) {
            this.delegate=delegate;
            this.activity = activity;
        }

        /**
         * The method which is executed in the background thread and downloads the avalanche bulletin and then extract the xml file and set the avalanche bulletin model
         * @param params The url for the http request.
         */

        @Override
        protected Void doInBackground(String... params) {
            try{
                this.urlString =params[0];
                //initiation of xml parser
                xmlParserFactory = XmlPullParserFactory.newInstance();
                xmlParser = xmlParserFactory.newPullParser();

                xmlParser.setInput(downloadUrl(urlString), null);

                //open file
                //stream=downloadUrl(URL_LWD_TIROL_DE);
                //xmlParser.setInput(stream,null);
                //xmlParser.setInput(getAssets().open("lwd_bulletin.xml"), null); //stream

                //parsing of file
                String text="";
                String attribute="";
                int event =xmlParser.getEventType();
                while (event!= XmlPullParser.END_DOCUMENT){
                    String name= xmlParser.getName();
                    switch (event){
                        case XmlPullParser.START_TAG:
                            Log.d("XML-START",name);
                        /* Another possible solution
                        if(name.equalsIgnoreCase("caaml:dateTimeReport")){
                            dateTimeReport=xmlParser.nextText();
                            titleLWDReport.setText(dateTimeReport);
                        }*/

                            //check some higher tags for right position
                            if(name.equals("caaml:ExtFile")){
                                attribute=xmlParser.getAttributeValue(null, "gml:id");
                            }
                            if(name.equalsIgnoreCase("caaml:locRef")){
                                attribute=xmlParser.getAttributeValue(null, "xlink:href");
                            }

                            break;
                        case XmlPullParser.TEXT:
                            if(!xmlParser.isWhitespace()){
                                text=xmlParser.getText();
                            }


                            break;
                        case XmlPullParser.END_TAG:
                            //set same essential vars, booleans
                            if(name.equalsIgnoreCase("caaml:beginPosition") && !text.equals("") && text.length()>4 && avalancheProblemType.equals("")){
                                if(text.substring(11,13).equals("00")){
                                    morningRegionalDangerRating=true;
                                }
                                else if(text.substring(11,13).equals("12")){
                                    morningRegionalDangerRating =false;
                                }
                            }
                            if(name.equalsIgnoreCase("caaml:endPosition") && !text.equals("") && text.length()>4 && avalancheProblemType.equals("")){
                                if(text.substring(11,13).equals("00")){
                                    morningRegionalDangerRating=true;
                                }
                                else if(text.substring(11,13).equals("12")){
                                    morningRegionalDangerRating =false;
                                }
                            }
                            if(name.equalsIgnoreCase("caaml:beginPosition") && !text.equals("") && text.length()<5 && avalancheProblemType.equals("")){
                                tempBorder =text;
                                belowBorderRegionalDangerRating=false;
                            }
                            if(name.equalsIgnoreCase("caaml:endPosition") && !text.equals("") && text.length()<5 && avalancheProblemType.equals("")){
                                tempBorder =text;
                                belowBorderRegionalDangerRating=true;
                            }



                            //set avalanchebulletingtyrol class + arraylist
                            if(name.equalsIgnoreCase("caaml:dateTimeReport") && !text.equals("")){
                                lwdReportList.add(text);
                                calendar=parseDateTime(text);

                                avalancheBulletinTyrol.setDateTime(parseDateTime(text));
                                avalancheBulletinTyrol.setDateTimeString(parseDateTimeToString(text));
                                text="";

                            } else if(name.equalsIgnoreCase("caaml:comment") && !text.equals("") && commentCounter<2){
                                lwdReportList.add(text);
                                switch (commentCounter){
                                    case 0:
                                        avalancheBulletinTyrol.setCreator(text);
                                        break;
                                    case 1:
                                        avalancheBulletinTyrol.setTendecyAvalancheSituation(text);
                                        break;
                                    default:

                                        break;
                                }
                                commentCounter++;
                                text="";

                            } else if(name.equalsIgnoreCase("caaml:comment") && !text.equals("") && avalancheBulletinTyrol!=null && commentCounter>=2){
                                lwdReportList.add(text);
                                switch (avalancheProblemType){
                                    case "drifting snow":
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1.setDescription(text);
                                                avalancheBulletinTyrol.getAvalancheProblemsTyrols().add(tempAvalancheProblem1);
                                                break;
                                            case 1:
                                                tempAvalancheProblem2.setDescription(text);
                                                avalancheBulletinTyrol.getAvalancheProblemsTyrols().add(tempAvalancheProblem2);
                                                break;
                                            case 2:
                                                tempAvalancheProblem3.setDescription(text);
                                                avalancheBulletinTyrol.getAvalancheProblemsTyrols().add(tempAvalancheProblem3);
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "gliding snow":
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1.setDescription(text);
                                                avalancheBulletinTyrol.getAvalancheProblemsTyrols().add(tempAvalancheProblem1);
                                                break;
                                            case 1:
                                                tempAvalancheProblem2.setDescription(text);
                                                avalancheBulletinTyrol.getAvalancheProblemsTyrols().add(tempAvalancheProblem2);
                                                break;
                                            case 2:
                                                tempAvalancheProblem3.setDescription(text);
                                                avalancheBulletinTyrol.getAvalancheProblemsTyrols().add(tempAvalancheProblem3);
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "new snow":
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1.setDescription(text);
                                                avalancheBulletinTyrol.getAvalancheProblemsTyrols().add(tempAvalancheProblem1);
                                                break;
                                            case 1:
                                                tempAvalancheProblem2.setDescription(text);
                                                avalancheBulletinTyrol.getAvalancheProblemsTyrols().add(tempAvalancheProblem2);
                                                break;
                                            case 2:
                                                tempAvalancheProblem3.setDescription(text);
                                                avalancheBulletinTyrol.getAvalancheProblemsTyrols().add(tempAvalancheProblem3);
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "old snow":
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1.setDescription(text);
                                                avalancheBulletinTyrol.getAvalancheProblemsTyrols().add(tempAvalancheProblem1);
                                                break;
                                            case 1:
                                                tempAvalancheProblem2.setDescription(text);
                                                avalancheBulletinTyrol.getAvalancheProblemsTyrols().add(tempAvalancheProblem2);
                                                break;
                                            case 2:
                                                tempAvalancheProblem3.setDescription(text);
                                                avalancheBulletinTyrol.getAvalancheProblemsTyrols().add(tempAvalancheProblem3);
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "wet snow":
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1.setDescription(text);
                                                avalancheBulletinTyrol.getAvalancheProblemsTyrols().add(tempAvalancheProblem1);
                                                break;
                                            case 1:
                                                tempAvalancheProblem2.setDescription(text);
                                                avalancheBulletinTyrol.getAvalancheProblemsTyrols().add(tempAvalancheProblem2);
                                                break;
                                            case 2:
                                                tempAvalancheProblem3.setDescription(text);
                                                avalancheBulletinTyrol.getAvalancheProblemsTyrols().add(tempAvalancheProblem3);
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    default:

                                        break;
                                }
                                if(problemCounter>0){
                                    problemCounter=0;
                                } else{
                                    problemCounter++;
                                }

                                text="";

                            }else if(name.equalsIgnoreCase("caaml:fileReferenceURI") && !text.equals("")){
                                lwdReportList.add(text);

                                switch (attribute){
                                    case "BulletinPhoto":
                                        avalancheBulletinTyrol.setLinkBulletinPhoto(text);
                                        Log.d("XML-URL",text);
                                        text="";
                                        break;
                                    case "DangerRatingMapColour":
                                        avalancheBulletinTyrol.setLinkDangerRatingMapColour(text);
                                        Log.d("XML-URL",text);
                                        text="";
                                        break;
                                    case "DangerRatingMapDetail":
                                        avalancheBulletinTyrol.setLinkDangerRatingMapDetail(text);
                                        text="";
                                        break;
                                    case "Problem1":
                                        avalancheBulletinTyrol.setAvalancheProblem1ImageLink(text);
                                        text="";
                                        break;
                                    case "Problem1Elevation":
                                        avalancheBulletinTyrol.setAvalancheProblem1ImageLinkElevation(text);
                                        text="";
                                        break;
                                    case "Problem1Aspect":
                                        avalancheBulletinTyrol.setAvalancheProblem1ImageLinkExposition(text);
                                        text="";
                                        break;
                                    case "Problem2":
                                        avalancheBulletinTyrol.setAvalancheProblem2ImageLink(text);
                                        text="";
                                        break;
                                    case "Problem2Elevation":
                                        avalancheBulletinTyrol.setAvalancheProblem2ImageLinkElevation(text);
                                        text="";
                                        break;
                                    case "Problem2Aspect":
                                        avalancheBulletinTyrol.setAvalancheProblem2ImageLinkExposition(text);
                                        text="";
                                        break;
                                    case "Problem3":
                                        avalancheBulletinTyrol.setAvalancheProblem3ImageLink(text);
                                        text="";
                                        break;
                                    case "Problem3Elevation":
                                        avalancheBulletinTyrol.setAvalancheProblem3ImageLinkElevation(text);
                                        text="";
                                        break;
                                    case "Problem3Aspect":
                                        avalancheBulletinTyrol.setAvalancheProblem3ImageLinkExposition(text);
                                        text="";
                                        break;
                                    case "Generallevel":
                                        avalancheBulletinTyrol.setLinkGeneralDangerLevelTyrol(text);
                                        text="";
                                        Log.d("XML-URL",text);
                                        break;
                                    case "Tendency":
                                        avalancheBulletinTyrol.setLinkTendency(text);
                                        text="";
                                        break;
                                    case "Pdf":
                                        avalancheBulletinTyrol.setLinkPdf(text);
                                        text="";
                                        break;
                                    default:
                                        break;
                                }

                            } else if(name.equalsIgnoreCase("caaml:highlights") && !text.equals("")){
                                lwdReportList.add(text);
                                avalancheBulletinTyrol.setHighlights(text);
                                text="";
                            } else if(name.equalsIgnoreCase("caaml:timePosition") && !text.equals("") && timePositionSkipFirstTime){
                                lwdReportList.add(text);
                                switch (timePositionCounter){
                                    case 0:
                                        avalancheBulletinTyrol.setPast3DangerLevelTimeString(parseDateToString(text));
                                        avalancheBulletinTyrol.setPast3DangerLevelTime(parseDate(text));
                                        timePositionCounter++;
                                        break;
                                    case 1:
                                        avalancheBulletinTyrol.setPast2DangerLevelTimeString(parseDateToString(text));
                                        avalancheBulletinTyrol.setPast2DangerLevelTime(parseDate(text));
                                        timePositionCounter++;
                                        break;
                                    case 2:
                                        avalancheBulletinTyrol.setPast1DangerLevelTimeString(parseDateToString(text));
                                        avalancheBulletinTyrol.setPast1DangerLevelTime(parseDate(text));
                                        timePositionCounter++;
                                        break;
                                    case 3:
                                        avalancheBulletinTyrol.setCurrentDangerLevelTimeString(parseDateToString(text));
                                        avalancheBulletinTyrol.setCurrentDangerLevelTime(parseDate(text));
                                        timePositionCounter++;
                                        break;
                                    case 4:
                                        avalancheBulletinTyrol.setFuture1DangerLevelTimeString(parseDateToString(text));
                                        avalancheBulletinTyrol.setFuture1DangerLevelTime(parseDate(text));
                                        timePositionCounter++;
                                        break;

                                    default:
                                        break;
                                }
                                text="";
                            } else if(name.equalsIgnoreCase("caaml:mainValue") && !text.equals("") && mainValueCounter <5){
                                lwdReportList.add(text);
                                switch (mainValueCounter){
                                    case 0:
                                        avalancheBulletinTyrol.setPast3DangerLevelValue(Integer.parseInt(text));
                                        mainValueCounter++;
                                        break;
                                    case 1:
                                        avalancheBulletinTyrol.setPast2DangerLevelValue(Integer.parseInt(text));
                                        mainValueCounter++;
                                        break;
                                    case 2:
                                        avalancheBulletinTyrol.setPast1DangerLevelValue(Integer.parseInt(text));
                                        mainValueCounter++;
                                        break;
                                    case 3:
                                        avalancheBulletinTyrol.setCurrentDangerLevelValue(Integer.parseInt(text));
                                        mainValueCounter++;
                                        break;
                                    case 4:
                                        avalancheBulletinTyrol.setFuture1DangerLevelValue(Integer.parseInt(text));
                                        mainValueCounter++;
                                        break;

                                    default:
                                        break;
                                }
                                text="";
                            } else if(name.equalsIgnoreCase("caaml:mainValue") && !text.equals("") && mainValueCounter >4 && morningRegionalDangerRating && !belowBorderRegionalDangerRating){
                                lwdReportList.add(attribute);
                                lwdReportList.add("Am Vormittag ab "+tempBorder);
                                lwdReportList.add(text);

                                switch (attribute){
                                    case "AT7R1":
                                        avalancheBulletinTyrol.setRegion1DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion1DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion1DangerLevelMorningBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R2":
                                        avalancheBulletinTyrol.setRegion2DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion2DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion2DangerLevelMorningBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R3":
                                        avalancheBulletinTyrol.setRegion3DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion3DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion3DangerLevelMorningBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R4":
                                        avalancheBulletinTyrol.setRegion4DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion4DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion4DangerLevelMorningBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R5":
                                        avalancheBulletinTyrol.setRegion5DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion5DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion5DangerLevelMorningBorderAbove(Integer.parseInt(text));

                                        break;
                                    case "AT7R6":
                                        avalancheBulletinTyrol.setRegion6DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion6DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion6DangerLevelMorningBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R7":
                                        avalancheBulletinTyrol.setRegion7DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion7DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion7DangerLevelMorningBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R8":
                                        avalancheBulletinTyrol.setRegion8DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion8DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion8DangerLevelMorningBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R9":
                                        avalancheBulletinTyrol.setRegion9DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion9DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion9DangerLevelMorningBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R10":
                                        avalancheBulletinTyrol.setRegion10DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion10DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion10DangerLevelMorningBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R11":
                                        avalancheBulletinTyrol.setRegion11DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion11DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion11DangerLevelMorningBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R12":
                                        avalancheBulletinTyrol.setRegion12DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion12DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion12DangerLevelMorningBorderAbove(Integer.parseInt(text));
                                        break;

                                    default:
                                        break;
                                }
                                text="";
                            } else if(name.equalsIgnoreCase("caaml:mainValue") && !text.equals("") && mainValueCounter >4 && morningRegionalDangerRating && belowBorderRegionalDangerRating){
                                lwdReportList.add(attribute);
                                lwdReportList.add("Am Vormittag bis "+tempBorder);
                                lwdReportList.add(text);

                                switch (attribute){
                                    case "AT7R1":
                                        avalancheBulletinTyrol.setRegion1DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion1DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion1DangerLevelMorningBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R2":
                                        avalancheBulletinTyrol.setRegion2DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion2DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion2DangerLevelMorningBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R3":
                                        avalancheBulletinTyrol.setRegion3DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion3DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion3DangerLevelMorningBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R4":
                                        avalancheBulletinTyrol.setRegion4DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion4DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion4DangerLevelMorningBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R5":
                                        avalancheBulletinTyrol.setRegion5DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion5DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion5DangerLevelMorningBorderBelow(Integer.parseInt(text));

                                        break;
                                    case "AT7R6":
                                        avalancheBulletinTyrol.setRegion6DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion6DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion6DangerLevelMorningBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R7":
                                        avalancheBulletinTyrol.setRegion7DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion7DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion7DangerLevelMorningBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R8":
                                        avalancheBulletinTyrol.setRegion8DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion8DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion8DangerLevelMorningBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R9":
                                        avalancheBulletinTyrol.setRegion9DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion9DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion9DangerLevelMorningBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R10":
                                        avalancheBulletinTyrol.setRegion10DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion10DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion10DangerLevelMorningBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R11":
                                        avalancheBulletinTyrol.setRegion11DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion11DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion11DangerLevelMorningBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R12":
                                        avalancheBulletinTyrol.setRegion12DangerLevelMorningRegion(attribute);
                                        avalancheBulletinTyrol.setRegion12DangerLevelMorningBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion12DangerLevelMorningBorderBelow(Integer.parseInt(text));
                                        break;

                                    default:
                                        break;
                                }
                                text="";
                            } else if(name.equalsIgnoreCase("caaml:mainValue") && !text.equals("") && mainValueCounter >4 && !morningRegionalDangerRating && !belowBorderRegionalDangerRating){
                                lwdReportList.add(attribute);
                                lwdReportList.add("Am Nachmittag ab "+tempBorder);
                                lwdReportList.add(text);

                                switch (attribute){
                                    case "AT7R1":
                                        avalancheBulletinTyrol.setRegion1DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion1DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion1DangerLevelAfternoonBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R2":
                                        avalancheBulletinTyrol.setRegion2DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion2DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion2DangerLevelAfternoonBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R3":
                                        avalancheBulletinTyrol.setRegion3DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion3DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion3DangerLevelAfternoonBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R4":
                                        avalancheBulletinTyrol.setRegion4DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion4DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion4DangerLevelAfternoonBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R5":
                                        avalancheBulletinTyrol.setRegion5DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion5DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion5DangerLevelAfternoonBorderAbove(Integer.parseInt(text));

                                        break;
                                    case "AT7R6":
                                        avalancheBulletinTyrol.setRegion6DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion6DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion6DangerLevelAfternoonBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R7":
                                        avalancheBulletinTyrol.setRegion7DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion7DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion7DangerLevelAfternoonBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R8":
                                        avalancheBulletinTyrol.setRegion8DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion8DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion8DangerLevelAfternoonBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R9":
                                        avalancheBulletinTyrol.setRegion9DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion9DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion9DangerLevelAfternoonBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R10":
                                        avalancheBulletinTyrol.setRegion10DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion10DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion10DangerLevelAfternoonBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R11":
                                        avalancheBulletinTyrol.setRegion11DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion11DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion11DangerLevelAfternoonBorderAbove(Integer.parseInt(text));
                                        break;
                                    case "AT7R12":
                                        avalancheBulletinTyrol.setRegion12DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion12DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion12DangerLevelAfternoonBorderAbove(Integer.parseInt(text));
                                        break;

                                    default:
                                        break;
                                }
                                text="";
                            } else if(name.equalsIgnoreCase("caaml:mainValue") && !text.equals("") && mainValueCounter >4 && !morningRegionalDangerRating && belowBorderRegionalDangerRating){
                                lwdReportList.add(attribute);
                                lwdReportList.add("Am Nachmittag bis "+tempBorder);
                                lwdReportList.add(text);

                                switch (attribute){
                                    case "AT7R1":
                                        avalancheBulletinTyrol.setRegion1DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion1DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion1DangerLevelAfternoonBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R2":
                                        avalancheBulletinTyrol.setRegion2DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion2DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion2DangerLevelAfternoonBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R3":
                                        avalancheBulletinTyrol.setRegion3DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion3DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion3DangerLevelAfternoonBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R4":
                                        avalancheBulletinTyrol.setRegion4DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion4DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion4DangerLevelAfternoonBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R5":
                                        avalancheBulletinTyrol.setRegion5DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion5DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion5DangerLevelAfternoonBorderBelow(Integer.parseInt(text));

                                        break;
                                    case "AT7R6":
                                        avalancheBulletinTyrol.setRegion6DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion6DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion6DangerLevelAfternoonBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R7":
                                        avalancheBulletinTyrol.setRegion7DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion7DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion7DangerLevelAfternoonBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R8":
                                        avalancheBulletinTyrol.setRegion8DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion8DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion8DangerLevelAfternoonBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R9":
                                        avalancheBulletinTyrol.setRegion9DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion9DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion9DangerLevelAfternoonBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R10":
                                        avalancheBulletinTyrol.setRegion10DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion10DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion10DangerLevelAfternoonBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R11":
                                        avalancheBulletinTyrol.setRegion11DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion11DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion11DangerLevelAfternoonBorderBelow(Integer.parseInt(text));
                                        break;
                                    case "AT7R12":
                                        avalancheBulletinTyrol.setRegion12DangerLevelAfternoonRegion(attribute);
                                        avalancheBulletinTyrol.setRegion12DangerLevelAfternoonBorder(Integer.parseInt(tempBorder));
                                        avalancheBulletinTyrol.setRegion12DangerLevelAfternoonBorderBelow(Integer.parseInt(text));
                                        break;

                                    default:
                                        break;
                                }
                                text="";
                            } else if(name.equalsIgnoreCase("caaml:type") && !text.equals("") && text.length()<4){
                                text=text.replace("DP","GM");
                                lwdReportList.add(text);
                                avalancheBulletinTyrol.getDangerPattern().add(text);
                                text="";
                            } else if(name.equalsIgnoreCase("caaml:type") && !text.equals("")){
                                //tempBorder =text;
                                //belowBorderRegionalDangerRating=true;
                                avalancheProblemType =text;
                                switch (text){
                                    case "drifting snow":
                                        text="Triebschnee";
                                        lwdReportList.add(text);
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1=new AvalancheProblemTyrol(text);
                                                tempAvalancheProblem1.setExpositionImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem1ImageLinkExposition()));
                                                tempAvalancheProblem1.setElevationImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem1ImageLinkElevation()));
                                                break;
                                            case 1:
                                                tempAvalancheProblem2=new AvalancheProblemTyrol(text);
                                                tempAvalancheProblem2.setExpositionImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem2ImageLinkExposition()));
                                                tempAvalancheProblem2.setElevationImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem2ImageLinkElevation()));
                                                break;
                                            case 2:
                                                tempAvalancheProblem3=new AvalancheProblemTyrol(text);
                                                tempAvalancheProblem3.setExpositionImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem3ImageLinkExposition()));
                                                tempAvalancheProblem3.setElevationImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem3ImageLinkElevation()));
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "gliding snow":
                                        text="Gleitschnee";
                                        lwdReportList.add(text);
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1=new AvalancheProblemTyrol(text);
                                                tempAvalancheProblem1.setExpositionImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem1ImageLinkExposition()));
                                                tempAvalancheProblem1.setElevationImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem1ImageLinkElevation()));
                                                break;
                                            case 1:
                                                tempAvalancheProblem2=new AvalancheProblemTyrol(text);
                                                tempAvalancheProblem2.setExpositionImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem2ImageLinkExposition()));
                                                tempAvalancheProblem2.setElevationImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem2ImageLinkElevation()));
                                                break;
                                            case 2:
                                                tempAvalancheProblem3=new AvalancheProblemTyrol(text);
                                                tempAvalancheProblem3.setExpositionImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem3ImageLinkExposition()));
                                                tempAvalancheProblem3.setElevationImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem3ImageLinkElevation()));
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "new snow":
                                        text="Neuschnee";
                                        lwdReportList.add(text);
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1=new AvalancheProblemTyrol(text);
                                                tempAvalancheProblem1.setExpositionImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem1ImageLinkExposition()));
                                                tempAvalancheProblem1.setElevationImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem1ImageLinkElevation()));
                                                break;
                                            case 1:
                                                tempAvalancheProblem2=new AvalancheProblemTyrol(text);
                                                tempAvalancheProblem2.setExpositionImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem2ImageLinkExposition()));
                                                tempAvalancheProblem2.setElevationImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem2ImageLinkElevation()));
                                                break;
                                            case 2:
                                                tempAvalancheProblem3=new AvalancheProblemTyrol(text);
                                                tempAvalancheProblem3.setExpositionImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem3ImageLinkExposition()));
                                                tempAvalancheProblem3.setElevationImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem3ImageLinkElevation()));
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "old snow":
                                        text="Altschnee";
                                        lwdReportList.add(text);
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1=new AvalancheProblemTyrol(text);
                                                tempAvalancheProblem1.setExpositionImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem1ImageLinkExposition()));
                                                tempAvalancheProblem1.setElevationImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem1ImageLinkElevation()));
                                                break;
                                            case 1:
                                                tempAvalancheProblem2=new AvalancheProblemTyrol(text);
                                                tempAvalancheProblem2.setExpositionImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem2ImageLinkExposition()));
                                                tempAvalancheProblem2.setElevationImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem2ImageLinkElevation()));
                                                break;
                                            case 2:
                                                tempAvalancheProblem3=new AvalancheProblemTyrol(text);
                                                tempAvalancheProblem3.setExpositionImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem3ImageLinkExposition()));
                                                tempAvalancheProblem3.setElevationImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem3ImageLinkElevation()));
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "wet snow":
                                        text="Nassschnee";
                                        lwdReportList.add(text);
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1=new AvalancheProblemTyrol(text);
                                                tempAvalancheProblem1.setExpositionImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem1ImageLinkExposition()));
                                                tempAvalancheProblem1.setElevationImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem1ImageLinkElevation()));
                                                break;
                                            case 1:
                                                tempAvalancheProblem2=new AvalancheProblemTyrol(text);
                                                tempAvalancheProblem2.setExpositionImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem2ImageLinkExposition()));
                                                tempAvalancheProblem2.setElevationImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem2ImageLinkElevation()));
                                                break;
                                            case 2:
                                                tempAvalancheProblem3=new AvalancheProblemTyrol(text);
                                                tempAvalancheProblem3.setExpositionImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem3ImageLinkExposition()));
                                                tempAvalancheProblem3.setElevationImage(downloadImageUrl(avalancheBulletinTyrol.getAvalancheProblem3ImageLinkElevation()));
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    default:
                                        break;

                                }
                                text="";
                            } else if(name.equalsIgnoreCase("caaml:validAspect")) {
                                attribute=xmlParser.getAttributeValue(null, "xlink:href");
                                attribute=attribute.substring(attribute.lastIndexOf("_") + 1);

                                lwdReportList.add(attribute);
                                switch (avalancheProblemType){
                                    case "drifting snow":
                                        switch (problemCounter){
                                            case 0:
                                                switch (attribute){
                                                    case "N":
                                                        tempAvalancheProblem1.getExpositions().add(1);
                                                        break;
                                                    case "NE":
                                                        tempAvalancheProblem1.getExpositions().add(2);
                                                        break;
                                                    case "E":
                                                        tempAvalancheProblem1.getExpositions().add(3);
                                                        break;
                                                    case "SE":
                                                        tempAvalancheProblem1.getExpositions().add(4);
                                                        break;
                                                    case "S":
                                                        tempAvalancheProblem1.getExpositions().add(5);
                                                        break;
                                                    case "SW":
                                                        tempAvalancheProblem1.getExpositions().add(6);
                                                        break;
                                                    case "W":
                                                        tempAvalancheProblem1.getExpositions().add(7);
                                                        break;
                                                    case "NW":
                                                        tempAvalancheProblem1.getExpositions().add(8);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                //tempAvalancheProblem1.getExpositions().add(attribute);
                                                break;
                                            case 1:
                                                switch (attribute){
                                                    case "N":
                                                        tempAvalancheProblem2.getExpositions().add(1);
                                                        break;
                                                    case "NE":
                                                        tempAvalancheProblem2.getExpositions().add(2);
                                                        break;
                                                    case "E":
                                                        tempAvalancheProblem2.getExpositions().add(3);
                                                        break;
                                                    case "SE":
                                                        tempAvalancheProblem2.getExpositions().add(4);
                                                        break;
                                                    case "S":
                                                        tempAvalancheProblem2.getExpositions().add(5);
                                                        break;
                                                    case "SW":
                                                        tempAvalancheProblem2.getExpositions().add(6);
                                                        break;
                                                    case "W":
                                                        tempAvalancheProblem2.getExpositions().add(7);
                                                        break;
                                                    case "NW":
                                                        tempAvalancheProblem2.getExpositions().add(8);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                            case 2:
                                                switch (attribute){
                                                    case "N":
                                                        tempAvalancheProblem3.getExpositions().add(1);
                                                        break;
                                                    case "NE":
                                                        tempAvalancheProblem3.getExpositions().add(2);
                                                        break;
                                                    case "E":
                                                        tempAvalancheProblem3.getExpositions().add(3);
                                                        break;
                                                    case "SE":
                                                        tempAvalancheProblem3.getExpositions().add(4);
                                                        break;
                                                    case "S":
                                                        tempAvalancheProblem3.getExpositions().add(5);
                                                        break;
                                                    case "SW":
                                                        tempAvalancheProblem3.getExpositions().add(6);
                                                        break;
                                                    case "W":
                                                        tempAvalancheProblem3.getExpositions().add(7);
                                                        break;
                                                    case "NW":
                                                        tempAvalancheProblem3.getExpositions().add(8);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "gliding snow":
                                        switch (problemCounter){
                                            case 0:
                                                switch (attribute){
                                                    case "N":
                                                        tempAvalancheProblem1.getExpositions().add(1);
                                                        break;
                                                    case "NE":
                                                        tempAvalancheProblem1.getExpositions().add(2);
                                                        break;
                                                    case "E":
                                                        tempAvalancheProblem1.getExpositions().add(3);
                                                        break;
                                                    case "SE":
                                                        tempAvalancheProblem1.getExpositions().add(4);
                                                        break;
                                                    case "S":
                                                        tempAvalancheProblem1.getExpositions().add(5);
                                                        break;
                                                    case "SW":
                                                        tempAvalancheProblem1.getExpositions().add(6);
                                                        break;
                                                    case "W":
                                                        tempAvalancheProblem1.getExpositions().add(7);
                                                        break;
                                                    case "NW":
                                                        tempAvalancheProblem1.getExpositions().add(8);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                //tempAvalancheProblem1.getExpositions().add(attribute);
                                                break;
                                            case 1:
                                                switch (attribute){
                                                    case "N":
                                                        tempAvalancheProblem2.getExpositions().add(1);
                                                        break;
                                                    case "NE":
                                                        tempAvalancheProblem2.getExpositions().add(2);
                                                        break;
                                                    case "E":
                                                        tempAvalancheProblem2.getExpositions().add(3);
                                                        break;
                                                    case "SE":
                                                        tempAvalancheProblem2.getExpositions().add(4);
                                                        break;
                                                    case "S":
                                                        tempAvalancheProblem2.getExpositions().add(5);
                                                        break;
                                                    case "SW":
                                                        tempAvalancheProblem2.getExpositions().add(6);
                                                        break;
                                                    case "W":
                                                        tempAvalancheProblem2.getExpositions().add(7);
                                                        break;
                                                    case "NW":
                                                        tempAvalancheProblem2.getExpositions().add(8);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                            case 2:
                                                switch (attribute){
                                                    case "N":
                                                        tempAvalancheProblem3.getExpositions().add(1);
                                                        break;
                                                    case "NE":
                                                        tempAvalancheProblem3.getExpositions().add(2);
                                                        break;
                                                    case "E":
                                                        tempAvalancheProblem3.getExpositions().add(3);
                                                        break;
                                                    case "SE":
                                                        tempAvalancheProblem3.getExpositions().add(4);
                                                        break;
                                                    case "S":
                                                        tempAvalancheProblem3.getExpositions().add(5);
                                                        break;
                                                    case "SW":
                                                        tempAvalancheProblem3.getExpositions().add(6);
                                                        break;
                                                    case "W":
                                                        tempAvalancheProblem3.getExpositions().add(7);
                                                        break;
                                                    case "NW":
                                                        tempAvalancheProblem3.getExpositions().add(8);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "new snow":
                                        switch (problemCounter){
                                            case 0:
                                                switch (attribute){
                                                    case "N":
                                                        tempAvalancheProblem1.getExpositions().add(1);
                                                        break;
                                                    case "NE":
                                                        tempAvalancheProblem1.getExpositions().add(2);
                                                        break;
                                                    case "E":
                                                        tempAvalancheProblem1.getExpositions().add(3);
                                                        break;
                                                    case "SE":
                                                        tempAvalancheProblem1.getExpositions().add(4);
                                                        break;
                                                    case "S":
                                                        tempAvalancheProblem1.getExpositions().add(5);
                                                        break;
                                                    case "SW":
                                                        tempAvalancheProblem1.getExpositions().add(6);
                                                        break;
                                                    case "W":
                                                        tempAvalancheProblem1.getExpositions().add(7);
                                                        break;
                                                    case "NW":
                                                        tempAvalancheProblem1.getExpositions().add(8);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                //tempAvalancheProblem1.getExpositions().add(attribute);
                                                break;
                                            case 1:
                                                switch (attribute){
                                                    case "N":
                                                        tempAvalancheProblem2.getExpositions().add(1);
                                                        break;
                                                    case "NE":
                                                        tempAvalancheProblem2.getExpositions().add(2);
                                                        break;
                                                    case "E":
                                                        tempAvalancheProblem2.getExpositions().add(3);
                                                        break;
                                                    case "SE":
                                                        tempAvalancheProblem2.getExpositions().add(4);
                                                        break;
                                                    case "S":
                                                        tempAvalancheProblem2.getExpositions().add(5);
                                                        break;
                                                    case "SW":
                                                        tempAvalancheProblem2.getExpositions().add(6);
                                                        break;
                                                    case "W":
                                                        tempAvalancheProblem2.getExpositions().add(7);
                                                        break;
                                                    case "NW":
                                                        tempAvalancheProblem2.getExpositions().add(8);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                            case 2:
                                                switch (attribute){
                                                    case "N":
                                                        tempAvalancheProblem3.getExpositions().add(1);
                                                        break;
                                                    case "NE":
                                                        tempAvalancheProblem3.getExpositions().add(2);
                                                        break;
                                                    case "E":
                                                        tempAvalancheProblem3.getExpositions().add(3);
                                                        break;
                                                    case "SE":
                                                        tempAvalancheProblem3.getExpositions().add(4);
                                                        break;
                                                    case "S":
                                                        tempAvalancheProblem3.getExpositions().add(5);
                                                        break;
                                                    case "SW":
                                                        tempAvalancheProblem3.getExpositions().add(6);
                                                        break;
                                                    case "W":
                                                        tempAvalancheProblem3.getExpositions().add(7);
                                                        break;
                                                    case "NW":
                                                        tempAvalancheProblem3.getExpositions().add(8);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "old snow":
                                        switch (problemCounter){
                                            case 0:
                                                switch (attribute){
                                                    case "N":
                                                        tempAvalancheProblem1.getExpositions().add(1);
                                                        break;
                                                    case "NE":
                                                        tempAvalancheProblem1.getExpositions().add(2);
                                                        break;
                                                    case "E":
                                                        tempAvalancheProblem1.getExpositions().add(3);
                                                        break;
                                                    case "SE":
                                                        tempAvalancheProblem1.getExpositions().add(4);
                                                        break;
                                                    case "S":
                                                        tempAvalancheProblem1.getExpositions().add(5);
                                                        break;
                                                    case "SW":
                                                        tempAvalancheProblem1.getExpositions().add(6);
                                                        break;
                                                    case "W":
                                                        tempAvalancheProblem1.getExpositions().add(7);
                                                        break;
                                                    case "NW":
                                                        tempAvalancheProblem1.getExpositions().add(8);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                //tempAvalancheProblem1.getExpositions().add(attribute);
                                                break;
                                            case 1:
                                                switch (attribute){
                                                    case "N":
                                                        tempAvalancheProblem2.getExpositions().add(1);
                                                        break;
                                                    case "NE":
                                                        tempAvalancheProblem2.getExpositions().add(2);
                                                        break;
                                                    case "E":
                                                        tempAvalancheProblem2.getExpositions().add(3);
                                                        break;
                                                    case "SE":
                                                        tempAvalancheProblem2.getExpositions().add(4);
                                                        break;
                                                    case "S":
                                                        tempAvalancheProblem2.getExpositions().add(5);
                                                        break;
                                                    case "SW":
                                                        tempAvalancheProblem2.getExpositions().add(6);
                                                        break;
                                                    case "W":
                                                        tempAvalancheProblem2.getExpositions().add(7);
                                                        break;
                                                    case "NW":
                                                        tempAvalancheProblem2.getExpositions().add(8);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                            case 2:
                                                switch (attribute){
                                                    case "N":
                                                        tempAvalancheProblem3.getExpositions().add(1);
                                                        break;
                                                    case "NE":
                                                        tempAvalancheProblem3.getExpositions().add(2);
                                                        break;
                                                    case "E":
                                                        tempAvalancheProblem3.getExpositions().add(3);
                                                        break;
                                                    case "SE":
                                                        tempAvalancheProblem3.getExpositions().add(4);
                                                        break;
                                                    case "S":
                                                        tempAvalancheProblem3.getExpositions().add(5);
                                                        break;
                                                    case "SW":
                                                        tempAvalancheProblem3.getExpositions().add(6);
                                                        break;
                                                    case "W":
                                                        tempAvalancheProblem3.getExpositions().add(7);
                                                        break;
                                                    case "NW":
                                                        tempAvalancheProblem3.getExpositions().add(8);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "wet snow":
                                        switch (problemCounter){
                                            case 0:
                                                switch (attribute){
                                                    case "N":
                                                        tempAvalancheProblem1.getExpositions().add(1);
                                                        break;
                                                    case "NE":
                                                        tempAvalancheProblem1.getExpositions().add(2);
                                                        break;
                                                    case "E":
                                                        tempAvalancheProblem1.getExpositions().add(3);
                                                        break;
                                                    case "SE":
                                                        tempAvalancheProblem1.getExpositions().add(4);
                                                        break;
                                                    case "S":
                                                        tempAvalancheProblem1.getExpositions().add(5);
                                                        break;
                                                    case "SW":
                                                        tempAvalancheProblem1.getExpositions().add(6);
                                                        break;
                                                    case "W":
                                                        tempAvalancheProblem1.getExpositions().add(7);
                                                        break;
                                                    case "NW":
                                                        tempAvalancheProblem1.getExpositions().add(8);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                //tempAvalancheProblem1.getExpositions().add(attribute);
                                                break;
                                            case 1:
                                                switch (attribute){
                                                    case "N":
                                                        tempAvalancheProblem2.getExpositions().add(1);
                                                        break;
                                                    case "NE":
                                                        tempAvalancheProblem2.getExpositions().add(2);
                                                        break;
                                                    case "E":
                                                        tempAvalancheProblem2.getExpositions().add(3);
                                                        break;
                                                    case "SE":
                                                        tempAvalancheProblem2.getExpositions().add(4);
                                                        break;
                                                    case "S":
                                                        tempAvalancheProblem2.getExpositions().add(5);
                                                        break;
                                                    case "SW":
                                                        tempAvalancheProblem2.getExpositions().add(6);
                                                        break;
                                                    case "W":
                                                        tempAvalancheProblem2.getExpositions().add(7);
                                                        break;
                                                    case "NW":
                                                        tempAvalancheProblem2.getExpositions().add(8);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                            case 2:
                                                switch (attribute){
                                                    case "N":
                                                        tempAvalancheProblem3.getExpositions().add(1);
                                                        break;
                                                    case "NE":
                                                        tempAvalancheProblem3.getExpositions().add(2);
                                                        break;
                                                    case "E":
                                                        tempAvalancheProblem3.getExpositions().add(3);
                                                        break;
                                                    case "SE":
                                                        tempAvalancheProblem3.getExpositions().add(4);
                                                        break;
                                                    case "S":
                                                        tempAvalancheProblem3.getExpositions().add(5);
                                                        break;
                                                    case "SW":
                                                        tempAvalancheProblem3.getExpositions().add(6);
                                                        break;
                                                    case "W":
                                                        tempAvalancheProblem3.getExpositions().add(7);
                                                        break;
                                                    case "NW":
                                                        tempAvalancheProblem3.getExpositions().add(8);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    default:

                                        break;
                                }
                                text="";
                            } else if(name.equalsIgnoreCase("caaml:beginPosition") && !text.equals("") && text.length()<5) {
                                lwdReportList.add("ab "+text);
                                switch (avalancheProblemType){
                                    case "drifting snow":
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1.setAvalancheProblemLevelAbove(true);
                                                tempAvalancheProblem1.setBorder(Integer.parseInt(text));
                                                break;
                                            case 1:
                                                tempAvalancheProblem2.setAvalancheProblemLevelAbove(true);
                                                tempAvalancheProblem2.setBorder(Integer.parseInt(text));
                                                break;
                                            case 2:
                                                tempAvalancheProblem3.setAvalancheProblemLevelAbove(true);
                                                tempAvalancheProblem3.setBorder(Integer.parseInt(text));
                                                break;
                                            default:
                                                break;
                                        }

                                        break;
                                    case "gliding snow":
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1.setAvalancheProblemLevelAbove(true);
                                                tempAvalancheProblem1.setBorder(Integer.parseInt(text));
                                                break;
                                            case 1:
                                                tempAvalancheProblem2.setAvalancheProblemLevelAbove(true);
                                                tempAvalancheProblem2.setBorder(Integer.parseInt(text));
                                                break;
                                            case 2:
                                                tempAvalancheProblem3.setAvalancheProblemLevelAbove(true);
                                                tempAvalancheProblem3.setBorder(Integer.parseInt(text));
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "new snow":
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1.setAvalancheProblemLevelAbove(true);
                                                tempAvalancheProblem1.setBorder(Integer.parseInt(text));
                                                break;
                                            case 1:
                                                tempAvalancheProblem2.setAvalancheProblemLevelAbove(true);
                                                tempAvalancheProblem2.setBorder(Integer.parseInt(text));
                                                break;
                                            case 2:
                                                tempAvalancheProblem3.setAvalancheProblemLevelAbove(true);
                                                tempAvalancheProblem3.setBorder(Integer.parseInt(text));
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "old snow":
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1.setAvalancheProblemLevelAbove(true);
                                                tempAvalancheProblem1.setBorder(Integer.parseInt(text));
                                                break;
                                            case 1:
                                                tempAvalancheProblem2.setAvalancheProblemLevelAbove(true);
                                                tempAvalancheProblem2.setBorder(Integer.parseInt(text));
                                                break;
                                            case 2:
                                                tempAvalancheProblem3.setAvalancheProblemLevelAbove(true);
                                                tempAvalancheProblem3.setBorder(Integer.parseInt(text));
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "wet snow":
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1.setAvalancheProblemLevelAbove(true);
                                                tempAvalancheProblem1.setBorder(Integer.parseInt(text));
                                                break;
                                            case 1:
                                                tempAvalancheProblem2.setAvalancheProblemLevelAbove(true);
                                                tempAvalancheProblem2.setBorder(Integer.parseInt(text));
                                                break;
                                            case 2:
                                                tempAvalancheProblem3.setAvalancheProblemLevelAbove(true);
                                                tempAvalancheProblem3.setBorder(Integer.parseInt(text));
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    default:

                                        break;
                                }
                                text="";
                            }  else if(name.equalsIgnoreCase("caaml:endPosition") && !text.equals("") && text.length()<5) {
                                lwdReportList.add("bis "+text);
                                switch (avalancheProblemType){
                                    case "drifting snow":
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1.setAvalancheProblemLevelBelow(true);
                                                tempAvalancheProblem1.setBorder(Integer.parseInt(text));
                                                break;
                                            case 1:
                                                tempAvalancheProblem2.setAvalancheProblemLevelBelow(true);
                                                tempAvalancheProblem2.setBorder(Integer.parseInt(text));
                                                break;
                                            case 2:
                                                tempAvalancheProblem3.setAvalancheProblemLevelBelow(true);
                                                tempAvalancheProblem3.setBorder(Integer.parseInt(text));
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "gliding snow":
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1.setAvalancheProblemLevelBelow(true);
                                                tempAvalancheProblem1.setBorder(Integer.parseInt(text));
                                                break;
                                            case 1:
                                                tempAvalancheProblem2.setAvalancheProblemLevelBelow(true);
                                                tempAvalancheProblem2.setBorder(Integer.parseInt(text));
                                                break;
                                            case 2:
                                                tempAvalancheProblem3.setAvalancheProblemLevelBelow(true);
                                                tempAvalancheProblem3.setBorder(Integer.parseInt(text));
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "new snow":
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1.setAvalancheProblemLevelBelow(true);
                                                tempAvalancheProblem1.setBorder(Integer.parseInt(text));
                                                break;
                                            case 1:
                                                tempAvalancheProblem2.setAvalancheProblemLevelBelow(true);
                                                tempAvalancheProblem2.setBorder(Integer.parseInt(text));
                                                break;
                                            case 2:
                                                tempAvalancheProblem3.setAvalancheProblemLevelBelow(true);
                                                tempAvalancheProblem3.setBorder(Integer.parseInt(text));
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "old snow":
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1.setAvalancheProblemLevelBelow(true);
                                                tempAvalancheProblem1.setBorder(Integer.parseInt(text));
                                                break;
                                            case 1:
                                                tempAvalancheProblem2.setAvalancheProblemLevelBelow(true);
                                                tempAvalancheProblem2.setBorder(Integer.parseInt(text));
                                                break;
                                            case 2:
                                                tempAvalancheProblem3.setAvalancheProblemLevelBelow(true);
                                                tempAvalancheProblem3.setBorder(Integer.parseInt(text));
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "wet snow":
                                        switch (problemCounter){
                                            case 0:
                                                tempAvalancheProblem1.setAvalancheProblemLevelBelow(true);
                                                tempAvalancheProblem1.setBorder(Integer.parseInt(text));
                                                break;
                                            case 1:
                                                tempAvalancheProblem2.setAvalancheProblemLevelBelow(true);
                                                tempAvalancheProblem2.setBorder(Integer.parseInt(text));
                                                break;
                                            case 2:
                                                tempAvalancheProblem3.setAvalancheProblemLevelBelow(true);
                                                tempAvalancheProblem3.setBorder(Integer.parseInt(text));
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    default:

                                        break;
                                }
                                text="";
                            } else if(name.equalsIgnoreCase("caaml:wxSynopsisHighlights") && !text.equals("")) {
                                lwdReportList.add(text);
                                avalancheBulletinTyrol.setZamgWeatherStation(text);
                                text="";
                            } else if(name.equalsIgnoreCase("caaml:wxSynopsisComment") && !text.equals("")) {
                                lwdReportList.add(text);
                                avalancheBulletinTyrol.setZamgWeatherStationReport(text);
                                text="";
                            } else if(name.equalsIgnoreCase("caaml:snowpackStructureHighlights") && !text.equals("")) {
                                lwdReportList.add(text);
                                avalancheBulletinTyrol.setSnowpackStructure(text);
                                text="";
                            } else if(name.equalsIgnoreCase("caaml:snowpackStructureComment") && !text.equals("")) {
                                lwdReportList.add(text);
                                avalancheBulletinTyrol.setSnowpackStructureDescription(text);
                                text="";
                            } else if(name.equalsIgnoreCase("caaml:travelAdvisoryHighlights") && !text.equals("")) {
                                lwdReportList.add(text);
                                avalancheBulletinTyrol.setAvalancheDangerAssessment(text);
                                text="";
                            } else if(name.equalsIgnoreCase("caaml:travelAdvisoryComment") && !text.equals("")) {
                                lwdReportList.add(text);
                                avalancheBulletinTyrol.setAvalancheDangerAssessmentDescription(text);
                                text="";
                            }

                            if(name.equalsIgnoreCase("caaml:timePosition")){
                                timePositionSkipFirstTime=true;
                            }
                            break;
                    }
                    event = xmlParser.next();
                }
            } catch (XmlPullParserException | IOException e){
                Log.d("EXCEPTION",e.getMessage()+e.toString()+e.getLocalizedMessage());
            } finally {
                if(stream!=null){
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            commentCounter=0;

            avalancheBulletinTyrol.setAvalancheSlopeAreaBulletinTyrolArrayList(createArrayListSlopeAreaBulletinTyrol());

            WeatherDataExtractor weatherDataExtractor = new WeatherDataExtractor(activity);
            windDirection=weatherDataExtractor.calculateSumWeatherStationDataCurrentDayWindDirection();

            return null;
        }

        /**
         * The method delegates the successfully created avalanche bulletin to the requested activity.
         * @param v empty
         */

        @Override
        protected void onPostExecute(Void v) {
            //delegate avalanche bulletin report back to invoking activity
            delegate.getAsyncResult(avalancheBulletinTyrol,windDirection);
        }

        /**
         * The method is used to download the xml file.
         * @param urlString The url where the xml is located.
         * @return It returns the inputstream which contains the xml file.
         * @throws IOException Is thrown if there is a problem with the internet/url
         */

        //downloads xml of URL
        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000); //milliseconds
            conn.setConnectTimeout(15000); //milliseconds
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            return conn.getInputStream();
        }

        /**
         * The method is used to download the images where the urls are inside the xml file.
         * @param urlString The url where the image file is located.
         * @return Returns the image as bitmap.
         */

        private Bitmap downloadImageUrl(String urlString){
            Bitmap bm = null;
            try {
                URL url = new URL(urlString);
                URLConnection conn = url.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.d("EXCEPTION",e.getMessage()+e.toString()+e.getLocalizedMessage());
            }
            return bm;
        }

        /**
         * The method parses a date and time string to a calendar object.
         * @param dateTime The string which contains a date and time.
         * @return Returns a calendar object.
         */

        private Calendar parseDateTime(String dateTime){
            Calendar calender = Calendar.getInstance();
            calender.set(Integer.parseInt(dateTime.substring(0,4)),Integer.parseInt(dateTime.substring(5,7))-1, Integer.parseInt(dateTime.substring(8,10)), Integer.parseInt(dateTime.substring(11,13)),Integer.parseInt(dateTime.substring(14,16)),0);
            return calender;
        }

        /**
         * The method parses a date and time string to a date and time string in the correct format.
         * @param dateTime The string which contains a date and time.
         * @return Returns a formated date string.
         */

        private String parseDateTimeToString(String dateTime){
            String calender = dateTime.substring(0,4) + dateTime.substring(5,7) + dateTime.substring(8,10) + dateTime.substring(11,13) +  dateTime.substring(14,16);
            //YYYYMMDDHHmm
            return calender;
        }

        /**
         * The method parses a date string to a calendar object.
         * @param date The string which contains a date.
         * @return Returns a calendar object.
         */

        private Calendar parseDate(String date){
            Calendar calender = Calendar.getInstance();
            calender.set(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(5,7))-1, Integer.parseInt(date.substring(8,10)));
            return calender;
        }

        /**
         * The method parses a date string to a formated date string.
         * @param date The string which contains a date.
         * @return Returns a format date string.
         */

        private String parseDateToString(String date){
            String calender = date.substring(0,4) + date.substring(5,7) +date.substring(8,10);
            //YYYYMMDD
            return calender;
        }

        /**
         * The method opens the slope data from the asset folder and create an arraylist of AvalancheSlopeAreaBulletinTyrol model and returns it.
         * @return Returns an arraylist of AvalancheSlopeAreaBulletinTyrol objects
         */

        public ArrayList<AvalancheSlopeAreaBulletinTyrol> createArrayListSlopeAreaBulletinTyrol(){
            ArrayList<AvalancheSlopeAreaBulletinTyrol> avalancheSlopeAreaBulletinTyrols = new ArrayList<>();
            AvalancheSlopeAreaBulletinTyrol tempSlopeAreaBulletinTyrol;
            InputStream inputStream = null;
            int AvalancheSlopeAreaBulletinTyrolId=1;
            try {
                inputStream = activity.getAssets().open("slopeareas_data.dat");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                String[] inputLineArray;
                while ((inputLine = reader.readLine()) != null) {
                    //skip commentar lines in .dat file
                    if(inputLine.startsWith("//")){
                        continue;
                    }

                    tempSlopeAreaBulletinTyrol = new AvalancheSlopeAreaBulletinTyrol();
                    inputLineArray = inputLine.split(" ");


                    tempSlopeAreaBulletinTyrol.setExpositionNumber(Integer.parseInt(inputLineArray[2]));
                    tempSlopeAreaBulletinTyrol.setMapboxId(Integer.parseInt(inputLineArray[3]));
                    tempSlopeAreaBulletinTyrol.setSlopeAngleNumber(Integer.parseInt(inputLineArray[4]));
                    tempSlopeAreaBulletinTyrol.setMaxSH(Integer.parseInt(inputLineArray[5]));
                    tempSlopeAreaBulletinTyrol.setMinSH(Integer.parseInt(inputLineArray[6]));
                    tempSlopeAreaBulletinTyrol.setId(AvalancheSlopeAreaBulletinTyrolId);
                    tempSlopeAreaBulletinTyrol.setRegionName("Gebiet "+AvalancheSlopeAreaBulletinTyrolId);

                    for(int i=0;i<avalancheBulletinTyrol.getAvalancheProblemsTyrols().size();i++){
                        AvalancheProblemTyrol tempProblem =avalancheBulletinTyrol.getAvalancheProblemsTyrols().get(i);
                        if(tempProblem.isAvalancheProblemLevelBelow()){
                            if(tempProblem.getBorder()>=tempSlopeAreaBulletinTyrol.getMinSH()|| tempProblem.getBorder()>=tempSlopeAreaBulletinTyrol.getMaxSH()){
                                for(int u=0;u<tempProblem.getExpositions().size();u++){
                                    if(tempProblem.getExpositions().get(u)==tempSlopeAreaBulletinTyrol.getExpositionNumber()){
                                        tempSlopeAreaBulletinTyrol.getListAvalancheProblems().add(tempProblem.getAvalancheProblem());
                                    }
                                }

                            }
                        } else if(tempProblem.isAvalancheProblemLevelAbove()) {
                            if(tempProblem.getBorder()<=tempSlopeAreaBulletinTyrol.getMaxSH() || tempProblem.getBorder()<=tempSlopeAreaBulletinTyrol.getMinSH()){
                                for(int u=0;u<tempProblem.getExpositions().size();u++){
                                    if(tempProblem.getExpositions().get(u)==tempSlopeAreaBulletinTyrol.getExpositionNumber()){
                                        tempSlopeAreaBulletinTyrol.getListAvalancheProblems().add(tempProblem.getAvalancheProblem());
                                    }
                                }
                            }
                        }
                    }

                    //set slope danger lvl
                    Calendar calendar = avalancheBulletinTyrol.getDateTime();
                    Calendar currentTime = Calendar.getInstance();
                    if(currentTime.get(Calendar.HOUR_OF_DAY)>=0 && currentTime.get(Calendar.HOUR_OF_DAY)<=11){ //is morning
                        if(avalancheBulletinTyrol.getRegion6DangerLevelMorningBorder()>tempSlopeAreaBulletinTyrol.getMaxSH() && avalancheBulletinTyrol.getRegion6DangerLevelMorningBorder()>tempSlopeAreaBulletinTyrol.getMinSH()){
                            tempSlopeAreaBulletinTyrol.setSlopeDangerLvl(avalancheBulletinTyrol.getRegion6DangerLevelMorningBorderBelow());
                        } else{ //slope area is above border value
                            tempSlopeAreaBulletinTyrol.setSlopeDangerLvl(avalancheBulletinTyrol.getRegion6DangerLevelMorningBorderAbove());
                        }
                    } else { // afternoon
                        if(avalancheBulletinTyrol.getRegion6DangerLevelAfternoonBorder()>tempSlopeAreaBulletinTyrol.getMaxSH() && avalancheBulletinTyrol.getRegion6DangerLevelAfternoonBorder()>tempSlopeAreaBulletinTyrol.getMinSH()){
                            tempSlopeAreaBulletinTyrol.setSlopeDangerLvl(avalancheBulletinTyrol.getRegion6DangerLevelAfternoonBorderBelow());
                        } else{ //slope area is above border value
                            tempSlopeAreaBulletinTyrol.setSlopeDangerLvl(avalancheBulletinTyrol.getRegion6DangerLevelAfternoonBorderAbove());
                        }
                    }

                    avalancheSlopeAreaBulletinTyrols.add(tempSlopeAreaBulletinTyrol);
                    AvalancheSlopeAreaBulletinTyrolId++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return avalancheSlopeAreaBulletinTyrols;
        }


    }

}
