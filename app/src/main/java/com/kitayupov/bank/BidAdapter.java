package com.kitayupov.bank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class BidAdapter extends BaseAdapter {

    private Context context;
    private List<Bid> bids;

    public BidAdapter(Context context, List<Bid> bids) {
        this.context = context;
        this.bids = bids;
    }

    @Override
    public int getCount() {
        return bids.size();
    }

    @Override
    public Bid getItem(int position) {
        return bids.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.bid_item_layout, null);
        }
        fillView(view, position);
        return view;
    }

    private void fillView(View view, int position) {
        Bid item = getItem(position);

        TextView descriptionTextView = (TextView) view.findViewById(R.id.description_textview);
        TextView nameTextView = (TextView) view.findViewById(R.id.name_textview);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView statusTextView = (TextView) view.findViewById(R.id.status_textview);

        descriptionTextView.setText(item.getDescription());
        nameTextView.setText(item.getName());
        imageView.setImageResource(item.getImgRes());

        String status = "На рассмотрении";

        if (item.getStatusA().equals(Constants.Status.ACCEPT) &&
                item.getStatusB().equals(Constants.Status.ACCEPT)) {
            status = "Согласовано";
        }

        if (item.getStatusA().equals(Constants.Status.DENY) ||
                item.getStatusB().equals(Constants.Status.DENY)) {
            status = "Отказано";
        }

        statusTextView.setText(status);
//
//        switch (item.getStatusA()) {
//            case WAIT:
//                status.setText("На рассмотрении");
//                break;
//            case DENY:
//                status.setText("Отказано");
//                break;
//            case ACCEPT:
//                status.setText("Согласовано");
//                break;
//        }

    }
}
