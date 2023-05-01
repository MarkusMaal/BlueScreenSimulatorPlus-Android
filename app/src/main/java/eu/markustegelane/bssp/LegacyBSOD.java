package eu.markustegelane.bssp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import eu.markustegelane.bssp.databinding.ActivityLegacyBinding;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LegacyBSOD extends AppCompatActivity {
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
    private boolean er = false;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private View mContentView;
    private String memcodes;

    private int caret_x = 0;
    private int caret_y = 0;
    private int w;
    private int h;
    private Boolean by = false;
    private Boolean visible = true;

    private Bitmap BufferA;
    private Bitmap BufferB;

    private String[] fileCodes;
    private boolean rb = false;
    private int tbg;
    private int tfg;

    List<Bitmap> characters = new ArrayList<>();

    Map<String, String> texts;

    MediaPlayer mp;
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
    private ActivityLegacyBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        BlueScreen me = (BlueScreen)bundle.getSerializable("bluescreen");
        binding = ActivityLegacyBinding.inflate(getLayoutInflater());
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        binding.bsodWindow.setDrawingCacheEnabled(false);
        setContentView(binding.getRoot());
        /* for fun stuff */
        try { byte[] bytesMessage = me.GetString("culprit").getBytes(StandardCharsets.UTF_8); MessageDigest md = MessageDigest.getInstance("MD5");
              byte[] md5digest = md.digest(bytesMessage);
              Eggy(byteArrayToHex(md5digest));} catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
        if (by) {
            me.SetTheme(Color.rgb(0, 0, 255), Color.rgb(255, 255, 0), false);
            me.SetTheme(Color.rgb(255, 255, 0), Color.rgb(0, 0, 255), true);
        } else if (er) {
            me.SetTheme(Color.rgb(255, 255, 255), Color.rgb(120, 120, 255), false);
            me.SetTheme(Color.rgb(120, 120, 255), Color.rgb(255, 255, 255), true);
        } else if (me.GetTheme(false, false) == me.GetTheme(true, false)) {
            rb = true;
            Random r = new Random();

            tbg = Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255));
            tfg = Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255));
            me.SetTheme(Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)), Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)), false);
            me.SetTheme(Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)), Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)), true);
        }
        if (!me.GetBool("watermark")) {
            binding.watermark.setVisibility(View.GONE);
        }
        if (me.GetBool("extrafile")) {
            Gson gson = new Gson();
            Type arrayType = new TypeToken<Map<String, String[]>>() {
            }.getType();
            Map<String, String[]> codes;
            codes = gson.fromJson(me.GetFiles(), arrayType);
            fileCodes = codes.get(codes.keySet().stream().findFirst().get().toString());
            for (int i = 0; i < fileCodes.length; i++) {
                fileCodes[i] = me.GenHex(8, fileCodes[i]);
            }
        }

        if ((me.GetBool("playsound") && me.GetString("os").equals("Windows 1.x/2.x")) || me.GetString("culprit").equals("Beep.SYS")) {
            mp = MediaPlayer.create(LegacyBSOD.this, R.raw.beep);
            mp.setLooping(true);
            mp.start();
        }
        if (me.GetString("os").equals("Windows 1.x/2.x")) {
            Random r = new Random();
            if (rb) { me.SetTheme(Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)), Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)), false);}

            switch(me.GetString("qr_file")) {
                case "local:0":
                    Draw12Canvas(me, 0, true, "Win1");
                    break;
                case "local:1":
                    Draw12Canvas(me, 0, true, "Win2");
                    break;
                case "local:null":
                    Draw12Canvas(me, 0, true, "default");
                    break;
            }
             new CountDownTimer(Long.MAX_VALUE, interval) {

                @Override
                public void onTick(long l) {
                    if (rb) { me.SetTheme(Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)), Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)), false);}
                    Draw12Canvas(me,r.nextInt(2) * 12, false, "default");
                }

                @Override
                public void onFinish() {
                    if (mp.isPlaying()) {
                        mp.stop();
                    }
                }
            }.start();
        }
        else if (me.GetString("os").equals("Windows XP") ||
                me.GetString("os").equals("Windows Vista") ||
                me.GetString("os").equals("Windows 7") ||
                me.GetString("os").equals("Windows CE")) {
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
            int add = 0;
            int add2 = 0;
            if (!me.GetBool("show_description")) {
                add = 30;
                add2 = 8;
            }

            if (!me.GetString("os").equals("Windows CE")) {
                DrawCanvas(0, me, 0);
                int finalAdd = add;
                int finalAdd2 = add2;
                long end = interval * 100L;
                if (rb) { end = Long.MAX_VALUE; interval = 10; }
                new CountDownTimer(end, interval) {
                    public void onTick(long millisUntilFinished) {
                        int progress;
                        progress = (int) ((interval * 100 - millisUntilFinished) / interval);
                        if (rb) { progress = 0; }
                        if(rb) { me.SetTheme(NextRain(true, me.GetTheme(true, false)), NextRain(false, me.GetTheme(false, false)), false);}
                        if (!me.GetString("os").equals("Windows XP")) {
                            if (!me.GetBool("extrafile") && !me.GetBool("show_file")) {
                                DrawCanvas(progress, me, 0);
                            } else if (me.GetBool("show_file") && !me.GetBool("extrafile")) {
                                DrawCanvas(progress, me, -8 + finalAdd2);
                            } else if (me.GetBool("extrafile") && !me.GetBool("show_file")) {
                                DrawCanvas(progress, me, -8 + finalAdd2);
                            } else if (me.GetBool("extrafile") && me.GetBool("show_file")) {
                                DrawCanvas(progress, me, -36 + finalAdd);
                            }
                        } else {
                            DrawCanvas(progress, me, 0);
                        }
                    }

                    public void onFinish() {
                        if (me.GetBool("autoclose")) {
                            finish();
                        } else {
                            switch (me.GetString("os")) {
                                case "Windows 7":
                                    if (!me.GetBool("extrafile") && !me.GetBool("show_file")) {
                                        DrawCanvas(100, me, -8 + finalAdd2);
                                    } else if (me.GetBool("extrafile") && !me.GetBool("show_file")) {
                                        DrawCanvas(100, me, -40 + finalAdd);
                                    } else if (!me.GetBool("extrafile") && me.GetBool("show_file")) {
                                        DrawCanvas(100, me, -40 + finalAdd);
                                    } else if (me.GetBool("extrafile") && me.GetBool("show_file")) {
                                        DrawCanvas(100, me, -64 + finalAdd);
                                    }
                                    break;
                                case "Windows Vista":
                                    if (!me.GetBool("extrafile") && !me.GetBool("show_file")) {
                                        DrawCanvas(100, me, -8 + finalAdd2);
                                    } else if (me.GetBool("extrafile") && !me.GetBool("show_file")) {
                                        DrawCanvas(100, me, -40 + finalAdd);
                                    } else if (!me.GetBool("extrafile") && me.GetBool("show_file")) {
                                        DrawCanvas(100, me, -40 + finalAdd);
                                    } else if (me.GetBool("extrafile") && me.GetBool("show_file")) {
                                        DrawCanvas(100, me, -64 + finalAdd);
                                    }
                                case "Windows XP":
                                    DrawCanvas(100, me, 0);
                                default:
                                    break;
                            }
                        }
                    }
                }.start();
            } else {
                DrawCanvas(me.GetInt("timer"), me, 0);
                int ce_interval = 1000;
                long end = ce_interval * (long)me.GetInt("timer") + ce_interval;
                if (rb) { end = Long.MAX_VALUE; ce_interval = 10; }
                new CountDownTimer(end, ce_interval) {
                    public void onTick(long millisUntilFinished) {
                        int progress;
                        progress = me.GetInt("timer") - (int) ((me.GetInt("timer") * 1000 + 1000 - millisUntilFinished) / 1000);
                        if (rb) { progress = 0; }
                        if(rb) { me.SetTheme(NextRain(true, me.GetTheme(true, false)), NextRain(false, me.GetTheme(false, false)), false);}
                        if (!me.GetBool("extrafile")) {
                            DrawCanvas(progress, me, 0);
                        } else {
                            DrawCanvas(progress, me, -16);
                        }
                    }

                    public void onFinish() {
                        finish();
                    }
                }.start();
            }
        } else if (me.GetString("os").equals("Windows 9x/Me") ||
                   me.GetString("os").equals("Windows 3.1x")) {
            Draw9xCanvas(me);
            CountDownTimer a = new CountDownTimer(Long.MAX_VALUE, me.GetInt("blink_speed")) {

                @Override
                public void onTick(long l) {
                    visible = !visible;
                    Bitmap bmp;
                    try {
                        if (visible) {
                            binding.bsodWindow.setImageBitmap(BufferB);
                        } else {
                            binding.bsodWindow.setImageBitmap(BufferA);
                        }
                    } catch (Exception ignored) {
                        cancel();
                    }
                }

                @Override
                public void onFinish() {

                }
            };
            a.start();
        } else if (me.GetString("os").equals("Windows 2000") ||
                    me.GetString("os").equals("Windows NT 3.x/4.0")) {
            DrawNTCanvas(me);
            CountDownTimer a = new CountDownTimer(Long.MAX_VALUE, me.GetInt("blink_speed")) {

                @Override
                public void onTick(long l) {
                    visible = !visible;
                    Bitmap bmp;
                    try {
                        if (visible) {
                            binding.bsodWindow.setImageBitmap(BufferB);
                        } else {
                            binding.bsodWindow.setImageBitmap(BufferA);
                        }
                    } catch (Exception ignored) {
                        cancel();
                    }
                }

                @Override
                public void onFinish() {

                }
            };
            if (me.GetBool("blinkblink")) {
                a.start();
            }
        }
        //binding.bsodWindow.setScaleX(((float)me.GetInt("scale")) / 100);
        //binding.bsodWindow.setScaleY(((float)me.GetInt("scale")) / 100);
    }

    void Eggy(String hash) {
        switch (hash) {
            case "2bf84fb23286854982db8ec5af73c05d":
                Intent b = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "h" + /* ow */ "t" /* o */ + "t" /* ake a */ + "p" /* is */ + "s" + ":/"
                                + "/" + /* j */
                                "he" + /* llscape */
                                "l" + /* ies behind */
                                "lo" + /* oving and caring */
                                "ki" + /* kittens */
                                "tty" + /* is a nightmare */ "." /*..*/ +
                                "fan" /* s */ + "do" + /* n't */ "m" + /*ake you cry*/ "." +
                                "com" /* ing to a */ + "/wiki" /* near you */ +
                                "/Hello" + /*, world! */
                                "_K" + /*_*/
                                /* Make */ "it" + /* the best */ "t" + /* hing */ "y" /* ou've ever created! */));
                startActivity(b);
                finish();
                break;
            case "51f7b567b750cbe96ee6b27849cc9d25":
                by = true;
                break;
            case "f55e66df8c62d983625450d5e86e7023":
                er = true;
                break;
            default:
                break;
        }
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
    private void Draw12Canvas(BlueScreen me, Integer shift, boolean newImage, String type) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };

        Bitmap rasters = BitmapFactory.decodeResource(getResources(), R.drawable.doscii);
        rasters.setPremultiplied(false);
        rasters.setHasAlpha(false);
        Bitmap.Config conf = Bitmap.Config.ARGB_4444;
        w = 8;
        int x = 0;
        h = rasters.getHeight();
        Bitmap bmp = Bitmap.createBitmap(624, 228, conf);
        Canvas canvas = new Canvas(bmp);
        if (!newImage) {
            if (shift == 0) {
                return;
            }
            Paint tPaint = new Paint();
            tPaint.setFilterBitmap(false);
            tPaint.setColor(me.GetTheme(true, false));
            canvas.drawRect(0, 0, bmp.getWidth(), bmp.getHeight(), tPaint);
            canvas.drawBitmap(BufferA, 0, -shift, null);
        }
        int i = 0;
        if (newImage || rb) {
            int[] pixels;
            int bg;
            int fg;
            int width;
            int height;
            Bitmap result;

            for (i = 0; i < rasters.getWidth() / w; i += 1) {
                if (x > rasters.getWidth() - w) {
                    x = rasters.getWidth() - w - 2;
                }
                if ((i == 3) || (i == 19) || (i == 50)) {
                    x -= 1;
                } else if ((i == 95)) {
                    x += 1;
                }
                Bitmap currentLetter = Bitmap.createBitmap(rasters, x, 0, w, h, new Matrix(), false);
                bg = me.GetTheme(true, false);
                fg = me.GetTheme(false, false);
                width = currentLetter.getWidth();
                height = currentLetter.getHeight();
                pixels = new int[width * height];
                currentLetter.getPixels(pixels, 0, width, 0, 0, width, height);
                for (int x2 = 0; x2 < pixels.length; ++x2) {
                    int y = x2 / currentLetter.getWidth();
                    int xx = x2 % currentLetter.getWidth();
                    if (Color.red(currentLetter.getPixel(xx, y)) > 140) {
                        pixels[x2] = fg;
                    } else {
                        pixels[x2] = bg;
                    }
                }
                // create result bitmap output
                result = Bitmap.createBitmap(width, height, currentLetter.getConfig());
                //set pixels
                result.setPixels(pixels, 0, width, 0, 0, width, height);
                characters.add(result);
                x += w;
            }
            Paint tPaint = new Paint();
            tPaint.setFilterBitmap(false);
            tPaint.setColor(me.GetTheme(true, false));
            if (!rb) { canvas.drawRect(0, 0, bmp.getWidth(), bmp.getHeight(), tPaint); }
            switch (type) {
                case "Win1":
                    bmp = BitmapFactory.decodeResource(getResources(), R.drawable.win1_splash);
                    bg = me.GetTheme(true, false);
                    fg = me.GetTheme(false, false);
                    width = bmp.getWidth();
                    height = bmp.getHeight();
                    pixels = new int[width * height];
                    bmp.getPixels(pixels, 0, width, 0, 0, width, height);
                    for (int x2 = 0; x2 < pixels.length; ++x2) {
                        int y = x2 / bmp.getWidth();
                        int xx = x2 % bmp.getWidth();
                        if (Color.red(bmp.getPixel(xx, y)) > 140) {
                            pixels[x2] = fg;
                        } else {
                            pixels[x2] = bg;
                        }
                    }
                    // create result bitmap output
                    result = Bitmap.createBitmap(width, height, bmp.getConfig());
                    //set pixels
                    result.setPixels(pixels, 0, width, 0, 0, width, height);
                    BufferA = result.copy(result.getConfig(), true);
                    binding.bsodWindow.setImageBitmap(result);
                    return;
                case "Win2":
                    bmp = BitmapFactory.decodeResource(getResources(), R.drawable.win2_splash);
                    bg = me.GetTheme(true, false);
                    fg = me.GetTheme(false, false);
                    width = bmp.getWidth();
                    height = bmp.getHeight();
                    pixels = new int[width * height];
                    bmp.getPixels(pixels, 0, width, 0, 0, width, height);
                    for (int x2 = 0; x2 < pixels.length; ++x2) {
                        int y = x2 / bmp.getWidth();
                        int xx = x2 % bmp.getWidth();
                        if (Color.red(bmp.getPixel(xx, y)) > 140) {
                            pixels[x2] = fg;
                        } else {
                            pixels[x2] = bg;
                        }
                    }
                    // create result bitmap output
                    result = Bitmap.createBitmap(width, height, bmp.getConfig());
                    //set pixels
                    result.setPixels(pixels, 0, width, 0, 0, width, height);
                    BufferA = result.copy(result.getConfig(), true);
                    binding.bsodWindow.setImageBitmap(result);
                    return;
                default:
                    break;
            }
        }
        Random r = new Random();
        for (int y = 0; y < bmp.getHeight() / h; y++) {
            ArrayList<Integer> rowChars = new ArrayList<>();
            int ay = y;
            if (!newImage) {
                ay = (bmp.getHeight() - h) / h;
            }
            int min = (bmp.getWidth() / w) - 8;
            for (int z = 0; z < r.nextInt(bmp.getWidth() / w + min) + min; z++) {
                rowChars.add(r.nextInt(characters.size() - 1));
            }
            x = 0;
            DrawFilter filter = new PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG, 0);
            canvas.setDrawFilter(filter);
            for (Integer c : rowChars) {
                canvas.drawBitmap(characters.get(c), x, ay * h, null);
                x += w;
            }
            if (!newImage) {
                break;
            }
        }
        BufferA = bmp.copy(bmp.getConfig(), true);
        binding.bsodWindow.setImageBitmap(bmp);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void DrawNTCanvas(BlueScreen me) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };

        Bitmap rasters = BitmapFactory.decodeResource(getResources(), R.drawable.rasternt);
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map <String, String> txts;
        Map <String, String[]> culpritfiles;
        txts = gson.fromJson(me.GetTexts(), type);
        type = new TypeToken<Map<String, String[]>>() {
        }.getType();
        culpritfiles = gson.fromJson(me.GetFiles(), type);
        rasters.setPremultiplied(false);
        rasters.setHasAlpha(false);
        Bitmap.Config conf = Bitmap.Config.ARGB_4444;
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~:,.+*()[]{}/\\-_ ";
        Bitmap bmp = Bitmap.createBitmap(640, 480, conf);
        bmp.setPremultiplied(false);
        bmp.setHasAlpha(false);
        Canvas canvas = new Canvas(bmp);
        Map<Character, Bitmap> alphabetPics = new LinkedHashMap<>();
        w = rasters.getWidth() / alphabet.length();
        int x = 0;
        h = rasters.getHeight();
        int i = 0;
        for (char c: alphabet.toCharArray()) {
            if (x > rasters.getWidth() - w) {
                x = rasters.getWidth() - w - 2;
            }
            alphabetPics.put(c, Bitmap.createBitmap(rasters, x, 0, w, h, new Matrix(), false));

            x += w;
            i++;
        }
        String[] errorMessage;
        StringBuilder myText = new StringBuilder(String.format(txts.get("Error code formatting"), me.GetString("code").split(" ")[1].substring(1, 11), me.GenAddress(4, 8, false).replace(", ", ",")) + "\n");
        String errorDescripy = me.GetString("code").split(" ")[0];
        myText.append(errorDescripy);
        int linelen = errorDescripy.length() + 44;
        if (me.GetBool("show_file") && me.GetString("os").equals("Windows NT 3.x/4.0")) {
            if (me.GetString("culprit").length() > 14) {
            myText.append("*** Address ")
                .append(me.GenHex(8, "RRRRRRRR").toLowerCase())
                .append(" has base at ")
                .append(me.GenHex(8, "RRRRRRRR").toLowerCase())
                .append(" - ")
                .append(me.GetString("culprit").toLowerCase().substring(0, 14));
            if (me.GetString("culprit").toLowerCase().substring(14).length() > 0) {
                myText.append("\n").append(me.GetString("culprit").toLowerCase().substring(14));
            } } else {
                myText.append("*** Address ")
                        .append(me.GenHex(8, "RRRRRRRR").toLowerCase())
                        .append(" has base at ")
                        .append(me.GenHex(8, "RRRRRRRR").toLowerCase())
                        .append(" - ")
                        .append(me.GetString("culprit").toLowerCase());
            }
        } else if (!me.GetString("os").equals("Windows NT 3.x/4.0")){
            myText.append("\n\n");
            String[] fileAddresses = culpritfiles.get(culpritfiles.keySet().stream().findFirst().get());
            String outText = String.format(txts.get("File information"), me.GenHex(8, fileAddresses[0]), me.GenHex(8, fileAddresses[0]), me.GenHex(8, fileAddresses[0]).toLowerCase(), me.GetString("culprit"));
            if (outText.length() < 81) {
                myText.append(outText);
            } else {
                myText.append(outText.substring(0, 80))
                        .append("\n")
                        .append(outText.substring(80));
            }
        }
        myText.append("\n\n");
        if (me.GetString("os").equals("Windows 2000")) {
            myText.append(txts.get("Troubleshooting introduction")).append("\n\n");
            myText.append(txts.get("Troubleshooting text")).append("\n\n");
            myText.append(txts.get("Additional troubleshooting information")).append("\n\n");
        } else if (me.GetString("os").equals("Windows NT 3.x/4.0")) {
            if (me.GetBool("stack_trace")) {
                String processorText = "GenuineIntel";
                if (me.GetBool("amd")) {
                    processorText = "AuthenticAMD";
                }
                myText.append(String.format(txts.get("CPUID formatting"), processorText)).append("\n\n");
                myText.append(String.format("%-38.38s %-38.38s", txts.get("Stack trace heading"), txts.get("Stack trace heading"))).append("\n");
                for (int n = 0; n < culpritfiles.size(); n += 2) {
                    String filename1 = (String) culpritfiles.keySet().toArray()[n];
                    if (culpritfiles.get(filename1).length > 2) {
                        continue;
                    }
                    String filename2 = null;
                    try {
                        filename2 = (String) culpritfiles.keySet().toArray()[n + 1];
                        if (culpritfiles.get(filename2).length > 2) {
                            continue;
                        }
                    } catch (Exception ignored) {

                    }
                    String[] codes1 = culpritfiles.get(filename1);
                    if (filename2 != null) {
                        String[] codes2 = culpritfiles.get(filename2);
                        myText.append(String.format("%-38.38s %-38.41s", String.format(txts.get("Stack trace table formatting"), me.GenHex(8, codes1[0]), me.GenHex(8, codes1[1]), filename1), String.format(txts.get("Stack trace table formatting"), me.GenHex(8, codes2[0]), me.GenHex(8, codes2[1]), filename2)));
                    } else {
                        myText.append(String.format("%-38.38s", String.format(txts.get("Stack trace table formatting"), me.GenHex(8, codes1[0]), me.GenHex(8, codes1[1]), filename1)));
                    }
                    myText.append("\n");
                }
                myText.append("\n\n");
                if (txts.get("Memory address dump table") != null) {
                    myText.append(txts.get("Memory address dump heading")).append("\n");
                    for (int n = 0; n < culpritfiles.size(); n++) {
                        String filename1 = (String) culpritfiles.keySet().toArray()[n];
                        if (culpritfiles.get(filename1).length < 6) {
                            continue;
                        }
                        String[] codes1 = culpritfiles.get(filename1);
                        // 6 code variant for backwards compatibility
                        if (culpritfiles.get(filename1).length != 6) {
                            myText.append(String.format(txts.get("Memory address dump table"), me.GenHex(8, codes1[0]), me.GenHex(8, codes1[1]), me.GenHex(8, codes1[2]), me.GenHex(8, codes1[3]), me.GenHex(8, codes1[4]), me.GenHex(8, codes1[5]), me.GenHex(8, codes1[6]), filename1)).append("\n");
                        } else {
                            myText.append(String.format(txts.get("Memory address dump table"), me.GenHex(8, codes1[0]), me.GenHex(8, codes1[1]), me.GenHex(8, codes1[2]), me.GenHex(8, codes1[3]), me.GenHex(8, codes1[4]), me.GenHex(8, codes1[5]), filename1)).append("\n");
                        }
                    }
                }
                myText.append("\n");
            }
            myText.append(txts.get("Troubleshooting text"));
        }
        errorMessage = myText.toString().split("\n");
        int y_offset = 0;
        Paint tPaint = new Paint();
        tPaint.setFilterBitmap(false);
        tPaint.setColor(me.GetTheme(true, false));
        canvas.drawRect(0, 0, bmp.getWidth(), bmp.getHeight(), tPaint);
        int i1 = y_offset + h + h + (errorMessage.length + 1) * h;
        caret_x = 0;
        caret_y = (h - h/4);
        int k = 0;
        for (String line: errorMessage) {
            bmp = DrawText(0, y_offset + h + h + k*h, w, h, alphabetPics, bmp, line, me.GetTheme(false, false), me.GetTheme(true, false), alphabet);
            if (bmp == null) {
               AlertDialog.Builder builder = new AlertDialog.Builder(getWindow().getContext());
                builder.setMessage(R.string.unsupportedDevice).setPositiveButton(R.string.ok, dialogClickListener).setTitle(R.string.unsupportedTitle).show();
                return;
            }
            k += 1;
        }

        BufferA = bmp.copy(bmp.getConfig(), true);
        BufferB = bmp.copy(bmp.getConfig(), true);
        bmp.recycle();
        bmp = null;

        canvas = new Canvas(BufferB);
        Paint cPaint = new Paint();
        cPaint.setFilterBitmap(false);
        cPaint.setColor(me.GetTheme(false, false));
        canvas.drawRect(0, 2 * h - (h/4f), w, 2 * h - (h / 4f) + ((float)h/4f), cPaint);
        binding.bsodWindow.setImageBitmap(BufferA);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void Draw9xCanvas(BlueScreen me) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
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
        int x_offset = 50;
        String firstcode = me.GenAddress(1, 2, false).replace("0x", "");
        String[] codes = me.GenAddress(4, 8, false).replace("0x", "").split(", ");
        String alphabet = "?ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz~1234567890:,.+*!_-()/\\'\\ ";
        Bitmap bmp = Bitmap.createBitmap(640, 320, conf);
        bmp.setPremultiplied(false);
        bmp.setHasAlpha(false);
        Canvas canvas = new Canvas(bmp);
        Map<Character, Bitmap> alphabetPics = new LinkedHashMap<>();
        w = rasters.getWidth() / alphabet.length();
        int x = 0;
        h = rasters.getHeight();
        int i = 0;
        String shiftOne = "TWXYZalmnoprsbcwxy1360:,.+*!-";
        String shiftTwo = "BEGIKMOQSVegijtv~479(/' ";
        for (char c: alphabet.toCharArray()) {
            if (shiftOne.indexOf(c) != -1) {
                x += 1;
            }
            if (shiftTwo.indexOf(c) != -1) {
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
        switch (me.GetString("Screen mode")) {
            case "System is unresponsive":
                windowsText = titles.get("Warning");
                break;
            case "System is busy":
                windowsText = titles.get("System is busy");
                break;
            default:
                windowsText = "Windows";
                break;
        }
        /*List<String> test_Message = new ArrayList<String>();
        for  (char letter: alphabet.toCharArray()) {
            switch (letter) {
                case 'f':
                case ':':
                    test_Message.add("\n");
                    break;
            }
            test_Message.add("?" + letter);
        }
        errorMessage = String.join("", test_Message).split("\n");*/
        if (me.GetString("Screen mode").equals("")) {
            me.SetString("Screen mode", "Application error");
        }
        if (me.GetString("os").equals("Windows 3.1x")) {
            me.SetString("Screen mode", "No unresponsive programs");
        }
        String screenText = txts.get(me.GetString("Screen mode"));
        String[] errorMessage;
        try {
            if (screenText.contains("%s")) {
                errorMessage = String.format(screenText, firstcode, codes[1].substring(0, 4), codes[2], codes[3]).split("\n");
            } else {
                errorMessage = screenText.split("\n");
            }
        } catch (Exception e) {
            Toast.makeText(getWindow().getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            errorMessage = new String[0];
        }
        int y_offset = bmp.getHeight() / 2 - (h * (4 + errorMessage.length)) / 2;
        Paint tPaint = new Paint();
        tPaint.setFilterBitmap(false);
        tPaint.setColor(me.GetTheme(true, false));
        int backBox_x = (bmp.getWidth() / 2 - (windowsText.length() * w) / 2) - 10;
        int backBox_y = y_offset - 2;
        int backBox_w = backBox_x + (w * windowsText.length() + 20);
        int backBox_h = backBox_y + h + 4;
        canvas.drawRect(0, 0, bmp.getWidth(), bmp.getHeight(), tPaint);
        Paint bPaint = new Paint();
        bPaint.setFilterBitmap(false);
        bPaint.setColor(me.GetTheme(true, true));
        canvas.drawRect(backBox_x, backBox_y, backBox_w, backBox_h, bPaint);
        String prompt = txts.get("Prompt");
        if (prompt == null) {
            prompt = "";
        }
        int i1 = y_offset + h + h + (errorMessage.length + 1) * h;
        caret_x = (bmp.getWidth() / 2 - (prompt.length() * w) / 2 - w) + (prompt.length() * w) + w / 2;
        caret_y = i1 + (h - h/4);
        int k = 0;
        for (String line: errorMessage) {
            bmp = DrawText(x_offset, y_offset + h + h + k*h, w, h, alphabetPics, bmp, line, me.GetTheme(false, false), me.GetTheme(true, false), alphabet);
            if (bmp == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getWindow().getContext());
                builder.setMessage(R.string.unsupportedDevice).setPositiveButton(R.string.ok, dialogClickListener).setTitle(R.string.unsupportedTitle).show();
                return;
            }
            DrawText(x_offset, y_offset + h + h + k*h, w, h, alphabetPics, bmp, line, me.GetTheme(false, false), me.GetTheme(true, false), alphabet);
            k += 1;
        }
        bmp = DrawText(bmp.getWidth() / 2 - (windowsText.length() * w) / 2, y_offset, w, h, alphabetPics, bmp, windowsText, me.GetTheme(false, true),me.GetTheme(true, true), alphabet);

        bmp = DrawText(bmp.getWidth() / 2 - (prompt.length() * w) / 2 - w, i1, w, h, alphabetPics, bmp, prompt, me.GetTheme(false, false), me.GetTheme(true, false), alphabet);

        BufferA = bmp.copy(bmp.getConfig(), true);
        BufferB = bmp.copy(bmp.getConfig(), true);
        bmp.recycle();
        bmp = null;

        canvas = new Canvas(BufferB);
        Paint cPaint = new Paint();
        cPaint.setFilterBitmap(false);
        cPaint.setColor(me.GetTheme(true, true));
        canvas.drawRect(caret_x, caret_y, caret_x + w, caret_y + ((float)h/8f), cPaint);
        binding.bsodWindow.setImageBitmap(BufferA);
    }

    private Bitmap DrawText(int offset_x, int offset_y, int w, int h, Map<Character, Bitmap> alphabetPics, Bitmap original, String text, int bg, int fg, String alphabet) {
        int x = offset_x;
        int y = offset_y;
        alphabetPics = ColorizeAlphabet(alphabetPics, bg, fg, alphabet);
        Bitmap newBitmap = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        DrawFilter filter = new PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG, 0);
        canvas.setDrawFilter(filter);
        canvas.drawBitmap(original, 0, 0, null);
        for (String line : text.split("\n")) {
            for (Character c : line.toCharArray()) {
                try {
                    canvas.drawBitmap(alphabetPics.get(c), x, y, null);
                } catch (Exception ignored) {
                    canvas.drawBitmap(alphabetPics.get(' '), x, y, null);
                }
                x += w;
            }
            x = offset_x;
            y += h;
        }
        return newBitmap;
    }

    private int NextRain(boolean bg, int current) {
        int r = Color.red(current);
        int g = Color.green(current);
        int b = Color.blue(current);
        int target = tbg;
        int tr = Color.red(tbg);
        int tg = Color.green(tbg);
        int tb = Color.blue(tbg);
        if (!bg) {
            target = tfg;
            tr = Color.red(tfg);
            tg = Color.green(tfg);
            tb = Color.blue(tfg);
        }
        if (current == target) {
            Random rnd = new Random();
            if (bg) { tbg = Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)); }
            else { tfg = Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));  }
        }
        if (r < tr) { r += 1; }
        else if (r > tr) { r -= 1; }
        if (g < tg) { g += 1; }
        else if (g > tg) { g -= 1; }
        if (b < tb) { b += 1; }
        else if (b > tb) { b -= 1; }
        return Color.rgb(r, g, b);
    }
    private Map<Character, Bitmap> ColorizeAlphabet(Map<Character, Bitmap> source, int bg, int fg, String alphabet) {
        //String alphabet = "?ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz~1234567890:,.+*!_-()/\\\\' ";
        Map<Character, Bitmap> output = new LinkedHashMap<>();
        for (char letter: alphabet.toCharArray()) {
            Bitmap currentLetter = source.get(letter);
            currentLetter.setPremultiplied(false);

            int width = currentLetter.getWidth();
            int height = currentLetter.getHeight();
            int[] pixels = new int[width * height];
            //get pixels
            currentLetter.getPixels(pixels, 0, width, 0, 0, width, height);
            List<Integer> pixelList = new ArrayList<>();
            for(int x = 0; x < pixels.length; ++x) {
                int y = x / currentLetter.getWidth();
                int xx = x % currentLetter.getWidth();
                if (Color.red(currentLetter.getPixel(xx, y)) > 140) {
                    pixels[x] = bg;
                } else {pixels[x] = fg;}
            }
            // create result bitmap output
            Bitmap result = Bitmap.createBitmap(width, height, currentLetter.getConfig());
            //set pixels
            result.setPixels(pixels, 0, width, 0, 0, width, height);
            output.put(letter, result);
        }
        return output;
    }

    private void DrawCanvas(int progress, BlueScreen me, int shift) {
        int w = 640, h = 480;
        String yourText = "\n";
        if (!me.GetString("os").equals("Windows CE")) {
            yourText += texts.get("A problem has been detected...");
            if (me.GetBool("show_file")) {
                yourText += "\n\n" + texts.get("Culprit file") + me.GetString("culprit");
            }
            if (me.GetBool("show_description")) {
                yourText += "\n\n" + me.GetString("code").split(" ")[0];
            }
            yourText += "\n\n" + texts.get("Troubleshooting introduction");
            yourText += "\n\n" + texts.get("Troubleshooting") + "\n\n";
            yourText += texts.get("Technical information") + "\n\n";
        } else {
            w = 750; h = 400;
        }
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(w, h, conf);
        Canvas canvas = new Canvas (bmp);
            switch (me.GetString("os")) {
                case "Windows XP":
                    yourText += String.format(texts.get("Technical information formatting"), me.GetString("code").split(" ")[1].replace("(", "").replace(")", ""), memcodes);
                    if (me.GetBool("extrafile")) {
                        if (texts.get("Culprit file memory address") != null) {
                            yourText += "\n\n\n" + String.format(texts.get("Culprit file memory address"), me.GetString("culprit"), me.GenHex(8, fileCodes[0]), me.GenHex(8, fileCodes[1]), me.GenHex(8, fileCodes[2]).toLowerCase());
                        }
                    }
                    yourText += "\n\n\n";
                    if (!me.GetBool("extrafile")) {
                        yourText += texts.get("Physical memory dump") + "\n" + texts.get("Technical support");
                    }
                    break;
                case "Windows CE":
                    yourText += texts.get("A problem has occurred...") + "\n";
                    yourText += texts.get("CTRL+ALT+DEL message") + "\n\n";
                    yourText += texts.get("Technical information") + "\n\n";
                    yourText += String.format(texts.get("Technical information formatting"), "0x" + new StringBuilder(new StringBuilder(me.GetString("code").split(" ")[1]).reverse().toString().substring(1, 7)).reverse().toString().toLowerCase(), me.GetString("code").split(" ")[0].toLowerCase().replace("_", " ")) + "\n\n\n";
                    yourText += String.format(texts.get("Restart message"), progress);
                    break;
                default:
                    yourText += String.format(texts.get("Technical information formatting"), me.GetString("code").split(" ")[1].replace("(", "").replace(")", ""), memcodes);
                    if (me.GetBool("extrafile")) {
                        yourText += "\n\n" + String.format(texts.get("Culprit file memory address"), me.GetString("culprit"), fileCodes[0], fileCodes[1], fileCodes[2]);
                    }
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
        canvas.drawRect(0, 0, w, h, tPaint);
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

    @Override
    public void onBackPressed() {
        if ((mp != null) && (mp.isPlaying())) {
            mp.stop();
            mp.reset();
        }
        finish();
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