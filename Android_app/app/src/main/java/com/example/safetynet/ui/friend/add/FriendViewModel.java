package com.example.safetynet.ui.friend.add;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FriendViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FriendViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is add a friend fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}