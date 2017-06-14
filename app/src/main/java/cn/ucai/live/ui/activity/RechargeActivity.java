package cn.ucai.live.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.live.LiveHelper;
import cn.ucai.live.R;
import cn.ucai.live.data.Result;
import cn.ucai.live.data.model.Wallet;
import cn.ucai.live.data.restapi.LiveException;
import cn.ucai.live.data.restapi.LiveManager;
import cn.ucai.live.utils.CommonUtils;

/**
 * Created by Administrator on 2017/6/14 0014.
 */
public class RechargeActivity extends BaseActivity {
    @BindView(R.id.etRecharge)
    EditText etRecharge;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnRecharge)
    public void onViewClicked() {
        final String trim = etRecharge.getText().toString().trim();
        if (!trim.matches("[0-9]+")) {
            CommonUtils.showLongToast("非法输入");
            return;
        }
        final int rmb = Integer.parseInt(trim);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean isSuccess = false;
                    final Result<Wallet> result = LiveManager.getInstance().recharge(LiveHelper.getInstance().getCurrentAppUserInfo().getMUserName(), rmb);
                    if (result != null) {
                        if (result.isRetMsg()) {
                            isSuccess = true;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CommonUtils.showLongToast("充值成功，余额为￥" + result.getRetData().getBalance());
                                    RechargeActivity.this.finish();
                                }
                            });
                        }
                    }
                    if (!isSuccess) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.showLongToast("充值失败");
                            }
                        });
                    }
                } catch (LiveException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.showLongToast("充值失败");
                        }
                    });
                }
            }
        }).start();

    }
}
