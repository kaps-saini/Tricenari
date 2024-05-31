package com.mavalore.tricenari

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mavalore.tricenari.adapter.recyclerView.ProductRecommendationAdapter
import com.mavalore.tricenari.databinding.FragmentProductRecommendationBinding
import com.mavalore.tricenari.domain.models.productRecomendation.RecommendedItemsData
import com.mavalore.tricenari.helper.Helper
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductRecommendation : Fragment() {

    private var _binding:FragmentProductRecommendationBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<TriceNariViewModel>()
    private lateinit var productRecommendationAdapter: ProductRecommendationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_product_recommendation, container, false)

        setupRecyclerView()

        binding.ivProductRecBack.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.getProductRecommendationData()
        viewModel.productRecommendationResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resources.Error -> {
                    shimmerNotLoading()
                    Helper.showSnackbar(requireView(),response.message.toString())}
                is Resources.Loading -> {
                    shimmerLoading()
                }
                is Resources.Success -> {
                    shimmerNotLoading()
                    productRecommendationAdapter.differ.submitList(response.data?.data)
                }
            }
        }

        productRecommendationAdapter.setOnItemClickListener(object :ProductRecommendationAdapter.OnClickListener{
            override fun onClick(position: Int, dataItem: Long, itemData: RecommendedItemsData) {
                openLink(itemData.link)
            }
        })

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun openLink(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)

        try {
            // Attempt to open the URL using the default handler (which may be the shopping app)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // If the shopping app is not installed, open the URL in a web browser
            Log.d("ExampleFragment", "Shopping app not found, opening in browser.")
            openLinkInBrowser(uri)
        }
    }

    private fun openLinkInBrowser(uri: Uri) {
        val browserIntent = Intent(Intent.ACTION_VIEW, uri)

        // Query all activities that can handle the intent
        val packageManager = requireActivity().packageManager
        val resolvedActivities = packageManager.queryIntentActivities(browserIntent, 0)

        // Filter out non-browser apps
        val browserIntents = resolvedActivities.filter {
            it.activityInfo.packageName.contains("browser") ||
                    it.activityInfo.packageName.contains("chrome") ||
                    it.activityInfo.packageName.contains("firefox") ||
                    it.activityInfo.packageName.contains("opera")
        }.map {
            val packageName = it.activityInfo.packageName
            Intent(Intent.ACTION_VIEW, uri).setPackage(packageName)
        }.toMutableList()

        // If there are no browsers, handle the error
        if (browserIntents.isEmpty()) {
            Log.d("ExampleFragment", "No browser found.")
            Helper.showSnackbar(requireView(), "No browser found on device")
            return
        }

        // Create a chooser with the browser intents
        val chooserIntent = Intent.createChooser(browserIntents.removeAt(0), "Open with")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, browserIntents.toTypedArray())

        // Start the chooser intent
        startActivity(chooserIntent)
    }

    private fun setupRecyclerView() {
        productRecommendationAdapter = ProductRecommendationAdapter()
        binding.rvProductRecommendation.apply {
            this.adapter = productRecommendationAdapter
            this.layoutManager = StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL
            )
        }
    }

    private fun shimmerNotLoading(){
        binding.recProdShimmer.visibility = View.GONE
        binding.rvProductRecommendation.visibility = View.VISIBLE
        binding.recProdShimmer.stopShimmer()
    }

    private fun shimmerLoading(){
        binding.recProdShimmer.visibility = View.VISIBLE
        binding.rvProductRecommendation.visibility = View.GONE
        binding.recProdShimmer.startShimmer()
    }

}