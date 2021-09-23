package at.ac.univie.lumi.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

import at.ac.univie.lumi.controller.ActivityConstants;
import at.ac.univie.lumi.controller.DAOSerialisationManager;
import at.ac.univie.lumi.controller.NearestMarkerCalculater;
import at.ac.univie.lumi.model.AvalancheBulletinTyrol;
import at.ac.univie.lumi.R;
import at.ac.univie.lumi.model.SlopeAreaMapMarker;

import static at.ac.univie.lumi.controller.NearestMarkerCalculater.myPosition;

/**
 *
 * This activity displays the overview menu and is the home screen of the app. The user can here choose between different actions/events. Furthermore, it reads all serialized files.
 */

public class OverviewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageButton toolbarHome;
    private ImageButton toolbarMenu;

    private Toolbar sosbar;
    private TextView sosbarTitle;
    private ImageView sosbarImage;

    private LinearLayout overviewAvalanncheInfo;
    private Button mapButton;
    private Button mapDangerZonesButton;
    private ImageView nearestAvalancheBulletinImage;
    private TextView nearestAvalancheBulletinTitle;
    private TextView nearestAvalancheBulletinDangerZones;
    private Button weatherstationButton;
    private Button weatherforecastButton;

    private AvalancheBulletinTyrol avalancheBulletinTyrol;
    public final static String EXTRA_BULLETIN = "at.ac.univie.lumi.BULLETIN";
    public final static String EXTRA_REGION = "at.ac.univie.lumi.REGION";
    public final static String EXTRA_WEATHERSTATION = "at.ac.univie.lumi.WEATHERSTATION";
    public final static String EXTRA_MAPSTYLE = "at.ac.univie.lumi.MAPSTYLE";

    //for searching of nearest Markers
    private ArrayList<SlopeAreaMapMarker> listMapMarkers;
    private NearestMarkerCalculater distanceCalculater;
    private boolean finishedListMapMarkerCalc=false;

    private int mInterval =500;
    private Handler mHandler;
    public boolean mHandlerForceExit=false;

    private Calendar choosenScenarioTime;

    /**
     * This method is called when the activity is ready. It initialize the different variables and read all serialized files. Moreover, it set the drop down menu and the top toolbar.
     * @param savedInstanceState Can be used to save the current state.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        initializeVariables();

        readFilesOrCalculate();
    }

    /**
     * This method initializes all variables.
     */

    private void initializeVariables() {
        mapButton = (Button) findViewById(R.id.mapButton);
        mapDangerZonesButton = (Button) findViewById(R.id.mapDangerZonesButton);
        weatherstationButton = (Button) findViewById(R.id.weatherstationButton);
        weatherforecastButton = (Button) findViewById(R.id.weatherforecastButton);
        overviewAvalanncheInfo = (LinearLayout) findViewById(R.id.overviewAvalanncheInfo);
        nearestAvalancheBulletinImage = (ImageView) findViewById(R.id.overviewAvalancheDangerLevelIcon);
        nearestAvalancheBulletinTitle = (TextView) findViewById(R.id.overviewAvalancheDangerLevelArea);
        nearestAvalancheBulletinDangerZones = (TextView) findViewById(R.id.overviewAvalancheDangerLevelDangerZones);

        mHandler = new Handler(getMainLooper());

        overviewAvalanncheInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // should show nearest avalanche bulletin
                Intent intent = new Intent(getApplicationContext(), AvalancheBulletinActivity.class);
                //show nearest avalanche bulletin depending on position
                SlopeAreaMapMarker nearstesMapMarker=distanceCalculater.calculateNearestMapMarker(listMapMarkers,"avalanchebulletin");
                String regionName = nearstesMapMarker.getName();
                intent.putExtra("calling-activity", ActivityConstants.OverviewActivity);
                intent.putExtra(EXTRA_REGION, regionName);
                startActivity(intent);
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // starts MapActivity on click  with intent map style string
                Intent intent= new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra(EXTRA_MAPSTYLE, getString(R.string.style_mapbox_slopeareas));
                startActivity(intent);
            }
        });
        mapDangerZonesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), MapActivity.class);
                // starts MapActivity on click  with intent map style string
                if(choosenScenarioTime!=null){
                    String choosenScenarioTimeString;
                    if((choosenScenarioTime.get(Calendar.MONTH)+1)<10 && choosenScenarioTime.get(Calendar.DAY_OF_MONTH)<10){
                        choosenScenarioTimeString=choosenScenarioTime.get(Calendar.YEAR)+"-0"+(choosenScenarioTime.get(Calendar.MONTH)+1)+"-0"+choosenScenarioTime.get(Calendar.DAY_OF_MONTH);
                    } else if(choosenScenarioTime.get(Calendar.DAY_OF_MONTH)<10){
                        choosenScenarioTimeString=choosenScenarioTime.get(Calendar.YEAR)+"-"+(choosenScenarioTime.get(Calendar.MONTH)+1)+"-0"+choosenScenarioTime.get(Calendar.DAY_OF_MONTH);
                    }else if((choosenScenarioTime.get(Calendar.MONTH)+1)<10){
                        choosenScenarioTimeString=choosenScenarioTime.get(Calendar.YEAR)+"-0"+(choosenScenarioTime.get(Calendar.MONTH)+1)+"-"+choosenScenarioTime.get(Calendar.DAY_OF_MONTH);
                    }else{
                        choosenScenarioTimeString=choosenScenarioTime.get(Calendar.YEAR)+"-"+(choosenScenarioTime.get(Calendar.MONTH)+1)+"-"+choosenScenarioTime.get(Calendar.DAY_OF_MONTH);
                    }
                    switch (choosenScenarioTimeString){
                        case ActivityConstants.Scenario1:
                            intent.putExtra(EXTRA_MAPSTYLE, getString(R.string.style_mapbox_scenario1));
                            break;
                        case ActivityConstants.Scenario2:
                            intent.putExtra(EXTRA_MAPSTYLE, getString(R.string.style_mapbox_scenario2));
                            break;
                        case ActivityConstants.Scenario3:
                            intent.putExtra(EXTRA_MAPSTYLE, getString(R.string.style_mapbox_scenario3));
                            break;
                        default:
                            break;
                    }
                } else{
                    intent.putExtra(EXTRA_MAPSTYLE, getString(R.string.style_mapbox_slopeareas));
                }
                startActivity(intent);
            }
        });
        weatherstationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // starts nearest WeatherStationActivity on click
                Intent intent= new Intent(getApplicationContext(), WeatherStationActivity.class);
                SlopeAreaMapMarker nearstesMapMarker=distanceCalculater.calculateNearestMapMarker(listMapMarkers,"weatherstation");
                String weatherStationName = nearstesMapMarker.getName();
                intent.putExtra(EXTRA_WEATHERSTATION, weatherStationName);
                intent.putExtra("calling-activity", ActivityConstants.OverviewActivity);
                startActivity(intent);
            }
        });
        weatherforecastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // starts nearest WeatherForecastActivity on click
                Intent intent= new Intent(getApplicationContext(), WeatherForecastActivity.class);
                SlopeAreaMapMarker nearstesMapMarker=distanceCalculater.calculateNearestMapMarker(listMapMarkers,"weatherstation");
                String weatherStationName = nearstesMapMarker.getName();
                intent.putExtra(EXTRA_WEATHERSTATION, weatherStationName);
                intent.putExtra("calling-activity", ActivityConstants.OverviewActivity);
                startActivity(intent);

            }
        });

        initializeToolbar();
        initializeSosbar();
    }

    /**
     * This method initialize the bottom sos bar and its functions.
     */

    private void initializeSosbar() {
        sosbar = (Toolbar) findViewById(R.id.sosbar);
        sosbarTitle = (TextView) sosbar.findViewById(R.id.sosbar_text);
        sosbarImage = (ImageView) sosbar.findViewById(R.id.sosbar_icon);

        sosbarTitle.setText(getString(R.string.sosbar_title));
        sosbarImage.setBackgroundColor(Color.RED);
        sosbar.setBackgroundColor(Color.RED);

        sosbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // starts EmergencyCallActivity on click
                Intent intent= new Intent(getApplicationContext(), EmergencyCallActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * This method initialize the top toolbar and its functions.
     */

    private void initializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setTextColor(Color.BLACK);
        toolbarHome = (ImageButton) toolbar.findViewById(R.id.toolbar_home);
        toolbarMenu = (ImageButton) toolbar.findViewById(R.id.toolbar_menu);

        toolbarHome.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // starts OverviewActivity on click
                Intent intent= new Intent(getApplicationContext(), OverviewActivity.class);
                startActivity(intent);
            }
        });
        toolbarMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    /**
     * This method initialize the drop down menu of the top toolbar and its functions.
     */

    //show popup menu
    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(OverviewActivity.this, view);
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon",boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                //GO to particular page/function
                if(item.getTitle().equals(getResources().getString(R.string.menu_emergencycall))){
                    // starts EmergencyCallActivity on click
                    Intent intent= new Intent(getApplicationContext(), EmergencyCallActivity.class);
                    startActivity(intent);
                } else if (item.getTitle().equals(getResources().getString(R.string.menu_avalanchebulletin))){
                    // should show nearest avalanche bulletin
                    Intent intent = new Intent(getApplicationContext(), AvalancheBulletinActivity.class);
                    //show nearest avalanche bulletin depending on position
                    SlopeAreaMapMarker nearstesMapMarker=distanceCalculater.calculateNearestMapMarker(listMapMarkers,"avalanchebulletin");
                    String regionName = nearstesMapMarker.getName();
                    intent.putExtra("calling-activity", ActivityConstants.OverviewActivity);
                    intent.putExtra(EXTRA_BULLETIN, avalancheBulletinTyrol);
                    intent.putExtra(EXTRA_REGION, regionName);
                    startActivity(intent);
                } else if (item.getTitle().equals(getResources().getString(R.string.menu_map))){
                    // starts MapActivity on click
                    Intent intent= new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent);

                }  else if (item.getTitle().equals(getResources().getString(R.string.menu_weatherstation))){
                    // starts nearest WeatherStationActivity on click
                    Intent intent= new Intent(getApplicationContext(), WeatherStationActivity.class);
                    SlopeAreaMapMarker nearstesMapMarker=distanceCalculater.calculateNearestMapMarker(listMapMarkers,"weatherstation");
                    String weatherStationName = nearstesMapMarker.getName();
                    intent.putExtra(EXTRA_WEATHERSTATION, weatherStationName);
                    intent.putExtra("calling-activity", ActivityConstants.OverviewActivity);
                    startActivity(intent);
                } else if (item.getTitle().equals(getResources().getString(R.string.menu_weatherforecast))){
                    // starts nearest WeatherForecastActivity on click
                    Intent intent= new Intent(getApplicationContext(), WeatherForecastActivity.class);
                    SlopeAreaMapMarker nearstesMapMarker=distanceCalculater.calculateNearestMapMarker(listMapMarkers,"weatherstation");
                    String weatherStationName = nearstesMapMarker.getName();
                    intent.putExtra(EXTRA_WEATHERSTATION, weatherStationName);
                    intent.putExtra("calling-activity", ActivityConstants.OverviewActivity);
                    startActivity(intent);

                } else if (item.getTitle().equals(getResources().getString(R.string.menu_aboutus))){
                    // starts AboutUsActivity on click
                    Intent intent= new Intent(getApplicationContext(), AboutUsActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
        popup.show();
    }

    /**
     * This method set the list map marker list.
     * @param listMapMarkers The calculated map marker list.
     */

    public void setMapMarkers(ArrayList<SlopeAreaMapMarker> listMapMarkers) {
        this.listMapMarkers= new ArrayList<>();
        this.listMapMarkers=listMapMarkers;
        finishedListMapMarkerCalc=true;
    }

    /**
     * This method is called when the activity is paused.
     */

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * This method is called when the activity is destroyed and stops all threads.
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();

        distanceCalculater.removeLocationUpdates();
        stopUpdateAvalancheAreaButtonRunnable();
    }

    /**
     * This thread is used to update the slope avalanche bulletin button and icon depending on the ready finished async tasks.
     */

    //Needed to synchronize all AsyncTasks to start OverviewActivity and to guarantee finishing of async tasks
    Runnable updateAvalancheAreaButtonRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if(finishedListMapMarkerCalc){ //&& listMapMarkers!=null
                    SlopeAreaMapMarker nearestAvalancheArea = distanceCalculater.calculateNearestMapMarker(listMapMarkers,"avalanchebulletin");
                    nearestAvalancheBulletinImage.setImageBitmap(nearestAvalancheArea.getBitmap());
                    nearestAvalancheBulletinTitle.setText(nearestAvalancheArea.getName());
                    if(nearestAvalancheArea.getSlopeAmountDangerZones()!=0){
                        for(int i=0; i<nearestAvalancheArea.getSlopeAmountDangerZones();i++){
                            nearestAvalancheBulletinDangerZones.append("!");
                        }
                    } else{
                        nearestAvalancheBulletinDangerZones.setText("-");
                    }


                    finishedListMapMarkerCalc=false;
                    mHandlerForceExit=true;
                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                if(mHandlerForceExit){
                    mHandler.removeCallbacks(updateAvalancheAreaButtonRunnable);
                } else {
                    mHandler.postDelayed(updateAvalancheAreaButtonRunnable, mInterval);
                }

            }
        }
    };

    /**
     * This method starts the above checking thread.
     */

    public void startUpdateAvalancheAreaButtonRunnable() {
        updateAvalancheAreaButtonRunnable.run();
    }

    /**
     * This method stops the above checking thread.
     */

    public void stopUpdateAvalancheAreaButtonRunnable() {
        mHandler.removeCallbacks(updateAvalancheAreaButtonRunnable);
    }

    /**
     * This method reads all serialized files and set the respective variables.
     */

    private void readFilesOrCalculate() {
        File fileScenario= new File(getFilesDir(),ActivityConstants.ScenarioFileName);
        if(fileScenario.exists()) {
            choosenScenarioTime = DAOSerialisationManager.readSerializedFile(fileScenario.getAbsolutePath());
        }

        //find new Position
        distanceCalculater = new NearestMarkerCalculater(this,this);
        if(myPosition!=null && myPosition.getLatitude()!=0.0 && myPosition.getLongitude()!= 0.0) {
            startUpdateAvalancheAreaButtonRunnable();
        }

        File fileAvalancheBulletin= new File(getFilesDir(),ActivityConstants.AvalancheBulletinFileName);
        if(fileAvalancheBulletin.exists()){
            avalancheBulletinTyrol = DAOSerialisationManager.readSerializedFile(fileAvalancheBulletin.getAbsolutePath());
        }

        File fileListMapMarkers= new File(getFilesDir(),ActivityConstants.MapMarkerListFileName);
        if(fileListMapMarkers.exists()){
            listMapMarkers = new ArrayList<>();
            listMapMarkers = DAOSerialisationManager.readSerializedFile(fileListMapMarkers.getAbsolutePath());
            finishedListMapMarkerCalc=true;
        }


    }

    /**
     * This method is used to prevent to use the back arrow to go back to the scenario chooser activity and produce an strange error.
     */

    //preventing error with scenariochooser activity
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext() , OverviewActivity.class);
        startActivity(intent);
    }


}
