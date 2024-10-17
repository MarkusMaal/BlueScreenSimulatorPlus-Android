package eu.markustegelane.bssp;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

import eu.markustegelane.bssp.databinding.ActivityBootmgrBinding;
import eu.markustegelane.bssp.databinding.ActivityJupiterBinding;
import eu.markustegelane.bssp.databinding.ActivityModernBinding;

public class JupiterBSOD extends AppCompatActivity {
    private View mContentView;

    BlueScreen me;

    private boolean NEAREST_NEIGHBOR = false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eu.markustegelane.bssp.databinding.ActivityJupiterBinding binding = ActivityJupiterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle bundle = getIntent().getExtras();
        if (bundle.getBoolean("immersive")) {
            System.out.println("** IMMERSIVE **");
            this.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            mHidePart2Runnable.run();
        }
        if (bundle.getBoolean("ignorenotch")) {
            System.out.println("** IGNORE NOTCH **");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
        }
        me = (BlueScreen)bundle.getSerializable("bluescreen");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int swidth = size.x;
        int sheight = size.y;
        binding.waterMark.setVisibility(me.GetBool("watermark") ? View.VISIBLE : View.GONE);
        binding.topText.setText(me.GetText("Your computer needs to restart"));
        binding.bottomText.setText(String.format("%s\n%s", me.GetText("Information text with dump"), String.format(me.GetText("Error code"), me.GetString("code").replace(")", "").replace("(", "").split("\\s")[1])));
        binding.main.setBackgroundColor(me.GetTheme(true, false));
        binding.topText.setTextColor(me.GetTheme(false, false));
        binding.bottomText.setTextColor(me.GetTheme(false, false));
        NEAREST_NEIGHBOR = bundle.getBoolean("nearestscaling");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}