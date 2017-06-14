package cn.ucai.live.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.ucai.live.LiveHelper;
import cn.ucai.live.R;
import cn.ucai.live.data.model.Gift;

/**
 * Created by wei on 2017/3/3.
 */

public class GiftManagementDialog extends DialogFragment{

    @BindView(R.id.rv_gift)
    RecyclerView rvGift;
    @BindView(R.id.tv_my_bill)
    TextView tvMyBill;
    @BindView(R.id.tv_recharge)
    TextView tvRecharge;
    Unbinder unbinder;
    private GridLayoutManager mGridLayoutManager;
    private LivegiftAdapter adapter;
    private static List<Gift> list;



    View.OnClickListener ClickListener;

    public void setClickListener(View.OnClickListener clickListener) {
        this.ClickListener = clickListener;
    }
    public static GiftManagementDialog newInstance() {
        GiftManagementDialog dialog = new GiftManagementDialog();
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gift_list, container, false);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initListener();
        initView();

    }

    private void initListener() {
        tvMyBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),BillActivity.class));
            }
        });
    }

    private void initData() {
        list = LiveHelper.getInstance().getGiftLists();
        if(list==null){
            LiveHelper.getInstance().getGiftList();
        }
    }

    private void initView() {
        mGridLayoutManager = new GridLayoutManager(getContext(), 4);
        adapter = new LivegiftAdapter(list, getContext(),ClickListener);
        rvGift.setLayoutManager(mGridLayoutManager);
        rvGift.setAdapter(adapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
