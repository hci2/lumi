package at.ac.univie.lumi.view;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import at.ac.univie.lumi.controller.ActivityConstants;
import at.ac.univie.lumi.controller.DAOSerialisationManager;
import at.ac.univie.lumi.controller.Helper;
import at.ac.univie.lumi.controller.LatLngJsonXmlExtractor;
import at.ac.univie.lumi.controller.MapMarkerAreaManager;
import at.ac.univie.lumi.controller.NearestMarkerCalculater;
import at.ac.univie.lumi.controller.OfflineMapManager;
import at.ac.univie.lumi.model.AvalancheBulletinTyrol;
import at.ac.univie.lumi.R;
import at.ac.univie.lumi.model.SlopeAreaMapMarker;

/**
 *
 * This activity displays the map and different legends, layers, map marker icons and the user position if requested. In addition, it reads all serialized files.
 */

public class MapActivity extends AppCompatActivity implements PermissionsListener, MapboxMap.OnMarkerClickListener {

    private MapView mapView;
    private static MapboxMap map;

    private LocationEngine locationEngine;
    private LocationEngineListener locationEngineListener;
    private PermissionsManager permissionsManager;

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageButton toolbarHome;
    private ImageButton toolbarMenu;

    private Toolbar navigationbar;
    private ImageButton navigationbarDownload;
    private ImageButton navigationbarLegend;
    private ImageButton navigationbarZoomin;
    private ImageButton navigationbarZoomout;
    private ImageButton navigationbarLocation;
    private ImageButton navigationbarLayer;

    //menu
    private PopupMenu popupLegend;

    private MapMarkerAreaManager mapMarkerAreaManager;
    private int mInterval =500;
    private Handler mHandler;
    public boolean asyncTaskMapMarkerAreaManager =false;
    public boolean asyncTaskXmlExtractor =false;
    public boolean asyncMapReady =false;
    public boolean mHandlerForceExit=false;

    public final static String EXTRA_BULLETIN = "at.ac.univie.lumi.BULLETIN";
    public final static String EXTRA_REGION = "at.ac.univie.lumi.REGION";
    public final static String EXTRA_WEATHERSTATION = "at.ac.univie.lumi.WEATHERSTATION";
    private String weatherStationName;

    private AvalancheBulletinTyrol avalancheBulletinTyrol;

    private ArrayList<SlopeAreaMapMarker> listMapMarkers;
    private NearestMarkerCalculater distanceCalculater;

    private Calendar choosenScenarioTime;
    private String choosenScenarioTimeString;


    private String mapStyle;

    private static final String styleSlopeareas="mapbox://styles/danglp44/cj3fhest000002rlpkoj33css";
    private static final String styleScenario1="mapbox://styles/danglp44/cj3q9tn2n000q2rs3mf4x3kin";
    private static final String styleScenario2="mapbox://styles/danglp44/cj45ruvel0ggv2snks92fbbr0";
    private static final String styleScenario3="mapbox://styles/danglp44/cj45rvd5a0gfb2rkbcpdi4qu4";
    private static final String styleSlopeangle="mapbox://styles/danglp44/cj3eo5nor000b2rntx8v4lu1e";
    private static final String styleExposition="mapbox://styles/danglp44/cj3ohd5tc000o2rp4qsboido9";
    private static final String styleGulliesBowls="mapbox://styles/danglp44/cj3fhq5of00022sn24ran4t68";
    private static final String styleBulge="mapbox://styles/danglp44/cj2sr3ags000s2sqonp6ee24i";

    //for displaying other icons in different zoom lvls
    private boolean zoomLvlAbove12=true;

    private int startGeneralMapMarkerView=0;
    private double startZoomLvl=0.0;


