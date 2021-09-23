package at.ac.univie.lumi.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import at.ac.univie.lumi.R;
import at.ac.univie.lumi.model.WeatherStation;
import at.ac.univie.lumi.model.WeatherStationData;
import at.ac.univie.lumi.view.SplashLoadingActivity;
import at.ac.univie.lumi.view.WeatherForecastActivity;
import at.ac.univie.lumi.view.WeatherStationActivity;

/**
 * Created by phili on 5/26/2017.
 *
 * This class extracts the weather station and forecast data of the asset folder for the applicable acitivties.
 */

public class WeatherDataExtractor extends AsyncTask<String, Void, Void> {
    private WeatherStationActivity weatherStationActivity;
    private String weatherStationName;
    private ProgressDialog pDialog;

    private Context context;
    //private WeatherStation weatherStation1; //Snowpillow
    //private WeatherStation weatherStation2; //Tarntalerböden
    private WeatherStation weatherStation;
    private WeatherStationData tempWeatherStationData;

    private Calendar currentTime;
    private boolean currenWeatherStationDataFound =false;
    // one day has 143 10 min slots
    private int counterWeatherStationData=143;

    private WeatherForecastActivity weatherForecastActivity;
    private Calendar last7dayTime;
    private boolean last7dayWeatherStationDataFound =false;
    //143 weatherstationdata per day x 9 days = 1287
    private int counterWeatherForecastStationData=1287;
    private ArrayList<WeatherStationData> tempWeatherStationDataList;

    private Activity activity;
    private boolean currentDayWeatherStationDataFound=false;
    private int counterWindDirection = 143;

    /**
     * This constructor set the vars for the weather station activity.
     * @param weatherStationActivity The current weather station activity
     */

    public WeatherDataExtractor(WeatherStationActivity weatherStationActivity){
        this.weatherStationActivity = weatherStationActivity;
    }

    /**
     * This constructor sets the vars for the weather forecast activity.
     * @param weatherForecastActivity The current weather forecast activity.
     */

    public WeatherDataExtractor(WeatherForecastActivity weatherForecastActivity){
        this.weatherForecastActivity = weatherForecastActivity;
    }

    /**
     * This constructor sets the vars.
     * @param activity The current activity.
     */

    public WeatherDataExtractor(Activity activity){
        this.activity = activity;
    }

    /**
     * This method is executed before the background thread is started to show a waid dialog.
     */

    @Override
    protected void onPreExecute() {
            if(weatherStationActivity!=null){
                weatherStationActivity.showWaitDialog();
            } else if(weatherForecastActivity!=null){
                weatherForecastActivity.showWaitDialog();
            }
    }

    /**
     * This method loads the weather data .dat file, extracts it and set the model object.
     * @param params The name of the searched weather station.
     * @return empty
     */

    @Override
    protected Void doInBackground(String... params) {
        //in REAL CONDITIONS only the date from now

        currentTime = Calendar.getInstance();

        weatherStationName= params[0];
        if(weatherStationActivity!=null) {

            //load scenario date
            File fileScenario= new File(weatherStationActivity.getFilesDir(),ActivityConstants.ScenarioFileName);
            if(fileScenario.exists()){
                currentTime = DAOSerialisationManager.readSerializedFile(fileScenario.getAbsolutePath());
            }else {
                currentTime.set(2017, 4, 1);
            }

            switch (weatherStationName) {
                case "Wetterstation Snowpillow":
                    weatherStation = new WeatherStation("Wetterstation Snowpillow");
                    setWeatherStation1();
                    break;
                case "Wetterstation Tarntalerboden":
                    weatherStation = new WeatherStation("Wetterstation Tarntalerboden");
                    setWeatherStation2();
                    break;
                default:
                    Log.d("NOSTRING", "Keine Wetterstationsübereinstimmung");
                    break;
            }
        }else if(weatherForecastActivity!=null){

            //load scenario date
            File fileScenario= new File(weatherForecastActivity.getFilesDir(),ActivityConstants.ScenarioFileName);
            if(fileScenario.exists()){
                currentTime = DAOSerialisationManager.readSerializedFile(fileScenario.getAbsolutePath());
            }else {
                currentTime.set(2017, 4, 1);
            }

            //get the day before 7 days for the loop
            last7dayTime = currentTime;
            //check to not reach dates before the begin of .dat files
            if(last7dayTime.get(Calendar.DAY_OF_MONTH)<8 && last7dayTime.get(Calendar.MONTH)<11 && last7dayTime.get(Calendar.YEAR)<2017){
                currentTime.set(2016, 9, 8);
            //check to not reach dates after the end of .dat files
            } else if(last7dayTime.get(Calendar.DAY_OF_MONTH)>21 && last7dayTime.get(Calendar.MONTH)>3 && last7dayTime.get(Calendar.YEAR)>2016){
                currentTime.set(2017, 4, 21);
            }
            last7dayTime = currentTime;
            last7dayTime.add(Calendar.DATE, -7);
            tempWeatherStationDataList = new ArrayList<>();

            switch (weatherStationName) {
                case "Wetterstation Snowpillow":
                    weatherStation = new WeatherStation("Wetterstation Snowpillow");
                    setWeatherStationForecast1();
                    break;
                case "Wetterstation Tarntalerboden":
                    weatherStation = new WeatherStation("Wetterstation Tarntalerboden");
                    setWeatherStationForecast2();
                    break;
                default:
                    Log.d("NOSTRING", "Keine Wetterstationsprognoseübereinstimmung");
                    break;
            }

        }
        return null;
    }

    /**
     * This method is executed when the background thread has finished and set in each activity.
     * @param aVoid empty
     */

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (pDialog.isShowing() && pDialog!=null)
            pDialog.dismiss();

