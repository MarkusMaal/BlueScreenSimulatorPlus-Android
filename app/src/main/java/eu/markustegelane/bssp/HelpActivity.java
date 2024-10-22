package eu.markustegelane.bssp;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getWindow().setStatusBarColor(getColor(R.color.black));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        boolean showChars = getIntent().getExtras().getBoolean("egg");
        int swidth = size.x;
        int sheight = size.y * 10;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(swidth, sheight, conf);
        final int text_color;
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {// Night mode is not active, we're using the light theme
            text_color = Color.rgb(0, 0, 0);
        } else {// Night mode is active, we're using dark theme
            text_color = Color.rgb(255, 255, 255);
        }
        if (showChars) {
            findViewById(R.id.basicsButton).setVisibility(View.GONE);
            findViewById(R.id.osOptionsButton).setVisibility(View.GONE);
            findViewById(R.id.fsOptionsButton).setVisibility(View.GONE);
            Random r = new Random();
            new CountDownTimer(Long.MAX_VALUE, 100) {
                @Override
                public void onTick(long l) {
                    Canvas canvas = new Canvas(bmp);
                    Paint tPaint = new Paint();
                    tPaint.setColor(text_color);
                    tPaint.setFilterBitmap(false);
                    tPaint.setAntiAlias(true);
                    tPaint.setTextSize(r.nextInt(500) + 150f);
                    tPaint.setStyle(Paint.Style.FILL);
                    int symbol = r.nextInt(9999);
                    String htmlSymbol = String.format("&#%s", symbol);
                    canvas.drawText(String.valueOf(HtmlCompat.fromHtml(htmlSymbol, HtmlCompat.FROM_HTML_MODE_LEGACY)), r.nextInt(swidth) - 100, r.nextInt(sheight + 100) - 100, tPaint);
                    ((ImageView) findViewById(R.id.imageView3)).setImageBitmap(bmp);
                }

                @Override
                public void onFinish() {

                }
            }.start();
        } else {
            findViewById(R.id.basicsButton).setOnClickListener(view -> {
                InputStream in_s = getResources().openRawResource(R.raw.help_docs_en);
                if (Locale.getDefault().getLanguage().equals("et")) {
                    try {
                        in_s.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    in_s = getResources().openRawResource(R.raw.help_docs_et);
                }
                Display display1 = getWindowManager().getDefaultDisplay();
                Point size1 = new Point();
                display1.getSize(size1);
                showDocument(in_s, size1.y * 4);
            });
            findViewById(R.id.osOptionsButton).setOnClickListener(view -> {
                InputStream in_s = getResources().openRawResource(R.raw.specifics_docs_en);
                if (Locale.getDefault().getLanguage().equals("et")) {
                    try {
                        in_s.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    in_s = getResources().openRawResource(R.raw.specifics_docs_et);
                }
                Display display12 = getWindowManager().getDefaultDisplay();
                Point size12 = new Point();
                display12.getSize(size12);
                showDocument(in_s, size12.y * 8);
            });

            findViewById(R.id.fsOptionsButton).setOnClickListener(view -> {
                InputStream in_s = getResources().openRawResource(R.raw.fullscreen_docs_en);
                if (Locale.getDefault().getLanguage().equals("et")) {
                    try {
                        in_s.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    in_s = getResources().openRawResource(R.raw.fullscreen_docs_et);
                }
                Display display13 = getWindowManager().getDefaultDisplay();
                Point size13 = new Point();
                display13.getSize(size13);
                showDocument(in_s, size13.y * 16);
            });
        }
        getWindow().setTitle(getString(R.string.help));

    }


    void showDocument(InputStream in_s, int size_y) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int swidth = size.x;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(swidth, size_y, conf);
        final int text_color;
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {// Night mode is not active, we're using the light theme
            text_color = Color.rgb(0, 0, 0);
        } else {// Night mode is active, we're using dark theme
            text_color = Color.rgb(255, 255, 255);
        }
        byte[] b;
        try {
            b = new byte[in_s.available()];
            in_s.read(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String markdownText = new String(b);
        String[] paragraphs = markdownText.split("\n\n");
        float h1_size = 120f;
        float h2_size = 80f;
        float h3_size = 50f;
        int y = (int)h1_size/2;
        for (String paragraph: paragraphs) {
            Canvas canvas = new Canvas(bmp);
            Paint tPaint = new Paint();
            tPaint.setColor(text_color);
            tPaint.setFilterBitmap(false);
            tPaint.setAntiAlias(true);
            if (paragraph.startsWith("!")) {
                String placeholder = paragraph.split("\\[")[1].split("\\(")[0].split("]")[0];
                String url = paragraph.split("\\(")[1].split("\\)")[0];
                if (url.startsWith("file:///android_asset")) {
                    String folder = url.replace("file:///android_asset", "").split("/")[1];
                    String identifier = url.replace("file:///android_asset", "").split("/")[2];
                    int resourceId = this.getResources().getIdentifier(identifier, folder, this.getPackageName());
                    if (resourceId != 0) {
                        Bitmap img = BitmapFactory.decodeResource(getResources(), resourceId);
                        Bitmap drawable;
                        if (swidth < img.getWidth()) {
                            float ratio = (float) img.getWidth() / (float) img.getHeight();
                            int height = (int) ((float) swidth / ratio);
                            drawable = Bitmap.createScaledBitmap(img, swidth, height, true);
                        } else {
                            drawable = Bitmap.createScaledBitmap(img, img.getWidth(), img.getHeight(), true);
                        }
                        canvas.drawBitmap(drawable, 0, y, tPaint);
                        y += drawable.getHeight() + 150;
                        continue;
                    }
                    paragraph = placeholder;
                }
            }
            else if (paragraph.startsWith("*")) {
                for (String bullet: paragraph.split("\n")) {
                    Paint cPaint = new Paint();
                    cPaint.setColor(text_color);
                    cPaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(30, y - 15, 10, cPaint);
                    tPaint.setTextSize(40f);
                    tPaint.setStyle(Paint.Style.FILL);
                    //canvas.drawText(paragraph, 0, y, tPaint);
                    int maxChars = (int) ((float) swidth / (tPaint.measureText("i") * 1.9)) - 6;
                    List<String> text_lines = MeasureLines(bullet.replace("* ", ""), maxChars);
                    for (String Line : text_lines) {
                        canvas.drawText("      "  + Line, 0, y, tPaint);
                        y += (int) tPaint.getTextSize();
                    }
                    y += (int) tPaint.getTextSize();
                }
                y += (int) tPaint.getTextSize();
                continue;
            }
            else if (paragraph.startsWith("#")) {
                String title = paragraph.split("\n")[0];
                int offset = title.length() + 1;
                float f_size = h1_size;
                if (title.startsWith("###")) {
                    f_size = h3_size;
                    title = title.substring(4);
                } else if (title.startsWith("##")) {
                    f_size = h2_size;
                    title = title.substring(3);
                } else {
                    title = title.substring(2);
                }
                y += (int) (f_size / 2);
                tPaint.setTextSize(f_size);
                tPaint.setUnderlineText(true);
                tPaint.setStyle(Paint.Style.FILL);
                int maxChars = (int)((float)swidth / (tPaint.measureText("i") * 1.9));
                List<String> title_lines = MeasureLines(title, maxChars);
                for (String title_line: title_lines) {
                    canvas.drawText(title_line, 0, y, tPaint);
                    y += (int) f_size;
                }
                y += (int) (f_size / 2);
                paragraph = paragraph.substring(offset);
            }
            tPaint.setTextSize(40f);
            tPaint.setUnderlineText(false);
            tPaint.setStyle(Paint.Style.FILL);
            //canvas.drawText(paragraph, 0, y, tPaint);
            int maxChars = (int)((float)swidth / (tPaint.measureText("i") * 1.9));
            List<String> text_lines = MeasureLines(paragraph, maxChars);
            for (String Line: text_lines) {
                canvas.drawText(Line, 0, y, tPaint);
                y += (int) tPaint.getTextSize();
            }
            y += (int) tPaint.getTextSize();
        }
        ((ImageView) findViewById(R.id.imageView3)).setImageBitmap(bmp);
    }

    List<String> MeasureLines(String text, int maxChars) {
        List<String> text_lines = new ArrayList<>();
        String[] title_words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word: title_words) {
            if (word.length() < maxChars) {
                if ((line + word).length() < maxChars) {
                    line.append(word).append(" ");
                } else {
                    text_lines.add(line.toString());
                    line = new StringBuilder();
                    line.append(word).append(" ");
                }
            } else {
                line.append(word.substring(0, maxChars));
                text_lines.add(line.toString());
                line = new StringBuilder();
                line.append(word.substring(maxChars + 1));
            }
        }
        if (!line.toString().isEmpty()) {
            text_lines.add(line.toString());
        }
        return text_lines;
    }
}