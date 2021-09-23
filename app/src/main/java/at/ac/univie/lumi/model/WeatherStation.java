package at.ac.univie.lumi.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by phili on 5/26/2017.
 *
 * This class is part of the model and contains the weather station model for the different weather stations.
 */

public class WeatherStation implements Serializable {
    private String name;
    private ArrayList<WeatherStationData> weatherStationData;

    /**
     * This constructor initialize the weather station data arraylist and set the name of the weather station.
     * @param name The name of the weather station.
     */

    public WeatherStation(String name) {
        this.name = name;
        weatherStationData = new ArrayList<>();
    }

    /**
     * The different getter and setter methods of every variable of the weather station model.
     *
     */

    public WeatherStation() {
        weatherStationData = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<WeatherStationData> getWeatherStationData() {
        return weatherStationData;
    }

    public void setWeatherStationData(ArrayList<WeatherStationData> weatherStationData) {
        this.weatherStationData = weatherStationData;
    }
}
