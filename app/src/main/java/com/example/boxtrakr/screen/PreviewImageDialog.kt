package com.example.boxtrakr.screen

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.view.ViewGroup
import android.widget.FrameLayout
import android.view.ViewGroup.LayoutParams
import com.example.boxtrakr.R

class PreviewImageDialog(
    private val ctx: Context,
    private val imageUri: Uri,
    private val onDecision: (keep: Boolean) -> Unit
) {
    fun show() {
        val imageView = ImageView(ctx).apply {
            setImageURI(imageUri)
            adjustViewBounds = true
            layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        val container = FrameLayout(ctx).apply {
            val pad = (ctx.resources.displayMetrics.density * 12).toInt()
            setPadding(pad, pad, pad, pad)
            addView(imageView, FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        }

        val dialog = AlertDialog.Builder(ctx)
            .setView(container)
            .setPositiveButton(ctx.getString(R.string.ok)) { _, _ -> onDecision(true) }
            .setNegativeButton(ctx.getString(R.string.cancel)) { _, _ -> onDecision(false) }
            .setOnCancelListener { onDecision(false) } // treat cancel as retake
            .create()

        dialog.show()
    }
}