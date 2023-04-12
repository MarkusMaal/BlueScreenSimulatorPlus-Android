package eu.markustegelane.bssp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.markustegelane.bssp.databinding.FragmentFirstBinding;
public class MainFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentFirstBinding binding;

    public List<BlueScreen> bluescreens = new ArrayList<>();

    private Boolean locked = false;

    BlueScreen os;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        if (sharedPreferences.getString("bluescreens", null) == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            bluescreens.add(new BlueScreen("Windows 1.x/2.x", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows 3.1x", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows 9x/Me", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows CE", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows NT 3.x/4.0", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows 2000", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows XP", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows Vista", true, getActivity()));
            bluescreens.add(new BlueScreen("Windows 7", true, getActivity()));
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


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
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


        List<String> friendlyNames = new ArrayList<String>();
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

        binding.executeButton.setOnClickListener(view1 -> {
            switch (bluescreens.get((int)winspin.getSelectedItemId()).GetString("os")) {
                case "Windows 8/8.1":
                case "Windows 10":
                case "Windows 11":
                    Intent i = new Intent(view1.getContext(), ModernBSOD.class);
                    Bundle b = new Bundle();
                    BlueScreen me = bluescreens.get((int)winspin.getSelectedItemId());
                    b.putSerializable("bluescreen", me);
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

        binding.autoCloseCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
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
            }
        });
        binding.insiderCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if ((!locked) && (os != null)) {
                    os.SetBool("green", b);
                    saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
                }
            }
        });
        binding.showDetailsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if ((!locked) && (os != null)) {
                    os.SetBool("show_description", b);
                    saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
                }
            }
        });

        binding.showParCodes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if ((!locked) && (os != null)) {
                    os.SetBool("extracodes", b);
                    saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
                }
            }
        });

        binding.serverScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if ((!locked) && (os != null)) {
                    os.SetBool("server", b);
                    saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
                }
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
                if ((!locked) && (os != null)) {
                    os.SetString("code", adapterView.getItemAtPosition(i).toString());
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

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        locked = true;
                        bluescreens.clear();
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        Gson gson = new Gson();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        bluescreens.add(new BlueScreen("Windows 1.x/2.x", true, getActivity()));
                        bluescreens.add(new BlueScreen("Windows 3.1x", true, getActivity()));
                        bluescreens.add(new BlueScreen("Windows 9x/Me", true, getActivity()));
                        bluescreens.add(new BlueScreen("Windows CE", true, getActivity()));
                        bluescreens.add(new BlueScreen("Windows NT 3.x/4.0", true, getActivity()));
                        bluescreens.add(new BlueScreen("Windows 2000", true, getActivity()));
                        bluescreens.add(new BlueScreen("Windows XP", true, getActivity()));
                        bluescreens.add(new BlueScreen("Windows Vista", true, getActivity()));
                        bluescreens.add(new BlueScreen("Windows 7", true, getActivity()));
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
            }
        };

        binding.codeEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), CodeEdit.class);
                Bundle b = new Bundle();
                BlueScreen me = bluescreens.get((int)winspin.getSelectedItemId());
                b.putSerializable("bluescreen", me);
                b.putInt("bluescreen_id", (int) binding.winSpinner.getSelectedItemId());
                b.putSerializable("bluescreens", (Serializable) bluescreens);
                i.putExtras(b);
                startActivity(i);
                getActivity().finish();
            }
        });

        binding.ntCodeEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nt = new Intent(getContext(), NTCodeEdit.class);
                Bundle b =  new Bundle();
                BlueScreen me = bluescreens.get((int)winspin.getSelectedItemId());
                b.putSerializable("bluescreen", me);
                b.putInt("bluescreen_id", (int) binding.winSpinner.getSelectedItemId());
                b.putSerializable("bluescreens", (Serializable) bluescreens);
                nt.putExtras(b);
                startActivity(nt);
                getActivity().finish();
            }
        });

        binding.advancedOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent s = new Intent(getContext(), SettingsActivity.class);
                Bundle b = new Bundle();
                BlueScreen me = bluescreens.get((int)winspin.getSelectedItemId());
                b.putSerializable("bluescreen", me);
                b.putInt("bluescreen_id", (int) binding.winSpinner.getSelectedItemId());
                b.putSerializable("bluescreens", (Serializable) bluescreens);
                s.putExtras(b);
                startActivity(s);
                getActivity().finish();
            }
        });

        binding.textForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorChooser(os.GetTheme(false, false), String.format(getString(R.string.selectColor), getString(R.string.foregroundCol)), false, false);
            }

        });

        binding.textBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorChooser(os.GetTheme(true, false), String.format(getString(R.string.selectColor), getString(R.string.backgroundCol)), true, false);
            }

        });

        binding.hlForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorChooser(os.GetTheme(false, true), String.format(getString(R.string.selectColor), String.format("%s %s", getString(R.string.highlight_), getString(R.string.foregroundCol))), false, true);
            }

        });

        binding.hlBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorChooser(os.GetTheme(true, true), String.format(getString(R.string.selectColor), String.format("%s %s", getString(R.string.highlight_), getString(R.string.backgroundCol))), true, true);
            }

        });

        binding.delPreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                TypedValue tv = new TypedValue();
                getActivity().getTheme().resolveAttribute(android.R.attr.alertDialogIcon, tv, true);
                builder.setMessage(getString(R.string.delConfigWarning)).setPositiveButton(getString(R.string.yes), delPresetListener)
                        .setNegativeButton(getString(R.string.no), delPresetListener).setTitle(getString(R.string.delConfig)).setIcon(tv.resourceId).show();
            }
        });

        binding.addPreset.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressLint("UseSwitchCompatOrMaterialCode")
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                TypedValue tv = new TypedValue();
                View new_view = LayoutInflater.from(getContext()).inflate(R.layout.new_view, null);
                final Spinner templateSelector = ((Spinner) new_view.findViewById(R.id.osSpinner));
                final Spinner osSelector = ((Spinner) new_view.findViewById(R.id.osSpinner2));
                final EditText friendlyText = ((EditText) new_view.findViewById(R.id.editTextTemplate));
                final TextView tv4 = ((TextView)new_view.findViewById(R.id.textView4));
                final Switch allowCust = ((Switch)new_view.findViewById(R.id.allowCust));
                templateSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (!allowCust.isChecked()) {
                            osSelector.setSelection(i);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                allowCust.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (!b) {
                            osSelector.setVisibility(View.GONE);
                            tv4.setVisibility(View.GONE);
                            osSelector.setSelection((int)templateSelector.getSelectedItemId());
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            TypedValue tv = new TypedValue();
                            getActivity().getTheme().resolveAttribute(android.R.attr.alertDialogIcon, tv, true);
                            builder.setMessage(getString(R.string.hybridWarn)).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            osSelector.setVisibility(View.VISIBLE);
                                            tv4.setVisibility(View.VISIBLE);
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            compoundButton.setChecked(false);
                                        }
                                    }).setIcon(tv.resourceId).setTitle(getString(R.string.danger)).show();
                        }
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
            }
        });

        binding.resetHacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                TypedValue tv = new TypedValue();
                getActivity().getTheme().resolveAttribute(android.R.attr.alertDialogIcon, tv, true);
                builder.setMessage(getString(R.string.currentReset)).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String backupName = os.GetString("friendlyname");
                                bluescreens.set((int)winspin.getSelectedItemId(), new BlueScreen(os.GetString("os"), true, getActivity()));
                                os = bluescreens.get((int)winspin.getSelectedItemId());
                                os.SetString("friendlyname", backupName);
                                saveSettings(bluescreens, os, winspin.getSelectedItemId());
                                Intent in = new Intent(getContext(), MainActivity.class);
                                startActivity(in);
                                getActivity().overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_enter_anim, androidx.navigation.ui.R.anim.nav_default_exit_anim);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), dialogClickListener).setIcon(tv.resourceId).setTitle(getString(R.string.resetAllConfig)).show();
            }
        });

        binding.loadConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotImplemented();
            }
        });

        binding.saveConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotImplemented();
            }
        });

        binding.resetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                TypedValue tv = new TypedValue();
                getActivity().getTheme().resolveAttribute(android.R.attr.alertDialogIcon, tv, true);
                builder.setMessage(getString(R.string.resetAllWarning)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(getString(R.string.no), dialogClickListener).setIcon(tv.resourceId).setTitle(getString(R.string.resetAll)).show();
            }
        });
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
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {

                    }
                })
                .setPositiveButton(R.string.ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors) {
                        if (bg) {
                            os.SetTheme(lastSelectedColor, os.GetTheme(false, hl), hl);
                        } else {
                            os.SetTheme(os.GetTheme(true, hl), lastSelectedColor, hl);
                        }
                        saveSettings(bluescreens, os, binding.winSpinner.getSelectedItemId());
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), String.format("%s!", getString(R.string.cancelled)), Toast.LENGTH_SHORT).show();
                    }
                })
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(blues);
        editor.putString("bluescreens", json);
        editor.apply();
    }

    public void saveSelection(int index) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
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
        Switch ac = binding.autoCloseCheck;
        Switch green = binding.insiderCheck;
        Switch details = binding.showDetailsCheck;
        Switch pars = binding.showParCodes;
        Switch server = binding.serverScreen;
        TextView elabel = binding.eCodeLabel;

        ac.setVisibility(View.GONE); green.setVisibility(View.GONE); details.setVisibility(View.GONE); pars.setVisibility(View.GONE);
        server.setVisibility(View.GONE); nineXSpinner.setVisibility(View.GONE); hbg.setVisibility(View.GONE); hfg.setVisibility(View.GONE);
        ntEdit.setVisibility(View.GONE); parEdit.setVisibility(View.GONE); codesel.setVisibility(View.GONE); elabel.setVisibility(View.GONE);
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
                break;
            case "Windows 8/8.1":
                ac.setVisibility(View.VISIBLE);
                details.setVisibility(View.VISIBLE);
                pars.setVisibility(View.VISIBLE);
                parEdit.setVisibility(View.VISIBLE);
                codesel.setVisibility(View.VISIBLE);
                elabel.setVisibility(View.VISIBLE);
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
                }
                codesel.setVisibility(View.VISIBLE);
                elabel.setVisibility(View.VISIBLE);
                break;
            case "Windows CE":
            case "Windows 1.x/2.x":
                details.setVisibility(View.GONE);
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
        saveSelection(i);
        for (int j = 0; j < eCodeSpin.getAdapter().getCount(); j++) {
            if (eCodeSpin.getItemAtPosition(j).toString().equals(ecode)) {
                eCodeSpin.setSelection(j);
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