package com.amanda.myappstory.ui.customView

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doOnTextChanged
import com.amanda.myappstory.R

class MyEmail : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        doOnTextChanged { text, start, before, count ->
            if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                error = resources.getString(R.string.error_email)
            } else {
                error = null
            }
        }
    }
}