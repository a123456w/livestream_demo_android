package cn.ucai.live.data.local;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import cn.ucai.live.LiveApplication;
import cn.ucai.live.data.model.Gift;

/**
 * Created by Administrator on 2017/6/9 0009.
 */
public class LiveDBManager {
    static private LiveDBManager dbMgr = new LiveDBManager();
    private LiveDbOpenHelper dbHelper;

    private LiveDBManager() {
        dbHelper = LiveDbOpenHelper.getInstance(LiveApplication.getInstance().getApplicationContext());
    }

    public static synchronized LiveDBManager getInstance() {
        if (dbMgr == null) {
            dbMgr = new LiveDBManager();
        }
        return dbMgr;
    }
    /**
     * save contact list
     *
     * @param contactList
     */
    synchronized public void saveContactList(List<Gift> contactList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(LiveDao.GIFT_TABLE_NAME, null, null);
            for (Gift gift : contactList) {
                ContentValues values = new ContentValues();
                values.put(LiveDao.GIFT_COLUMN_ID, gift.getId());
                if (gift.getGname() != null)
                    values.put(LiveDao.GIFT_COLUMN_NAME, gift.getGname() );
                if (gift.getGprice() != null)
                    values.put(LiveDao.GIFT_COLUMN_PRICE, gift.getGprice());
                if (gift.getGurl() != null)
                    values.put(LiveDao.GIFT_COLUMN_URL, gift.getGurl());
                db.replace(LiveDao.GIFT_TABLE_NAME, null, values);
            }
        }
    }
    /**
     * get contact list
     *
     * @return
     */
    synchronized public Map<Integer, Gift> getContactList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<Integer, Gift> gifts = new Hashtable<Integer, Gift>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + LiveDao.GIFT_TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                int gifId = cursor.getInt(cursor.getColumnIndex(LiveDao.GIFT_COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(LiveDao.GIFT_COLUMN_NAME));
                int price = cursor.getInt(cursor.getColumnIndex(LiveDao.GIFT_COLUMN_PRICE));
                String url = cursor.getString(cursor.getColumnIndex(LiveDao.GIFT_COLUMN_URL));
                Gift gift = new Gift();
                gift.setId(gifId);
                gift.setGname(name);
                gift.setGprice(price);
                gift.setGurl(url);

                gifts.put(gifId, gift);
            }
            cursor.close();
        }
        return gifts;
    }
}
