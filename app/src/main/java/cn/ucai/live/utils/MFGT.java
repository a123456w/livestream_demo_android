package cn.ucai.live.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;




/**
 * Created by Administrator on 2017/5/19 0019.
 */
public class MFGT {
    private static void startActivity(Context context, Class clazz) {
        context.startActivity(new Intent(context,clazz));
    }
    private static void startActivity(Context context,Intent intent) {
        context.startActivity(intent);
    }
    public static void finish(Activity activity) {
        activity.finish();
    }

}
