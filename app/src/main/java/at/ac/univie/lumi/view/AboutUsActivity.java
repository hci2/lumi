package at.ac.univie.lumi.view;

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
import at.ac.univie.lumi.model.AvalancheBulletinTyrol;
import at.ac.univie.lumi.model.SlopeAreaMapMarker;

/**
 * This activity displays the about us information and reads all serialized files.
 */

public class AboutUsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageButton toolbarHome;
    private ImageButton toolbarMenu;

    public final static String EXTRA_BULLETIN = "at.ac.univie.lumi.BULLETIN";
    public final static String EXTRA_REGION = "at.ac.univie.lumi.REGION";
    public final static String EXTRA_WEATHERSTATION = "at.ac.univie.lumi.WEATHERSTATION";

    private AvalancheBulletinTyrol avalancheBulletinTyrol;

    private ArrayList<SlopeAreaMapMarker> listMapMarkers;
    private NearestMarkerCalculater distanceCalculater;

    /**
     * This method is called when the activity is ready. It initialize the different variables and read all serialized files. Moreover, it set the drop down menu and the top toolbar.
     * @param savedInstanceState Can be used to save the current state.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        initializeToolbar();

        readFilesOrCalculate();
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
     * @param view The current view.
     */

    //show popup menu
    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(AboutUsActivity.this, view);
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
     * This method is called when the activity is destroyed and stops all threads.
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        distanceCalculater.removeLocationUpdates();
    }
}
