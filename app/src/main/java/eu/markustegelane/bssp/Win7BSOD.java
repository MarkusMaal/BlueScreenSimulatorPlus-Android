package eu.markustegelane.bssp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.format.FormatStyle;
import java.util.Hashtable;
import java.util.Map;

import eu.markustegelane.bssp.databinding.ActivityWin7BsodBinding;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Win7BSOD extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    public static int interval = 100;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private View mContentView;
    private String memcodes;
    Map<String, String> texts;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };
    private ActivityWin7BsodBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        BlueScreen me = (BlueScreen)bundle.getSerializable("bluescreen");
        binding = ActivityWin7BsodBinding.inflate(getLayoutInflater());
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        if (me.GetString("os").equals("Windows XP") ||
                me.GetString("os").equals("Windows Vista") ||
                me.GetString("os").equals("Windows 7")) {
            int len = 16;
            if (me.GetString("os").equals("Windows XP")) {
                len = 8;
            }
            String thirdword = me.GenHex(len, me.GetCodes()[2]);
            memcodes = "0x" + me.GenHex(len, me.GetCodes()[0]) + "," + "0x" + me.GenHex(len, me.GetCodes()[1]) + ",";
            if (!me.GetString("os").equals("Windows XP")) {
                memcodes += "0x" + thirdword + ",0\nx" + me.GenHex(len, me.GetCodes()[3]);
            } else {
                memcodes += "0x" + thirdword + ",0x" + me.GenHex(len, me.GetCodes()[3]);
            }

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            texts = gson.fromJson(me.GetTexts(), type);
            DrawCanvas(0, me, 0);

            new CountDownTimer(interval * 100L, interval) {
                public void onTick(long millisUntilFinished) {
                    int progress;
                    progress = (int) ((interval * 100 - millisUntilFinished) / interval);
                    DrawCanvas(progress, me, 0);
                }

                public void onFinish() {
                    if (me.GetBool("autoclose")) {
                        finish();
                    } else {
                        switch (me.GetString("os")) {
                            case "Windows 7":
                                if (!me.GetBool("showfile")) {
                                    DrawCanvas(100, me, -8);
                                } else {
                                    DrawCanvas(100, me, -64);
                                }
                                break;
                            case "Windows Vista":
                                DrawCanvas(100, me, -8);
                            case "Windows XP":
                                DrawCanvas(100, me, 0);
                            default:
                                break;
                        }
                    }
                }
            }.start();
        } else if (me.GetString("os").equals("Windows 9x/Me")) {
            Draw9xCanvas(me);
        }
        //binding.bsodWindow.setScaleX(((float)me.GetInt("scale")) / 100);
        //binding.bsodWindow.setScaleY(((float)me.GetInt("scale")) / 100);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void Draw9xCanvas(BlueScreen me) {
        Bitmap rasters = BitmapFactory.decodeResource(getResources(), R.drawable.rasters3);
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map <String, String> titles;
        Map <String, String> txts;
        titles = gson.fromJson(me.GetTitles(), type);
        txts = gson.fromJson(me.GetTexts(), type);
        rasters.setPremultiplied(false);
        rasters.setHasAlpha(false);
        Bitmap.Config conf = Bitmap.Config.ARGB_4444;
        Bitmap bmp = Bitmap.createBitmap(1150, 640, conf);
        bmp.setPremultiplied(false);
        bmp.setHasAlpha(false);
        Canvas canvas = new Canvas(bmp);
        String alphabet = "?ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz~1234567890:,.+*!_-()/\\\\' ";
        Map<Character, Bitmap> alphabetPics = new Hashtable<>();
        int w = 16;
        int x = 0;
        int h = rasters.getHeight();
        int i = 0;
        for (char c: alphabet.toCharArray()) {
            if ((i > 4) && (i % 2 == 1) && (i < 15)) {
                x += 2;
            }
            if ((i > 14) && (i % 2 == 0) && (i < 28)) {
                x += 2;
            }
            if ((i > 31) && (i % 2 == 0) && (i < 36)) {
                x += 3;
            }
            if ((i > 35) && (i % 2 == 1) && (i < 50)) {
                x += 2;
            }
            if ((i > 50) && (i % 2 == 0) && (i < 58)) {
                x += 2;
            }
            if ((i > 57) && (i % 2 == 1) && (i < 64)) {
                x += 2;
            }
            if ((i > 65) && (i % 2 == 0)) {
                x += 2;
            }
            if (x > rasters.getWidth() - w) {
                x = rasters.getWidth() - w - 2;
            }
            alphabetPics.put(c, Bitmap.createBitmap(rasters, x, 0, w, h, new Matrix(), false));

            x += w;
            i++;
        }
        String windowsText = titles.get("Main");
        String[] errorMessage = txts.get("Driver error").split("\n");
        int y_offset = bmp.getHeight() / 2 - (h * (4 + errorMessage.length)) / 2;
        int x_offset = 50;
        Paint tPaint = new Paint();
        tPaint.setFilterBitmap(false);
        tPaint.setColor(me.GetTheme(true, false));
        canvas.drawRect(0, 0, bmp.getWidth(), bmp.getHeight(), tPaint);
        bmp = DrawText(bmp.getWidth() / 2 - (windowsText.length() * w) / 2, y_offset, w, h, alphabetPics, bmp, windowsText, me.GetTheme(true, true),me.GetTheme(false, true));
        int k = 0;
        for (String line: errorMessage) {
            bmp = DrawText(x_offset, y_offset + h + h + k*h, w, h, alphabetPics, bmp, line, me.GetTheme(false, false), me.GetTheme(true, false));
            DrawText(x_offset, y_offset + h + h + k*h, w, h, alphabetPics, bmp, line, me.GetTheme(false, false), me.GetTheme(true, false));
            k += 1;
        }
        String prompt = txts.get("Prompt");
        bmp = DrawText(bmp.getWidth() / 2 - (prompt.length() * w) / 2 - w, y_offset + h + h + (k+1)*h, w, h, alphabetPics, bmp, prompt, me.GetTheme(false, false), me.GetTheme(true, false));

        binding.bsodWindow.setImageBitmap(bmp);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Bitmap DrawText(int offset_x, int offset_y, int w, int h, Map<Character, Bitmap> alphabetPics, Bitmap original, String text, int bg, int fg) {
        int x = offset_x;
        int y = offset_y;
        alphabetPics = ColorizeAlphabet(alphabetPics, bg, fg);
        Bitmap newBitmap =  Bitmap.createBitmap(original.getWidth(), original.getHeight(),Bitmap.Config.ARGB_8888,false);
        Canvas canvas = new Canvas(newBitmap);
        DrawFilter filter = new PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG, 0);
        canvas.setDrawFilter(filter);
        canvas.drawBitmap(original, 0, 0, null);
        for (String line: text.split("\n")) {
            for (Character c: line.toCharArray()) {
                try {
                    canvas.drawBitmap(alphabetPics.get(c), x, y, null);
                } catch (Exception ignored){
                    canvas.drawBitmap(alphabetPics.get('?'), x, y, null);
                }
                x+=w;
            }
            x = offset_x;
            y+=h;
        }
        return newBitmap;
    }

    private Map<Character, Bitmap> ColorizeAlphabet(Map<Character, Bitmap> source, int bg, int fg) {
        String alphabet = "?ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz~1234567890:,.+*!_-()/\\\\' ";
        for (char letter: alphabet.toCharArray()) {
            Bitmap currentLetter = source.get(letter);
            currentLetter.setPremultiplied(false);

            int width = currentLetter.getWidth();
            int height = currentLetter.getHeight();
            int[] pixels = new int[width * height];
            //get pixels
            currentLetter.getPixels(pixels, 0, width, 0, 0, width, height);

            for(int x = 0; x < pixels.length; ++x) {
                pixels[x] = (Color.red(pixels[x]) < 90) ? Color.rgb(17, 17, 17) : pixels[x];
                pixels[x] = (pixels[x] != Color.rgb(17, 17, 17)) ? fg : pixels[x];
                pixels[x] = (pixels[x] == Color.rgb(17, 17, 17)) ? bg : pixels[x];
            }
            // create result bitmap output
            Bitmap result = Bitmap.createBitmap(width, height, currentLetter.getConfig());
            //set pixels
            result.setPixels(pixels, 0, width, 0, 0, width, height);

            source.replace(letter, result);
        }
        return source;
    }

    private void DrawCanvas(int progress, BlueScreen me, int shift) {
        int w = 640, h = 480;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(w, h, conf);
        Canvas canvas = new Canvas (bmp);
        String yourText = "\n";
        yourText += texts.get("A problem has been detected...");
        yourText += "\n\n" + me.GetString("code").split(" ")[0];
        yourText += "\n\n" + texts.get("Troubleshooting introduction");
        yourText += "\n\n" + texts.get("Troubleshooting") + "\n\n";
        yourText += texts.get("Technical information") + "\n\n";
        switch (me.GetString("os")) {
            case "Windows XP":
                yourText += String.format(texts.get("Technical information formatting"), me.GetString("code").split(" ")[1].replace("(", "").replace(")", ""), memcodes);
                yourText += "\n\n\n";
                yourText += texts.get("Physical memory dump") + "\n" + texts.get("Technical support");
                break;
            default:
                yourText += String.format(texts.get("Technical information formatting"), me.GetString("code").split(" ")[1].replace("(", "").replace(")", ""), memcodes);
                yourText += "\n\n\n" + texts.get("Collecting data for crash dump") + "\n";
                yourText += texts.get("Initializing crash dump") + "\n" + texts.get("Begin dump") + "\n";
                yourText += String.format(texts.get("Physical memory dump"), String.format("%3s", String.valueOf(progress)));
                break;
        }
        if (!me.GetString("os").equals("Windows XP")) {
            if (progress == 100) {
                yourText += "\n" + texts.get("End dump");
                yourText += "\n" + texts.get("Technical support");
            }
        }
        int i = 0;
        Paint tPaint = new Paint();
        tPaint.setColor(me.GetTheme(true, false));
        canvas.drawRect(0, 0, 640, 480, tPaint);
        for (String line: yourText.split("\n")) {
            tPaint.setTextSize(16);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.inconsolata);
            tPaint.setAntiAlias(true);
            tPaint.setTypeface(typeface);
            tPaint.setColor(me.GetTheme(false, false));
            tPaint.setStyle(Paint.Style.FILL);
            float height = tPaint.measureText("yY");
            float width = tPaint.measureText(line);
            float x_coord = 0;
            canvas.drawText(line, x_coord, (height + 15f * i) + shift, tPaint); // 15f is to put space between top edge and the text, if you want to change it, you can
            //canvas.drawBitmap(bmp, 0f, 0f, null);
            i++;
        }
        binding.bsodWindow.setImageBitmap(bmp);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().show(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}