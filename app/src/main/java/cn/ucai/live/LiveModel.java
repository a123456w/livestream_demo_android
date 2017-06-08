package cn.ucai.live;

import com.hyphenate.easeui.model.EasePreferenceManager;

import cn.ucai.live.data.restapi.LiveManager;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class LiveModel {

    public void LiveManager(){
    }

    public void setCurrentUserName(String username) {
        EasePreferenceManager.getInstance().setCurrentUserName(username);
    }

    public String getCurrentUsernName() {
        return EasePreferenceManager.getInstance().getCurrentUsername();
    }
}
