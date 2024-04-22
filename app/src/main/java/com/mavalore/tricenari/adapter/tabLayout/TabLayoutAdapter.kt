package com.mavalore.tricenari.adapter.tabLayout

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mavalore.tricenari.presentation.fragments.JoinedEventsFragment
import com.mavalore.tricenari.presentation.fragments.UpcomingEventsFragment

class TabLayoutAdapter(activity: FragmentActivity):FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> UpcomingEventsFragment()
            1-> JoinedEventsFragment()
            else -> { throw IllegalArgumentException("Invalid position")}
        }
    }
}