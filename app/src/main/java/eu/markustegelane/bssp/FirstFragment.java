package eu.markustegelane.bssp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eu.markustegelane.bssp.databinding.FragmentFirstBinding;
public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    public List<BlueScreen> bluescreens = new ArrayList<>();

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
        Spinner mySpinner = (Spinner)view.findViewById(R.id.winSpinner);
        binding.executeButton.setOnClickListener(view1 -> {
            Spinner eCodes = (Spinner)view.findViewById(R.id.ecodeSpinner);
            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch isInsider = (Switch)view.findViewById(R.id.insiderCheck);
            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch isAutoClose = (Switch)view.findViewById(R.id.autoCloseCheck);
            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch showDetails = (Switch)view.findViewById(R.id.showDetailsCheck);
            switch (bluescreens.get((int)mySpinner.getSelectedItemId()).GetString("os")) {
                case "Windows 11":
                    Intent i = new Intent(view1.getContext(), Win11BSOD.class);
                    Bundle b = new Bundle();
                    BlueScreen me = bluescreens.get((int)mySpinner.getSelectedItemId());
                    b.putSerializable("texts", (Serializable) me.GetTexts());
                    b.putSerializable("bluescreen", me);
                    b.putInt("bg", me.GetTheme(true, false));
                    b.putInt("fg", me.GetTheme(false, false));
                    b.putBoolean("insiderPreview", isInsider.isChecked());
                    b.putBoolean("autoClose", isAutoClose.isChecked());
                    b.putBoolean("showDetails", showDetails.isChecked());
                    b.putString("emoticon", me.GetString("emoticon"));
                    b.putString("errorCode", eCodes.getSelectedItem().toString());
                    i.putExtras(b);
                    startActivity(i);
                    break;
                default:
                    Snackbar.make(view, "Not implemented yet!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
            }
        });
        List<String> friendlyNames = new ArrayList<String>();
        for (BlueScreen element : bluescreens) {
            friendlyNames.add(element.GetString("friendlyname"));
        }
        ArrayAdapter<String> catAdapter;
        catAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, friendlyNames);
        mySpinner.setAdapter(catAdapter);
        mySpinner.setSelection(11);

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

}