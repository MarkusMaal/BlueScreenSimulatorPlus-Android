package eu.markustegelane.bssp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.markustegelane.bssp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    final public List<BlueScreen> bluescreens = new ArrayList<>();

    boolean egg = true;

    BlueScreen os;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, -(int) getResources().getDimension(com.flask.colorpicker.R.dimen.default_slider_margin), 0, 0);
        binding.appBarLayoutMain.setLayoutParams(params);
        binding.appBarLayoutMain.setPadding(0, (int) getResources().getDimension(com.flask.colorpicker.R.dimen.default_slider_margin), 0,0);

        setSupportActionBar(binding.toolbar);

        //Navigation.setViewNavController(binding.getRoot(), new NavController(this));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void LoadSettings() {
        SharedPreferences sharedPreferences = binding.getRoot().getContext().getSharedPreferences("eu.markustegelane.bssp", Context.MODE_PRIVATE);
        egg = sharedPreferences.getBoolean("egg", true);
        Gson gson = new Gson();
        if (sharedPreferences.getString("bluescreens", null) == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            bluescreens.add(new BlueScreen("Windows 1.x/2.x", true, this));
            bluescreens.add(new BlueScreen("Windows 3.1x", true, this));
            bluescreens.add(new BlueScreen("Windows 9x/Me", true, this));
            bluescreens.add(new BlueScreen("Windows CE", true, this));
            bluescreens.add(new BlueScreen("Windows NT 3.1", true, this));
            bluescreens.add(new BlueScreen("Windows NT 3.x/4.0", true, this));
            bluescreens.add(new BlueScreen("Windows 2000", true, this));
            bluescreens.add(new BlueScreen("Windows XP", true, this));
            bluescreens.add(new BlueScreen("Windows Vista", true, this));
            bluescreens.add(new BlueScreen("Windows 7", true, this));
            bluescreens.add(new BlueScreen("Windows 8 Beta", true, this));
            bluescreens.add(new BlueScreen("Windows 8/8.1", true, this));
            bluescreens.add(new BlueScreen("Windows 10", true, this));
            bluescreens.add(new BlueScreen("Windows 11", true, this));
            String json = gson.toJson(bluescreens);
            editor.putString("bluescreens", json);
            editor.apply();
        } else {
            String json = sharedPreferences.getString("bluescreens", null);
            BlueScreen[] bss = gson.fromJson(json, BlueScreen[].class);
            bluescreens.addAll(Arrays.asList(bss));
        }

        String selectedItem = ((Spinner)findViewById(R.id.winSpinner)).getSelectedItem().toString();
        for (BlueScreen element: bluescreens) {
            if (element.GetString("friendlyname").equals(selectedItem)) {
                os = element;
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        LoadSettings();
        int id = item.getItemId();
        Intent s;
        if (id == R.id.advancedOptionButton) {
            s = new Intent(binding.getRoot().getContext(), SettingsActivity.class);
            Bundle b = new Bundle();
            BlueScreen me = bluescreens.get((int)((Spinner)findViewById(R.id.winSpinner)).getSelectedItemId());
            b.putSerializable("bluescreen", me);
            b.putInt("bluescreen_id", (int) ((Spinner)findViewById(R.id.winSpinner)).getSelectedItemId());
            b.putSerializable("bluescreens", (Serializable) bluescreens);
            s.putExtras(b);
            startActivity(s);
            finish();
        } else if (id == R.id.helpButton) {
            s = new Intent(binding.getRoot().getContext(), HelpActivity.class);
            s.putExtra("egg", (os.GetString("And now, the moment you've been waiting for...").equals("Unicode awesomeness!") && egg));
            startActivity(s);
        }
        return super.onOptionsItemSelected(item);
    }


}