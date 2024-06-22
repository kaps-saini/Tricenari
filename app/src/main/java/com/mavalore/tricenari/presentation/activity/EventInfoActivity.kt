package com.mavalore.tricenari.presentation.activity

import android.content.Intent
import android.content.res.Resources.Theme
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.ActivityEventInfoBinding
import com.mavalore.tricenari.databinding.FragmentEventBookingBinding
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.helper.CheckInternetConnection
import com.mavalore.tricenari.helper.Helper
import com.mavalore.tricenari.presentation.fragments.EventBookingFragmentArgs
import com.mavalore.tricenari.presentation.fragments.EventBookingFragmentDirections
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class EventInfoActivity : AppCompatActivity() {

    private var _binding: ActivityEventInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<TriceNariViewModel>()

    @Inject
    lateinit var checkInternetConnection: CheckInternetConnection

    @Inject
    lateinit var alertDialogBox: AlertDialogBox

    private var eventId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEventInfoBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_TriceNari)
        setContentView(binding.root)

        binding.ivEventBack.setOnClickListener{
            finish()
        }


        eventId = intent.getIntExtra("eventId", 0)
        viewModel.getEventInfo(eventId)
        viewModel.eventInfoResponse.observe(this){response->
            when(response){
                is Resources.Error -> {
                    if (response.message?.contains("No Internet") == true){
                        alertDialogBox.showNoInternetDialog(this)
                    }else{ Helper.showSnackbar(View(this),response.message.toString()) }
                }
                is Resources.Loading -> {
                    Helper.showSnackbar(binding.root,"Loading")}
                is Resources.Success ->{
                    response.data.let {data->
                        data.let {
                            eventId = it?.data?.id!!
                            val month = extractMonth(it.data.evdate)
                            val day = extractDay(it.data.evdate)

                            val lastMonth = extractMonth(it.data.lastdate)
                            val lastDay = extractDay(it.data.lastdate)

                            binding.tvEventTitle.text = it.data.title
                            binding.tvEventLongTitle.text = it.data.subtitle
                            binding.tvStartTime.text = formatTime(it.data.starttime)
                            binding.tvEndTime.text = formatTime(it.data.endtime)
                            binding.tvEventDate.text = "$day\n$month"
                            binding.tvSeatsAvailable.text = it.data.remainingseats.toString()
                            binding.tvMode.text = it.data.mode
                            binding.tvDuration.text = it.data.duration.toString()
                            binding.tvLocation.text = it.data.venue
                            binding.tvGuestNo.text = it.data.guest.toString()
                            val lastDate = formatDateWithMonthName(it.data.lastdate)
                            binding.tvLastDate.text = "Hurry up! Hurry up! Last date to join the" +
                                    "event is $lastDay $lastMonth"
                        }

                    }
                }
            }
        }

        binding.btnJoin.setOnClickListener {
            val intent = Intent(this,EventJoiningActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }
    }

    private fun formatTime(time: String): String? {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(time)
        return date?.let { outputFormat.format(it) }
    }

    private fun formatDateWithMonthName(date: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd:MMM", Locale.getDefault())
        val parsedDate = inputFormat.parse(date)
        return parsedDate?.let { outputFormat.format(it) }
    }

    private fun extractMonth(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateObject = inputFormat.parse(date)
        val calendar = Calendar.getInstance()
        if (dateObject != null) {
            calendar.time = dateObject
        }
        return SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
    }

    private fun extractDay(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateObject = inputFormat.parse(date)
        val calendar = Calendar.getInstance()
        if (dateObject != null) {
            calendar.time = dateObject
        }
        return SimpleDateFormat("dd", Locale.getDefault()).format(calendar.time)
    }




    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}