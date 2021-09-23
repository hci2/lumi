package at.ac.univie.lumi.model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by phili on 5/26/2017.
 *
 *  This class is part of the model and contains the weather station data model for one timeslot of one day of one weather station.
 */

public class WeatherStationData implements Serializable {
    private String day;
    private String month;
    private String year;
    private String hours;
    private String minutes;

    private Calendar dateTime;
    private float airtemperature,
            relHumidity,
            windspeed,
            winddirection,
            windgust,
            winddirectionGust,
            snowHeight,
            globalstr,
            reflkwstr,
            atmstragainst,
            ausstr,
            tempjuddsnowheightsensor,
            batteryVoltage,
            vaporpressure,
            saturationvaporpressure,
            dewpoint,
            airpressure,
            surfacetemperature;

    private int venti;

    /* FORMAT
Tag
Monat
Jahr
Stunde
Minute
Tag im Jahr
tair ... Lufttemp.
rh   ... rel. Feuchte
ff   ... Windgeschw.
ddvm  ... Windrichtung (Vektomittel)
ffmax ... Böe
ddffmax  ... Windrichtung der Böe
sd   ... Schneehöhe
Kein ... Globalstr.
Kaus ... refl. kw. Str.
Lein ... atm. Gegenstr.
Laus ... Ausstr.
Tjudd ... Temp. Judd Schneehöhensensor
U1   ... Batteriespannung
venti ... Venitalion ein/aus (defekt)
e    ... Dampfdruck
es   ... Sättingungsdampfdruck
Td   ... Taupunkt
p    ... Luftdruck
tsurf ... Oberflächentemperatur
     */

    /**
     * The constructor initialize an empty weather station model.
     */


    public WeatherStationData() {
    }

    /**
     * The different getter and setter methods of every variable of the weather station data model.
     *
     */

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public Calendar getDateTime() {
        return dateTime;
    }

    public void setDateTime(Calendar dateTime) {
        this.dateTime = dateTime;
    }

    public float getAirtemperature() {
        return airtemperature;
    }

    public void setAirtemperature(float airtemperature) {
        this.airtemperature = airtemperature;
    }

    public float getRelHumidity() {
        return relHumidity;
    }

    public void setRelHumidity(float relHumidity) {
        this.relHumidity = relHumidity;
    }

    public float getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(float windspeed) {
        this.windspeed = windspeed;
    }

    public float getWinddirection() {
        return winddirection;
    }

    public void setWinddirection(float winddirection) {
        this.winddirection = winddirection;
    }

    public float getWindgust() {
        return windgust;
    }

    public void setWindgust(float windgust) {
        this.windgust = windgust;
    }

    public float getWinddirectionGust() {
        return winddirectionGust;
    }

    public void setWinddirectionGust(float winddirectionGust) {
        this.winddirectionGust = winddirectionGust;
    }

    public float getSnowHeight() {
        return snowHeight;
    }

    public void setSnowHeight(float snowHeight) {
        this.snowHeight = snowHeight;
    }

    public float getGlobalstr() {
        return globalstr;
    }

    public void setGlobalstr(float globalstr) {
        this.globalstr = globalstr;
    }

    public float getReflkwstr() {
        return reflkwstr;
    }

    public void setReflkwstr(float reflkwstr) {
        this.reflkwstr = reflkwstr;
    }

    public float getAtmstragainst() {
        return atmstragainst;
    }

    public void setAtmstragainst(float atmstragainst) {
        this.atmstragainst = atmstragainst;
    }

    public float getAusstr() {
        return ausstr;
    }

    public void setAusstr(float ausstr) {
        this.ausstr = ausstr;
    }

    public float getTempjuddsnowheightsensor() {
        return tempjuddsnowheightsensor;
    }

    public void setTempjuddsnowheightsensor(float tempjuddsnowheightsensor) {
        this.tempjuddsnowheightsensor = tempjuddsnowheightsensor;
    }

    public int getVenti() {
        return venti;
    }

    public void setVenti(int venti) {
        this.venti = venti;
    }

    public float getVaporpressure() {
        return vaporpressure;
    }

    public void setVaporpressure(float vaporpressure) {
        this.vaporpressure = vaporpressure;
    }

    public float getSaturationvaporpressure() {
        return saturationvaporpressure;
    }

    public void setSaturationvaporpressure(float saturationvaporpressure) {
        this.saturationvaporpressure = saturationvaporpressure;
    }

    public float getDewpoint() {
        return dewpoint;
    }

    public void setDewpoint(float dewpoint) {
        this.dewpoint = dewpoint;
    }

    public float getAirpressure() {
        return airpressure;
    }

    public void setAirpressure(float airpressure) {
        this.airpressure = airpressure;
    }

    public float getSurfacetemperature() {
        return surfacetemperature;
    }

    public void setSurfacetemperature(float surfacetemperature) {
        this.surfacetemperature = surfacetemperature;
    }

    public float getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(float batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }



}
