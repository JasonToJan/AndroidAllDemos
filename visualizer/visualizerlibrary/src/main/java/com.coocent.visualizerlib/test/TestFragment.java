package com.coocent.visualizerlib.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coocent.visualizerlib.R;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2019/6/11 17:36
 **/
public class TestFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_test_fragment2, container, false);

        return rootView;
    }
}
