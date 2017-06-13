package cn.ucai.live.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ucai.live.LiveHelper;
import cn.ucai.live.R;

/**
 * Created by Administrator on 2017/6/13 0013.
 */
public class BillActivity extends BaseActivity {
    @BindView(R.id.rv_gift_bill)
    RecyclerView rvGiftBill;
    LinearLayoutManager manager;
    ArrayList<LiveHelper.GiftBill> mlist;
    BillAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initView() {
        manager=new LinearLayoutManager(BillActivity.this);
        adapter=new BillAdapter(BillActivity.this,mlist);
        rvGiftBill.setLayoutManager(manager);
        rvGiftBill.setAdapter(adapter);
    }

    private void initData() {
        mlist = new ArrayList<>();
        Map<Integer, LiveHelper.GiftBill> map = LiveHelper.getInstance().getGiftBillMap();
        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()) {
            Integer key = it.next();
            LiveHelper.GiftBill bill = map.get(key);
            mlist.add(bill);
        }
    }

    class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillHolder> {
        Context context;
        List<LiveHelper.GiftBill> billList;

        public BillAdapter(Context context, List<LiveHelper.GiftBill> billList) {
            this.context = context;
            this.billList = billList;
        }



        @Override
        public BillAdapter.BillHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BillHolder(View.inflate(context, R.layout.item_bill, null));
        }

        @Override
        public void onBindViewHolder(BillAdapter.BillHolder holder, int position) {
            LiveHelper.GiftBill giftBill = billList.get(position);
            holder.setDate(giftBill);
        }

        @Override
        public int getItemCount() {
            return billList.size();
        }

        class BillHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.iv_gift)
            ImageView ivGift;
            @BindView(R.id.tv_gift_bill_name)
            TextView tvGiftBillName;
            @BindView(R.id.gift_number)
            TextView giftNumber;
            @BindView(R.id.gift_gprice)
            TextView giftGprice;

            BillHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }


            public void setDate(LiveHelper.GiftBill giftBill) {
                tvGiftBillName.setText(giftBill.getGift().getGname());
                giftGprice.setText(String.valueOf(giftBill.getGift().getGprice()));
                giftNumber.setText(String.valueOf(giftBill.getNumber()));
                EaseUserUtils.setGiftAvatar(context,giftBill.getGift().getGurl(),ivGift);
            }
        }
    }
}
