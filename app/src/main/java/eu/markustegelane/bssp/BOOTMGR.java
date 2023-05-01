package eu.markustegelane.bssp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

import eu.markustegelane.bssp.databinding.ActivityBootmgrBinding;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class BOOTMGR extends AppCompatActivity {
    private View mContentView;

    BlueScreen me;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };
    private ActivityBootmgrBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBootmgrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle bundle = getIntent().getExtras();
        if (bundle.getBoolean("immersive")) {
            this.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        if (bundle.getBoolean("ignorenotch")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
        }
        me = (BlueScreen)bundle.getSerializable("bluescreen");
        Gson gson = new Gson();
        Type strType = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> txts = gson.fromJson(me.GetTexts(), strType);
        Map<String, String> titles = gson.fromJson(me.GetTitles(), strType);
        int w = 1024, h = 768;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(w, h, conf);
        Canvas canvas = new Canvas (bmp);
        Paint tPaint = new Paint();
        tPaint.setColor(me.GetTheme(true, false));
        canvas.drawRect(0, 0, w, h, tPaint);
        tPaint.setColor(me.GetTheme(false, false));
        canvas.drawRect(20, 10, w - 40, 44, tPaint);
        canvas.drawRect(20, h - 44, w - 40, h - 10, tPaint);
        Typeface tf = ResourcesCompat.getFont(this, R.font.inconsolata);
        DrawText(txts.get("Troubleshooting introduction"), 84, 20, canvas, bmp, me.GetTheme(false, false), "", 0, tf);
        DrawText(txts.get("Troubleshooting"), 180, 60, canvas, bmp, me.GetTheme(false, false), "", 0, tf);
        DrawText(txts.get("Troubleshooting without disc"), 300, 20, canvas, bmp, me.GetTheme(false, false), "", 0, tf);
        DrawText(txts.get("Status"), 436, 69, canvas, bmp, me.GetTheme(false, false), me.GetString("code").toLowerCase(), me.GetTheme(false, true), tf);
        DrawText(txts.get("Info"), 498, 69, canvas, bmp, me.GetTheme(false, false), txts.get("Error description"), me.GetTheme(false, true), tf);
        tPaint.setTextSize(24);
        tPaint.setAntiAlias(true);
        tPaint.setTypeface(tf);
        tPaint.setStyle(Paint.Style.FILL);
        float titleWidth = tPaint.measureText(titles.get("Main"));
        float exitWidth = tPaint.measureText(txts.get("Exit") + "  ");
        float titlePosition = (w - 20f) / 2 - titleWidth / 2;
        float exitPosition = (w - 20f) - exitWidth;
        DrawText(titles.get("Main"), 10, titlePosition, canvas, bmp, me.GetTheme(true, false), "", 0, tf);
        DrawText(txts.get("Exit"), h - 44, exitPosition, canvas, bmp, me.GetTheme(true, false), "", 0, tf);
        DrawText(txts.get("Continue"), h - 44, 25, canvas, bmp, me.GetTheme(true, false), "", 0, tf);
    }

    void DrawText(String yourText, int shift, float x_coord, Canvas canvas, Bitmap bmp, int theme, String extra, int themehl, Typeface tf) {
        int i = 0;
        Paint tPaint = new Paint();
        for (String line: yourText.split("\n")) {
            tPaint.setTextSize(24);
            tPaint.setAntiAlias(true);
            tPaint.setTypeface(tf);
            tPaint.setColor(theme);
            tPaint.setStyle(Paint.Style.FILL);
            float height = tPaint.measureText("yY");
            float width = tPaint.measureText(line);
            if (!extra.equals("")) {
                DrawText(extra, shift, x_coord + width + tPaint.measureText(" "), canvas, bmp, themehl, "", themehl, tf);
            }
            canvas.drawText(line, x_coord, (height + 24f * i) + shift, tPaint); // 15f is to put space between top edge and the text, if you want to change it, you can
            //canvas.drawBitmap(bmp, 0f, 0f, null);
            i++;
        }
        binding.imageView.setImageBitmap(bmp);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}