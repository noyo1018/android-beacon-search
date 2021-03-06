package com.example.register;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.ContentValues.TAG;
import static org.altbeacon.beacon.BeaconManager.DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD;
import static org.altbeacon.beacon.BeaconManager.DEFAULT_FOREGROUND_SCAN_PERIOD;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    private TextView tvLocationMsg;
    private BeaconManager beaconManager;
    private static final long DEFAULT_FOREGROUND_SCAN_PERIOD = 1000L;
    private static final long DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD = 10000L;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public static final String IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    public BeaconLocationData beaconLocationData;
    public static final String FILTER_UUID = "51A907AD-D117-2FA1-C44B-F573F4390AE2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        requestLocationPermissions();
        initBeacon();
        initData();
    }
    private void initView() {
        tvLocationMsg = (TextView) findViewById(R.id.textView);
    }

    private void requestLocationPermissions() {
        //??????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);

                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode,String Permissions[],int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, Permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }

    }
    private void initBeacon() {
//        ??????beaconManager????????????
        beaconManager = BeaconManager.getInstanceForApplication(this);
//        ????????????????????????????????????
        beaconManager.setForegroundBetweenScanPeriod(DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD);
        beaconManager.setForegroundScanPeriod(DEFAULT_FOREGROUND_SCAN_PERIOD);
//        ??????beacon???????????????
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_FORMAT));
//        ???activity????????????BeaconService???????????????,????????????????????????????????????????????????onBeaconServiceConnect??????
        beaconManager.bind(this);

    }
    private void initData(){
        beaconLocationData = new BeaconLocationData();
    }
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                Log.i(TAG, "didRangeBeaconsInRegion: ?????????????????????:"+collection.size());
//                String location = "";
//                updateTextViewMsg("??????didRangeBeaconsInRegion??????");
                if (collection.size() > 0) {
                    //???????????????beacon??????
                    List<Beacon> beacons = new ArrayList<>();
                    for (Beacon beacon : collection) {
//                        ?????????beacon???UUID???????????????????????????
//                        location = location.concat("UUID: ");
//                        location = location.concat(beacon.getId1().toString());
//                        location = location.concat(", Major: ");
//                        location = location.concat(beacon.getId2().toString());
//                        location = location.concat(", Minor: ");
//                        location = location.concat(beacon.getId3().toString());
//                        location = location.concat("\n");

//                            ?????????????????????
                        beacons.add(beacon);

                    }

                    if (beacons.size() > 0) {
                        //                    ???????????????beacons???rssi?????????????????????
                        Collections.sort(beacons, new Comparator<Beacon>() {
                            public int compare(Beacon arg0, Beacon arg1) {
                                return arg1.getRssi()-arg0.getRssi();
                            }
                        });
//                        ???????????????beacon
                        Beacon nearBeacon = beacons.get(0);
                        String UUID = nearBeacon.getId1().toString().toUpperCase();
//                        ??????beacon???major??????
//                        String major = nearBeacon.getId2().toString();
//                        ??????beacon???minor??????
//                        String minor = nearBeacon.getId3().toString();
//                        ?????????????????????????????????
                        String location = beaconLocationData.getLocationMsg(UUID);
                        Log.i(TAG, "didRangeBeaconsInRegion: "+ beacons.toString() );
                        updateTextViewMsg(location);

                    }
                }
            }

        });
        try {
//            ?????????????????????,??????????????????didRangeBeaconsInRegion??????
            beaconManager.startRangingBeaconsInRegion(new Region(FILTER_UUID, null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updateTextViewMsg(final String location) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvLocationMsg.setText(location);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

}