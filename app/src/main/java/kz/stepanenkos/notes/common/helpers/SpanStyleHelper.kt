package kz.stepanenkos.notes.common.helpers

import android.graphics.Typeface
import android.text.Spannable
import android.text.style.StyleSpan
import android.util.Log
import android.widget.EditText

class SpanStyleHelper(private val mEditText: EditText, private val mSpannable: Spannable, private val mSelectedTextStart: Int, private val mSelectedTextEnd: Int) {

    fun boldSelectedText(): Spannable {
        Log.d("Ramansoft", "Try to bold selected text..")
        val styleSpans = mEditText.text.getSpans(
            mSelectedTextStart,
            mSelectedTextEnd,
            StyleSpan::class.java
        )
        if (styleSpans.isNotEmpty()) {
            var lastSpanEnd = 0
            for (styleSpan in styleSpans) {
                /**
                 * Save old style
                 */
                val oldStyle = styleSpan.style

                /**
                 * Get start and end of span
                 */
                val spanStart = mSpannable.getSpanStart(styleSpan)
                val spanEnd = mSpannable.getSpanEnd(styleSpan)
                /**
                 * Before bold this span, we check if any unspanned
                 * text between this span and last span remains. if any
                 * unspanned text exist, we should bold it
                 */
                if (spanStart > lastSpanEnd) {
                    mSpannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        lastSpanEnd,
                        spanStart,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                /**
                 * Update last span end
                 */
                lastSpanEnd = spanEnd
                /**
                 * Remove the span
                 */
                mSpannable.removeSpan(styleSpan)
                /**
                 * Because we just need change selected text,
                 * if span start is lower than selected text start or
                 * if span end is higher than selected text end start
                 * we should restore span for unselected part of span
                 */
                if (spanStart < mEditText.selectionStart) {
                    mSpannable.setSpan(
                        StyleSpan(oldStyle),
                        spanStart,
                        mSelectedTextStart,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                if (spanEnd > mEditText.selectionEnd) {
                    mSpannable.setSpan(
                        StyleSpan(oldStyle),
                        mSelectedTextEnd,
                        spanEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                /**
                 * We want to add bold style to current style
                 * so we most detect current style and change
                 * the style depend on current style
                 */
                if (oldStyle == Typeface.ITALIC) {
                    mSpannable.setSpan(
                        StyleSpan(Typeface.BOLD_ITALIC),
                        spanStart,
                        spanEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    mSpannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        spanStart,
                        spanEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            /**
             * Now we should check if any
             * unspanned selected text remains
             */
            if (mSelectedTextEnd != lastSpanEnd) {
                mSpannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    lastSpanEnd,
                    mSelectedTextEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        } else {
            mSpannable.setSpan(
                StyleSpan(Typeface.BOLD),
                mSelectedTextStart,
                mSelectedTextEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return mSpannable
    }

    fun unBoldSelectedText(): Spannable {
        Log.d("Ramansoft", "Try to unbold selected text..")
        val styleSpans = mEditText.text.getSpans(
            mSelectedTextStart,
            mSelectedTextEnd,
            StyleSpan::class.java
        )
        for (styleSpan in styleSpans) {
            /**
             * Save old style
             */
            val oldStyle = styleSpan.style

            /**
             * Get start and end of span
             */
            val spanStart = mSpannable.getSpanStart(styleSpan)
            val spanEnd = mSpannable.getSpanEnd(styleSpan)
            /**
             * Remove the span
             */
            mSpannable.removeSpan(styleSpan)
            /**
             * Because we just need change selected text,
             * if span start is lower than selected text start or
             * if span end is higher than selected text end start
             * we should restore span for unselected part of span
             */
            if (spanStart < mEditText.selectionStart) {
                mSpannable.setSpan(
                    StyleSpan(oldStyle),
                    spanStart,
                    mSelectedTextStart,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (spanEnd > mEditText.selectionEnd) {
                mSpannable.setSpan(
                    StyleSpan(oldStyle),
                    mSelectedTextEnd,
                    spanEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            /**
             * Because we just want to remove bold style,
             * if the span has another style, we should restore it
             */
            if (oldStyle == Typeface.BOLD_ITALIC) {
                mSpannable.setSpan(
                    StyleSpan(Typeface.ITALIC),
                    spanStart,
                    spanEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        return mSpannable
    }

    fun toggleBoldSelectedText(): Spannable {
        Log.d("Ramansoft", "Try to toggle bold selected text..")
        var isAllSpansBold = true
        val styleSpans = mEditText.text.getSpans(
            mSelectedTextStart,
            mSelectedTextEnd,
            StyleSpan::class.java
        )
        return if (styleSpans.isEmpty()) {
            boldSelectedText()
        } else {
            for (styleSpan in styleSpans) {
                Log.d("Ramansoft", "styleSpan.getStyle() = " + styleSpan.style)
                if (styleSpan.style != Typeface.BOLD && styleSpan.style != Typeface.BOLD_ITALIC) {
                    isAllSpansBold = false
                    break
                }
            }
            Log.d("Ramansoft", "isAllSpansBold = $isAllSpansBold")
            if (isAllSpansBold) unBoldSelectedText() else boldSelectedText()
        }
    }

    fun italicSelectedText(): Spannable{
        Log.d("Ramansoft", "Try to italic selected text..")
        val styleSpans = mEditText.text.getSpans(
            mSelectedTextStart,
            mSelectedTextEnd,
            StyleSpan::class.java
        )
        if (styleSpans.isNotEmpty()) {
            var lastSpanEnd = 0
            for (styleSpan in styleSpans) {
                /**
                 * Save old style
                 */
                val oldStyle = styleSpan.style

                /**
                 * Get start and end of span
                 */
                val spanStart = mSpannable.getSpanStart(styleSpan)
                val spanEnd = mSpannable.getSpanEnd(styleSpan)
                /**
                 * Before italic this span, we check if any unspanned
                 * text between this span and last span remains. if any
                 * unspanned text exist, we should italic it
                 */
                if (spanStart > lastSpanEnd) {
                    mSpannable.setSpan(
                        StyleSpan(Typeface.ITALIC),
                        lastSpanEnd,
                        spanStart,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                /**
                 * Update last span end
                 */
                lastSpanEnd = spanEnd
                /**
                 * Remove the span
                 */
                mSpannable.removeSpan(styleSpan)
                /**
                 * Because we just need change selected text,
                 * if span start is lower than selected text start or
                 * if span end is higher than selected text end start
                 * we should restore span for unselected part of span
                 */
                if (spanStart < mEditText.selectionStart) {
                    mSpannable.setSpan(
                        StyleSpan(oldStyle),
                        spanStart,
                        mSelectedTextStart,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                if (spanEnd > mEditText.selectionEnd) {
                    mSpannable.setSpan(
                        StyleSpan(oldStyle),
                        mSelectedTextEnd,
                        spanEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                /**
                 * We want to add bold style to current style
                 * so we most detect current style and change
                 * the style depend on current style
                 */
                if (oldStyle == Typeface.BOLD) {
                    mSpannable.setSpan(
                        StyleSpan(Typeface.BOLD_ITALIC),
                        spanStart,
                        spanEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    mSpannable.setSpan(
                        StyleSpan(Typeface.ITALIC),
                        spanStart,
                        spanEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            /**
             * Now we should check if any
             * unspanned selected text remains
             */
            if (mSelectedTextEnd != lastSpanEnd) {
                mSpannable.setSpan(
                    StyleSpan(Typeface.ITALIC),
                    lastSpanEnd,
                    mSelectedTextEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        } else {
            mSpannable.setSpan(
                StyleSpan(Typeface.ITALIC),
                mSelectedTextStart,
                mSelectedTextEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return mSpannable
    }

    fun unItalicSelectedText(): Spannable {
        Log.d("Ramansoft", "Try to un-italic selected text..")
        val styleSpans = mEditText.text.getSpans(
            mSelectedTextStart,
            mSelectedTextEnd,
            StyleSpan::class.java
        )
        for (styleSpan in styleSpans) {
            /**
             * Save old style
             */
            val oldStyle = styleSpan.style

            /**
             * Get start and end of span
             */
            val spanStart = mSpannable.getSpanStart(styleSpan)
            val spanEnd = mSpannable.getSpanEnd(styleSpan)
            /**
             * Remove the span
             */
            mSpannable.removeSpan(styleSpan)
            /**
             * Because we just need change selected text,
             * if span start is lower than selected text start or
             * if span end is higher than selected text end start
             * we should restore span for unselected part of span
             */
            if (spanStart < mEditText.selectionStart) {
                mSpannable.setSpan(
                    StyleSpan(oldStyle),
                    spanStart,
                    mSelectedTextStart,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (spanEnd > mEditText.selectionEnd) {
                mSpannable.setSpan(
                    StyleSpan(oldStyle),
                    mSelectedTextEnd,
                    spanEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            /**
             * Because we just want to remove bold style,
             * if the span has another style, we should restore it
             */
            if (oldStyle == Typeface.BOLD_ITALIC) {
                mSpannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    spanStart,
                    spanEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        return mSpannable
    }

    fun toggleItalicSelectedText(): Spannable {
        Log.d("Ramansoft", "Try to toggle italic selected text..")
        var isAllSpansItalic = true
        val styleSpans = mEditText.text.getSpans(
            mSelectedTextStart,
            mSelectedTextEnd,
            StyleSpan::class.java
        )
        return if (styleSpans.size == 0) {
            italicSelectedText()
        } else {
            for (styleSpan in styleSpans) {
                Log.d("Ramansoft", "styleSpan.getStyle() = " + styleSpan.style)
                if (styleSpan.style != Typeface.ITALIC && styleSpan.style != Typeface.BOLD_ITALIC) {
                    isAllSpansItalic = false
                    break
                }
            }
            Log.d("Ramansoft", "isAllSpansItalic = $isAllSpansItalic")
            if (isAllSpansItalic) unItalicSelectedText() else italicSelectedText()
        }
    }

}