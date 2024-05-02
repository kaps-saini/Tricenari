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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentArticleDetailsBinding
import com.mavalore.tricenari.domain.models.article.SingleArticleResponse
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

    private lateinit var title:String
    private lateinit var articleLongTitle:String

    // get the arguments from the Registration fragment
    private val args : ArticleDetailsFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_article_details, container, false)

        val currentId = args.CurrentArticle.id

        var nextArticleId = currentId?.plus(1)

        args.CurrentArticle.id?.let { viewModel.getArticleById(it) }
        binding.tvArticleLongTitle.text = args.CurrentArticle.longTitle
        binding.tvTitleBlog.text = args.CurrentArticle.shortTitle.toString()
        viewModel.singleArticleResponse.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error -> errorResponse(response)
                is Resources.Loading -> loadingResponse()
                is Resources.Success -> successResponse(response)
                else -> {}
            }
        }

        viewModel.getNextArticleById(nextArticleId!!)
        viewModel.nextSingleArticleResponse.observe(viewLifecycleOwner){response->
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
                            binding.tvNextArticleTitle.text = it.longTitle.toString()
                            Glide.with(requireContext())
                                .load("https://www.tricenari.com/writeups/images/${nextArticleId}.png")
                                .into(binding.ivNextArticle)

                            title = it.shortTitle.toString()
                            articleLongTitle = it.longTitle.toString()

                            binding.scArticle.visibility = View.VISIBLE
                            binding.shimmerArticle.visibility = View.GONE
                            binding.shimmerArticle.stopShimmer()

                        }
                    }else{
                        binding.nextArticle.visibility = View.GONE
                    }

                }
                else -> {}
            }
        }




        binding.ivBlogBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.nextArticle.setOnClickListener {
            viewModel.getArticleById(nextArticleId)
            nextArticleId++
            viewModel.getNextArticleById(nextArticleId)

            binding.tvTitleBlog.text = title
            binding.tvArticleLongTitle.text = articleLongTitle

            binding.scArticle.smoothScrollTo(0,0)
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

        binding.scArticle.visibility = View.VISIBLE
        binding.shimmerArticle.visibility = View.GONE
        binding.shimmerArticle.stopShimmer()

    }

    private fun loadingResponse() {
        binding.scArticle.visibility = View.GONE
        binding.shimmerArticle.visibility = View.VISIBLE
        binding.shimmerArticle.startShimmer()
    }

    private fun errorResponse(response: Resources.Error<SingleArticleResponse>) {
        binding.scArticle.visibility = View.GONE
        binding.shimmerArticle.visibility = View.VISIBLE
        binding.shimmerArticle.startShimmer()
        if (response.message?.contains("No internet") == true){
            alertDialogBox.showNoInternetDialog(requireContext())
        }else{
            Toast.makeText(requireContext(),response.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.shimmerArticle.stopShimmer()

        _binding = null
    }

}