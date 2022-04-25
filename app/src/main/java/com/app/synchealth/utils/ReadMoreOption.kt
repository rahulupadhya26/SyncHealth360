package com.app.synchealth.utils

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView


class ReadMoreOption private constructor(builder: Builder) {
    // required
    private val context: Context

    // optional
    private val textLength: Int
    private val textLengthType: Int
    private val moreLabel: String
    private val lessLabel: String
    private val moreLabelColor: Int
    private val lessLabelColor: Int
    private val labelUnderLine: Boolean
    private val expandAnimation: Boolean
    fun addReadMoreTo(textView: TextView, text: CharSequence) {
        if (textLengthType == TYPE_CHARACTER) {
            if (text.length <= textLength) {
                textView.text = text
                return
            }
        } else {
            // If TYPE_LINE
            textView.setLines(textLength)
            textView.text = text
        }
        textView.post(Runnable {
            var textLengthNew = textLength
            if (textLengthType == TYPE_LINE) {
                if (textView.layout.lineCount <= textLength) {
                    textView.text = text
                    return@Runnable
                }
                val lp = textView.layoutParams as MarginLayoutParams
                val subString = text.toString().substring(
                    textView.layout.getLineStart(0),
                    textView.layout.getLineEnd(textLength - 1)
                )
                textLengthNew = subString.length - (moreLabel.length + 4 + lp.rightMargin / 6)
            }
            val spannableStringBuilder =
                SpannableStringBuilder(text.subSequence(0, textLengthNew))
                    .append("...")
                    .append(moreLabel)
            val ss = SpannableString.valueOf(spannableStringBuilder)
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    addReadLess(textView, text)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = labelUnderLine
                    ds.color = moreLabelColor
                }
            }
            ss.setSpan(
                clickableSpan,
                ss.length - moreLabel.length,
                ss.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val layoutTransition = LayoutTransition()
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            (textView.parent as ViewGroup).layoutTransition = layoutTransition
            textView.text = ss
            textView.movementMethod = LinkMovementMethod.getInstance()
        })
    }

    private fun addReadLess(textView: TextView, text: CharSequence) {
        textView.maxLines = Int.MAX_VALUE
        val spannableStringBuilder = SpannableStringBuilder(text)
            .append(lessLabel)
        val ss = SpannableString.valueOf(spannableStringBuilder)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                Handler().post { addReadMoreTo(textView, text) }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = labelUnderLine
                ds.color = lessLabelColor
            }
        }
        ss.setSpan(
            clickableSpan,
            ss.length - lessLabel.length,
            ss.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = ss
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    class Builder(// required
        internal val context: Context
    ) {

        // optional
        var textLength = 100
        var textLengthType = TYPE_CHARACTER
        var moreLabel = "read more"
        var lessLabel = "read less"
        var moreLabelColor = Color.parseColor("#ff00ff")
        var lessLabelColor = Color.parseColor("#ff00ff")
        var labelUnderLine = false
        var expandAnimation = false

        /**
         * @param length can be no. of line OR no. of characters - default is 100 character
         * @param textLengthType ReadMoreOption.TYPE_LINE for no. of line OR
         * ReadMoreOption.TYPE_CHARACTER for no. of character
         * - default is ReadMoreOption.TYPE_CHARACTER
         * @return Builder obj
         */
        fun textLength(length: Int, textLengthType: Int): Builder {
            textLength = length
            this.textLengthType = textLengthType
            return this
        }

        fun moreLabel(moreLabel: String): Builder {
            this.moreLabel = moreLabel
            return this
        }

        fun lessLabel(lessLabel: String): Builder {
            this.lessLabel = lessLabel
            return this
        }

        fun moreLabelColor(moreLabelColor: Int): Builder {
            this.moreLabelColor = moreLabelColor
            return this
        }

        fun lessLabelColor(lessLabelColor: Int): Builder {
            this.lessLabelColor = lessLabelColor
            return this
        }

        fun labelUnderLine(labelUnderLine: Boolean): Builder {
            this.labelUnderLine = labelUnderLine
            return this
        }

        /**
         * @param expandAnimation either true to enable animation on expand or false to disable animation
         * - default is false
         * @return Builder obj
         */
        fun expandAnimation(expandAnimation: Boolean): Builder {
            this.expandAnimation = expandAnimation
            return this
        }

        fun build(): ReadMoreOption {
            return ReadMoreOption(this)
        }

    }

    companion object {
        private val TAG = ReadMoreOption::class.java.simpleName
        const val TYPE_LINE = 1
        const val TYPE_CHARACTER = 2
    }

    init {
        context = builder.context
        textLength = builder.textLength
        textLengthType = builder.textLengthType
        moreLabel = builder.moreLabel
        lessLabel = builder.lessLabel
        moreLabelColor = builder.moreLabelColor
        lessLabelColor = builder.lessLabelColor
        labelUnderLine = builder.labelUnderLine
        expandAnimation = builder.expandAnimation
    }
}