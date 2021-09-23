package at.ac.univie.lumi.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import at.ac.univie.lumi.controller.ActivityConstants;
import at.ac.univie.lumi.controller.DAOSerialisationManager;
import at.ac.univie.lumi.controller.LatLngJsonXmlExtractor;
import at.ac.univie.lumi.R;
import at.ac.univie.lumi.controller.NearestMarkerCalculater;
import at.ac.univie.lumi.model.AvalancheBulletinTyrol;
import at.ac.univie.lumi.model.SlopeAreaMapMarker;

/**
 *
 * This activity displays the emergency call information, helpful latitude, longitude and altitude of the user position and reads all serialized files.
 */

public class EmergencyCallActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageButton toolbarHome;
    private ImageButton toolbarMenu;

    private TextView heightContent;
    private TextView latitudeContent;
    private TextView longitudeContent;

    private LatLngJsonXmlExtractor latLngJsonXmlExtractor;

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
        setContentView(R.layout.activity_emergency_call);

        initializeVariables();

        readFilesOrCalculate();
    }

    /**
     * This method updates the latitude, longitude and the altitude of the xml file when the RestApiJSONAltitudeGetter background thread has finished.
     */

    public void updateLatLngAltitude(){
        if(LatLngJsonXmlExtractor.altitude==0.0){
            heightContent.setText("unbekannt");
        } else{
            heightContent.setText(String.format("%.2f", LatLngJsonXmlExtractor.altitude)+" m");
        }
        latitudeContent.setText(String.valueOf(LatLngJsonXmlExtractor.latitude)+"°");
        longitudeContent.setText(String.valueOf(LatLngJsonXmlExtractor.longitude)+"°");
    }

    /**
     * This method initialize all variables.
     */

    private void initializeVariables() {

        latLngJsonXmlExtractor= new LatLngJsonXmlExtractor();
        latLngJsonXmlExtractor.requestLatLngAltitude(this,this);

        heightContent = (TextView) findViewById(R.id.height_content);
        latitudeContent = (TextView) findViewById(R.id.latitude_content);
        longitudeContent = (TextView) findViewById(R.id.longitude_content);

        initializeToolbar();
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
        PopupMenu popup = new PopupMenu(EmergencyCallActivity.this, view);
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
     * This method is used to display the sos info popup window.
     * @param v The current view.
     */

    public void showSOSLegend(View v){
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) EmergencyCallActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.sos_infopopup,
                    (ViewGroup) findViewById(R.id.sos_info_popup));
            // create a PopupWindow
            PopupWindow pw = new PopupWindow(layout, 700, 1150, true);
            // display the popup in the center
            pw.showAtLocation(v, Gravity.CENTER, 0, 0);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to call the austrian mountain rescue.
     * @param v The current view.
     */

    public void callAustriaEmergencyCall(View v){
        Intent callIntent = new Intent(Intent.ACTION_CALL); //.ACTION_CALL for direct calling but this needs permission of user compared to .ACTION_DIAL
        callIntent.setData(Uri.parse("tel:1234")); //140
        //check permission
        //If the device is running Android 6.0 (API level 23) and the app's targetSdkVersion is 23 or higher,
        //the system asks the user to grant approval.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //request permission from user if the app hasn't got the required permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                    10);
        }else {     //have got permission
            try{
                startActivity(callIntent);  //call activity and make phone call
            }
            catch (android.content.ActivityNotFoundException ex){
                Toast.makeText(getApplicationContext(),"Ein Problem ist beim Anrufen aufgetreten!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This method is used to call the eurpean mountain rescue.
     * @param v The current view.
     */

    public void callEuropeEmergencyCall(View v){
        Intent callIntent = new Intent(Intent.ACTION_CALL); //.ACTION_CALL for direct calling but this needs permission of user compared to .ACTION_DIAL
        callIntent.setData(Uri.parse("tel:1234")); //112
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //request permission from user if the app hasn't got the required permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                    10);
        }else {     //have got permission
            try{
                startActivity(callIntent);  //call activity and make phone call
            }
            catch (android.content.ActivityNotFoundException ex){
                Toast.makeText(getApplicationContext(),"Ein Problem ist beim Anrufen aufgetreten!",Toast.LENGTH_SHORT).show();
            }
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
