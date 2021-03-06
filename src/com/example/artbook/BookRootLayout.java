package com.example.artbook;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-3-18
 * Time: 下午3:05
 * To change this template use File | Settings | File Templates.
 */
public class BookRootLayout extends FrameLayout implements CurlView.PageProvider {

    private ViewGroup webViewLayout;

    private WebView webView;

    private CurlView curlView;

    private Handler handler = new Handler();

    ProgressDialog loadDialog;

    public BookRootLayout(Context context) {
        super(context);
        initWebView();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
    }

    private void initWebView() {
        webViewLayout = new LinearLayout(getContext());
        webViewLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        webViewLayout.setBackgroundColor(Color.LTGRAY);

        webView = new WebView(getContext());
        webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage message) {
                Log.d("artbook", message.message() + " -- From line "
                        + message.lineNumber() + " of "
                        + message.sourceId());
                return true;
            }
        });

        loadDialog = new ProgressDialog(getContext());
        loadDialog.setMessage("加载网页..");
        loadDialog.setCancelable(false);
        loadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadDialog.show();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loadDialog.setMessage("生成翻页信息..");
                initCurlView();
            }
        });

        webView.setHorizontalScrollBarEnabled(false);

        webView.loadUrl("file:///android_asset/book.html");

        this.webViewLayout.addView(webView);
        this.addView(this.webViewLayout);
    }

    private void initCurlView() {
        curlView = new CurlView(getContext());
        curlView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        curlView.setPageProvider(this);
        curlView.setViewMode(CurlView.SHOW_ONE_PAGE);
        curlView.setCurrentIndex(0);
        curlView.setBackgroundColor(0xFF202830);

        this.addView(curlView);
        loadDialog.dismiss();
    }

    @Override
    public int getPageCount() {
        return 16;
    }

    @Override
    public void updatePage(final CurlPage page, final int width, final int height, final int index) {
        webView.scrollTo((getWidth()) * index, 0);
        Bitmap bitmap = ArtBookUtils.loadBitmapFromView(webViewLayout, webViewLayout.getWidth(),
                webViewLayout.getHeight());

        page.setTexture(bitmap, CurlPage.SIDE_FRONT);
        page.setTexture(flip(new BitmapDrawable(bitmap)).getBitmap(), CurlPage.SIDE_BACK);
        page.setColor(Color.argb(50, 255, 255, 255),
                CurlPage.SIDE_BACK);
    }

    BitmapDrawable flip(BitmapDrawable d) {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap src = d.getBitmap();
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return new BitmapDrawable(dst);
    }

}
