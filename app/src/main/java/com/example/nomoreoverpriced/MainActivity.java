package com.example.nomoreoverpriced;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.view.LayoutInflater;
import android.location.Address;
import android.location.Geocoder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import java.lang.Object;
import java.lang.String;
import java.lang.Throwable;
import static java.sql.DriverManager.println;


public class MainActivity extends AppCompatActivity
        implements
            OnMapReadyCallback,
            GoogleMap.OnInfoWindowClickListener
        {
    private GoogleMap mMap;
    SQLiteDatabase database;

    /*
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent indent = new Intent(this, LoadingActivity.class);
        startActivity(indent);
    }
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        try{
            boolean bResult = isCheckDB();
            Log.d("MiniApp", "DB Check=" + bResult);
            if(!bResult){
                copyDB(this);
            }
            else{}
        } catch(Exception e){}
        String databaseName = "good_shop.db";
        createDatabase(databaseName);
        //executeQuery();
        Intent indent = new Intent(this, LoadingActivity.class);
        startActivity(indent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public boolean isCheckDB(){
        String filePath = "/data/data/com.example.nomoreoverpriced/databases/good_shop.db";
        File file = new File(filePath);
        if (file.exists()) {
            Log.d("데이터베이스 존재",String.format("true"));
            return true;
        }
        Log.d("데이터베이스 존재",String.format("false"));
        return false;
    }
    public void copyDB(Context mContext){
        Log.d("MiniApp", "copyDB");
        AssetManager manager = mContext.getAssets();
        String folderPath = "/data/data/com.example.nomoreoverpriced/databases";
        String filePath = "/data/data/com.example.nomoreoverpriced/databases/good_shop.db";
        File folder = new File(folderPath);
        File file = new File(filePath);

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            Log.d("db",String.format("복사시작"));
            InputStream is = manager.open("db/good_shop.db");
            BufferedInputStream bis = new BufferedInputStream(is);

            if (folder.exists()) {
                Log.d("폴더 존재",String.format("true"));
            }else{
                Log.d("폴더 존재",String.format("false, 새로만듬"));
                folder.mkdirs();
            }

            if (file.exists()) {
                Log.d("폴더",String.format("재생성"));
                file.delete();
                file.createNewFile();
            }
            Log.d("파일",String.format("만들기"));
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            int read = -1;
            byte[] buffer = new byte[1024];
            while ((read = bis.read(buffer, 0, 1024)) != -1) {
                bos.write(buffer, 0, read);
            }

            bos.flush();

            bos.close();
            fos.close();
            bis.close();
            is.close();

        } catch (IOException e) {
            Log.d("에러발생",String.format("true"));
            Log.e("ErrorMessage : ", e.getMessage());
        }
    }
    private void createDatabase(String name){
        database = openOrCreateDatabase(name, MODE_PRIVATE, null);
    }
    public void executeQuery(){
        Cursor cursor = database.rawQuery("select 업소분류, 업소명, 연락처, 지역, address, Latitude, Longitude, 대표품목가격, 영업상세, 휴무일, 데이터기준일자  from good_shop", null);
        int recordCount = cursor.getCount();
        for(int i = 0; i < recordCount; i++){
            cursor.moveToNext();
            String store = cursor.getString(0);
            String storeName = cursor.getString(1);
            String tel = cursor.getString(2);
            String Nation = cursor.getString(3);
            String address = cursor.getString(4);
            double latitude = cursor.getDouble(5);
            double longitude = cursor.getDouble(6);
            String Price = cursor.getString(7);
            String day = cursor.getString(8);
            String rest_day = cursor.getString(9);

        }
        cursor.close();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
/*
        mMap = googleMap;
        for(int idx = 0; idx < 10; idx++) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions
                    .position(new LatLng(37.52487 + idx, 126.92723))
                    .title("마커" + idx);

            mMap.addMarker(markerOptions);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.52487, 126.92723)));
*/
        mMap = googleMap;
        mMap.setMinZoomPreference(9.0f);
        Cursor cursor = database.rawQuery("select 업소분류, 업소명, 연락처, 지역, address, Latitude, Longitude, 대표품목가격, 영업상세, 휴무일, 데이터기준일자  from good_shop", null);
        int recordCount = cursor.getCount();

        for(int i = 0; i < recordCount; i++){
            cursor.moveToNext();
            String store = cursor.getString(0);
            String storeName = cursor.getString(1);
            String tel = cursor.getString(2);
            String Nation = cursor.getString(3);
            String address = cursor.getString(4);
            double latitude = cursor.getDouble(5);
            double longitude = cursor.getDouble(6);
            String Price = cursor.getString(7);
            String day = cursor.getString(8);
            String rest_day = cursor.getString(9);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions
                    .position(new LatLng(latitude, longitude))
                    .title(storeName + "\n" + address)
                    .snippet("대표품목 : " +  Price);

            mMap.addMarker(markerOptions);
            mMap.setInfoWindowAdapter(new CustomInfoWindow(getLayoutInflater()));
            mMap.setOnInfoWindowClickListener(this);
        }


        // 마커 클릭에 대한 이벤트 처리
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(33.3838, 126.5550)));
        cursor.close();

    }


    // onMarkerClick 구현
    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, marker.getTitle() + "\n" + marker.getPosition(), Toast.LENGTH_SHORT).show();
    }


}
