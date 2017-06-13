package cn.ucai.live.ui.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ucai.live.R;
import cn.ucai.live.data.model.Gift;

/**
 * Created by Administrator on 2017/6/12 0012.
 */

public class LivegiftAdapter extends RecyclerView.Adapter<LivegiftAdapter.GiftHolder> {
    List<Gift> list;
    Context context;

    public LivegiftAdapter(List<Gift> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public GiftHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GiftHolder(View.inflate(context, R.layout.item_gift, null));
    }

    @Override
    public void onBindViewHolder(GiftHolder holder, int position) {
        Gift gift = list.get(position);
        holder.tvGiftName.setText(gift.getGname());
        holder.tvGiftPrice.setText(String.valueOf(gift.getGprice()));
        EaseUserUtils.setGiftAvatar(context,gift.getGurl(),holder.ivGiftThumb);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class GiftHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.ivGiftThumb)
        ImageView ivGiftThumb;
        @BindView(R.id.tvGiftName)
        TextView tvGiftName;
        @BindView(R.id.tvGiftPrice)
        TextView tvGiftPrice;

        GiftHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
