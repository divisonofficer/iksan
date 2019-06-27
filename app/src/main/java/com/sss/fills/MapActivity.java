package com.sss.fills;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.*;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


class Marker{
    private double Lon,Len;
    public Bitmap photo;
    public int photo_cnt;
    public Boolean image_exists;
    private String Name,Index,Memo;
    Marker(double Lon, double Len, String name,String index)
    {
        this.Lon=Lon;
        this.Len=Len;
        this.Name=name;
        this.Index=index;
        photo_cnt=0;
        Memo="메모를 입력하세요";
        image_exists=false;
    }
    public LatLng returnLocation()
    {

        return new LatLng(Len,Lon) ;
    }
    public String getName()
    {
        return Name;
    }
    public String getIndex()
    {
        return Index;
    }
    public String getMemo()
    {
        return Memo;
    }
    public void setMemo(Editable a)
    {
        Memo=a.toString();
    }
}
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener{
    public int Cur_Spot=-1;
    String[] permission_list = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE


    };
    MapFragment mapFragment;
    private static int  PICK_IMAGE_REQUEST=1;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        checkPermission();
        Log.e("sss", "잘돔1");


        FragmentManager fragmentManager = getFragmentManager();
        mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
        marker=new Marker[Max_Spot];
        marker[0] = new Marker(126.94397,35.94884,"익산1","이곳은 익산의 어딘가이다.");
        marker[1] = new Marker(126.97213,35.98111,"익산2","이곳은 익산의 어딘가이다.");
        marker[2] = new Marker(126.91209,35.98219,"익산3","이곳은 익산의 어딘가이다.");
        marker[3] = new Marker(126.89054,35.92987,"익산4","이곳은 익산의 어딘가이다.");
        marker[4] = new Marker(126.89054,35.89987,"익산5","이곳은 익산의 어딘가이다.");

        ConstraintLayout Lay = (ConstraintLayout)findViewById(R.id.SpotOption);
        Lay.setVisibility(View.GONE);
        Button ButtonAddPicture = (Button)findViewById(R.id.button_add_picture);
        ButtonAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //ACTION_PIC과 차이점?
                    intent.setType("image/*"); //이미지만 보이게
                    //Intent 시작 - 갤러리앱을 열어서 원하는 이미지를 선택할 수 있다.
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

         }
        });

    }
    public final int Max_Spot=5;
    Marker []marker;
    MarkerOptions[] markerOptions;
    public void onMapReady(final GoogleMap map) {

        markerOptions=new MarkerOptions[Max_Spot];
        for(int i=0;i<Max_Spot;i++) {
            markerOptions[i] = new MarkerOptions();
            markerOptions[i].position(marker[i].returnLocation());
            markerOptions[i].title(marker[i].getName());
            markerOptions[i].snippet(marker[i].getIndex());

            map.addMarker(markerOptions[i]);

        }

        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(35.94884,126.94397)));
        map.animateCamera(CameraUpdateFactory.zoomTo(10));
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //이미지를 하나 골랐을때
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
                //data에서 절대경로로 이미지를 가져옴
                Uri uri = data.getData();

                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);

                //이미지가 한계이상(?) 크면 불러 오지 못하므로 사이즈를 줄여 준다.

                String imagePath = getRealPathFromURI(uri);//assign it to a string(your choice).

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);


                ExifInterface  exif = new ExifInterface(imagePath);
                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int exifDegree = exifOrientationToDegrees(exifOrientation);
                scaled=rotate(scaled,exifDegree);
                scaled=createSquaredBitmap(scaled);
                  marker[Cur_Spot].photo=scaled;
                  marker[Cur_Spot].image_exists=true;
                Paint paint= new Paint();
                Bitmap sccaled=scaled.copy(scaled.getConfig(),true);
                Canvas tempcanvas= new Canvas(sccaled);
                Bitmap Nshape=((BitmapDrawable)getResources().getDrawable(R.drawable.shashashape)).getBitmap();
                Nshape=Bitmap.createScaledBitmap(Nshape,scaled.getWidth()/2,scaled.getHeight()/2,true);
                tempcanvas.drawBitmap(scaled,0,0,paint);
                PorterDuff.Mode mode=Mode.XOR;
                paint.setXfermode(new PorterDuffXfermode(mode));
                tempcanvas.drawBitmap(Nshape,0,0,paint);
                Bitmap sscaled = Bitmap.createScaledBitmap(sccaled, 128, 128, true);
                tempMarker.setIcon(BitmapDescriptorFactory.fromBitmap(sscaled));
                  refreshPhoto();


            } else {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Oops! 로딩에 오류가 있습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
    private static Bitmap createSquaredBitmap(Bitmap srcBmp) {
        int dim = Math.min(srcBmp.getWidth(), srcBmp.getHeight());
        Bitmap dstBmp = Bitmap.createBitmap(dim, dim, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(dstBmp);
        //canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(srcBmp, (dim - srcBmp.getWidth()) / 2, (dim - srcBmp.getHeight()) / 2, null);

        return dstBmp;
    }
    private String getRealPathFromURI(Uri contentUri) { if (contentUri.getPath().startsWith("/storage")) { return contentUri.getPath(); }
        String id = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        }
        String[] columns = { MediaStore.Files.FileColumns.DATA }; String selection = MediaStore.Files.FileColumns._ID + " = " + id; Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null); try { int columnIndex = cursor.getColumnIndex(columns[0]); if (cursor.moveToFirst()) { return cursor.getString(columnIndex); } } finally { cursor.close(); } return null; }



    private Bitmap rotate(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    private int exifOrientationToDegrees(int exifOrientation) { if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) { return 180; } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) { return 270; } return 0; }

    public void checkPermission() {
        boolean isGrant = false;
        for (String str : permission_list) {
            if (ContextCompat.checkSelfPermission(this, str) == PackageManager.PERMISSION_GRANTED) {
            } else {
                isGrant = false;
                break;
            }
        }
        if (isGrant == false) {
            ActivityCompat.requestPermissions(this, permission_list, 0);
        }
    }

    // 사용자가 권한 허용/거부 버튼을 눌렀을 때 호출되는 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isGrant = true;
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                isGrant = false;
                break;
            }
        }
        // 모든 권한을 허용했다면 사용자 위치를 측정한다.
        if (isGrant == true) {
            getMyLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            myLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
         //   Toast.makeText(this,Double.toString(myLocation.getLongitude()),Toast.LENGTH_LONG).show();
        }
    }

    LocationManager manager;
    Location myLocation;

    public void getMyLocation() {
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 권한이 모두 허용되어 있을 때만 동작하도록 한다.
        int chk1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int chk2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (chk1 == PackageManager.PERMISSION_GRANTED && chk2 == PackageManager.PERMISSION_GRANTED) {
          //  myLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }
        // 새롭게 위치를 측정한다.
        GpsListener listener = new GpsListener();
        if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, listener);
        }
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, listener);
        }

    }
    com.google.android.gms.maps.model.Marker tempMarker;
    @Override
    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker Mmarker) {

        if(Cur_Spot>-1)
        {
            EditText et= findViewById(R.id.TextInit_Memo);
            marker[Cur_Spot].setMemo(et.getText());
        }

        for(int i=0;i<Max_Spot;i++)
        {
         if(marker[i].getName().compareTo(Mmarker.getTitle())==0) Cur_Spot=i;
        }
        tempMarker=Mmarker;
        TextView text=(TextView)findViewById(R.id.Spotnametext);
        text.setText(marker[Cur_Spot].getName());
        ConstraintLayout lay=(ConstraintLayout)findViewById(R.id.SpotOption);
        lay.setVisibility(View.VISIBLE);
        EditText edittext = findViewById(R.id.TextInit_Memo);
        edittext.setText(marker[Cur_Spot].getMemo());
        refreshPhoto();
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {

        ConstraintLayout lay=(ConstraintLayout)findViewById(R.id.SpotOption);
        if(Cur_Spot>-1)
        {
            EditText et= findViewById(R.id.TextInit_Memo);
            marker[Cur_Spot].setMemo(et.getText());
        }
        Cur_Spot=-1;
        lay.setVisibility(View.GONE);
    }


    // GPS Listener
    class GpsListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // 현재 위치 값을 저장한다.
            myLocation = location;
            manager.removeUpdates(this);

            Log.e("Location",Double.toString(location.getLongitude()));
            // 위치 측정을 중단한다.
            manager.removeUpdates(this);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }
    }
    public void refreshPhoto()
    {
        ImageView ImView = findViewById(R.id.SpodInstantImage);
        Button bt=findViewById(R.id.button_add_picture);
        bt.setVisibility(View.GONE);
        if(marker[Cur_Spot].image_exists) ImView.setImageBitmap(marker[Cur_Spot].photo);
        else {
            ImView.setImageDrawable(getResources().getDrawable(R.drawable.newpicture));

            bt.setVisibility(View.VISIBLE);
        }



    }



}
