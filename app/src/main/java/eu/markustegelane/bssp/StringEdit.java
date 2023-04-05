package eu.markustegelane.bssp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.markustegelane.bssp.databinding.FragmentFirstBinding;
import kotlin.Suppress;

public class StringEdit extends AppCompatActivity {

    private FragmentFirstBinding binding;
    BlueScreen os;
    int os_id;
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_string_edit);
        Bundle bundle = getIntent().getExtras();
        Toolbar titleBar = findViewById(R.id.titleBar);
        os = (BlueScreen) bundle.getSerializable("bluescreen");
        os_id = bundle.getInt("bluescreen_id");
        titleBar.setTitle(os.GetString("friendlyname"));
        Spinner slist = findViewById(R.id.settingList);


        List<String> strings = new ArrayList<String>();
        Gson gson = new Gson();
        Type typeStringString = new TypeToken<Map<String, String>>(){}.getType();
        Type typeStringBool = new TypeToken<Map<String, Boolean>>(){}.getType();
        Type typeStringInteger = new TypeToken<Map<String, Integer>>(){}.getType();
        ((Map<String, String>) gson.fromJson(os.AllStrings(), typeStringString)).forEach((key, value) -> strings.add(key + " [string]"));
        ((Map<String, String>) gson.fromJson(os.GetTitles(), typeStringString)).forEach((key, value) -> strings.add(key + " [title]"));
        ((Map<String, String>) gson.fromJson(os.GetTexts(), typeStringString)).forEach((key, value) -> strings.add(key + " [text]"));
        ((Map<String, Boolean>) gson.fromJson(os.AllBools(), typeStringBool)).forEach((key, value) -> strings.add(key + " [boolean]"));
        ((Map<String, Integer>) gson.fromJson(os.AllInts(), typeStringInteger)).forEach((key, value) -> strings.add(key + " [integer]"));

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings);
        slist.setAdapter(catAdapter);

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
    }

}