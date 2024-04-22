package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentEventBookingBinding

class EventBookingFragment : Fragment() {

    private var _binding: FragmentEventBookingBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_event_booking, container, false)


        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}