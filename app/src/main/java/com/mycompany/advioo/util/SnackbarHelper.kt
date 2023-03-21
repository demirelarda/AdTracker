package com.mycompany.advioo.util

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mycompany.advioo.R

object SnackbarHelper{

    fun showErrorSnackBar(view: View, message: String) {
        showSnackBar(view, message, R.color.colorSnackBarError)
    }

    fun showSuccessSnackBar(view: View, message: String) {
        showSnackBar(view, message, R.color.colorSnackBarSuccess)
    }

    private fun showSnackBar(view: View, message: String, colorId: Int) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(view.context, colorId))
        snackBar.show()
    }
}
