package eu.markustegelane.bssp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import eu.markustegelane.bssp.databinding.FragmentFirstBinding;

public class StringEdit extends AppCompatActivity {

    private FragmentFirstBinding binding;
    BlueScreen os;
    List<BlueScreen> bsods;
    int os_id;
    Boolean locked = false;
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_string_edit);
        Bundle bundle = getIntent().getExtras();
        Toolbar titleBar = findViewById(R.id.titleBar);
        os = (BlueScreen) bundle.getSerializable("bluescreen");
        os_id = bundle.getInt("bluescreen_id");
        bsods = (List<BlueScreen>)bundle.getSerializable("bluescreens");
        titleBar.setTitle(os.GetString("friendlyname"));
        Spinner slist = findViewById(R.id.settingList);
        RefreshSpinner(os);
        findViewById(R.id.okButton).setOnClickListener(v -> {
            Intent firstFrg = new Intent(v.getContext(), MainActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("feedback", os);
            b.putInt("id", os_id);
            firstFrg.putExtras(b);
            startActivity(firstFrg);
            setResult(RESULT_OK, firstFrg);
            finish();
        });

        findViewById(R.id.removeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedItem = ((Spinner)findViewById(R.id.settingList)).getSelectedItem().toString();
                String[] fullnamearr = selectedItem.split(" ");
                List<String> ls = new ArrayList<>(Arrays.asList(fullnamearr));
                String key = "";
                key = String.join(" ", ls.subList(0, ls.size() - 1));
                String type = ls.get(ls.size() - 1);
                switch (type) {
                    case "[boolean]":
                        os.DelBool(key);
                        break;
                    case "[integer]":
                        os.DelInt(key);
                        break;
                    case "[string]":
                        os.DelString(key);
                        break;
                    case "[text]":
                        os.DelText(key);
                        break;
                    case "[title]":
                        os.DelTitle(key);
                        break;
                }
                saveSettings(bsods, os, os_id);
                bsods.set(os_id, os);
                RefreshSpinner(os);
            }
        });

        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                View hashtable_maker = LayoutInflater.from(view.getContext()).inflate(R.layout.add_hashtable, null);
                ((Spinner)hashtable_maker.findViewById(R.id.typeSpinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        switch (adapterView.getItemAtPosition(i).toString()) {
                            case "string":
                            case "titles":
                            case "texts":
                                hashtable_maker.findViewById(R.id.stringEditText).setVisibility(View.VISIBLE);
                                hashtable_maker.findViewById(R.id.integerEditText).setVisibility(View.GONE);
                                hashtable_maker.findViewById(R.id.booleanSwitch).setVisibility(View.GONE);
                                break;
                            case "integer":
                                hashtable_maker.findViewById(R.id.stringEditText).setVisibility(View.GONE);
                                hashtable_maker.findViewById(R.id.integerEditText).setVisibility(View.VISIBLE);
                                hashtable_maker.findViewById(R.id.booleanSwitch).setVisibility(View.GONE);
                                break;
                            case "boolean":
                                hashtable_maker.findViewById(R.id.stringEditText).setVisibility(View.GONE);
                                hashtable_maker.findViewById(R.id.integerEditText).setVisibility(View.GONE);
                                hashtable_maker.findViewById(R.id.booleanSwitch).setVisibility(View.VISIBLE);
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Spinner spin = hashtable_maker.findViewById(R.id.typeSpinner);
                        String key = ((EditText)hashtable_maker.findViewById(R.id.keyText)).getText().toString();
                        String strValue = ((EditText)hashtable_maker.findViewById(R.id.stringEditText)).getText().toString();
                        switch (spin.getAdapter().getItem((int)spin.getSelectedItemId()).toString()) {
                            case "string":
                                os.SetString(key, strValue);
                                break;
                            case "titles":
                                os.PushTitle(key, strValue);
                                break;
                            case "texts":
                                os.PushText(key, strValue);
                                break;
                            case "integer":
                                int intValue = Integer.parseInt(((EditText)hashtable_maker.findViewById(R.id.integerEditText)).getText().toString());
                                os.SetInt(key, intValue);
                                break;
                            case "boolean":
                                boolean boolValue = ((Switch)hashtable_maker.findViewById(R.id.booleanSwitch)).isChecked();
                                os.SetBool(key, boolValue);
                                break;
                        }
                        saveSettings(bsods, os, os_id);
                        bsods.set(os_id, os);

                        RefreshSpinner(os);
                    }
                });
                builder.setCancelable(false);
                builder.setView(hashtable_maker);
                AlertDialog ad = builder.create();
                ad.show();
            }
        });

        ((Switch)findViewById(R.id.boolValue)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!locked) {
                    String selectedItem = ((Spinner)findViewById(R.id.settingList)).getSelectedItem().toString();
                    String[] fullnamearr = selectedItem.split(" ");
                    List<String> ls = new ArrayList<>(Arrays.asList(fullnamearr));
                    String key = "";
                    key = String.join(" ", ls.subList(0, ls.size() - 1));
                    String type = ls.get(ls.size() - 1);
                    switch (type) {
                        case "[boolean]":
                            os.SetBool(key, b);
                            break;
                        default:
                            break;
                    }
                    saveSettings(bsods, os, os_id);
                }
            }
        });

        ((EditText)findViewById(R.id.integerValue)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!locked) {
                    String selectedItem = ((Spinner)findViewById(R.id.settingList)).getSelectedItem().toString();
                    String[] fullnamearr = selectedItem.split(" ");
                    List<String> ls = new ArrayList<>(Arrays.asList(fullnamearr));
                    String key = String.join(" ", ls.subList(0, ls.size() - 1));
                    String type = ls.get(ls.size() - 1);
                    try {
                        if (type.equals("[integer]")) {
                            os.SetInt(key, Integer.parseInt(editable.toString()));
                        }
                    } catch (Exception ignored) {

                    }
                    saveSettings(bsods, os, os_id);
                }
            }
        });
        ((EditText)findViewById(R.id.stringValue)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!locked) {
                    String selectedItem = ((Spinner)findViewById(R.id.settingList)).getSelectedItem().toString();
                    String[] fullnamearr = selectedItem.split(" ");
                    List<String> ls = new ArrayList<>(Arrays.asList(fullnamearr));
                    String key = "";
                    key = String.join(" ", ls.subList(0, ls.size() - 1));
                    String type = ls.get(ls.size() - 1);
                    switch (type) {
                        case "[string]":
                            os.SetString(key, editable.toString());
                            break;
                        case "[text]":
                            os.SetText(key, editable.toString());
                            break;
                        case "[title]":
                            os.SetTitle(key, editable.toString());
                            break;
                        default:
                            break;
                    }
                    saveSettings(bsods, os, os_id);
                }
            }
        });
        slist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                locked = true;
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                String[] fullnamearr = selectedItem.split(" ");
                List<String> ls = new ArrayList<>(Arrays.asList(fullnamearr));
                String key = "";
                key = String.join(" ", ls.subList(0, ls.size() - 1));
                String type = ls.get(ls.size() - 1);
                Gson gson = new Gson();
                Type type2 = new TypeToken<Map<String, String>>(){}.getType();
                switch (type) {
                    case "[string]":
                        findViewById(R.id.boolValue).setVisibility(View.GONE);
                        findViewById(R.id.stringValue).setVisibility(View.VISIBLE);
                        findViewById(R.id.integerValue).setVisibility(View.GONE);
                        ((EditText)findViewById(R.id.stringValue)).setText(os.GetString(key));
                        break;
                    case "[text]":
                        findViewById(R.id.boolValue).setVisibility(View.GONE);
                        findViewById(R.id.stringValue).setVisibility(View.VISIBLE);
                        findViewById(R.id.integerValue).setVisibility(View.GONE);
                        ((EditText)findViewById(R.id.stringValue)).setText(((Map<String, String>)gson.fromJson(os.GetTexts(), type2)).get(key));
                        break;
                    case "[title]":
                        findViewById(R.id.boolValue).setVisibility(View.GONE);
                        findViewById(R.id.stringValue).setVisibility(View.VISIBLE);
                        findViewById(R.id.integerValue).setVisibility(View.GONE);
                        ((EditText)findViewById(R.id.stringValue)).setText(((Map<String, String>)gson.fromJson(os.GetTitles(), type2)).get(key));
                        break;
                    case "[integer]":
                        findViewById(R.id.boolValue).setVisibility(View.GONE);
                        findViewById(R.id.stringValue).setVisibility(View.GONE);
                        findViewById(R.id.integerValue).setVisibility(View.VISIBLE);
                        ((EditText)findViewById(R.id.integerValue)).setText(String.valueOf(os.GetInt(key)));
                        break;
                    case "[boolean]":
                        findViewById(R.id.boolValue).setVisibility(View.VISIBLE);
                        findViewById(R.id.stringValue).setVisibility(View.GONE);
                        findViewById(R.id.integerValue).setVisibility(View.GONE);
                        ((Switch)findViewById(R.id.boolValue)).setText(key);
                        ((Switch)findViewById(R.id.boolValue)).setChecked(os.GetBool(key));
                        break;
                    default:
                        findViewById(R.id.boolValue).setVisibility(View.GONE);
                        findViewById(R.id.stringValue).setVisibility(View.GONE);
                }
                locked = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressWarnings("unchecked")
    private void RefreshSpinner(BlueScreen os) {
        Spinner slist = findViewById(R.id.settingList);
        List<String> strings = new ArrayList<String>();
        Gson gson = new Gson();
        Type typeStringString = new TypeToken<Map<String, String>>(){}.getType();
        Type typeStringBool = new TypeToken<Map<String, Boolean>>(){}.getType();
        Type typeStringInteger = new TypeToken<Map<String, Integer>>(){}.getType();
        ((Map<String, String>) gson.fromJson(os.AllStrings(), typeStringString)).forEach((k, value) -> strings.add(k + " [string]"));
        ((Map<String, String>) gson.fromJson(os.GetTitles(), typeStringString)).forEach((k, value) -> strings.add(k + " [title]"));
        ((Map<String, String>) gson.fromJson(os.GetTexts(), typeStringString)).forEach((k, value) -> strings.add(k + " [text]"));
        ((Map<String, Boolean>) gson.fromJson(os.AllBools(), typeStringBool)).forEach((k, value) -> strings.add(k + " [boolean]"));
        ((Map<String, Integer>) gson.fromJson(os.AllInts(), typeStringInteger)).forEach((k, value) -> strings.add(k + " [integer]"));

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(getWindow().getContext(), android.R.layout.simple_list_item_1, strings);
        slist.setAdapter(catAdapter);
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
}