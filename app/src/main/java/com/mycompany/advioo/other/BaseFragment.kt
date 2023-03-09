package com.mycompany.advioo.other

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentBaseBinding
import org.w3c.dom.Text


open class BaseFragment : Fragment() {

    private lateinit var myProgressDialog : Dialog
    private var _binding: FragmentBaseBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_base, container, false)
        _binding = FragmentBaseBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    fun showProgressBar(text: String) {
        myProgressDialog = Dialog(requireActivity())
        myProgressDialog.setContentView(R.layout.dialog_progress)
        myProgressDialog.findViewById<TextView>(R.id.tv_progress_text)
        myProgressDialog.setCancelable(false)
        myProgressDialog.setCanceledOnTouchOutside(false)
        myProgressDialog.show()
    }

    fun hideProgressBar() {
        myProgressDialog.dismiss()
    }

    /*
    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar =
            Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)

        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorSnackBarError
                )
            )
        }else{
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorSnackBarSuccess
                )
            )
        }
        snackBar.show()
    }

     */




}