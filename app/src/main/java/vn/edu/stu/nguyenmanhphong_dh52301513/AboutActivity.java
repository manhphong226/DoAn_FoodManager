package vn.edu.stu.nguyenmanhphong_dh52301513;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;

public class AboutActivity extends AppCompatActivity {

    private static final String STUDENT_NAME = "Nguyễn Mạnh Phong";
    private static final String STUDENT_CLASS = "D23_TH11";
    private static final String STUDENT_ID = "DH52301513";
    private static final String STUDENT_EMAIL = "manhphong2406@gmail.com";
    private static final String STUDENT_PHONE = "0818915799";

    // Toa do Truong Dai hoc Cong nghe Sai Gon (180 Cao Lo, TP.HCM),
    // doi chieu tren OpenStreetMap: 10.7379821, 106.6779264.
    private static final double STU_LATITUDE = 10.73805805392432;
    private static final double STU_LONGITUDE = 106.67798546803746;

    private static final int REQUEST_CALL_PHONE = 100;

    // OSM yeu cau User-Agent ro rang khi tai tile.
    private static final String OSM_USER_AGENT = "QuanLyMonAn/1.0 (" + STUDENT_EMAIL + ")";
    private static final String OSM_TILE_URL = "https://tile.openstreetmap.org/";

    Toolbar toolbar;
    TextView tvStudentName, tvClassName, tvStudentId, tvEmail, tvPhone;
    Button btnDial, btnCall;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupOsmdroidConfig();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addControls();
        addEvents();
        loadStudentInfo();
        loadMap();
    }

    private void addControls() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvStudentName = findViewById(R.id.tvStudentName);
        tvClassName = findViewById(R.id.tvClassName);
        tvStudentId = findViewById(R.id.tvStudentId);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        btnDial = findViewById(R.id.btnDial);
        btnCall = findViewById(R.id.btnCall);
        mapView = findViewById(R.id.mapView);
    }

    private void addEvents() {
        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEmailApp();
            }
        });
        tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialer();
            }
        });
        btnDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialer();
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processCall();
            }
        });
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private void setupOsmdroidConfig() {
        IConfigurationProvider config = Configuration.getInstance();
        config.load(
                getApplicationContext(),
                getSharedPreferences("osmdroid_prefs", MODE_PRIVATE)
        );

        // Uu tien bo nho ngoai cua ung dung, neu khong co thi dung bo nho trong.
        File basePath = getExternalFilesDir(null);
        if (basePath == null) {
            basePath = getFilesDir();
        }
        File osmdroidBasePath = new File(basePath, "osmdroid");
        File tileCache = new File(osmdroidBasePath, "tiles");
        if (!tileCache.exists()) {
            tileCache.mkdirs();
        }

        config.setOsmdroidBasePath(osmdroidBasePath);
        config.setOsmdroidTileCache(tileCache);
        config.setUserAgentValue(OSM_USER_AGENT);
    }

    private void loadStudentInfo() {
        tvStudentName.setText(getString(R.string.about_full_name, STUDENT_NAME));
        tvClassName.setText(getString(R.string.about_class, STUDENT_CLASS));
        tvStudentId.setText(getString(R.string.about_student_id, STUDENT_ID));
        tvEmail.setText(getString(R.string.about_email, STUDENT_EMAIL));
        tvPhone.setText(getString(R.string.about_phone, STUDENT_PHONE));
    }

    private void loadMap() {
        // Khai bao ro tile source HTTPS de khong bi chan boi chinh sach cleartext.
        OnlineTileSourceBase osmTileSource = new XYTileSource(
                "OSM-Mapnik",
                0,
                19,
                256,
                ".png",
                new String[]{OSM_TILE_URL},
                "\u00A9 OpenStreetMap contributors"
        );

        mapView.setTileSource(osmTileSource);
        mapView.setUseDataConnection(true);
        mapView.setMultiTouchControls(true);
        mapView.setTilesScaledToDpi(true);
        mapView.setMinZoomLevel(3.0);
        mapView.setMaxZoomLevel(19.0);
        mapView.getController().setZoom(17.0);

        GeoPoint stuPoint = new GeoPoint(STU_LATITUDE, STU_LONGITUDE);
        mapView.getController().setCenter(stuPoint);
        mapView.setExpectedCenter(stuPoint);

        Marker marker = new Marker(mapView);
        marker.setPosition(stuPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(getString(R.string.about_map_marker));
        marker.setSnippet(getString(R.string.about_address));
        mapView.getOverlays().add(marker);
        mapView.invalidate();

        if (!isNetworkAvailable()) {
            Toast.makeText(this, R.string.about_no_network, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkCapabilities capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        if (capabilities == null) {
            return false;
        }
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }

    private void openEmailApp() {
        Intent intent = new Intent(
                Intent.ACTION_SENDTO,
                Uri.parse("mailto:" + STUDENT_EMAIL)
        );
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.about_no_email_app, Toast.LENGTH_SHORT).show();
        }
    }

    private void openDialer() {
        Intent intent = new Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + STUDENT_PHONE)
        );
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.about_no_dialer, Toast.LENGTH_SHORT).show();
        }
    }

    private void processCall() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CALL_PHONE
            );
        } else {
            makeCall();
        }
    }

    private void makeCall() {
        Intent intent = new Intent(
                Intent.ACTION_CALL,
                Uri.parse("tel:" + STUDENT_PHONE)
        );
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.about_no_dialer, Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.about_call_permission_denied, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall();
            } else {
                Toast.makeText(
                        this,
                        R.string.about_call_permission_denied,
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
            // Tai lai tile khi quay lai man hinh (vi du vua bat lai Wi-Fi/4G).
            mapView.invalidate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        if (mapView != null) {
            mapView.onDetach();
            mapView = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnuAbout) {
            return true;
        } else if (item.getItemId() == R.id.mnuExit) {
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }
}
