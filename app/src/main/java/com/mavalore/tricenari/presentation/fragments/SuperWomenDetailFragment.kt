package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.mavalore.tricenari.R
import com.mavalore.tricenari.adapter.recyclerView.LatestSuperWomenAdapter
import com.mavalore.tricenari.adapter.recyclerView.PopularSuperWomenAdapter
import com.mavalore.tricenari.databinding.FragmentEventBinding
import com.mavalore.tricenari.databinding.FragmentSuperWomenBinding
import com.mavalore.tricenari.databinding.FragmentSuperWomenDetailBinding
import com.mavalore.tricenari.domain.models.SingleArticleResponse
import com.mavalore.tricenari.domain.models.SingleSuperWomenResponse
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SuperWomenDetailFragment : Fragment() {

    private var _binding: FragmentSuperWomenDetailBinding? = null
    private val binding get() = _binding!!


    private val viewModel by viewModels<TriceNariViewModel>()

    @Inject
    lateinit var alertDialogBox: AlertDialogBox

    private val args: SuperWomenDetailFragmentArgs by navArgs()

    private var womenDataId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_super_women_detail, container, false)

        womenDataId = args.CurrentWomenData.id
        viewModel.getWomenDetailsById(womenDataId)
        viewModel.singleSuperWomenResponse.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error -> errorResponse(response)
                is Resources.Loading -> loadingResponse()
                is Resources.Success -> successResponse(response)
                else -> {}
            }
        }

        binding.ivSWBack.setOnClickListener {
            findNavController().navigateUp()
        }

        womenDataId += 1

        if (args.NextSuperWomen != null){
            viewModel.getNextWomenDetailsById(womenDataId)
        }else{
            binding.ivNextSWPic.visibility = View.INVISIBLE
            binding.tvNextSWSummary.visibility = View.INVISIBLE
        }

        showNextWomenSummaryAndImageData()
        binding.tvNextSWSummary.setOnClickListener {

            showNextWomenData()
            showNextWomenSummaryAndImageData()
        }

        return binding.root
    }

    private fun successResponse(response: Resources.Success<SingleSuperWomenResponse>) {
        binding.tvSWName.text = args.CurrentWomenData.name
        val story = viewModel.removeTags(response.data?.data?.story.toString())
        binding.tvDescription.text = story
        val role = args.CurrentWomenData.role
        binding.tvLivingPlace.text = role
        val quote = viewModel.removeTags(response.data?.data?.quote.toString())
        binding.tvSWQuote.text = quote.trim()
        val summary = viewModel.removeTags(response.data?.data?.summary.toString())
        binding.tvSummary.text = summary

        val imageUrl = response.data?.data?.picurl?.trim()
        Glide.with(requireContext())
            .load("https://www.tricenari.com/${imageUrl}")
            .into(binding.ivSWPic)
    }

    private fun loadingResponse() {
        Toast.makeText(requireContext(),"Loading..", Toast.LENGTH_SHORT).show()
    }

    private fun errorResponse(response: Resources.Error<SingleSuperWomenResponse>) {
        if (response.message?.contains("No internet") == true){
            alertDialogBox.showNoInternetDialog(requireContext())
        }else{
            Toast.makeText(requireContext(),response.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun showNextWomenData() {
        viewModel.nextSingleSuperWomenResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resources.Error -> errorResponse(response)
                is Resources.Loading -> loadingResponse()
                is Resources.Success -> {
                    successResponse(response)
                    womenDataId += 1
                    viewModel.getNextWomenDetailsById(womenDataId)
                }

                else -> {}
            }
        }
    }

    private fun showNextWomenSummaryAndImageData(){
        viewModel.nextSingleSuperWomenResponse.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error -> {
                    errorResponse(response)
                    binding.tvNextSWSummary.visibility = View.INVISIBLE
                    binding.ivNextSWPic.visibility = View.INVISIBLE
                }
                is Resources.Loading -> loadingResponse()
                is Resources.Success -> {
                    val nextSummary = response.data?.data?.summary.toString()
                    binding.tvNextSWSummary.text = nextSummary
                    val imageUrl = response.data?.data?.picurl?.trim()
                    Glide.with(requireContext())
                        .load("https://www.tricenari.com/${imageUrl}")
                        .into(binding.ivSWPic)
                }

                else -> {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}