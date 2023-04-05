package eu.markustegelane.bssp;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
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
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Map;

import eu.markustegelane.bssp.BlueScreen;
import eu.markustegelane.bssp.databinding.ActivityWin11BsodBinding;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Win11BSOD extends AppCompatActivity {
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

    public static int interval = 500;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        Map<String, String> texts = (Map<String, String>) bundle.getSerializable("texts");
        eu.markustegelane.bssp.databinding.ActivityWin11BsodBinding binding = ActivityWin11BsodBinding.inflate(getLayoutInflater());
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        FrameLayout fl = (FrameLayout)findViewById(R.id.frameLayout1);
        insiderPreview = bundle.get("insiderPreview").toString().equals("true");
        autoClose = bundle.get("autoClose").toString().equals("true");
        showDetails = bundle.get("showDetails").toString().equals("true");
        errorCode = bundle.get("errorCode").toString();
        mVisible = true;
        TextView techInfo = (TextView)findViewById(R.id.technicalDetails);
        TextView descripy = (TextView)findViewById(R.id.errorDescription);
        TextView emoticon = findViewById(R.id.sadSmile);
        TextView progress = findViewById(R.id.errorProgress);
        TextView moreInfo = findViewById(R.id.moreInfo);
        moreInfo.setText(texts.get("Additional information"));
        progress.setText(texts.get("Progress"));
        emoticon.setText(bundle.get("emoticon").toString());
        if (autoClose) {
            descripy.setText(texts.get("Information text with dump"));
        } else {
            descripy.setText(texts.get("Information text without dump"));
        }
        if (showDetails) {
            techInfo.setText(String.format(texts.get("Error code"), errorCode.split(" ")[0]));
        } else {
            techInfo.setText(String.format(texts.get("Error code"), errorCode.split(" ")[1].replace("(", "").replace(")", "")));
        }
        fl.setBackgroundColor((int)bundle.get("bg"));
        if (insiderPreview) {
            fl.setBackgroundColor(Color.argb(255, 0, 128, 0));
            descripy.setText(getResources().getString(R.string.Win11_Description).replace("device", "Windows Insider Build"));

        }
        new CountDownTimer(interval * 100L, interval) {
            public void onTick(long millisUntilFinished) {
                Long progress;
                progress = (Long)((interval * 100L - millisUntilFinished) / interval);
                TextView progressText = (TextView)findViewById(R.id.errorProgress);
                progressText.setText(String.format(getResources().getString(R.string.Win11_Progress), progress.toString()));
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