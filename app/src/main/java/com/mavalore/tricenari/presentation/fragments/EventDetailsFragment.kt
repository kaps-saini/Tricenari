package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentEventDetailsBinding
import com.mavalore.tricenari.domain.models.event.EventDetailsResponse
import com.mavalore.tricenari.domain.models.event.Faq
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.helper.CheckInternetConnection
import com.mavalore.tricenari.helper.Helper
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EventDetailsFragment : Fragment() {

    private var _binding: FragmentEventDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TriceNariViewModel>()
    private val args:EventDetailsFragmentArgs by navArgs()

    @Inject
    lateinit var checkInternetConnection: CheckInternetConnection

    @Inject
    lateinit var alertDialogBox: AlertDialogBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_event_details, container, false)


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}