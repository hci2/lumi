package at.ac.univie.lumi.controller;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.json.JSONObject;

import at.ac.univie.lumi.R;
import at.ac.univie.lumi.view.MapActivity;

import static at.ac.univie.lumi.view.MapActivity.getMap;

/**
 * Created by phili on 6/13/2017.
 *
 * This class is responsible for saving the current map view offline for later offline usage.
 */

public class OfflineMapManager {

    private MapActivity mapActivity;
    private String choosenScenarioStyle;


    //offline saving of map
    private static final String TAG = "OFFLINEMAPDOWNLOAD";
    private boolean isEndNotified;
    private ProgressDialog progressDialog;
    private OfflineManager offlineManager;
    private static OfflineRegion offlineRegion;
    // JSON encoding/decoding
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";

    /**
     * The constructor set the activity.
     * @param mapActivity The invoking activity.
     */

    public OfflineMapManager(MapActivity mapActivity, String choosenScenarioStyle){//, ProgressDialog progressDialog){
        this.choosenScenarioStyle = choosenScenarioStyle;
        this.mapActivity = mapActivity;
    }

    /**
     * This method is used to download the current view of the map offline.
     */

    public void downloadMapOffline() {
        // Set up the OfflineManager
        offlineManager = OfflineManager.getInstance(mapActivity);

        deleteOfflineRegions();

        // Create a bounding box for the offline region

        //current view of map

        // Define the current offline region of the current layer
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                getMap().getStyleUrl(),
                getMap().getProjection().getVisibleRegion().latLngBounds, //latLngBoundsOffline
                getMap().getCameraPosition().zoom,
                getMap().getMaxZoomLevel(),
                mapActivity.getResources().getDisplayMetrics().density);

