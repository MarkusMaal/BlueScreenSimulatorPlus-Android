package eu.markustegelane.bssp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import eu.markustegelane.bssp.databinding.ActivityErrorCodeEditorBinding;

public class ErrorCodeEditor extends AppCompatActivity {

    private ActivityErrorCodeEditorBinding binding;
    BlueScreen bs;
    List<BlueScreen> bluescreens;
    Map<Integer, Integer> progression;

    int selected_id;

    int interval = 10;
    boolean locked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityErrorCodeEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        Bundle b = getIntent().getExtras();

        final boolean[] cancel = {true};

        CountDownTimer cdt = new CountDownTimer(Long.MAX_VALUE, interval) {
            @Override
            public void onTick(long l) {
                cancel[0] = false;
                if (binding.progressSeekBar.getProgress() < bs.GetInt("progressmillis")) {
                    binding.progressSeekBar.incrementProgressBy(1);
                } else {
                    cancel[0] = true;
                    binding.playButton.setBackground(ResourcesCompat.getDrawable(getWindow().getContext().getResources(), android.R.drawable.ic_media_play, getWindow().getContext().getTheme()));
                    binding.progressSeekBar.setProgress(0);
                    cancel();
                }
            }

            @Override
            public void onFinish() {
                cancel[0] = true;
            }
        };

