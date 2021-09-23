package at.ac.univie.lumi.controller;

import java.util.Calendar;

/**
 * Created by phili on 5/30/2017.
 *
 * The class is used to have final vars which are not changeable and can be used from every class.
 */

public interface ActivityConstants {

    public static final int AvalancheBulletinActivity = 1;
    public static final int EmergencyCallActivity = 2;
    public static final int MapActivity = 3;
    public static final int OverviewActivity = 4;
    public static final int WeatherStationActivity = 5;
    public static final int WeatherForecastActivity = 6;
    public static final int AboutUsActivity = 7;

    public static final String MapMarkerListFileName ="listmapmarkers.ser";
    public static final String AvalancheBulletinFileName="avalanchebulletin.ser";
    public static final String ScenarioFileName="scenario.ser";

    public static final String Scenario1="2017-03-14";
    public static final String Scenario2="2017-01-14";
    public static final String Scenario3="2017-05-14";
    public static final String Scenario3Fake="2017-05-01";

}
