package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentDashboardBinding
import com.mavalore.tricenari.utils.Const

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_dashboard, container, false)

       val userImageUrl = Const.fireBaseAuth().currentUser?.photoUrl
        Glide.with(requireContext())
            .load(userImageUrl)
            .into(binding.ivUserDashboardProfile)

        binding.cvEvents.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_eventFragment)
        }

        binding.cvQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_quizFragment)
        }

        binding.cvArticles.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_articleListFragement)
        }

        binding.cvSuperWomen.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_superWomenFragment)
        }

        binding.lottieAnimation.setOnClickListener {
           val showPrizeWheel = PrizeWheelFragment()
            showPrizeWheel.show((activity as AppCompatActivity).supportFragmentManager,"showPrizeWheel")
        }

        binding.ivUserDashboardProfile.setOnClickListener{
            findNavController().navigate(R.id.action_dashboardFragment_to_profileFragment)
        }


        if (Const.dashboardAdIsVisible){
            binding.cvDashboardAd.visibility = View.VISIBLE
        }else{
            binding.cvDashboardAd.visibility = View.GONE
        }

        binding.ivCloseDashboardAd.setOnClickListener {
            if (Const.dashboardAdIsVisible){
                Const.dashboardAdIsVisible = false
                binding.cvDashboardAd.visibility = View.GONE
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.lottieAnimation.clearAnimation()

        _binding = null
    }


}