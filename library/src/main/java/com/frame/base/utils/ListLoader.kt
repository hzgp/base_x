package com.frame.base.utils

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.frame.base.R
import com.frame.base.inter.ui.activity.AbstractSimpleActivity
import com.frame.base.inter.ui.fragment.AbstractSimpleFragment
import com.frame.base.widget.RefreshListView
import java.util.*

/**
 * Desc:
 * Author:zhujb
 * Date:2021/5/8
 */
class ListLoader<T>( val refreshLayout: RefreshListView, val adapter: BaseQuickAdapter<T, *>, private val context: Context, val config: Config) {
    fun initView() {
        refreshLayout.setOnRefreshListener {
            config.refresh()
        }
        adapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { _: BaseQuickAdapter<*, *>?, _: View?, position: Int ->
            config.listener.onItemClick(position)
        }
        refreshLayout.setAdapter(adapter)
        adapter.setOnLoadMoreListener({
            config.loadMore()
        }, refreshLayout.recyclerView)
    }

    fun updateList(list: IList<T>) {
        if (config.page == 1) {
            adapter.replaceData(ArrayList())
        } else {
            adapter.loadMoreComplete()
        }
        val hasNext = list.hasNext()
        if (hasNext) {
            adapter.setEnableLoadMore(true)
        } else {
            adapter.setEnableLoadMore(false)
        }
        adapter.addData(list.list()!!)
        refreshLayout.showNoData(adapter.itemCount<=0)
        refreshLayout.isRefreshing = false
    }

    fun page(): Int {
        return config.page
    }
    fun refreshPage():Int{
        config.page=1
        return config.page
    }
    fun refresh(){
        config.refresh()
    }

    class Config {
        private var attachActivity: AbstractSimpleActivity? = null
        private var attachFragment: AbstractSimpleFragment? = null

        constructor(attachActivity: AbstractSimpleActivity?, listener: OnItemClickListener) {
            this.attachActivity = attachActivity
            this.listener = listener
        }

        constructor(attachFragment: AbstractSimpleFragment?, listener: OnItemClickListener) {
            this.attachFragment = attachFragment
            this.listener = listener
        }

        var page = 1
        fun refresh() {
            page = 1
            loadDate()
        }

        fun loadMore() {
            page++
            loadDate()
        }

        fun loadDate() {
            if (attachActivity != null) {
                attachActivity!!.initEventAndData()
            } else if (attachFragment != null) {
                attachFragment!!.initEventAndData()
            }
        }

        var listener: OnItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface IList<T> {
        operator fun hasNext(): Boolean
        fun list(): List<T>?
    }
    class SinglePageList<T>(val list:List<T>?):IList<T>{
        override fun hasNext(): Boolean {
            return false
        }

        override fun list(): List<T>? {
            return list
        }
    }
    companion object {
        @JvmStatic
        fun initNormal(recyclerView: RecyclerView, adapter: BaseQuickAdapter<*, *>, refreshLayout: SwipeRefreshLayout, context: Context, config: Config) {
            refreshLayout.setOnRefreshListener { config.refresh() }
            refreshLayout.setDistanceToTriggerSync(context.resources.getDimensionPixelOffset(R.dimen.dp_150))
            config.listener.let {
                adapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { quickAdapter: BaseQuickAdapter<*, *>?, view: View?, position: Int -> it.onItemClick(position) }
            }
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = layoutManager
        }
    }
}