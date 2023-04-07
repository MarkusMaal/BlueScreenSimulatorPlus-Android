package eu.markustegelane.bssp;

import static android.content.Context.SENSOR_SERVICE;
import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
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
            bluescreens.add(new BlueScreen("Windows 1.x/2.x", true));
            bluescreens.add(new BlueScreen("Windows 3.1x", true));
            bluescreens.add(new BlueScreen("Windows 9x/Me", true));
            bluescreens.add(new BlueScreen("Windows CE", true));
            bluescreens.add(new BlueScreen("Windows NT 3.x/4.0", true));
            bluescreens.add(new BlueScreen("Windows 2000", true));
            bluescreens.add(new BlueScreen("Windows XP", true));
            bluescreens.add(new BlueScreen("Windows Vista", true));
            bluescreens.add(new BlueScreen("Windows 7", true));
            bluescreens.add(new BlueScreen("Windows 8/8.1", true));
            bluescreens.add(new BlueScreen("Windows 10", true));
            bluescreens.add(new BlueScreen("Windows 11", true));
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
                    Intent i = new Intent(view1.getContext(), Win11BSOD.class);
                    Bundle b = new Bundle();
                    BlueScreen me = bluescreens.get((int)winspin.getSelectedItemId());
                    b.putSerializable("bluescreen", me);
                    i.putExtras(b);
                    startActivity(i);
                    break;
                case "Windows 9x/Me":
                case "Windows XP":
                case "Windows Vista":
                case "Windows 7":
                    i = new Intent(view1.getContext(), Win7BSOD.class);
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
                        bluescreens.add(new BlueScreen("Windows 1.x/2.x", true));
                        bluescreens.add(new BlueScreen("Windows 3.1x", true));
                        bluescreens.add(new BlueScreen("Windows 9x/Me", true));
                        bluescreens.add(new BlueScreen("Windows CE", true));
                        bluescreens.add(new BlueScreen("Windows NT 3.x/4.0", true));
                        bluescreens.add(new BlueScreen("Windows 2000", true));
                        bluescreens.add(new BlueScreen("Windows XP", true));
                        bluescreens.add(new BlueScreen("Windows Vista", true));
                        bluescreens.add(new BlueScreen("Windows 7", true));
                        bluescreens.add(new BlueScreen("Windows 8/8.1", true));
                        bluescreens.add(new BlueScreen("Windows 10", true));
                        bluescreens.add(new BlueScreen("Windows 11", true));
                        String json = gson.toJson(bluescreens);
                        editor.putString("bluescreens", json);
                        editor.putInt("selectedItem", 0);
                        editor.apply();
                        Toast.makeText(getContext(), "Settings have been reset. Restarting activity...", Toast.LENGTH_SHORT).show();
                        Intent i = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(i);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(getContext(), "No changes to settings have been made", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        binding.resetSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Warning: This will reset EVERYTHING and all of your custom preferences will be erased. Are you sure you want to continue?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).setTitle("Reset all settings").show();
            }
        });

        binding.codeEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotImplemented();
            }
        });

        binding.ntCodeEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotImplemented();
            }
        });
        /*inding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });*/
    }

    void NotImplemented() {
        Toast.makeText(getContext(), R.string.notImplemented, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        Switch ac = binding.autoCloseCheck;
        Switch green = binding.insiderCheck;
        Switch details = binding.showDetailsCheck;
        Switch pars = binding.showParCodes;
        Spinner eCodeSpin = binding.ecodeSpinner;
        String ecode = os.GetString("code");
        ac.setChecked(os.GetBool("autoclose"));
        green.setChecked(os.GetBool("green"));
        details.setChecked(os.GetBool("show_description"));
        pars.setChecked(os.GetBool("extracodes"));
        saveSelection(i);
        for (int j = 0; j < eCodeSpin.getAdapter().getCount(); j++) {
            if (eCodeSpin.getItemAtPosition(j).toString().equals(ecode)) {
                eCodeSpin.setSelection(j);
                break;
            }
        }
        locked = false;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(adapterView.getContext(), "Wait, how?", Toast.LENGTH_SHORT).show();
    }
}