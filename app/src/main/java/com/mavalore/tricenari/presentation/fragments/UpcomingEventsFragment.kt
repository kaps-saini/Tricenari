package com.mavalore.tricenari.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentEventBinding
import com.mavalore.tricenari.databinding.FragmentUpcomingEventsBinding
import com.mavalore.tricenari.presentation.activity.EventInfoActivity

class UpcomingEventsFragment : Fragment() {

    private var _binding: FragmentUpcomingEventsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_upcoming_events, container, false)

        binding.layoutSample.imageView26.setOnClickListener {
//            val destination = UpcomingEventsFragmentDirections.actionUpcomingEventsFragmentToEventBookingFragment(1)
//            findNavController().navigate(destination)
            val intent = Intent(requireActivity(),EventInfoActivity::class.java)
            intent.putExtra("eventId", 1)
            startActivity(intent)
        }


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}