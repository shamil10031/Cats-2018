package com.shomazzapp.catsandroid;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class UploadActivity extends AppCompatActivity {

    private ImageButton goButton;
    private ImageView catPreview;
    private TextView catPreviewText;
    private String path;

    public static void loadImageToView(String path, ImageView imView) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        imView.setImageBitmap(BitmapFactory.decodeFile(path, options));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        path = getIntent().getStringExtra(MainActivity.extraPhotoPath);
        catPreviewText = (TextView) findViewById(R.id.text_view_uploaded);
        catPreview = (ImageView) findViewById(R.id.cat_image_view);
        goButton = (ImageButton) findViewById(R.id.upload_image_button);
        loadImageToView(path, catPreview);
    }

    public void onGoClick(View v) {
        new LoadPhotoTOServerTask().execute(path);
    }

    private class LoadPhotoTOServerTask extends AsyncTask<String, Void, Void> {

        private ProgressDialog progressDialog;
        private String catBreed;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadActivity.this);
            progressDialog.setMessage("Отправляю котика на проверку... \n" +
                    "Не волнуйтесь, с ним все будет хорошо!");
            progressDialog.setCancelable(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... path) {
            try {
                Socket socket = new Socket("192.168.1.69", 3838);
                DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream());
                if (!socket.isOutputShutdown()) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    BitmapFactory.decodeFile(path[0])
                            .compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte [] arr = stream.toByteArray();
                    Log.d(getClass().getSimpleName(), "SIZEEE = " + arr.length);
                    oos.writeInt(arr.length);
                    oos.write(arr);
                    oos.flush();
                    Log.d(getClass().getSimpleName(), "Sended!");
                    catBreed = in.readUTF();
                    Log.d(getClass().getSimpleName(), "catBreed = " + catBreed);
                    oos.close();
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.hide();
            if (catBreed == null || catBreed.equals("null"))
                Toast.makeText(UploadActivity.this, "Не получилось узнать породу котика :(",
                        Toast.LENGTH_LONG).show();
            else
                Toast.makeText(UploadActivity.this, "Порода вашего кота : " + catBreed,
                        Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}
