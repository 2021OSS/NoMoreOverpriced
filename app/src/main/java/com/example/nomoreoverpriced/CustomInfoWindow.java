package com.example.nomoreoverpriced;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {
    private View infoWindow = null;
    private LayoutInflater inflater = null;

    public CustomInfoWindow(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @SuppressLint("InflateParams")

    @Override
    public View getInfoContents(Marker marker) {
        if (infoWindow == null) {
            infoWindow = inflater.inflate(R.layout.activity_custom_info_window, null);
        }

        TextView title = (TextView)infoWindow.findViewById(R.id.txtPickupInfo);
        TextView snippet = (TextView)infoWindow.findViewById(R.id.txtPickupSnippet);
        title.setText(marker.getTitle());
        snippet.setText(marker.getSnippet());

        return (infoWindow);
    }
}
