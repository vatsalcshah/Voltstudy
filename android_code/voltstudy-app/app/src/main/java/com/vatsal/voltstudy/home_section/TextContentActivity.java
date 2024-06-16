package com.vatsal.voltstudy.home_section;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.vatsal.voltstudy.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

import javax.annotation.Nullable;

import es.dmoral.toasty.Toasty;

/** View Textual Content */

public class TextContentActivity extends AppCompatActivity {

    private String textName;
    private String textContent;
    private AppCompatSeekBar appCompatSeekBar;
    private PDFView pdfView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_content);


        getDataOverIntent();

        getSupportActionBar();
        Objects.requireNonNull(getSupportActionBar()).setTitle(textName);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        pdfView = findViewById(R.id.pdf_view);
        textView = findViewById(R.id.seekBar_textView);

        initSeekBar();

        String pdfLocation = textName+".pdf";
        downloadPdf(pdfLocation);

    }

    private void initSeekBar() {
        appCompatSeekBar = findViewById(R.id.seekBar_pdf);
        appCompatSeekBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        appCompatSeekBar.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        appCompatSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int val = (progress * (seekBar.getWidth() - 3 * seekBar.getThumbOffset())) / seekBar.getMax();
                textView.setText("" + progress);
                textView.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void getDataOverIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            textName = Objects.requireNonNull(intent.getExtras()).getString("textTitle");
            textContent = intent.getExtras().getString("textContent");
        }
    }


    //On Back Pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void downloadPdf(final String fileName) {

        new AsyncTask<Void, Integer, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                return downloadPdf();
            }

            @Nullable
            private Boolean downloadPdf() {
                try {
                    File file = getFileStreamPath(fileName);
                    if (file.exists())
                        return true;

                    try {
                        FileOutputStream fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                        URL u = new URL(textContent);
                        URLConnection conn = u.openConnection();
                        int contentLength = conn.getContentLength();
                        InputStream inputStream = new BufferedInputStream(u.openStream());
                        byte data[] = new byte[contentLength];
                        long total = 0;
                        int count;
                        while ((count = inputStream.read(data)) != -1) {
                            total += count;
                            publishProgress((int) ((total * 100) / contentLength));
                            fileOutputStream.write(data, 0, count);
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        inputStream.close();
                        return true;
                    } catch (final Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                appCompatSeekBar.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean) {
                    openPdf(fileName);
                } else {
                    Toasty.error(TextContentActivity.this, "Unable to download this Pdf", Toasty.LENGTH_LONG).show();
                }
            }
        }.execute();
    }


    private void openPdf(String fileName) {
        try{
            File file = getFileStreamPath(fileName);
            Log.e("file", "file: " + file.getAbsolutePath());
            appCompatSeekBar.setVisibility(View.GONE);
            pdfView.setVisibility(View.VISIBLE);
            pdfView.fromFile(file)
                    .enableSwipe(true)
                    .swipeHorizontal(true)
                    .pageSnap(true)
                    .autoSpacing(true)
                    .pageFling(true)
                    .enableDoubletap(true)
                    .scrollHandle(new DefaultScrollHandle(this))
                    .onRender(nbPages -> {
                        pdfView.fitToWidth(pdfView.getCurrentPage());
                    })
                    .onError(t -> {
                        Log.e("file: ", "file: " + t.toString());
                    })
                    .enableAntialiasing(true)
                    .load();
            pdfView.setBackgroundColor(Color.LTGRAY);

        } catch (Exception e){
            e.printStackTrace();
        }


    }





}
