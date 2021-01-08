package com.zjp.commonutils

import android.graphics.Bitmap
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.*
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.ListenerUtil

/**
 * <pre>
 *     author : zhangjunpu
 *     e-mail : zhangjp@zhigujinyun.com
 *     time   : 2020/12/18
 *     version: 1.0
 *     desc   :
 * </pre>
 */
object BindingAdapters {
    @BindingAdapter("app:imageUrl")//指定xml的属性名字
    @JvmStatic
    fun loadImage(view: ImageView, imageUrl: String) {
        view.tag = imageUrl
    }

    @BindingAdapter("app:bitmap")//指定xml的属性名字
    @JvmStatic
    fun setBitmap(view: ImageView, bitmap: Bitmap?) {
        view.setImageBitmap(bitmap)
    }


    @BindingAdapter("app:onCheckedChange")
    @JvmStatic
    fun setOnCheckedChange(view: CompoundButton, edtPassword: EditText) {
        view.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                edtPassword.transformationMethod = HideReturnsTransformationMethod()
            } else {
                edtPassword.transformationMethod = PasswordTransformationMethod()
            }
            //将光标移至末尾
            val length: Int = edtPassword.text.toString().length
            edtPassword.setSelection(length)
        }
    }

    @BindingAdapter(
        "app:onViewDetachedFromWindow",
        "app:onViewAttachedToWindow",
        requireAll = false
    )
    @JvmStatic
    fun setOnAttachStateListener(
        view: View,
        detach: OnViewDetachedFromWindow?,
        attach: OnViewAttachedToWindow?
    ) {
        val newListener: View.OnAttachStateChangeListener?
        newListener = if (detach == null && attach == null) {
            null
        } else {
            object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    attach?.onViewAttachedToWindow(v)
                }

                override fun onViewDetachedFromWindow(v: View) {
                    detach?.onViewDetachedFromWindow(v)
                }
            }
        }

        val oldListener: View.OnAttachStateChangeListener? =
            ListenerUtil.trackListener(view, newListener, R.id.onAttachStateChangeListener)
        if (oldListener != null) {
            view.removeOnAttachStateChangeListener(oldListener)
        }
        if (newListener != null) {
            view.addOnAttachStateChangeListener(newListener)
        }
    }
}

interface OnViewDetachedFromWindow {
    fun onViewDetachedFromWindow(v: View)
}

interface OnViewAttachedToWindow {
    fun onViewAttachedToWindow(v: View)
}