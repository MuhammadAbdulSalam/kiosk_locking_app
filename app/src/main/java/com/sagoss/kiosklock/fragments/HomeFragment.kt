package com.sagoss.kiosklock.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.sagoss.kiosklock.R


class HomeFragment(context: Context) : Fragment() {

    private val handler: Handler = Handler()
    private var pStatus = 0
    private var mContext : Context = context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        val aniRotateClk: Animation = AnimationUtils.loadAnimation(
            mContext,
            R.anim.roatate_clockwise
        )
        view.findViewById<ImageView>(R.id.img_sagoss_ring).startAnimation(aniRotateClk)

        return view
    }
}