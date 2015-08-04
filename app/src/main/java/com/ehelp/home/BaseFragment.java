package com.ehelp.home;

import com.wangjie.androidinject.annotation.present.AISupportFragment;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;

public abstract class BaseFragment extends AISupportFragment {
    public abstract String getRfabIdentificationCode();

    public abstract String getTitle();

    public abstract void setUserID(int id);

    public void onInitialRFAB(RapidFloatingActionButton rfab) {
    }

}
