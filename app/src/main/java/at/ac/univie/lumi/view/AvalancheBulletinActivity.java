package at.ac.univie.lumi.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

import at.ac.univie.lumi.controller.ActivityConstants;
import at.ac.univie.lumi.controller.DAOSerialisationManager;
import at.ac.univie.lumi.controller.Helper;
import at.ac.univie.lumi.controller.NearestMarkerCalculater;
import at.ac.univie.lumi.model.AvalancheBulletinTyrol;
import at.ac.univie.lumi.model.AvalancheProblemTyrol;
import at.ac.univie.lumi.R;
import at.ac.univie.lumi.model.AvalancheSlopeAreaBulletinTyrol;
import at.ac.univie.lumi.model.SlopeAreaMapMarker;

/**
 *
 * This activity displays the avalanche bulletin information and also the slope specific information and reads all serialized files.
 */

public class AvalancheBulletinActivity extends AppCompatActivity {

    private TextView bulletinTitle;
    private TextView bulletinDateTime;
    private boolean isMorning=true;

    //slope specific details
    private ImageView slopeDangerLvl;
    private TextView slopeamountdangerzones;
    private TextView slopeAdditionalLoad;
    private TextView slopeMinSH;
    private TextView slopeMaxSH;
    private TextView slopeExposition;
    private TextView slopeAngle;
    private TextView slopeAvalancheProblems;

    //general avalanche bulletin region 6, tuxer alps
    private TextView bulletinGeneralTitle;
    private ImageView bulletinDangerLvlBelow;
    private TextView bulletinDangerLvlBelowDescription;
    private ImageView bulletinDangerLvlAbove;
    private TextView bulletinDangerLvlAboveDescription;
    private ImageView bulletinProblem1;
    private ImageView bulletinProblem2;
    private ImageView bulletinProblem3;
    private TextView bulletinAccidents;
    private ImageView bulletinExposition1;
    private ImageView bulletinExposition2;
    private ImageView bulletinExposition3;
    private TextView bulletinDangerousHeightsText1;
    private ImageView bulletinDangerousHeightsImage1;
    private TextView bulletinDangerousHeightsText2;
    private ImageView bulletinDangerousHeightsImage2;
    private TextView bulletinDangerpattern;

    //text description
    private TextView bulletinDangerAssessmentTitle;
    private TextView bulletinDangerAssessment;
    private TextView bulletinSnowpackstructureTitle;
    private TextView bulletinSnowpackstructure;
    private TextView bulletinZamgweatherTitle;
    private TextView bulletinZamgweather;
    private TextView bulletinTendencyTitle;
    private TextView bulletinTendency;
    private TextView bulletinWriter;


    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageButton toolbarHome;
    private ImageButton toolbarMenu;

    private String areaName;

    public final static String EXTRA_BULLETIN = "at.ac.univie.lumi.BULLETIN";
    public final static String EXTRA_REGION = "at.ac.univie.lumi.REGION";
    public final static String EXTRA_WEATHERSTATION = "at.ac.univie.lumi.WEATHERSTATION";

    private AvalancheBulletinTyrol avalancheBulletinTyrol;

    private ArrayList<SlopeAreaMapMarker> listMapMarkers;
    private NearestMarkerCalculater distanceCalculater;

    private String choosenScenarioTimeString;


