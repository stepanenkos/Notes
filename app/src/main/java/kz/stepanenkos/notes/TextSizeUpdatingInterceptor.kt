package kz.stepanenkos.notes

import android.util.TypedValue
import androidx.preference.PreferenceManager
import io.github.inflationx.viewpump.InflateResult
import io.github.inflationx.viewpump.Interceptor
import kz.stepanenkos.notes.common.presentation.ContentEditText
import kz.stepanenkos.notes.common.presentation.ContentTextView
import kz.stepanenkos.notes.common.presentation.TitleEditText
import kz.stepanenkos.notes.common.presentation.TitleTextView

class TextSizeUpdatingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): InflateResult {
        val result: InflateResult = chain.proceed(chain.request())

        if (result.view is TitleTextView) {
            val titleTextView: TitleTextView = result.view as TitleTextView
            val titleNoteTextSize = PreferenceManager.getDefaultSharedPreferences(titleTextView.context)
                .getString("title_note_font_size", "20")
            titleNoteTextSize?.toFloat()?.let { titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, it) }
        }

        if(result.view is ContentTextView) {
            val contentTextView: ContentTextView = result.view as ContentTextView
            val contentNoteTextSize = PreferenceManager.getDefaultSharedPreferences(contentTextView.context)
                .getString("content_note_font_size", "18")
            contentNoteTextSize?.toFloat()?.let { contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, it) }
        }

        if(result.view is TitleEditText) {
            val titleEditText: TitleEditText = result.view as TitleEditText
            val titleNoteTextSize = PreferenceManager.getDefaultSharedPreferences(titleEditText.context)
                .getString("title_note_font_size", "20")
            titleNoteTextSize?.toFloat()?.let { titleEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, it) }
        }

        if(result.view is ContentEditText) {
            val contentEditText: ContentEditText = result.view as ContentEditText
            val contentNoteTextSize = PreferenceManager.getDefaultSharedPreferences(contentEditText.context)
                .getString("content_note_font_size", "18")
            contentNoteTextSize?.toFloat()?.let { contentEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, it) }
        }

        return result
    }
}