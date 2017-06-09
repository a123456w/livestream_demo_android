package cn.ucai.live.data.local;

import java.util.List;
import java.util.Map;

import cn.ucai.live.data.model.Gift;

/**
 * Created by Administrator on 2017/6/9 0009.
 */

public class LiveDao {
    static final String GIFT_TABLE_NAME = "m_live_gift";
    static final String GIFT_COLUMN_ID = "m_gift_id";
    static final String GIFT_COLUMN_NAME = "m_gift_name";
    static final String GIFT_COLUMN_URL = "m_gift_url";
    static final String GIFT_COLUMN_PRICE = "m_gift_price";


    public void setGiftList(List<Gift> contactList) {
        LiveDBManager.getInstance().saveContactList(contactList);
    }

    public Map<Integer, Gift> getGiftList() {
        return LiveDBManager.getInstance().getContactList();
    }
}
