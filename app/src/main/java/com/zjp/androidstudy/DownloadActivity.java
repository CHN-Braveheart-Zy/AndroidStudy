package com.zjp.androidstudy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zjp.androidstudy.databinding.ActivityDownloadBinding;
import com.zjp.androidstudy.viewmodel.DownloadViewModel;

/**
 * <pre>
 *     author : zhangjunpu
 *     e-mail : zhangjp@zhigujinyun.com
 *     time   : 2020/12/10
 *     version: 1.0
 *     desc   :
 * </pre>
 */
public class DownloadActivity extends AppCompatActivity {

    private ActivityDownloadBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDownloadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.setVm(new DownloadViewModel());
    }
}
