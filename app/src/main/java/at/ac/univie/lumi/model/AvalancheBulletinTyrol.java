package at.ac.univie.lumi.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by phili on 5/10/2017.
 *
 * This class is part of the model and contains the avalanche bulletin of the avalanche warning service Tyrol. It will be also serialized on the smartphone of the user.
 */

public class AvalancheBulletinTyrol implements Serializable {

    private static final long serialVersionUID = 0L;

    private ArrayList<AvalancheSlopeAreaBulletinTyrol> avalancheSlopeAreaBulletinTyrolArrayList;

    private float windDirection;

    private Calendar dateTime;
    private String dateTimeString;
    private String creator;

    private String linkBulletinPhoto;

    private String linkDangerRatingMapColour;
    private String linkDangerRatingMapDetail;

    //Images for avalanche problem1
    private String avalancheProblem1ImageLink;
    private String avalancheProblem1ImageLinkElevation;
    private String avalancheProblem1ImageLinkExposition;
    //Images for avalanche problem2
    private String avalancheProblem2ImageLink;
    private String avalancheProblem2ImageLinkElevation;
    private String avalancheProblem2ImageLinkExposition;
    //Images for avalanche problem3
    private String avalancheProblem3ImageLink;
    private String avalancheProblem3ImageLinkElevation;
    private String avalancheProblem3ImageLinkExposition;

    private String linkGeneralDangerLevelTyrol;
    private String linkTendency;
    //whole avalanche report as pdf file
    private String linkPdf;

    //Description of Tendency
    private String tendecyAvalancheSituation;
    private String highlights;

    private Calendar past3DangerLevelTime;
    private String past3DangerLevelTimeString;
    private int past3DangerLevelValue;

    private Calendar past2DangerLevelTime;
    private String past2DangerLevelTimeString;
    private int past2DangerLevelValue;

    private Calendar past1DangerLevelTime;
    private String past1DangerLevelTimeString;
    private int past1DangerLevelValue;

    private Calendar currentDangerLevelTime;
    private String currentDangerLevelTimeString;
    private int currentDangerLevelValue;

    private Calendar future1DangerLevelTime;
    private String future1DangerLevelTimeString;
    private int future1DangerLevelValue;

    //AT7R1
    private String region1DangerLevelMorningRegion;
    private int region1DangerLevelMorningBorder;
    private int region1DangerLevelMorningBorderBelow;
    private int region1DangerLevelMorningBorderAbove;
    private String region1DangerLevelAfternoonRegion;
    private int region1DangerLevelAfternoonBorder;
    private int region1DangerLevelAfternoonBorderBelow;
    private int region1DangerLevelAfternoonBorderAbove;

    //AT7R2
    private String region2DangerLevelMorningRegion;
    private int region2DangerLevelMorningBorder;
    private int region2DangerLevelMorningBorderBelow;
    private int region2DangerLevelMorningBorderAbove;
    private String region2DangerLevelAfternoonRegion;
    private int region2DangerLevelAfternoonBorder;
    private int region2DangerLevelAfternoonBorderBelow;
    private int region2DangerLevelAfternoonBorderAbove;

    //AT7R3
    private String region3DangerLevelMorningRegion;
    private int region3DangerLevelMorningBorder;
    private int region3DangerLevelMorningBorderBelow;
    private int region3DangerLevelMorningBorderAbove;
    private String region3DangerLevelAfternoonRegion;
    private int region3DangerLevelAfternoonBorder;
    private int region3DangerLevelAfternoonBorderBelow;
    private int region3DangerLevelAfternoonBorderAbove;

    //AT7R4
    private String region4DangerLevelMorningRegion;
    private int region4DangerLevelMorningBorder;
    private int region4DangerLevelMorningBorderBelow;
    private int region4DangerLevelMorningBorderAbove;
    private String region4DangerLevelAfternoonRegion;
    private int region4DangerLevelAfternoonBorder;
    private int region4DangerLevelAfternoonBorderBelow;
    private int region4DangerLevelAfternoonBorderAbove;

    //AT7R5
    private String region5DangerLevelMorningRegion;
    private int region5DangerLevelMorningBorder;
    private int region5DangerLevelMorningBorderBelow;
    private int region5DangerLevelMorningBorderAbove;
    private String region5DangerLevelAfternoonRegion;
    private int region5DangerLevelAfternoonBorder;
    private int region5DangerLevelAfternoonBorderBelow;
    private int region5DangerLevelAfternoonBorderAbove;

    //AT7R6
    private String region6DangerLevelMorningRegion;
    private int region6DangerLevelMorningBorder;
    private int region6DangerLevelMorningBorderBelow;
    private int region6DangerLevelMorningBorderAbove;
    private String region6DangerLevelAfternoonRegion;
    private int region6DangerLevelAfternoonBorder;
    private int region6DangerLevelAfternoonBorderBelow;
    private int region6DangerLevelAfternoonBorderAbove;

    //AT7R7
    private String region7DangerLevelMorningRegion;
    private int region7DangerLevelMorningBorder;
    private int region7DangerLevelMorningBorderBelow;
    private int region7DangerLevelMorningBorderAbove;
    private String region7DangerLevelAfternoonRegion;
    private int region7DangerLevelAfternoonBorder;
    private int region7DangerLevelAfternoonBorderBelow;
    private int region7DangerLevelAfternoonBorderAbove;

    //AT7R8
    private String region8DangerLevelMorningRegion;
    private int region8DangerLevelMorningBorder;
    private int region8DangerLevelMorningBorderBelow;
    private int region8DangerLevelMorningBorderAbove;
    private String region8DangerLevelAfternoonRegion;
    private int region8DangerLevelAfternoonBorder;
    private int region8DangerLevelAfternoonBorderBelow;
    private int region8DangerLevelAfternoonBorderAbove;

    //AT7R9
    private String region9DangerLevelMorningRegion;
    private int region9DangerLevelMorningBorder;
    private int region9DangerLevelMorningBorderBelow;
    private int region9DangerLevelMorningBorderAbove;
    private String region9DangerLevelAfternoonRegion;
    private int region9DangerLevelAfternoonBorder;
    private int region9DangerLevelAfternoonBorderBelow;
    private int region9DangerLevelAfternoonBorderAbove;

    //AT7R10
    private String region10DangerLevelMorningRegion;
    private int region10DangerLevelMorningBorder;
    private int region10DangerLevelMorningBorderBelow;
    private int region10DangerLevelMorningBorderAbove;
    private String region10DangerLevelAfternoonRegion;
    private int region10DangerLevelAfternoonBorder;
    private int region10DangerLevelAfternoonBorderBelow;
    private int region10DangerLevelAfternoonBorderAbove;

