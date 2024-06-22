package com.mavalore.tricenari.presentation.activity

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.ActivityEventJoiningBinding
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
class EventJoiningActivity : AppCompatActivity() {
    private var _binding: ActivityEventJoiningBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TriceNariViewModel>()

    @Inject
    lateinit var checkInternetConnection: CheckInternetConnection

    @Inject
    lateinit var alertDialogBox: AlertDialogBox

    private var eventId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEventJoiningBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_TriceNari)
        setContentView(binding.root)

        binding.ivEventDetailsBack.setOnClickListener{
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        eventId = intent.getIntExtra("eventId", 0)
        viewModel.getEventDetails(eventId)
        viewModel.eventDetailsResponse.observe(this){response->
            when(response){
                is Resources.Error -> {
                    if (response.message?.contains("No Internet") == true){
                        alertDialogBox.showNoInternetDialog(this)
                    }else{ Helper.showSnackbar(binding.root,response.message.toString()) }
                }
                is Resources.Loading -> {
                    Helper.showSnackbar(binding.root,"Loading")}
                is Resources.Success ->{
                    successResponse(response.data)
                }
            }
        }

    }

    private fun successResponse(data: EventDetailsResponse?) {

        //add details data
        data.let {
            for (rules in data?.data?.details?.toList()!!) {
                addSingleRow(binding.tlRules, rules)
            }

            for (whoCan in data.data.whocan.toList()) {
                addSingleRow(binding.tlWhoCanJoin, whoCan)
            }

            //add faqs
            for (faq in data.data.faq.toList()) {
                addRow(binding.tlFaqs, faq)
            }

            binding.tvGuestInfo.text = it?.data?.guest?.info
            binding.tvGuestName.text = it?.data?.guest?.name
            binding.tvInstaLink.text = it?.data?.guest?.instagram
            binding.tvTwitter.text = it?.data?.guest?.twitter
            binding.tvYoutube.text = it?.data?.guest?.youtube
            binding.tvFacebookLink.text = it?.data?.guest?.facebook
            binding.tvWebUrl.text = it?.data?.guest?.website
        }
    }

    private fun addSingleRow(tableLayout: TableLayout, faq: String) {
        // Add question row
        val questionRow = TableRow(this)
        val questionTextView = TextView(this).apply {
            text = faq
            setPadding(16, 16, 16, 16)
//            layoutParams = TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f
//            ).apply {
//                setMargins(1, 1, 1, 1)
//            }
            gravity = Gravity.START
        }
        questionRow.addView(questionTextView)
        tableLayout.addView(
            questionRow, TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }


    private fun addRow(tableLayout: TableLayout, faq: Faq) {
        // Add question row
        val questionRow = TableRow(this)
        val questionTextView = TextView(this).apply {
            text = faq.question
            setPadding(16, 16, 16, 16)
//            layoutParams = TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f
//            ).apply {
//                setMargins(1, 1, 1, 1)
//            }
            gravity = Gravity.START
        }
        questionRow.addView(questionTextView)
        tableLayout.addView(questionRow, TableLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        // Add answer row
        val answerRow = TableRow(this)
        val answerTextView = TextView(this).apply {
            text = faq.answer
//            setPadding(16, 16, 16, 16)
//            setBackgroundColor(Color.WHITE)
//            layoutParams = TableRow.LayoutParams(
//                0,
//                LayoutParams.WRAP_CONTENT,
//                1f
//            ).apply {
//                setMargins(1, 1, 1, 1)
//            }
            gravity = Gravity.START
        }
        answerRow.addView(answerTextView)
        tableLayout.addView(answerRow, TableLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }


}