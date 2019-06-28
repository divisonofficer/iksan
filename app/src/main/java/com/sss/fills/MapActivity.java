package com.sss.fills;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.*;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
    private String Name,Index;
    public String Memo;
    public File Sound=null;
    public String Adress;
    Drawable MarkerMask;
    public Bitmap markerimage=null;
    Marker(double Lon, double Len, String name,String index,String adr)
    {


        this.Lon=Lon;
        this.Len=Len;
        this.Name=name;
        this.Index=index;
        photo_cnt=0;
        Adress=adr;
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
    public static int Cur_Spot=-1;
    String[] permission_list = {

            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE


    };
    MapFragment mapFragment;
    private static int  PICK_IMAGE_REQUEST=1;
    private GoogleMap mMap;
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
        altermarkis=new boolean[Max_Spot];
        for(int i=0;i<Max_Spot;i++) altermarkis[i]=false;
        marker[0] = new Marker(127.093879, 35.982107, "왕궁다원","누구나 좋아하는 곳","전라북도 익산시 왕궁면 사곡길 21-5");
        marker[1] = new Marker(126.946112, 35.953805, "오르도","인기많은 감성카페","전라북도 익산시 선화도 21길 28");
        marker[2] = new Marker(126.944783, 36.001420, "미스터박","맛있는 밥집","전라북도 익산시 황등면 황등로 119-1");
        marker[3] = new Marker(126.978475, 35.961138, "당고","젊은층이 좋아하는 곳","전라북도 익산시 무왕로 11길 6-11");
        marker[4] = new Marker(127.024292, 36.011825, "미륵산순두부","순두부맛있어요","전라북도 익산시 금마면 미륵사지로 397");
        marker[5] = new Marker(127.054974, 35.973117, "왕궁리유적","왕궁리유적","전라북도 익산시 왕궁면 궁성로 666");
        marker[6] = new Marker(127.033123, 35.980438, "쌍릉","쌍릉입니다.","전라북도 익산시 석왕동 산54");
        marker[7] = new Marker(127.030431, 36.012059, "미륵사지 당간지주","미륵사지 당간지주","전라북도 익산시 금마면 기양리");
        marker[8] = new Marker(127.040109, 35.991998, "토성","흙으로 만든 성","전라북도 익산시 금마면 서고도리");

        ConstraintLayout Lay = (ConstraintLayout)findViewById(R.id.SpotOption);
        Lay.setVisibility(View.GONE);
        Button ButtonAddPicture = (Button)findViewById(R.id.button_add_picture);
        ButtonAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //ACTION_PIC과 차이점?
                    intent.setType("image/*"); //이미지만 보이게
                    //Intent 시작 - 갤러리앱을 열어서 원하는 이미지를 선택할 수 있다.
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);*/
                    Intent intent1 = new Intent(MapActivity.this,GetInfActivity.class);
                    startActivity(intent1);
                ConstraintLayout lay=(ConstraintLayout)findViewById(R.id.SpotOption);
                lay.setVisibility(View.GONE);
                    //refreshPhoto();

         }
        });
        Button ButtonGoViewer = (Button)findViewById(R.id.Button_goto_Viewer);
        ButtonGoViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Cur_Spot>=0 && marker[Cur_Spot].image_exists)
                {
                    //setContentView(R.layout.activity_view_photo);
                    //ImageView viewer=(ImageView)findViewById(R.id.imageview_main);
                  //  viewer.setImageBitmap(marker[Cur_Spot].photo);
                   // TextView tb = (TextView)findViewById(R.id.textView_Viewer);
                  //  tb.setText(marker[Cur_Spot].getMemo());
                    Intent intent1 = new Intent(MapActivity.this, ViewPhotoActivity.class);
                    startActivity(intent1);


                }

            }
        });

    }

    public final int Max_Spot=9;
    public static Marker []marker;

    MarkerOptions[] markerOptions;
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        map.getUiSettings().setZoomGesturesEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);

        markerOptions=new MarkerOptions[Max_Spot];
        for(int i=0;i<Max_Spot;i++) {

                markerOptions[i] = new MarkerOptions();
                markerOptions[i].position(marker[i].returnLocation());
                markerOptions[i].title(marker[i].getName());
                markerOptions[i].snippet(marker[i].getIndex());
                markerOptions[i].icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_spot));
                map.addMarker(markerOptions[i]);


        }

        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(36.023070, 126.989683)));
        map.animateCamera(CameraUpdateFactory.zoomTo(11));
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);

    }
    boolean[] altermarkis;
    public void ImageCutting(Bitmap origin,Bitmap Mask,int width, int height,double len, double lon,String title)
    {
        Paint paint= new Paint();
        Bitmap sccaled=origin.copy(origin.getConfig(),true);
        Canvas tempcanvas= new Canvas(sccaled);
        MarkerOptions makerOptions = new MarkerOptions();
        Bitmap Nshape=Mask;
        Nshape=Bitmap.createScaledBitmap(Nshape,origin.getWidth(),origin.getHeight(),true);
        tempcanvas.drawBitmap(origin,0,0,paint);
        PorterDuff.Mode mode=Mode.XOR;
        paint.setXfermode(new PorterDuffXfermode(mode));
        tempcanvas.drawBitmap(Nshape,0,0,paint);
        Bitmap sscaled = Bitmap.createScaledBitmap(sccaled, width, height, true);
        makerOptions
                .position(new LatLng(len, lon))
                .title(title); // 타이틀.
        makerOptions.icon(BitmapDescriptorFactory.fromBitmap(sscaled));

        mMap.addMarker(makerOptions);
        //tempMarker.setIcon(BitmapDescriptorFactory.fromBitmap(sscaled));

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
                Log.e("sss", ""+Cur_Spot);
                marker[Cur_Spot].photo=scaled;
                marker[Cur_Spot].image_exists=true;
                Paint paint= new Paint();
                Bitmap sccaled=scaled.copy(scaled.getConfig(),true);
                Canvas tempcanvas= new Canvas(sccaled);
                MarkerOptions makerOptions = new MarkerOptions();

                if(Cur_Spot == 0) {
                    Bitmap Nshape=((BitmapDrawable)getResources().getDrawable(R.drawable.sector1_1)).getBitmap();
                    Nshape=Bitmap.createScaledBitmap(Nshape,scaled.getWidth(),scaled.getHeight(),true);
                    tempcanvas.drawBitmap(scaled,0,0,paint);
                    PorterDuff.Mode mode=Mode.XOR;
                    paint.setXfermode(new PorterDuffXfermode(mode));
                    tempcanvas.drawBitmap(Nshape,0,0,paint);
                    Bitmap sscaled = Bitmap.createScaledBitmap(sccaled, 850, 670, true);
                    makerOptions
                            .position(new LatLng(36.052777, 126.959019))
                            .title("01번 지역"); // 타이틀.
                    makerOptions.icon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    mMap.addMarker(makerOptions);
                    //tempMarker.setIcon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    refreshPhoto();
                }
                else if(Cur_Spot == 1) {
                    Bitmap Nshape=((BitmapDrawable)getResources().getDrawable(R.drawable.sector1_2)).getBitmap();
                    Nshape=Bitmap.createScaledBitmap(Nshape,scaled.getWidth(),scaled.getHeight(),true);
                    tempcanvas.drawBitmap(scaled,0,0,paint);
                    PorterDuff.Mode mode=Mode.XOR;
                    paint.setXfermode(new PorterDuffXfermode(mode));
                    tempcanvas.drawBitmap(Nshape,0,0,paint);
                    Bitmap sscaled = Bitmap.createScaledBitmap(sccaled, 698, 884, true);
                    makerOptions
                            .position(new LatLng(36.014077, 127.025019))
                            .title("02번 지역"); // 타이틀.
                    makerOptions.icon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    mMap.addMarker(makerOptions);
                    //tempMarker.setIcon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    refreshPhoto();
                }
                else if(Cur_Spot == 2) {
                    Bitmap Nshape=((BitmapDrawable)getResources().getDrawable(R.drawable.sector1_3)).getBitmap();
                    Nshape=Bitmap.createScaledBitmap(Nshape,scaled.getWidth(),scaled.getHeight(),true);
                    tempcanvas.drawBitmap(scaled,0,0,paint);
                    PorterDuff.Mode mode=Mode.XOR;
                    paint.setXfermode(new PorterDuffXfermode(mode));
                    tempcanvas.drawBitmap(Nshape,0,0,paint);
                    Bitmap sscaled = Bitmap.createScaledBitmap(sccaled, 618, 568, true);
                    makerOptions
                            .position(new LatLng(36.002777, 126.929019))
                            .title("03번 지역"); // 타이틀.
                    makerOptions.icon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    mMap.addMarker(makerOptions);
                    //tempMarker.setIcon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    refreshPhoto();
                }
                else if(Cur_Spot == 3) {
                    Bitmap Nshape=((BitmapDrawable)getResources().getDrawable(R.drawable.sector2_4)).getBitmap();
                    Nshape=Bitmap.createScaledBitmap(Nshape,scaled.getWidth(),scaled.getHeight(),true);
                    tempcanvas.drawBitmap(scaled,0,0,paint);
                    PorterDuff.Mode mode=Mode.XOR;
                    paint.setXfermode(new PorterDuffXfermode(mode));
                    tempcanvas.drawBitmap(Nshape,0,0,paint);
                    Bitmap sscaled = Bitmap.createScaledBitmap(sccaled, 464, 778, true);
                    makerOptions
                            .position(new LatLng(35.962877, 127.043019))
                            .title("04번 지역"); // 타이틀.
                    makerOptions.icon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    mMap.addMarker(makerOptions);
                    //tempMarker.setIcon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    refreshPhoto();
                }
                else if(Cur_Spot == 4) {
                    Bitmap Nshape=((BitmapDrawable)getResources().getDrawable(R.drawable.sector2_5)).getBitmap();
                    Nshape=Bitmap.createScaledBitmap(Nshape,scaled.getWidth(),scaled.getHeight(),true);
                    tempcanvas.drawBitmap(scaled,0,0,paint);
                    PorterDuff.Mode mode=Mode.XOR;
                    paint.setXfermode(new PorterDuffXfermode(mode));
                    tempcanvas.drawBitmap(Nshape,0,0,paint);
                    Bitmap sscaled = Bitmap.createScaledBitmap(sccaled, 432, 687, true);
                    makerOptions
                            .position(new LatLng(35.965877, 127.100019))
                            .title("05번 지역"); // 타이틀.
                    makerOptions.icon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    mMap.addMarker(makerOptions);
                    //tempMarker.setIcon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    refreshPhoto();
                }
                else if(Cur_Spot == 5) {
                    Bitmap Nshape=((BitmapDrawable)getResources().getDrawable(R.drawable.sector2_6)).getBitmap();
                    Nshape=Bitmap.createScaledBitmap(Nshape,scaled.getWidth(),scaled.getHeight(),true);
                    tempcanvas.drawBitmap(scaled,0,0,paint);
                    PorterDuff.Mode mode=Mode.XOR;
                    paint.setXfermode(new PorterDuffXfermode(mode));
                    tempcanvas.drawBitmap(Nshape,0,0,paint);
                    Bitmap sscaled = Bitmap.createScaledBitmap(sccaled, 850, 670, true);
                    makerOptions
                            .position(new LatLng(36.026070, 126.989683))
                            .title("06번 지역"); // 타이틀.
                    makerOptions.icon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    mMap.addMarker(makerOptions);
                    //tempMarker.setIcon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    refreshPhoto();
                }
                else if(Cur_Spot == 6) {
                    Bitmap Nshape=((BitmapDrawable)getResources().getDrawable(R.drawable.sector3_7)).getBitmap();
                    Nshape=Bitmap.createScaledBitmap(Nshape,scaled.getWidth(),scaled.getHeight(),true);
                    tempcanvas.drawBitmap(scaled,0,0,paint);
                    PorterDuff.Mode mode=Mode.XOR;
                    paint.setXfermode(new PorterDuffXfermode(mode));
                    tempcanvas.drawBitmap(Nshape,0,0,paint);
                    Bitmap sscaled = Bitmap.createScaledBitmap(sccaled, 850, 670, true);
                    makerOptions
                            .position(new LatLng(36.025070, 126.989683))
                            .title("07번 지역"); // 타이틀.
                    makerOptions.icon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    mMap.addMarker(makerOptions);
                    //tempMarker.setIcon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    refreshPhoto();
                }
                else if(Cur_Spot == 7) {
                    Bitmap Nshape=((BitmapDrawable)getResources().getDrawable(R.drawable.sector3_8)).getBitmap();
                    Nshape=Bitmap.createScaledBitmap(Nshape,scaled.getWidth(),scaled.getHeight(),true);
                    tempcanvas.drawBitmap(scaled,0,0,paint);
                    PorterDuff.Mode mode=Mode.XOR;
                    paint.setXfermode(new PorterDuffXfermode(mode));
                    tempcanvas.drawBitmap(Nshape,0,0,paint);
                    Bitmap sscaled = Bitmap.createScaledBitmap(sccaled, 850, 670, true);
                    makerOptions
                            .position(new LatLng(36.024070, 126.989683))
                            .title("08번 지역"); // 타이틀.
                    makerOptions.icon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    mMap.addMarker(makerOptions);
                    //tempMarker.setIcon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    refreshPhoto();
                }
                else if(Cur_Spot == 8) {
                    Bitmap Nshape=((BitmapDrawable)getResources().getDrawable(R.drawable.sector3_9)).getBitmap();
                    Nshape=Bitmap.createScaledBitmap(Nshape,scaled.getWidth(),scaled.getHeight(),true);
                    tempcanvas.drawBitmap(scaled,0,0,paint);
                    PorterDuff.Mode mode=Mode.XOR;
                    paint.setXfermode(new PorterDuffXfermode(mode));
                    tempcanvas.drawBitmap(Nshape,0,0,paint);
                    Bitmap sscaled = Bitmap.createScaledBitmap(sccaled, 850, 670, true);
                    makerOptions
                            .position(new LatLng(36.023070, 126.989683))
                            .title("09번 지역"); // 타이틀.
                    makerOptions.icon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    mMap.addMarker(makerOptions);
                    //tempMarker.setIcon(BitmapDescriptorFactory.fromBitmap(sscaled));
                    refreshPhoto();
                }


            } else {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Oops! 로딩에 오류가 있습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
    public static Bitmap createSquaredBitmap(Bitmap srcBmp) {
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



    public static Bitmap rotate(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public static int exifOrientationToDegrees(int exifOrientation) { if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) { return 180; } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) { return 270; } return 0; }

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
        Boolean is_ok=false;
        if(Cur_Spot>-1)
        {
            if(!(GetInfActivity.SavedMemo!=null && GetInfActivity.SavedMemo.compareTo("")!=0))
            {EditText et= findViewById(R.id.TextInit_Memo);
            marker[Cur_Spot].setMemo(et.getText());}
        }

        for(int i=0;i<Max_Spot;i++)
        {
            if(Mmarker.getTitle()==null) continue;
         if(marker[i].getName().compareTo(Mmarker.getTitle())==0) {
             Cur_Spot = i;
             is_ok=true;
             break;
         }
        }
        if(!is_ok) return true;
        tempMarker=Mmarker;
        TextView text=(TextView)findViewById(R.id.Spotnametext);
        text.setText(marker[Cur_Spot].getName());
        ConstraintLayout lay=(ConstraintLayout)findViewById(R.id.SpotOption);
        lay.setVisibility(View.VISIBLE);
        EditText edittext = findViewById(R.id.TextInit_Memo);
        edittext.setText(marker[Cur_Spot].getMemo());
        TextView tb= (TextView)findViewById(R.id.SpotAdresstext);
        tb.setText(marker[Cur_Spot].Adress);
        refreshPhoto();
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {

        for(int i=0;i<Max_Spot;i++) {
            if(!altermarkis[i] && marker[i].image_exists)
            {
                switch(i)
                {
                    case 0 : ImageCutting(marker[i].photo,((BitmapDrawable)getResources().getDrawable(R.drawable.sector1_1)).getBitmap(),600,427,36.082777, 126.959019,"왕궁다원");  altermarkis[i]=true; break;
                    case 1 : ImageCutting(marker[i].photo,((BitmapDrawable)getResources().getDrawable(R.drawable.sector1_2)).getBitmap(),500,633,36.024077, 127.015019,"오르도");  altermarkis[i]=true; break;
                    case 2 : ImageCutting(marker[i].photo,((BitmapDrawable)getResources().getDrawable(R.drawable.sector1_3)).getBitmap(),450,413,36.012777, 126.919019,"미스터박");  altermarkis[i]=true; break;
                    case 3 : ImageCutting(marker[i].photo,((BitmapDrawable)getResources().getDrawable(R.drawable.sector2_4)).getBitmap(),850,670,36.032777, 126.959019,"당고");  altermarkis[i]=true; break;

                    case 4 : ImageCutting(marker[i].photo,((BitmapDrawable)getResources().getDrawable(R.drawable.sector2_5)).getBitmap(),850,670,36.022777, 126.959019,"미륵산순두부");  altermarkis[i]=true; break;
                    case 5 : ImageCutting(marker[i].photo,((BitmapDrawable)getResources().getDrawable(R.drawable.sector2_6)).getBitmap(),850,670,36.012777, 126.859019,"왕궁리유적");  altermarkis[i]=true; break;
                    case 6 : ImageCutting(marker[i].photo,((BitmapDrawable)getResources().getDrawable(R.drawable.sector3_7)).getBitmap(),850,670,36.082777, 126.959019,"쌍릉");  altermarkis[i]=true; break;
                    case 7 : ImageCutting(marker[i].photo,((BitmapDrawable)getResources().getDrawable(R.drawable.sector3_8)).getBitmap(),850,670,36.062777, 127.159019,"미륵사지 당간지주");  altermarkis[i]=true; break;
                    case 8 : ImageCutting(marker[i].photo,((BitmapDrawable)getResources().getDrawable(R.drawable.sector3_9)).getBitmap(),850,670,36.062777, 126.959019,"토성");  altermarkis[i]=true; break;



                }
            }
        }

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