        if(weatherStationActivity!=null){
            weatherStationActivity.getWeatherStationTitle().setText(weatherStation.getName());
            ArrayList<WeatherStationData> weatherStationData= weatherStation.getWeatherStationData();
            //set date and time
            Calendar foundCalendar = weatherStationData.get(0).getDateTime();
            if(foundCalendar.get(Calendar.HOUR_OF_DAY)<10 && foundCalendar.get(Calendar.MINUTE)<10){
                weatherStationActivity.getWeatherStationDateTime().setText(String.valueOf(foundCalendar.get(Calendar.DAY_OF_MONTH))+"."+String.valueOf(foundCalendar.get(Calendar.MONTH)+1)+"."+String.valueOf(foundCalendar.get(Calendar.YEAR)+" 0"+String.valueOf(foundCalendar.get(Calendar.HOUR_OF_DAY)+":0"+String.valueOf(foundCalendar.get(Calendar.MINUTE)))));
            } else if(foundCalendar.get(Calendar.HOUR_OF_DAY)<10){
                weatherStationActivity.getWeatherStationDateTime().setText(String.valueOf(foundCalendar.get(Calendar.DAY_OF_MONTH))+"."+String.valueOf(foundCalendar.get(Calendar.MONTH)+1)+"."+String.valueOf(foundCalendar.get(Calendar.YEAR)+" 0"+String.valueOf(foundCalendar.get(Calendar.HOUR_OF_DAY)+":"+String.valueOf(foundCalendar.get(Calendar.MINUTE)))));
            } else if(foundCalendar.get(Calendar.MINUTE)<10){
                weatherStationActivity.getWeatherStationDateTime().setText(String.valueOf(foundCalendar.get(Calendar.DAY_OF_MONTH))+"."+String.valueOf(foundCalendar.get(Calendar.MONTH)+1)+"."+String.valueOf(foundCalendar.get(Calendar.YEAR)+" "+String.valueOf(foundCalendar.get(Calendar.HOUR_OF_DAY)+":0"+String.valueOf(foundCalendar.get(Calendar.MINUTE)))));
            } else {
                weatherStationActivity.getWeatherStationDateTime().setText(String.valueOf(foundCalendar.get(Calendar.DAY_OF_MONTH))+"."+String.valueOf(foundCalendar.get(Calendar.MONTH)+1)+"."+String.valueOf(foundCalendar.get(Calendar.YEAR)+" "+String.valueOf(foundCalendar.get(Calendar.HOUR_OF_DAY)+":"+String.valueOf(foundCalendar.get(Calendar.MINUTE)))));
            }

            //set height
            if(weatherStation.getName().equalsIgnoreCase(weatherStationActivity.getString(R.string.weatherstation_snowpillow))){
                weatherStationActivity.getWeatherStationHeight().setText(R.string.weatherstation_snowpillow_height);
            } else if(weatherStation.getName().equalsIgnoreCase(weatherStationActivity.getString(R.string.weatherstation_tarntalerboden))){
                weatherStationActivity.getWeatherStationHeight().setText(R.string.weatherstation_tarntalerboden_height);
            }
            weatherStationActivity.getWeatherStationWindDirection().setText(String.valueOf(weatherStationData.get(0).getWinddirection())+"°");
            weatherStationActivity.getWeatherStationWindSpeed().setText(String.valueOf(weatherStationData.get(0).getWindspeed())+" m/s");
            weatherStationActivity.getWeatherStationWindGustDirection().setText(String.valueOf(weatherStationData.get(0).getWinddirectionGust())+"°");
            weatherStationActivity.getWeatherStationWindGust().setText(String.valueOf(weatherStationData.get(0).getWindgust())+" m/s");
            weatherStationActivity.getWeatherStationAirTemperature().setText(String.valueOf(weatherStationData.get(0).getAirtemperature())+"°C");
            weatherStationActivity.getWeatherStationRelHumidity().setText(String.valueOf(weatherStationData.get(0).getRelHumidity())+"%");
            weatherStationActivity.getWeatherStationSnowHeight().setText(String.valueOf(weatherStationData.get(0).getSnowHeight())+" cm");
        } else if (weatherForecastActivity!=null){
            weatherForecastActivity.getWeatherForecastTitle().setText("Wetter der "+weatherStation.getName());
            ArrayList<WeatherStationData> weatherStationData= weatherStation.getWeatherStationData();

            weatherForecastActivity.getLast7day_title().setText(String.valueOf(weatherStationData.get(0).getDateTime().get(Calendar.DAY_OF_MONTH))+"."+String.valueOf(weatherStationData.get(0).getDateTime().get(Calendar.MONTH)+1)+".\n"+String.valueOf(weatherStationData.get(0).getDateTime().get(Calendar.YEAR)));
            weatherForecastActivity.getTempmin_last7day().setText(String.valueOf(weatherStationData.get(0).getAirtemperature())+"°C");
            weatherForecastActivity.getTempmax_last7day().setText(String.valueOf(weatherStationData.get(0).getSurfacetemperature())+"°C");
            weatherForecastActivity.getWinddirection_last7day().setText(String.format("%.2f", weatherStationData.get(0).getWinddirection())+"°");
            weatherForecastActivity.getWindspeed_last7day().setText(String.format("%.2f", weatherStationData.get(0).getWindspeed())+" m/s");
            weatherForecastActivity.getSnow_last7day().setText(String.valueOf(weatherStationData.get(0).getSnowHeight())+" cm");
            weatherForecastActivity.getRelhumidity_last7day().setText(String.format("%.2f", weatherStationData.get(0).getRelHumidity())+"%");

            weatherForecastActivity.getLast6day_title().setText(String.valueOf(weatherStationData.get(1).getDateTime().get(Calendar.DAY_OF_MONTH))+"."+String.valueOf(weatherStationData.get(1).getDateTime().get(Calendar.MONTH)+1)+".\n"+String.valueOf(weatherStationData.get(1).getDateTime().get(Calendar.YEAR)));
            weatherForecastActivity.getTempmin_last6day().setText(String.valueOf(weatherStationData.get(1).getAirtemperature())+"°C");
            weatherForecastActivity.getTempmax_last6day().setText(String.valueOf(weatherStationData.get(1).getSurfacetemperature())+"°C");
            weatherForecastActivity.getWinddirection_last6day().setText(String.format("%.2f", weatherStationData.get(1).getWinddirection())+"°");
            weatherForecastActivity.getWindspeed_last6day().setText(String.format("%.2f", weatherStationData.get(1).getWindspeed())+" m/s");
            weatherForecastActivity.getSnow_last6day().setText(String.valueOf(weatherStationData.get(1).getSnowHeight())+" cm");
            weatherForecastActivity.getRelhumidity_last6day().setText(String.format("%.2f", weatherStationData.get(1).getRelHumidity())+"%");

            weatherForecastActivity.getLast5day_title().setText(String.valueOf(weatherStationData.get(2).getDateTime().get(Calendar.DAY_OF_MONTH))+"."+String.valueOf(weatherStationData.get(2).getDateTime().get(Calendar.MONTH)+1)+".\n"+String.valueOf(weatherStationData.get(2).getDateTime().get(Calendar.YEAR)));
            weatherForecastActivity.getTempmin_last5day().setText(String.valueOf(weatherStationData.get(2).getAirtemperature())+"°C");
            weatherForecastActivity.getTempmax_last5day().setText(String.valueOf(weatherStationData.get(2).getSurfacetemperature())+"°C");
            weatherForecastActivity.getWinddirection_last5day().setText(String.format("%.2f", weatherStationData.get(2).getWinddirection())+"°");
            weatherForecastActivity.getWindspeed_last5day().setText(String.format("%.2f", weatherStationData.get(2).getWindspeed())+" m/s");
            weatherForecastActivity.getSnow_last5day().setText(String.valueOf(weatherStationData.get(2).getSnowHeight())+" cm");
            weatherForecastActivity.getRelhumidity_last5day().setText(String.format("%.2f", weatherStationData.get(2).getRelHumidity())+"%");

            weatherForecastActivity.getLast4day_title().setText(String.valueOf(weatherStationData.get(3).getDateTime().get(Calendar.DAY_OF_MONTH))+"."+String.valueOf(weatherStationData.get(3).getDateTime().get(Calendar.MONTH)+1)+".\n"+String.valueOf(weatherStationData.get(3).getDateTime().get(Calendar.YEAR)));
            weatherForecastActivity.getTempmin_last4day().setText(String.valueOf(weatherStationData.get(3).getAirtemperature())+"°C");
            weatherForecastActivity.getTempmax_last4day().setText(String.valueOf(weatherStationData.get(3).getSurfacetemperature())+"°C");
            weatherForecastActivity.getWinddirection_last4day().setText(String.format("%.2f", weatherStationData.get(3).getWinddirection())+"°");
            weatherForecastActivity.getWindspeed_last4day().setText(String.format("%.2f", weatherStationData.get(3).getWindspeed())+" m/s");
            weatherForecastActivity.getSnow_last4day().setText(String.valueOf(weatherStationData.get(3).getSnowHeight())+" cm");
            weatherForecastActivity.getRelhumidity_last4day().setText(String.format("%.2f", weatherStationData.get(3).getRelHumidity())+"%");

            weatherForecastActivity.getLast3day_title().setText(String.valueOf(weatherStationData.get(4).getDateTime().get(Calendar.DAY_OF_MONTH))+"."+String.valueOf(weatherStationData.get(4).getDateTime().get(Calendar.MONTH)+1)+".\n"+String.valueOf(weatherStationData.get(4).getDateTime().get(Calendar.YEAR)));
            weatherForecastActivity.getTempmin_last3day().setText(String.valueOf(weatherStationData.get(4).getAirtemperature())+"°C");
            weatherForecastActivity.getTempmax_last3day().setText(String.valueOf(weatherStationData.get(4).getSurfacetemperature())+"°C");
            weatherForecastActivity.getWinddirection_last3day().setText(String.format("%.2f", weatherStationData.get(4).getWinddirection())+"°");
            weatherForecastActivity.getWindspeed_last3day().setText(String.format("%.2f", weatherStationData.get(4).getWindspeed())+" m/s");
            weatherForecastActivity.getSnow_last3day().setText(String.valueOf(weatherStationData.get(4).getSnowHeight())+" cm");
            weatherForecastActivity.getRelhumidity_last3day().setText(String.format("%.2f", weatherStationData.get(4).getRelHumidity())+"%");

            weatherForecastActivity.getLast2day_title().setText(String.valueOf(weatherStationData.get(5).getDateTime().get(Calendar.DAY_OF_MONTH))+"."+String.valueOf(weatherStationData.get(5).getDateTime().get(Calendar.MONTH)+1)+".\n"+String.valueOf(weatherStationData.get(5).getDateTime().get(Calendar.YEAR)));
            weatherForecastActivity.getTempmin_last2day().setText(String.valueOf(weatherStationData.get(5).getAirtemperature())+"°C");
            weatherForecastActivity.getTempmax_last2day().setText(String.valueOf(weatherStationData.get(5).getSurfacetemperature())+"°C");
            weatherForecastActivity.getWinddirection_last2day().setText(String.format("%.2f", weatherStationData.get(5).getWinddirection())+"°");
            weatherForecastActivity.getWindspeed_last2day().setText(String.format("%.2f", weatherStationData.get(5).getWindspeed())+" m/s");
            weatherForecastActivity.getSnow_last2day().setText(String.valueOf(weatherStationData.get(5).getSnowHeight())+" cm");
            weatherForecastActivity.getRelhumidity_last2day().setText(String.format("%.2f", weatherStationData.get(5).getRelHumidity())+"%");

            weatherForecastActivity.getLast1day_title().setText(String.valueOf(weatherStationData.get(6).getDateTime().get(Calendar.DAY_OF_MONTH))+"."+String.valueOf(weatherStationData.get(6).getDateTime().get(Calendar.MONTH)+1)+".\n"+String.valueOf(weatherStationData.get(6).getDateTime().get(Calendar.YEAR)));
            weatherForecastActivity.getTempmin_last1day().setText(String.valueOf(weatherStationData.get(6).getAirtemperature())+"°C");
            weatherForecastActivity.getTempmax_last1day().setText(String.valueOf(weatherStationData.get(6).getSurfacetemperature())+"°C");
            weatherForecastActivity.getWinddirection_last1day().setText(String.format("%.2f", weatherStationData.get(6).getWinddirection())+"°");
            weatherForecastActivity.getWindspeed_last1day().setText(String.format("%.2f", weatherStationData.get(6).getWindspeed())+" m/s");
            weatherForecastActivity.getSnow_last1day().setText(String.valueOf(weatherStationData.get(6).getSnowHeight())+" cm");
            weatherForecastActivity.getRelhumidity_last1day().setText(String.format("%.2f", weatherStationData.get(6).getRelHumidity())+"%");

            weatherForecastActivity.getToday_title().setText(String.valueOf(weatherStationData.get(7).getDateTime().get(Calendar.DAY_OF_MONTH))+"."+String.valueOf(weatherStationData.get(7).getDateTime().get(Calendar.MONTH)+1)+".\n"+String.valueOf(weatherStationData.get(7).getDateTime().get(Calendar.YEAR)));
            weatherForecastActivity.getTempmin_today().setText(String.valueOf(weatherStationData.get(7).getAirtemperature())+"°C");
            weatherForecastActivity.getTempmax_today().setText(String.valueOf(weatherStationData.get(7).getSurfacetemperature())+"°C");
            weatherForecastActivity.getWinddirection_today().setText(String.format("%.2f", weatherStationData.get(7).getWinddirection())+"°");
            weatherForecastActivity.getWindspeed_today().setText(String.format("%.2f", weatherStationData.get(7).getWindspeed())+" m/s");
            weatherForecastActivity.getSnow_today().setText(String.valueOf(weatherStationData.get(7).getSnowHeight())+" cm");
            weatherForecastActivity.getRelhumidity_today().setText(String.format("%.2f", weatherStationData.get(7).getRelHumidity())+"%");

            weatherForecastActivity.getFuture1day_title().setText(String.valueOf(weatherStationData.get(8).getDateTime().get(Calendar.DAY_OF_MONTH))+"."+String.valueOf(weatherStationData.get(8).getDateTime().get(Calendar.MONTH)+1)+".\n"+String.valueOf(weatherStationData.get(8).getDateTime().get(Calendar.YEAR)));
            weatherForecastActivity.getTempmin_future1day().setText(String.valueOf(weatherStationData.get(8).getAirtemperature())+"°C");
            weatherForecastActivity.getTempmax_future1day().setText(String.valueOf(weatherStationData.get(8).getSurfacetemperature())+"°C");
            weatherForecastActivity.getWinddirection_future1day().setText(String.format("%.2f", weatherStationData.get(8).getWinddirection())+"°");
            weatherForecastActivity.getWindspeed_future1day().setText(String.format("%.2f", weatherStationData.get(8).getWindspeed())+" m/s");
            weatherForecastActivity.getSnow_future1day().setText(String.valueOf(weatherStationData.get(8).getSnowHeight())+" cm");
            weatherForecastActivity.getRelhumidity_future1day().setText(String.format("%.2f", weatherStationData.get(8).getRelHumidity())+"%");
        }
    }

    /**
     * The method set the waid dialog.
     * @param pd The progress dialog to set.
     */

    public void setWaitDialog(ProgressDialog pd){
        pDialog=pd;
    }

    /**
     * This method is used to open and extract the weather data file of the weather station snowpillow for the weather station activity.
     */

    private void setWeatherStation1(){
        try{

            InputStream inputStream  = weatherStationActivity.getAssets().open("meteo_snowpillow_201617.dat");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            String inputLineAfterDate;
            String[] inputLineAfterDateArray;
            while ((inputLine = reader.readLine()) != null) {


                Calendar calendar = parseDateTimeWeatherStation1CheckCurrentTime(inputLine);
                if(currenWeatherStationDataFound && counterWeatherStationData>=0){
                    tempWeatherStationData = new WeatherStationData();
                    tempWeatherStationData.setDateTime(calendar);

                    inputLineAfterDate=inputLine.substring(21);
                    inputLineAfterDateArray = inputLineAfterDate.split(" ");

                    //check wrong values of .dat file and replace them
                    for(int i =0; i<inputLineAfterDateArray.length; i++){
                        if(inputLineAfterDateArray[i].equalsIgnoreCase("-9999") || inputLineAfterDateArray[i].equalsIgnoreCase("-9999.90")){
                            inputLineAfterDateArray[i]="0.0";
                        }
                    }

                    //extract inputLineAfterDateArray and set WeatherStationData model
                    tempWeatherStationData.setAirtemperature(Float.parseFloat(inputLineAfterDateArray[0]));
                    tempWeatherStationData.setRelHumidity(Float.parseFloat(inputLineAfterDateArray[1]));
                    tempWeatherStationData.setWindspeed(Float.parseFloat(inputLineAfterDateArray[2]));
                    tempWeatherStationData.setWinddirection(Float.parseFloat(inputLineAfterDateArray[3]));
                    tempWeatherStationData.setWindgust(Float.parseFloat(inputLineAfterDateArray[4]));
                    tempWeatherStationData.setWinddirectionGust(Float.parseFloat(inputLineAfterDateArray[5]));
                    tempWeatherStationData.setSnowHeight(Float.parseFloat(inputLineAfterDateArray[6]));
                    tempWeatherStationData.setGlobalstr(Float.parseFloat(inputLineAfterDateArray[7]));
                    tempWeatherStationData.setReflkwstr(Float.parseFloat(inputLineAfterDateArray[8]));
                    tempWeatherStationData.setAtmstragainst(Float.parseFloat(inputLineAfterDateArray[9]));
                    tempWeatherStationData.setAusstr(Float.parseFloat(inputLineAfterDateArray[10]));
                    tempWeatherStationData.setTempjuddsnowheightsensor(Float.parseFloat(inputLineAfterDateArray[11]));
                    tempWeatherStationData.setBatteryVoltage(Float.parseFloat(inputLineAfterDateArray[12]));
                    tempWeatherStationData.setVenti(Integer.parseInt(inputLineAfterDateArray[13]));
                    tempWeatherStationData.setVaporpressure(Float.parseFloat(inputLineAfterDateArray[14]));
                    tempWeatherStationData.setSaturationvaporpressure(Float.parseFloat(inputLineAfterDateArray[15]));
                    tempWeatherStationData.setDewpoint(Float.parseFloat(inputLineAfterDateArray[16]));
                    tempWeatherStationData.setAirpressure(Float.parseFloat(inputLineAfterDateArray[17]));
                    tempWeatherStationData.setSurfacetemperature(Float.parseFloat(inputLineAfterDateArray[18]));
                    //inputLineAfterDateArray index from 0 to 19, value index 18,19 are the same, size 20
                    weatherStation.getWeatherStationData().add(tempWeatherStationData);
                    counterWeatherStationData--;
                }
            }

            reader.close();
            inputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * This method is used to open and extract the weather data file of the weather station tarntaler for the weather station activity.
     */

    private void setWeatherStation2() {
        try{
            //AssetManager assetManager = context.getAssets();
            //InputStream inputStream  = assetManager.open("meteo_tarntaler_201617.dat");
            InputStream inputStream  = weatherStationActivity.getAssets().open("meteo_tarntaler_201617.dat");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            String inputLineAfterDate;
            String[] inputLineAfterDateArray;
            while ((inputLine = reader.readLine()) != null) {
                Calendar calendar = parseDateTimeWeatherStation2CheckCurrentTime(inputLine);
                if(currenWeatherStationDataFound && counterWeatherStationData>=0){
                    tempWeatherStationData = new WeatherStationData();

                    tempWeatherStationData.setDateTime(calendar);

                    inputLineAfterDate=inputLine.substring(12);
                    //reduce spaces with more than one to one
                    inputLineAfterDate=inputLineAfterDate.trim().replaceAll(" +", " ");

                    //split string line to pure number values
                    inputLineAfterDateArray = inputLineAfterDate.split(" ");

                    //extract inputLineAfterDateArray and set WeatherStationData model
                    tempWeatherStationData.setAirtemperature(Float.parseFloat(inputLineAfterDateArray[0]));
                    tempWeatherStationData.setRelHumidity(Float.parseFloat(inputLineAfterDateArray[1]));
                    tempWeatherStationData.setWindspeed(Float.parseFloat(inputLineAfterDateArray[2]));
                    tempWeatherStationData.setWinddirection(Float.parseFloat(inputLineAfterDateArray[3]));
                    tempWeatherStationData.setWindgust(Float.parseFloat(inputLineAfterDateArray[4]));
                    tempWeatherStationData.setWinddirectionGust(Float.parseFloat(inputLineAfterDateArray[5]));
                    tempWeatherStationData.setSnowHeight(Float.parseFloat(inputLineAfterDateArray[6]));
                    //inputLineAfterDateArray index from 0 to 6, size 7

                    weatherStation.getWeatherStationData().add(tempWeatherStationData);
                    counterWeatherStationData--;
                }

            }
            reader.close();
            inputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * This method parses the date and time of the weather station data of snowpillow
     * @param dateTime The date and time as string.
     * @return Returns the date and time as calendar object.
     */

    private Calendar parseDateTimeWeatherStation1CheckCurrentTime(String dateTime){
        Calendar calender = Calendar.getInstance();
        calender.set(Integer.parseInt(dateTime.substring(6,10)),Integer.parseInt(dateTime.substring(3,5))-1, Integer.parseInt(dateTime.substring(0,2)), Integer.parseInt(dateTime.substring(11,13)),Integer.parseInt(dateTime.substring(14,16)),0);

        if(calender.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR) &&
                calender.get(Calendar.MONTH) == currentTime.get(Calendar.MONTH) &&
                calender.get(Calendar.DAY_OF_MONTH) == currentTime.get(Calendar.DAY_OF_MONTH) &&
                calender.get(Calendar.HOUR_OF_DAY) == currentTime.get(Calendar.HOUR_OF_DAY) &&
                //important check if current minute is inside the interval of minus 5 and plus 4 example: 13:20 --> 13:15 and 13:24
                (
                        (calender.get(Calendar.MINUTE)-5) <= currentTime.get(Calendar.MINUTE) &&
                                (calender.get(Calendar.MINUTE)+4) >= currentTime.get(Calendar.MINUTE)
                )) {
                currenWeatherStationDataFound=true;
        }
        return calender;
    }

    /**
     * This method parses the date and time of the weather station data of tarntaler
     * @param dateTime The date and time as string.
     * @return Returns the date and time as calendar object.
     */

    private Calendar parseDateTimeWeatherStation2CheckCurrentTime(String dateTime){
        Calendar calender = Calendar.getInstance();
        calender.set(Integer.parseInt(dateTime.substring(0,4)),Integer.parseInt(dateTime.substring(4,6))-1, Integer.parseInt(dateTime.substring(6,8)), Integer.parseInt(dateTime.substring(8,10)),Integer.parseInt(dateTime.substring(10,12)),0);
        if(calender.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR) &&
                calender.get(Calendar.MONTH) == currentTime.get(Calendar.MONTH) &&
                calender.get(Calendar.DAY_OF_MONTH) == currentTime.get(Calendar.DAY_OF_MONTH) &&
                calender.get(Calendar.HOUR_OF_DAY) == currentTime.get(Calendar.HOUR_OF_DAY) &&
                //important check if current minute is inside the interval of minus 5 and plus 4 example: 13:20 --> 13:15 and 13:24
                (
                        (calender.get(Calendar.MINUTE)-5) <= currentTime.get(Calendar.MINUTE) &&
                                (calender.get(Calendar.MINUTE)+4) >= currentTime.get(Calendar.MINUTE)
                )) {
            currenWeatherStationDataFound=true;
        }
        return calender;
    }

    /**
     * This method is used to open and extract the weather data file of the weather station snowpillow for the weather forecast activity.
     */

    private void setWeatherStationForecast1() {
        try{
            InputStream inputStream  = weatherForecastActivity.getAssets().open("meteo_snowpillow_201617.dat");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            String inputLineAfterDate;
            String[] inputLineAfterDateArray;
            while ((inputLine = reader.readLine()) != null) {
                Calendar calendar = parseDateTimeWeatherForecast1CheckCurrentTime(inputLine);
                if(last7dayWeatherStationDataFound && counterWeatherForecastStationData>=0){
                    tempWeatherStationData = new WeatherStationData();
                    tempWeatherStationData.setDateTime(calendar);

                    inputLineAfterDate=inputLine.substring(21);
                    inputLineAfterDateArray = inputLineAfterDate.split(" ");

                    //check wrong values of .dat file and replace them
                    for(int i =0; i<inputLineAfterDateArray.length; i++){
                        if(inputLineAfterDateArray[i].equalsIgnoreCase("-9999") || inputLineAfterDateArray[i].equalsIgnoreCase("-9999.90")){
                            inputLineAfterDateArray[i]="0.0";
                        }
                    }
                    //extract inputLineAfterDateArray and set WeatherStationData model
                    tempWeatherStationData.setAirtemperature(Float.parseFloat(inputLineAfterDateArray[0]));
                    tempWeatherStationData.setRelHumidity(Float.parseFloat(inputLineAfterDateArray[1]));
                    tempWeatherStationData.setWindspeed(Float.parseFloat(inputLineAfterDateArray[2]));
                    tempWeatherStationData.setWinddirection(Float.parseFloat(inputLineAfterDateArray[3]));
                    tempWeatherStationData.setWindgust(Float.parseFloat(inputLineAfterDateArray[4]));
                    tempWeatherStationData.setWinddirectionGust(Float.parseFloat(inputLineAfterDateArray[5]));
                    tempWeatherStationData.setSnowHeight(Float.parseFloat(inputLineAfterDateArray[6]));
                    tempWeatherStationData.setGlobalstr(Float.parseFloat(inputLineAfterDateArray[7]));
                    tempWeatherStationData.setReflkwstr(Float.parseFloat(inputLineAfterDateArray[8]));
                    tempWeatherStationData.setAtmstragainst(Float.parseFloat(inputLineAfterDateArray[9]));
                    tempWeatherStationData.setAusstr(Float.parseFloat(inputLineAfterDateArray[10]));
                    tempWeatherStationData.setTempjuddsnowheightsensor(Float.parseFloat(inputLineAfterDateArray[11]));
                    tempWeatherStationData.setBatteryVoltage(Float.parseFloat(inputLineAfterDateArray[12]));
                    tempWeatherStationData.setVenti(Integer.parseInt(inputLineAfterDateArray[13]));
                    tempWeatherStationData.setVaporpressure(Float.parseFloat(inputLineAfterDateArray[14]));
                    tempWeatherStationData.setSaturationvaporpressure(Float.parseFloat(inputLineAfterDateArray[15]));
                    tempWeatherStationData.setDewpoint(Float.parseFloat(inputLineAfterDateArray[16]));
                    tempWeatherStationData.setAirpressure(Float.parseFloat(inputLineAfterDateArray[17]));
                    tempWeatherStationData.setSurfacetemperature(Float.parseFloat(inputLineAfterDateArray[18]));
                    //inputLineAfterDateArray index from 0 to 19, value index 18,19 are the same, size 20

                    //weatherStation.getWeatherStationData().add(tempWeatherStationData);
                    tempWeatherStationDataList.add(tempWeatherStationData);
                    counterWeatherForecastStationData--;
                }
            }
            calculateTableValues();

            reader.close();
            inputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * This method is used to open and extract the weather data file of the weather station tarntaler for the weather forecast activity.
     */

    private void setWeatherStationForecast2() {
        try{
            //AssetManager assetManager = context.getAssets();
            //InputStream inputStream  = assetManager.open("meteo_tarntaler_201617.dat");
            InputStream inputStream  = weatherForecastActivity.getAssets().open("meteo_tarntaler_201617.dat");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            String inputLineAfterDate;
            String[] inputLineAfterDateArray;
            while ((inputLine = reader.readLine()) != null) {
                Calendar calendar = parseDateTimeWeatherForecast2CheckCurrentTime(inputLine);
                if(last7dayWeatherStationDataFound && counterWeatherForecastStationData>=0){
                    tempWeatherStationData = new WeatherStationData();

                    tempWeatherStationData.setDateTime(calendar);

                    inputLineAfterDate=inputLine.substring(12);
                    //reduce spaces with more than one to one
                    inputLineAfterDate=inputLineAfterDate.trim().replaceAll(" +", " ");

                    //split string line to pure number values
                    inputLineAfterDateArray = inputLineAfterDate.split(" ");

                /*TESTING
                Log.i("WEATHERSTATION",inputLineAfterDate);
                for(int i=0; i<inputLineAfterDateArray.length; i++){
                    Log.i("WEATHERSTATION-ARRAY","a"+inputLineAfterDateArray[i] + "a "+ i);
                }
                Log.i("WEATHERSTATION-LENGTH",String.valueOf(inputLineAfterDateArray.length));
                */

                    //extract inputLineAfterDateArray and set WeatherStationData model
                    tempWeatherStationData.setAirtemperature(Float.parseFloat(inputLineAfterDateArray[0]));
                    tempWeatherStationData.setRelHumidity(Float.parseFloat(inputLineAfterDateArray[1]));
                    tempWeatherStationData.setWindspeed(Float.parseFloat(inputLineAfterDateArray[2]));
                    tempWeatherStationData.setWinddirection(Float.parseFloat(inputLineAfterDateArray[3]));
                    tempWeatherStationData.setWindgust(Float.parseFloat(inputLineAfterDateArray[4]));
                    tempWeatherStationData.setWinddirectionGust(Float.parseFloat(inputLineAfterDateArray[5]));
                    tempWeatherStationData.setSnowHeight(Float.parseFloat(inputLineAfterDateArray[6]));
                    //inputLineAfterDateArray index from 0 to 6, size 7

                    tempWeatherStationDataList.add(tempWeatherStationData);
                    counterWeatherForecastStationData--;
                }

            }
            calculateTableValues();

            reader.close();
            inputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * This method parses the date and time of the weather station data of snowpillow for the weather forecast activity.
     * @param dateTime The date and time as string.
     * @return Returns the date and time as calendar object.
     */

    private Calendar parseDateTimeWeatherForecast1CheckCurrentTime(String dateTime){
        Calendar calender = Calendar.getInstance();
        calender.set(Integer.parseInt(dateTime.substring(6,10)),Integer.parseInt(dateTime.substring(3,5))-1, Integer.parseInt(dateTime.substring(0,2)), Integer.parseInt(dateTime.substring(11,13)),Integer.parseInt(dateTime.substring(14,16)),0);

        if(calender.get(Calendar.YEAR) == last7dayTime.get(Calendar.YEAR) &&
                calender.get(Calendar.MONTH) == last7dayTime.get(Calendar.MONTH) &&
                calender.get(Calendar.DAY_OF_MONTH) == last7dayTime.get(Calendar.DAY_OF_MONTH)) {
            last7dayWeatherStationDataFound=true;
        }
        return calender;
    }

    /**
     * This method parses the date and time of the weather station data of tarntaler for the weather forecast activity.
     * @param dateTime The date and time as string.
     * @return Returns the date and time as calendar object.
     */

    private Calendar parseDateTimeWeatherForecast2CheckCurrentTime(String dateTime){
        Calendar calender = Calendar.getInstance();
        calender.set(Integer.parseInt(dateTime.substring(0,4)),Integer.parseInt(dateTime.substring(4,6))-1, Integer.parseInt(dateTime.substring(6,8)), Integer.parseInt(dateTime.substring(8,10)),Integer.parseInt(dateTime.substring(10,12)),0);
        if(calender.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR) &&
                calender.get(Calendar.MONTH) == currentTime.get(Calendar.MONTH) &&
                calender.get(Calendar.DAY_OF_MONTH) == currentTime.get(Calendar.DAY_OF_MONTH)) {
            last7dayWeatherStationDataFound=true;
        }
        return calender;
    }

    /**
     * This method calculate the sum of every value for every day for the weather forecast activity.
     * @return Returns the weather station object with all sum values.
     */

    private WeatherStation calculateTableValues() {
        if(activity!=null){
            weatherStation=new WeatherStation();
        }
        List<Float> listAirTemp;
        float airTempMin;
        float airTempMax;
        float windDirection=0.0f;
        int counterWindDirection=0;
        float windSpeed=0.0f;
        int counterWindSpeed=0;
        float snowHeight=0.0f;
        List<Float> listSnowHeight;
        float relHumidity= 0.0f;
        int counterRelHumidity=0;

        WeatherStationData wsd= new WeatherStationData();
        int tempDay;
        tempDay=tempWeatherStationDataList.get(0).getDateTime().get(Calendar.DAY_OF_MONTH);
        wsd.setDateTime(tempWeatherStationDataList.get(0).getDateTime());
        listAirTemp= new ArrayList<>();
        listSnowHeight= new ArrayList<>();
        int i;
        for(i =0; i<tempWeatherStationDataList.size();i++){
            if(tempDay==tempWeatherStationDataList.get(i).getDateTime().get(Calendar.DAY_OF_MONTH)){
                //calculate average, sum, etc.
                listAirTemp.add(tempWeatherStationDataList.get(i).getAirtemperature());
                windDirection+=tempWeatherStationDataList.get(i).getWinddirection();
                counterWindDirection++;
                windSpeed+=tempWeatherStationDataList.get(i).getWindspeed();
                counterWindSpeed++;
                listSnowHeight.add(tempWeatherStationDataList.get(i).getSnowHeight());
                relHumidity+=tempWeatherStationDataList.get(i).getRelHumidity();
                counterRelHumidity++;

            } else{
                //calculate results

                //min and max value
                airTempMin=Collections.min(listAirTemp);
                airTempMax=Collections.max(listAirTemp);
                //average value
                windDirection/=counterWindDirection;
                windSpeed/=counterWindSpeed;
                //max snow height
                snowHeight=Collections.max(listSnowHeight);
                //average
                relHumidity/=counterRelHumidity;

                //set values to WeatherStationData
                wsd.setAirtemperature(airTempMin);
                wsd.setSurfacetemperature(airTempMax);
                wsd.setWinddirection(windDirection);
                wsd.setWindspeed(windSpeed);
                wsd.setSnowHeight(snowHeight);
                wsd.setRelHumidity(relHumidity);

                //set value to final model for weatherforecastactivity
                if(weatherStationActivity != null || weatherForecastActivity!=null){
                    weatherStation.getWeatherStationData().add(wsd);
                } else{
                    weatherStation.getWeatherStationData().add(wsd);
                }

                //reset all values
                tempDay=tempWeatherStationDataList.get(i).getDateTime().get(Calendar.DAY_OF_MONTH);
                //last7dayTime.add(Calendar.DATE, -7);
                wsd = new WeatherStationData();
                wsd.setDateTime(tempWeatherStationDataList.get(i).getDateTime());
                listAirTemp = new ArrayList<>();
                windDirection=0.0f;
                counterWindDirection=0;
                windSpeed=0.0f;
                counterWindSpeed=0;
                listSnowHeight= new ArrayList<>();
                relHumidity = 0.0f;
                counterRelHumidity=0;
                airTempMin=0;
                airTempMax=0;
                snowHeight=0.0f;

                //set next values for next day first time interval
                listAirTemp.add(tempWeatherStationDataList.get(i).getAirtemperature());
                windDirection+=tempWeatherStationDataList.get(i).getWinddirection();
                counterWindDirection++;
                windSpeed+=tempWeatherStationDataList.get(i).getWindspeed();
                counterWindSpeed++;
                listSnowHeight.add(tempWeatherStationDataList.get(i).getSnowHeight());
                relHumidity+=tempWeatherStationDataList.get(i).getRelHumidity();
                counterRelHumidity++;
            }
        }
        //set last values of tempWeatherStationDataList.size()
        if(i==tempWeatherStationDataList.size()){
            //calculate results

            //min and max value
            airTempMin=Collections.min(listAirTemp);
            airTempMax=Collections.max(listAirTemp);
            //average value
            windDirection/=counterWindDirection;
            windSpeed/=counterWindSpeed;
            //max snow height
            snowHeight=Collections.max(listSnowHeight);
            //average
            relHumidity/=counterRelHumidity;

            //set values to WeatherStationData
            wsd.setAirtemperature(airTempMin);
            wsd.setSurfacetemperature(airTempMax);
            wsd.setWinddirection(windDirection);
            wsd.setWindspeed(windSpeed);
            wsd.setSnowHeight(snowHeight);
            wsd.setRelHumidity(relHumidity);
            wsd.setDateTime(tempWeatherStationDataList.get(i-1).getDateTime());
            //set value to final model for weatherforecastactivity

            if(weatherStationActivity != null || weatherForecastActivity!=null){
                weatherStation.getWeatherStationData().add(wsd);
            } else{
                weatherStation.getWeatherStationData().add(wsd);
            }
        }
        return weatherStation;
    }

    /**
     * This method calculates the sum of the current day for both weather stations.
     * @return Returns the mean of the wind direction.
     */

    public float calculateSumWeatherStationDataCurrentDayWindDirection(){
        //load scenario date
        File fileScenario= new File(activity.getFilesDir(),ActivityConstants.ScenarioFileName);
        if(fileScenario.exists()){
            currentTime = DAOSerialisationManager.readSerializedFile(fileScenario.getAbsolutePath());
        }else {
            currentTime.set(2017, 4, 1);
        }

        //check to not reach dates before the begin of .dat files
        if(currentTime.get(Calendar.DAY_OF_MONTH)<8 && currentTime.get(Calendar.MONTH)<11 && currentTime.get(Calendar.YEAR)<2017){
            currentTime.set(2016, 9, 8);
            //check to not reach dates after the end of .dat files
        } else if(currentTime.get(Calendar.DAY_OF_MONTH)>21 && currentTime.get(Calendar.MONTH)>3 && currentTime.get(Calendar.YEAR)>2016){
            currentTime.set(2017, 4, 21);
        }

        WeatherStation weatherStationSnowpillow = new WeatherStation("Wetterstation Snowpillow");
        WeatherStation weatherStationTarntalerboden = new WeatherStation("Wetterstation Tarntalerboden");
        tempWeatherStationDataList = new ArrayList<>();
        weatherStationSnowpillow=getWeatherStationSnowpillowDataCurrentDay();
        tempWeatherStationDataList = new ArrayList<>();
        weatherStationTarntalerboden= getWeatherStationTarntalerbodenDataCurrentDay();

        float windDirection= 0.0f;
        windDirection=+weatherStationSnowpillow.getWeatherStationData().get(0).getWinddirection();
        windDirection=+weatherStationTarntalerboden.getWeatherStationData().get(0).getWinddirection();
        windDirection/=2;
        return windDirection;
    }

    /**
     * This method opens and extract the snowpillow weather station data and calculate the sum.
     * @return Returns the weather station object.
     */

    private WeatherStation getWeatherStationSnowpillowDataCurrentDay() {
        WeatherStation tempWeatherStation = new WeatherStation();
        try{
            InputStream inputStream  = activity.getAssets().open("meteo_snowpillow_201617.dat");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            String inputLineAfterDate;
            String[] inputLineAfterDateArray;
            while ((inputLine = reader.readLine()) != null) {
                Calendar calendar = parseDateTimeCheckCurrentTimeSnowpillow(inputLine,currentTime);
                if(currentDayWeatherStationDataFound && counterWindDirection>=0){
                    tempWeatherStationData = new WeatherStationData();
                    tempWeatherStationData.setDateTime(calendar);

                    inputLineAfterDate=inputLine.substring(21);
                    inputLineAfterDateArray = inputLineAfterDate.split(" ");

                    //check wrong values of .dat file and replace them
                    for(int i =0; i<inputLineAfterDateArray.length; i++){
                        if(inputLineAfterDateArray[i].equalsIgnoreCase("-9999") || inputLineAfterDateArray[i].equalsIgnoreCase("-9999.90")){
                            inputLineAfterDateArray[i]="0.0";
                        }
                    }
                    //extract inputLineAfterDateArray and set WeatherStationData model
                    tempWeatherStationData.setAirtemperature(Float.parseFloat(inputLineAfterDateArray[0]));
                    tempWeatherStationData.setRelHumidity(Float.parseFloat(inputLineAfterDateArray[1]));
                    tempWeatherStationData.setWindspeed(Float.parseFloat(inputLineAfterDateArray[2]));
                    tempWeatherStationData.setWinddirection(Float.parseFloat(inputLineAfterDateArray[3]));
                    tempWeatherStationData.setWindgust(Float.parseFloat(inputLineAfterDateArray[4]));
                    tempWeatherStationData.setWinddirectionGust(Float.parseFloat(inputLineAfterDateArray[5]));
                    tempWeatherStationData.setSnowHeight(Float.parseFloat(inputLineAfterDateArray[6]));
                    tempWeatherStationData.setGlobalstr(Float.parseFloat(inputLineAfterDateArray[7]));
                    tempWeatherStationData.setReflkwstr(Float.parseFloat(inputLineAfterDateArray[8]));
                    tempWeatherStationData.setAtmstragainst(Float.parseFloat(inputLineAfterDateArray[9]));
                    tempWeatherStationData.setAusstr(Float.parseFloat(inputLineAfterDateArray[10]));
                    tempWeatherStationData.setTempjuddsnowheightsensor(Float.parseFloat(inputLineAfterDateArray[11]));
                    tempWeatherStationData.setBatteryVoltage(Float.parseFloat(inputLineAfterDateArray[12]));
                    tempWeatherStationData.setVenti(Integer.parseInt(inputLineAfterDateArray[13]));
                    tempWeatherStationData.setVaporpressure(Float.parseFloat(inputLineAfterDateArray[14]));
                    tempWeatherStationData.setSaturationvaporpressure(Float.parseFloat(inputLineAfterDateArray[15]));
                    tempWeatherStationData.setDewpoint(Float.parseFloat(inputLineAfterDateArray[16]));
                    tempWeatherStationData.setAirpressure(Float.parseFloat(inputLineAfterDateArray[17]));
                    tempWeatherStationData.setSurfacetemperature(Float.parseFloat(inputLineAfterDateArray[18]));
                    //inputLineAfterDateArray index from 0 to 19, value index 18,19 are the same, size 20

                    //weatherStation.getWeatherStationData().add(tempWeatherStationData);
                    tempWeatherStationDataList.add(tempWeatherStationData);
                    counterWindDirection--;
                }
            }
            currentDayWeatherStationDataFound=false;
            counterWindDirection=143;

            tempWeatherStation=calculateTableValues();

            reader.close();
            inputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return tempWeatherStation;
    }

    /**
     * This method opens and extract the tarntalerboden weather station data and calculate the sum.
     * @return Returns the weather station object.
     */

    private WeatherStation getWeatherStationTarntalerbodenDataCurrentDay() {
        WeatherStation tempWeatherStation = new WeatherStation();
        try{
            InputStream inputStream  = activity.getAssets().open("meteo_tarntaler_201617.dat");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            String inputLineAfterDate;
            String[] inputLineAfterDateArray;
            while ((inputLine = reader.readLine()) != null) {
                Calendar calendar = parseDateTimeCheckCurrentTimeTarntalerboden(inputLine,currentTime);
                if(currentDayWeatherStationDataFound && counterWindDirection>=0){
                    tempWeatherStationData = new WeatherStationData();

                    tempWeatherStationData.setDateTime(calendar);

                    inputLineAfterDate=inputLine.substring(12);
                    //reduce spaces with more than one to one
                    inputLineAfterDate=inputLineAfterDate.trim().replaceAll(" +", " ");

                    //split string line to pure number values
                    inputLineAfterDateArray = inputLineAfterDate.split(" ");

                    //extract inputLineAfterDateArray and set WeatherStationData model
                    tempWeatherStationData.setAirtemperature(Float.parseFloat(inputLineAfterDateArray[0]));
                    tempWeatherStationData.setRelHumidity(Float.parseFloat(inputLineAfterDateArray[1]));
                    tempWeatherStationData.setWindspeed(Float.parseFloat(inputLineAfterDateArray[2]));
                    tempWeatherStationData.setWinddirection(Float.parseFloat(inputLineAfterDateArray[3]));
                    tempWeatherStationData.setWindgust(Float.parseFloat(inputLineAfterDateArray[4]));
                    tempWeatherStationData.setWinddirectionGust(Float.parseFloat(inputLineAfterDateArray[5]));
                    tempWeatherStationData.setSnowHeight(Float.parseFloat(inputLineAfterDateArray[6]));
                    //inputLineAfterDateArray index from 0 to 6, size 7

                    tempWeatherStationDataList.add(tempWeatherStationData);
                    counterWindDirection--;
                }

            }
            currentDayWeatherStationDataFound=false;
            counterWindDirection=143;
            tempWeatherStation=calculateTableValues();

            reader.close();
            inputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return tempWeatherStation;
    }

    /**
     * This method parses the date and time of snowpillow.
     * @param dateTime The string of the date time.
     * @param scenarioTime The scenario calendar object.
     * @return The parsed calendar object.
     */

    private Calendar parseDateTimeCheckCurrentTimeSnowpillow(String dateTime, Calendar scenarioTime){
        Calendar calender = Calendar.getInstance();
        calender.set(Integer.parseInt(dateTime.substring(6,10)),Integer.parseInt(dateTime.substring(3,5))-1, Integer.parseInt(dateTime.substring(0,2)), Integer.parseInt(dateTime.substring(11,13)),Integer.parseInt(dateTime.substring(14,16)),0);

        if(calender.get(Calendar.YEAR) == scenarioTime.get(Calendar.YEAR) &&
                calender.get(Calendar.MONTH) == scenarioTime.get(Calendar.MONTH) &&
                calender.get(Calendar.DAY_OF_MONTH) == scenarioTime.get(Calendar.DAY_OF_MONTH)) {
            currentDayWeatherStationDataFound=true;
        }
        return calender;
    }

    /**
     * This method parses the date and time of tarntaler.
     * @param dateTime The string of the date time.
     * @param scenarioTime The scenario calendar object.
     * @return The parsed calendar object.
     */

    private Calendar parseDateTimeCheckCurrentTimeTarntalerboden(String dateTime,Calendar scenarioTime){
        Calendar calender = Calendar.getInstance();
        calender.set(Integer.parseInt(dateTime.substring(0,4)),Integer.parseInt(dateTime.substring(4,6))-1, Integer.parseInt(dateTime.substring(6,8)), Integer.parseInt(dateTime.substring(8,10)),Integer.parseInt(dateTime.substring(10,12)),0);
        if(calender.get(Calendar.YEAR) == scenarioTime.get(Calendar.YEAR) &&
                calender.get(Calendar.MONTH) == scenarioTime.get(Calendar.MONTH) &&
                calender.get(Calendar.DAY_OF_MONTH) == scenarioTime.get(Calendar.DAY_OF_MONTH)) {
            currentDayWeatherStationDataFound=true;
        }
        return calender;
    }
}
