package com.example.translateitapp.ui.TextDetectorFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Get Image From Text fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}