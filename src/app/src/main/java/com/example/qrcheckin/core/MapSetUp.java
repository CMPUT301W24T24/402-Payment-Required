package com.example.qrcheckin.core;

import android.content.Context;
import android.location.GpsStatus;

import com.example.qrcheckin.MainActivity;
import com.example.qrcheckin.R;
import com.google.android.gms.maps.SupportMapFragment;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapSetUp {
    private Context context;//create the map
    private MapView mapView;
    private IMapController iMapController;
    private MyLocationNewOverlay mMyLocationOverlay;

    /**
     *
     * @param context
     * @param mapView
     */
    public MapSetUp(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;
        this.iMapController = mapView.getController();

        // Initialize osmdroid configuration
        Configuration.getInstance().load(
                context,
                context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
        );

        // Set up the map
        initializeMap();
        addLocationOverlay();
        mapView.setMapListener((MapListener) this);
    }

    public MapSetUp(MainActivity context, com.google.android.gms.maps.MapView mapView) {
    }

    /**
     *
     */
    private void addLocationOverlay() {
        // Initialize MyLocationOverlay for displaying user's location
        mMyLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(mMyLocationOverlay);
    }

    /**
     *
     */
    private void initializeMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
    }

    /**
     *
     */
    public void onResume() {
        mapView.onResume();
    }

    /**
     *
     */
    public void onPause() {
        mapView.onPause();
    }
}