    //AT7R11
    private String region11DangerLevelMorningRegion;
    private int region11DangerLevelMorningBorder;
    private int region11DangerLevelMorningBorderBelow;
    private int region11DangerLevelMorningBorderAbove;
    private String region11DangerLevelAfternoonRegion;
    private int region11DangerLevelAfternoonBorder;
    private int region11DangerLevelAfternoonBorderBelow;
    private int region11DangerLevelAfternoonBorderAbove;

    //AT7R12
    private String region12DangerLevelMorningRegion;
    private int region12DangerLevelMorningBorder;
    private int region12DangerLevelMorningBorderBelow;
    private int region12DangerLevelMorningBorderAbove;
    private String region12DangerLevelAfternoonRegion;
    private int region12DangerLevelAfternoonBorder;
    private int region12DangerLevelAfternoonBorderBelow;
    private int region12DangerLevelAfternoonBorderAbove;

    //Further details: https://lawine.tirol.gv.at/basics/lawinengefahrenmuster/
    private ArrayList<String> dangerPattern;

    private ArrayList<AvalancheProblemTyrol> avalancheProblemTyrol;

    private String zamgWeatherStation;
    private String zamgWeatherStationReport;

    private String snowpackStructure;
    private String snowpackStructureDescription;

    private String avalancheDangerAssessment;
    private String avalancheDangerAssessmentDescription;

    /**
     * The constructor initiate the different arraylist objects.
     */

    public AvalancheBulletinTyrol(){
        dangerPattern = new ArrayList<>();
        avalancheProblemTyrol = new ArrayList<>();
        avalancheSlopeAreaBulletinTyrolArrayList = new ArrayList<>();
    }

    /**
     * The different getter and setter methods of every variable of the avalanche bulletin model.
     *
     */

    public ArrayList<AvalancheSlopeAreaBulletinTyrol> getAvalancheSlopeAreaBulletinTyrolArrayList() {
        return avalancheSlopeAreaBulletinTyrolArrayList;
    }

    public void setAvalancheSlopeAreaBulletinTyrolArrayList(ArrayList<AvalancheSlopeAreaBulletinTyrol> avalancheSlopeAreaBulletinTyrolArrayList) {
        this.avalancheSlopeAreaBulletinTyrolArrayList = avalancheSlopeAreaBulletinTyrolArrayList;
    }

    public String getDateTimeString() {
        return dateTimeString;
    }

    public void setDateTimeString(String dateTimeString) {
        this.dateTimeString = dateTimeString;
    }

    public String getPast3DangerLevelTimeString() {
        return past3DangerLevelTimeString;
    }

    public void setPast3DangerLevelTimeString(String past3DangerLevelTimeString) {
        this.past3DangerLevelTimeString = past3DangerLevelTimeString;
    }

    public String getPast2DangerLevelTimeString() {
        return past2DangerLevelTimeString;
    }

    public void setPast2DangerLevelTimeString(String past2DangerLevelTimeString) {
        this.past2DangerLevelTimeString = past2DangerLevelTimeString;
    }

    public String getPast1DangerLevelTimeString() {
        return past1DangerLevelTimeString;
    }

    public void setPast1DangerLevelTimeString(String past1DangerLevelTimeString) {
        this.past1DangerLevelTimeString = past1DangerLevelTimeString;
    }

    public String getCurrentDangerLevelTimeString() {
        return currentDangerLevelTimeString;
    }

    public void setCurrentDangerLevelTimeString(String currentDangerLevelTimeString) {
        this.currentDangerLevelTimeString = currentDangerLevelTimeString;
    }

    public String getFuture1DangerLevelTimeString() {
        return future1DangerLevelTimeString;
    }

    public void setFuture1DangerLevelTimeString(String future1DangerLevelTimeString) {
        this.future1DangerLevelTimeString = future1DangerLevelTimeString;
    }

    public ArrayList<AvalancheProblemTyrol> getAvalancheProblemTyrol() {
        return avalancheProblemTyrol;
    }

    public void setAvalancheProblemTyrol(ArrayList<AvalancheProblemTyrol> avalancheProblemTyrol) {
        this.avalancheProblemTyrol = avalancheProblemTyrol;
    }

    public Calendar getDateTime() {
        return dateTime;
    }

