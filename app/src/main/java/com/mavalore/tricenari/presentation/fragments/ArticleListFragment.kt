package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mavalore.tricenari.R
import com.mavalore.tricenari.adapter.recyclerView.ArticleHorizontalAdapter
import com.mavalore.tricenari.adapter.recyclerView.ArticleVerticleAdapter
import com.mavalore.tricenari.databinding.FragmentArticleListFragementBinding
import com.mavalore.tricenari.domain.models.article.AllArticleResponse
import com.mavalore.tricenari.domain.models.article.ArticleData
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Const
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ArticleListFragment : Fragment() {

    private var _binding: FragmentArticleListFragementBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TriceNariViewModel>()
    private lateinit var verticalArticleAdapter: ArticleVerticleAdapter
    private lateinit var horizontalAdapter: ArticleHorizontalAdapter
    @Inject
    lateinit var alertDialogBox: AlertDialogBox

    private lateinit var dataList:List<ArticleData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_list_fragement,
            container, false)

        setupRecyclerView()
        setupPopularRecyclerView()

        viewModel.allArticleResponse.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error -> errorResponse(response)
                is Resources.Loading -> loadingResponse()
                is Resources.Success -> successResponse(response)
            }
        }
        viewModel.dynamicValuesResponse.observe(viewLifecycleOwner){response1->
            when(response1){
                is Resources.Error -> {
                    Log.i("ArticleList",response1.message.toString())
                }
                is Resources.Loading -> loadingResponse()
                is Resources.Success -> {
                    response1.data?.data?.popularArticles?.let {ids->
                        val idList: List<Long> = ids.map { it.toLong() }.toList()
                        filterArticlesByIds(idList)
                    }
                }
            }
        }

        verticalArticleAdapter.setOnItemClickListener(object : ArticleVerticleAdapter.OnClickListener{
            override fun onClick(
                position: Int,
                dataItem: Long,
                articleData: ArticleData,
                nextArticle: ArticleData?
            ) {
                val destination = ArticleListFragmentDirections
                    .actionArticleListFragementToArticleDetailsFragment(articleData,nextArticle)
                findNavController().navigate(destination)
            }
        })

        horizontalAdapter.setOnItemClickListener(object :ArticleHorizontalAdapter.OnClickListener{
            override fun onClick(
                position: Int,
                dataItem: Long,
                articleData: ArticleData,
                nextArticle: ArticleData?
            ) {
                val destination = ArticleListFragmentDirections
                    .actionArticleListFragementToArticleDetailsFragment(articleData,nextArticle)
                findNavController().navigate(destination)
            }

        })

        binding.ivArticleListBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivCloseArticleAd.setOnClickListener {
            if (Const.articleAdIsVisible){
                Const.articleAdIsVisible = false
                binding.cvArticleAd.visibility = View.GONE
            }
        }

        if (Const.articleAdIsVisible){
            binding.cvArticleAd.visibility = View.VISIBLE
        }else{
            binding.cvArticleAd.visibility = View.GONE
        }



        return binding.root
    }

    private fun successResponse(response: Resources.Success<AllArticleResponse>) {
        verticalArticleAdapter.differ.submitList(response.data?.data?.sortedByDescending { it.id })

        //horizontalAdapter.differ.submitList(response.data?.data)
        dataList = response.data?.data as List<ArticleData>
    }

    private fun loadingResponse() {
        Toast.makeText(requireContext(),"Loading..",Toast.LENGTH_SHORT).show()
    }

    private fun errorResponse(response: Resources.Error<AllArticleResponse>) {
        if (response.message?.contains("No internet") == true){
            alertDialogBox.showNoInternetDialog(requireContext())
        }else{
            Toast.makeText(requireContext(),response.message.toString(),Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView(){
        verticalArticleAdapter = ArticleVerticleAdapter()
        binding.rvRecommended.apply {
            this.adapter = verticalArticleAdapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupPopularRecyclerView(){
        horizontalAdapter = ArticleHorizontalAdapter()
        binding.rvPopular.apply {
            this.adapter = horizontalAdapter
            this.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }

    // Call filterArticlesByIds with a list of IDs to filter
    private fun filterArticlesByIds(idsToFilter: List<Long>) {
        //horizontalAdapter.filterByIds(idsToFilter)
        val filteredList = if (idsToFilter != null) {
            dataList.filter { it.id?.toLong() in idsToFilter }
        } else {
            dataList
        }
            if (filteredList.isEmpty()) {
            Toast.makeText(requireContext(), "Not found", Toast.LENGTH_SHORT).show()
        } else {
            horizontalAdapter.differ.submitList(filteredList)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}