package com.example.khccqboard.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.khccqboard.R
import com.example.khccqboard.databinding.HomeFragmentBinding
import com.example.khccqboard.util.PreferenceManager

class HomeFragment : Fragment() {

    private lateinit var binding: HomeFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkAddedURL()


        binding.btnSubmit.setOnClickListener {
            checkUrlAndNavigate()
        }

        var storedUrl = PreferenceManager.getUrl(requireContext())
        binding.etUrl.setText(storedUrl)

        val storedCode = PreferenceManager.getBranchCode(requireContext())
        binding.etBranchCode.setText(storedCode)


        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val v = requireActivity().currentFocus
                if (v is EditText) {
                    val outRect = Rect()
                    v.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        v.clearFocus()
                        hideKeyboard()
                    }
                }
            }
            false
        }


    }

    private fun checkAddedURL() {
        val isAddedUrl = PreferenceManager.isAddedURL(requireContext())
        Log.v("is saved", isAddedUrl.toString())

        if (isAddedUrl) {
            // Navigate to the Details screen if logged in
             findNavController().navigate(R.id.action_homeFragment_to_detailsFragment)
        }
    }

    private fun checkUrlAndNavigate() {
        if (isEditTextEmpty(binding.etUrl) || isEditTextEmpty(binding.etBranchCode) || isEditTextEmpty(binding.etDisplayId)
        ) {
            Toast.makeText(
                requireContext(),
                "Please fill the URL , branch code and display id",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val branchCode = binding.etBranchCode.text.toString()
            PreferenceManager.saveBranchCode(requireContext(), branchCode, true)

            val displayNumber = binding.etDisplayId.text.toString()
            PreferenceManager.saveDisplayNumber(requireContext(), displayNumber, true)


            val url = binding.etUrl.text.toString().trim()
            // Check if the URL starts with http:// or https:// and ends with /
            if (!(url.startsWith("http://") || url.startsWith("https://")) || !url.endsWith("/")) {
                Toast.makeText(
                    requireContext(),
                    "URL must start with 'http://' or 'https://' and end with '/'",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                PreferenceManager.saveBaseUrl(requireContext(), url, true)
                PreferenceManager.saveUrl(requireContext(), url, true)

                 findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailsFragment())


            }
        }
    }

    fun hideKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus ?: View(requireActivity())
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun isEditTextEmpty(editText: EditText): Boolean {
        return editText.text.toString().trim().isEmpty()
    }

}
