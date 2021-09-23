package at.ac.univie.lumi.view;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

import at.ac.univie.lumi.R;
import at.ac.univie.lumi.controller.ActivityConstants;
import at.ac.univie.lumi.controller.DAOSerialisationManager;
import at.ac.univie.lumi.controller.Helper;
import at.ac.univie.lumi.controller.LatLngJsonXmlExtractor;
import at.ac.univie.lumi.controller.MapMarkerAreaManager;
import at.ac.univie.lumi.controller.NearestMarkerCalculater;
import at.ac.univie.lumi.controller.SlopeDangerLvlProblemsCalculater;
import at.ac.univie.lumi.model.AvalancheBulletinTyrol;
import at.ac.univie.lumi.model.AvalancheSlopeAreaBulletinTyrol;

/**
 *
 * This activity is used as splash activity and displays a progress bar to wait for finishing. It downloads all needed files and serialize all later needed files.
 */

public class SplashLoadingActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private NearestMarkerCalculater distanceCalculater;

    private final String URL_LWD_TYROL_DE="https://lawine.tirol.gv.at/rest/bulletin/latest/xml/de";
    //"https://lawine.tirol.gv.at/rest/bulletin/2017-03-14_073000/xml/de"
    private final String URL_LWD_TYROL_DE_SCENARIO_BEGIN= "https://lawine.tirol.gv.at/rest/bulletin/";
    private final String URL_LWD_TYROL_DE_SCENARIO_END= "_073000/xml/de";
    private AvalancheBulletinTyrol avalancheBulletinTyrol;

    public boolean asyncMyPositionFound=false;
    private boolean asyncTaskXmlExtractor=false;
    public static boolean asyncMapMarkerAreaManager=false;

    //private Thread overviewActivityStarter;

    //private MapMarkerAreaManager mapMarkerAreaManager;
    private int mInterval =500;
    private Handler mHandler;
    public boolean mHandlerForceExit=false;

    private int mXMLBulletinInterval =250;
    private Handler mXMLBulletinHandler;
    public boolean mXMLBulletinHandlerForceExit=false;

    private Calendar choosenScenarioTime;
    private String choosenScenarioString;
    private boolean foundScenario=false;
    private boolean sameScenario=false;

    /**
     * This method is called when the activity is ready. It initialize the different variables and downloads all needed files.
     * It also checks if the scenario has changed. In addition it serializes listmapmarker file, the avalanche bulletin file and the current scenario.
     * @param savedInstanceState Can be used to save the current state.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_loading);

        Intent intent = getIntent();
        if(intent != null && intent.getExtras() != null) {
            choosenScenarioString = intent.getStringExtra(ScenarioChooserActivity.EXTRA_SCENARIO);

            //save scenario date as Calendar object
            choosenScenarioTime = Calendar.getInstance();
            choosenScenarioTime.set(Integer.parseInt(choosenScenarioString.substring(0, 4)), (Integer.parseInt(choosenScenarioString.substring(5, 7))-1), Integer.parseInt(choosenScenarioString.substring(8, 10)));


            File fileScenario= new File(getFilesDir(),ActivityConstants.ScenarioFileName);
            if(fileScenario.exists()){
                Calendar tempCal = DAOSerialisationManager.readSerializedFile(fileScenario.getAbsolutePath());
                if(choosenScenarioTime.get(Calendar.YEAR)==tempCal.get(Calendar.YEAR) &&choosenScenarioTime.get(Calendar.MONTH)==tempCal.get(Calendar.MONTH) && choosenScenarioTime.get(Calendar.DAY_OF_MONTH)==tempCal.get(Calendar.DAY_OF_MONTH)){
                    //save scenario date into a file
                    DAOSerialisationManager.removeSerializedFile(fileScenario.getAbsolutePath());
                    DAOSerialisationManager.saveSerializedFile(choosenScenarioTime, fileScenario.getAbsolutePath());
                    sameScenario=true;
                }else{
                    //save scenario date into a file
                    DAOSerialisationManager.removeSerializedFile(fileScenario.getAbsolutePath());
                    DAOSerialisationManager.saveSerializedFile(choosenScenarioTime, fileScenario.getAbsolutePath());
                }
            } else {
                DAOSerialisationManager.saveSerializedFile(choosenScenarioTime, fileScenario.getAbsolutePath());
            }
            foundScenario=true;
        }

        asyncMyPositionFound=false;
        asyncTaskXmlExtractor=false;
        asyncMapMarkerAreaManager=false;

        if(!sameScenario){
            if(Helper.isNetworkAvailable(SplashLoadingActivity.this)){
                downloadAvalancheBulletins();
            } else{
                Toast.makeText(getApplicationContext(), getString(R.string.appstart_nointernet),  Toast.LENGTH_LONG).show();
            }
        } else{
            File fileLastAvalancheBulletin= new File(getFilesDir(),ActivityConstants.AvalancheBulletinFileName);
            if(fileLastAvalancheBulletin.exists()){
                avalancheBulletinTyrol= DAOSerialisationManager.readSerializedFile(fileLastAvalancheBulletin.getAbsolutePath());
                asyncTaskXmlExtractor=true;
            }else{
                if(Helper.isNetworkAvailable(SplashLoadingActivity.this)){
                    downloadAvalancheBulletins();
                } else{
                    Toast.makeText(getApplicationContext(), getString(R.string.appstart_nointernet),  Toast.LENGTH_LONG).show();
                }
            }
        }


        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mHandler = new Handler(getMainLooper());

        distanceCalculater = new NearestMarkerCalculater(this,this);

        mXMLBulletinHandler = new Handler();
        startThreadDownloadAvalancheBulletinChecker();
    }

    /**
     * This method start to download the requested scenario avalanche bulletin and serialize it. If no scneario is found it tries to download the actual avalanche bulletin.
     */

    private void downloadAvalancheBulletins() {
        if(foundScenario){
            //special case for scenario 3 because the respective avalanche bulletin is not available to download thats why we take the one of the 5.1.2017
            if(choosenScenarioString.equalsIgnoreCase(ActivityConstants.Scenario3)){
                choosenScenarioString=getString(R.string.scenario3_lwdbulletinfake);
                new LatLngJsonXmlExtractor.RestApiXmlLWDTyrolGetter(new LatLngJsonXmlExtractor.RestApiXmlLWDTyrolGetter.AsyncResponseXmlLWDTyrol() {
                    @Override
                    public void getAsyncResult(AvalancheBulletinTyrol result, float windDirection) {
                        avalancheBulletinTyrol = result;


                        //set slope specific values
                        avalancheBulletinTyrol.setWindDirection(windDirection);
                        SlopeDangerLvlProblemsCalculater slopeDangerLvlProblemsCalculater = new SlopeDangerLvlProblemsCalculater(avalancheBulletinTyrol);
                        for(AvalancheSlopeAreaBulletinTyrol slope:avalancheBulletinTyrol.getAvalancheSlopeAreaBulletinTyrolArrayList()){
                            slope.setSlopeAmountDangerZones(slopeDangerLvlProblemsCalculater.calculateSlopeAmountDangerZones(slope));
                            slope.setAdditionalLoad(slopeDangerLvlProblemsCalculater.calculateSlopeAdditionalLoad());
                        }

                        //save file
                        File fileLastAvalancheBulletin= new File(getFilesDir(),ActivityConstants.AvalancheBulletinFileName);
                        DAOSerialisationManager.removeSerializedFile(fileLastAvalancheBulletin.getAbsolutePath());
                        DAOSerialisationManager.saveSerializedFile(avalancheBulletinTyrol, fileLastAvalancheBulletin.getAbsolutePath());

                        asyncTaskXmlExtractor=true;
                    }
                },this).execute(URL_LWD_TYROL_DE_SCENARIO_BEGIN+choosenScenarioString+URL_LWD_TYROL_DE_SCENARIO_END);
            }else{
                if(((choosenScenarioTime.get(Calendar.MONTH)+1)<10) && (choosenScenarioTime.get(Calendar.DAY_OF_MONTH)<10)){
                    new LatLngJsonXmlExtractor.RestApiXmlLWDTyrolGetter(new LatLngJsonXmlExtractor.RestApiXmlLWDTyrolGetter.AsyncResponseXmlLWDTyrol() {
                        @Override
                        public void getAsyncResult(AvalancheBulletinTyrol result, float windDirection) {
                            avalancheBulletinTyrol = result;


                            //set slope specific values
                            avalancheBulletinTyrol.setWindDirection(windDirection);
                            SlopeDangerLvlProblemsCalculater slopeDangerLvlProblemsCalculater = new SlopeDangerLvlProblemsCalculater(avalancheBulletinTyrol);
                            for(AvalancheSlopeAreaBulletinTyrol slope:avalancheBulletinTyrol.getAvalancheSlopeAreaBulletinTyrolArrayList()){
                                slope.setSlopeAmountDangerZones(slopeDangerLvlProblemsCalculater.calculateSlopeAmountDangerZones(slope));
                                slope.setAdditionalLoad(slopeDangerLvlProblemsCalculater.calculateSlopeAdditionalLoad());
                            }

                            //save file
                            File fileLastAvalancheBulletin= new File(getFilesDir(),ActivityConstants.AvalancheBulletinFileName);
                            DAOSerialisationManager.removeSerializedFile(fileLastAvalancheBulletin.getAbsolutePath());
                            DAOSerialisationManager.saveSerializedFile(avalancheBulletinTyrol, fileLastAvalancheBulletin.getAbsolutePath());

                            asyncTaskXmlExtractor=true;
                        }
                    },this).execute(URL_LWD_TYROL_DE_SCENARIO_BEGIN+choosenScenarioTime.get(Calendar.YEAR)+"-0"+(choosenScenarioTime.get(Calendar.MONTH)+1)+"-0"+choosenScenarioTime.get(Calendar.DAY_OF_MONTH)+URL_LWD_TYROL_DE_SCENARIO_END);
                } else if(((choosenScenarioTime.get(Calendar.MONTH)+1)<10) && (choosenScenarioTime.get(Calendar.DAY_OF_MONTH)>=10)){
                    new LatLngJsonXmlExtractor.RestApiXmlLWDTyrolGetter(new LatLngJsonXmlExtractor.RestApiXmlLWDTyrolGetter.AsyncResponseXmlLWDTyrol() {
                        @Override
                        public void getAsyncResult(AvalancheBulletinTyrol result, float windDirection) {
                            avalancheBulletinTyrol = result;


                            //set slope specific values
                            avalancheBulletinTyrol.setWindDirection(windDirection);
                            SlopeDangerLvlProblemsCalculater slopeDangerLvlProblemsCalculater = new SlopeDangerLvlProblemsCalculater(avalancheBulletinTyrol);
                            for(AvalancheSlopeAreaBulletinTyrol slope:avalancheBulletinTyrol.getAvalancheSlopeAreaBulletinTyrolArrayList()){
                                slope.setSlopeAmountDangerZones(slopeDangerLvlProblemsCalculater.calculateSlopeAmountDangerZones(slope));
                                slope.setAdditionalLoad(slopeDangerLvlProblemsCalculater.calculateSlopeAdditionalLoad());
                            }

                            //save file
                            File fileLastAvalancheBulletin= new File(getFilesDir(),ActivityConstants.AvalancheBulletinFileName);
                            DAOSerialisationManager.removeSerializedFile(fileLastAvalancheBulletin.getAbsolutePath());
                            DAOSerialisationManager.saveSerializedFile(avalancheBulletinTyrol, fileLastAvalancheBulletin.getAbsolutePath());

                            asyncTaskXmlExtractor=true;
                        }
                    },this).execute(URL_LWD_TYROL_DE_SCENARIO_BEGIN+choosenScenarioTime.get(Calendar.YEAR)+"-0"+(choosenScenarioTime.get(Calendar.MONTH)+1)+"-"+choosenScenarioTime.get(Calendar.DAY_OF_MONTH)+URL_LWD_TYROL_DE_SCENARIO_END);
                } else if(((choosenScenarioTime.get(Calendar.MONTH)+1)>=10) && (choosenScenarioTime.get(Calendar.DAY_OF_MONTH)<10)){
                    new LatLngJsonXmlExtractor.RestApiXmlLWDTyrolGetter(new LatLngJsonXmlExtractor.RestApiXmlLWDTyrolGetter.AsyncResponseXmlLWDTyrol() {
                        @Override
                        public void getAsyncResult(AvalancheBulletinTyrol result, float windDirection) {
                            avalancheBulletinTyrol = result;


                            //set slope specific values
                            avalancheBulletinTyrol.setWindDirection(windDirection);
                            SlopeDangerLvlProblemsCalculater slopeDangerLvlProblemsCalculater = new SlopeDangerLvlProblemsCalculater(avalancheBulletinTyrol);
                            for(AvalancheSlopeAreaBulletinTyrol slope:avalancheBulletinTyrol.getAvalancheSlopeAreaBulletinTyrolArrayList()){
                                slope.setSlopeAmountDangerZones(slopeDangerLvlProblemsCalculater.calculateSlopeAmountDangerZones(slope));
                                slope.setAdditionalLoad(slopeDangerLvlProblemsCalculater.calculateSlopeAdditionalLoad());
                            }

                            //save file
                            File fileLastAvalancheBulletin= new File(getFilesDir(),ActivityConstants.AvalancheBulletinFileName);
                            DAOSerialisationManager.removeSerializedFile(fileLastAvalancheBulletin.getAbsolutePath());
                            DAOSerialisationManager.saveSerializedFile(avalancheBulletinTyrol, fileLastAvalancheBulletin.getAbsolutePath());

                            asyncTaskXmlExtractor=true;
                        }
                    },this).execute(URL_LWD_TYROL_DE_SCENARIO_BEGIN+choosenScenarioTime.get(Calendar.YEAR)+"-"+(choosenScenarioTime.get(Calendar.MONTH)+1)+"-0"+choosenScenarioTime.get(Calendar.DAY_OF_MONTH)+URL_LWD_TYROL_DE_SCENARIO_END);
                }else {
                    new LatLngJsonXmlExtractor.RestApiXmlLWDTyrolGetter(new LatLngJsonXmlExtractor.RestApiXmlLWDTyrolGetter.AsyncResponseXmlLWDTyrol() {
                        @Override
                        public void getAsyncResult(AvalancheBulletinTyrol result, float windDirection) {
                            avalancheBulletinTyrol = result;

                            //set slope specific values
                            avalancheBulletinTyrol.setWindDirection(windDirection);
                            SlopeDangerLvlProblemsCalculater slopeDangerLvlProblemsCalculater = new SlopeDangerLvlProblemsCalculater(avalancheBulletinTyrol);
                            for(AvalancheSlopeAreaBulletinTyrol slope:avalancheBulletinTyrol.getAvalancheSlopeAreaBulletinTyrolArrayList()){
                                slope.setSlopeAmountDangerZones(slopeDangerLvlProblemsCalculater.calculateSlopeAmountDangerZones(slope));
                                slope.setAdditionalLoad(slopeDangerLvlProblemsCalculater.calculateSlopeAdditionalLoad());
                            }

                            //save file
                            File fileLastAvalancheBulletin= new File(getFilesDir(),ActivityConstants.AvalancheBulletinFileName);
                            DAOSerialisationManager.removeSerializedFile(fileLastAvalancheBulletin.getAbsolutePath());
                            DAOSerialisationManager.saveSerializedFile(avalancheBulletinTyrol, fileLastAvalancheBulletin.getAbsolutePath());

                            asyncTaskXmlExtractor=true;
                        }
                    },this).execute(URL_LWD_TYROL_DE_SCENARIO_BEGIN+choosenScenarioTime.get(Calendar.YEAR)+"-"+(choosenScenarioTime.get(Calendar.MONTH)+1)+"-"+choosenScenarioTime.get(Calendar.DAY_OF_MONTH)+URL_LWD_TYROL_DE_SCENARIO_END);
                }
            }
        } else {
            new LatLngJsonXmlExtractor.RestApiXmlLWDTyrolGetter(new LatLngJsonXmlExtractor.RestApiXmlLWDTyrolGetter.AsyncResponseXmlLWDTyrol() {
                @Override
                public void getAsyncResult(AvalancheBulletinTyrol result, float windDirection) {
                    avalancheBulletinTyrol = result;

                    //set slope specific values
                    avalancheBulletinTyrol.setWindDirection(windDirection);
                    SlopeDangerLvlProblemsCalculater slopeDangerLvlProblemsCalculater = new SlopeDangerLvlProblemsCalculater(avalancheBulletinTyrol);
                    for(AvalancheSlopeAreaBulletinTyrol slope:avalancheBulletinTyrol.getAvalancheSlopeAreaBulletinTyrolArrayList()){
                        slope.setSlopeAmountDangerZones(slopeDangerLvlProblemsCalculater.calculateSlopeAmountDangerZones(slope));
                        slope.setAdditionalLoad(slopeDangerLvlProblemsCalculater.calculateSlopeAdditionalLoad());
                    }

                    //save file
                    File fileLastAvalancheBulletin= new File(getFilesDir(),ActivityConstants.AvalancheBulletinFileName);
                    DAOSerialisationManager.removeSerializedFile(fileLastAvalancheBulletin.getAbsolutePath());
                    DAOSerialisationManager.saveSerializedFile(avalancheBulletinTyrol, fileLastAvalancheBulletin.getAbsolutePath());

                    asyncTaskXmlExtractor=true;
                }
            },this).execute(URL_LWD_TYROL_DE);
        }
    }

    /**
     * This thread is used to check if the download/reading of the avalanche bulletin has finished and then start the map marker area background thread.
     */

    //Needed to synchronize all AsyncTasks to start OverviewActivity and to guarantee finishing of async tasks
    Runnable downloadAvalancheBulletinChecker = new Runnable() {
        @Override
        public void run() {
            try {
                if(asyncTaskXmlExtractor){
                    if(!sameScenario){
                        if(Helper.isNetworkAvailable(SplashLoadingActivity.this)){
                            //get all map markers for nearest markers of current position
                            new MapMarkerAreaManager(SplashLoadingActivity.this, avalancheBulletinTyrol).execute(SplashLoadingActivity.this);
                        } else{
                            Toast.makeText(getApplicationContext(), getString(R.string.appstart_nointernet),  Toast.LENGTH_LONG).show();
                        }
                    } else{
                        File fileListMapMarkers= new File(getFilesDir(),ActivityConstants.MapMarkerListFileName);
                        if(fileListMapMarkers.exists()){
                            asyncMapMarkerAreaManager =true;
                        }else{
                            if(Helper.isNetworkAvailable(SplashLoadingActivity.this)){
                                //get all map markers for nearest markers of current position
                                new MapMarkerAreaManager(SplashLoadingActivity.this, avalancheBulletinTyrol).execute(SplashLoadingActivity.this);
                            } else{
                                Toast.makeText(getApplicationContext(), getString(R.string.appstart_nointernet),  Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    startThreadChecker();
                    mXMLBulletinHandlerForceExit=true;
                }

            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                if(mXMLBulletinHandlerForceExit){
                    mXMLBulletinHandler.removeCallbacks(downloadAvalancheBulletinChecker);
                } else {
                    mXMLBulletinHandler.postDelayed(downloadAvalancheBulletinChecker, mXMLBulletinInterval);
                }

            }
        }
    };

    /**
     * This thread is used to check if the download/reading of the avalanche bulletin has finished,the user position was found and the map marker list has successfully created.
     * After fulfilling this it starts the overview activity and finish itself to be never invoked again.
     */

    //Needed to synchronize all AsyncTasks to start OverviewActivity and to guarantee finishing of async tasks
    Runnable overviewActivityStarter = new Runnable() {
        @Override
        public void run() {
            try {
                Log.i("THREADCHECKER", String.valueOf(asyncMyPositionFound)+String.valueOf(asyncTaskXmlExtractor)+String.valueOf(asyncMapMarkerAreaManager));
                if(asyncMyPositionFound && asyncTaskXmlExtractor && asyncMapMarkerAreaManager){
                    //start OverviewActivity
                    Intent intent = new Intent(SplashLoadingActivity.this, OverviewActivity.class);
                    startActivity(intent);
                    finish(); //no return with the back arrow possible

                    asyncMyPositionFound=false;
                    asyncTaskXmlExtractor=false;
                    asyncMapMarkerAreaManager=false;
                    mHandlerForceExit=true;
                }
                //updateStatus(); //this function can change value of mInterval.

            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                if(mHandlerForceExit){
                    mHandler.removeCallbacks(overviewActivityStarter);
                } else {
                    mHandler.postDelayed(overviewActivityStarter, mInterval);
                }

            }
        }
    };

    /**
     * This method starts the overview ready to start checker thread.
     */

    void startThreadChecker() {
        overviewActivityStarter.run();
    }

    /**
     * This method stops the overview ready to start checker thread.
     */

    void stopThreadChecker() {
        mHandler.removeCallbacks(overviewActivityStarter);
    }

    /**
     * This method starts the download/read avalanche bulletin checker thread.
     */

    void startThreadDownloadAvalancheBulletinChecker() {
        downloadAvalancheBulletinChecker.run();
    }

    /**
     * This method stops the download/read avalanche bulletin checker thread.
     */

    void stopThreadDownloadAvalancheBulletinChecker() {
        mXMLBulletinHandler.removeCallbacks(downloadAvalancheBulletinChecker);
    }

    /**
     * This method is called when the activity is destroyed and it stops all threads.
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();

        distanceCalculater.removeLocationUpdates();
        stopThreadChecker();
        stopThreadDownloadAvalancheBulletinChecker();
    }
}
