package eu.markustegelane.bssp;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

import eu.markustegelane.bssp.databinding.ActivityWin11BsodBinding;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ModernBSOD extends AppCompatActivity {
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
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    // Blue screen settings
    public static Boolean autoClose = true;
    public static Boolean showDetails = true;
    public static Boolean insiderPreview = true;

    public static Boolean device = true;
    public static Boolean qr = true;
    public static Boolean watermark = true;
    public static Boolean blackscreen = false;
    public static Boolean server = false;

    public static int interval = 500;
    public static float scale = 0.75f;
    public static String errorCode = "IRQL_NOT_LESS_OR_EQUAL (0x0000000a)";
    private View mContentView;
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


    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(VISIBLE);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        BlueScreen me = (BlueScreen)bundle.getSerializable("bluescreen");
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> texts = gson.fromJson(me.GetTexts(), type);
        eu.markustegelane.bssp.databinding.ActivityWin11BsodBinding binding = ActivityWin11BsodBinding.inflate(getLayoutInflater());
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        FrameLayout fl = (FrameLayout)findViewById(R.id.frameLayout1);
        insiderPreview = me.GetBool("green");
        autoClose = me.GetBool("autoclose");
        showDetails = me.GetBool("show_description");
        errorCode = me.GetString("code");
        watermark = me.GetBool("watermark");
        qr = me.GetBool("qr");
        device = me.GetBool("device");
        blackscreen = me.GetBool("blackscreen");
        server = me.GetBool("server");
        mVisible = true;
        TextView techInfo = findViewById(R.id.technicalDetails);
        TextView descripy = findViewById(R.id.errorDescription);
        TextView emoticon = findViewById(R.id.sadSmile);
        TextView progress = findViewById(R.id.errorProgress);
        TextView moreInfo = findViewById(R.id.moreInfo);
        descripy.setHorizontallyScrolling(true);
        techInfo.setHorizontallyScrolling(true);
        emoticon.setHorizontallyScrolling(true);
        moreInfo.setText(texts.get("Additional information"));
        progress.setText(texts.get("Progress"));
        emoticon.setText(me.GetString("emoticon"));
        moreInfo.setTextColor(me.GetTheme(false, false));
        techInfo.setTextColor(me.GetTheme(false, false));
        emoticon.setTextColor(me.GetTheme(false, false));
        progress.setTextColor(me.GetTheme(false, false));
        descripy.setTextColor(me.GetTheme(false, false));

        if (server) {
            emoticon.setVisibility(View.GONE);
        }
        if (!watermark) { binding.waterMark.setVisibility(View.INVISIBLE);}
        if (!qr) {binding.qrCode.setVisibility(View.GONE);}
        if (autoClose) {
            descripy.setText(texts.get("Information text with dump"));
        } else {
            descripy.setText(texts.get("Information text without dump"));
        }
        String ecode = texts.get("Error code");
        if (ecode == null) {
            ecode = "";
        }
        if (showDetails) {
            techInfo.setText(String.format(ecode, errorCode.split(" ")[0]));
        } else {
            techInfo.setText(String.format(ecode, errorCode.split(" ")[1].replace("(", "").replace(")", "")));
        }
        fl.setBackgroundColor(me.GetTheme(true, false));
        if (blackscreen) {
            fl.setBackgroundColor(Color.argb(255, 0, 0, 0));
        }
        if (insiderPreview) {
            fl.setBackgroundColor(Color.argb(255, 0, 128, 0));
            if (me.GetString("os").equals("Windows 11")) {
                descripy.setText(descripy.getText().toString().replace("device", "Windows Insider Build"));
            } else {
                descripy.setText(descripy.getText().toString().replace("PC", "Windows Insider Build"));
            }
        }
        if (device) {
            descripy.setText(descripy.getText().toString().replace("PC", "device"));
        }
        scale = (float)me.GetInt("scale") / 100;
        LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout);
        ll.setScaleX(scale);
        ll.setScaleY(scale);
        if (server) {
            ll.setVerticalGravity(Gravity.TOP);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.TOP;
            ll.setLayoutParams(params);
        }
        //ll.setTop(me.GetInt("margin-y"));
        ll.setPadding(me.GetInt("margin-x"), me.GetInt("margin-y"), 0, 0);
        if (me.GetString("os").equals("Windows 8/8.1")) {
            binding.errorProgress.setVisibility(View.GONE);
            binding.qrCode.setVisibility(View.GONE);
            binding.moreInfo.setVisibility(View.GONE);
            binding.linLay1.setPadding(0, -20, 0, 0);
        }

        if (me.GetBool("extracodes")) {
            binding.parameters.setVisibility(VISIBLE);
            binding.parameters.setTextSize((float)me.GetInt("scale") / 9f);
            binding.parameters.setTextSize((float)me.GetInt("scale") / 9f);
            String codes = "";
            codes += me.GenAddress(4, 16, false).replace(", ", "\n");
            binding.parameters.setText(codes);
        }

        new CountDownTimer(interval * 100L, interval) {
            public void onTick(long millisUntilFinished) {
                Long progress;
                progress = (Long)((interval * 100L - millisUntilFinished) / interval);
                TextView progressText = (TextView)findViewById(R.id.errorProgress);
                try {
                    if (me.GetString("os").equals("Windows 8/8.1")) {
                        if (autoClose) {
                            descripy.setText(String.format(texts.get("Information text with dump"), progress.toString()));
                        }
                    } else {
                        progressText.setText(String.format(texts.get("Progress"), progress.toString()));
                    }
                } catch (Exception e) {
                    Toast.makeText(getWindow().getContext(), "Error occoured:\n" + e.toString(), Toast.LENGTH_SHORT).show();
                    cancel();
                }
            }

            public void onFinish() {
                if (autoClose) {
                    finish();
                } else {
                    TextView progressText = (TextView)findViewById(R.id.errorProgress);
                    progressText.setText(String.format(getResources().getString(R.string.Win11_Progress), "100"));
                }
            }
        }.start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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
        mControlsView.setVisibility(View.GONE);
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