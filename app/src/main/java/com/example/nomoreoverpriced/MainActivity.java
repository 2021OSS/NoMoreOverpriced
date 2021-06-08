package com.example.nomoreoverpriced;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.sql.DriverManager.println;


public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;
    SQLiteDatabase database;

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
        mMap.setMinZoomPreference(8.0f);
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
                    .title(storeName);

            mMap.addMarker(markerOptions);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(33.2238, 126.3350)));
        cursor.close();

    }


}
