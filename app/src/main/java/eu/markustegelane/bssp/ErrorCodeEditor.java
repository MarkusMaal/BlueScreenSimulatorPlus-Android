package eu.markustegelane.bssp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import eu.markustegelane.bssp.databinding.ActivityErrorCodeEditorBinding;

public class ErrorCodeEditor extends AppCompatActivity {

    private ActivityErrorCodeEditorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityErrorCodeEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
    }
}