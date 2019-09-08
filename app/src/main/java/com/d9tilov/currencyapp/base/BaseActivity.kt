package com.d9tilov.currencyapp.base

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.main_progress_bar as progressBar

abstract class BaseActivity : AppCompatActivity() {

    fun setActionBarTitle(title: Int) {
        supportActionBar?.setTitle(title)
    }

    fun showBackButton(show: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(show)
    }

    fun showProgress() {
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgress() {
        progressBar.let {
            if (it.isShown) {
                it.visibility = View.GONE
            }
        }
    }

    override fun onBackPressed() {
        // Передача нажатия в текущее окно
        val contentFragment = getContentFragment()
        if (contentFragment != null && contentFragment.processBackButton()) {
            // Обработка нажатия перехвачена во фрагменте текущего окна
            return
        }
        super.onBackPressed()
    }

    abstract fun getFragmentContainerId(): Int

    private fun getContentFragment(): BaseFragment? {
        val fragment = supportFragmentManager.findFragmentById(getFragmentContainerId())
        return if (fragment is BaseFragment) {
            fragment
        } else null
    }
}