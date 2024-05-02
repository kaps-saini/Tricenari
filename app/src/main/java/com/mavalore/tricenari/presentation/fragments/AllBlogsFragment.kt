package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mavalore.tricenari.R
import com.mavalore.tricenari.adapter.recyclerView.ArticleVerticleAdapter
import com.mavalore.tricenari.databinding.FragmentAllBlogsBinding
import com.mavalore.tricenari.domain.models.article.AllArticleResponse
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import javax.inject.Inject

class AllBlogsFragment : Fragment() {

    private var _binding:FragmentAllBlogsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TriceNariViewModel>()
    private lateinit var articleAdapter:ArticleVerticleAdapter

    @Inject
    lateinit var alertDialogBox: AlertDialogBox


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_all_blogs, container, false)

        setupRecyclerView()
       // viewModel.getAllArticle()
        viewModel.allArticleResponse.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error -> errorResponse(response)
                is Resources.Loading -> loadingResponse()
                is Resources.Success -> successResponse(response)
            }
        }


        return binding.root
    }

    private fun successResponse(response: Resources.Success<AllArticleResponse>) {
        articleAdapter.differ.submitList(response.data?.data)
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
        binding.recyclerView.apply {
            this.adapter = articleAdapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}