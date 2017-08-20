package com.imperial.jack.pbdappstore;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ListItemAdapter extends BaseAdapter implements OnClickListener {
    private List<String> mList;
    private Context mContext;
    private InnerItemOnclickListener mListener;

    public ListItemAdapter(List<String> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item,
                    null);
            viewHolder.bt1 = (Button) convertView.findViewById(R.id.bt1);
            viewHolder.bt2 = (Button) convertView.findViewById(R.id.bt2);
            viewHolder.tv = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.bt1.setOnClickListener(this);
        viewHolder.bt2.setOnClickListener(this);
        viewHolder.bt1.setTag(position);
        viewHolder.bt2.setTag(position);
        viewHolder.tv.setText(mList.get(position));
        return convertView;
    }

    public final static class ViewHolder {
        Button bt1, bt2;
        TextView tv;
    }

    interface InnerItemOnclickListener {
        void itemClick(View v);
    }

    public void setOnInnerItemOnClickListener(InnerItemOnclickListener listener){
        this.mListener=listener;
    }

    @Override
    public void onClick(View v) {
        mListener.itemClick(v);
    }
}