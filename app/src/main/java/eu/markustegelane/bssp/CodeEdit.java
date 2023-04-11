package eu.markustegelane.bssp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import eu.markustegelane.bssp.databinding.ActivityCodeEditBinding;

public class CodeEdit extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityCodeEditBinding binding;
    BlueScreen me;
    int bs_id;
    int selected = 0;
    List<BlueScreen> bluescreens;
    List<String> words = new ArrayList<>();

    @SuppressLint("DiscouragedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCodeEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Bundle bundle = getIntent().getExtras();
        me = (BlueScreen) bundle.getSerializable("bluescreen");
        bs_id = bundle.getInt("bluescreen_id");
        bluescreens = (List<BlueScreen>)bundle.getSerializable("bluescreens");
        for (int i = 1; i <= 16; i++) {
            int idF = getResources().getIdentifier(String.format("f1l%s", i), "id", this.getPackageName());
            int idZ = getResources().getIdentifier(String.format("z1l%s", i), "id", this.getPackageName());
            int idR = getResources().getIdentifier(String.format("r1l%s", i), "id", this.getPackageName());
            String tagF = "f1l" + i;
            String tagZ = "z1l" + i;
            String tagR = "r1l" + i;
            View fb = findViewById(idF);
            View zb = findViewById(idZ);
            View rb = findViewById(idR);
            zb.setOnClickListener(view -> {
                int lumerand = Integer.parseInt(tagZ.replace("z1l", ""));
                String cval = words.get(selected);
                words.set(selected, cval.substring(0, lumerand - 1) + "0" + cval.substring(lumerand));
                me.SetCodes(words.get(0), words.get(1), words.get(2), words.get(3));
                UpdateValues();
            });
            rb.setOnClickListener(view -> {
                int lumerand = Integer.parseInt(tagR.replace("r1l", ""));
                String cval = words.get(selected);
                words.set(selected, cval.substring(0, lumerand - 1) + "R" + cval.substring(lumerand));
                me.SetCodes(words.get(0), words.get(1), words.get(2), words.get(3));
                UpdateValues();
            });
            fb.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                int lumerand = Integer.parseInt(tagF.replace("f1l", ""));
                int id2 = getResources().getIdentifier(String.format("c1l%s", lumerand), "id", getPackageName());
                TextView tv = findViewById(id2);
                String current = tv.getText().toString();
                builder.setTitle(String.format("Set value for word %s at %s", selected + 1, lumerand));
                EditText input = new EditText(view.getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
                input.setText(current);
                builder.setView(input);
                builder.setPositiveButton(getString(R.string.ok), (dialogInterface, i1) -> {
                    String cval = words.get(selected);
                    words.set(selected, cval.substring(0, lumerand - 1) + input.getText().toString() + cval.substring(lumerand));
                    me.SetCodes(words.get(0), words.get(1), words.get(2), words.get(3));
                    UpdateValues();
                });
                builder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i1) -> dialogInterface.cancel());
                builder.show();
            });
        }
        RadioButton radio1 = (RadioButton)findViewById(R.id.wordRadio1);
        RadioButton radio2 = (RadioButton)findViewById(R.id.wordRadio2);
        RadioButton radio3 = (RadioButton)findViewById(R.id.wordRadio3);
        RadioButton radio4 = (RadioButton)findViewById(R.id.wordRadio4);
        radio1.setText(String.format(getString(R.string.word), "1"));
        radio2.setText(String.format(getString(R.string.word), "2"));
        radio3.setText(String.format(getString(R.string.word), "3"));
        radio4.setText(String.format(getString(R.string.word), "4"));
        radio1.setOnCheckedChangeListener((compoundButton, b) -> { if (b) { selected = 0; UpdateValues(); } });
        radio2.setOnCheckedChangeListener((compoundButton, b) -> { if (b) { selected = 1; UpdateValues(); } });
        radio3.setOnCheckedChangeListener((compoundButton, b) -> { if (b) { selected = 2; UpdateValues(); } });
        radio4.setOnCheckedChangeListener((compoundButton, b) -> { if (b) { selected = 3; UpdateValues(); } });
        binding.nullCodeButton.setOnClickListener(view -> {
            words.set(selected, "0000000000000000");
            me.SetCodes(words.get(0), words.get(1), words.get(2), words.get(3));
            UpdateValues();
        });
        binding.randomCodeButton.setOnClickListener(view -> {
            words.set(selected, "RRRRRRRRRRRRRRRR");
            me.SetCodes(words.get(0), words.get(1), words.get(2), words.get(3));
            UpdateValues();
        });
        binding.codeEditOkButton.setOnClickListener(view -> {
            saveSettings(bluescreens, me, bs_id);
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_enter_anim, androidx.navigation.ui.R.anim.nav_default_exit_anim);
            finish();
        });
        UpdateValues();
    }

    public void saveSettings(List<BlueScreen> blues, BlueScreen modified, long id) {
        blues.set((int)id, modified);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(blues);
        editor.putString("bluescreens", json);
        editor.apply();
    }
    public void UpdateValues() {
        words.clear();
        words.add(0, me.GetString("ecode1"));
        words.add(1, me.GetString("ecode2"));
        words.add(2, me.GetString("ecode3"));
        words.add(3, me.GetString("ecode4"));
        for (int i = 1; i <= 16; i++) {
            int id = getResources().getIdentifier(String.format("c1l%s", i), "id", this.getPackageName());
            TextView tv = findViewById(id);
            tv.setText(words.get(selected).substring(i - 1, i));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_code_edit);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}