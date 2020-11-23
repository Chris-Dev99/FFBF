package edu.catalin.forfoodiesbyfoodies.basic_user;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import edu.catalin.forfoodiesbyfoodies.R;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        WebView webView = (WebView)findViewById(R.id.webViewPage);
        String url = "https://www.opentable.com";
        webView.loadUrl(url);
    }
}