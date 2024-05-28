package com.mavalore.tricenari.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentPartnerWithUsBinding

class PartnerWithUs : Fragment() {

    private var _binding:FragmentPartnerWithUsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_partner_with_us, container, false)

        val view1Heading = binding.view1.findViewById<TextView>(R.id.tvHeading)
        val view1Content = binding.view1.findViewById<TextView>(R.id.tvContent)
        val view1Image = binding.view1.findViewById<ImageView>(R.id.image)

        val view2Heading = binding.view2.findViewById<TextView>(R.id.tvHeading)
        val view2Content = binding.view2.findViewById<TextView>(R.id.tvContent)
        val view2Image = binding.view2.findViewById<ImageView>(R.id.image)

        val view3Heading = binding.view3.findViewById<TextView>(R.id.tvHeading)
        val view3Content = binding.view3.findViewById<TextView>(R.id.tvContent)
        val view3Image = binding.view3.findViewById<ImageView>(R.id.image)

        val view4Heading = binding.view4.findViewById<TextView>(R.id.tvHeading)
        val view4Content = binding.view4.findViewById<TextView>(R.id.tvContent)
        val view4Image = binding.view4.findViewById<ImageView>(R.id.image)

        val view5Heading = binding.view5.findViewById<TextView>(R.id.tvHeading)
        val view5Content = binding.view5.findViewById<TextView>(R.id.tvContent)
        val view5Image = binding.view5.findViewById<ImageView>(R.id.image)

        val view6Heading = binding.view6.findViewById<TextView>(R.id.tvHeading)
        val view6Content = binding.view6.findViewById<TextView>(R.id.tvContent)
        val view6Image = binding.view6.findViewById<ImageView>(R.id.image)

        view1Heading.text = "Place Your Ads"
        view1Content.text = resources.getString(R.string.partner_content_1)
        view1Image.setImageDrawable(resources.getDrawable(R.drawable.images))

        view2Heading.text = "Special Offers"
        view2Content.text = resources.getString(R.string.partner_content_2)
        view2Image.setImageDrawable(resources.getDrawable(R.drawable.hypnotize))

        view3Heading.text = "Get Featured"
        view3Content.text = resources.getString(R.string.partner_content_3)
        view3Image.setImageDrawable(resources.getDrawable(R.drawable.shop))

        view4Heading.text = "Promote by Sampling"
        view4Content.text = resources.getString(R.string.partner_content_4)
        view4Image.setImageDrawable(resources.getDrawable(R.drawable.share))

        view5Heading.text = "Get Known"
        view5Content.text = resources.getString(R.string.partner_content_5)
        view5Image.setImageDrawable(resources.getDrawable(R.drawable.person_rolodex))

        view6Heading.text = "Product Suggestions"
        view6Content.text = resources.getString(R.string.partner_content_6)
        view6Image.setImageDrawable(resources.getDrawable(R.drawable.union))


        colorText()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun colorText(){
        val fullText = resources.getString(R.string.partner_with_us_note)
        val wordToColor = "Contact Us"
        val wordToColor2 = "partnership@tricenari.com"

        // Create a SpannableString from the full text
        val spannableString = SpannableString(fullText)

        // Find the start and end position of the word to color
        val start = fullText.indexOf(wordToColor)
        val start2 = fullText.indexOf(wordToColor2)
        val end = start + wordToColor.length
        val end2 = start2 + wordToColor2.length

        // Apply a ForegroundColorSpan to the word
        if (start in 0..<end) {
            spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.red)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(StyleSpan(android.graphics.Typeface.BOLD),start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    findNavController().navigate(R.id.action_partnerWithUs_to_contact_us)
                }
            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        if (start2 in 0..<end2) {
            spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.red)), start2, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(StyleSpan(android.graphics.Typeface.BOLD),start2,end2,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            spannableString.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    // Create an intent to send an email
                    val emailIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "message/rfc822"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("partnership@tricenari.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "Partnership with TriceNARI")
                    }
                    // Check if there's an app that can handle this intent
                    if (emailIntent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(Intent.createChooser(emailIntent, "Send email..."))
                    } else {
                        // Handle case where no email apps are available
                        Toast.makeText(requireContext(), "No email clients installed.", Toast.LENGTH_SHORT).show()
                    }
                }
            }, start2, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Set the spannable string to the TextView
        binding.tvEndNote.text = spannableString
        binding.tvEndNote.movementMethod = LinkMovementMethod.getInstance()
    }

}