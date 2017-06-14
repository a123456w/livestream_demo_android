package cn.ucai.live.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.live.LiveConstants;
import cn.ucai.live.LiveHelper;
import cn.ucai.live.data.Result;
import cn.ucai.live.data.model.Gift;
import cn.ucai.live.data.model.Wallet;
import cn.ucai.live.data.restapi.LiveException;
import cn.ucai.live.data.restapi.LiveManager;
import cn.ucai.live.data.restapi.model.StatisticsType;

import com.bumptech.glide.Glide;

import cn.ucai.live.R;
import cn.ucai.live.ThreadPoolManager;
import cn.ucai.live.data.restapi.model.LiveStatusModule;
import cn.ucai.live.utils.CommonUtils;
import cn.ucai.live.utils.L;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hyphenate.EMError;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.model.EasePreferenceManager;
import com.hyphenate.exceptions.HyphenateException;
import com.ucloud.uvod.UMediaProfile;
import com.ucloud.uvod.UPlayerStateListener;
import com.ucloud.uvod.widget.UVideoView;
import java.util.Random;

public class LiveAudienceActivity extends LiveBaseActivity implements UPlayerStateListener {

    String rtmpPlayStreamUrl = "rtmp://vlive3.rtmp.cdn.ucloud.com.cn/ucloud/";
    private UVideoView mVideoView;
    private UMediaProfile profile;

