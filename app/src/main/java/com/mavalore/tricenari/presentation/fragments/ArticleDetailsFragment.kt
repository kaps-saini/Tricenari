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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentArticleDetailsBinding
import com.mavalore.tricenari.domain.models.SingleArticleResponse
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ArticleDetailsFragment : Fragment() {

    private var _binding:FragmentArticleDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TriceNariViewModel>()

    @Inject
    lateinit var alertDialogBox: AlertDialogBox

    // get the arguments from the Registration fragment
    private val args : ArticleDetailsFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_article_details, container, false)


        args.CurrentArticle.id?.let { viewModel.getArticleById(it) }
        viewModel.singleArticleResponse.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error -> errorResponse(response)
                is Resources.Loading -> loadingResponse()
                is Resources.Success -> successResponse(response)
                else -> {}
            }
        }

        binding.tvArticleLongTitle.text = args.CurrentArticle.longTitle
        binding.tvTitleBlog.text = args.CurrentArticle.shortTitle.toString()
        if (args.NextArticle != null){
            binding.nextArticle.visibility = View.GONE

            binding.tvNextArticleTitle.text = args.NextArticle?.longTitle.toString()
            Glide.with(requireContext())
                .load("https://www.tricenari.com/writeups/images/${args.NextArticle?.id}.png")
                .into(binding.ivNextArticle)
        }else{
            binding.nextArticle.visibility = View.GONE
        }

        binding.ivBlogBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.nextArticle.setOnClickListener {
            args.NextArticle?.id?.let { it1 -> viewModel.getArticleById(it1) }
            binding.tvTitleBlog.text = args.NextArticle?.shortTitle.toString()
            binding.tvArticleLongTitle.text = args.NextArticle?.longTitle.toString()

            binding.nextArticle.visibility = View.GONE
        }

        return binding.root
    }


    private fun successResponse(response: Resources.Success<SingleArticleResponse>) {
        val newContentTop = viewModel.removeTags(response.data?.data?.contentTop.toString())
        binding.tvContentTop.text = newContentTop
        val newContentMiddle = viewModel.removeTags(response.data?.data?.contentMiddle.toString())
        binding.tvContentMiddle.text = newContentMiddle
        val newContentBottom = viewModel.removeTags(response.data?.data?.contentBottom.toString())
        binding.tvContentBottom.text = newContentBottom
        val newBlackQuote = response.data?.data?.blackquote.toString().trim()
        binding.tvBlackQuote.text = viewModel.removeTags(newBlackQuote)

        val imageUrl = response.data?.data?.imgurl.toString().trim()
        Glide.with(requireContext())
            .load("https://www.tricenari.com${imageUrl}")
            .into(binding.ivArticleImage)
        Log.e("ImageUrl",response.data?.data?.imgurl.toString())
    }

    private fun loadingResponse() {
        Toast.makeText(requireContext(),"Loading..", Toast.LENGTH_SHORT).show()
    }

    private fun errorResponse(response: Resources.Error<SingleArticleResponse>) {
        if (response.message?.contains("No internet") == true){
            alertDialogBox.showNoInternetDialog(requireContext())
        }else{
            Toast.makeText(requireContext(),response.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}