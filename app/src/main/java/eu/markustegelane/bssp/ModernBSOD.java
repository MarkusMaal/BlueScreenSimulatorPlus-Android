package eu.markustegelane.bssp;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
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
import androidx.core.content.res.ResourcesCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.markustegelane.bssp.databinding.ActivityModernBinding;

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
    public static Boolean show_file = false;

    public static int interval = 10;
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
    private final Runnable mHideRunnable = this::hide;

    private void FillCustomGradient(View v) {
        final View view = v;
        Drawable[] layers = new Drawable[1];

        ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(
                        -0.125f * view.getWidth(),
                        -0.125f * view.getHeight(),
                        1.25f * view.getWidth(),
                        1.25f * view.getHeight(),
                        new int[] {
                                Color.rgb(0xEE, 0x82, 0xEE), // please input your color from resource for color-4
                                Color.rgb(255,0,0),
                                Color.rgb(255,0xA5,0),
                                Color.rgb(255,255,0),
                                Color.rgb(0,0x80,0),
                                Color.rgb(0,0,255),
                                Color.rgb(0x4B,0,0x82),
                                Color.rgb(0xEE, 0x82, 0xEE)},
                        new float[] { 0, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f,0.75f, 0.875f },
                        Shader.TileMode.CLAMP);
            }
        };
        PaintDrawable p = new PaintDrawable();
        p.setShape(new RectShape());
        p.setShaderFactory(sf);
        p.setCornerRadii(new float[] { 5, 5, 5, 5, 0, 0, 0, 0 });
        layers[0] = p;

        LayerDrawable composite = new LayerDrawable(layers);
        view.setBackground(composite);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        BlueScreen me = (BlueScreen)bundle.getSerializable("bluescreen");
        if (me.GetFamily() == null) {
            me.SetFont("Ubuntu Light", me.GetStyle(), 23f);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Type proType = new TypeToken<Map<Integer, Integer>>(){}.getType();
        Map<String, String> texts = gson.fromJson(me.GetTexts(), type);
        eu.markustegelane.bssp.databinding.ActivityModernBinding binding = ActivityModernBinding.inflate(getLayoutInflater());
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        FrameLayout fl = findViewById(R.id.frameLayout1);
        insiderPreview = me.GetBool("green");
        autoClose = me.GetBool("autoclose");
        showDetails = me.GetBool("show_description");
        errorCode = me.GetString("code");
        watermark = me.GetBool("watermark");
        qr = me.GetBool("qr");
        device = me.GetBool("device");
        blackscreen = me.GetBool("blackscreen");
        server = me.GetBool("server");
        show_file = me.GetBool("show_file");
        mVisible = true;
        TextView techInfo = findViewById(R.id.technicalDetails);
        TextView descripy = findViewById(R.id.errorDescription);
        TextView emoticon = findViewById(R.id.sadSmile);
        TextView progress = findViewById(R.id.errorProgress);
        TextView moreInfo = findViewById(R.id.moreInfo);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.ubuntu_light);
        int checkExist = getWindow().getContext().getResources().getIdentifier(me.GetFamily().toLowerCase().replace(" ", "_"), "font", getWindow().getContext().getPackageName());
        if (checkExist != 0) {
            typeface = ResourcesCompat.getFont(this, getWindow().getContext().getResources().getIdentifier(me.GetFamily().toLowerCase().replace(" ", "_"), "font", getWindow().getContext().getPackageName()));
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Typeface.Builder tb = new Typeface.Builder("/system/fonts/" + me.GetFamily() + ".ttf");
                typeface = tb.build();
            }
        }
        techInfo.setTypeface(typeface);
        descripy.setTypeface(typeface);
        emoticon.setTypeface(typeface);
        progress.setTypeface(typeface);
        moreInfo.setTypeface(typeface);

        descripy.setTextSize(me.GetSize());
        progress.setTextSize(me.GetSize());
        emoticon.setTextSize(me.GetSize()/23*100);
        moreInfo.setTextSize(me.GetSize()/23*14);
        techInfo.setTextSize(me.GetSize()/23*12);

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
        String suffix = "";
        if (show_file && me.GetString("os").equals("Windows 8/8.1")) {
            suffix = " (" + me.GetString("culprit") + ")";
        }
        if (ecode == null) {
            ecode = "";
        }
        if (showDetails) {
            techInfo.setText(String.format(ecode, errorCode.split(" ")[0] + suffix));
        } else {
            techInfo.setText(String.format(ecode, errorCode.split(" ")[1].replace("(", "").replace(")", "") + suffix));
        }
        if (show_file) {
            if (!me.GetString("os").equals("Windows 8/8.1")) {
                techInfo.setText(String.format("%s\n\n%s", techInfo.getText(), String.format(texts.get("Culprit file"), me.GetString("culprit"))));
            }
        }
        fl.setBackgroundColor(me.GetTheme(true, false));
        if (blackscreen) {
            fl.setBackgroundColor(Color.argb(255, 0, 0, 0));
        }
        if (insiderPreview) {
            fl.setBackgroundColor(Color.argb(255, 0, 128, 0));
            if (me.GetString("os").equals("Windows 11") || me.GetBool("device")) {
                descripy.setText(descripy.getText().toString().replace("device", "Windows Insider Build"));
            } else {
                descripy.setText(descripy.getText().toString().replace("PC", "Windows Insider Build"));
            }
        }
        if (device) {
            descripy.setText(descripy.getText().toString().replace("PC", "device"));
        }
        if (me.GetBool("rainbow")) {
            FillCustomGradient(binding.frameLayout1);
        }
        scale = (float)me.GetInt("scale") / 100;

        LinearLayout ll = findViewById(R.id.linearLayout);
        ll.setScaleX(scale);
        ll.setScaleY(scale);
        ll.setMinimumWidth((int)((float)ll.getWidth() * scale));
        if (server) {
            binding.errorDescription.setPadding(0, me.GetInt("margin-y"), 0, 0);
            ll.setVerticalGravity(Gravity.TOP);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.TOP;
            ll.setLayoutParams(params);
        } else {
            binding.sadSmile.setPadding(0, me.GetInt("margin-y"), 0, 0);
        }
        //ll.setTop(me.GetInt("margin-y"));
        ll.setPadding(me.GetInt("margin-x"), me.GetInt("margin-y"), 0, 0);
        if (me.GetString("os").equals("Windows 8/8.1")) {
            binding.errorProgress.setVisibility(View.GONE);
            binding.qrCode.setVisibility(View.GONE);
            binding.moreInfo.setVisibility(View.GONE);
            binding.linLay1.setPadding(0, -20, 0, 0);
        } else {
            binding.qrCode.setMinimumWidth(me.GetInt("qr_size"));
            binding.qrCode.setMinimumHeight(me.GetInt("qr_size"));
            binding.linLay1.setMinimumHeight(me.GetInt("qr_size"));
        }

        if (me.GetBool("extracodes")) {
            binding.parameters.setVisibility(VISIBLE);
            binding.parameters.setTextSize((float)me.GetInt("scale") / 9f);
            binding.parameters.setTextSize((float)me.GetInt("scale") / 9f);
            String codes = "";
            codes += me.GenAddress(4, 16, false).replace(", ", "\n");
            binding.parameters.setTextColor(me.GetTheme(false, false));
            binding.parameters.setText(codes);
        }
        int length = me.GetInt("progressmillis");
        if (length < 1) {
            length = 100;
        }
        int finalLength = length;
        new CountDownTimer((long) finalLength * interval, interval) {
            final Map<Integer, Integer> prog = gson.fromJson(me.AllProgress(), proType);
            String proText = "0";
            final List<Integer> ignorable = new ArrayList<>();
            public void onTick(long millisUntilFinished) {
                int cs = (int)((finalLength * interval - millisUntilFinished) / interval);
                String newText = proText;
                for (Integer i: prog.keySet()) {
                    if ((cs > i) && (!ignorable.contains(i))) {
                        newText = String.valueOf(prog.get(i) + Integer.parseInt(proText));
                        proText = newText;
                        ignorable.add(i);
                    }
                }
                TextView progressText = findViewById(R.id.errorProgress);
                try {
                    if (me.GetString("os").equals("Windows 8/8.1")) {
                        if (autoClose) {
                            descripy.setText(String.format(texts.get("Information text with dump"), newText));
                        }
                    } else {
                        progressText.setText(String.format(texts.get("Progress"), newText));
                    }
                } catch (Exception e) {
                    Toast.makeText(getWindow().getContext(), "Error occoured:\n" + e.toString(), Toast.LENGTH_SHORT).show();
                    cancel();
                }
            }

            public void onFinish() {
                if (autoClose) {
                    finish();
                } /*else {
                    /// Behaviour in beta versions: switch to 100% if autoclose is disabled and progression has ended
                    // TextView progressText = (TextView)findViewById(R.id.errorProgress);
                    // progressText.setText(String.format(getResources().getString(R.string.Win11_Progress), "100"));
                } */
            }
        }.start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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
    private void delayedHide() {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, ModernBSOD.AUTO_HIDE_DELAY_MILLIS);
    }
}