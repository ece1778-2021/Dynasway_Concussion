package com.example.dynaswayconcussion.ui.tests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TestsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TestsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the tests fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}