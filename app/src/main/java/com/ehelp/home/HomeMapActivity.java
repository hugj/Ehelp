package com.ehelp.home;

import android.os.Bundle;

import com.ehelp.R;
import com.wangjie.androidinject.annotation.annotations.base.AILayout;
import com.wangjie.androidinject.annotation.annotations.base.AIView;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

@AILayout(R.layout.activity_home_map)
public class HomeMapActivity extends BaseFragment implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {

    @AIView(R.id.rfab_group_sample_fragment_a_rfal)
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private int user_id;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void setUserID(int id) {
        user_id = id;
    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        showToastMessage("clicked label: " + position);
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        showToastMessage("clicked icon: " + position);
        rfabHelper.toggleContent();
    }

    @Override
    public void onInitialRFAB(RapidFloatingActionButton rfab) {
        this.rfaButton = rfab;
    }


    @Override
    public String getRfabIdentificationCode() {
        return getString(R.string.rfab_identification_code_fragment_a);
    }

    @Override
    public String getTitle() {
        return "附近的人";
    }

}
