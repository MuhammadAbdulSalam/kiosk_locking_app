package com.sagoss.kiosklock.custompager

import com.sagoss.kiosklock.utils.RuntimeDataHelper

class NavigationHelper {

    fun navigatePage(navigation: Int) {
        val currentViewPager: CurrentViewPager = RuntimeDataHelper.currentViewPager
        if (navigation == Companion.FORWARD) {
            currentViewPager.getViewPager().setCurrentItem(currentViewPager.getViewPager().getItem(+1), true)
        } else {
            currentViewPager.getViewPager()
                .setCurrentItem(currentViewPager.getViewPager().getItem(-1), true)
        }
    }

    companion object {
        const val FORWARD = 1
        const val BACKWARD = 0
    }
}