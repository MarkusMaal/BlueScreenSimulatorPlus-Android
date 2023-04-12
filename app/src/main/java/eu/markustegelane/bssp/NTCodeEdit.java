package eu.markustegelane.bssp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.markustegelane.bssp.databinding.ActivityNtcodeEditBinding;

public class NTCodeEdit extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityNtcodeEditBinding binding;
    BlueScreen me;
    List<BlueScreen> bluescreens = new ArrayList<>();
    int bs_id;

    int selected;
    String[] words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNtcodeEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Bundle bundle = getIntent().getExtras();
        me = (BlueScreen) bundle.getSerializable("bluescreen");
        bs_id = bundle.getInt("bluescreen_id");
        bluescreens = (List<BlueScreen>)bundle.getSerializable("bluescreens");
        AddListeners(getResources(), this.getPackageName());
        SetWords(7);

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String[]>>(){}.getType();
        Map<String, String[]> codeFiles = gson.fromJson(me.GetFiles(), type);
        RefreshSpinner();
        binding.errorFileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] cfiles = codeFiles.get(((String)adapterView.getAdapter().getItem(adapterView.getSelectedItemPosition())).split(" ")[0]);
                words = cfiles;
                SetWords(words.length);
                selected = 0;
                UpdateValues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.codeSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected = i;
                UpdateValues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        UpdateValues();
    }


    public void RefreshSpinner() {
        long backup_sel = binding.errorFileSpinner.getSelectedItemId();
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String[]>>(){}.getType();
        Map<String, String[]> codeFiles = gson.fromJson(me.GetFiles(), type);
        List<String> codeStrLst = new ArrayList<>();
        for (String key: codeFiles.keySet()) {
            codeStrLst.add(key + " (0x" + String.join(", 0x", codeFiles.get(key)) + ")");
        }
        ArrayAdapter<String> spa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, codeStrLst);
        spa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.errorFileSpinner.setAdapter(spa);
        binding.errorFileSpinner.setSelection((int)backup_sel);
    }

    public void SetWords(int length) {
        int backup = (int)binding.codeSelectSpinner.getSelectedItemId();
        List<String> codeListItems = new ArrayList<>();
        for (int i = 1; i <= length; i++) {
            codeListItems.add(String.format(getString(R.string.word), String.valueOf(i)));
        }
        ArrayAdapter<String> spa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, codeListItems);
        spa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.codeSelectSpinner.setAdapter(spa);
        binding.codeSelectSpinner.setSelection(backup);
    }

    public void AddListeners(Resources r, String p) {
        for (int i = 1; i <= 8; i++) {
            int idF = r.getIdentifier(String.format("f1l%s", i), "id", p);
            int idZ = r.getIdentifier(String.format("z1l%s", i), "id", p);
            int idR = r.getIdentifier(String.format("r1l%s", i), "id", p);
            String tagF = "f1l" + i;
            String tagZ = "z1l" + i;
            String tagR = "r1l" + i;
            View fb = findViewById(idF);
            View zb = findViewById(idZ);
            View rb = findViewById(idR);
            zb.setOnClickListener(view -> {
                int lumerand = Integer.parseInt(tagZ.replace("z1l", ""));
                String cval = words[selected];
                words[selected] =  cval.substring(0, lumerand - 1) + "0" + cval.substring(lumerand);
                SetCodes(words);
                UpdateValues();
            });
            rb.setOnClickListener(view -> {
                int lumerand = Integer.parseInt(tagR.replace("r1l", ""));
                String cval = words[selected];
                words[selected] = cval.substring(0, lumerand - 1) + "R" + cval.substring(lumerand);
                SetCodes(words);
                UpdateValues();
            });
            fb.setOnClickListener(view -> {
                int lumerand = Integer.parseInt(tagF.replace("f1l", ""));
                String cval = words[selected];
                char[] nums = "0123456789ABCDEF".toCharArray();
                char nextVal = '0';
                for (int k = 0; k < nums.length; k++) {
                    if (cval.charAt(lumerand - 1) == nums[k]) {
                        if (k + 1 > nums.length - 1) {
                            nextVal = '0';
                        } else {
                            nextVal = nums[k + 1];
                        }
                        break;
                    }
                }
                words[selected] = cval.substring(0, lumerand - 1) + nextVal + cval.substring(lumerand);
                SetCodes(words);
                UpdateValues();
            });
        }
        findViewById(R.id.nullCodeButton).setOnClickListener(view -> {
            words[selected] = "00000000";
            SetCodes(words);
            UpdateValues();
        });
        findViewById(R.id.randomCodeButton).setOnClickListener(view -> {
            words[selected] = "RRRRRRRR";
            SetCodes(words);
            UpdateValues();
        });
        findViewById(R.id.codeEditOkButton).setOnClickListener(view -> {
            saveSettings(bluescreens, me, bs_id);
            onBackPressed();
        });
    }


    public void SetCodes(String[] words) {
        me.SetFile(binding.errorFileSpinner.getAdapter().getItem((int)binding.errorFileSpinner.getSelectedItemId()).toString().split(" ")[0],selected, words[selected]);
        RefreshSpinner();
        saveSettings(bluescreens, me, bs_id);
    }

    public void UpdateValues() {
        if (words == null) { return; }
        String word = words[selected];
        for (int i = 1; i <= word.length(); i++) {
            int id = getResources().getIdentifier(String.format("c1l%s", i), "id", this.getPackageName());
            TextView tv = findViewById(id);
            tv.setText(word.substring(i - 1, i));
        }
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_enter_anim, androidx.navigation.ui.R.anim.nav_default_exit_anim);
        finish();
    }
}