    public void setDateTime(Calendar dateTime) {
        this.dateTime = dateTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getLinkBulletinPhoto() {
        return linkBulletinPhoto;
    }

    public void setLinkBulletinPhoto(String linkBulletinPhoto) {
        this.linkBulletinPhoto = linkBulletinPhoto;
    }

    public String getLinkDangerRatingMapColour() {
        return linkDangerRatingMapColour;
    }

    public void setLinkDangerRatingMapColour(String linkDangerRatingMapColour) {
        this.linkDangerRatingMapColour = linkDangerRatingMapColour;
    }

    public String getLinkDangerRatingMapDetail() {
        return linkDangerRatingMapDetail;
    }

    public void setLinkDangerRatingMapDetail(String linkDangerRatingMapDetail) {
        this.linkDangerRatingMapDetail = linkDangerRatingMapDetail;
    }

    public String getAvalancheProblem1ImageLink() {
        return avalancheProblem1ImageLink;
    }

    public void setAvalancheProblem1ImageLink(String avalancheProblem1ImageLink) {
        this.avalancheProblem1ImageLink = avalancheProblem1ImageLink;
    }

    public String getAvalancheProblem1ImageLinkElevation() {
        return avalancheProblem1ImageLinkElevation;
    }

    public void setAvalancheProblem1ImageLinkElevation(String avalancheProblem1ImageLinkElevation) {
        this.avalancheProblem1ImageLinkElevation = avalancheProblem1ImageLinkElevation;
    }

    public String getAvalancheProblem1ImageLinkExposition() {
        return avalancheProblem1ImageLinkExposition;
    }

    public void setAvalancheProblem1ImageLinkExposition(String avalancheProblem1ImageLinkExposition) {
        this.avalancheProblem1ImageLinkExposition = avalancheProblem1ImageLinkExposition;
    }

    public String getAvalancheProblem2ImageLink() {
        return avalancheProblem2ImageLink;
    }

    public void setAvalancheProblem2ImageLink(String avalancheProblem2ImageLink) {
        this.avalancheProblem2ImageLink = avalancheProblem2ImageLink;
    }

    public String getAvalancheProblem2ImageLinkElevation() {
        return avalancheProblem2ImageLinkElevation;
    }

    public void setAvalancheProblem2ImageLinkElevation(String avalancheProblem2ImageLinkElevation) {
        this.avalancheProblem2ImageLinkElevation = avalancheProblem2ImageLinkElevation;
    }

    public String getAvalancheProblem2ImageLinkExposition() {
        return avalancheProblem2ImageLinkExposition;
    }

    public void setAvalancheProblem2ImageLinkExposition(String avalancheProblem2ImageLinkExposition) {
        this.avalancheProblem2ImageLinkExposition = avalancheProblem2ImageLinkExposition;
    }

    public String getAvalancheProblem3ImageLink() {
        return avalancheProblem3ImageLink;
    }

    public void setAvalancheProblem3ImageLink(String avalancheProblem3ImageLink) {
        this.avalancheProblem3ImageLink = avalancheProblem3ImageLink;
    }

    public String getAvalancheProblem3ImageLinkElevation() {
        return avalancheProblem3ImageLinkElevation;
    }

    public void setAvalancheProblem3ImageLinkElevation(String avalancheProblem3ImageLinkElevation) {
        this.avalancheProblem3ImageLinkElevation = avalancheProblem3ImageLinkElevation;
    }

    public String getAvalancheProblem3ImageLinkExposition() {
        return avalancheProblem3ImageLinkExposition;
    }

    public void setAvalancheProblem3ImageLinkExposition(String avalancheProblem3ImageLinkExposition) {
        this.avalancheProblem3ImageLinkExposition = avalancheProblem3ImageLinkExposition;
    }

    public String getLinkGeneralDangerLevelTyrol() {
        return linkGeneralDangerLevelTyrol;
    }

    public void setLinkGeneralDangerLevelTyrol(String linkGeneralDangerLevelTyrol) {
        this.linkGeneralDangerLevelTyrol = linkGeneralDangerLevelTyrol;
    }

    public String getLinkTendency() {
        return linkTendency;
    }

    public void setLinkTendency(String linkTendency) {
        this.linkTendency = linkTendency;
    }

    public String getLinkPdf() {
        return linkPdf;
    }

    public void setLinkPdf(String linkPdf) {
        this.linkPdf = linkPdf;
    }

    public String getTendecyAvalancheSituation() {
        return tendecyAvalancheSituation;
    }

    public void setTendecyAvalancheSituation(String tendecyAvalancheSituation) {
        this.tendecyAvalancheSituation = tendecyAvalancheSituation;
    }

    public String getHighlights() {
        return highlights;
    }

    public void setHighlights(String highlights) {
        this.highlights = highlights;
    }

    public Calendar getPast3DangerLevelTime() {
        return past3DangerLevelTime;
    }

    public void setPast3DangerLevelTime(Calendar past3DangerLevelTime) {
        this.past3DangerLevelTime = past3DangerLevelTime;
    }

    public int getPast3DangerLevelValue() {
        return past3DangerLevelValue;
    }

    public void setPast3DangerLevelValue(int past3DangerLevelValue) {
        this.past3DangerLevelValue = past3DangerLevelValue;
    }

    public Calendar getPast2DangerLevelTime() {
        return past2DangerLevelTime;
    }

    public void setPast2DangerLevelTime(Calendar past2DangerLevelTime) {
        this.past2DangerLevelTime = past2DangerLevelTime;
    }

    public int getPast2DangerLevelValue() {
        return past2DangerLevelValue;
    }

    public void setPast2DangerLevelValue(int past2DangerLevelValue) {
        this.past2DangerLevelValue = past2DangerLevelValue;
    }

    public Calendar getPast1DangerLevelTime() {
        return past1DangerLevelTime;
    }

    public void setPast1DangerLevelTime(Calendar past1DangerLevelTime) {
        this.past1DangerLevelTime = past1DangerLevelTime;
    }

    public int getPast1DangerLevelValue() {
        return past1DangerLevelValue;
    }

    public void setPast1DangerLevelValue(int past1DangerLevelValue) {
        this.past1DangerLevelValue = past1DangerLevelValue;
    }

    public Calendar getCurrentDangerLevelTime() {
        return currentDangerLevelTime;
    }

    public void setCurrentDangerLevelTime(Calendar currentDangerLevelTime) {
        this.currentDangerLevelTime = currentDangerLevelTime;
    }

    public int getCurrentDangerLevelValue() {
        return currentDangerLevelValue;
    }

    public void setCurrentDangerLevelValue(int currentDangerLevelValue) {
        this.currentDangerLevelValue = currentDangerLevelValue;
    }

    public Calendar getFuture1DangerLevelTime() {
        return future1DangerLevelTime;
    }

    public void setFuture1DangerLevelTime(Calendar future1DangerLevelTime) {
        this.future1DangerLevelTime = future1DangerLevelTime;
    }

    public int getFuture1DangerLevelValue() {
        return future1DangerLevelValue;
    }

    public void setFuture1DangerLevelValue(int future1DangerLevelValue) {
        this.future1DangerLevelValue = future1DangerLevelValue;
    }

    public String getRegion1DangerLevelMorningRegion() {
        return region1DangerLevelMorningRegion;
    }

    public void setRegion1DangerLevelMorningRegion(String region1DangerLevelMorningRegion) {
        this.region1DangerLevelMorningRegion = region1DangerLevelMorningRegion;
    }

    public int getRegion1DangerLevelMorningBorder() {
        return region1DangerLevelMorningBorder;
    }

    public void setRegion1DangerLevelMorningBorder(int region1DangerLevelMorningBorder) {
        this.region1DangerLevelMorningBorder = region1DangerLevelMorningBorder;
    }

    public int getRegion1DangerLevelMorningBorderBelow() {
        return region1DangerLevelMorningBorderBelow;
    }

    public void setRegion1DangerLevelMorningBorderBelow(int region1DangerLevelMorningBorderBelow) {
        this.region1DangerLevelMorningBorderBelow = region1DangerLevelMorningBorderBelow;
    }

    public int getRegion1DangerLevelMorningBorderAbove() {
        return region1DangerLevelMorningBorderAbove;
    }

    public void setRegion1DangerLevelMorningBorderAbove(int region1DangerLevelMorningBorderAbove) {
        this.region1DangerLevelMorningBorderAbove = region1DangerLevelMorningBorderAbove;
    }

    public String getRegion1DangerLevelAfternoonRegion() {
        return region1DangerLevelAfternoonRegion;
    }

    public void setRegion1DangerLevelAfternoonRegion(String region1DangerLevelAfternoonRegion) {
        this.region1DangerLevelAfternoonRegion = region1DangerLevelAfternoonRegion;
    }

    public int getRegion1DangerLevelAfternoonBorder() {
        return region1DangerLevelAfternoonBorder;
    }

    public void setRegion1DangerLevelAfternoonBorder(int region1DangerLevelAfternoonBorder) {
        this.region1DangerLevelAfternoonBorder = region1DangerLevelAfternoonBorder;
    }

    public int getRegion1DangerLevelAfternoonBorderBelow() {
        return region1DangerLevelAfternoonBorderBelow;
    }

    public void setRegion1DangerLevelAfternoonBorderBelow(int region1DangerLevelAfternoonBorderBelow) {
        this.region1DangerLevelAfternoonBorderBelow = region1DangerLevelAfternoonBorderBelow;
    }

    public int getRegion1DangerLevelAfternoonBorderAbove() {
        return region1DangerLevelAfternoonBorderAbove;
    }

    public void setRegion1DangerLevelAfternoonBorderAbove(int region1DangerLevelAfternoonBorderAbove) {
        this.region1DangerLevelAfternoonBorderAbove = region1DangerLevelAfternoonBorderAbove;
    }

    public String getRegion2DangerLevelMorningRegion() {
        return region2DangerLevelMorningRegion;
    }

    public void setRegion2DangerLevelMorningRegion(String region2DangerLevelMorningRegion) {
        this.region2DangerLevelMorningRegion = region2DangerLevelMorningRegion;
    }

    public int getRegion2DangerLevelMorningBorder() {
        return region2DangerLevelMorningBorder;
    }

    public void setRegion2DangerLevelMorningBorder(int region2DangerLevelMorningBorder) {
        this.region2DangerLevelMorningBorder = region2DangerLevelMorningBorder;
    }

    public int getRegion2DangerLevelMorningBorderBelow() {
        return region2DangerLevelMorningBorderBelow;
    }

    public void setRegion2DangerLevelMorningBorderBelow(int region2DangerLevelMorningBorderBelow) {
        this.region2DangerLevelMorningBorderBelow = region2DangerLevelMorningBorderBelow;
    }

    public int getRegion2DangerLevelMorningBorderAbove() {
        return region2DangerLevelMorningBorderAbove;
    }

    public void setRegion2DangerLevelMorningBorderAbove(int region2DangerLevelMorningBorderAbove) {
        this.region2DangerLevelMorningBorderAbove = region2DangerLevelMorningBorderAbove;
    }

    public String getRegion2DangerLevelAfternoonRegion() {
        return region2DangerLevelAfternoonRegion;
    }

    public void setRegion2DangerLevelAfternoonRegion(String region2DangerLevelAfternoonRegion) {
        this.region2DangerLevelAfternoonRegion = region2DangerLevelAfternoonRegion;
    }

    public int getRegion2DangerLevelAfternoonBorder() {
        return region2DangerLevelAfternoonBorder;
    }

    public void setRegion2DangerLevelAfternoonBorder(int region2DangerLevelAfternoonBorder) {
        this.region2DangerLevelAfternoonBorder = region2DangerLevelAfternoonBorder;
    }

    public int getRegion2DangerLevelAfternoonBorderBelow() {
        return region2DangerLevelAfternoonBorderBelow;
    }

    public void setRegion2DangerLevelAfternoonBorderBelow(int region2DangerLevelAfternoonBorderBelow) {
        this.region2DangerLevelAfternoonBorderBelow = region2DangerLevelAfternoonBorderBelow;
    }

    public int getRegion2DangerLevelAfternoonBorderAbove() {
        return region2DangerLevelAfternoonBorderAbove;
    }

    public void setRegion2DangerLevelAfternoonBorderAbove(int region2DangerLevelAfternoonBorderAbove) {
        this.region2DangerLevelAfternoonBorderAbove = region2DangerLevelAfternoonBorderAbove;
    }

    public String getRegion3DangerLevelMorningRegion() {
        return region3DangerLevelMorningRegion;
    }

    public void setRegion3DangerLevelMorningRegion(String region3DangerLevelMorningRegion) {
        this.region3DangerLevelMorningRegion = region3DangerLevelMorningRegion;
    }

    public int getRegion3DangerLevelMorningBorder() {
        return region3DangerLevelMorningBorder;
    }

    public void setRegion3DangerLevelMorningBorder(int region3DangerLevelMorningBorder) {
        this.region3DangerLevelMorningBorder = region3DangerLevelMorningBorder;
    }

    public int getRegion3DangerLevelMorningBorderBelow() {
        return region3DangerLevelMorningBorderBelow;
    }

    public void setRegion3DangerLevelMorningBorderBelow(int region3DangerLevelMorningBorderBelow) {
        this.region3DangerLevelMorningBorderBelow = region3DangerLevelMorningBorderBelow;
    }

    public int getRegion3DangerLevelMorningBorderAbove() {
        return region3DangerLevelMorningBorderAbove;
    }

    public void setRegion3DangerLevelMorningBorderAbove(int region3DangerLevelMorningBorderAbove) {
        this.region3DangerLevelMorningBorderAbove = region3DangerLevelMorningBorderAbove;
    }

    public String getRegion3DangerLevelAfternoonRegion() {
        return region3DangerLevelAfternoonRegion;
    }

    public void setRegion3DangerLevelAfternoonRegion(String region3DangerLevelAfternoonRegion) {
        this.region3DangerLevelAfternoonRegion = region3DangerLevelAfternoonRegion;
    }

    public int getRegion3DangerLevelAfternoonBorder() {
        return region3DangerLevelAfternoonBorder;
    }

    public void setRegion3DangerLevelAfternoonBorder(int region3DangerLevelAfternoonBorder) {
        this.region3DangerLevelAfternoonBorder = region3DangerLevelAfternoonBorder;
    }

    public int getRegion3DangerLevelAfternoonBorderBelow() {
        return region3DangerLevelAfternoonBorderBelow;
    }

    public void setRegion3DangerLevelAfternoonBorderBelow(int region3DangerLevelAfternoonBorderBelow) {
        this.region3DangerLevelAfternoonBorderBelow = region3DangerLevelAfternoonBorderBelow;
    }

    public int getRegion3DangerLevelAfternoonBorderAbove() {
        return region3DangerLevelAfternoonBorderAbove;
    }

    public void setRegion3DangerLevelAfternoonBorderAbove(int region3DangerLevelAfternoonBorderAbove) {
        this.region3DangerLevelAfternoonBorderAbove = region3DangerLevelAfternoonBorderAbove;
    }

    public String getRegion4DangerLevelMorningRegion() {
        return region4DangerLevelMorningRegion;
    }

    public void setRegion4DangerLevelMorningRegion(String region4DangerLevelMorningRegion) {
        this.region4DangerLevelMorningRegion = region4DangerLevelMorningRegion;
    }

    public int getRegion4DangerLevelMorningBorder() {
        return region4DangerLevelMorningBorder;
    }

    public void setRegion4DangerLevelMorningBorder(int region4DangerLevelMorningBorder) {
        this.region4DangerLevelMorningBorder = region4DangerLevelMorningBorder;
    }

    public int getRegion4DangerLevelMorningBorderBelow() {
        return region4DangerLevelMorningBorderBelow;
    }

    public void setRegion4DangerLevelMorningBorderBelow(int region4DangerLevelMorningBorderBelow) {
        this.region4DangerLevelMorningBorderBelow = region4DangerLevelMorningBorderBelow;
    }

    public int getRegion4DangerLevelMorningBorderAbove() {
        return region4DangerLevelMorningBorderAbove;
    }

    public void setRegion4DangerLevelMorningBorderAbove(int region4DangerLevelMorningBorderAbove) {
        this.region4DangerLevelMorningBorderAbove = region4DangerLevelMorningBorderAbove;
    }

    public String getRegion4DangerLevelAfternoonRegion() {
        return region4DangerLevelAfternoonRegion;
    }

    public void setRegion4DangerLevelAfternoonRegion(String region4DangerLevelAfternoonRegion) {
        this.region4DangerLevelAfternoonRegion = region4DangerLevelAfternoonRegion;
    }

    public int getRegion4DangerLevelAfternoonBorder() {
        return region4DangerLevelAfternoonBorder;
    }

    public void setRegion4DangerLevelAfternoonBorder(int region4DangerLevelAfternoonBorder) {
        this.region4DangerLevelAfternoonBorder = region4DangerLevelAfternoonBorder;
    }

    public int getRegion4DangerLevelAfternoonBorderBelow() {
        return region4DangerLevelAfternoonBorderBelow;
    }

    public void setRegion4DangerLevelAfternoonBorderBelow(int region4DangerLevelAfternoonBorderBelow) {
        this.region4DangerLevelAfternoonBorderBelow = region4DangerLevelAfternoonBorderBelow;
    }

    public int getRegion4DangerLevelAfternoonBorderAbove() {
        return region4DangerLevelAfternoonBorderAbove;
    }

    public void setRegion4DangerLevelAfternoonBorderAbove(int region4DangerLevelAfternoonBorderAbove) {
        this.region4DangerLevelAfternoonBorderAbove = region4DangerLevelAfternoonBorderAbove;
    }

    public String getRegion5DangerLevelMorningRegion() {
        return region5DangerLevelMorningRegion;
    }

    public void setRegion5DangerLevelMorningRegion(String region5DangerLevelMorningRegion) {
        this.region5DangerLevelMorningRegion = region5DangerLevelMorningRegion;
    }

    public int getRegion5DangerLevelMorningBorder() {
        return region5DangerLevelMorningBorder;
    }

    public void setRegion5DangerLevelMorningBorder(int region5DangerLevelMorningBorder) {
        this.region5DangerLevelMorningBorder = region5DangerLevelMorningBorder;
    }

    public int getRegion5DangerLevelMorningBorderBelow() {
        return region5DangerLevelMorningBorderBelow;
    }

    public void setRegion5DangerLevelMorningBorderBelow(int region5DangerLevelMorningBorderBelow) {
        this.region5DangerLevelMorningBorderBelow = region5DangerLevelMorningBorderBelow;
    }

    public int getRegion5DangerLevelMorningBorderAbove() {
        return region5DangerLevelMorningBorderAbove;
    }

    public void setRegion5DangerLevelMorningBorderAbove(int region5DangerLevelMorningBorderAbove) {
        this.region5DangerLevelMorningBorderAbove = region5DangerLevelMorningBorderAbove;
    }

    public String getRegion5DangerLevelAfternoonRegion() {
        return region5DangerLevelAfternoonRegion;
    }

    public void setRegion5DangerLevelAfternoonRegion(String region5DangerLevelAfternoonRegion) {
        this.region5DangerLevelAfternoonRegion = region5DangerLevelAfternoonRegion;
    }

    public int getRegion5DangerLevelAfternoonBorder() {
        return region5DangerLevelAfternoonBorder;
    }

    public void setRegion5DangerLevelAfternoonBorder(int region5DangerLevelAfternoonBorder) {
        this.region5DangerLevelAfternoonBorder = region5DangerLevelAfternoonBorder;
    }

    public int getRegion5DangerLevelAfternoonBorderBelow() {
        return region5DangerLevelAfternoonBorderBelow;
    }

    public void setRegion5DangerLevelAfternoonBorderBelow(int region5DangerLevelAfternoonBorderBelow) {
        this.region5DangerLevelAfternoonBorderBelow = region5DangerLevelAfternoonBorderBelow;
    }

    public int getRegion5DangerLevelAfternoonBorderAbove() {
        return region5DangerLevelAfternoonBorderAbove;
    }

    public void setRegion5DangerLevelAfternoonBorderAbove(int region5DangerLevelAfternoonBorderAbove) {
        this.region5DangerLevelAfternoonBorderAbove = region5DangerLevelAfternoonBorderAbove;
    }

    public String getRegion6DangerLevelMorningRegion() {
        return region6DangerLevelMorningRegion;
    }

    public void setRegion6DangerLevelMorningRegion(String region6DangerLevelMorningRegion) {
        this.region6DangerLevelMorningRegion = region6DangerLevelMorningRegion;
    }

    public int getRegion6DangerLevelMorningBorder() {
        return region6DangerLevelMorningBorder;
    }

    public void setRegion6DangerLevelMorningBorder(int region6DangerLevelMorningBorder) {
        this.region6DangerLevelMorningBorder = region6DangerLevelMorningBorder;
    }

    public int getRegion6DangerLevelMorningBorderBelow() {
        return region6DangerLevelMorningBorderBelow;
    }

    public void setRegion6DangerLevelMorningBorderBelow(int region6DangerLevelMorningBorderBelow) {
        this.region6DangerLevelMorningBorderBelow = region6DangerLevelMorningBorderBelow;
    }

    public int getRegion6DangerLevelMorningBorderAbove() {
        return region6DangerLevelMorningBorderAbove;
    }

    public void setRegion6DangerLevelMorningBorderAbove(int region6DangerLevelMorningBorderAbove) {
        this.region6DangerLevelMorningBorderAbove = region6DangerLevelMorningBorderAbove;
    }

    public String getRegion6DangerLevelAfternoonRegion() {
        return region6DangerLevelAfternoonRegion;
    }

    public void setRegion6DangerLevelAfternoonRegion(String region6DangerLevelAfternoonRegion) {
        this.region6DangerLevelAfternoonRegion = region6DangerLevelAfternoonRegion;
    }

    public int getRegion6DangerLevelAfternoonBorder() {
        return region6DangerLevelAfternoonBorder;
    }

    public void setRegion6DangerLevelAfternoonBorder(int region6DangerLevelAfternoonBorder) {
        this.region6DangerLevelAfternoonBorder = region6DangerLevelAfternoonBorder;
    }

    public int getRegion6DangerLevelAfternoonBorderBelow() {
        return region6DangerLevelAfternoonBorderBelow;
    }

    public void setRegion6DangerLevelAfternoonBorderBelow(int region6DangerLevelAfternoonBorderBelow) {
        this.region6DangerLevelAfternoonBorderBelow = region6DangerLevelAfternoonBorderBelow;
    }

    public int getRegion6DangerLevelAfternoonBorderAbove() {
        return region6DangerLevelAfternoonBorderAbove;
    }

    public void setRegion6DangerLevelAfternoonBorderAbove(int region6DangerLevelAfternoonBorderAbove) {
        this.region6DangerLevelAfternoonBorderAbove = region6DangerLevelAfternoonBorderAbove;
    }

    public String getRegion7DangerLevelMorningRegion() {
        return region7DangerLevelMorningRegion;
    }

    public void setRegion7DangerLevelMorningRegion(String region7DangerLevelMorningRegion) {
        this.region7DangerLevelMorningRegion = region7DangerLevelMorningRegion;
    }

    public int getRegion7DangerLevelMorningBorder() {
        return region7DangerLevelMorningBorder;
    }

    public void setRegion7DangerLevelMorningBorder(int region7DangerLevelMorningBorder) {
        this.region7DangerLevelMorningBorder = region7DangerLevelMorningBorder;
    }

    public int getRegion7DangerLevelMorningBorderBelow() {
        return region7DangerLevelMorningBorderBelow;
    }

    public void setRegion7DangerLevelMorningBorderBelow(int region7DangerLevelMorningBorderBelow) {
        this.region7DangerLevelMorningBorderBelow = region7DangerLevelMorningBorderBelow;
    }

    public int getRegion7DangerLevelMorningBorderAbove() {
        return region7DangerLevelMorningBorderAbove;
    }

    public void setRegion7DangerLevelMorningBorderAbove(int region7DangerLevelMorningBorderAbove) {
        this.region7DangerLevelMorningBorderAbove = region7DangerLevelMorningBorderAbove;
    }

    public String getRegion7DangerLevelAfternoonRegion() {
        return region7DangerLevelAfternoonRegion;
    }

    public void setRegion7DangerLevelAfternoonRegion(String region7DangerLevelAfternoonRegion) {
        this.region7DangerLevelAfternoonRegion = region7DangerLevelAfternoonRegion;
    }

    public int getRegion7DangerLevelAfternoonBorder() {
        return region7DangerLevelAfternoonBorder;
    }

    public void setRegion7DangerLevelAfternoonBorder(int region7DangerLevelAfternoonBorder) {
        this.region7DangerLevelAfternoonBorder = region7DangerLevelAfternoonBorder;
    }

    public int getRegion7DangerLevelAfternoonBorderBelow() {
        return region7DangerLevelAfternoonBorderBelow;
    }

    public void setRegion7DangerLevelAfternoonBorderBelow(int region7DangerLevelAfternoonBorderBelow) {
        this.region7DangerLevelAfternoonBorderBelow = region7DangerLevelAfternoonBorderBelow;
    }

    public int getRegion7DangerLevelAfternoonBorderAbove() {
        return region7DangerLevelAfternoonBorderAbove;
    }

    public void setRegion7DangerLevelAfternoonBorderAbove(int region7DangerLevelAfternoonBorderAbove) {
        this.region7DangerLevelAfternoonBorderAbove = region7DangerLevelAfternoonBorderAbove;
    }

    public String getRegion8DangerLevelMorningRegion() {
        return region8DangerLevelMorningRegion;
    }

    public void setRegion8DangerLevelMorningRegion(String region8DangerLevelMorningRegion) {
        this.region8DangerLevelMorningRegion = region8DangerLevelMorningRegion;
    }

    public int getRegion8DangerLevelMorningBorder() {
        return region8DangerLevelMorningBorder;
    }

    public void setRegion8DangerLevelMorningBorder(int region8DangerLevelMorningBorder) {
        this.region8DangerLevelMorningBorder = region8DangerLevelMorningBorder;
    }

    public int getRegion8DangerLevelMorningBorderBelow() {
        return region8DangerLevelMorningBorderBelow;
    }

    public void setRegion8DangerLevelMorningBorderBelow(int region8DangerLevelMorningBorderBelow) {
        this.region8DangerLevelMorningBorderBelow = region8DangerLevelMorningBorderBelow;
    }

    public int getRegion8DangerLevelMorningBorderAbove() {
        return region8DangerLevelMorningBorderAbove;
    }

    public void setRegion8DangerLevelMorningBorderAbove(int region8DangerLevelMorningBorderAbove) {
        this.region8DangerLevelMorningBorderAbove = region8DangerLevelMorningBorderAbove;
    }

    public String getRegion8DangerLevelAfternoonRegion() {
        return region8DangerLevelAfternoonRegion;
    }

    public void setRegion8DangerLevelAfternoonRegion(String region8DangerLevelAfternoonRegion) {
        this.region8DangerLevelAfternoonRegion = region8DangerLevelAfternoonRegion;
    }

    public int getRegion8DangerLevelAfternoonBorder() {
        return region8DangerLevelAfternoonBorder;
    }

    public void setRegion8DangerLevelAfternoonBorder(int region8DangerLevelAfternoonBorder) {
        this.region8DangerLevelAfternoonBorder = region8DangerLevelAfternoonBorder;
    }

    public int getRegion8DangerLevelAfternoonBorderBelow() {
        return region8DangerLevelAfternoonBorderBelow;
    }

    public void setRegion8DangerLevelAfternoonBorderBelow(int region8DangerLevelAfternoonBorderBelow) {
        this.region8DangerLevelAfternoonBorderBelow = region8DangerLevelAfternoonBorderBelow;
    }

    public int getRegion8DangerLevelAfternoonBorderAbove() {
        return region8DangerLevelAfternoonBorderAbove;
    }

    public void setRegion8DangerLevelAfternoonBorderAbove(int region8DangerLevelAfternoonBorderAbove) {
        this.region8DangerLevelAfternoonBorderAbove = region8DangerLevelAfternoonBorderAbove;
    }

    public String getRegion9DangerLevelMorningRegion() {
        return region9DangerLevelMorningRegion;
    }

    public void setRegion9DangerLevelMorningRegion(String region9DangerLevelMorningRegion) {
        this.region9DangerLevelMorningRegion = region9DangerLevelMorningRegion;
    }

    public int getRegion9DangerLevelMorningBorder() {
        return region9DangerLevelMorningBorder;
    }

    public void setRegion9DangerLevelMorningBorder(int region9DangerLevelMorningBorder) {
        this.region9DangerLevelMorningBorder = region9DangerLevelMorningBorder;
    }

    public int getRegion9DangerLevelMorningBorderBelow() {
        return region9DangerLevelMorningBorderBelow;
    }

    public void setRegion9DangerLevelMorningBorderBelow(int region9DangerLevelMorningBorderBelow) {
        this.region9DangerLevelMorningBorderBelow = region9DangerLevelMorningBorderBelow;
    }

    public int getRegion9DangerLevelMorningBorderAbove() {
        return region9DangerLevelMorningBorderAbove;
    }

    public void setRegion9DangerLevelMorningBorderAbove(int region9DangerLevelMorningBorderAbove) {
        this.region9DangerLevelMorningBorderAbove = region9DangerLevelMorningBorderAbove;
    }

    public String getRegion9DangerLevelAfternoonRegion() {
        return region9DangerLevelAfternoonRegion;
    }

    public void setRegion9DangerLevelAfternoonRegion(String region9DangerLevelAfternoonRegion) {
        this.region9DangerLevelAfternoonRegion = region9DangerLevelAfternoonRegion;
    }

    public int getRegion9DangerLevelAfternoonBorder() {
        return region9DangerLevelAfternoonBorder;
    }

    public void setRegion9DangerLevelAfternoonBorder(int region9DangerLevelAfternoonBorder) {
        this.region9DangerLevelAfternoonBorder = region9DangerLevelAfternoonBorder;
    }

    public int getRegion9DangerLevelAfternoonBorderBelow() {
        return region9DangerLevelAfternoonBorderBelow;
    }

    public void setRegion9DangerLevelAfternoonBorderBelow(int region9DangerLevelAfternoonBorderBelow) {
        this.region9DangerLevelAfternoonBorderBelow = region9DangerLevelAfternoonBorderBelow;
    }

    public int getRegion9DangerLevelAfternoonBorderAbove() {
        return region9DangerLevelAfternoonBorderAbove;
    }

    public void setRegion9DangerLevelAfternoonBorderAbove(int region9DangerLevelAfternoonBorderAbove) {
        this.region9DangerLevelAfternoonBorderAbove = region9DangerLevelAfternoonBorderAbove;
    }

    public String getRegion10DangerLevelMorningRegion() {
        return region10DangerLevelMorningRegion;
    }

    public void setRegion10DangerLevelMorningRegion(String region10DangerLevelMorningRegion) {
        this.region10DangerLevelMorningRegion = region10DangerLevelMorningRegion;
    }

    public int getRegion10DangerLevelMorningBorder() {
        return region10DangerLevelMorningBorder;
    }

    public void setRegion10DangerLevelMorningBorder(int region10DangerLevelMorningBorder) {
        this.region10DangerLevelMorningBorder = region10DangerLevelMorningBorder;
    }

    public int getRegion10DangerLevelMorningBorderBelow() {
        return region10DangerLevelMorningBorderBelow;
    }

    public void setRegion10DangerLevelMorningBorderBelow(int region10DangerLevelMorningBorderBelow) {
        this.region10DangerLevelMorningBorderBelow = region10DangerLevelMorningBorderBelow;
    }

    public int getRegion10DangerLevelMorningBorderAbove() {
        return region10DangerLevelMorningBorderAbove;
    }

    public void setRegion10DangerLevelMorningBorderAbove(int region10DangerLevelMorningBorderAbove) {
        this.region10DangerLevelMorningBorderAbove = region10DangerLevelMorningBorderAbove;
    }

    public String getRegion10DangerLevelAfternoonRegion() {
        return region10DangerLevelAfternoonRegion;
    }

    public void setRegion10DangerLevelAfternoonRegion(String region10DangerLevelAfternoonRegion) {
        this.region10DangerLevelAfternoonRegion = region10DangerLevelAfternoonRegion;
    }

    public int getRegion10DangerLevelAfternoonBorder() {
        return region10DangerLevelAfternoonBorder;
    }

    public void setRegion10DangerLevelAfternoonBorder(int region10DangerLevelAfternoonBorder) {
        this.region10DangerLevelAfternoonBorder = region10DangerLevelAfternoonBorder;
    }

    public int getRegion10DangerLevelAfternoonBorderBelow() {
        return region10DangerLevelAfternoonBorderBelow;
    }

    public void setRegion10DangerLevelAfternoonBorderBelow(int region10DangerLevelAfternoonBorderBelow) {
        this.region10DangerLevelAfternoonBorderBelow = region10DangerLevelAfternoonBorderBelow;
    }

    public int getRegion10DangerLevelAfternoonBorderAbove() {
        return region10DangerLevelAfternoonBorderAbove;
    }

    public void setRegion10DangerLevelAfternoonBorderAbove(int region10DangerLevelAfternoonBorderAbove) {
        this.region10DangerLevelAfternoonBorderAbove = region10DangerLevelAfternoonBorderAbove;
    }

    public String getRegion11DangerLevelMorningRegion() {
        return region11DangerLevelMorningRegion;
    }

    public void setRegion11DangerLevelMorningRegion(String region11DangerLevelMorningRegion) {
        this.region11DangerLevelMorningRegion = region11DangerLevelMorningRegion;
    }

    public int getRegion11DangerLevelMorningBorder() {
        return region11DangerLevelMorningBorder;
    }

    public void setRegion11DangerLevelMorningBorder(int region11DangerLevelMorningBorder) {
        this.region11DangerLevelMorningBorder = region11DangerLevelMorningBorder;
    }

    public int getRegion11DangerLevelMorningBorderBelow() {
        return region11DangerLevelMorningBorderBelow;
    }

    public void setRegion11DangerLevelMorningBorderBelow(int region11DangerLevelMorningBorderBelow) {
        this.region11DangerLevelMorningBorderBelow = region11DangerLevelMorningBorderBelow;
    }

    public int getRegion11DangerLevelMorningBorderAbove() {
        return region11DangerLevelMorningBorderAbove;
    }

    public void setRegion11DangerLevelMorningBorderAbove(int region11DangerLevelMorningBorderAbove) {
        this.region11DangerLevelMorningBorderAbove = region11DangerLevelMorningBorderAbove;
    }

    public String getRegion11DangerLevelAfternoonRegion() {
        return region11DangerLevelAfternoonRegion;
    }

    public void setRegion11DangerLevelAfternoonRegion(String region11DangerLevelAfternoonRegion) {
        this.region11DangerLevelAfternoonRegion = region11DangerLevelAfternoonRegion;
    }

    public int getRegion11DangerLevelAfternoonBorder() {
        return region11DangerLevelAfternoonBorder;
    }

    public void setRegion11DangerLevelAfternoonBorder(int region11DangerLevelAfternoonBorder) {
        this.region11DangerLevelAfternoonBorder = region11DangerLevelAfternoonBorder;
    }

    public int getRegion11DangerLevelAfternoonBorderBelow() {
        return region11DangerLevelAfternoonBorderBelow;
    }

    public void setRegion11DangerLevelAfternoonBorderBelow(int region11DangerLevelAfternoonBorderBelow) {
        this.region11DangerLevelAfternoonBorderBelow = region11DangerLevelAfternoonBorderBelow;
    }

    public int getRegion11DangerLevelAfternoonBorderAbove() {
        return region11DangerLevelAfternoonBorderAbove;
    }

    public void setRegion11DangerLevelAfternoonBorderAbove(int region11DangerLevelAfternoonBorderAbove) {
        this.region11DangerLevelAfternoonBorderAbove = region11DangerLevelAfternoonBorderAbove;
    }

    public String getRegion12DangerLevelMorningRegion() {
        return region12DangerLevelMorningRegion;
    }

    public void setRegion12DangerLevelMorningRegion(String region12DangerLevelMorningRegion) {
        this.region12DangerLevelMorningRegion = region12DangerLevelMorningRegion;
    }

    public int getRegion12DangerLevelMorningBorder() {
        return region12DangerLevelMorningBorder;
    }

    public void setRegion12DangerLevelMorningBorder(int region12DangerLevelMorningBorder) {
        this.region12DangerLevelMorningBorder = region12DangerLevelMorningBorder;
    }

    public int getRegion12DangerLevelMorningBorderBelow() {
        return region12DangerLevelMorningBorderBelow;
    }

    public void setRegion12DangerLevelMorningBorderBelow(int region12DangerLevelMorningBorderBelow) {
        this.region12DangerLevelMorningBorderBelow = region12DangerLevelMorningBorderBelow;
    }

    public int getRegion12DangerLevelMorningBorderAbove() {
        return region12DangerLevelMorningBorderAbove;
    }

    public void setRegion12DangerLevelMorningBorderAbove(int region12DangerLevelMorningBorderAbove) {
        this.region12DangerLevelMorningBorderAbove = region12DangerLevelMorningBorderAbove;
    }

    public String getRegion12DangerLevelAfternoonRegion() {
        return region12DangerLevelAfternoonRegion;
    }

    public void setRegion12DangerLevelAfternoonRegion(String region12DangerLevelAfternoonRegion) {
        this.region12DangerLevelAfternoonRegion = region12DangerLevelAfternoonRegion;
    }

    public int getRegion12DangerLevelAfternoonBorder() {
        return region12DangerLevelAfternoonBorder;
    }

    public void setRegion12DangerLevelAfternoonBorder(int region12DangerLevelAfternoonBorder) {
        this.region12DangerLevelAfternoonBorder = region12DangerLevelAfternoonBorder;
    }

    public int getRegion12DangerLevelAfternoonBorderBelow() {
        return region12DangerLevelAfternoonBorderBelow;
    }

    public void setRegion12DangerLevelAfternoonBorderBelow(int region12DangerLevelAfternoonBorderBelow) {
        this.region12DangerLevelAfternoonBorderBelow = region12DangerLevelAfternoonBorderBelow;
    }

    public int getRegion12DangerLevelAfternoonBorderAbove() {
        return region12DangerLevelAfternoonBorderAbove;
    }

    public void setRegion12DangerLevelAfternoonBorderAbove(int region12DangerLevelAfternoonBorderAbove) {
        this.region12DangerLevelAfternoonBorderAbove = region12DangerLevelAfternoonBorderAbove;
    }

    public ArrayList<String> getDangerPattern() {
        return dangerPattern;
    }

    public void setDangerPattern(ArrayList<String> dangerPattern) {
        this.dangerPattern = dangerPattern;
    }

    public String getZamgWeatherStation() {
        return zamgWeatherStation;
    }

    public void setZamgWeatherStation(String zamgWeatherStation) {
        this.zamgWeatherStation = zamgWeatherStation;
    }

    public String getZamgWeatherStationReport() {
        return zamgWeatherStationReport;
    }

    public void setZamgWeatherStationReport(String zamgWeatherStationReport) {
        this.zamgWeatherStationReport = zamgWeatherStationReport;
    }

    public String getSnowpackStructure() {
        return snowpackStructure;
    }

    public void setSnowpackStructure(String snowpackStructure) {
        this.snowpackStructure = snowpackStructure;
    }

    public String getSnowpackStructureDescription() {
        return snowpackStructureDescription;
    }

    public void setSnowpackStructureDescription(String snowpackStructureDescription) {
        this.snowpackStructureDescription = snowpackStructureDescription;
    }

    public String getAvalancheDangerAssessment() {
        return avalancheDangerAssessment;
    }

    public void setAvalancheDangerAssessment(String avalancheDangerAssessment) {
        this.avalancheDangerAssessment = avalancheDangerAssessment;
    }

    public String getAvalancheDangerAssessmentDescription() {
        return avalancheDangerAssessmentDescription;
    }

    public void setAvalancheDangerAssessmentDescription(String avalancheDangerAssessmentDescription) {
        this.avalancheDangerAssessmentDescription = avalancheDangerAssessmentDescription;
    }

    public ArrayList<AvalancheProblemTyrol> getAvalancheProblemsTyrols() {
        return avalancheProblemTyrol;
    }

    public void setAvalancheProblemsTyrols(ArrayList<AvalancheProblemTyrol> avalancheProblemsTyrols) {
        this.avalancheProblemTyrol = avalancheProblemsTyrols;
    }

    public float getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(float windDirection) {
        this.windDirection = windDirection;
    }
}
