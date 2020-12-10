package com.zjp.androidstudy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zjp.androidstudy.databinding.ActivityMainBinding;
import com.zjp.androidstudy.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.setVm(new MainViewModel());


    }
}