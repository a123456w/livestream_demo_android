package cn.ucai.live.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.ucai.live.LiveHelper;
import cn.ucai.live.R;
import cn.ucai.live.data.Result;
import cn.ucai.live.data.model.Wallet;
import cn.ucai.live.data.restapi.LiveException;
import cn.ucai.live.data.restapi.LiveManager;
import cn.ucai.live.utils.CommonUtils;
import cn.ucai.live.utils.L;

/**
 * Created by Administrator on 2017/6/14 0014.
 */
public class LivePersonalActivity extends BaseActivity {
    private static final String TAG = "LivePersonalActivity";
    @BindView(R.id.iv_userinfo_avatar)
    ImageView ivUserinfoAvatar;
    @BindView(R.id.tv_userinfo_nick)
    TextView tvUserinfoNick;
    @BindView(R.id.tv_userinfo_name)
    TextView tvUserinfoName;
    @BindView(R.id.tv_userinfo_money)
    TextView tvUserinfoMoney;
    Unbinder bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_live);
        bind = ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        loadMoneyInfo();
        setUserInfo();
    }

    private void setUserInfo() {
        EaseUserUtils.setAppUserAvatar(this, EMClient.getInstance().getCurrentUser(), ivUserinfoAvatar);
        tvUserinfoName.setText(LiveHelper.getInstance().getCurrentAppUserInfo().getMUserName());
        tvUserinfoNick.setText(LiveHelper.getInstance().getCurrentAppUserInfo().getMUserNick());
    }

    private void loadMoneyInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean isSuccess = false;

                    Result<Wallet> result = LiveManager.getInstance().getBalance(LiveHelper.getInstance().getCurrentAppUserInfo().getMUserName());
                    L.e(TAG, "result=" + result + ",name=" + LiveHelper.getInstance().getCurrentUsernName());
                    if (result != null) {
                        if (result.isRetMsg()) {
                            isSuccess = true;
                            final Wallet retData = result.getRetData();
                            if (retData != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvUserinfoMoney.setText("￥ " + retData.getBalance());
                                    }
                                });

                            }
                        }
                    }
                    if (!isSuccess) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.showLongToast(R.string.get_no_money);
                            }
                        });
                    }

                } catch (LiveException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.showLongToast(R.string.get_no_money);
                        }
                    });
                }
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
        }
    }

}
