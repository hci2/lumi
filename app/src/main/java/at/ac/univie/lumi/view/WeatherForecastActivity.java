package at.ac.univie.lumi.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import at.ac.univie.lumi.R;
import at.ac.univie.lumi.controller.ActivityConstants;
import at.ac.univie.lumi.controller.DAOSerialisationManager;
import at.ac.univie.lumi.controller.NearestMarkerCalculater;
import at.ac.univie.lumi.controller.WeatherDataExtractor;
import at.ac.univie.lumi.model.AvalancheBulletinTyrol;
import at.ac.univie.lumi.model.SlopeAreaMapMarker;

/**
 *
 * This activity displays the weather station forecast information of one weather station for the last seven days and one future day. Moreover, it reads all serialized files.
 */

public class WeatherForecastActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    private WeatherDataExtractor weatherDataExtractor;
    private String weatherStationName;

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageButton toolbarHome;
    private ImageButton toolbarMenu;

    private TextView weatherForecastTitle;

    private TextView future1day_title;
    private TextView tempmin_future1day;
    private TextView tempmax_future1day;
    private TextView winddirection_future1day;
    private TextView windspeed_future1day;
    private TextView snow_future1day;
    private TextView relhumidity_future1day;

    private TextView today_title;
    private TextView tempmin_today;
    private TextView tempmax_today;
    private TextView winddirection_today;
    private TextView windspeed_today;
    private TextView snow_today;
    private TextView relhumidity_today;

    private TextView last1day_title;
    private TextView tempmin_last1day;
    private TextView tempmax_last1day;
    private TextView winddirection_last1day;
    private TextView windspeed_last1day;
    private TextView snow_last1day;
    private TextView relhumidity_last1day;

    private TextView last2day_title;
    private TextView tempmin_last2day;
    private TextView tempmax_last2day;
    private TextView winddirection_last2day;
    private TextView windspeed_last2day;
    private TextView snow_last2day;
    private TextView relhumidity_last2day;

    private TextView last3day_title;
    private TextView tempmin_last3day;
    private TextView tempmax_last3day;
    private TextView winddirection_last3day;
    private TextView windspeed_last3day;
    private TextView snow_last3day;
    private TextView relhumidity_last3day;

    private TextView last4day_title;
    private TextView tempmin_last4day;
    private TextView tempmax_last4day;
    private TextView winddirection_last4day;
    private TextView windspeed_last4day;
    private TextView snow_last4day;
    private TextView relhumidity_last4day;

    private TextView last5day_title;
    private TextView tempmin_last5day;
    private TextView tempmax_last5day;
    private TextView winddirection_last5day;
    private TextView windspeed_last5day;
    private TextView snow_last5day;
    private TextView relhumidity_last5day;

    private TextView last6day_title;
    private TextView tempmin_last6day;
    private TextView tempmax_last6day;
    private TextView winddirection_last6day;
    private TextView windspeed_last6day;
    private TextView snow_last6day;
    private TextView relhumidity_last6day;

    private TextView last7day_title;
    private TextView tempmin_last7day;
    private TextView tempmax_last7day;
    private TextView winddirection_last7day;
    private TextView windspeed_last7day;
    private TextView snow_last7day;
    private TextView relhumidity_last7day;

    public final static String EXTRA_BULLETIN = "at.ac.univie.lumi.BULLETIN";
    public final static String EXTRA_REGION = "at.ac.univie.lumi.REGION";
    public final static String EXTRA_WEATHERSTATION = "at.ac.univie.lumi.WEATHERSTATION";

    private AvalancheBulletinTyrol avalancheBulletinTyrol;

    private ArrayList<SlopeAreaMapMarker> listMapMarkers;
    private NearestMarkerCalculater distanceCalculater;


    /**
     * This method is called when the activity is ready. It initialize the different variables and read all serialized files. Moreover, it set the drop down menu and the top toolbar.
     * Furthermore, it displays the respective weather station forecast data.
     * @param savedInstanceState Can be used to save the current state.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        Intent intent = getIntent();
        if(intent != null && intent.getExtras() != null){
            int callingActivity = intent.getIntExtra("calling-activity", 0);

            switch (callingActivity){
                case ActivityConstants.MapActivity:
                    weatherStationName = getIntent().getStringExtra(MapActivity.EXTRA_WEATHERSTATION); //getExtras().getSerializable(MapActivity.EXTRA_WEATHERSTATION);
                    break;
                case ActivityConstants.OverviewActivity:
                    weatherStationName = getIntent().getStringExtra(OverviewActivity.EXTRA_WEATHERSTATION); //getExtras().getSerializable(MapActivity.EXTRA_WEATHERSTATION);
                    break;
                case ActivityConstants.AvalancheBulletinActivity:
                    //weatherStationName = getIntent().getStringExtra(AvalancheBulletinActivity.EXTRA_WEATHERSTATION); //getExtras().getSerializable(MapActivity.EXTRA_WEATHERSTATION);
                    break;
                case ActivityConstants.EmergencyCallActivity:
                    //weatherStationName = getIntent().getStringExtra(EmergencyCallActivity.EXTRA_WEATHERSTATION); //getExtras().getSerializable(MapActivity.EXTRA_WEATHERSTATION);
                    break;
                case ActivityConstants.WeatherStationActivity:
                    //weatherStationName = getIntent().getStringExtra(WeatherStationActivity.EXTRA_WEATHERSTATION); //getExtras().getSerializable(MapActivity.EXTRA_WEATHERSTATION);
                    break;
                default:
                    break;
            }
        }

        initializeVariables();

        readFilesOrCalculate();
    }

    /**
     * This method initializes all variables and its functions.
     */

    private void initializeVariables() {

        weatherDataExtractor = new WeatherDataExtractor(this);
        weatherDataExtractor.execute(weatherStationName);

        weatherForecastTitle = (TextView) findViewById(R.id.weatherforecast_title);

        future1day_title = (TextView) findViewById(R.id.weatherforecast_future1day_title);
        tempmin_future1day = (TextView) findViewById(R.id.weatherforecast_tempmin_future1day);
        tempmax_future1day = (TextView) findViewById(R.id.weatherforecast_tempmax_future1day);
        winddirection_future1day = (TextView) findViewById(R.id.weatherforecast_winddirection_future1day);
        windspeed_future1day = (TextView) findViewById(R.id.weatherforecast_windspeed_future1day);
        snow_future1day = (TextView) findViewById(R.id.weatherforecast_snow_future1day);
        relhumidity_future1day = (TextView) findViewById(R.id.weatherforecast_relhumidity_future1day);

        today_title = (TextView) findViewById(R.id.weatherforecast_today_title);
        tempmin_today = (TextView) findViewById(R.id.weatherforecast_tempmin_today);
        tempmax_today = (TextView) findViewById(R.id.weatherforecast_tempmax_today);
        winddirection_today = (TextView) findViewById(R.id.weatherforecast_winddirection_today);
        windspeed_today = (TextView) findViewById(R.id.weatherforecast_windspeed_today);
        snow_today = (TextView) findViewById(R.id.weatherforecast_snow_today);
        relhumidity_today = (TextView) findViewById(R.id.weatherforecast_relhumidity_today);

        last1day_title = (TextView) findViewById(R.id.weatherforecast_last1day_title);
        tempmin_last1day = (TextView) findViewById(R.id.weatherforecast_tempmin_last1day);
        tempmax_last1day = (TextView) findViewById(R.id.weatherforecast_tempmax_last1day);
        winddirection_last1day = (TextView) findViewById(R.id.weatherforecast_winddirection_last1day);
        windspeed_last1day = (TextView) findViewById(R.id.weatherforecast_windspeed_last1day);
        snow_last1day = (TextView) findViewById(R.id.weatherforecast_snow_last1day);
        relhumidity_last1day = (TextView) findViewById(R.id.weatherforecast_relhumidity_last1day);

        last2day_title = (TextView) findViewById(R.id.weatherforecast_last2day_title);
        tempmin_last2day = (TextView) findViewById(R.id.weatherforecast_tempmin_last2day);
        tempmax_last2day = (TextView) findViewById(R.id.weatherforecast_tempmax_last2day);
        winddirection_last2day = (TextView) findViewById(R.id.weatherforecast_winddirection_last2day);
        windspeed_last2day = (TextView) findViewById(R.id.weatherforecast_windspeed_last2day);
        snow_last2day = (TextView) findViewById(R.id.weatherforecast_snow_last2day);
        relhumidity_last2day = (TextView) findViewById(R.id.weatherforecast_relhumidity_last2day);

        last3day_title = (TextView) findViewById(R.id.weatherforecast_last3day_title);
        tempmin_last3day = (TextView) findViewById(R.id.weatherforecast_tempmin_last3day);
        tempmax_last3day = (TextView) findViewById(R.id.weatherforecast_tempmax_last3day);
        winddirection_last3day = (TextView) findViewById(R.id.weatherforecast_winddirection_last3day);
        windspeed_last3day = (TextView) findViewById(R.id.weatherforecast_windspeed_last3day);
        snow_last3day = (TextView) findViewById(R.id.weatherforecast_snow_last3day);
        relhumidity_last3day = (TextView) findViewById(R.id.weatherforecast_relhumidity_last3day);

        last4day_title = (TextView) findViewById(R.id.weatherforecast_last4day_title);
        tempmin_last4day = (TextView) findViewById(R.id.weatherforecast_tempmin_last4day);
        tempmax_last4day = (TextView) findViewById(R.id.weatherforecast_tempmax_last4day);
        winddirection_last4day = (TextView) findViewById(R.id.weatherforecast_winddirection_last4day);
        windspeed_last4day = (TextView) findViewById(R.id.weatherforecast_windspeed_last4day);
        snow_last4day = (TextView) findViewById(R.id.weatherforecast_snow_last4day);
        relhumidity_last4day = (TextView) findViewById(R.id.weatherforecast_relhumidity_last4day);

        last5day_title = (TextView) findViewById(R.id.weatherforecast_last5day_title);
        tempmin_last5day = (TextView) findViewById(R.id.weatherforecast_tempmin_last5day);
        tempmax_last5day = (TextView) findViewById(R.id.weatherforecast_tempmax_last5day);
        winddirection_last5day = (TextView) findViewById(R.id.weatherforecast_winddirection_last5day);
        windspeed_last5day = (TextView) findViewById(R.id.weatherforecast_windspeed_last5day);
        snow_last5day = (TextView) findViewById(R.id.weatherforecast_snow_last5day);
        relhumidity_last5day = (TextView) findViewById(R.id.weatherforecast_relhumidity_last5day);

        last6day_title = (TextView) findViewById(R.id.weatherforecast_last6day_title);
        tempmin_last6day = (TextView) findViewById(R.id.weatherforecast_tempmin_last6day);
        tempmax_last6day = (TextView) findViewById(R.id.weatherforecast_tempmax_last6day);
        winddirection_last6day = (TextView) findViewById(R.id.weatherforecast_winddirection_last6day);
        windspeed_last6day = (TextView) findViewById(R.id.weatherforecast_windspeed_last6day);
        snow_last6day = (TextView) findViewById(R.id.weatherforecast_snow_last6day);
        relhumidity_last6day = (TextView) findViewById(R.id.weatherforecast_relhumidity_last6day);

        last7day_title = (TextView) findViewById(R.id.weatherforecast_last7day_title);
        tempmin_last7day = (TextView) findViewById(R.id.weatherforecast_tempmin_last7day);
        tempmax_last7day = (TextView) findViewById(R.id.weatherforecast_tempmax_last7day);
        winddirection_last7day = (TextView) findViewById(R.id.weatherforecast_winddirection_last7day);
        windspeed_last7day = (TextView) findViewById(R.id.weatherforecast_windspeed_last7day);
        snow_last7day = (TextView) findViewById(R.id.weatherforecast_snow_last7day);
        relhumidity_last7day = (TextView) findViewById(R.id.weatherforecast_relhumidity_last7day);

        initializeToolbar();
    }

    /**
     * This method initializes the top toolbar and its functions.
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
     * @param view The current view.
     */


    //show popup menu
    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(WeatherForecastActivity.this, view);
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
        //find new Position
        distanceCalculater = new NearestMarkerCalculater(this,this);

        File fileAvalancheBulletin= new File(getFilesDir(),ActivityConstants.AvalancheBulletinFileName);
        if(fileAvalancheBulletin.exists()){
            avalancheBulletinTyrol = DAOSerialisationManager.readSerializedFile(fileAvalancheBulletin.getAbsolutePath());
        }

        File fileListMapMarkers= new File(getFilesDir(),ActivityConstants.MapMarkerListFileName);
        if(fileListMapMarkers.exists()){
            listMapMarkers = new ArrayList<>();
            listMapMarkers = DAOSerialisationManager.readSerializedFile(fileListMapMarkers.getAbsolutePath());
        }

    }

    /**
     * This method starts to show the wait dialog for loading of the data.
     */

    public void showWaitDialog(){
        pDialog = ProgressDialog.show(this,
                "Abrufen von Details..", "Bitte warten...", true);
        weatherDataExtractor.setWaitDialog(pDialog);
    }

    /**
     * This method is used when the current activity will be paused.
     */

    @Override
    public void onPause(){

        super.onPause();

        if(pDialog != null)
            pDialog.dismiss();

    }

    /**
     * This method is used when the current activity will be destroyed and will stop all threads.
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        distanceCalculater.removeLocationUpdates();
    }

    /**
     * The different getter  methods of every variable of the weather forecast view.
     *
     */

    public TextView getWeatherForecastTitle() {
        return weatherForecastTitle;
    }

    public TextView getFuture1day_title() {
        return future1day_title;
    }

    public TextView getTempmin_future1day() {
        return tempmin_future1day;
    }

    public TextView getTempmax_future1day() {
        return tempmax_future1day;
    }

    public TextView getWinddirection_future1day() {
        return winddirection_future1day;
    }

    public TextView getWindspeed_future1day() {
        return windspeed_future1day;
    }

    public TextView getSnow_future1day() {
        return snow_future1day;
    }

    public TextView getRelhumidity_future1day() {
        return relhumidity_future1day;
    }

    public TextView getToday_title() {
        return today_title;
    }

    public TextView getTempmin_today() {
        return tempmin_today;
    }

    public TextView getTempmax_today() {
        return tempmax_today;
    }

    public TextView getWinddirection_today() {
        return winddirection_today;
    }

    public TextView getWindspeed_today() {
        return windspeed_today;
    }

    public TextView getSnow_today() {
        return snow_today;
    }

    public TextView getRelhumidity_today() {
        return relhumidity_today;
    }

    public TextView getLast1day_title() {
        return last1day_title;
    }

    public TextView getTempmin_last1day() {
        return tempmin_last1day;
    }

    public TextView getTempmax_last1day() {
        return tempmax_last1day;
    }

    public TextView getWinddirection_last1day() {
        return winddirection_last1day;
    }

    public TextView getWindspeed_last1day() {
        return windspeed_last1day;
    }

    public TextView getSnow_last1day() {
        return snow_last1day;
    }

    public TextView getRelhumidity_last1day() {
        return relhumidity_last1day;
    }

    public TextView getLast2day_title() {
        return last2day_title;
    }

    public TextView getTempmin_last2day() {
        return tempmin_last2day;
    }

    public TextView getTempmax_last2day() {
        return tempmax_last2day;
    }

    public TextView getWinddirection_last2day() {
        return winddirection_last2day;
    }

    public TextView getWindspeed_last2day() {
        return windspeed_last2day;
    }

    public TextView getSnow_last2day() {
        return snow_last2day;
    }

    public TextView getRelhumidity_last2day() {
        return relhumidity_last2day;
    }

    public TextView getLast3day_title() {
        return last3day_title;
    }

    public TextView getTempmin_last3day() {
        return tempmin_last3day;
    }

    public TextView getTempmax_last3day() {
        return tempmax_last3day;
    }

    public TextView getWinddirection_last3day() {
        return winddirection_last3day;
    }

    public TextView getWindspeed_last3day() {
        return windspeed_last3day;
    }

    public TextView getSnow_last3day() {
        return snow_last3day;
    }

    public TextView getRelhumidity_last3day() {
        return relhumidity_last3day;
    }

    public TextView getLast4day_title() {
        return last4day_title;
    }

    public TextView getTempmin_last4day() {
        return tempmin_last4day;
    }

    public TextView getTempmax_last4day() {
        return tempmax_last4day;
    }

    public TextView getWinddirection_last4day() {
        return winddirection_last4day;
    }

    public TextView getWindspeed_last4day() {
        return windspeed_last4day;
    }

    public TextView getSnow_last4day() {
        return snow_last4day;
    }

    public TextView getRelhumidity_last4day() {
        return relhumidity_last4day;
    }

    public TextView getLast5day_title() {
        return last5day_title;
    }

    public TextView getTempmin_last5day() {
        return tempmin_last5day;
    }

    public TextView getTempmax_last5day() {
        return tempmax_last5day;
    }

    public TextView getWinddirection_last5day() {
        return winddirection_last5day;
    }

    public TextView getWindspeed_last5day() {
        return windspeed_last5day;
    }

    public TextView getSnow_last5day() {
        return snow_last5day;
    }

    public TextView getRelhumidity_last5day() {
        return relhumidity_last5day;
    }

    public TextView getLast6day_title() {
        return last6day_title;
    }

    public TextView getTempmin_last6day() {
        return tempmin_last6day;
    }

    public TextView getTempmax_last6day() {
        return tempmax_last6day;
    }

    public TextView getWinddirection_last6day() {
        return winddirection_last6day;
    }

    public TextView getWindspeed_last6day() {
        return windspeed_last6day;
    }

    public TextView getSnow_last6day() {
        return snow_last6day;
    }

    public TextView getRelhumidity_last6day() {
        return relhumidity_last6day;
    }

    public TextView getLast7day_title() {
        return last7day_title;
    }

    public TextView getTempmin_last7day() {
        return tempmin_last7day;
    }

    public TextView getTempmax_last7day() {
        return tempmax_last7day;
    }

    public TextView getWinddirection_last7day() {
        return winddirection_last7day;
    }

    public TextView getWindspeed_last7day() {
        return windspeed_last7day;
    }

    public TextView getSnow_last7day() {
        return snow_last7day;
    }

    public TextView getRelhumidity_last7day() {
        return relhumidity_last7day;
    }
}
