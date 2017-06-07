package cn.ucai.live.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;

import cn.ucai.easeui.domain.User;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.ui.AddContactsActivity;
import cn.ucai.superwechat.ui.ChatActivity;
import cn.ucai.superwechat.ui.ContactDetailsActivity;
import cn.ucai.superwechat.ui.GuideActivity;
import cn.ucai.superwechat.ui.LoginActivity;
import cn.ucai.superwechat.ui.MainActivity;
import cn.ucai.superwechat.ui.RegisterActivity;
import cn.ucai.superwechat.ui.SettingsActivity;
import cn.ucai.superwechat.ui.UserProfileActivity;
import cn.ucai.superwechat.ui.VideoCallActivity;

/**
 * Created by Administrator on 2017/5/19 0019.
 */
public class MFGT {


    public static void gotoLogin(Activity activity) {
        startActivity(activity, LoginActivity.class);
    }

    public static void gotoMain(Activity activity) {
        startActivity(activity,MainActivity.class);
    }
    public static void gotoMain(Activity activity,boolean isChat) {
        startActivity(activity,new Intent(activity,MainActivity.class)
        .putExtra(I.RESULT_CODE_IS_CHAT,isChat));
    }




    private static void startActivity(Context context, Class clazz) {
        context.startActivity(new Intent(context,clazz));
    }
    private static void startActivity(Context context,Intent intent) {
        context.startActivity(intent);
    }


    public static void gotoGuide(Activity activity) {
       startActivity(activity,GuideActivity.class);
    }
    public static void gotoRegister(Activity activity) {
        startActivity(activity, RegisterActivity.class);
    }

    public static void finish(Activity activity) {
        activity.finish();
    }

    public static void gotoSetting(Activity activity) {
        startActivity(activity,SettingsActivity.class);
    }

    public static void logout(Activity activity) {
        startActivity(activity,new Intent(activity,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }

    public static void gotoProfile(Activity activity) {
        startActivity(activity, UserProfileActivity.class);
    }
    public static void gotoProfile(Activity activity,User user) {
        startActivity(activity, new Intent(activity, ContactDetailsActivity.class)
                .putExtra(I.User.TABLE_NAME,user)
        );
    }
    public static void gotoProfile(Activity activity,String userName) {
        startActivity(activity, new Intent(activity, ContactDetailsActivity.class)
                .putExtra(I.User.USER_NAME,userName)
        );
    }

    public static void gotoProfiles(Activity activity, String userName) {
        startActivity(activity,new Intent(activity,AddContactsActivity.class).putExtra(I.User.USER_NAME,userName));
    }

    public static void gotoChat(Activity activity, String userName) {
        startActivity(activity,new Intent(activity,ChatActivity.class).putExtra("userId",userName));
    }

    public static void gotoVideo(Activity activity, String username) {
        if (!EMClient.getInstance().isConnected())
            Toast.makeText(activity, R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        else {
            startActivity(activity,new Intent(activity, VideoCallActivity.class).putExtra("username", username)
                    .putExtra("isComingCall", false));
        }
    }
}