    /**
     * This method is called when the activity is ready. It initialize the different variables and read all serialized files. Moreover, it set the drop down menu and the top toolbar.
     * It also set the slope name depending on the calling activity and extract the avalanche bulletin model.
     * @param savedInstanceState Can be used to save the current state.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avalanche_bulletin);

        Intent intent = getIntent();
        if(intent != null && intent.getExtras() != null) {
            int callingActivity = intent.getIntExtra("calling-activity", 0);

            switch (callingActivity) {
                case ActivityConstants.MapActivity:
                    areaName = getIntent().getExtras().getString(MapActivity.EXTRA_REGION);
                    break;
                case ActivityConstants.OverviewActivity:
                    areaName = getIntent().getExtras().getString(OverviewActivity.EXTRA_REGION);
                    break;
                case ActivityConstants.AvalancheBulletinActivity:
                    areaName =getIntent().getExtras().getString(AvalancheBulletinActivity.EXTRA_REGION);
                    break;
                case ActivityConstants.EmergencyCallActivity:
                    areaName =getIntent().getExtras().getString(EmergencyCallActivity.EXTRA_REGION);
                    break;
                case ActivityConstants.WeatherStationActivity:
                    areaName =getIntent().getExtras().getString(WeatherStationActivity.EXTRA_REGION);
                    break;
                case ActivityConstants.WeatherForecastActivity:
                    areaName =getIntent().getExtras().getString(WeatherForecastActivity.EXTRA_REGION);
                    break;
                case ActivityConstants.AboutUsActivity:
                    areaName =getIntent().getExtras().getString(AboutUsActivity.EXTRA_REGION);
                    break;
                default:
                    break;
            }
        }
        initializeVariables();

        readFilesOrCalculate();

        extractSetTitleSubtitel();

        extractSetSlopeSpecificAvalancheBulletin();

        extractSetGeneralAvalancheBulletin();
    }

    /**
     * This method extract the title and date and time of the avalanche bulletin model.
     */

