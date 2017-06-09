package cn.ucai.live.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/6/9 0009.
 */
public class LiveDbOpenHelper extends SQLiteOpenHelper {

    private static final String GIFT_TABLE_CREATE = "CREATE TABLE "
            + LiveDao.GIFT_TABLE_NAME + " ("
            + LiveDao.GIFT_COLUMN_NAME + " TEXT, "
            + LiveDao.GIFT_COLUMN_URL + " TEXT, "
            + LiveDao.GIFT_COLUMN_PRICE + " INTEGER, "
            + LiveDao.GIFT_COLUMN_ID + " INTEGER PRIMARY KEY);";

    private static LiveDbOpenHelper instance;
    private static final int versionNumber=1;
    private LiveDbOpenHelper(Context context) {
        super(context, getLiveDatabaseName(context), null, versionNumber);
    }

    private static String getLiveDatabaseName(Context context) {
        return context.getPackageName()+".db";
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GIFT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public static LiveDbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new LiveDbOpenHelper(context.getApplicationContext());
        }
        return instance;
    }
    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }
}
