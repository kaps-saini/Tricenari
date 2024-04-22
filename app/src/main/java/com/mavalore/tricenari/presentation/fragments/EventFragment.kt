package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.mavalore.tricenari.R
import com.mavalore.tricenari.adapter.tabLayout.TabLayoutAdapter
import com.mavalore.tricenari.databinding.FragmentEventBinding

class EventFragment : Fragment() {


    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_event, container, false)

        // Attach the adapter to the ViewPager2
        binding.viewPager.adapter = TabLayoutAdapter(requireActivity())

        // Connect the TabLayout and ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            if (position == 0) {
                tab.text = "Upcoming"
            } else if (position == 1) {
                tab.text = "Joined"
            }
        }.attach()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}