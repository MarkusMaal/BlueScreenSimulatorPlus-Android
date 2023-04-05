package eu.markustegelane.bssp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eu.markustegelane.bssp.databinding.FragmentFirstBinding;
public class FirstFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentFirstBinding binding;

    public List<BlueScreen> bluescreens = new ArrayList<>();

    private Boolean locked = false;

    BlueScreen os;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
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
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get reference to the spinner
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

        // Set selection to 11
        winspin.setSelection(11);

        binding.executeButton.setOnClickListener(view1 -> {
            Spinner eCodes = (Spinner)view.findViewById(R.id.ecodeSpinner);
            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch isInsider = (Switch)view.findViewById(R.id.insiderCheck);
            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch isAutoClose = (Switch)view.findViewById(R.id.autoCloseCheck);
            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch showDetails = (Switch)view.findViewById(R.id.showDetailsCheck);
            switch (bluescreens.get((int)winspin.getSelectedItemId()).GetString("os")) {
                case "Windows 10":
                case "Windows 11":
                    Intent i = new Intent(view1.getContext(), Win11BSOD.class);
                    Bundle b = new Bundle();
                    BlueScreen me = bluescreens.get((int)winspin.getSelectedItemId());
                    b.putSerializable("bluescreen", me);
                    i.putExtras(b);
                    startActivity(i);
                    break;
                default:
                    Snackbar.make(view, "Not implemented yet!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
            }
        });

        binding.autoCloseCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!locked) {
                    os.SetBool("autoclose", b);
                }
            }
        });
        binding.insiderCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!locked) {
                    os.SetBool("green", b);
                }
            }
        });
        binding.showDetailsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!locked) {
                    os.SetBool("show_description", b);
                }
            }
        });

        binding.ecodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!locked) {
                    os.SetString("code", adapterView.getItemAtPosition(i).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        Spinner eCodeSpin = binding.ecodeSpinner;
        String ecode = os.GetString("code");
        ac.setChecked(os.GetBool("autoclose"));
        green.setChecked(os.GetBool("green"));
        details.setChecked(os.GetBool("show_description"));
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