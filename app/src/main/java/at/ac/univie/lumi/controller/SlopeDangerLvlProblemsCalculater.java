package at.ac.univie.lumi.controller;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import at.ac.univie.lumi.model.AvalancheBulletinTyrol;
import at.ac.univie.lumi.model.AvalancheSlopeAreaBulletinTyrol;

/**
 * Created by phili on 6/13/2017.
 *
 * The class calculates the amount of applicable danger zones per slope and the applicable additional load.
 */

public class SlopeDangerLvlProblemsCalculater {
    private AvalancheBulletinTyrol avalancheBulletinTyrol;

    private HashMap<String, ArrayList<String>> listPossibleDangerZones;
    private ArrayList<String> listTodayDangerZones;
    //information taken from: http://www.slf.ch/schneeinfo/zusatzinfos/interpretationshilfe/info_gefahrenstellen/index_DE
    // https://lawine.tirol.gv.at/basics/matrix/

    /**
     * The constructor set the vars and initiate the model to search for the different danger zones and the additional load.
     * @param avalancheBulletinTyrol The current avalanche bulletin.
     */

    public SlopeDangerLvlProblemsCalculater(AvalancheBulletinTyrol avalancheBulletinTyrol){
        //create mapping lists
        this.avalancheBulletinTyrol = avalancheBulletinTyrol;
        listPossibleDangerZones= new HashMap<>();

        //general search logic
        listPossibleDangerZones.put("exposition_north",new ArrayList<String>(Arrays.asList("Nordhang","Norden")));
        listPossibleDangerZones.put("exposition_south",new ArrayList<String>(Arrays.asList("Südhang", "Süden")));
        listPossibleDangerZones.put("exposition_shadow",new ArrayList<String>(Arrays.asList("Schattenhängen","Schattenhang", "Schatten")));
        listPossibleDangerZones.put("exposition_shadow_1",new ArrayList<String>(Arrays.asList("mässig steile Schattenhängen","mässig steiler Schattenhang", "mässig steiler Schatten")));
        listPossibleDangerZones.put("exposition_shadow_2",new ArrayList<String>(Arrays.asList("steile Schattenhängen","steiler Schattenhang", "steiler Schatten")));
        listPossibleDangerZones.put("exposition_shadow_3",new ArrayList<String>(Arrays.asList("sehr steilen Schattenhängen","sehr steilen Schattenhang", "sehr steiler Schatten")));
        listPossibleDangerZones.put("exposition_shadow_4",new ArrayList<String>(Arrays.asList("extrem steilen Schattenhängen","extrem steilen Schattenhang", "extrem steiler Schatten")));
        listPossibleDangerZones.put("exposition_sun",new ArrayList<String>(Arrays.asList("Sonnenhänge", "Sonnenhang", "besonnten Hänge eine")));
        listPossibleDangerZones.put("exposition_slipstream",new ArrayList<String>(Arrays.asList("Windschattenhänge","Windschatten","Windschattenhang")));
        listPossibleDangerZones.put("drifting_snow",new ArrayList<String>(Arrays.asList("Triebschneehänge", "Triebschnee")));
        listPossibleDangerZones.put("drifting_snow_1",new ArrayList<String>(Arrays.asList("mässig steilen Triebschneehänge", "mässig steiler Triebschnee")));
        listPossibleDangerZones.put("drifting_snow_2",new ArrayList<String>(Arrays.asList("steilen Triebschneehänge", "steiler Triebschnee")));
        listPossibleDangerZones.put("drifting_snow_3",new ArrayList<String>(Arrays.asList("sehr steilen Triebschneehänge", "sehr steiler Triebschnee")));
        listPossibleDangerZones.put("drifting_snow_4",new ArrayList<String>(Arrays.asList("extrem steilen Triebschneehänge", "extrem steiler Triebschnee")));
        listPossibleDangerZones.put("ridge_near_steep_slopes",new ArrayList<String>(Arrays.asList("Kammnahe Steilhänge", "kammnahe Steilhänge","kammnahen Steilhängen", "kammnahe")));

        listPossibleDangerZones.put("below_altitude",new ArrayList<String>(Arrays.asList("Lagen unterhalb von etwa 1000 m","Lagen unterhalb von etwa")));
        listPossibleDangerZones.put("between_altitude",new ArrayList<String>(Arrays.asList("Lagen zwischen etwa 1000 m und 2000 m","Lagen zwischen etwa")));
        listPossibleDangerZones.put("above_altitude",new ArrayList<String>(Arrays.asList("Lagen oberhalb etwa 3000 m","Lagen oberhalb etwa")));
        listPossibleDangerZones.put("forest_boundary",new ArrayList<String>(Arrays.asList("Waldgrenze","Waldgrenzen", "Wald")));

        listPossibleDangerZones.put("gullies_bowls",new ArrayList<String>(Arrays.asList("Rinnen und Mulden","Kammlagen", "Rinnen","Mulden","Rinne","Mulde", "Kammlage")));
        listPossibleDangerZones.put("gullies_bowls_1",new ArrayList<String>(Arrays.asList("mässig steilen Rinnen und Mulden","mässig steile Kammlagen", "mässig steile Rinnen","mässig steile Mulden","mässig steile Rinne","mässig steile Mulde", "mässig steile Kammlage")));
        listPossibleDangerZones.put("gullies_bowls_2",new ArrayList<String>(Arrays.asList("steilen Rinnen und Mulden","steile Kammlagen", "steile Rinnen","steile Mulden","steile Rinne","steile Mulde", "steile Kammlage")));
        listPossibleDangerZones.put("gullies_bowls_3",new ArrayList<String>(Arrays.asList("sehr steilen Rinnen und Mulden","sehr steile Kammlagen", "sehr steile Rinnen","sehr steile Mulden","sehr steile Rinne","sehr steile Mulde", "sehr steile Kammlage")));
        listPossibleDangerZones.put("gullies_bowls_4",new ArrayList<String>(Arrays.asList("extrem steilen Rinnen und Mulden","extrem steile Kammlagen", "extrem steile Rinnen","extrem steile Mulden","extrem steile Rinne","extrem steile Mulde", "extrem steile Kammlage")));

        listPossibleDangerZones.put("additional_load",new ArrayList<String>(Arrays.asList("Zusatzbelastung","Zusatzbelastungen")));
        listPossibleDangerZones.put("additional_load_small",new ArrayList<String>(Arrays.asList("kleine Zusatzbelastung","geringe Zusatzbelastung","kleinen Zusatzbelastungen", "geringen Zusatzbelastungen","geringe Belastung")));
        listPossibleDangerZones.put("additional_load_medium",new ArrayList<String>(Arrays.asList("mittlere Zusatzbelastung","mittleren Zusatzbelastungen","mittlerer Belastung")));
        listPossibleDangerZones.put("additional_load_big",new ArrayList<String>(Arrays.asList("große Zusatzbelastung","großen Zusatzbelastungen","große Belastung")));

        listPossibleDangerZones.put("wind_north",new ArrayList<String>(Arrays.asList("Nordwind","starker Nordwind","mässiger Nordwind")));
        listPossibleDangerZones.put("wind_east",new ArrayList<String>(Arrays.asList("Ostwind","starker Ostwind","mässiger Ostwind")));
        listPossibleDangerZones.put("wind_west",new ArrayList<String>(Arrays.asList("Westwind","starker Westwind","mässiger Westwind")));
        listPossibleDangerZones.put("wind_south",new ArrayList<String>(Arrays.asList("Südwind","starker Südwind","mässiger Südwind")));


        //today found danger zones
        listTodayDangerZones =  new ArrayList<>();
        String avalancheDangerAssessmentDescription =avalancheBulletinTyrol.getAvalancheDangerAssessmentDescription().toLowerCase();
        String snowpackStructureDescription =avalancheBulletinTyrol.getSnowpackStructureDescription().toLowerCase();
        String zamgWeatherStationReport =avalancheBulletinTyrol.getZamgWeatherStationReport().toLowerCase();
        Iterator matchFinder = listPossibleDangerZones.entrySet().iterator();
        while (matchFinder.hasNext()) {
            Map.Entry pair = (Map.Entry)matchFinder.next();
            String key =(String)pair.getKey();
            ArrayList<String> tempStringlist =(ArrayList<String>)listPossibleDangerZones.get(key);
            for(String searcher: tempStringlist){//listPossibleDangerZones.get(key)){
                searcher=searcher.toLowerCase();
                if(avalancheDangerAssessmentDescription.contains(searcher) || snowpackStructureDescription.contains(searcher) ||  zamgWeatherStationReport.contains(searcher)){
                    listTodayDangerZones.add(key);
                }
            }
        }

        //get only unique values
        Set<String> uniqueValues = new HashSet<>();
        uniqueValues.addAll(listTodayDangerZones);
        listTodayDangerZones.clear();
        listTodayDangerZones.addAll(uniqueValues);
    }