    @BindView(R.id.loading_layout)
    RelativeLayout loadingLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.loading_text)
    TextView loadingText;
    @BindView(R.id.cover_image)
    ImageView coverView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void loadAnchor(final String anchorId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = null;
                try {
                    user = LiveManager.getInstance().loadUserInfo(anchorId);
                    liveRoom.setLiveNick(user.getMUserNick());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initAnchorId();
                        }
                    });

                } catch (LiveException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onActivityCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_live_audience);
        ButterKnife.bind(this);

        switchCameraView.setVisibility(View.INVISIBLE);
        likeImageView.setVisibility(View.VISIBLE);


        Glide.with(this).load(liveRoom.getCover()).placeholder(R.color.placeholder).into(coverView);

        mVideoView = (UVideoView) findViewById(R.id.videoview);

        connect();
    }

    private void connect() {
        connectChatServer();
    }

    private void connectChatServer() {

        executeTask(new ThreadPoolManager.Task<LiveStatusModule.LiveStatus>() {
            @Override
            public LiveStatusModule.LiveStatus onRequest() throws HyphenateException {
                return LiveManager.getInstance().getLiveRoomStatus(liveId);
            }

            @Override
            public void onSuccess(LiveStatusModule.LiveStatus status) {
                loadingLayout.setVisibility(View.INVISIBLE);
                switch (status) {
                    case completed: //complete状态允许用户加入聊天室
                        showLongToast("直播已结束");
                    case ongoing:
                        connectLiveStream();
                        joinChatRoom();
                        break;
                    case closed:
                        showLongToast("直播间已关闭");
                        finish();
                        break;
                    case not_start:
                        showLongToast("直播尚未开始");
                        break;
                }

            }

            @Override
            public void onError(HyphenateException exception) {
                loadingLayout.setVisibility(View.INVISIBLE);
                showToast("加载失败");
            }
        });
    }

    private void joinChatRoom() {
        //loadingLayout.setVisibility(View.INVISIBLE);
        EMClient.getInstance()
                .chatroomManager()
                .joinChatRoom(chatroomId, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom emChatRoom) {
                        chatroom = emChatRoom;
                        addChatRoomChangeListener();
                        onMessageListInit();
                        //postUserChangeEvent(StatisticsType.JOIN, EMClient.getInstance().getCurrentUser());
                    }

                    @Override
                    public void onError(int i, String s) {
                        if (i == EMError.GROUP_PERMISSION_DENIED || i == EMError.CHATROOM_PERMISSION_DENIED) {
                            showLongToast("你没有权限加入此房间");
                            finish();
                        } else if (i == EMError.CHATROOM_MEMBERS_FULL) {
                            showLongToast("房间成员已满");
                            finish();
                        }
                        showLongToast("加入聊天室失败: " + s);
                    }
                });
    }

    private void connectLiveStream() {
        profile = new UMediaProfile();
        profile.setInteger(UMediaProfile.KEY_START_ON_PREPARED, 1);
        profile.setInteger(UMediaProfile.KEY_ENABLE_BACKGROUND_PLAY, 0);
        profile.setInteger(UMediaProfile.KEY_LIVE_STREAMING, 1);
        profile.setInteger(UMediaProfile.KEY_MEDIACODEC, 1);

        profile.setInteger(UMediaProfile.KEY_PREPARE_TIMEOUT, 1000 * 5);
        profile.setInteger(UMediaProfile.KEY_MIN_READ_FRAME_TIMEOUT_RECONNECT_INTERVAL, 3);

        profile.setInteger(UMediaProfile.KEY_READ_FRAME_TIMEOUT, 1000 * 5);
        profile.setInteger(UMediaProfile.KEY_MIN_PREPARE_TIMEOUT_RECONNECT_INTERVAL, 3);

        if (mVideoView != null && mVideoView.isInPlaybackState()) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
        }

        mVideoView.setMediaPorfile(profile);//set before setVideoPath
        mVideoView.setOnPlayerStateListener(this);//set before setVideoPath
        mVideoView.setVideoPath(liveRoom.getLivePullUrl());

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //if(getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE){
        messageView.getInputView().requestFocus();
        messageView.getInputView().requestFocusFromTouch();
        //}
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.onResume();
        if (isMessageListInited) messageView.refresh();
        EaseUI.getInstance().pushActivity(this);
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        // unregister this event listener when this activity enters the
        // background
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);

        // 把此activity 从foreground activity 列表里移除
        EaseUI.getInstance().popActivity(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isMessageListInited) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(chatroomId);

            //postUserChangeEvent(StatisticsType.LEAVE, EMClient.getInstance().getCurrentUser());
        }

        if (chatRoomChangeListener != null) {
            EMClient.getInstance()
                    .chatroomManager()
                    .removeChatRoomChangeListener(chatRoomChangeListener);
        }

        mVideoView.onDestroy();
    }

    @Override
    public void onPlayerStateChanged(State state, int i, Object o) {
        switch (state) {
            case START:
                isSteamConnected = true;
                isReconnecting = false;
                mVideoView.applyAspectRatio(UVideoView.VIDEO_RATIO_FILL_PARENT);//set after start
                break;
            case VIDEO_SIZE_CHANGED:
                break;
            case COMPLETED:
                Toast.makeText(this, "直播已结束", Toast.LENGTH_LONG).show();
                break;
            case RECONNECT:
                isReconnecting = true;
                break;
        }
    }

    @Override
    public void onPlayerInfo(Info info, int extra1, Object o) {
    }

    @Override
    public void onPlayerError(Error error, int extra1, Object o) {
        isSteamConnected = false;
        isReconnecting = false;
        switch (error) {
            case IOERROR:
                reconnect();
                break;
            case PREPARE_TIMEOUT:
                break;
            case READ_FRAME_TIMEOUT:
                System.out.println();
                break;
            case UNKNOWN:
                Toast.makeText(this, "Error: " + extra1, Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @OnClick(R.id.img_bt_close)
    void close() {
        finish();
    }

    int praiseCount;
    final int praiseSendDelay = 4 * 1000;
    private Thread sendPraiseThread;

    @OnClick(R.id.tv_GiftList)
    void giftList() {
        GiftManagementDialog dialog = GiftManagementDialog.newInstance();
        dialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int giftId = (int) v.getTag();
                LiveHelper.getInstance().setGiftBillMap(giftId);
                L.e(TAG, "LiveAudienceActivity.giftList....gift=" + giftId);
                showSendDialogPro(giftId);
            }
        });
        dialog.show(getSupportFragmentManager(), "GiftManagementDialog");
    }

    private void showSendDialogPro(int giftId) {
        if (EasePreferenceManager.getInstance().getIsShowDialog()) {
            sendGift(giftId);
        } else {
            showSendDialog(giftId);
        }
    }

    private void sendGift(final int giftId) {
        Gift gift = LiveHelper.getInstance().getGiftList().get(giftId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean isSuccess = false;
                    Result<Wallet> result = LiveManager.getInstance().givingGift(
                            LiveHelper.getInstance().getCurrentAppUserInfo().getMUserName()
                            , chatroomId, giftId, 1
                    );
                    if (result != null) {
                        if (result.isRetMsg()) {
                            isSuccess = true;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onPresentImage(giftId, LiveHelper.getInstance()
                                            .getCurrentAppUserInfo().getMUserNick());
                                }
                            });
                        }
                    }
                    if (!isSuccess) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.showShortToast(R.string.no_money);
                            }
                        });
                    }

                } catch (final LiveException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.showLongToast(e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    private void showSendDialog(final int giftId) {
        Gift gift = LiveHelper.getInstance().getGiftList().get(giftId);
        CheckBox cb = new CheckBox(this);
        cb.setText("不再显示");
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("提示")
                .setMessage("你确定要打赏主播" + gift.getGname() + "吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendGift(giftId);
                    }
                })
                .setView(cb)
                .show();
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EasePreferenceManager.getInstance().setIsShowDialog(isChecked);
            }
        });
    }

    /**
     * 点赞
     */
    @OnClick(R.id.like_image)
    void Praise() {
        periscopeLayout.addHeart();
        synchronized (this) {
            ++praiseCount;
        }
        if (sendPraiseThread == null) {
            sendPraiseThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!isFinishing()) {
                        int count = 0;
                        synchronized (LiveAudienceActivity.this) {
                            count = praiseCount;
                            praiseCount = 0;
                        }
                        if (count > 0) {
                            sendPraiseMessage(count);
                            try {
                                LiveManager.getInstance().postStatistics(StatisticsType.PRAISE, liveId, count);
                            } catch (LiveException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            Thread.sleep(praiseSendDelay + new Random().nextInt(2000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }
            });
            sendPraiseThread.setDaemon(true);
            sendPraiseThread.start();
        }
    }


    private void sendPraiseMessage(int praiseCount) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.setTo(chatroomId);
        EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody(LiveConstants.CMD_PRAISE);
        message.addBody(cmdMessageBody);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setAttribute(LiveConstants.EXTRA_PRAISE_COUNT, praiseCount);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    volatile boolean isSteamConnected;
    volatile boolean isReconnecting;

    Thread reconnectThread;

    /**
     * 重连到直播server
     */
    private void reconnect() {
        if (isSteamConnected || isReconnecting)
            return;
        if (reconnectThread != null && reconnectThread.isAlive())
            return;

        reconnectThread = new Thread() {
            @Override
            public void run() {
                while (!isFinishing() && !isSteamConnected) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isReconnecting) {
                                isReconnecting = true;
                                connectLiveStream();
                            }
                            //mVideoView.setVideoPath(liveRoom.getLivePullUrl());
                        }
                    });
                    try {
                        // TODO 根据reconnect次数动态改变sleep时间
                        Thread.sleep(3000 + new Random().nextInt(3000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        };
        reconnectThread.setDaemon(true);
        reconnectThread.start();


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("LiveAudience Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }
}
