package com.d9tilov.currencyapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.d9tilov.currencyapp.di.ComponentHolder

abstract class BaseFragment : Fragment() {

    abstract val layoutRes: Int
    abstract val componentName: String
    abstract val fragmentTag: String
    abstract val isComponentDestroyable: Boolean

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutRes, container, false)

    override fun onDestroy() {
        super.onDestroy()
        if (needReleaseComponent()) {
            ComponentHolder.releaseComponent(componentName)
        }
    }

    private fun needReleaseComponent() = when {
        activity?.isChangingConfigurations == true -> false
        activity?.isFinishing == true -> true
        else -> isRealRemoving() && isComponentDestroyable
    }

    private fun isRealRemoving(): Boolean =
        (isRemoving) //because isRemoving == true for fragment in backstack on screen rotation
                || ((parentFragment as? BaseFragment)?.isRealRemoving() ?: false)

    fun processBackButton(): Boolean {
        val childFragments = childFragmentManager.fragments
        for (childFragment in childFragments) {
            if (childFragment is BaseFragment && childFragment.processBackButton()) {
                return true
            }
        }

        // Дочерних фрагментов нет, или никто из них событие не поглотил - передаём обработку в
        // текущий фрагмент
        return onBackPressed()
    }

    open fun onBackPressed(): Boolean {
        return false
    }
}
