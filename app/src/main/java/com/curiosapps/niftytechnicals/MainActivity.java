package com.curiosapps.niftytechnicals;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    ProgressBar pb;
    boolean isRedirected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.Pbar);
        pb.setVisibility(View.GONE);


        webView = (WebView) findViewById(R.id.myWebView);
        assert webView != null;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog pd;

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                isRedirected = true;
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isRedirected = false;
            }

            public void onLoadResource(WebView view, String url) {
                if (!isRedirected) {
                    if (pd == null) {
                        pd = new ProgressDialog(MainActivity.this);
                        pd.setMessage("Loading...");
                        pd.show();
                        pd.setCanceledOnTouchOutside(false);
                    }
                }

            }

            public void onPageFinished(WebView view, String url) {
                try {
                    isRedirected = true;

                    if (pd.isShowing()) {
                        pd.dismiss();
                        pd = null;
                    }


                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        });
        webSettings.setAppCacheEnabled(true);


        webView.loadUrl("http://google.com");

        webView.setDownloadListener(new DownloadListener() {

            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {

                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Lootwala");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); //This is important!
                intent.addCategory(Intent.CATEGORY_OPENABLE); //CATEGORY.OPENABLE
                intent.setType("*/*");//any application,any extension
                Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
                        Toast.LENGTH_LONG).show();

            }
        });

    }

    public void onBackPressed() {
        if (webView.canGoBack())
            webView.goBack();
        else
            finish();
    }
}
