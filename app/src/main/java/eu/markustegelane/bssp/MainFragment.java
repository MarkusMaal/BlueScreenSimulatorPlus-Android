package eu.markustegelane.bssp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import eu.markustegelane.bssp.databinding.FragmentFirstBinding;
public class MainFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentFirstBinding binding;

    public List<BlueScreen> bluescreens = new ArrayList<>();

    private Boolean locked = false;

    boolean hasFileAccess = true;

    boolean developer = false;
    boolean immersive = false;
    boolean notch = false;

    boolean egg = true;

    boolean nearest = false;

    Random r = new Random();

    BlueScreen os;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("eu.markustegelane.bssp", Context.MODE_PRIVATE);
        developer = sharedPreferences.getBoolean("developer", false);
        immersive = sharedPreferences.getBoolean("immersive", false);
        notch = sharedPreferences.getBoolean("ignorenotch", false);
        egg = sharedPreferences.getBoolean("egg", true);
        nearest = sharedPreferences.getBoolean("nearestscaling", false);
        Gson gson = new Gson();
        if (sharedPreferences.getString("bluescreens", null) == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            bluescreens.add(new BlueScreen("Windows 1.x/2.x", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows 3.1x", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows 9x/Me", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows CE", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows NT 3.1", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows NT 3.x/4.0", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows 2000", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows XP", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows Vista", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows 7", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows 8 Beta", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows 8/8.1", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows 10", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows 11", true, getActivity()));
            String json = gson.toJson(bluescreens);
            editor.putString("bluescreens", json);
            editor.apply();
        } else {
            String json = sharedPreferences.getString("bluescreens", null);
            BlueScreen[] bss = gson.fromJson(json, BlueScreen[].class);
            bluescreens.addAll(Arrays.asList(bss));
        }
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get reference to the spinner
        Bundle pb = getActivity().getIntent().getExtras();


        SharedPreferences sharedPreferences = getContext().getSharedPreferences("eu.markustegelane.bssp", 0);
        int selection = 0;
        if (pb != null) {
            selection = pb.getInt("id");
        } else if (sharedPreferences.getInt("selectedItem", -1) != -1) {
            selection = sharedPreferences.getInt("selectedItem", -1);
        }
        if (selection > bluescreens.size() - 1) {
            selection = bluescreens.size() - 1;
        }
        Spinner winspin = binding.winSpinner;
        if (sharedPreferences.getBoolean("developer", false)) {
            binding.devOpsText.setVisibility(View.VISIBLE);
            binding.devOps.setVisibility(View.VISIBLE);
            System.out.println("******** DEVELOPER MODE ACTIVE ********");
        }

        List<String> friendlyNames = new ArrayList<>();
        for (BlueScreen element : bluescreens) {
            friendlyNames.add(element.GetString("friendlyname"));
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> catAdapter;
        catAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, friendlyNames);


        // Apply the adapter to the spinner
        winspin.setAdapter(catAdapter);

        // Set the item selected listener
        winspin.setOnItemSelectedListener(this);

        // Set selection
        winspin.setSelection(selection);

        binding.dispJson.setOnClickListener(view2 -> {
            Intent jd = new Intent(view2.getContext(), devTextDisplay.class);
            Bundle b = new Bundle();
            Gson gson = new Gson();
            b.putString("json", gson.toJson(bluescreens));
            jd.putExtras(b);
            startActivity(jd);
        });

        binding.playSound.setOnCheckedChangeListener((compoundButton, b) -> {
            if ((!locked) && (os != null)) {
                os.SetBool("playsound", b);
                saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
            }
        });

        binding.deviceCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            if ((!locked) && (os != null)) {
                os.SetBool("device", b);
                saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
            }
        });
        getActivity().findViewById(R.id.luckyFloater).setOnClickListener(view117 -> {
            String[] oses = getResources().getStringArray(R.array.OperatingSystems);
            BlueScreen unlucky = new BlueScreen(oses[r.nextInt(oses.length)], true, getActivity());
            while (unlucky.GetString("os").equals("BOOTMGR")) {
                unlucky = new BlueScreen(oses[r.nextInt(oses.length)], true, getActivity());
            }
            unlucky.Shuffle();
            Intent i;
            Bundle b;
            switch (unlucky.GetString("os")) {
                case "Windows 8/8.1":
                case "Windows 10":
                case "Windows 11":
                    i = new Intent(view117.getContext(), ModernBSOD.class);
                    b = new Bundle();
                    b.putSerializable("bluescreen", unlucky);
                    b.putBoolean("immersive", immersive);
                    b.putBoolean("ignorenotch", notch);
                    b.putBoolean("egg", egg);
                    i.putExtras(b);
                    startActivity(i);
                    break;
                case "Windows 1.x/2.x":
                case "Windows 3.1x":
                case "Windows 9x/Me":
                case "Windows NT 3.x/4.0":
                case "Windows 2000":
                case "Windows CE":
                case "Windows XP":
                case "Windows Vista":
                case "Windows 7":
                case "BOOTMGR":
                    i = new Intent(view117.getContext(), LegacyBSOD.class);
                    b = new Bundle();
                    b.putSerializable("bluescreen", unlucky);
                    b.putBoolean("immersive", immersive);
                    b.putBoolean("ignorenotch", notch);
                    b.putBoolean("egg", egg);
                    b.putBoolean("nearestscaling", nearest);
                    i.putExtras(b);
                    startActivity(i);
                    break;
                default:
                    Toast.makeText(getContext(), "Unknown OS: " + unlucky.GetString("os"), Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        getActivity().findViewById(R.id.simulateFloater).setOnClickListener(view1 -> {
            if (egg && !os.GetString("Karrots").isEmpty()) { Toast.makeText(getContext(), "Karrots are good for your eyesight", Toast.LENGTH_SHORT).show(); }
            Intent i;
            Bundle b;
            BlueScreen me;
            switch (bluescreens.get((int)winspin.getSelectedItemId()).GetString("os")) {
                case "Windows 8/8.1":
                case "Windows 10":
                case "Windows 11":
                    i = new Intent(view1.getContext(), ModernBSOD.class);
                    b = new Bundle();
                    me = bluescreens.get((int)winspin.getSelectedItemId());
                    b.putSerializable("bluescreen", me);
                    b.putBoolean("immersive", immersive);
                    b.putBoolean("ignorenotch", notch);
                    b.putBoolean("egg", egg);
                    i.putExtras(b);
                    startActivity(i);
                    break;
                case "Windows 1.x/2.x":
                case "Windows 3.1x":
                case "Windows 9x/Me":
                case "Windows NT 3.x/4.0":
                case "Windows 2000":
                case "Windows CE":
                case "Windows XP":
                case "Windows Vista":
                case "Windows 7":
                    i = new Intent(view1.getContext(), LegacyBSOD.class);
                    b = new Bundle();
                    me = bluescreens.get((int)winspin.getSelectedItemId());
                    b.putSerializable("bluescreen", me);
                    b.putBoolean("immersive", immersive);
                    b.putBoolean("ignorenotch", notch);
                    b.putBoolean("egg", egg);
                    b.putBoolean("nearestscaling", nearest);
                    i.putExtras(b);
                    startActivity(i);
                    break;
                case "BOOTMGR":
                    i = new Intent(view1.getContext(), BOOTMGR.class);
                    b = new Bundle();
                    me = bluescreens.get((int)winspin.getSelectedItemId());
                    b.putSerializable("bluescreen", me);
                    b.putBoolean("immersive", immersive);
                    b.putBoolean("ignorenotch", notch);
                    b.putBoolean("egg", egg);
                    b.putBoolean("nearestscaling", nearest);
                    i.putExtras(b);
                    startActivity(i);
                    break;
                default:
                    NotImplemented();
                    break;
            }
        });

        binding.settingsButton.setOnClickListener(view1 -> {
            Intent s = new Intent(view1.getContext(), StringEdit.class);
            Bundle b = new Bundle();
            BlueScreen me = bluescreens.get((int)winspin.getSelectedItemId());
            b.putSerializable("bluescreen", me);
            b.putInt("bluescreen_id", (int) binding.winSpinner.getSelectedItemId());
            b.putSerializable("bluescreens", (Serializable) bluescreens);
            s.putExtras(b);
            startActivity(s);
        });

        binding.autoCloseCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            if ((!locked) && (os != null)) {
                try {
                    os.SetBool("autoclose", b);
                    saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
                } catch (Exception ignored) {
                    Intent i = getActivity().getIntent();
                    getActivity().finish();
                    startActivity(i);
                }
            }
        });
        binding.insiderCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            if ((!locked) && (os != null)) {
                os.SetBool("green", b);
                saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
            }
        });
        binding.showDetailsCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            if ((!locked) && (os != null)) {
                os.SetBool("show_description", b);
                saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
            }
        });

        binding.showParCodes.setOnCheckedChangeListener((compoundButton, b) -> {
            if ((!locked) && (os != null)) {
                os.SetBool("extracodes", b);
                saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
            }
        });

        binding.serverScreen.setOnCheckedChangeListener((compoundButton, b) -> {
            if ((!locked) && (os != null)) {
                os.SetBool("server", b);
                saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
            }
        });

        binding.rainbowCheck.setOnCheckedChangeListener(((compoundButton, b) -> {
            if ((!locked) && (os != null)) {
                os.SetBool("rainbow", b);
                saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
            }
        }));

        binding.moreFileInfoCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            if ((!locked) && (os != null)) {
                os.SetBool("extrafile", b);
                if (os.GetText("Culprit file memory address").isEmpty()) {
                    os.PushText("Culprit file memory address", "***  %s - Address %s base at %s, DateStamp %s");
                }
                saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
            }
        });

        binding.showWatermark.setOnCheckedChangeListener((compoundButton, b) -> {
            if ((!locked) && (os != null)) {
                os.SetBool("watermark", b);
                saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
            }
        });

        binding.nineXSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if ((!locked) && (os != null)) {
                    os.SetString("Screen mode", adapterView.getItemAtPosition(i).toString());
                    saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        
        binding.ecodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if ((!locked) && (os != null) && (binding.ecodeSpinner.getVisibility() == View.VISIBLE)) {
                    os.SetString("code", adapterView.getItemAtPosition(i).toString());
                    saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.oneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if ((!locked) && (os != null)) {
                    if (adapterView.getItemAtPosition(i).toString().contains("Windows 1.01")) {
                        os.SetString("qr_file", "local:0");
                    } else if (adapterView.getItemAtPosition(i).toString().contains("Windows 2.03")) {
                        os.SetString("qr_file", "local:1");
                    } else {
                        os.SetString("qr_file", "local:null");
                    }
                    saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        DialogInterface.OnClickListener delPresetListener = (dialogInterface, i) -> {
            switch (i) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (bluescreens.size() > 1) {
                        bluescreens.remove(bluescreens.get((int) winspin.getSelectedItemId()));
                        saveSettings(bluescreens, bluescreens.get(0), 0);
                        Toast.makeText(getContext(), getString(R.string.presetDelFinished), Toast.LENGTH_SHORT).show();
                        Intent mi = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(mi);
                        getActivity().finish();
                        break;
                    }
                    Toast.makeText(getContext(), getString(R.string.cannotDel), Toast.LENGTH_SHORT).show();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    locked = true;
                    bluescreens.clear();
                    SharedPreferences sharedPreferences1 = getContext().getSharedPreferences("eu.markustegelane.bssp", Context.MODE_PRIVATE);
                    Gson gson = new Gson();
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    bluescreens.add(new BlueScreen("Windows 1.x/2.x", true, getActivity()));
                    bluescreens.add(new BlueScreen("Windows 3.1x", true, getActivity()));
                    bluescreens.add(new BlueScreen("Windows 9x/Me", true, getActivity()));
                    bluescreens.add(new BlueScreen("Windows CE", true, getActivity()));
                    bluescreens.add(new BlueScreen("Windows NT 3.1", true, getActivity()));
                    bluescreens.add(new BlueScreen("Windows NT 3.x/4.0", true, getActivity()));
                    bluescreens.add(new BlueScreen("Windows 2000", true, getActivity()));
                    bluescreens.add(new BlueScreen("Windows XP", true, getActivity()));
                    bluescreens.add(new BlueScreen("Windows Vista", true, getActivity()));
                    bluescreens.add(new BlueScreen("Windows 7", true, getActivity()));
                    bluescreens.add(new BlueScreen("Windows 8 Beta", true, getActivity()));
                    bluescreens.add(new BlueScreen("Windows 8/8.1", true, getActivity()));
                    bluescreens.add(new BlueScreen("Windows 10", true, getActivity()));
                    bluescreens.add(new BlueScreen("Windows 11", true, getActivity()));
                    String json = gson.toJson(bluescreens);
                    editor.putString("bluescreens", json);
                    editor.putInt("selectedItem", 0);
                    editor.apply();
                    Toast.makeText(getContext(), getString(R.string.resetFinished), Toast.LENGTH_SHORT).show();
                    Intent i = getActivity().getIntent();
                    getActivity().finish();
                    startActivity(i);
                    getActivity().finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    Toast.makeText(getContext(), getString(R.string.noChange), Toast.LENGTH_SHORT).show();
                    break;
            }
        };
        binding.culpritCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            if ((!locked) && (os != null)) {
                os.SetBool("show_file", b);
                if (b) {
                    binding.setCulpritButton.setVisibility(View.VISIBLE);
                } else {
                    binding.setCulpritButton.setVisibility(View.GONE);
                }
                if (b) {
                    Map<String, String[]> cfiles;
                    Type arrayType = new TypeToken<Map<String, String[]>>() {
                    }.getType();
                    Gson gson = new Gson();
                    cfiles = gson.fromJson(os.GetFiles(), arrayType);
                    String[] files = getActivity().getResources().getStringArray(R.array.CulpritFiles);
                    List<String> filenames = new ArrayList<>();
                    for (String line : files) {
                        filenames.add(line.split(":")[0]);
                    }
                    if ((cfiles.isEmpty()) || os.GetString("os").equals("Windows NT 3.x/4.0")) {
                        int temp = r.nextInt(filenames.size() - 1);
                        os.SetString("culprit", filenames.get(temp));
                        os.RenameFile("", filenames.get(temp));
                    } else {
                        os.SetString("culprit", cfiles.entrySet().iterator().next().getKey());
                    }
                }
                saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
            }
        });

        binding.setCulpritButton.setOnClickListener(view116 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View culpritView = LayoutInflater.from(getContext()).inflate(R.layout.culprit_chooser, null);
            final RadioButton cust = culpritView.findViewById(R.id.customRadio);
            final RadioButton preset = culpritView.findViewById(R.id.presetRadio);
            final EditText cfile = culpritView.findViewById(R.id.eFileEditText);
            final Spinner fileSelect = culpritView.findViewById(R.id.eFileSpinner);
            String culprit_file = os.GetString("culprit");
            boolean useCustom = true;
            for (int i = 0; i < fileSelect.getAdapter().getCount(); i++) {
                String current_file = fileSelect.getAdapter().getItem(i).toString().split(":")[0];
                if (culprit_file.equals(current_file)) {
                    fileSelect.setSelection(i);
                    useCustom = false;
                    break;
                }
            }
            if (useCustom) {
                fileSelect.setVisibility(View.GONE);
                cfile.setText(culprit_file);
                cfile.setVisibility(View.VISIBLE);
                preset.setChecked(false);
                cust.setChecked(true);
            }

            cust.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    cfile.setVisibility(View.VISIBLE);
                    fileSelect.setVisibility(View.GONE);
                    cfile.setText(fileSelect.getAdapter().getItem((int)fileSelect.getSelectedItemId()).toString().split(":")[0]);
                } else {
                    cfile.setVisibility(View.GONE);
                    fileSelect.setVisibility(View.VISIBLE);
                    String selectedFile = cfile.getText().toString();
                    for (int i = 0; i < fileSelect.getAdapter().getCount(); i++) {
                        String currentFile = fileSelect.getAdapter().getItem(i).toString();
                        if (selectedFile.equals(currentFile.split(":")[0])) {
                            fileSelect.setSelection(i);
                            break;
                        }
                    }
                }
            });
            builder.setView(culpritView);
            builder.setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                        if (cust.isChecked()) {
                            os.SetString("culprit", cfile.getText().toString());
                        } else {
                            os.SetString("culprit", fileSelect.getAdapter().getItem((int)fileSelect.getSelectedItemId()).toString().split(":")[0]);
                        }
                        if ((os.GetString("os").equals("Windows XP")) || (os.GetString("os").equals("Windows Vista")) || (os.GetString("os").equals("Windows 7")) || (os.GetString("os").equals("Windows 2000"))) {

                            Map <String, String[]> files;
                            Type arrayType = new TypeToken<Map<String, String[]>>() {}.getType();
                            Gson gson = new Gson();
                            files = gson.fromJson(os.GetFiles(), arrayType);
                            if (files.keySet().stream().findFirst().isPresent()) {
                                os.RenameFile(files.keySet().stream().findFirst().get(), os.GetString("culprit"));
                            }
                        }
                        saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
                    })
                    .setNegativeButton(getString(R.string.cancel), null);
            AlertDialog ad = builder.create();
            ad.show();
        });
        binding.blinkCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            if ((!locked) && (os != null)) {
                os.SetBool("blinkblink", b);
                saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
            }
        });
        binding.amdProcessorCheck.setOnCheckedChangeListener(((compoundButton, b) -> {
            if ((!locked) && (os != null)) {
                os.SetBool("amd", b);
                saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
            }
        }));
        binding.stackTraceCheck.setOnCheckedChangeListener(((compoundButton, b) -> {
            if ((!locked) && (os != null)) {
                os.SetBool("stack_trace", b);
                saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
            }
        }));
        binding.codeEditButton.setOnClickListener(view12 -> {
            Intent i = new Intent(getContext(), CodeEdit.class);
            Bundle b = new Bundle();
            BlueScreen me = bluescreens.get((int)winspin.getSelectedItemId());
            b.putSerializable("bluescreen", me);
            b.putInt("bluescreen_id", (int) binding.winSpinner.getSelectedItemId());
            b.putSerializable("bluescreens", (Serializable) bluescreens);
            i.putExtras(b);
            startActivity(i);
            getActivity().finish();
        });

        binding.progressEditor.setOnClickListener(view13 -> {
            Intent i = new Intent(getContext(), ErrorCodeEditor.class);
            Bundle b = new Bundle();
            BlueScreen me = bluescreens.get((int)winspin.getSelectedItemId());
            b.putSerializable("bluescreen", me);
            b.putInt("bluescreen_id", (int)binding.winSpinner.getSelectedItemId());
            b.putSerializable("bluescreens", (Serializable) bluescreens);
            i.putExtras(b);
            startActivity(i);
            getActivity().finish();
        });

        binding.ntCodeEditButton.setOnClickListener(view14 -> {
            Intent nt = new Intent(getContext(), NTCodeEdit.class);
            Bundle b =  new Bundle();
            BlueScreen me = bluescreens.get((int)winspin.getSelectedItemId());
            b.putSerializable("bluescreen", me);
            b.putInt("bluescreen_id", (int) binding.winSpinner.getSelectedItemId());
            b.putSerializable("bluescreens", (Serializable) bluescreens);
            nt.putExtras(b);
            startActivity(nt);
            getActivity().finish();
        });

        binding.textForeground.setOnClickListener(view115 -> ColorChooser(os.GetTheme(false, false), String.format(getString(R.string.selectColor), getString(R.string.foregroundCol)), false, false));

        binding.textBackground.setOnClickListener(view114 -> ColorChooser(os.GetTheme(true, false), String.format(getString(R.string.selectColor), getString(R.string.backgroundCol)), true, false));

        binding.hlForeground.setOnClickListener(view113 -> ColorChooser(os.GetTheme(false, true), String.format(getString(R.string.selectColor), String.format("%s %s", getString(R.string.highlight_), getString(R.string.foregroundCol))), false, true));

        binding.hlBackground.setOnClickListener(view112 -> ColorChooser(os.GetTheme(true, true), String.format(getString(R.string.selectColor), String.format("%s %s", getString(R.string.highlight_), getString(R.string.backgroundCol))), true, true));

        binding.delPreset.setOnClickListener(view111 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            TypedValue tv = new TypedValue();
            getActivity().getTheme().resolveAttribute(android.R.attr.alertDialogIcon, tv, true);
            builder.setMessage(getString(R.string.delConfigWarning)).setPositiveButton(getString(R.string.yes), delPresetListener)
                    .setNegativeButton(getString(R.string.no), delPresetListener).setTitle(getString(R.string.delConfig)).setIcon(tv.resourceId).show();
        });

        binding.addPreset.setOnClickListener(view110 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            TypedValue tv = new TypedValue();
            View new_view = LayoutInflater.from(getContext()).inflate(R.layout.new_view, null);
            final Spinner templateSelector = new_view.findViewById(R.id.osSpinner);
            final Spinner osSelector = new_view.findViewById(R.id.osSpinner2);
            final EditText friendlyText = new_view.findViewById(R.id.editTextTemplate);
            final TextView tv4 = new_view.findViewById(R.id.textView4);
            @SuppressLint("UseSwitchCompatOrMaterialCode")
            final Switch allowCust = new_view.findViewById(R.id.allowCust);
            final String[] friendlyTemplateNames = new String[] {"Windows 11 (Native, ClearType)", "Windows 10 (Native, ClearType)",
                    "Windows 8/8.1 (Native, ClearType)", "Windows 7 (640x480, ClearType)", "Windows Vista (640x480, Standard)",
                    "Windows XP (640x480, Standard)", "Windows CE 5.0 and later (750x400, Standard)",
                    "Windows 2000 Professional/Server Family (640x480, Standard)", "Windows NT 4.0/3.x (Text mode, Standard)",
                    "Windows 9x/Millennium Edition (Text mode, Standard)", "Windows 3.1 (Text mode, Standard)",
                    "Windows 1.x/2.x (Text mode, Standard)", "Windows Boot Manager (1024x768, ClearType)"};
            templateSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view110, int i, long l) {
                    if (!allowCust.isChecked()) {
                        osSelector.setSelection(i);
                        friendlyText.setText(friendlyTemplateNames[i]);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            allowCust.setOnCheckedChangeListener((compoundButton, b) -> {
                if (!b) {
                    osSelector.setVisibility(View.GONE);
                    tv4.setVisibility(View.GONE);
                    osSelector.setSelection((int)templateSelector.getSelectedItemId());
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                    TypedValue tv1 = new TypedValue();
                    getActivity().getTheme().resolveAttribute(android.R.attr.alertDialogIcon, tv1, true);
                    builder1.setMessage(getString(R.string.hybridWarn)).setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        osSelector.setVisibility(View.VISIBLE);
                        tv4.setVisibility(View.VISIBLE);
                    })
                            .setNegativeButton(getString(R.string.no), (dialogInterface, i) -> compoundButton.setChecked(false)).setIcon(tv1.resourceId).setTitle(getString(R.string.danger)).show();
                }
            });
            getActivity().getTheme().resolveAttribute(android.R.attr.dialogIcon, tv, true);
            builder.setView(new_view);
            builder.setCancelable(false)
                    .setPositiveButton(getString(R.string.ok),
                            (dialogInterface, i) -> {
                                BlueScreen newScreen = new BlueScreen(templateSelector.getAdapter().getItem((int)templateSelector.getSelectedItemId()).toString(), false, getActivity());
                                newScreen.os = osSelector.getAdapter().getItem((int)templateSelector.getSelectedItemId()).toString();
                                newScreen.SetOSSpecificDefaults();
                                newScreen.os = osSelector.getAdapter().getItem((int)osSelector.getSelectedItemId()).toString();
                                newScreen.SetString("friendlyname", friendlyText.getText().toString());
                                bluescreens.add(newScreen);
                                saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
                                Intent in = new Intent(getContext(), MainActivity.class);
                                startActivity(in);
                                getActivity().overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_enter_anim, androidx.navigation.ui.R.anim.nav_default_exit_anim);
                                getActivity().finish();
                            })
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {

                    });

            AlertDialog ad = builder.create();
            ad.show();
        });

        binding.resetHacks.setOnClickListener(view19 -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            TypedValue tv = new TypedValue();
            getActivity().getTheme().resolveAttribute(android.R.attr.alertDialogIcon, tv, true);
            builder.setMessage(getString(R.string.currentReset)).setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                String backupName = os.GetString("friendlyname");
                bluescreens.set((int)winspin.getSelectedItemId(), new BlueScreen(os.GetString("os"), true, getActivity()));
                os = bluescreens.get((int)winspin.getSelectedItemId());
                os.SetString("friendlyname", backupName);
                saveSettings(bluescreens, os, winspin.getSelectedItemId());
                Intent in = new Intent(getContext(), MainActivity.class);
                startActivity(in);
                getActivity().overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_enter_anim, androidx.navigation.ui.R.anim.nav_default_exit_anim);
                getActivity().finish();
            })
                    .setNegativeButton(getString(R.string.no), dialogClickListener).setIcon(tv.resourceId).setTitle(getString(R.string.resetAllConfig)).show();
        });

        binding.loadConfig.setOnClickListener(view18 -> {
            if (hasFileAccess) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory().toString()), "*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Open .."), 21);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                TypedValue tv = new TypedValue();
                getActivity().getTheme().resolveAttribute(android.R.attr.alertDialogIcon, tv, true);
                builder.setMessage("Couldn't access the file, because file permissions weren't granted. Re-launch or check app settings.").setPositiveButton(getString(R.string.ok), null)
                        .setIcon(tv.resourceId).setTitle("Permission denied").show();
            }
        });

        binding.saveConfig.setOnClickListener(view17 -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory().toString()), "text/bs2cfg");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Save as .."), 22);
        });

        binding.resetAll.setOnClickListener(view16 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            TypedValue tv = new TypedValue();
            getActivity().getTheme().resolveAttribute(android.R.attr.alertDialogIcon, tv, true);
            builder.setMessage(getString(R.string.resetAllWarning)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.no), dialogClickListener).setIcon(tv.resourceId).setTitle(getString(R.string.resetAll)).show();
        });

        binding.customErrorCodeCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            if ((!locked) && (os != null)) {
                if (!b) {
                    binding.ecodeSpinner.setSelection(0);
                    binding.ecodeSpinner.setVisibility(View.VISIBLE);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    View ecode_maker = LayoutInflater.from(getContext()).inflate(R.layout.ecode_maker, null);
                    builder.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                        String descripy = ((EditText)ecode_maker.findViewById(R.id.errorDescripy)).getText().toString();
                        String hex = ((EditText)ecode_maker.findViewById(R.id.errorHex)).getText().toString();
                        os.SetString("code", String.format("%s (0x%s)", descripy, hex.toUpperCase()));
                        binding.ecodeSpinner.setVisibility(View.GONE);
                    });
                    builder.setCancelable(false);
                    builder.setView(ecode_maker);
                    AlertDialog ad = builder.create();
                    ad.show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int req_code, int res_code, Intent data) {
        if (res_code == Activity.RESULT_OK) {
            String content;
            Uri uri;
            switch (req_code) {
                case 22:
                    content = GenerateSaveData("2.1");
                    uri = data.getData();
                    try {
                        OutputStream os = getContext().getContentResolver().openOutputStream(uri);
                        os.write(content.getBytes());
                        os.flush();
                        os.close();
                        Toast.makeText(getContext(), getString(R.string.done), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 21:
                    uri = data.getData();
                    InputStream is;
                    try {
                        is = getActivity().getContentResolver().openInputStream(uri);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder strBuilder = new StringBuilder();
                    String line;
                    while (true) {
                        try {
                            if ((line = reader.readLine()) == null) break;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        strBuilder.append(line)
                                .append("\n");
                    }
                    ParseSaveData(strBuilder.toString());
                    break;
            }
        }
    }

    public void ParseSaveData(String fileData) {
        String version = fileData.split("\n")[0];
        if (version.startsWith("*** Blue screen simulator plus 1.")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            TypedValue tv = new TypedValue();
            getActivity().getTheme().resolveAttribute(android.R.attr.alertDialogIcon, tv, true);
            builder.setMessage("This version of Blue Screen Simulator Plus for Android doesn't support version 1.x configuration files.").setPositiveButton(getString(R.string.ok), null)
                    .setIcon(tv.resourceId).setTitle("Incompatible file").show();
        } else if (version.startsWith("*** Blue screen simulator plus 2.")) {
            String[] primary_section_tokens = fileData.split("#");
            bluescreens.clear();
            for (String section: primary_section_tokens) {
                String[] subsection_tokens = section.split("\\[");
                if (section.startsWith("*")) { continue; }
                String os_name = subsection_tokens[0].replace("\n", "").replace("Windows Vista/7", "Windows 7");
                if (os_name.isEmpty()) { continue; }
                BlueScreen bs = new BlueScreen(os_name, false, getActivity());
                bs.SetString("os", os_name);
                bs.ClearAllTitleTexts();
                bs.ClearFiles();
                for (String subsection: subsection_tokens) {
                    if (subsection.indexOf("]") > 0) {
                        String type = subsection.substring(0, subsection.indexOf("]"));
                        String subsection_withoutheading = subsection.substring(type.length() + 1);
                        int bg = Color.rgb(0, 0, 0);
                        int fg = Color.rgb(255, 255, 255);
                        int hbg = Color.rgb(255, 255, 255);
                        int hfg = Color.rgb(0,0,0);
                        String family = "Inconsolata";
                        float size = 16f;
                        String[] entries = subsection_withoutheading.split(";");
                        for (String entry: entries) {
                            if (!entry.replace("\n", "").isEmpty()) {
                                String key = entry.split("=")[0].replace("\n", "");
                                String value = entry.substring(entry.indexOf("=")+1);
                                switch (type) {
                                    case "string":
                                        bs.SetString(key, UnsanitizeString(value));
                                        break;
                                    case "integer":
                                        bs.SetInt(key, Integer.parseInt(value));
                                        break;
                                    case "boolean":
                                        bs.SetBool(key, UnsanitizeString(value).equals("True"));
                                        break;
                                    case "title":
                                        bs.PushTitle(key, UnsanitizeString(value));
                                        break;
                                    case "progress":
                                        bs.SetProgression(Integer.parseInt(key), Integer.parseInt(value));
                                        break;
                                    case "nt_codes":
                                        bs.PushFile(key, value.split(","));
                                        break;
                                    case "text":
                                        bs.PushText(key, UnsanitizeString(value));
                                        break;
                                    case "format":
                                        switch (key) {
                                            case "fontfamily":
                                                family = value;
                                                bs.SetFont(family, bs.GetStyle(), size);
                                                break;
                                            case "style":
                                                break;
                                            case "size":
                                                size = Float.parseFloat(value);
                                                bs.SetFont(family, bs.GetStyle(), size);
                                                break;
                                        }
                                    case "theme":
                                        switch (key) {
                                            case "bg":
                                                bg = StringToColor(value);
                                                bs.SetTheme(bg, fg, false);
                                                break;
                                            case "fg":
                                                fg = StringToColor(value);
                                                bs.SetTheme(bg, fg, false);
                                                break;
                                            case "hbg":
                                                hbg = StringToColor(value);
                                                bs.SetTheme(hbg, hfg, true);
                                                break;
                                            case "hfg":
                                                hfg = StringToColor(value);
                                                bs.SetTheme(hbg, hfg, true);
                                                break;
                                        }
                                        break;
                                }
                            }
                        }
                    }
                }
                if (bs.GetInt("scale") == 1) {
                    bs.SetInt("scale", 75);
                }
                bluescreens.add(bs);
            }

            Spinner winspin = binding.winSpinner;


            List<String> friendlyNames = new ArrayList<>();
            for (BlueScreen element : bluescreens) {
                friendlyNames.add(element.GetString("friendlyname"));
            }

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<String> catAdapter;
            catAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, friendlyNames);


            // Apply the adapter to the spinner
            winspin.setAdapter(catAdapter);

            winspin.setSelection(0);
            saveSettings(bluescreens, bluescreens.get(0), 0);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            TypedValue tv = new TypedValue();
            getActivity().getTheme().resolveAttribute(android.R.attr.alertDialogIcon, tv, true);
            builder.setMessage("Couldn't recognize the file format. This could be for one of the following reasons:\n\n* The file is not a blue screen simulator plus configuration file\n* This file was designed for a newer version of blue screen simulator plus\n* The file is damaged").setPositiveButton(getString(R.string.ok), null)
                    .setIcon(tv.resourceId).setTitle("Unrecognized file").show();
        }
    }

    public String GenerateSaveData(String format) {
        StringBuilder fileData = new StringBuilder();
        fileData.append("*** Blue screen simulator plus ").append(format).append(" ***");
        Gson gson = new Gson();
        Type strType = new TypeToken<Map<String, String>>() {
        }.getType();
        Type intType = new TypeToken<Map<String, Integer>>() {
        }.getType();
        Type boolType = new TypeToken<Map<String, Boolean>>() {
        }.getType();
        Type progType = new TypeToken<Map<Integer, Integer>>() {}.getType();
        Type arrayType = new TypeToken<Map<String, String[]>>() {}.getType();
        for (BlueScreen bs: bluescreens) {
            Map <String, String> titles;
            Map <String, String> txts;
            Map <String, String> strings;
            Map <String, Integer> ints;
            Map <String, Boolean> bools;
            Map <Integer, Integer> progress;
            Map <String, String[]> files;
            titles = gson.fromJson(bs.GetTitles(), strType);
            txts = gson.fromJson(bs.GetTexts(), strType);
            strings = gson.fromJson(bs.AllStrings(), strType);
            ints = gson.fromJson(bs.AllInts(), intType);
            bools = gson.fromJson(bs.AllBools(), boolType);
            progress = gson.fromJson(bs.AllProgress(), progType);
            files = gson.fromJson(bs.GetFiles(), arrayType);
            if (format.equals("2.1")) {
                fileData.append("\n\n\n#").append(bs.GetString("os")).append("\n\n");
            } else if (format.equals("2.0")) {
                fileData.append("\n\n\n#").append(bs.GetString("os").replace("Windows Vista", "Windows Vista/7").replace("Windows 7", "Windows Vista/7")).append("\n\n");
            }
            if (!strings.isEmpty()) {
                fileData.append("\n\n[string]");
                for (Map.Entry<String, String> entry : strings.entrySet()) {
                    fileData.append("\n");
                    fileData.append(entry.getKey()).append("=")
                            .append(SanitizeString(entry.getValue())).append(";");
                }
            }
            for (int i = 1; i <= 4; i++) {
                fileData.append(String.format("\necode%s=", i)).append(bs.GetString(String.format("ecode%s", i))).append(";");
            }
            fileData.append("\nicon=2D Window;");
            if ((!progress.isEmpty()) && (format.equals("2.1"))) {
                fileData.append("\n\n[progress]");
                for (Map.Entry<Integer, Integer> entry: progress.entrySet()) {
                    fileData.append(String.format("\n%s=%s;", entry.getKey(), entry.getValue()));
                }
            }
            if (!files.isEmpty()) {
                fileData.append("\n\n[nt_codes]");
                for (Map.Entry<String, String[]> entry: files.entrySet()) {
                    fileData.append("\n")
                            .append(entry.getKey())
                            .append("=")
                            .append(String.join(",", entry.getValue()))
                            .append(";");
                }
            }
            if (!bools.isEmpty()) {
                fileData.append("\n\n[boolean]");
                for (Map.Entry<String, Boolean> entry: bools.entrySet()) {
                    String outpend = "True";
                    if (!entry.getValue()) {
                        outpend = "False";
                    }
                    fileData.append("\n")
                            .append(entry.getKey())
                            .append("=")
                            .append(outpend)
                            .append(";");
                }
            }
            if (!ints.isEmpty()) {
                fileData.append("\n\n[integer]");
                for (Map.Entry<String, Integer> entry : ints.entrySet()) {
                    fileData.append("\n")
                            .append(entry.getKey())
                            .append("=")
                            .append(entry.getValue().toString())
                            .append(";");
                }
            }
            fileData.append("\n\n[theme]");
            fileData.append("\nbg=")
                    .append(RGB_String(bs.GetTheme(true, false)))
                    .append(";");
            fileData.append("\nfg=")
                    .append(RGB_String(bs.GetTheme(false, false)))
                    .append(";");
            fileData.append("\nhbg=")
                    .append(RGB_String(bs.GetTheme(true, true)))
                    .append(";");
            fileData.append("\nhfg=")
                    .append(RGB_String(bs.GetTheme(false, true)))
                    .append(";");
            if (!titles.isEmpty()) {
                fileData.append("\n\n[title]");
                for (Map.Entry<String, String> entry: titles.entrySet()) {
                    fileData.append("\n")
                            .append(entry.getKey())
                            .append("=")
                            .append(SanitizeString(entry.getValue()))
                            .append(";");
                }
            }
            if (!txts.isEmpty()) {
                fileData.append("\n\n[text]");
                for (Map.Entry<String, String> entry: txts.entrySet()) {
                    fileData.append("\n")
                            .append(entry.getKey())
                            .append("=")
                            .append(SanitizeString(entry.getValue()))
                            .append(";");
                }
            }
            if (bs.GetBool("font_support")) {
                fileData.append("\n\n[format]");
                fileData.append("\nfontfamily=")
                        .append(bs.GetFamily())
                        .append(";");
                fileData.append("\nsize=")
                        .append(bs.GetSize())
                        .append(";");
                fileData.append("\nstyle=")
                        .append(bs.GetStyle())
                        .append(";");
            }
        }
        return fileData.toString();
    }

    private String UnsanitizeString(String original) {
        return original.replace("::", ":/:/:")
                .replace(":h:", "#")
                .replace(":sc:", ";")
                .replace(":sb:", "[")
                .replace(":eb:", "]")
                .replace(":/:/:", ":")
                .replace("{0}%", "%s%%")
                .replace("{0}", "%s")
                .replace("{1}", "%s")
                .replace("{2}", "%s")
                .replace("{3}", "%s")
                .replace("{4}", "%s")
                .replace("{5}", "%s")
                .replace("{6}", "%s")
                .replace("{7}", "%s")
                .replace("{8}", "%s")
                .replace("{9}", "%s");
    }

    private String SanitizeString(String original) {
        return original.replace(":", "::")
                .replace("#", ":h:")
                .replace("[", ":sb:")
                .replace("]", ":eb:");
    }

    private String RGB_String(int rgb) {
        String[] rgbArray = {String.valueOf(Color.red(rgb)), String.valueOf(Color.green(rgb)), String.valueOf(Color.blue(rgb))};
        return String.join(",", rgbArray);
    }

    private int StringToColor(String commastring) {
        String[] rgbArray = commastring.split(",");
        int r = Integer.parseInt(rgbArray[0]);
        int g = Integer.parseInt(rgbArray[1]);
        int b = Integer.parseInt(rgbArray[2]);
        return Color.rgb(r, g, b);
    }

    void NotImplemented() {
        Toast.makeText(getContext(), R.string.notImplemented, Toast.LENGTH_SHORT).show();
    }

    void ColorChooser(int color, String title, Boolean bg, Boolean hl) {
        int textColor = Color.BLACK;
        if (isDarkTheme(getActivity())) {
            textColor = Color.WHITE;
        }
        ColorPickerDialogBuilder
                .with(getContext())
                .setTitle(title)
                .initialColor(color)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(10)
                .setOnColorSelectedListener(selectedColor -> {

                })
                .setPositiveButton(R.string.ok, (d, lastSelectedColor, allColors) -> {
                    if (bg) {
                        os.SetTheme(lastSelectedColor, os.GetTheme(false, hl), hl);
                    } else {
                        os.SetTheme(os.GetTheme(true, hl), lastSelectedColor, hl);
                    }
                    saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
                })
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> Toast.makeText(getContext(), String.format("%s!", getString(R.string.cancelled)), Toast.LENGTH_SHORT).show())
                .showAlphaSlider(false)
                .showColorEdit(true)
                .setColorEditTextColor(textColor)
                .build()
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public boolean isDarkTheme(Activity activity) {
        int currentNightMode = activity.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public void saveSettings(List<BlueScreen> blues, BlueScreen modified, long id) {
        blues.set((int)id, modified);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("eu.markustegelane.bssp", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(blues);
        editor.putString("bluescreens", json);
        editor.apply();
    }

    public void saveSelection(int index) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("eu.markustegelane.bssp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedItem", index);
        editor.apply();
    }

    @Override
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Get the selected item
        locked = true;
        String selectedItem = adapterView.getItemAtPosition(i).toString();
        for (BlueScreen element: bluescreens) {
            if (element.GetString("friendlyname").equals(selectedItem)) {
                os = element;
                break;
            }
        }
        Button ntEdit = binding.ntCodeEditButton;
        Button parEdit = binding.codeEditButton;
        Button hbg = binding.hlBackground;
        Button hfg = binding.hlForeground;
        Spinner codesel = binding.ecodeSpinner;
        Spinner nineXSpinner = binding.nineXSpinner;
        CheckBox ac = binding.autoCloseCheck;
        CheckBox green = binding.insiderCheck;
        CheckBox details = binding.showDetailsCheck;
        CheckBox pars = binding.showParCodes;
        CheckBox server = binding.serverScreen;
        CheckBox waterMark = binding.showWatermark;
        CheckBox deviceCheck = binding.deviceCheck;
        CheckBox rainbowCheck = binding.rainbowCheck;
        TextView elabel = binding.eCodeLabel;

        ac.setVisibility(View.GONE); green.setVisibility(View.GONE); details.setVisibility(View.GONE); pars.setVisibility(View.GONE);
        server.setVisibility(View.GONE); nineXSpinner.setVisibility(View.GONE); hbg.setVisibility(View.GONE); hfg.setVisibility(View.GONE);
        ntEdit.setVisibility(View.GONE); parEdit.setVisibility(View.GONE); codesel.setVisibility(View.GONE); elabel.setVisibility(View.GONE);
        binding.playSound.setVisibility(View.GONE); binding.oneSpinner.setVisibility(View.GONE); binding.blinkCheck.setVisibility(View.GONE);
        binding.amdProcessorCheck.setVisibility(View.GONE); binding.stackTraceCheck.setVisibility(View.GONE);
        binding.setCulpritButton.setVisibility(View.GONE); binding.culpritCheck.setVisibility(View.GONE); binding.moreFileInfoCheck.setVisibility(View.GONE);
        binding.progressEditor.setVisibility(View.GONE); rainbowCheck.setVisibility(View.GONE);
        deviceCheck.setVisibility(View.GONE);
        binding.customErrorCodeCheck.setVisibility(View.GONE); binding.customErrorCodeCheck.setChecked(false);
        if ("Windows NT 3.x/4.0".equals(os.GetString("os"))) {
            binding.blinkCheck.setVisibility(View.VISIBLE);
            binding.amdProcessorCheck.setVisibility(View.VISIBLE);
            binding.stackTraceCheck.setVisibility(View.VISIBLE);
        }
        String s = os.GetString("os");
        if ("Windows XP".equals(s) || "Windows Vista".equals(s) || "Windows 7".equals(s)) {
            binding.moreFileInfoCheck.setVisibility(View.VISIBLE);
            ac.setVisibility(View.VISIBLE);
        }
        if (os.GetString("os").equals("Windows 10")) {
            deviceCheck.setVisibility(View.VISIBLE);
        }
        switch (os.GetString("os")) {
            case "Windows 11":
            case "Windows 10":
                ac.setVisibility(View.VISIBLE);
                green.setVisibility(View.VISIBLE);
                details.setVisibility(View.VISIBLE);
                pars.setVisibility(View.VISIBLE);
                parEdit.setVisibility(View.VISIBLE);
                server.setVisibility(View.VISIBLE);
                codesel.setVisibility(View.VISIBLE);
                elabel.setVisibility(View.VISIBLE);
                rainbowCheck.setVisibility(View.VISIBLE);
                binding.setCulpritButton.setVisibility(View.VISIBLE);
                binding.culpritCheck.setVisibility(View.VISIBLE);
                binding.customErrorCodeCheck.setVisibility(View.VISIBLE);
                binding.progressEditor.setVisibility(View.VISIBLE);
                break;
            case "Windows 8/8.1":
                ac.setVisibility(View.VISIBLE);
                details.setVisibility(View.VISIBLE);
                pars.setVisibility(View.VISIBLE);
                parEdit.setVisibility(View.VISIBLE);
                codesel.setVisibility(View.VISIBLE);
                elabel.setVisibility(View.VISIBLE);
                rainbowCheck.setVisibility(View.VISIBLE);
                binding.setCulpritButton.setVisibility(View.VISIBLE);
                binding.culpritCheck.setVisibility(View.VISIBLE);
                binding.customErrorCodeCheck.setVisibility(View.VISIBLE);
                binding.progressEditor.setVisibility(View.VISIBLE);
                break;
            case "Windows 7":
            case "Windows Vista":
            case "Windows XP":
            case "Windows NT 3.x/4.0":
            case "Windows 2000":
                ntEdit.setVisibility(View.VISIBLE);
                parEdit.setVisibility(View.VISIBLE);
                details.setVisibility(View.VISIBLE);
                if (os.GetString("os").equals("Windows 7") || os.GetString("os").equals("Windows Vista")) {
                    ac.setVisibility(View.VISIBLE);
                } else {
                    ac.setVisibility(View.GONE);
                }
                codesel.setVisibility(View.VISIBLE);
                elabel.setVisibility(View.VISIBLE);
                binding.setCulpritButton.setVisibility(View.VISIBLE);
                binding.culpritCheck.setVisibility(View.VISIBLE);
                binding.customErrorCodeCheck.setVisibility(View.VISIBLE);
                break;
            case "Windows 1.x/2.x":
                binding.oneSpinner.setVisibility(View.VISIBLE);
                details.setVisibility(View.GONE);
                binding.playSound.setVisibility(View.VISIBLE);
                break;
            case "Windows 9x/Me":
                parEdit.setVisibility(View.VISIBLE);
                nineXSpinner.setVisibility(View.VISIBLE);
                hbg.setVisibility(View.VISIBLE);
                hfg.setVisibility(View.VISIBLE);
                elabel.setVisibility(View.VISIBLE);
                break;
            case "Windows 3.1x":
                hbg.setVisibility(View.VISIBLE);
                hfg.setVisibility(View.VISIBLE);
                details.setVisibility(View.GONE);
                break;
            case "Windows CE":
                details.setVisibility(View.GONE);
                codesel.setVisibility(View.VISIBLE);
                ac.setVisibility(View.VISIBLE);
                binding.customErrorCodeCheck.setVisibility(View.VISIBLE);
                break;
            default:
                details.setVisibility(View.VISIBLE);
                elabel.setVisibility(View.VISIBLE);
                break;
        }
        Spinner eCodeSpin = binding.ecodeSpinner;
        String ecode = os.GetString("code");
        ac.setChecked(os.GetBool("autoclose"));
        green.setChecked(os.GetBool("green"));
        details.setChecked(os.GetBool("show_description"));
        pars.setChecked(os.GetBool("extracodes"));
        server.setChecked(os.GetBool("server"));
        waterMark.setChecked(os.GetBool("watermark"));
        rainbowCheck.setChecked(os.GetBool("rainbow"));
        binding.playSound.setChecked(os.GetBool("playsound"));
        binding.blinkCheck.setChecked(os.GetBool("blinkblink"));
        binding.amdProcessorCheck.setChecked(os.GetBool("amd"));
        binding.stackTraceCheck.setChecked(os.GetBool("stack_trace"));
        binding.culpritCheck.setChecked(os.GetBool("show_file"));
        binding.moreFileInfoCheck.setChecked(os.GetBool("extrafile"));
        deviceCheck.setChecked(os.GetBool("device"));
        if (!binding.culpritCheck.isChecked()) {
            binding.setCulpritButton.setVisibility(View.GONE);
        }
        saveSelection(i);
        boolean foundCode = false;
        for (int j = 0; j < eCodeSpin.getAdapter().getCount(); j++) {
            if (eCodeSpin.getItemAtPosition(j).toString().equals(ecode)) {
                eCodeSpin.setSelection(j);
                foundCode = true;
                break;
            }
        }
        if (!foundCode) {
            eCodeSpin.setVisibility(View.GONE);
            binding.customErrorCodeCheck.setChecked(true);
        }
        for (int j = 0; j < binding.oneSpinner.getAdapter().getCount(); j++) {
            if (binding.oneSpinner.getItemAtPosition(j).toString().equals(os.GetString("qr_file"))) {
                binding.oneSpinner.setSelection(j);
                break;
            }
        }

        for (int j = 0; j < nineXSpinner.getAdapter().getCount(); j++) {
            if (nineXSpinner.getItemAtPosition(j).toString().equals(os.GetString("Screen mode"))) {
                nineXSpinner.setSelection(j);
                break;
            }
        }
        locked = false;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(adapterView.getContext(), getString(R.string.what), Toast.LENGTH_SHORT).show();
    }
}