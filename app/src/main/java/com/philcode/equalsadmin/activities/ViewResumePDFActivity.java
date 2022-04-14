package com.philcode.equalsadmin.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.philcode.equalsadmin.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewResumePDFActivity extends AppCompatActivity {
    private PDFView pdfView;
    private FloatingActionButton fab_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_resume_pdf);
        fab_main = findViewById(R.id.fab);
        pdfView = findViewById(R.id.pdfView);
        String url = getIntent().getStringExtra("PDF_Uri");

        new RetrivePdfStream().execute(url);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadFile(url);
            }
        });
    }

    private void DownloadFile(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String title = URLUtil.guessFileName(url, null, null);
        request.setTitle(title);
        request.setDescription("Downloading File please wait...");
        String cookie = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookie);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);

        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);

        Toast.makeText(ViewResumePDFActivity.this, "Downloading Started.", Toast.LENGTH_SHORT).show();
    }

    private class RetrivePdfStream extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL (strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if(urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            }catch (IOException e){
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream)
                    .swipeHorizontal(true)
                    .enableSwipe(true)
                    .defaultPage(0)
                    .enableDoubletap(true)
                    .pageSnap(true)
                    .load();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        /*Intent intent = new Intent(EMP_ViewResumePDF_Activity.this, EMP_ViewResume.class);
        intent.putExtra("POST_ID", postJobID);
        startActivity(intent);*/
    }
}