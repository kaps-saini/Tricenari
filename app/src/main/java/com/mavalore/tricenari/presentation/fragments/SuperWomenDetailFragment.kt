package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
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
import com.mavalore.tricenari.databinding.FragmentSuperWomenDetailBinding
import com.mavalore.tricenari.domain.models.superwomen.SingleSuperWomenResponse
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
    private lateinit var nextName:String
    private lateinit var nextRole:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_super_women_detail, container, false)

        val currentWomenId = args.CurrentWomenData.id
        var nextWomenId = currentWomenId.plus(1)

        viewModel.getWomenDetailsById(currentWomenId)
        viewModel.singleSuperWomenResponse.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error -> errorResponse(response)
                is Resources.Loading -> loadingResponse()
                is Resources.Success -> successResponse(response)
                else -> {}
            }
        }

        viewModel.getNextWomenDetailsById(nextWomenId)
        viewModel.nextSuperWomenInfo.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error ->{
                    if (response.message?.contains("No internet") == true){
                        alertDialogBox.showNoInternetDialog(requireContext())
                    }else{
                        Toast.makeText(requireContext(),response.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading -> loadingResponse()
                is Resources.Success ->{
                    if (response.data?.data != null){
                        response.data.data.let {
                            binding.tvNextSWSummary.text = it.name
                            Glide.with(requireContext())
                                .load("https://www.tricenari.com/superwomenPages/pics/${it.id}.jpg")
                                .into(binding.ivNextSWPic)

                            nextName = it.name
                            nextRole = it.role

                            binding.scSuperWomen.visibility = View.VISIBLE
                            binding.shimmerSuperWomen.visibility = View.GONE
                            binding.shimmerSuperWomen.stopShimmer()

                        }
                    }else{
                        binding.nextSwLayout.visibility = View.GONE
                    }

                }
                else -> {}
            }
        }


        binding.ivSWBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivShimmerBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvNextSWSummary.setOnClickListener {
            viewModel.getWomenDetailsById(nextWomenId)
            nextWomenId++
            viewModel.getNextWomenDetailsById(nextWomenId)

            binding.tvSWName.text = nextName
            binding.tvLivingPlace.text = nextRole

            binding.scSuperWomen.smoothScrollTo(0,0)

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

        binding.scSuperWomen.visibility = View.VISIBLE
        binding.shimmerSuperWomen.visibility = View.GONE
        binding.shimmerSuperWomen.stopShimmer()
    }

    private fun loadingResponse() {
        binding.scSuperWomen.visibility = View.GONE
        binding.shimmerSuperWomen.visibility = View.VISIBLE
        binding.shimmerSuperWomen.startShimmer()
    }

    private fun errorResponse(response: Resources.Error<SingleSuperWomenResponse>) {
        if (response.message?.contains("No internet") == true){
            alertDialogBox.showNoInternetDialog(requireContext())
        }else{
            Toast.makeText(requireContext(),response.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.shimmerSuperWomen.stopShimmer()

        _binding = null
    }

}