    private ProgressDialog progressDialog;
    private OfflineMapManager offlineMapManager;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    /**
     * This method is called when the activity is ready. It initialize the different variables and read all serialized files. Moreover, it set the drop down menu and the top toolbar.
     * It also includes the map read function.
     *
     * @param savedInstanceState Can be used to save the current state.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect Map with Mapbox SDK
        Mapbox.getInstance(this, getString(R.string.access_token_lumi));

        // This contains the MapView in XML and needs to be called after the account manager
        setContentView(R.layout.activity_map);

        initializeVariables();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;

                //get choose Style and set map with style
                Intent intent = getIntent();
                if(intent != null && intent.getExtras() != null) {
                    mapStyle = intent.getStringExtra(OverviewActivity.EXTRA_MAPSTYLE);
                    map.setStyleUrl(mapStyle);
                }

                UiSettings uiSettings=map.getUiSettings();
                moveAdaptCompassAttributionLocationButton(uiSettings);

                //create map markers
                mapMarkerAreaManager.execute(MapActivity.this);

                asyncMapReady=true;
            }
        });

        readFilesOrCalculate();
    }

    /**
     * This method moves the default location button and zoom the map view to the wattentaler lizum.
     * @param uiSettings The ui settings of the map object.
     */

    private void moveAdaptCompassAttributionLocationButton(UiSettings uiSettings){
        //move compass controls to bottom left
        //uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setCompassGravity(Gravity.LEFT); //CENTER_HORIZONTAL
        uiSettings.setCompassGravity(Gravity.BOTTOM);
        uiSettings.setCompassMargins(30,0,0,0);

        //move attribution and logo of mapbox
        uiSettings.setAttributionMargins(170,0,0,125);
        uiSettings.setLogoMargins(0,0,0,150);

        //set light blue user location icon on map
        map.getMyLocationViewSettings().setForegroundTintColor(Color.parseColor("#4285f4"));
        map.getMyLocationViewSettings().setAccuracyTintColor(Color.parseColor("#4285f4"));
        zoomMapToTarntalerKoepfe();
    }

    /**
     * This method initializes all variables.
     */

