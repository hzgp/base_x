package com.frame.base.inter.ui

/**
 * Desc:
 * Author:zhujb
 * Date:2020/4/30
 */
interface IView {
    /**
     * Show error message
     *
     * @param errorMsg error message
     */
    fun showErrorMsg(errorMsg: String?)
    fun showError()
    fun showNoNetwork()
    fun loginOut()
}