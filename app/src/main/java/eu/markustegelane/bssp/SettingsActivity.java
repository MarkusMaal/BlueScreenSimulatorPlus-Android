package eu.markustegelane.bssp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class SettingsActivity extends AppCompatActivity {

    static int devProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_enter_anim, androidx.navigation.ui.R.anim.nav_default_exit_anim);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        Toast devToast;
        @Override
        @SuppressWarnings("unchecked")
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            devToast = Toast.makeText(getContext(), String.format(getString(R.string.devThingie), String.valueOf(8 - devProgress)), Toast.LENGTH_SHORT);
            addPreferencesFromResource(R.xml.pscreen);
            PreferenceScreen ps = getPreferenceScreen();
            Bundle b = getActivity().getIntent().getExtras();
            PreferenceCategory pc;
            BlueScreen me = (BlueScreen)b.getSerializable("bluescreen");
            getActivity().setTitle(me.GetString("friendlyname"));
            int os_id = b.getInt("bluescreen_id");
            List<BlueScreen> bsods = (List<BlueScreen>)b.getSerializable("bluescreens");
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Type inType = new TypeToken<Map<String, Integer>>(){}.getType();
            Map<String, String> texts = gson.fromJson(me.GetTexts(), type);
            Map<String, String> titles = gson.fromJson(me.GetTitles(), type);
            Map<String, Integer> numbers = gson.fromJson(me.AllInts(), inType);
            Map<String, String> strings = gson.fromJson(me.AllStrings(), type);

            if (titles.size() > 0) {
                pc = new PreferenceCategory(ps.getContext());
                pc.setTitle(R.string.titles);
                ps.addPreference(pc);
                for (String s: titles.keySet()) {
                    EditTextPreference p = new EditTextPreference(ps.getContext());
                    p.setTitle(s);
                    p.setKey(s + "/" + me.GenHex(32, "RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR"));
                    p.setEnabled(true);
                    p.setSummary(titles.get(s));
                    p.setDefaultValue(titles.get(s));
                    p.setOnPreferenceChangeListener((preference, newValue) -> {
                        preference.setSummary(newValue.toString());

                        me.SetTitle(preference.getKey().split("/")[0], newValue.toString());
                        saveSettings(bsods, me, os_id);
                        return true;
                    });
                    p.setOnPreferenceClickListener(preference -> {
                        Map<String, String> inTitles = gson.fromJson(me.GetTitles(), type);
                        preference.setDefaultValue(inTitles.get(preference.getKey().split("/")[0]));
                        return false;
                    });
                    pc.addPreference(p);
                }
            }
            if (texts.size() > 0) {
                pc = new PreferenceCategory(ps.getContext());
                pc.setTitle(R.string.texts);
                ps.addPreference(pc);
                for (String s : texts.keySet()) {
                    EditTextPreference p = new EditTextPreference(ps.getContext());
                    p.setTitle(s);
                    p.setKey(s + "/" + me.GenHex(32, "RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR"));
                    p.setEnabled(true);
                    String summary = texts.get(s);
                    if (summary.length() > 50) {
                        summary = summary.substring(0, 50) + "...";
                    }
                    p.setSummary(summary);
                    p.setDefaultValue(texts.get(s));
                    p.setOnPreferenceChangeListener((preference, newValue) -> {
                        String summary2 = newValue.toString();
                        if (summary2.length() > 50) {
                            summary2 = summary2.substring(0, 50) + "...";
                        }
                        preference.setSummary(summary2);
                        preference.setDefaultValue(newValue.toString());
                        me.SetText(preference.getKey().split("/")[0], newValue.toString());
                        saveSettings(bsods, me, os_id);
                        return true;
                    });
                    p.setOnPreferenceClickListener(preference -> {
                        Map<String, String> inTexts = gson.fromJson(me.GetTexts(), type);
                        preference.setDefaultValue(inTexts.get(preference.getKey().split("/")[0]));
                        return false;
                    });
                    pc.addPreference(p);
                }
            }
            if (numbers.size() > 0) {
                pc = new PreferenceCategory(ps.getContext());
                pc.setTitle(R.string.integers);
                ps.addPreference(pc);
                for (String s : numbers.keySet()) {
                    EditTextPreference p = new EditTextPreference(ps.getContext());
                    boolean ignoreSetting = false;
                    switch (s) {
                        case "blink_speed": p.setTitle(R.string.bs); break;
                        case "scale": p.setTitle(R.string.scaling); break;
                        case "qr_size": p.setTitle(R.string.qrsize); break;
                        case "margin-x": p.setTitle(R.string.xmargin); break;
                        case "margin-y": p.setTitle(R.string.ymargin); break;
                        case "progressmillis": ignoreSetting = true; break;
                        default: p.setTitle(s); break;
                    }
                    if (ignoreSetting) {
                        continue;
                    }
                    p.setKey(s + "/" + me.GenHex(32, "RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR"));
                    p.setEnabled(true);
                    p.setSummary(numbers.get(s).toString());
                    p.setDefaultValue(numbers.get(s).toString());
                    p.setOnPreferenceChangeListener((preference, newValue) -> {
                        preference.setSummary(newValue.toString());
                        preference.setDefaultValue(newValue.toString());
                        me.SetInt(preference.getKey().split("/")[0], Integer.parseInt(newValue.toString()));
                        saveSettings(bsods, me, os_id);
                        return true;
                    });
                    p.setOnPreferenceClickListener(preference -> {
                        preference.setDefaultValue(me.GetInt(preference.getKey().split("/")[0]));
                        return false;
                    });
                    pc.addPreference(p);
                }
            }

            pc = new PreferenceCategory(ps.getContext());
            pc.setTitle(R.string.strings);
            ps.addPreference(pc);
            if (strings.size() > 0) {
                for (String s : strings.keySet()) {
                    EditTextPreference p = new EditTextPreference(ps.getContext());
                    boolean ignoreSetting = false;
                    switch (s) {
                        case "screen_mode":
                        case "Screen mode":
                        case "os":
                        case "qr_file":
                            ignoreSetting = true;
                            break;
                        case "emoticon": p.setTitle(R.string.emoticon); break;
                        case "friendlyname": p.setTitle(R.string.template); break;
                        case "code": p.setTitle(R.string.ecode); break;
                        case "culprit":
                            ListPreference lp = new ListPreference(ps.getContext());
                            ArrayList<String> filenames = new ArrayList<>();
                            ArrayList<String> descriptions = new ArrayList<>();
                            for (String val: getResources().getStringArray(R.array.CulpritFiles)) {
                                try {
                                    filenames.add(val.split(":")[0]);
                                    descriptions.add(val.split(":")[1]);
                                } catch (Exception ignored) {

                                }
                            }
                            lp.setEntryValues(filenames.toArray(new String[0]));
                            lp.setEntries(filenames.toArray(new String[0]));
                            lp.setTitle(R.string.culprit);
                            lp.setKey(s  + "/" +  me.GenHex(32, "RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR"));
                            lp.setOnPreferenceChangeListener((preference, newValue) -> {
                                preference.setSummary(newValue.toString());
                                preference.setDefaultValue(newValue.toString());
                                me.SetString("culprit", newValue.toString());
                                saveSettings(bsods, me, os_id);
                                return true;
                            });
                            lp.setOnPreferenceClickListener(preference -> {
                                preference.setDefaultValue(me.GetString(preference.getKey().split("/")[0]));
                                return false;
                            });
                            lp.setDefaultValue(strings.get(s));
                            lp.setSummary(strings.get(s));
                            pc.addPreference(lp);
                            ignoreSetting = true;
                            break;
                        default: p.setTitle(s); break;
                    }
                    if (ignoreSetting) {
                        continue;
                    }
                    p.setKey(s + "/" + me.GenHex(32, "RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR"));
                    p.setEnabled(true);
                    p.setSummary(strings.get(s));
                    p.setDefaultValue(strings.get(s));
                    p.setPersistent(false);
                    p.setOnPreferenceChangeListener((preference, newValue) -> {
                        preference.setSummary(newValue.toString());
                        preference.setDefaultValue(newValue.toString());
                        me.SetString(preference.getKey().split("/")[0], newValue.toString());
                        saveSettings(bsods, me, os_id);
                        return true;
                    });
                    p.setOnPreferenceClickListener(preference -> {
                        preference.setDefaultValue(me.GetString(preference.getKey().split("/")[0]));
                        return false;
                    });
                    pc.addPreference(p);
                }
            }
            if (me.GetBool("font_support")) {
                ListPreference fp = new ListPreference(ps.getContext());
                String path = "/system/fonts";
                File file = new File(path);
                File ff[] = file.listFiles();
                StringBuilder fonts = new StringBuilder();
                fonts.append("Ubuntu").append("\n");
                fonts.append("Ubuntu Light").append("\n");
                fonts.append("Inconsolata").append("\n");
                for (File font : ff) {
                    fonts.append(font.getName().replace(".ttf", "")).append("\n");
                }
                String[] fonts_arr = fonts.toString().split("\n");
                fp.setEntries(fonts_arr);
                fp.setEntryValues(fonts_arr);
                fp.setKey("font" + new Random().nextInt(Integer.MAX_VALUE));
                fp.setTitle(getString(R.string.font));
                fp.setDefaultValue(me.GetFamily());
                fp.setSummary(me.GetFamily());
                fp.setOnPreferenceChangeListener((preference, newValue) -> {
                    me.SetFont(newValue.toString(), me.GetStyle(), me.GetSize());
                    preference.setSummary(newValue.toString());
                    saveSettings(bsods, me, os_id);
                    return true;
                });
                pc.addPreference(fp);
                EditTextPreference ts = new EditTextPreference(ps.getContext());
                ts.setDefaultValue(String.valueOf(me.GetSize()));
                ts.setTitle(getString(R.string.fontsize));
                ts.setSummary(String.valueOf(me.GetSize()));
                ts.setKey("size" + new Random().nextInt(Integer.MAX_VALUE));
                ts.setOnPreferenceChangeListener((preference, newValue) -> {
                    me.SetFont(me.GetFamily(), me.GetStyle(), Float.parseFloat(newValue.toString()));
                    preference.setSummary(newValue.toString());
                    saveSettings(bsods, me, os_id);
                    return true;
                });
                pc.addPreference(ts);
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SwitchPreference ip = new SwitchPreference(ps.getContext());
            ip.setTitle(getString(R.string.immersive));
            ip.setSummary(getString(R.string.immersiveDesc));
            ip.setDefaultValue(sharedPreferences.getBoolean("immersive", false));
            ip.setOnPreferenceChangeListener((preference, newValue) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("immersive", (Boolean) newValue);
                editor.apply();
                return true;
            });
            pc.addPreference(ip);
            SwitchPreference np = new SwitchPreference(ps.getContext());
            np.setTitle(getString(R.string.ignoreNotch));
            np.setSummary(getString(R.string.ignoreNotchDesc));
            np.setDefaultValue(sharedPreferences.getBoolean("ignorenotch", false));
            np.setOnPreferenceChangeListener((preference, newValue) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("ignorenotch", (Boolean) newValue);
                editor.apply();
                return true;
            });
            pc.addPreference(np);
            SwitchPreference ep = new SwitchPreference(ps.getContext());
            ep.setTitle(getString(R.string.enableEgg));
            ep.setSummary(getString(R.string.enableEggDesc));
            ep.setDefaultValue(sharedPreferences.getBoolean("egg", true));
            ep.setOnPreferenceChangeListener((preference, newValue) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("egg", (Boolean) newValue);
                editor.apply();
                return true;
            });
            pc.addPreference(ep);

            pc = new PreferenceCategory(ps.getContext());
            pc.setTitle(R.string.about);
            ps.addPreference(pc);
            Preference p = new Preference(ps.getContext());
            p.setTitle(R.string.devName);
            p.setSummary("Markus Maal");
            Random r = new Random();
            p.setOnPreferenceClickListener(preference -> {
                if (r.nextBoolean()) {
                    preference.setSummary(MakeAnagram(preference.getSummary().toString()));
                }
                return false;
            });
            ps.addPreference(p);
            p = new Preference(ps.getContext());
            p.setTitle(R.string.aboutFonts);
            p.setSummary("Inconsolata\nUbuntu\nUbuntu Light");
            p.setOnPreferenceClickListener(preference -> {
                if (r.nextBoolean()) {
                    preference.setSummary(Shuffle(preference.getSummary().toString()));
                }
                return false;
            });
            ps.addPreference(p);
            p = new Preference(ps.getContext());
            p.setTitle(R.string.aboutSource);
            p.setSummary(R.string.aboutSourceDescription);
            p.setOnPreferenceClickListener(preference -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MarkusMaal/BlueScreenSimulatorPlus-Android"));
                startActivity(browserIntent);
                return false;
            });
            ps.addPreference(p);
            p = new Preference(ps.getContext());
            p.setTitle(R.string.version);
            p.setSummary(BuildConfig.VERSION_NAME);
            p.setOnPreferenceClickListener(preference -> {
                setToast();
                return false;
            });
            ps.addPreference(p);
        }

        public void setToast() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            boolean dev = sharedPreferences.getBoolean("developer", false);
            if (!dev) {
                devProgress += 1;
            } else {
                devProgress = 999;
            }
            if ((devProgress < 8) && (devProgress > 1)) {
                devToast.cancel();
                devToast = Toast.makeText(getContext(), String.format(getString(R.string.devThingie), String.valueOf(8 - devProgress)), Toast.LENGTH_SHORT);
                devToast.show();
            } else if (devProgress < 2) {
                return;
            } else if (devProgress == 8) {
                devToast.cancel();
                devToast = Toast.makeText(getContext(), getString(R.string.devUnlocked), Toast.LENGTH_SHORT);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("developer", true);
                editor.apply();
                devToast.show();
            } else {
                devToast.cancel();
                devToast = Toast.makeText(getContext(), getString(R.string.devNoNeed), Toast.LENGTH_SHORT);
                devToast.show();
            }
        }

        public String Shuffle(String original) {
            List<String> originals = List.of(original.split("\n"));
            StringBuilder newList = new StringBuilder();
            Random r = new Random();
            while (newList.length() < original.length()) {
                String font = originals.get(r.nextInt(originals.size()));
                int loopcount = 0;
                while (newList.toString().contains(font)) {
                    font = originals.get(r.nextInt(originals.size()));
                    loopcount ++;
                    if (loopcount > 100) {
                        return original;
                    }
                }
                if (!font.equals("")) {
                    if (newList.chars().filter(ch -> ch == '\n').count() < original.split("\n").length - 1) {
                        newList.append(font).append("\n");
                    } else{
                        newList.append(font);
                    }
                }
            }
            return newList.toString();
        }

        public String MakeAnagram(String original) {
            List<Character> original_letters = original.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
            StringBuilder newName = new StringBuilder();
            Random r = new Random();
            while (original_letters.size() > 0) {
                Character letter = original_letters.get(r.nextInt(original_letters.size()));
                if (r.nextBoolean()) {
                    newName.append(letter.toString().toUpperCase());
                } else {
                    newName.append(letter.toString().toLowerCase());
                }
                original_letters.remove(letter);
            }
            return newName.toString();
        }

        public void saveSettings(List<BlueScreen> blues, BlueScreen modified, long id) {
            blues.set((int)id, modified);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            Gson gson = new Gson();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String json = gson.toJson(blues);
            editor.putString("bluescreens", json);
            editor.apply();
        }
    }
}