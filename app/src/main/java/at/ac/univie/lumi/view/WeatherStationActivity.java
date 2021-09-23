package at.ac.univie.lumi.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

import at.ac.univie.lumi.R;
import at.ac.univie.lumi.controller.ActivityConstants;
import at.ac.univie.lumi.controller.DAOSerialisationManager;
import at.ac.univie.lumi.controller.NearestMarkerCalculater;
import at.ac.univie.lumi.controller.WeatherDataExtractor;
import at.ac.univie.lumi.model.AvalancheBulletinTyrol;
import at.ac.univie.lumi.model.SlopeAreaMapMarker;

/**
 *
 * This activity displays the weather station information of one weather station and reads all serialized files.
 */

public class WeatherStationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageButton toolbarHome;
    private ImageButton toolbarMenu;

    private TextView weatherStationTitle;
    private TextView weatherStationDateTime;
    private ImageView buttonWinddirection;
    private ImageView buttonAirtemperatureForecast;
    private ImageView buttonNewsnowForecast72h;
    private TextView weatherStationHeight;
    private TextView weatherStationWindDirection;
    private TextView weatherStationWindSpeed;
    private TextView weatherStationWindGustDirection;
    private TextView weatherStationWindGust;
    private TextView weatherStationAirTemperature;
    private TextView weatherStationRelHumidity;
    private TextView weatherStationSnowHeight;

    private WeatherDataExtractor weatherDataExtractor;

    private String weatherStationName;
    private ProgressDialog pDialog;

    public final static String EXTRA_BULLETIN = "at.ac.univie.lumi.BULLETIN";
    public final static String EXTRA_REGION = "at.ac.univie.lumi.REGION";
    public final static String EXTRA_WEATHERSTATION = "at.ac.univie.lumi.WEATHERSTATION";

    private AvalancheBulletinTyrol avalancheBulletinTyrol;

    private ArrayList<SlopeAreaMapMarker> listMapMarkers;
    private NearestMarkerCalculater distanceCalculater;

    private Calendar choosenScenarioTime;
    String choosenScenarioTimeString;

    /**
     * This method is called when the activity is ready. It initialize the different variables and read all serialized files. Moreover, it set the drop down menu and the top toolbar.
     * Furthermore, it displays the respective weather station data.
     * @param savedInstanceState Can be used to save the current state.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_station);


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

        setWeatherStationGraphicsImageButtons();

    }

    /**
     * This method is used to set the on click listener for the weather station graphics depending on the chosen scenario.
     */

    private void setWeatherStationGraphicsImageButtons() {

        buttonWinddirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog imageDialog = new Dialog(WeatherStationActivity.this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                imageDialog.setCancelable(true);
                imageDialog.setCanceledOnTouchOutside(true);
                imageDialog.setContentView(R.layout.graphic_dialog);
                ImageView imageView = (ImageView)imageDialog.findViewById(R.id.graphic_image);

                if(choosenScenarioTime!=null) {
                    switch (choosenScenarioTimeString) {
                        case ActivityConstants.Scenario1:
                            imageView.setImageResource(R.drawable.graphic_winddirection_scenario1);
                            break;
                        case ActivityConstants.Scenario2:
                            imageView.setImageResource(R.drawable.graphic_winddirection_scenario2);
                            break;
                        case ActivityConstants.Scenario3:
                            imageView.setImageResource(R.drawable.graphic_winddirection_scenario3);
                            break;
                        default:
                            break;
                    }
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
            }
        });

        buttonAirtemperatureForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog imageDialog = new Dialog(WeatherStationActivity.this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                imageDialog.setCancelable(true);
                imageDialog.setCanceledOnTouchOutside(true);
                imageDialog.setContentView(R.layout.graphic_dialog);
                ImageView imageView = (ImageView)imageDialog.findViewById(R.id.graphic_image);

                if(choosenScenarioTime!=null){
                    switch (choosenScenarioTimeString){
                        case ActivityConstants.Scenario1:
                            imageView.setImageResource(R.drawable.graphic_airtemperatureforecast_scenario1);
                            break;
                        case ActivityConstants.Scenario2:
                            imageView.setImageResource(R.drawable.graphic_airtemperatureforecast_scenario2);
                            break;
                        case ActivityConstants.Scenario3:
                            imageView.setImageResource(R.drawable.graphic_airtemperatureforecast_scenario3);
                            break;
                        default:
                            break;
                    }
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
            }
        });

        buttonNewsnowForecast72h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog imageDialog = new Dialog(WeatherStationActivity.this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                imageDialog.setCancelable(true);
                imageDialog.setCanceledOnTouchOutside(true);
                imageDialog.setContentView(R.layout.graphic_dialog);
                ImageView imageView = (ImageView)imageDialog.findViewById(R.id.graphic_image);

                if(choosenScenarioTimeString!=null){
                    switch (choosenScenarioTimeString){
                        case ActivityConstants.Scenario1:
                            imageView.setImageResource(R.drawable.graphic_newsnowforecast72h_scenario1);
                            break;
                        case ActivityConstants.Scenario2:
                            imageView.setImageResource(R.drawable.graphic_newsnowforecast72h_scenario2);
                            break;
                        case ActivityConstants.Scenario3:
                            imageView.setImageResource(R.drawable.graphic_newsnowforecast72h_scenario3);
                            break;
                        default:
                            break;
                    }
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
            }
        });

    }

    /**
     * This method initialize all variables and its functions.
     */

    private void initializeVariables() {
        weatherDataExtractor = new WeatherDataExtractor(this);
        weatherDataExtractor.execute(weatherStationName);


        weatherStationTitle = (TextView) findViewById(R.id.weatherstation_title);
        weatherStationDateTime = (TextView) findViewById(R.id.weatherstation_datetime);
        buttonWinddirection = (ImageView) findViewById(R.id.button_graphic_winddirection);
        buttonAirtemperatureForecast = (ImageView) findViewById(R.id.button_graphic_airtemperatureforecast);
        buttonNewsnowForecast72h = (ImageView) findViewById(R.id.button_graphic_newsnowforecast72h);
        weatherStationHeight = (TextView) findViewById(R.id.weatherstation_height_view);
        weatherStationWindDirection = (TextView) findViewById(R.id.weatherstation_winddirection_view);
        weatherStationWindSpeed = (TextView) findViewById(R.id.weatherstation_windspeed_view);
        weatherStationWindGustDirection = (TextView) findViewById(R.id.weatherstation_windgustdirection_view);
        weatherStationWindGust = (TextView) findViewById(R.id.weatherstation_windgust_view);
        weatherStationAirTemperature = (TextView) findViewById(R.id.weatherstation_airtemperature_view);
        weatherStationRelHumidity = (TextView) findViewById(R.id.weatherstation_relhumidity_view);
        weatherStationSnowHeight = (TextView) findViewById(R.id.weatherstation_snowheight_view);

        initializeToolbar();
    }

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
        PopupMenu popup = new PopupMenu(WeatherStationActivity.this, view);
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
            //create choosenScenarioTimeString
            if((choosenScenarioTime.get(Calendar.MONTH)+1)<10 && choosenScenarioTime.get(Calendar.DAY_OF_MONTH)<10){
                choosenScenarioTimeString=choosenScenarioTime.get(Calendar.YEAR)+"-0"+(choosenScenarioTime.get(Calendar.MONTH)+1)+"-0"+choosenScenarioTime.get(Calendar.DAY_OF_MONTH);
            } else if(choosenScenarioTime.get(Calendar.DAY_OF_MONTH)<10){
                choosenScenarioTimeString=choosenScenarioTime.get(Calendar.YEAR)+"-"+(choosenScenarioTime.get(Calendar.MONTH)+1)+"-0"+choosenScenarioTime.get(Calendar.DAY_OF_MONTH);
            }else if((choosenScenarioTime.get(Calendar.MONTH)+1)<10){
                choosenScenarioTimeString=choosenScenarioTime.get(Calendar.YEAR)+"-0"+(choosenScenarioTime.get(Calendar.MONTH)+1)+"-"+choosenScenarioTime.get(Calendar.DAY_OF_MONTH);
            }else{
                choosenScenarioTimeString=choosenScenarioTime.get(Calendar.YEAR)+"-"+(choosenScenarioTime.get(Calendar.MONTH)+1)+"-"+choosenScenarioTime.get(Calendar.DAY_OF_MONTH);
            }
        }

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
     * The different getter  methods of every variable of the weather station view.
     *
     */

    public TextView getWeatherStationTitle() {
        return weatherStationTitle;
    }


    public TextView getWeatherStationDateTime() {
        return weatherStationDateTime;
    }


    public TextView getWeatherStationHeight() {
        return weatherStationHeight;
    }


    public TextView getWeatherStationWindDirection() {
        return weatherStationWindDirection;
    }


    public TextView getWeatherStationWindSpeed() {
        return weatherStationWindSpeed;
    }


    public TextView getWeatherStationWindGust() {
        return weatherStationWindGust;
    }


    public TextView getWeatherStationAirTemperature() {
        return weatherStationAirTemperature;
    }


    public TextView getWeatherStationWindGustDirection() {
        return weatherStationWindGustDirection;
    }


    public TextView getWeatherStationRelHumidity() {
        return weatherStationRelHumidity;
    }


    public TextView getWeatherStationSnowHeight() {
        return weatherStationSnowHeight;
    }

}
