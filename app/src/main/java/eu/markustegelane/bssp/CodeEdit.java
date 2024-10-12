package eu.markustegelane.bssp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

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
    @SuppressWarnings("unchecked")
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
        AddListeners(getResources(), this.getPackageName(), this);
        int blockSize = 0;
        switch (me.GetString("os")) {
            case "Windows XP":
            case "Windows NT 3.x/4.0":
            case "Windows 2000":
            case "Windows 9x/Me":
                blockSize = 8;
                break;
        }
        HideElements(blockSize);
        UpdateValues(this, "");
    }

    @SuppressLint("DiscouragedApi")
    public void AddListeners(Resources r, String p, Activity a) {
        for (int i = 1; i <= 16; i++) {
            int idF = r.getIdentifier(String.format("f1l%s", i), "id", p);
            int idZ = r.getIdentifier(String.format("z1l%s", i), "id", p);
            int idR = r.getIdentifier(String.format("r1l%s", i), "id", p);
            String tagF = "f1l" + i;
            String tagZ = "z1l" + i;
            String tagR = "r1l" + i;
            View fb = a.findViewById(idF);
            View zb = a.findViewById(idZ);
            View rb = a.findViewById(idR);
            zb.setOnClickListener(view -> {
                int lumerand = Integer.parseInt(tagZ.replace("z1l", ""));
                String cval = words.get(selected);
                words.set(selected, cval.substring(0, lumerand - 1) + "0" + cval.substring(lumerand));
                if (a == CodeEdit.this) { ((CodeEdit)a).SetCodes(words.toArray(new String[0])); }
                else { ((NTCodeEdit)a).SetCodes(words.toArray(new String[0])); }
                UpdateValues(a, "");
            });
            rb.setOnClickListener(view -> {
                int lumerand = Integer.parseInt(tagR.replace("r1l", ""));
                String cval = words.get(selected);
                words.set(selected, cval.substring(0, lumerand - 1) + "R" + cval.substring(lumerand));
                if (a == CodeEdit.this) { ((CodeEdit)a).SetCodes(words.toArray(new String[0])); }
                else { ((NTCodeEdit)a).SetCodes(words.toArray(new String[0])); }
                UpdateValues(a, "");
            });
            fb.setOnClickListener(view -> {
                int lumerand = Integer.parseInt(tagF.replace("f1l", ""));
                String cval = words.get(selected);
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
                words.set(selected, cval.substring(0, lumerand - 1) + nextVal + cval.substring(lumerand));
                if (a == CodeEdit.this) { ((CodeEdit)a).SetCodes(words.toArray(new String[0])); }
                else { ((NTCodeEdit)a).SetCodes(words.toArray(new String[0])); }
                UpdateValues(a, "");
            });
        }
        RadioButton radio1 = (RadioButton)a.findViewById(R.id.wordRadio1);
        RadioButton radio2 = (RadioButton)a.findViewById(R.id.wordRadio2);
        RadioButton radio3 = (RadioButton)a.findViewById(R.id.wordRadio3);
        RadioButton radio4 = (RadioButton)a.findViewById(R.id.wordRadio4);

        if (radio1 != null) {
            radio1.setText(String.format(getString(R.string.word), "1"));
            radio2.setText(String.format(getString(R.string.word), "2"));
            radio3.setText(String.format(getString(R.string.word), "3"));
            radio4.setText(String.format(getString(R.string.word), "4"));
            radio1.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    selected = 0;
                    UpdateValues(a, "");
                }
            });
            radio2.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    selected = 1;
                    UpdateValues(a, "");
                }
            });
            radio3.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    selected = 2;
                    UpdateValues(a, "");
                }
            });
            radio4.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    selected = 3;
                    UpdateValues(a, "");
                }
            });
        }
        a.findViewById(R.id.nullCodeButton).setOnClickListener(view -> {
            words.set(selected, "0000000000000000");
            if (a == CodeEdit.this) { ((CodeEdit)a).SetCodes(words.toArray(new String[0])); }
            else { ((NTCodeEdit)a).SetCodes(words.toArray(new String[0])); }
            UpdateValues(a, words.get(selected));
        });
        a.findViewById(R.id.randomCodeButton).setOnClickListener(view -> {
            words.set(selected, "RRRRRRRRRRRRRRRR");
            if (a == CodeEdit.this) { ((CodeEdit)a).SetCodes(words.toArray(new String[0])); }
            else { ((NTCodeEdit)a).SetCodes(words.toArray(new String[0])); }
            UpdateValues(a, words.get(selected));
        });
        a.findViewById(R.id.codeEditOkButton).setOnClickListener(view -> {
            saveSettings(bluescreens, me, bs_id);
            a.onBackPressed();
        });
    }


    public void SetCodes(String[] words) {
        me.SetCodes(words[0], words[1], words[2], words[3]);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_enter_anim, androidx.navigation.ui.R.anim.nav_default_exit_anim);
        finish();
    }
    @SuppressLint("DiscouragedApi")
    public void HideElements(int blockSize) {
        for (int i = 16 - blockSize + 1; i <= 16; i++) {
            int idF = getResources().getIdentifier(String.format("f1l%s", i), "id", this.getPackageName());
            int idZ = getResources().getIdentifier(String.format("z1l%s", i), "id", this.getPackageName());
            int idR = getResources().getIdentifier(String.format("r1l%s", i), "id", this.getPackageName());
            int idL = getResources().getIdentifier(String.format("c1l%s", i), "id", this.getPackageName());
            View fb = findViewById(idF);
            View zb = findViewById(idZ);
            View rb = findViewById(idR);
            View lb = findViewById(idL);
            fb.setVisibility(View.GONE);
            zb.setVisibility(View.GONE);
            rb.setVisibility(View.GONE);
            lb.setVisibility(View.GONE);
        }
        for (int i = 1; i < 16 - blockSize + 1; i++) {
            int idF = getResources().getIdentifier(String.format("f1l%s", i), "id", this.getPackageName());
            int idZ = getResources().getIdentifier(String.format("z1l%s", i), "id", this.getPackageName());
            int idR = getResources().getIdentifier(String.format("r1l%s", i), "id", this.getPackageName());
            int idL = getResources().getIdentifier(String.format("c1l%s", i), "id", this.getPackageName());
            View fb = findViewById(idF);
            View zb = findViewById(idZ);
            View rb = findViewById(idR);
            View lb = findViewById(idL);
            fb.setVisibility(View.VISIBLE);
            zb.setVisibility(View.VISIBLE);
            rb.setVisibility(View.VISIBLE);
            lb.setVisibility(View.VISIBLE);
        }
    }

    public void saveSettings(List<BlueScreen> blues, BlueScreen modified, long id) {
        blues.set((int)id, modified);
        SharedPreferences sharedPreferences = this.getSharedPreferences("eu.markustegelane.bssp", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(blues);
        editor.putString("bluescreens", json);
        editor.apply();
    }
    public void UpdateValues(Activity a, String word) {
        boolean here = a == CodeEdit.this;
        if (!here) {
            for (int i = 1; i <= 16; i++) {
                int id = getResources().getIdentifier(String.format("c1l%s", i), "id", this.getPackageName());
                TextView tv = findViewById(id);
                tv.setText(word.substring(i - 1, i));
            }
            return;
        }
        words.clear();
        words.add(0, me.GetString("ecode1"));
        words.add(1, me.GetString("ecode2"));
        words.add(2, me.GetString("ecode3"));
        words.add(3, me.GetString("ecode4"));
        if (me.GetString("os").equals("Windows 9x/Me")) {
            switch (selected) {
                case 0:
                    HideElements(14);
                    break;
                case 1:
                    HideElements(12);
                    break;
                case 2:
                case 3:
                    HideElements(8);
                    break;
            }
        }
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