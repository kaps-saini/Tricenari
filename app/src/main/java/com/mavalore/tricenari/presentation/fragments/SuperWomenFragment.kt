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
import androidx.recyclerview.widget.LinearLayoutManager
import com.mavalore.tricenari.R
import com.mavalore.tricenari.adapter.recyclerView.LatestSuperWomenAdapter
import com.mavalore.tricenari.adapter.recyclerView.PopularSuperWomenAdapter
import com.mavalore.tricenari.databinding.FragmentSuperWomenBinding
import com.mavalore.tricenari.domain.models.superwomen.SuperWomenData
import com.mavalore.tricenari.domain.models.superwomen.SuperWomenResponse
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Const
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SuperWomenFragment : Fragment() {

    private var _binding: FragmentSuperWomenBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TriceNariViewModel>()
    private lateinit var popularSuperWomenAdapter: PopularSuperWomenAdapter
    private lateinit var latestSuperWomenAdapter: LatestSuperWomenAdapter

    private lateinit var listOfPopularSW: List<SuperWomenData>

    @Inject
    lateinit var alertDialogBox: AlertDialogBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_super_women, container, false)

        binding.ivSuperWomenBack.setOnClickListener {
            findNavController().navigateUp()
        }

        setupPopularRecyclerView()
        setupLatestRecyclerView()

        viewModel.superWomenResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resources.Error -> errorResponse(response)
                is Resources.Loading -> loadingResponse()
                is Resources.Success -> successResponse(response)
            }
        }

        viewModel.dynamicValuesResponse.observe(viewLifecycleOwner) { response1 ->
            when (response1) {
                is Resources.Error -> {
                    Log.i("ArticleList", response1.message.toString())
                }

                is Resources.Loading -> loadingResponse()
                is Resources.Success -> {
                    response1.data?.data?.popular_superwomen?.let { ids ->
                        val idList: List<Long> = ids.map { it.id.toLong() }.toList()
                        filterArticlesByIds(idList)
                    }
                }
            }
        }

        popularSuperWomenAdapter.setOnItemClickListener(object :
            PopularSuperWomenAdapter.OnClickListener {
            override fun onClick(
                position: Int,
                dataItem: Long,
                currentWomen: SuperWomenData,
                nextWomen: SuperWomenData?
            ) {
                val destination = SuperWomenFragmentDirections
                    .actionSuperWomenFragmentToSuperWomenDetailFragment(
                        currentWomen,
                        nextWomen
                    )
                findNavController().navigate(destination)
            }

        })

        latestSuperWomenAdapter.setOnItemClickListener(object :
            LatestSuperWomenAdapter.OnClickListener {
            override fun onClick(
                position: Int,
                dataItem: Long,
                articleData: SuperWomenData,
                nextArticle: SuperWomenData?
            ) {
                val destination =
                    SuperWomenFragmentDirections
                        .actionSuperWomenFragmentToSuperWomenDetailFragment(
                            articleData,
                            nextArticle
                        )
                findNavController().navigate(destination)

            }

        })

        if (Const.superWomenAdIsVisible) {
            binding.cvSuperWomenAd.visibility = View.VISIBLE
        } else {
            binding.cvSuperWomenAd.visibility = View.GONE
        }

        binding.ivCloseSWAd.setOnClickListener {
            Const.superWomenAdIsVisible = false
            binding.cvSuperWomenAd.visibility = View.GONE
        }
        return binding.root
    }

    private fun successResponse(response: Resources.Success<SuperWomenResponse>) {
        listOfPopularSW = response.data?.data as List<SuperWomenData>

        latestSuperWomenAdapter.differ.submitList(response.data?.data?.sortedByDescending { it.id })
    }

    private fun loadingResponse() {
        Toast.makeText(requireContext(), "Loading..", Toast.LENGTH_SHORT).show()
    }

    private fun errorResponse(response: Resources.Error<SuperWomenResponse>) {
        if (response.message?.contains("No internet") == true) {
            alertDialogBox.showNoInternetDialog(requireContext())
        } else {
            Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun setupLatestRecyclerView() {
        latestSuperWomenAdapter = LatestSuperWomenAdapter()
        binding.rvLatestSuperWomen.apply {
            this.adapter = latestSuperWomenAdapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupPopularRecyclerView() {
        popularSuperWomenAdapter = PopularSuperWomenAdapter()
        binding.rvPopularSuperWomen.apply {
            this.adapter = popularSuperWomenAdapter
            this.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL, false
            )
        }
    }

    // Call filterArticlesByIds with a list of IDs to filter
    private fun filterArticlesByIds(idsToFilter: List<Long>) {
        //horizontalAdapter.filterByIds(idsToFilter)
        val filteredList = if (idsToFilter != null) {
            listOfPopularSW.filter { it.id.toLong() in idsToFilter }
        } else {
            listOfPopularSW
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(requireContext(), "Not found", Toast.LENGTH_SHORT).show()
        } else {
            popularSuperWomenAdapter.differ.submitList(filteredList)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}