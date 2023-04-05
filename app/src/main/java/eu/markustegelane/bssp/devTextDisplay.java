package eu.markustegelane.bssp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class devTextDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_text_display);
        Bundle bundle = getIntent().getExtras();
        ((TextView)this.findViewById(R.id.jsonOutput)).setText(bundle.getString("json"));
    }
}