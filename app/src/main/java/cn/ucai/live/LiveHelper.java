package cn.ucai.live;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.model.EasePreferenceManager;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.ucai.live.data.local.LiveDao;
import cn.ucai.live.data.model.Gift;
import cn.ucai.live.data.restapi.LiveException;
import cn.ucai.live.data.restapi.LiveManager;
import cn.ucai.live.ui.activity.MainActivity;
import cn.ucai.live.utils.L;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class LiveHelper {
    private static final String TAG = "LiveHelper";
    private String username;
    private static LiveHelper instance=null;
    private LiveModel model;
    private Context appContext;
    private User currentAppUser;
    private Map<Integer, Gift> giftMap;
    private EaseUI easeui=null;
    Map<String,User> audience=null;
    List<Gift> giftList;
    private LiveHelper() {
    }

    public synchronized static LiveHelper getInstance() {
        if (instance == null) {
            instance = new LiveHelper();
        }
        return instance;
    }
    /**
     * set current username
     *
     * @param username
     */
    public void setCurrentUserName(String username) {
        this.username = username;
        model.setCurrentUserName(username);
    }

    /**
     * get current user's id
     */
    public String getCurrentUsernName() {
        if (username == null) {
            username = model.getCurrentUsernName();
        }
        return username;
    }
    public void init(final Context context){
        appContext=context;
        model=new LiveModel();
        EaseUI.getInstance().init(context, null);
        EMClient.getInstance().setDebugMode(true);
        easeui=EaseUI.getInstance();
        setEaseUiProvider();
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override public void onConnected() {

            }

            @Override public void onDisconnected(int error) {

                    EMLog.d("global listener", "onDisconnect" + error);
                    if (error == EMError.USER_REMOVED) {
                        onUserException(LiveConstants.ACCOUNT_REMOVED);
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        onUserException(LiveConstants.ACCOUNT_CONFLICT);
                    } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                        onUserException(LiveConstants.ACCOUNT_FORBIDDEN);
                    }
            }
        });
    }

    private void setEaseUiProvider() {
        easeui.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String username) {
                return null;
            }

            @Override
            public User getAppUser(String username) {
                return getAppUserInfo(username);
            }
        });
    }


    private User getAppUserInfo(final String username) {
        User user = null;
        if(username.equals(EMClient.getInstance().getCurrentUser())){
            user = getCurrentAppUserInfo();
        }
        user=getAudience().get(username);
        // if user is not in your contacts, set inital letter for him/her
        if(user == null){
            user = new User(username);
            //EaseCommonUtils.setAppUserInitialLetter(user);
        }
        return user;
    }

    public Map<String, User> getAudience() {
        if(audience==null){
            audience=new Hashtable<>();
        }
        return audience;
    }
    public void saveAudience(User user){
        audience.put(user.getMUserName(),user);
    }

    protected void onUserException(String exception) {
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(exception, true);
        appContext.startActivity(intent);
    }

    public void syncUserInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    User user= LiveManager.getInstance().loadUserInfo(EMClient.getInstance().getCurrentUser());
                            if(user!=null){
                                setCurrentAppUserNick(user.getMUserNick());
                                setCurrentAppUserAvatar(user.getAvatar());
                            }
                } catch (LiveException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setCurrentAppUserAvatar(String avatar) {
        getCurrentAppUserInfo().setAvater(avatar);
        EasePreferenceManager.getInstance().setCurrentUserAvatar(avatar);
    }



    private void setCurrentAppUserNick(String nickname) {
        getCurrentAppUserInfo().setMUserNick(nickname);
        EasePreferenceManager.getInstance().setCurrentUserNick(nickname);
    }
    public synchronized User getCurrentAppUserInfo() {
        if (currentAppUser == null) {
            String username = EMClient.getInstance().getCurrentUser();
            currentAppUser = new User(username);
            String nick = getCurrentUserNick();
            currentAppUser.setMUserNick((nick != null) ? nick : username);
            currentAppUser.setAvater(getCurrentUserAvatar());
            Log.i("main", "UserProfileManager.user.avatar:" + getCurrentUserAvatar());
        }
        return currentAppUser;
    }
    private String getCurrentUserNick() {
        return EasePreferenceManager.getInstance().getCurrentUserNick();
    }

    private String getCurrentUserAvatar() {
        return EasePreferenceManager.getInstance().getCurrentUserAvatar();
    }
    public synchronized void reset() {
               currentAppUser = null;
                EasePreferenceManager.getInstance().removeCurrentUserInfo();
    }

    public void setGiftList(Map<Integer, Gift> list) {
        if (list == null) {
            if (giftMap != null) {
                giftMap.clear();
            }
            return;
        }

        giftMap = list;
    }
    public Map<Integer, Gift> getGiftList() {
        if (giftMap == null) {
            giftMap = model.getGiftList();
        }

        // return a empty non-null object to avoid app crash
        if (giftMap == null) {
            giftMap=new Hashtable<Integer, Gift>();
        }

        return giftMap;
    }
    public void getGiftListFromServer(){
        if(getGiftList().size()==0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Map<Integer, Gift> map=new HashMap<>();
                        List<Gift> gifts = LiveManager.getInstance().loadGiftList();
                        Log.e(TAG,"getGiftListFromServer.gifts="+gifts);
                        for (Gift gift : gifts) {
                            map.put(gift.getId(),gift);
                        }
                        //
                        setGiftList(map);
                        //
                        LiveDao dao=new LiveDao();
                        dao.setGiftList(gifts);
                        dao.getGiftList();
                        L.e(TAG,"getGiftListFromServer.dao.getGiftList().size()="+dao.getGiftList().size());
                    } catch (LiveException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public List<Gift> getGiftLists() {
        if(giftList==null){
            giftList=new ArrayList<>();
        }
        if(getGiftList().size()>0){
            Iterator<Integer> it =getGiftList().keySet().iterator();
            while (it.hasNext()){
                Integer key = it.next();
                giftList.add(giftMap.get(key));
            }
            Collections.sort(giftList, new Comparator<Gift>() {
                @Override
                public int compare(Gift o1, Gift o2) {
                    return o1.getGprice()-o2.getGprice();
                }
            });
        }
        return giftList;
    }
    Map<Integer,GiftBill> map;

    public void setGiftBillMap(int giftId) {
        if(getGiftBillMap().containsKey(giftId)){
            getGiftBillMap().get(giftId).setNumber();
            return;
        }
        GiftBill giftBill = new GiftBill();
        giftBill.setGift(getGiftList().get(giftId));
        giftBill.setNumber();
        getGiftBillMap().put(giftId,giftBill);
    }
    public Map<Integer,GiftBill> getGiftBillMap(){
        if(map==null){
            map=new Hashtable<>();
        }
        return map;
    }

    public class GiftBill  {
        private Gift gift;
        private int number;

        public int getNumber() {
            return number;
        }

        public void setNumber() {
            this.number = ++number;
        }

        public Gift getGift() {
            return gift;
        }

        public void setGift(Gift gift) {
            this.gift = gift;
        }




    }
}