    private void initializeVariables() {
        startGeneralMapMarkerView=0;
        mapMarkerAreaManager=new MapMarkerAreaManager(MapActivity.this);

        mHandler = new Handler(getMainLooper());
        // Get the location engine object for later use.
        locationEngine = LocationSource.getLocationEngine(this);
        locationEngine.activate();

        mapView = (MapView) findViewById(R.id.mapview);

        initializeToolbar();
        initializeNavigationbar();
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
                // starts OverviewAcitivty on click
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
        PopupMenu popup = new PopupMenu(MapActivity.this, view);
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
     * This method reads all serialized files and set the respective variables.
     */

    private void readFilesOrCalculate() {
        File fileScenario= new File(getFilesDir(),ActivityConstants.ScenarioFileName);
        if(fileScenario.exists()){
            choosenScenarioTime = DAOSerialisationManager.readSerializedFile(fileScenario.getAbsolutePath());

            //parse calendar choosenScenarioTime to string
            if((choosenScenarioTime.get(Calendar.MONTH)+1)<10){
                choosenScenarioTimeString=choosenScenarioTime.get(Calendar.YEAR)+"-0"+(choosenScenarioTime.get(Calendar.MONTH)+1)+"-"+choosenScenarioTime.get(Calendar.DAY_OF_MONTH);
            } else if(choosenScenarioTime.get(Calendar.DAY_OF_MONTH)<10){
                choosenScenarioTimeString=choosenScenarioTime.get(Calendar.YEAR)+"-"+(choosenScenarioTime.get(Calendar.MONTH)+1)+"-0"+choosenScenarioTime.get(Calendar.DAY_OF_MONTH);
            }else if((choosenScenarioTime.get(Calendar.MONTH)+1)<10 && choosenScenarioTime.get(Calendar.DAY_OF_MONTH)<10){
                choosenScenarioTimeString=choosenScenarioTime.get(Calendar.YEAR)+"-0"+(choosenScenarioTime.get(Calendar.MONTH)+1)+"-0"+choosenScenarioTime.get(Calendar.DAY_OF_MONTH);
            }else{
                choosenScenarioTimeString=choosenScenarioTime.get(Calendar.YEAR)+"-"+(choosenScenarioTime.get(Calendar.MONTH)+1)+"-"+choosenScenarioTime.get(Calendar.DAY_OF_MONTH);
            }
        }

        //find new Position
        distanceCalculater = new NearestMarkerCalculater(this,this);

        File fileAvalancheBulletin= new File(getFilesDir(),ActivityConstants.AvalancheBulletinFileName);
        if(fileAvalancheBulletin.exists()){
            avalancheBulletinTyrol = DAOSerialisationManager.readSerializedFile(fileAvalancheBulletin.getAbsolutePath());
            asyncTaskXmlExtractor=true;

            File fileListMapMarkers= new File(getFilesDir(),ActivityConstants.MapMarkerListFileName);
            if(fileListMapMarkers.exists()){
                listMapMarkers = new ArrayList<>();
                listMapMarkers = DAOSerialisationManager.readSerializedFile(fileListMapMarkers.getAbsolutePath());
                asyncTaskMapMarkerAreaManager=true;
            }
        }
        startUpdatingMarkers();
    }

    /**
     * This method initialize the navigation bar on the bottom of the map.
     */

    private void initializeNavigationbar() {
        navigationbar = (Toolbar) findViewById(R.id.navigationbarMap);

        //calculate and set space for compass button
        navigationbar.post(new Runnable()
        {
            @Override
            public void run()
            {
                double navigationbarNewWidth=navigationbar.getWidth()*0.83;
                navigationbar.getLayoutParams().width=(int)navigationbarNewWidth;
            }
        });

        navigationbarDownload = (ImageButton) navigationbar.findViewById(R.id.navigationbar_mapdownload);
        navigationbarLegend = (ImageButton) navigationbar.findViewById(R.id.navigationbar_maplegend);
        navigationbarZoomin = (ImageButton) navigationbar.findViewById(R.id.navigationbar_zoomin);
        navigationbarZoomout = (ImageButton) navigationbar.findViewById(R.id.navigationbar_zoomout);
        navigationbarLocation = (ImageButton) navigationbar.findViewById(R.id.navigationbar_locationposition);
        navigationbarLayer = (ImageButton) navigationbar.findViewById(R.id.navigationbar_layer);

        navigationbarDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null) {
                    if(Helper.isNetworkAvailable(MapActivity.this)){
                        String choosenScenarioStyle="";
                        switch (choosenScenarioTimeString){
                            case ActivityConstants.Scenario1:
                                choosenScenarioStyle= getResources().getString(R.string.style_mapbox_scenario1);
                                break;
                            case ActivityConstants.Scenario2:
                                choosenScenarioStyle=  getResources().getString(R.string.style_mapbox_scenario2);
                                break;
                            case ActivityConstants.Scenario3:
                                choosenScenarioStyle=  getResources().getString(R.string.style_mapbox_scenario3);
                                break;
                            default:
                                break;
                        }
                        offlineMapManager = new OfflineMapManager(MapActivity.this, choosenScenarioStyle);
                        offlineMapManager.downloadMapOffline();
                    } else{
                        Toast.makeText(getApplicationContext(), getString(R.string.mapoffline_nointernet),  Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        navigationbarLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null) {
                    toggleGps(!map.isMyLocationEnabled());
                }
            }
        });

