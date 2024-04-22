package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentAllBlogsBinding
import com.mavalore.tricenari.databinding.FragmentQuizBinding
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TriceNariViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_quiz, container, false)



        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}