package com.frame.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.frame.base.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Desc:
 * Author:Zhu
 * Date:2022/7/20
 */
public class RefreshListView extends SwipeRefreshLayout {
    private TextView tvEmptyNotice;
    private String emptyNotice;
    private int emptyIcon;
    private View emptyContainer;
    private RecyclerView recyclerView;
    public RefreshListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array=context.obtainStyledAttributes(attrs,R.styleable.RefreshListView);
        emptyIcon=array.getResourceId(R.styleable.RefreshListView_empty_icon,-1);
        emptyNotice=array.getString(R.styleable.RefreshListView_empty_notice);
        if (emptyNotice==null||emptyNotice.isEmpty()) emptyNotice="暂无数据";
        array.recycle();
        init();
    }

    private void init() {
        Context context=getContext();
        View parent = LayoutInflater.from(context).inflate(R.layout.layout_refresh_list_view,this,true);
        tvEmptyNotice=parent.findViewById(R.id.empty_notice);
        ImageView ivEmptyIcon = parent.findViewById(R.id.empty_icon);
        emptyContainer=parent.findViewById(R.id.empty);
        recyclerView=parent.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        if (emptyIcon!=-1) ivEmptyIcon.setImageResource(emptyIcon);
        tvEmptyNotice.setText(emptyNotice);
        setDistanceToTriggerSync(context.getResources().getDimensionPixelOffset(R.dimen.dp_150));
    }

    public void setAdapter(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter){
        recyclerView.setAdapter(adapter);
    }
    public void setEmptyNotice(String emptyNotice) {
        this.emptyNotice = emptyNotice;
    }
    public void showNoData(boolean isEmpty){
        showEmpty(isEmpty);
        if (isEmpty)  tvEmptyNotice.setText(emptyNotice);
    }
    public void showNetWorkError(){
        showEmpty(true);
        tvEmptyNotice.setText("请求错误，请尝试下拉刷新");
    }
    public void showEmpty(boolean isEmpty){
        if (isEmpty) {
            emptyContainer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyContainer.setVisibility(View.GONE);
        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
