package com.mavalore.tricenari.presentation.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mavalore.tricenari.R
import com.mavalore.tricenari.helper.SpinManager
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class PrizeWheelFragment : DialogFragment() {

    private lateinit var btnSpin:Button
    private lateinit var ivPrize:ImageView
    private lateinit var tvDate:TextView
    private lateinit var tvSpinLeft:TextView
    private val sectors = arrayListOf("1","2","3","4","5","6","7","8","9","10")
    private val sectorDegrees = IntArray(sectors.size)
    private var degree = 0
    private var isSpinning = false

    @Inject
    lateinit var spinManager:SpinManager
    private val viewModel by viewModels<TriceNariViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_prize_wheel, container, false)

        view.setBackgroundResource(R.drawable.prize_wheel_layout_background)

        return view.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSpin = view.findViewById(R.id.btnSpin)
        ivPrize = view.findViewById(R.id.ivPrizeWheel)
        tvDate = view.findViewById(R.id.tvDate)
        tvSpinLeft = view.findViewById(R.id.tvSpinLeft)

        getDegreesFromSectors()
        setCurrentDate()
        viewModel.initSharedPreferences(requireContext())


        viewModel.getSpinLeft()
        viewModel.spinLeft.observe(this){
            tvSpinLeft.text = "Spin left - ${it}"
        }


        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels

        val newWidth = width*.7
        val newHeight = height*.6

        view.layoutParams.height = newHeight.toInt()
        view.layoutParams.width = newWidth.toInt()

        btnSpin.setOnClickListener {
            if (spinManager.canSpin()){
                if (!isSpinning){
                    spin()
                    isSpinning = true
                    btnSpin.isClickable = false
                    btnSpin.setBackgroundColor(Color.GRAY)
                }
            }else{
                Toast.makeText(requireContext(),"No more spins",Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setCurrentDate() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance().time
        val formattedDate = sdf.format(currentDate)
        tvDate.text = formattedDate
    }

    private fun spin() {

        val random = Random.Default
        degree = random.nextInt(sectors.size - 1)

        val rotate = RotateAnimation(
            0F, ((360 * sectors.size)+ sectorDegrees[degree]).toFloat(),
            RotateAnimation.RELATIVE_TO_SELF,0.5f,
            RotateAnimation.RELATIVE_TO_SELF,0.5f)

        rotate.duration = 3600
        rotate.fillAfter = true
        rotate.interpolator = DecelerateInterpolator()
        rotate.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {
                //decrease spin count by 1
                viewModel.spin(requireContext())
            }

            override fun onAnimationEnd(animation: Animation?) {
                viewModel.getSpinLeft()
                showPopUp()
                isSpinning = false
                btnSpin.isClickable = true
                btnSpin.setBackgroundColor(resources.getColor(R.color.maroon,resources.newTheme()))

            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })

        ivPrize.startAnimation(rotate)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun getDegreesFromSectors(){

        val sectorDegree = 360/sectors.size

        for (i in sectors.indices){
            sectorDegrees[i] = ( i + 1 ) * sectorDegree
        }
    }

    private fun showPopUp(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.pop_up)
        dialog.setCancelable(true)
        dialog.show()

        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)

        val tvWin = dialog.findViewById<TextView>(R.id.tvWonJewels)
        tvWin.setText("You won ${sectors[sectors.size-(degree+3)]} Jewels!")

        val btnMore = dialog.findViewById<Button>(R.id.btnSpinMore)
        val tvPlayQuiz = dialog.findViewById<TextView>(R.id.tvPlayQuiz)
        btnMore.setOnClickListener {
            dialog.dismiss()
        }

        tvPlayQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_quizFragment)
            dialog.dismiss()
            dismissNow()
        }
    }

}