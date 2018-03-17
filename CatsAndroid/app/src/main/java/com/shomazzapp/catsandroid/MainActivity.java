package com.shomazzapp.catsandroid;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String extraPhotoPath = "photoPath";

    private static String TAG = "MainActivity";
    private ImageButton btnPhoto;
    private File mediaFolder;
    private Uri photoPath;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        btnPhoto = (ImageButton) findViewById(R.id.photo_button);
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        if (!isCameraAviable()) {
            Toast.makeText(this, getResources().getString(R.string.camera_not_available_msg),
                    Toast.LENGTH_LONG).show();
            finish();
        }
        mediaFolder = new File(Environment.getExternalStorageDirectory()
                + getResources().getString(R.string.media_folder));
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelable("photo_path", photoPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        photoPath = savedInstanceState.getParcelable("photo_path");
    }

    @TargetApi(23)
    private void requestPermissions() {
        String[] s = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"};
       // requestPermissions(s, 200);
    }

    private boolean isCameraAviable() {
        return getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    private void openCamera() {
        photoPath = Uri.fromFile(getOutputPhotoFile());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            launchUploadActivity();
        else if (resultCode != RESULT_CANCELED)
            Toast.makeText(this, getResources().getString(R.string.photo_capture_failed_msg),
                    Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed(){
        if (currentFragment == null)
            showExitAlertDialog();
        else if (currentFragment instanceof PhotosFragment) {
            ((PhotosFragment) currentFragment).closeFragment();
            currentFragment = null;
        };
    }

    public void showExitAlertDialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
        ab.setTitle(getResources().getString(R.string.exit));
        ab.setMessage(getResources().getString(R.string.exit_confirmation_msg));
        ab.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                android.os.Process.killProcess(android.os.Process.myPid());
                finish();
                finishAffinity();
                System.exit(0);
            }
        });
        ab.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ab.show();
    }

    private void launchUploadActivity() {
        Intent i = new Intent(this, UploadActivity.class);
        i.putExtra(extraPhotoPath, photoPath.getPath());
        startActivity(i);
    }

    private File getOutputPhotoFile() {
        if (!mediaFolder.exists())
            if (!mediaFolder.mkdirs()) {
                Log.d(TAG, "Failed create mediaFolder!");
                return null;
            }
        String data = new SimpleDateFormat("ssmmHH_ddMMyyyy",
                Locale.getDefault()).format(new Date());
        Log.d(TAG,data);
        return new File(mediaFolder.getPath() + "/Cat_from_" + data + ".jpg");
    }

    public void loadFragment(Fragment fragment) {
        currentFragment = fragment;
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.frame, fragment);
        transaction.commit();

    }

    public void onGalleryClick(View view) {
        loadFragment(new PhotosFragment());
    }
}