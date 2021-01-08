package com.sagoss.kiosklock.custompager


class CurrentViewPager {

    private lateinit var viewPager: CustomViewPager

    fun getViewPager(): CustomViewPager {
        return viewPager
    }

    fun setViewPager(viewPager: CustomViewPager) {
        this.viewPager = viewPager
    }

}
