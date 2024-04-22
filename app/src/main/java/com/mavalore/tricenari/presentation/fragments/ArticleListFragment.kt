package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.mavalore.tricenari.R
import com.mavalore.tricenari.adapter.recyclerView.ArticleHorizontalAdapter
import com.mavalore.tricenari.adapter.recyclerView.ArticleVerticleAdapter
import com.mavalore.tricenari.databinding.FragmentArticleListFragementBinding
import com.mavalore.tricenari.domain.models.AllArticleResponse
import com.mavalore.tricenari.domain.models.ArticleData
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
    private lateinit var articleAdapter: ArticleVerticleAdapter
    private lateinit var horizontalAdapter: ArticleHorizontalAdapter
    @Inject
    lateinit var alertDialogBox: AlertDialogBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_list_fragement,
            container, false)

        setupRecyclerView()
        setupPopularRecyclerView()

        viewModel.getAllArticle()
        viewModel.allArticleResponse.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error -> errorResponse(response)
                is Resources.Loading -> loadingResponse()
                is Resources.Success -> successResponse(response)
            }
        }

        articleAdapter.setOnItemClickListener(object : ArticleVerticleAdapter.OnClickListener{
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
        articleAdapter.differ.submitList(response.data?.data)

        horizontalAdapter.differ.submitList(response.data?.data)

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
        articleAdapter = ArticleVerticleAdapter()
        binding.rvRecommended.apply {
            this.adapter = articleAdapter
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


    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}