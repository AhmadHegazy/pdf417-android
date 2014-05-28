package mobi.pdf417;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import mobi.pdf417.activity.Pdf417ScanActivity;
import net.photopay.barcode.BarcodeDetailedData;
import net.photopay.base.BaseBarcodeActivity;
import net.photopay.base.BaseRecognitionActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Pdf417MobiDemo extends Activity {

    private static final int MY_REQUEST_CODE = 1337;

    private static final String TAG = "Pdf417MobiDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** request full screen window without title bar (looks better :-P ) */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }

    @SuppressWarnings("deprecation")
    private void showVersionString() {
        String nativeVersionString = BaseRecognitionActivity.getNativeLibraryVersionString();
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String appVersion = pInfo.versionName;
            int appVersionCode = pInfo.versionCode;

            StringBuilder infoStr = new StringBuilder();
            infoStr.append("Application version: ");
            infoStr.append(appVersion);
            infoStr.append(", build ");
            infoStr.append(appVersionCode);
            infoStr.append("\nLibrary version: ");
            infoStr.append(nativeVersionString);

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Version info");
            alertDialog.setMessage(infoStr.toString());

            alertDialog.setButton(this.getString(R.string.photopayOK), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            alertDialog.setCancelable(false);
            alertDialog.show();
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_version:
            showVersionString();
            return true;
        }
        return false;
    }

    public void btnScan_click(View v) {
        Log.i(TAG, "scan will be performed");
        // Intent for ScanActivity
        Intent intent = new Intent(this, Pdf417ScanActivity.class);

        // If you want sound to be played after the scanning process ends, 
        // put here the resource ID of your sound file. (optional)
        intent.putExtra(Pdf417ScanActivity.EXTRAS_BEEP_RESOURCE, R.raw.beep);

        // set EXTRAS_ALWAYS_USE_HIGH_RES to true if you want to always use highest 
        // possible camera resolution (enabled by default for all devices that support
        // at least 720p camera preview frame size)
        //		intent.putExtra(Pdf417ScanActivity.EXTRAS_ALWAYS_USE_HIGH_RES, true);

        // set the license key (for commercial versions only) - obtain your key at
        // http://pdf417.mobi
        //		intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSE_KEY, "Enter_License_Key_Here");

        // If you want to open front facing camera, uncomment the following line.
        // Note that front facing cameras do not have autofocus support, so it will not
        // be possible to scan denser and smaller codes.
        //		intent.putExtra(Pdf417ScanActivity.EXTRAS_CAMERA_TYPE, (Parcelable)CameraType.CAMERA_FRONTFACE);

        // You can use Pdf417MobiSettings object to tweak additional scanning parameters.
        // This is entirely optional. If you don't send this object via intent, default
        // scanning parameters will be used - this means both QR and PDF417 codes will
        // be scanned and default camera overlay will be shown.

        Pdf417MobiSettings sett = new Pdf417MobiSettings();
        // set this to true to enable PDF417 scanning
        sett.setPdf417Enabled(true);
        // Set this to true to scan even barcode not compliant with standards
        // For example, malformed PDF417 barcodes which were incorrectly encoded
        // Use only if necessary because it slows down the recognition process
        //		sett.setUncertainScanning(true);
        // Set this to true to scan barcodes which don't have quiet zone (white area) around it
        // Use only if necessary because it drastically slows down the recognition process 
        sett.setNullQuietZoneAllowed(true);
        // set this to true to enable QR code scanning
        sett.setQrCodeEnabled(true);
        // set this to true to prevent showing dialog after successful scan
        sett.setDontShowDialog(false);
        // if license permits this, remove Pdf417.mobi logo overlay on scan activity
        // if license forbids this, this option has no effect
        sett.setRemoveOverlayEnabled(true);
        // set this to false if you want to receive at most one scan result
//        sett.setAllowMultipleScanResultsOnSingleImage(false);
        // put settings as intent extra
        intent.putExtra(Pdf417ScanActivity.EXTRAS_SETTINGS, sett);

        // Start Activity
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    public void btnInfo_click(View v) {
        int vid = v.getId();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        switch (vid) {
        case R.id.btnGitHub: {
            intent.setData(Uri.parse("https://github.com/PDF417/Android"));
            break;
        }
        case R.id.btnFacebook: {
            intent.setData(Uri.parse("https://www.facebook.com/pdf417mobi"));
            break;
        }
        case R.id.btnInfo: {
            intent.setData(Uri.parse("http://pdf417.mobi"));
            break;
        }
        }
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE && resultCode == BaseBarcodeActivity.RESULT_OK) {
            // obtain scan results
            ArrayList<Pdf417MobiScanData> scanDataList = data.getParcelableArrayListExtra(BaseBarcodeActivity.EXTRAS_RESULT_LIST);
            // NOTE: if you are interested in only single scan result, you can obtain the first element of the array list
            //       or you can use the old key EXTRAS_RESULT
            // If you have set allowing of multiple scan results on single image to false (Pdf417MobiSettings.setAllowMultipleScanResultsOnSingleImage method)
            // scanDataList will contain at most one element.
//            Pdf417MobiScanData scanData = data.getParcelableExtra(BaseBarcodeActivity.EXTRAS_RESULT);
            
            StringBuilder sb = new StringBuilder();
            
            for(Pdf417MobiScanData scanData : scanDataList) {

                // read scanned barcode type (PDF417 or QR code)
                String barcodeType = scanData.getBarcodeType();
                // read the data contained in barcode
                String barcodeData = scanData.getBarcodeData();
                // read raw barcode data
                BarcodeDetailedData rawData = scanData.getBarcodeRawData();
                // determine if returned scan data is certain
                boolean uncertainData = scanData.isResultUncertain();

                // if barcode contains URL, create intent for browser
                // else, contain intent for message
                boolean barcodeDataIsUrl = false;

                try {
                    @SuppressWarnings("unused")
                    URL url = new URL(barcodeData);
                    barcodeDataIsUrl = true;
                } catch (MalformedURLException exc) {
                    barcodeDataIsUrl = false;
                }

                if (barcodeDataIsUrl) {
                    // create intent for browser
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(barcodeData));
                    startActivity(intent);
                    return;
                } else {
                    // ask user what to do with data
                    if (uncertainData) {
                        sb.append("This scan data is uncertain!\n\n");
                    }
                    sb.append(barcodeType);
                    sb.append(" string data:\n");
                    sb.append(barcodeData);
                    if (rawData != null) {
                        sb.append("\n\n");
                        sb.append(barcodeType);
                        sb.append(" raw data:\n");
                        sb.append(rawData.toString());
                        sb.append("\n");
                        sb.append(barcodeType);
                        sb.append(" raw data merged:\n");
                        byte[] allData = rawData.getAllData();
                        sb.append("{");
                        for (int i = 0; i < allData.length; ++i) {
                            sb.append((int) allData[i] & 0x0FF);
                            if (i != allData.length - 1) {
                                sb.append(", ");
                            }
                        }
                        sb.append("}\n\n\n");
                    }
                }
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            startActivity(Intent.createChooser(intent, getString(R.string.UseWith)));
        }
    }
}