        // Set the metadata
        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_FIELD_REGION_NAME, "Tux alps");
            String json = jsonObject.toString();
            metadata = json.getBytes(JSON_CHARSET);
        } catch (Exception exception) {
            Log.e(TAG, "Failed to encode metadata: " + exception.getMessage());
            metadata = null;
        }

        //create current viewed map as offline map
        createOfflineRegion(definition,metadata);

        /* If we are interested to creat with one click offline maps of all layers

        String styleUrl= mapActivity.getResources().getString(R.string.style_mapbox_slopeareas);
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        double minZoom = map.getCameraPosition().zoom;
        double maxZoom = map.getMaxZoomLevel();
        float pixelRatio = this.getResources().getDisplayMetrics().density;

        // Define the offline region
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                styleUrl, bounds, minZoom, maxZoom, pixelRatio);

        //create current viewed map as offline map
        createOfflineRegion(definition,metadata);

        styleUrl= choosenScenarioStyle;
        //for danger zones layer
        definition = new OfflineTilePyramidRegionDefinition(
                styleUrl, bounds, minZoom, maxZoom, pixelRatio);

        //create default map offline
        createOfflineRegion(definition,metadata);


        styleUrl= mapActivity.getResources().getString(R.string.style_mapbox_slopeangle_general);
        //for slope angle layer
        definition = new OfflineTilePyramidRegionDefinition(
                styleUrl, bounds, minZoom, maxZoom, pixelRatio);
        //create default map offline
        createOfflineRegion(definition,metadata);

        styleUrl= mapActivity.getResources().getString(R.string.style_mapbox_exposition_perslopearea);
        //for exposition layer
        definition = new OfflineTilePyramidRegionDefinition(
                styleUrl, bounds, minZoom, maxZoom, pixelRatio);
        //create default map offline
        createOfflineRegion(definition,metadata);

        styleUrl= mapActivity.getResources().getString(R.string.style_mapbox_gullies_bowls);
        //for gullies and bowls layer
        definition = new OfflineTilePyramidRegionDefinition(
                styleUrl, bounds, minZoom, maxZoom, pixelRatio);
        //create default map offline
        createOfflineRegion(definition,metadata);

        styleUrl= mapActivity.getResources().getString(R.string.style_mapbox_bulge);
        //for bulge layer
        definition = new OfflineTilePyramidRegionDefinition(
                styleUrl, bounds, minZoom, maxZoom, pixelRatio);
        //create default map offline
        createOfflineRegion(definition,metadata);

        */

    }

    /**
     * This method saves a specific part of the map offline.
     * @param definition Contains the data of the interested map view.
     * @param metadata The metadata of the offline map.
     */

    private void createOfflineRegion(OfflineTilePyramidRegionDefinition definition, byte[] metadata) {

        // Create the region asynchronously
        offlineManager.createOfflineRegion(
                definition,
                metadata,
                new OfflineManager.CreateOfflineRegionCallback() {
                    @Override
                    public void onCreate(OfflineRegion offlineRegion1) {
                        OfflineMapManager.offlineRegion= offlineRegion1;
                        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

                        // Display the download progress bar
                        mapActivity.showProgressDialog();

                        // Monitor the download progress using setObserver
                        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                            @Override
                            public void onStatusChanged(OfflineRegionStatus status) {

                                // Calculate the download percentage and update the progress bar
                                double percentage = status.getRequiredResourceCount() >= 0
                                        ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                        0.0;

                                if (status.isComplete()) {
                                    // Download complete
                                    endProgress("Karte erfolreich heruntergeladen.");
                                } else if (status.isRequiredResourceCountPrecise()) {
                                    // Switch to determinate state
                                    setPercentage((int) Math.round(percentage));
                                }
                            }

                            @Override
                            public void onError(OfflineRegionError error) {
                                // If an error occurs, print to logcat
                                Log.e(TAG, "onError reason: " + error.getReason());
                                Log.e(TAG, "onError message: " + error.getMessage());
                            }

                            @Override
                            public void mapboxTileCountLimitExceeded(long limit) {
                                // Notify if offline region exceeds maximum tile count
                                Log.e(TAG, "Mapbox tile count limit exceeded: " + limit);
                            }

                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error: " + error);
                    }
                });
    }

    /**
     * This method removes the offline saved map.
     */

    private void deleteOfflineRegions() {

        if(offlineManager!=null){
            offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
                @Override
                public void onList(OfflineRegion[] offlineRegions) {
                    if (offlineRegions.length > 0) {
                        Log.e(TAG, "Size: " + offlineRegions.length);
                        // delete the all items in the offlineRegions list which will be offline map
                        /*for(int i=0; i<offlineRegions.length;i++){
                            offlineRegions[i].delete(new OfflineRegion.OfflineRegionDeleteCallback() {
                                @Override
                                public void onDelete() {
                                    Log.i(TAG, "Map deleted successfully");
                                }

                                @Override
                                public void onError(String error) {
                                    Log.e(TAG, "On Delete error: " + error);
                                }
                            });
                        }*/
                        // delete the last item in the offlineRegions list which will be offline map
                        offlineRegions[offlineRegions.length-1].delete(new OfflineRegion.OfflineRegionDeleteCallback() {
                            @Override
                            public void onDelete() {
                                Log.i(TAG, "Map deleted successfully");
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "On Delete error: " + error);
                            }
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "onListError: " + error);
                }
            });
        }

    }

    /**
     * This method set current percentage of the progress in the progress bar of the map acitivty.
     * @param percentage The amount of the progress.
     */

    private void setPercentage(final int percentage) {
        progressDialog.setIndeterminate(false);
        progressDialog.setProgress(percentage);
        //progressDialog.setMessage(String.valueOf(percentage)+" %");
    }

    /**
     * This method is invoked at the end of the progress and disable the progress dialog.
     * @param message The message which is shown to the user.
     */

    private void endProgress(final String message) {
        // Don't notify more than once
        if (isEndNotified) {
            return;
        }

        // Stop and hide the progress bar
        isEndNotified = true;
        if (progressDialog.isShowing() && progressDialog!=null){
            progressDialog.setIndeterminate(false);
            progressDialog.dismiss();
        }
        // Show a toast
        Toast.makeText(mapActivity, message, Toast.LENGTH_LONG).show();
    }

    /**
     * This method set the private var.
     * @param isEndNotified The value which is set.
     */

    public void setEndNotified(boolean isEndNotified){
        this.isEndNotified = isEndNotified;
    }

    /**
     * This method set the progress dialog variable.
     * @param pd The progress dialog which is set.
     */

    public void setProgressDialog(ProgressDialog pd){
        progressDialog=pd;
    }

    /**
     * This method returns the offlineregion object.
     * @return The offlineregion object.
     */

    public OfflineRegion getOfflineRegion(){
        return offlineRegion;
    }

}