        bs = (BlueScreen) b.getSerializable("bluescreen");
        bluescreens = (List<BlueScreen>)b.getSerializable("bluescreens");
        selected_id = b.getInt("bluescreen_id");
        binding.progressSeekBar.setMax(bs.GetInt("progressmillis"));
        Gson gson = new Gson();
        Type type = new TypeToken<Map<Integer, Integer>>(){}.getType();
        progression = gson.fromJson(bs.AllProgress(), type);
        int backup_color = binding.totalMillisEditText.getTextColors().getDefaultColor();
        binding.progressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                UpdateText(i, progression);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                locked = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                locked = false;
            }
        });
        binding.progressSeekBar.setProgress(0);

        binding.delKfButton.setOnClickListener(view -> {
            if (progression.containsKey(binding.progressSeekBar.getProgress())) {
                int progress = binding.progressSeekBar.getProgress();
                progression.remove(progress, progression.get(progress));
                bs.SetAllProgression(Arrays.stream(progression.keySet().toArray(new Integer[0])).mapToInt(Integer::intValue).toArray(),
                                     Arrays.stream(progression.values().toArray(new Integer[0])).mapToInt(Integer::intValue).toArray());
                UpdateText(progress, progression);
            }
        });

        binding.addKfButton.setOnClickListener(view -> {
            int progress = binding.progressSeekBar.getProgress();
            String textVal = binding.editTextValue.getText().toString();
            if (!textVal.equals("")) {
                int value = Integer.parseInt(textVal);
                if (!progression.containsKey(binding.progressSeekBar.getProgress())) {
                    progression.put(progress, value);
                    bs.SetProgression(progress, value);
                    UpdateText(progress, progression);
                    return;
                }
                progression.replace(progress, value);
                bs.SetProgression(progress, value);
                UpdateText(progress, progression);
            } else {
                UpdateText(progress, progression);
            }
        });

        binding.kfPlusButton.setOnClickListener(view -> {
            int current = binding.progressSeekBar.getProgress();
            for (int i = current + 1; i < bs.GetInt("progressmillis"); i++) {
                if (progression.containsKey(i)) {
                    binding.progressSeekBar.setProgress(i);
                    break;
                }
            }
        });

        binding.kfMinusButton.setOnClickListener(view -> {
            int current = binding.progressSeekBar.getProgress();
            for (int i = current - 1; i > 0; i--) {
                if (progression.containsKey(i)) {
                    binding.progressSeekBar.setProgress(i);
                    break;
                }
            }
        });

        binding.randButton.setOnClickListener(view -> {
            Random r = new Random();
            int rVal = r.nextInt(20);
            binding.editTextValue.setText(String.valueOf(rVal));
        });


        binding.editTextValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!locked) {
                    if (charSequence.toString().equals(String.valueOf(progression.get(binding.progressSeekBar.getProgress())))) {
                        if (isDarkThemeOn()) {
                            binding.editTextValue.setTextColor(getColor(android.R.color.primary_text_dark));
                        } else {
                            binding.editTextValue.setTextColor(getColor(android.R.color.primary_text_light));
                        }
                    } else {
                        binding.editTextValue.setTextColor(Color.rgb(0, 128, 0));
                    }
                    return;
                }
                binding.editTextValue.setTextColor(backup_color);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.rewButton.setOnClickListener(view -> binding.progressSeekBar.setProgress(0));

        binding.endButton.setOnClickListener(view -> binding.progressSeekBar.setProgress(bs.GetInt("progressmillis")));

        binding.playButton.setOnClickListener(view -> {
            if (cancel[0]) {
                view.setBackground(ResourcesCompat.getDrawable(view.getContext().getResources(), android.R.drawable.ic_media_pause, view.getContext().getTheme()));
                locked = true;
                cancel[0] = false;
                cdt.start();
            } else {
                view.setBackground(ResourcesCompat.getDrawable(view.getContext().getResources(), android.R.drawable.ic_media_play, view.getContext().getTheme()));
                locked = false;
                cancel[0] = true;
                cdt.cancel();
            }
        });

        binding.clearButton.setOnClickListener(view -> {
            bs.ClearProgress();
            progression.clear();
            binding.progressSeekBar.setProgress(0);
        });

        binding.autoButton.setOnClickListener(view -> {
            bs.ClearProgress();
            bs.SetDefaultProgression();
            progression = gson.fromJson(bs.AllProgress(), type);
            binding.progressSeekBar.setProgress(0);
        });

        binding.saveButton.setOnClickListener(view -> {
            saveSettings(bluescreens, bs, selected_id);
            onBackPressed();
        });

        locked = true;
        ((EditText)findViewById(R.id.totalMillisEditText)).setText(String.valueOf(bs.GetInt("progressmillis") * interval));
        locked = false;
        binding.totalMillisEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().matches("[0-9]+") && !locked) {
                    int newvalueundivided = Integer.parseInt(charSequence.toString());
                    int newvalue = Integer.parseInt(charSequence.toString()) / 10;
                    if ((newvalue > 0) && (newvalueundivided % 10 == 0)) {
                        binding.totalMillisEditText.setTextColor(backup_color);
                        bs.SetInt("progressmillis", newvalue);
                        binding.progressSeekBar.setMax(bs.GetInt("progressmillis"));
                    } else {
                        binding.totalMillisEditText.setTextColor(Color.rgb(128, 128, 0));
                    }
                } else if (!locked) {
                    binding.totalMillisEditText.setTextColor(Color.rgb(255, 0, 0));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    boolean isDarkThemeOn() {
        int nightModeFlags = getWindow().getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    private void UpdateText(int i, Map<Integer, Integer> progression) {
        int progressSum = 0;
        int currentSum = 0;
        for (Integer key: progression.keySet()) {
            if (key <= i) {
                currentSum += progression.get(key);
            }
            progressSum += progression.get(key);
        }
        String previewTemplate = "Progress";
        if (bs.GetString("os").equals("Windows 8/8.1")) {
            previewTemplate = "Information text with dump";
        }
        ((TextView)findViewById(R.id.msTextView)).setText(String.format("%s ms (%s ms, %s%%)\n" + getString(R.string.preview), i * interval, bs.GetInt("progressmillis") * interval, progressSum, String.format(bs.GetString(previewTemplate), currentSum)));
        if (progression.containsKey(i)) {
            ((EditText) findViewById(R.id.editTextValue)).setText(progression.get(i).toString());
        } else {
            ((EditText) findViewById(R.id.editTextValue)).setText("0");
        }
    }

    public void saveSettings(List<BlueScreen> blues, BlueScreen modified, long id) {
        blues.set((int)id, modified);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getWindow().getContext());
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(blues);
        editor.putString("bluescreens", json);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getWindow().getContext(), MainActivity.class);
        startActivity(i);
        finish();
    }
}