    /**
     * This method calculate the amount of danger zones per slope.
     * @param avalancheSlopeAreaBulletinTyrol The current avalanche bulletin.
     * @return Returns the amount of applicable danger zones.
     */

    public int calculateSlopeAmountDangerZones(AvalancheSlopeAreaBulletinTyrol avalancheSlopeAreaBulletinTyrol){
        int amountDangerZonesSum=0;
        for(int i=0;i<listTodayDangerZones.size();i++){
            switch (listTodayDangerZones.get(i)){
                case "exposition_north":
                    if(avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==1 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==2 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==8){
                        amountDangerZonesSum++;
                    }
                    break;
                case "exposition_east":
                    if(avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==2 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==3 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==4){
                        amountDangerZonesSum++;
                    }
                    break;
                case "exposition_west":
                    if(avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==6 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==7 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==8){
                        amountDangerZonesSum++;
                    }
                    break;
                case "exposition_south":
                    if(avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==4 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==5 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==6){
                        amountDangerZonesSum++;
                    }
                    break;
                case "exposition_shadow":
                    if(avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==1 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==2 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==8){
                        amountDangerZonesSum++;
                    }
                    break;
                case "exposition_shadow_1":
                    if((avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==1 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==2 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==8)
                            && avalancheSlopeAreaBulletinTyrol.getSlopeAngleNumber()==1){
                        amountDangerZonesSum++;
                    }
                    break;
                case "exposition_shadow_2":
                    if((avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==1 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==2 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==8)
                            && avalancheSlopeAreaBulletinTyrol.getSlopeAngleNumber()==2){
                        amountDangerZonesSum++;
                    }
                    break;
                case "exposition_shadow_3":
                    if((avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==1 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==2 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==8)
                            && avalancheSlopeAreaBulletinTyrol.getSlopeAngleNumber()==3){
                        amountDangerZonesSum++;
                    }
                    break;
                case "exposition_shadow_4":
                    if((avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==1 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==2 || avalancheSlopeAreaBulletinTyrol.getExpositionNumber()==8)
                            && avalancheSlopeAreaBulletinTyrol.getSlopeAngleNumber()==4){
                        amountDangerZonesSum++;
                    }
                    break;
                case "exposition_sun":
                    if(avalancheSlopeAreaBulletinTyrol.getExpositionNumber()>=3 && avalancheSlopeAreaBulletinTyrol.getExpositionNumber()<=7){
                        amountDangerZonesSum++;
                    }
                    break;
                case "exposition_slipstream":
                    float tempWindDirection=avalancheBulletinTyrol.getWindDirection();
                    //get opponent exposition
                    if(tempWindDirection>=180){
                        tempWindDirection-=180;
                    } else{ //<180
                        tempWindDirection+=180;
                    }
                    int requiredWindDirection=degToCompass(tempWindDirection);
                    if((requiredWindDirection+1)>=avalancheSlopeAreaBulletinTyrol.getExpositionNumber() && (requiredWindDirection-1)<=avalancheSlopeAreaBulletinTyrol.getExpositionNumber()){
                        amountDangerZonesSum++;
                    }
                    break;
                //drifting_snow ignored
                case "drifting_snow_1":
                    if(avalancheSlopeAreaBulletinTyrol.getSlopeAngleNumber()==1){
                        amountDangerZonesSum++;
                    }
                    break;
                case "drifting_snow_2":
                    if(avalancheSlopeAreaBulletinTyrol.getSlopeAngleNumber()==2){
                        amountDangerZonesSum++;
                    }
                    break;
                case "drifting_snow_3":
                    if(avalancheSlopeAreaBulletinTyrol.getSlopeAngleNumber()==3){
                        amountDangerZonesSum++;
                    }
                    break;
                case "drifting_snow_4":
                    if(avalancheSlopeAreaBulletinTyrol.getSlopeAngleNumber()==4){
                        amountDangerZonesSum++;
                    }
                    break;
                case "forest_boundary": // forest_boundary about 2000
                    if(avalancheSlopeAreaBulletinTyrol.getMinSH()>=2000 ||
                            avalancheSlopeAreaBulletinTyrol.getMaxSH()>2000){
                        amountDangerZonesSum++;
                    }
                    break;
                //ridge_near_steep_slopes ignored
                // below_altitude ignored
                // between_altitude ignored
                // above_altitude ignored
                // gullies_bowls ignored
                case "gullies_bowls_1":
                    if(avalancheSlopeAreaBulletinTyrol.getSlopeAngleNumber()==1){
                        amountDangerZonesSum++;
                    }
                    break;
                case "gullies_bowls_2":
                    if(avalancheSlopeAreaBulletinTyrol.getSlopeAngleNumber()==2){
                        amountDangerZonesSum++;
                    }
                    break;
                case "gullies_bowls_3":
                    if(avalancheSlopeAreaBulletinTyrol.getSlopeAngleNumber()==3){
                        amountDangerZonesSum++;
                    }
                    break;
                case "gullies_bowls_4":
                    if(avalancheSlopeAreaBulletinTyrol.getSlopeAngleNumber()==4){
                        amountDangerZonesSum++;
                    }
                    break;
                //ignored additional_load
                //ignored additional_load_small
                //ignored additional_load_medium
                //ignored additional_load_big

                //ignored wind_north
                //ignored wind_east
                //ignored wind_west
                //ignored wind_south
            }
        }

        //add amout of relevant avalanche problems
        amountDangerZonesSum+=avalancheSlopeAreaBulletinTyrol.getListAvalancheProblems().size();

        return amountDangerZonesSum;
    }

    /**
     * This method transform the wind direction in degrees to an int values between 1 to 8.
     * @param windDirection The wind direction in degrees.
     * @return The applicable exposition from 1 to 8.
     */

    private int degToCompass(float windDirection){
        int directions[] = {1, 2, 3, 4, 5, 6, 7, 8, 1};
        return directions[ (int)Math.round((  ((double)windDirection % 360) / 45)) ];
    }

    /**
     * This method calculates the additional load per slope.
     * @return Returns the additional load.
     */

    public int calculateSlopeAdditionalLoad(){
        int additionalLoad=1;
        for(String comparer: listTodayDangerZones){
            switch (comparer){
                case "additional_load_small":
                    additionalLoad=3;
                    break;
                case "additional_load_medium":
                    additionalLoad=2;
                    break;
                case "additional_load_big":
                    additionalLoad=1;
                    break;
                default:
                    break;
            }
        }
        return additionalLoad;
    }

}