    private void extractSetTitleSubtitel() {
        bulletinTitle.setText(areaName);

        Calendar calendar = avalancheBulletinTyrol.getDateTime();
        Calendar currentTime = Calendar.getInstance();

        File fileScenario= new File(getFilesDir(),ActivityConstants.ScenarioFileName);
        if(fileScenario.exists()) {
            Calendar choosenScenarioTime = DAOSerialisationManager.readSerializedFile(fileScenario.getAbsolutePath());

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


        if(currentTime.get(Calendar.HOUR_OF_DAY)>=0 && currentTime.get(Calendar.HOUR_OF_DAY)<=11){
            if(!choosenScenarioTimeString.equals("") && choosenScenarioTimeString.equalsIgnoreCase(ActivityConstants.Scenario3)){
                bulletinDateTime.setText("14.5.2017, um "+String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)+":"+String.valueOf(calendar.get(Calendar.MINUTE)))+" Uhr, Vormittag");
            } else{
                bulletinDateTime.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"."+String.valueOf(calendar.get(Calendar.MONTH)+1)+"."+String.valueOf(calendar.get(Calendar.YEAR)+", um "+String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)+":"+String.valueOf(calendar.get(Calendar.MINUTE)))+" Uhr, Vormittag"));
            }
            isMorning=true;
        } else {
            if(!choosenScenarioTimeString.equals("") && choosenScenarioTimeString.equalsIgnoreCase(ActivityConstants.Scenario3)){
                bulletinDateTime.setText("14.5.2017, um "+String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)+":"+String.valueOf(calendar.get(Calendar.MINUTE)))+" Uhr, Nachmittag");
            } else{
                bulletinDateTime.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"."+String.valueOf(calendar.get(Calendar.MONTH)+1)+"."+String.valueOf(calendar.get(Calendar.YEAR)+", um "+String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)+":"+String.valueOf(calendar.get(Calendar.MINUTE)))+" Uhr, Nachmittag"));
            }
            isMorning=false;
        }
    }

    /**
     * This method extract the vars for the slope specific description of the avalanche bulletin of the avalanche bulletin model.
     */

    private void extractSetSlopeSpecificAvalancheBulletin() {
        ArrayList<AvalancheSlopeAreaBulletinTyrol> avalancheSlopeAreaBulletinTyrolArrayList = avalancheBulletinTyrol.getAvalancheSlopeAreaBulletinTyrolArrayList();
        AvalancheSlopeAreaBulletinTyrol currentArea;

        //get right area and set values
        for(int i=0; i<avalancheSlopeAreaBulletinTyrolArrayList.size();i++){
            if(avalancheSlopeAreaBulletinTyrolArrayList.get(i).getRegionName().equalsIgnoreCase(areaName)){
                currentArea=avalancheSlopeAreaBulletinTyrolArrayList.get(i);

                switch (currentArea.getSlopeDangerLvl()){
                    case 0:
                        slopeDangerLvl.setImageResource(R.drawable.marker_slopedangerlvl_unknown);
                        break;
                    case 1:
                        slopeDangerLvl.setImageResource(R.drawable.marker_slopedangerlvl1);
                        break;
                    case 2:
                        slopeDangerLvl.setImageResource(R.drawable.marker_slopedangerlvl2);
                        break;
                    case 3:
                        slopeDangerLvl.setImageResource(R.drawable.marker_slopedangerlvl3);
                        break;
                    case 4:
                        slopeDangerLvl.setImageResource(R.drawable.marker_slopedangerlvl4);
                        break;
                    case 5:
                        slopeDangerLvl.setImageResource(R.drawable.marker_slopedangerlvl5);
                        break;
                    default:
                        slopeDangerLvl.setImageResource(R.drawable.marker_slopedangerlvl_unknown);
                        break;
                }
                switch (currentArea.getSlopeAmountDangerZones()){
                    case 0:
                        slopeamountdangerzones.setText("-");
                        break;
                    case 1:
                        slopeamountdangerzones.setText("!");
                        break;
                    case 2:
                        slopeamountdangerzones.setText("!!");
                        break;
                    case 3:
                        slopeamountdangerzones.setText("!!!");
                        slopeamountdangerzones.setTextSize(20);
                        break;
                    case 4:
                        slopeamountdangerzones.setText("!!!!");
                        slopeamountdangerzones.setTextSize(20);
                        break;
                    case 5:
                        slopeamountdangerzones.setText("!!!!!");
                        slopeamountdangerzones.setTextSize(20);
                        break;
                    case 6:
                        slopeamountdangerzones.setText("!!!!!!");
                        slopeamountdangerzones.setTextSize(20);
                        slopeAdditionalLoad.setTypeface(null, Typeface.BOLD);
                        break;
                    case 7:
                        slopeamountdangerzones.setText("!!!!!!!");
                        slopeamountdangerzones.setTextSize(20);
                        slopeAdditionalLoad.setTypeface(null, Typeface.BOLD);
                        break;
                    case 8:
                        slopeamountdangerzones.setText("!!!!!!!!");
                        slopeamountdangerzones.setTextSize(20);
                        slopeAdditionalLoad.setTypeface(null, Typeface.BOLD);
                        break;
                    case 9:
                        slopeamountdangerzones.setText("!!!!!!!!!");
                        slopeamountdangerzones.setTextSize(20);
                        slopeAdditionalLoad.setTypeface(null, Typeface.BOLD);
                        break;
                    case 10:
                        slopeamountdangerzones.setText("!!!!!!!!!!");
                        slopeamountdangerzones.setTextSize(20);
                        slopeAdditionalLoad.setTypeface(null, Typeface.BOLD);
                        break;
                    default:
                        slopeamountdangerzones.setText("-");
                        break;
                }

                slopeamountdangerzones.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            //We need to get the instance of the LayoutInflater, use the context of this activity
                            LayoutInflater inflater = (LayoutInflater) AvalancheBulletinActivity.this
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            //Inflate the view from a predefined XML layout
                            View layout = inflater.inflate(R.layout.avalanchebulletin_infopopup_dangerzones,
                                    (ViewGroup) findViewById(R.id.avalanchebulletin_infopopup_dangerzones));
                            // create a PopupWindow
                            PopupWindow pw = new PopupWindow(layout, 700, 1200, true);
                            // display the popup in the center
                            pw.showAtLocation(v, Gravity.CENTER, 0, 0);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


                switch (currentArea.getAdditionalLoad()){
                    case "groß":
                        //slopeAdditionalLoad.setTextSize(10);
                        slopeAdditionalLoad.setTextSize(15);
                        slopeAdditionalLoad.setText("! ("+currentArea.getAdditionalLoad()+")");
                        break;
                    case "mittel":
                        slopeAdditionalLoad.setTextSize(20);
                        slopeAdditionalLoad.setText("! ("+currentArea.getAdditionalLoad()+")");
                        break;
                    case "gering":
                        slopeAdditionalLoad.setTextSize(20);
                        slopeAdditionalLoad.setTypeface(null, Typeface.BOLD);
                        slopeAdditionalLoad.setText("! ("+currentArea.getAdditionalLoad()+")");
                        break;
                    default:
                        break;
                }

                slopeAdditionalLoad.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            //We need to get the instance of the LayoutInflater, use the context of this activity
                            LayoutInflater inflater = (LayoutInflater) AvalancheBulletinActivity.this
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            //Inflate the view from a predefined XML layout
                            View layout = inflater.inflate(R.layout.avalanchebulletin_infopopup_additionalload,
                                    (ViewGroup) findViewById(R.id.avalanchebulletin_infopopup_additionalload));
                            // create a PopupWindow
                            PopupWindow pw = new PopupWindow(layout, 700, 1500, true);
                            // display the popup in the center
                            pw.showAtLocation(v, Gravity.CENTER, 0, 0);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                slopeMinSH.setText(String.valueOf(currentArea.getMinSH())+" m");
                slopeMaxSH.setText(String.valueOf(currentArea.getMaxSH())+" m");

                slopeAngle.setText(currentArea.getSlopeAngleString());
                slopeExposition.setText(currentArea.getExpositionString());

                //set avalanche probs
                int u;
                for(u=0; u<currentArea.getListAvalancheProblems().size()-1;u++){
                    slopeAvalancheProblems.append((currentArea.getListAvalancheProblems().get(u))+", ");
                }
                if(currentArea.getListAvalancheProblems().size()!=0){
                    slopeAvalancheProblems.append((currentArea.getListAvalancheProblems().get(u)));
                }
                if(slopeAvalancheProblems.getText().equals("")){
                    slopeAvalancheProblems.setText("-");
                }
                slopeAvalancheProblems.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if(Helper.isNetworkAvailable(AvalancheBulletinActivity.this)){
                            Helper.goToURL(AvalancheBulletinActivity.this,getResources().getString(R.string.link_avalancheproblems));
                        } else{
                            Toast.makeText(getApplicationContext(), getString(R.string.link_nointernet),  Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        }

    }

    /**
     * This method extract the vars for the general description of the avalanche bulletin of the avalanche bulletin model.
     */

    private void extractSetGeneralAvalancheBulletin() {
        //we are currently only in Region 6 (tux alps) southeast of Innsbruck interested
        bulletinGeneralTitle.setText(getResources().getString(R.string.bulletin_generaltitle));

        if(isMorning){
            switch (avalancheBulletinTyrol.getRegion6DangerLevelMorningBorderAbove()){
                case 1:
                    bulletinDangerLvlAbove.setImageResource(R.drawable.marker_gefahrenstufen_1);
                    break;
                case 2:
                    bulletinDangerLvlAbove.setImageResource(R.drawable.marker_gefahrenstufen_2);
                    break;
                case 3:
                    bulletinDangerLvlAbove.setImageResource(R.drawable.marker_gefahrenstufen_3);
                    break;
                case 4:
                    bulletinDangerLvlAbove.setImageResource(R.drawable.marker_gefahrenstufen_4);
                    break;
                case 5:
                    bulletinDangerLvlAbove.setImageResource(R.drawable.marker_gefahrenstufen_5);
                    break;
                default:
                    break;
            }
            switch (avalancheBulletinTyrol.getRegion6DangerLevelMorningBorderBelow()){
                case 1:
                    bulletinDangerLvlBelow.setImageResource(R.drawable.marker_gefahrenstufen_1);
                    break;
                case 2:
                    bulletinDangerLvlBelow.setImageResource(R.drawable.marker_gefahrenstufen_2);
                    break;
                case 3:
                    bulletinDangerLvlBelow.setImageResource(R.drawable.marker_gefahrenstufen_3);
                    break;
                case 4:
                    bulletinDangerLvlBelow.setImageResource(R.drawable.marker_gefahrenstufen_4);
                    break;
                case 5:
                    bulletinDangerLvlBelow.setImageResource(R.drawable.marker_gefahrenstufen_5);
                    break;
                default:
                    break;
            }
            bulletinDangerLvlBelowDescription.setText("unter "+avalancheBulletinTyrol.getRegion6DangerLevelMorningBorder()+" m");
            bulletinDangerLvlAboveDescription.setText("über "+avalancheBulletinTyrol.getRegion6DangerLevelMorningBorder()+" m");

        }else{
            switch (avalancheBulletinTyrol.getRegion6DangerLevelAfternoonBorderAbove()){
                case 1:
                    bulletinDangerLvlAbove.setImageResource(R.drawable.marker_gefahrenstufen_1);
                    break;
                case 2:
                    bulletinDangerLvlAbove.setImageResource(R.drawable.marker_gefahrenstufen_2);
                    break;
                case 3:
                    bulletinDangerLvlAbove.setImageResource(R.drawable.marker_gefahrenstufen_3);
                    break;
                case 4:
                    bulletinDangerLvlAbove.setImageResource(R.drawable.marker_gefahrenstufen_4);
                    break;
                case 5:
                    bulletinDangerLvlAbove.setImageResource(R.drawable.marker_gefahrenstufen_5);
                    break;
                default:
                    break;
            }
            switch (avalancheBulletinTyrol.getRegion6DangerLevelAfternoonBorderBelow()){
                case 1:
                    bulletinDangerLvlBelow.setImageResource(R.drawable.marker_gefahrenstufen_1);
                    break;
                case 2:
                    bulletinDangerLvlBelow.setImageResource(R.drawable.marker_gefahrenstufen_2);
                    break;
                case 3:
                    bulletinDangerLvlBelow.setImageResource(R.drawable.marker_gefahrenstufen_3);
                    break;
                case 4:
                    bulletinDangerLvlBelow.setImageResource(R.drawable.marker_gefahrenstufen_4);
                    break;
                case 5:
                    bulletinDangerLvlBelow.setImageResource(R.drawable.marker_gefahrenstufen_5);
                    break;
                default:
                    break;
            }
            bulletinDangerLvlBelowDescription.setText("unter "+avalancheBulletinTyrol.getRegion6DangerLevelAfternoonBorder()+" m");
            bulletinDangerLvlAboveDescription.setText("über "+avalancheBulletinTyrol.getRegion6DangerLevelAfternoonBorder()+" m");
        }
        bulletinDangerLvlBelow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(Helper.isNetworkAvailable(AvalancheBulletinActivity.this)){
                    Helper.goToURL(AvalancheBulletinActivity.this,getResources().getString(R.string.link_dangerlvl));
                } else{
                    Toast.makeText(getApplicationContext(), getString(R.string.link_nointernet),  Toast.LENGTH_SHORT).show();
                }
            }
        });

        bulletinDangerLvlBelow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(Helper.isNetworkAvailable(AvalancheBulletinActivity.this)){
                    Helper.goToURL(AvalancheBulletinActivity.this,getResources().getString(R.string.link_dangerlvl));
                } else{
                    Toast.makeText(getApplicationContext(), getString(R.string.link_nointernet),  Toast.LENGTH_SHORT).show();
                }
            }
        });

        int counterProblems=0;
        for(int i=0;i<avalancheBulletinTyrol.getAvalancheProblemsTyrols().size();i++){
            AvalancheProblemTyrol tempProblem =avalancheBulletinTyrol.getAvalancheProblemsTyrols().get(i);
            switch (tempProblem.getAvalancheProblem()){
                case "Triebschnee":
                    switch (counterProblems){
                        case 0:
                            bulletinProblem1.setImageResource(R.drawable.bulletin_drifting_snow);
                            bulletinDangerousHeightsImage1.setImageBitmap(tempProblem.getElevationImage());
                            bulletinExposition1.setImageBitmap(tempProblem.getExpositionImage());
                            bulletinDangerousHeightsText1.setText(tempProblem.getDescription());
                            counterProblems++;
                            break;
                        case 1:
                            bulletinProblem2.setImageResource(R.drawable.bulletin_drifting_snow);
                            bulletinDangerousHeightsImage2.setImageBitmap(tempProblem.getElevationImage());
                            bulletinExposition2.setImageBitmap(tempProblem.getExpositionImage());
                            bulletinDangerousHeightsText2.setText(tempProblem.getDescription());
                            counterProblems++;
                            break;
                        case 2:
                            bulletinProblem3.setImageResource(R.drawable.bulletin_drifting_snow);
                            //bulletinDangerousHeightsImage3.setImageBitmap(tempProblem.getElevationImage());
                            bulletinExposition3.setImageBitmap(tempProblem.getExpositionImage());
                            counterProblems++;
                        default:
                            break;
                    }
                    break;
                case "Gleitschnee":
                    switch (counterProblems){
                        case 0:
                            bulletinProblem1.setImageResource(R.drawable.bulletin_gliding_snow);
                            bulletinDangerousHeightsImage1.setImageBitmap(tempProblem.getElevationImage());
                            bulletinExposition1.setImageBitmap(tempProblem.getExpositionImage());
                            bulletinDangerousHeightsText1.setText(tempProblem.getDescription());
                            counterProblems++;
                            break;
                        case 1:
                            bulletinProblem2.setImageResource(R.drawable.bulletin_gliding_snow);
                            bulletinDangerousHeightsImage2.setImageBitmap(tempProblem.getElevationImage());
                            bulletinExposition2.setImageBitmap(tempProblem.getExpositionImage());
                            bulletinDangerousHeightsText2.setText(tempProblem.getDescription());
                            counterProblems++;
                            break;
                        case 2:
                            bulletinProblem3.setImageResource(R.drawable.bulletin_gliding_snow);
                            //bulletinDangerousHeightsImage3.setImageBitmap(tempProblem.getElevationImage());
                            bulletinExposition3.setImageBitmap(tempProblem.getExpositionImage());
                            counterProblems++;
                        default:
                            break;
                    }
                    break;
                case "Neuschnee":
                    switch (counterProblems){
                        case 0:
                            bulletinProblem1.setImageResource(R.drawable.bulletin_new_snow);
                            bulletinDangerousHeightsImage1.setImageBitmap(tempProblem.getElevationImage());
                            bulletinExposition1.setImageBitmap(tempProblem.getExpositionImage());
                            bulletinDangerousHeightsText1.setText(tempProblem.getDescription());
                            counterProblems++;
                            break;
                        case 1:
                            bulletinProblem2.setImageResource(R.drawable.bulletin_new_snow);
                            bulletinDangerousHeightsImage2.setImageBitmap(tempProblem.getElevationImage());
                            bulletinExposition2.setImageBitmap(tempProblem.getExpositionImage());
                            bulletinDangerousHeightsText2.setText(tempProblem.getDescription());
                            counterProblems++;
                            break;
                        case 2:
                            bulletinProblem3.setImageResource(R.drawable.bulletin_new_snow);
                            //bulletinDangerousHeightsImage3.setImageBitmap(tempProblem.getElevationImage());
                            bulletinExposition3.setImageBitmap(tempProblem.getExpositionImage());
                            counterProblems++;
                        default:
                            break;
                    }
                    break;
                case "Altschnee":
                    switch (counterProblems){
                        case 0:
                            bulletinProblem1.setImageResource(R.drawable.bulletin_old_snow);
                            bulletinDangerousHeightsImage1.setImageBitmap(tempProblem.getElevationImage());
                            bulletinExposition1.setImageBitmap(tempProblem.getExpositionImage());
                            bulletinDangerousHeightsText1.setText(tempProblem.getDescription());
                            counterProblems++;
                            break;
                        case 1:
                            bulletinProblem2.setImageResource(R.drawable.bulletin_old_snow);
                            bulletinDangerousHeightsImage2.setImageBitmap(tempProblem.getElevationImage());
                            bulletinExposition2.setImageBitmap(tempProblem.getExpositionImage());
                            bulletinDangerousHeightsText2.setText(tempProblem.getDescription());
                            counterProblems++;
                            break;
                        case 2:
                            bulletinProblem3.setImageResource(R.drawable.bulletin_old_snow);
                            //bulletinDangerousHeightsImage3.setImageBitmap(tempProblem.getElevationImage());
                            bulletinExposition3.setImageBitmap(tempProblem.getExpositionImage());
                            counterProblems++;
                        default:
                            break;
                    }
                    break;
                case "Nassschnee":
                    switch (counterProblems){
                        case 0:
                            bulletinProblem1.setImageResource(R.drawable.bulletin_wet_snow);
                            bulletinDangerousHeightsImage1.setImageBitmap(tempProblem.getElevationImage());
                            bulletinExposition1.setImageBitmap(tempProblem.getExpositionImage());
                            bulletinDangerousHeightsText1.setText(tempProblem.getDescription());
                            counterProblems++;
                            break;
                        case 1:
                            bulletinProblem2.setImageResource(R.drawable.bulletin_wet_snow);
                            //bulletinDangerousHeightsImage2.setImageBitmap(tempProblem.getElevationImage());
                            bulletinExposition2.setImageBitmap(tempProblem.getExpositionImage());
                            bulletinDangerousHeightsText2.setText(tempProblem.getDescription());
                            counterProblems++;
                            break;
                        case 2:
                            bulletinProblem3.setImageResource(R.drawable.bulletin_wet_snow);
                            //bulletinDangerousHeightsImage3.setImageBitmap(tempProblem.getElevationImage());
                            bulletinExposition3.setImageBitmap(tempProblem.getExpositionImage());
                            counterProblems++;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            //to ignore all problem above 2
            if(counterProblems==2){
                counterProblems=4;
            }
        }
        bulletinProblem1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(Helper.isNetworkAvailable(AvalancheBulletinActivity.this)){
                    Helper.goToURL(AvalancheBulletinActivity.this,getResources().getString(R.string.link_avalancheproblems));
                } else{
                    Toast.makeText(getApplicationContext(), getString(R.string.link_nointernet),  Toast.LENGTH_SHORT).show();
                }
            }
        });
        bulletinProblem2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(Helper.isNetworkAvailable(AvalancheBulletinActivity.this)){
                    Helper.goToURL(AvalancheBulletinActivity.this,getResources().getString(R.string.link_avalancheproblems));
                } else{
                    Toast.makeText(getApplicationContext(), getString(R.string.link_nointernet),  Toast.LENGTH_SHORT).show();
                }
            }
        });

        //set danger pattern
        int i;
        for(i=0; i<avalancheBulletinTyrol.getDangerPattern().size()-1;i++){
            bulletinDangerpattern.append((avalancheBulletinTyrol.getDangerPattern().get(i))+", ");

        }
        if(avalancheBulletinTyrol.getDangerPattern().size()!=0){
            bulletinDangerpattern.append(avalancheBulletinTyrol.getDangerPattern().get(i));
        }
        if(bulletinDangerpattern.getText().equals("")){
            bulletinDangerpattern.setText("-");
        }


        bulletinDangerpattern.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(Helper.isNetworkAvailable(AvalancheBulletinActivity.this)){
                    Helper.goToURL(AvalancheBulletinActivity.this,getResources().getString(R.string.link_dangerpattern));
                } else{
                    Toast.makeText(getApplicationContext(), getString(R.string.link_nointernet),  Toast.LENGTH_SHORT).show();
                }
            }
        });


        bulletinAccidents.setText("Unfälle: 0 Betroffene: 0");
        bulletinDangerAssessmentTitle.setText(avalancheBulletinTyrol.getAvalancheDangerAssessment());
        bulletinDangerAssessment.setText(avalancheBulletinTyrol.getAvalancheDangerAssessmentDescription());
        bulletinSnowpackstructureTitle.setText(avalancheBulletinTyrol.getSnowpackStructure());
        bulletinSnowpackstructure.setText(avalancheBulletinTyrol.getSnowpackStructureDescription());
        bulletinZamgweatherTitle.setText(avalancheBulletinTyrol.getZamgWeatherStation());
        bulletinZamgweather.setText(avalancheBulletinTyrol.getZamgWeatherStationReport());
        bulletinTendency.setText(avalancheBulletinTyrol.getTendecyAvalancheSituation());
        bulletinWriter.setText(avalancheBulletinTyrol.getCreator()+"\n\n\u00A9 Lawinenwarndienst Tirol");



    }

    /**
     * This method initialize all needed variables.
     */

    private void initializeVariables() {
        avalancheBulletinTyrol = new AvalancheBulletinTyrol();

        bulletinTitle = (TextView) findViewById(R.id.bulletin_title);
        bulletinDateTime = (TextView) findViewById(R.id.bulletin_datetime);

        //slope vars
        slopeDangerLvl= (ImageView) findViewById(R.id.slopedangerlvl);
        slopeamountdangerzones= (TextView) findViewById(R.id.slopeamountdangerzones);
        slopeAdditionalLoad= (TextView) findViewById(R.id.slopeadditionalload);
        slopeMinSH= (TextView) findViewById(R.id.slopeminsh);
        slopeMaxSH= (TextView) findViewById(R.id.slopemaxsh);
        slopeExposition= (TextView) findViewById(R.id.slopeexposition);
        slopeAngle= (TextView) findViewById(R.id.slopeangle);
        slopeAvalancheProblems= (TextView) findViewById(R.id.slopeavalancheproblems);


        //general avalanche bulletin
        bulletinGeneralTitle = (TextView) findViewById(R.id.bulletin_generaltitle);
        bulletinAccidents = (TextView) findViewById(R.id.bulletin_accidents_view);
        bulletinDangerLvlBelow = (ImageView) findViewById(R.id.bulletin_dangerlvl_below_view);
        bulletinDangerLvlBelowDescription = (TextView) findViewById(R.id.bulletin_dangerlvl_below_view_description);
        bulletinDangerLvlAbove = (ImageView) findViewById(R.id.bulletin_dangerlvl_above_view);
        bulletinDangerLvlAboveDescription = (TextView) findViewById(R.id.bulletin_dangerlvl_above_view_description);
        bulletinProblem1 = (ImageView) findViewById(R.id.bulletin_problems_view1);
        bulletinProblem2 = (ImageView) findViewById(R.id.bulletin_problems_view2);
        bulletinExposition1 = (ImageView) findViewById(R.id.bulletin_exposition_view1);
        bulletinExposition2 = (ImageView) findViewById(R.id.bulletin_exposition_view2);
        bulletinDangerousHeightsText1 = (TextView) findViewById(R.id.bulletin_dangerousheigts_viewtext1);
        bulletinDangerousHeightsImage1 = (ImageView) findViewById(R.id.bulletin_dangeroursheights_view1);
        bulletinDangerousHeightsText2 = (TextView) findViewById(R.id.bulletin_dangerousheigts_viewtext2);
        bulletinDangerousHeightsImage2 = (ImageView) findViewById(R.id.bulletin_dangeroursheights_view2);
        bulletinDangerpattern = (TextView) findViewById(R.id.bulletin_dangerpattern_view);

        bulletinDangerAssessmentTitle = (TextView) findViewById(R.id.bulletin_danger_assessment_title);
        bulletinDangerAssessment = (TextView) findViewById(R.id.bulletin_danger_assessment);
        bulletinSnowpackstructureTitle = (TextView) findViewById(R.id.bulletin_snowpackstructure_title);
        bulletinSnowpackstructure = (TextView) findViewById(R.id.bulletin_snowpackstructure);
        bulletinZamgweatherTitle = (TextView) findViewById(R.id.bulletin_zamgweather_title);
        bulletinZamgweather = (TextView) findViewById(R.id.bulletin_zamgweather);
        bulletinTendencyTitle = (TextView) findViewById(R.id.bulletin_tendency_title);
        bulletinTendency = (TextView) findViewById(R.id.bulletin_tendency);
        bulletinWriter = (TextView) findViewById(R.id.bulletin_writer);

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
        PopupMenu popup = new PopupMenu(AvalancheBulletinActivity.this, view);
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