        navigationbarZoomin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null) {
                    map.animateCamera(CameraUpdateFactory.zoomIn());
                }
            }
        });
        navigationbarZoomout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null) {
                    map.animateCamera(CameraUpdateFactory.zoomOut());
                }
            }
        });

        navigationbarLegend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupLegend(v);
            }
        });

        navigationbarLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupLayer(v);
            }
        });
    }

    /**
     * This method initialize the popup legend and its respective view depending on the selected layer.
     * @param view The current view.
     */

    //show popup legend
    private void showPopupLegend(View view) {
        popupLegend = new PopupMenu(MapActivity.this, view);
        try {
            Field[] fields = popupLegend.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupLegend);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon",boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (map.getStyleUrl()){
            case styleSlopeareas:
                popupLegend.getMenuInflater().inflate(R.menu.popup_legend, popupLegend.getMenu());
                break;
            case styleScenario1:
                popupLegend.getMenuInflater().inflate(R.menu.popup_legend_dangerzones, popupLegend.getMenu());
                break;
            case styleScenario2:
                popupLegend.getMenuInflater().inflate(R.menu.popup_legend_dangerzones, popupLegend.getMenu());
                break;
            case styleScenario3:
                popupLegend.getMenuInflater().inflate(R.menu.popup_legend_dangerzones, popupLegend.getMenu());
                break;
            case styleExposition:
                popupLegend.getMenuInflater().inflate(R.menu.popup_legend_exposition, popupLegend.getMenu());
                break;
            case styleSlopeangle:
                popupLegend.getMenuInflater().inflate(R.menu.popup_legend_slopeangle, popupLegend.getMenu());
                break;
            case styleGulliesBowls:
                popupLegend.getMenuInflater().inflate(R.menu.popup_legend_gulliesbowls, popupLegend.getMenu());
                break;
            case styleBulge:
                popupLegend.getMenuInflater().inflate(R.menu.popup_legend_bulge, popupLegend.getMenu());
                break;
            default:
                popupLegend.getMenuInflater().inflate(R.menu.popup_legend, popupLegend.getMenu());
                break;
        }
        popupLegend.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        popupLegend.show();
    }

    /**
     * This method initialize the popup layer and its selectable different layers.
     * @param view The current view.
     */

    //show popup layer
    private void showPopupLayer(View view) {
        PopupMenu popup = new PopupMenu(MapActivity.this, view);
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
        popup.getMenuInflater().inflate(R.menu.popup_layer, popup.getMenu());


        switch (choosenScenarioTimeString){
                case ActivityConstants.Scenario1:
                    setLayerOnClickAction(popup, getResources().getString(R.string.style_mapbox_scenario1));
                    break;
                case ActivityConstants.Scenario2:
                    setLayerOnClickAction(popup, getResources().getString(R.string.style_mapbox_scenario2));
                    break;
                case ActivityConstants.Scenario3:
                    setLayerOnClickAction(popup, getResources().getString(R.string.style_mapbox_scenario3));
                    break;
                default:
                    break;
        }
        popup.show();
    }

    /**
     * This method set the layer on click action and change the style url depending on the user click.
     * @param popup The layer popup window.
     * @param styleChoosenScenario The current choosen scenario.
     */

    private void setLayerOnClickAction(PopupMenu popup, final String styleChoosenScenario) {
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                //switch to different overlays on click
                if(item.getTitle().equals(getResources().getString(R.string.layer_dangerzones_scenario))){
                    if(map.getStyleUrl().equalsIgnoreCase(getResources().getString(R.string.style_mapbox_slopeareas)) || !map.getStyleUrl().equalsIgnoreCase(styleChoosenScenario)){
                        map.setStyleUrl(styleChoosenScenario);
                    } else {
                        map.setStyleUrl(getResources().getString(R.string.style_mapbox_slopeareas));
                    }
                } else if (item.getTitle().equals(getResources().getString(R.string.layer_slopeangle))){
                    if(map.getStyleUrl().equalsIgnoreCase(getResources().getString(R.string.style_mapbox_slopeareas))|| !map.getStyleUrl().equalsIgnoreCase(getResources().getString(R.string.style_mapbox_slopeangle_general))){
                        map.setStyleUrl(getResources().getString(R.string.style_mapbox_slopeangle_general));

                    } else {
                        map.setStyleUrl(getResources().getString(R.string.style_mapbox_slopeareas));
                    }
                } else if (item.getTitle().equals(getResources().getString(R.string.layer_exposition))){
                    if(map.getStyleUrl().equalsIgnoreCase(getResources().getString(R.string.style_mapbox_slopeareas))|| !map.getStyleUrl().equalsIgnoreCase(getResources().getString(R.string.style_mapbox_exposition_perslopearea))){
                        map.setStyleUrl(getResources().getString(R.string.style_mapbox_exposition_perslopearea));

                    } else {
                        map.setStyleUrl(getResources().getString(R.string.style_mapbox_slopeareas));
                    }
                } else if (item.getTitle().equals(getResources().getString(R.string.layer_gullies_bowls))){
                    if(map.getStyleUrl().equalsIgnoreCase(getResources().getString(R.string.style_mapbox_slopeareas))|| !map.getStyleUrl().equalsIgnoreCase(getResources().getString(R.string.style_mapbox_gullies_bowls))){
                        map.setStyleUrl(getResources().getString(R.string.style_mapbox_gullies_bowls));

                    } else {
                        map.setStyleUrl(getResources().getString(R.string.style_mapbox_slopeareas));
                    }
                } else if (item.getTitle().equals(getResources().getString(R.string.layer_bulge))) {
                    if (map.getStyleUrl().equalsIgnoreCase(getResources().getString(R.string.style_mapbox_slopeareas))|| !map.getStyleUrl().equalsIgnoreCase(getResources().getString(R.string.style_mapbox_bulge))) {
                        map.setStyleUrl(getResources().getString(R.string.style_mapbox_bulge));

                    } else {
                        map.setStyleUrl(getResources().getString(R.string.style_mapbox_slopeareas));
                    }
                }
                return true;
            }
        });
    }

    /**
     * This method zoom the map view to the tarntaler köpfe mountains.
     */

    private void zoomMapToTarntalerKoepfe() {
        //zoom to target area Navis Wattener Lizum
        LatLngBounds latLngBoundsNavisWattenerLizum = getlatLngBounds("tarntalerköpfe");
        map.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsNavisWattenerLizum, 50), 0);
    }

    /**
     * This method returns the LatLngBounds of the requested type.
     * @param type The area of the LatLngBounds.
     * @return The LatLngBounds of the requested type.
     */

    private LatLngBounds getlatLngBounds(String type){
        LatLngBounds latLngBounds;
        if(type.equalsIgnoreCase("tuxalps")){
            latLngBounds = new LatLngBounds.Builder()
                    .include(new LatLng(47.336551, 11.839066)) // Northeast
                    .include(new LatLng(47.015018, 11.419449)) // Southwest
                    .build();

        } else if(type.equalsIgnoreCase("tyrolvorarlberg")){
            latLngBounds = new LatLngBounds.Builder()
                    .include(new LatLng(47.591450, 12.884521)) // Northeast
                    .include(new LatLng(46.762548, 9.566650)) // Southwest
                    .build();
        } else{ //tarntaler köpfe
            latLngBounds = new LatLngBounds.Builder()
                    .include(new LatLng(47.210762, 11.657448)) // Northeast
                    .include(new LatLng(47.122116, 11.576500)) // Southwest
                    .build();
        }
        return latLngBounds;
    }

    /**
     * This method is called on the start of the map.
     */

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    /**
     * This method is called when the user resumes to the map.
     */

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * This method is called when the user pauses the map view.
     */

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * This method is called when the user stops to the map.
     */

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    /**
     * This method is called when the battery of the smartphone is low.
     */

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * This method is called when the activity is destroyed and it stops all threads and the map.
     */


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopUpdatingMarkers();
        mapView.onDestroy();

        // Ensure no memory leak occurs if we register the location listener but the call hasn't
        // been made yet.
        if (locationEngineListener != null) {
            locationEngine.removeLocationEngineListener(locationEngineListener);
        }
        distanceCalculater.removeLocationUpdates();

    }

    /**
     * This method is used to save the current state to return on the map later with the same view where it stopped.
     * @param outState The last state.
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * This method starts the searching for the user gps position.
     * @param enableGps True if we are interested in the searching.
     */

    private void toggleGps(boolean enableGps) {
        if (enableGps) {
            // Check if user has granted location permission
            permissionsManager = new PermissionsManager(this);
            if (!PermissionsManager.areLocationPermissionsGranted(this)) {
                permissionsManager.requestLocationPermissions(this);
            } else {
                enableLocation(true);
            }
        } else {
            enableLocation(false);
        }
    }

    /**
     * This method enable the location and return the last location coordinates and change the location icon in the navigation bar.
     * @param enabled True if the permission is granted.
     */

    private void enableLocation(boolean enabled) {
        if (enabled) {
            // If we have the last location of the user, we can move the camera to that position.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},   //request specific permission from user
                        9);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},   //request specific permission from user
                        8);
                return;
            }
            Location lastLocation = locationEngine.getLastLocation();
            if (lastLocation != null) {
                LatLngJsonXmlExtractor.latitude = lastLocation.getLatitude();
                LatLngJsonXmlExtractor.longitude = lastLocation.getLongitude();
                LatLngJsonXmlExtractor.altitude = lastLocation.getAltitude();
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), 16));

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
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), 16));

                        //This prevents the whole time to focus to your location, and guarantee that it is only executed once
                        locationEngine.removeLocationEngineListener(this);
                    }
                }
            };
            locationEngine.addLocationEngineListener(locationEngineListener);
            navigationbarLocation.setImageResource(R.drawable.navigationbar_location_marked);
        } else {
            navigationbarLocation.setImageResource(R.drawable.navigationbar_location_unmarked);
            //zoom back to area
            zoomMapToTarntalerKoepfe();
        }
        // Enable or disable the location layer on the map
        map.setMyLocationEnabled(enabled);
    }

    /**
     * This method is called after the user grants or not the request for the permission.
     * @param requestCode The request code.
     * @param permissions The called permissions.
     * @param grantResults The result of the request.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * If an explanation for the permission request is needed.
     * @param permissionsToExplain A list of the permission which is needed to explain.
     */

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Diese App benötigt die Erlaubnis auf GPS Daten um ihre Position zu ermitteln.",
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
            Toast.makeText(this, "Sie haben keine Erlaubnis auf die Verwendung von GPS Daten vergeben.",
                    Toast.LENGTH_LONG).show();
            finish();
        }

    }

    /**
     * This method is used to handle the different map marker on click actions.
     * @param marker The clicked map marker.
     * @return Returns true if a map marker is clicked and not the map.
     */

    //click event of different markers
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if(marker.getTitle().equalsIgnoreCase("Tarntaler Köpfe")){
            Toast.makeText(this, "Es funktioniert",
                    Toast.LENGTH_LONG).show();

        }
        switch (marker.getSnippet()){
            case "weatherstation":
                Intent intent= new Intent(this, WeatherStationActivity.class);
                weatherStationName =marker.getTitle();
                intent.putExtra(EXTRA_WEATHERSTATION, weatherStationName);
                intent.putExtra("calling-activity", ActivityConstants.MapActivity);
                startActivity(intent);
                break;
            case "webcam":
                Toast.makeText(this, "Hier könnten Sie Webcamfotos sehen.",
                        Toast.LENGTH_LONG).show();
                break;
            case "observation":
                final Dialog imageDialog = new Dialog(MapActivity.this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                imageDialog.setCancelable(true);
                imageDialog.setCanceledOnTouchOutside(true);
                imageDialog.setContentView(R.layout.graphic_dialog);
                ImageView imageView = (ImageView)imageDialog.findViewById(R.id.graphic_image);

                switch (marker.getTitle()){
                    case "Lawinensprengung Pluderling":
                        imageView.setImageResource(R.drawable.graphic_avalanchepluderling);
                        break;
                    //and so on
                    default:
                        break;
                }
                Button btnClose = (Button)imageDialog.findViewById(R.id.button_close);
                imageDialog.getWindow().setBackgroundDrawable(null);

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        imageDialog.dismiss();
                    }
                });
                imageDialog.show();
                break;
            case "avalanchebulletin":
                intent = new Intent(this, AvalancheBulletinActivity.class);
                String regionName=marker.getTitle();
                intent.putExtra(EXTRA_BULLETIN, avalancheBulletinTyrol);
                intent.putExtra(EXTRA_REGION, regionName);
                intent.putExtra("calling-activity", ActivityConstants.MapActivity);
                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * This method return a access to the current map object.
     * @return Return the current map object.
     */

    public static MapboxMap getMap(){
        return map;
    }

    /**
     * This thread is used to check if the avalanche bulletin async task, themap marker area manager async task and the map async task are ready.
     * After this it update the map marker with the general icons on the first view and later with the slope specific icons.
     */

    //Needed to synchronize both AsyncTasks to change map icons to current icons of avalanche bulletin
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                if(asyncTaskMapMarkerAreaManager && asyncTaskXmlExtractor && asyncMapReady){
                    handleFirstMapZoomLvlView();

                    //initializeDifferentMapZoomLvlViews();
                    mHandlerForceExit=true;
                }
                //updateStatus(); //this function can change value of mInterval.

            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                if(mHandlerForceExit){
                    mHandler.removeCallbacks(mStatusChecker);
                } else {
                    mHandler.postDelayed(mStatusChecker, mInterval);
                }

            }
        }
    };

    /**
     * This method is used to start the above checking thread.
     */

    void startUpdatingMarkers() {
        mStatusChecker.run();
    }

    /**
     * This method is used to stop the above checking thread.
     */

    void stopUpdatingMarkers() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    /**
     * This method is used to set firstly the general map marker icons and after changing the zoom the slope specific map marker icons.
     */

    void handleFirstMapZoomLvlView(){
        map.setOnCameraChangeListener(new MapboxMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                if(startGeneralMapMarkerView==1 && position.zoom!=startZoomLvl){
                    mapMarkerAreaManager.updateMapMarkerDangerLvl(avalancheBulletinTyrol);
                    startGeneralMapMarkerView=2;
                }

                if(startGeneralMapMarkerView==0){
                    startZoomLvl=position.zoom;
                    startGeneralMapMarkerView = 1;
                }
            }

        });
        if(startGeneralMapMarkerView==0){
            mapMarkerAreaManager.updateGeneralMapMarkerDangerLvl(avalancheBulletinTyrol);
        }

    }

    /**
     * This method was an approach to view on the low zoom lvl only slope map marker with the highest values and if the user reaches a threshold it displays all icons.
     */

    void initializeDifferentMapZoomLvlViews(){
        map.setOnCameraChangeListener(new MapboxMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                if (position.zoom <= 11.5) {
                    if(zoomLvlAbove12){
                        mapMarkerAreaManager.removeAvalancheBulletinsBelowHighestValue();
                        zoomLvlAbove12=false;
                    }

                } else { //zoom lvl >13.2
                    if(!zoomLvlAbove12){
                        mapMarkerAreaManager.addAvalancheBulletinsBelowHighestValue(avalancheBulletinTyrol);
                        zoomLvlAbove12=true;
                    }

                }

            }
        });
    }

    /**
     * This method is used to show the progress dialog of the offline map downloading process.
     */

    public void showProgressDialog(){
        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Die Karte wird heruntergeladen ..");
        // Start and show the progress bar
        offlineMapManager.setEndNotified(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "Abbrechen", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Cancel download task
                progressDialog.cancel();
                //pause download
                offlineMapManager.getOfflineRegion().setDownloadState(OfflineRegion.STATE_INACTIVE);
            }
        });
        progressDialog.show();
        //do not allow the user to disturb the download process
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        offlineMapManager.setProgressDialog(progressDialog);
    }
}
