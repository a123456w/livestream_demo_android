package cn.ucai.live;

import com.hyphenate.easeui.model.EasePreferenceManager;

import java.util.Map;

import cn.ucai.live.data.local.LiveDao;
import cn.ucai.live.data.model.Gift;
import cn.ucai.live.data.restapi.LiveManager;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class LiveModel {
    LiveDao dao=null;

    public void LiveManager(){
    }

    public void setCurrentUserName(String username) {
        EasePreferenceManager.getInstance().setCurrentUserName(username);
    }

    public String getCurrentUsernName() {
        return EasePreferenceManager.getInstance().getCurrentUsername();
    }

    public Map<Integer, Gift> getGiftList() {
        dao=new LiveDao();
        return dao.getGiftList();
    }
}
