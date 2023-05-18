package eu.markustegelane.bssp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.Display;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Random;

import io.noties.markwon.Markwon;

public class HelpActivity extends AppCompatActivity {

    Bitmap Buffer;
    boolean showChars = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        if (showChars) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int swidth = size.x;
            int sheight = size.y * 2;
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap bmp = Bitmap.createBitmap(swidth, sheight, conf);
            Random r = new Random();

            new CountDownTimer(Long.MAX_VALUE, 100) {
                @Override
                public void onTick(long l) {
                    Canvas canvas = new Canvas(bmp);
                    Paint tPaint = new Paint();
                    tPaint.setColor(Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
                    tPaint.setFilterBitmap(false);
                    tPaint.setAntiAlias(true);
                    tPaint.setTextSize(r.nextInt(500) + 150f);
                    tPaint.setStyle(Paint.Style.FILL);
                    int symbol = r.nextInt(9999);
                    String htmlSymbol = String.format("&#%s", symbol);
                    canvas.drawText(String.valueOf(Html.fromHtml(htmlSymbol)), r.nextInt(swidth) - 100, r.nextInt(sheight + 100) - 100, tPaint);
                    ((ImageView) findViewById(R.id.imageView3)).setImageBitmap(bmp);
                }

                @Override
                public void onFinish() {

                }
            }.start();
        } else {
            final Markwon markwon = Markwon.create(getWindow().getContext());
            InputStream in_s = getResources().openRawResource(R.raw.help_docs_en);
            if (Locale.getDefault().getLanguage().equals("et")) {
                try {
                    in_s.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                in_s = getResources().openRawResource(R.raw.help_docs_et);
            }
            byte[] b = new byte[0];
            try {
                b = new byte[in_s.available()];
                in_s.read(b);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String markdownText = new String(b);
            markwon.setMarkdown(findViewById(R.id.textView6), markdownText);
        }
        getWindow().setTitle(getString(R.string.help));

    }
}