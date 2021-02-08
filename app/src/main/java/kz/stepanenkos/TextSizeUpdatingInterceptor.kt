package kz.stepanenkos

import android.util.TypedValue
import androidx.preference.PreferenceManager
import io.github.inflationx.viewpump.InflateResult
import io.github.inflationx.viewpump.Interceptor
import kz.stepanenkos.notes.common.presentation.ContentNoteEditText
import kz.stepanenkos.notes.common.presentation.ContentNoteTextView
import kz.stepanenkos.notes.common.presentation.TitleNoteEditText
import kz.stepanenkos.notes.common.presentation.TitleNoteTextView

class TextSizeUpdatingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): InflateResult {
        val result: InflateResult = chain.proceed(chain.request())

        if (result.view is TitleNoteTextView) {
            val titleNoteTextView: TitleNoteTextView = result.view as TitleNoteTextView
            val titleNoteTextSize = PreferenceManager.getDefaultSharedPreferences(titleNoteTextView.context)
                .getString("title_note_font_size", "20")
            titleNoteTextSize?.toFloat()?.let { titleNoteTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, it) }
        }

        if(result.view is ContentNoteTextView) {
            val contentNoteTextView: ContentNoteTextView = result.view as ContentNoteTextView
            val contentNoteTextSize = PreferenceManager.getDefaultSharedPreferences(contentNoteTextView.context)
                .getString("content_note_font_size", "18")
            contentNoteTextSize?.toFloat()?.let { contentNoteTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, it) }
        }

        if(result.view is TitleNoteEditText) {
            val titleNoteEditText: TitleNoteEditText = result.view as TitleNoteEditText
            val titleNoteTextSize = PreferenceManager.getDefaultSharedPreferences(titleNoteEditText.context)
                .getString("title_note_font_size", "20")
            titleNoteTextSize?.toFloat()?.let { titleNoteEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, it) }
        }

        if(result.view is ContentNoteEditText) {
            val contentNoteEditText: ContentNoteEditText = result.view as ContentNoteEditText
            val contentNoteTextSize = PreferenceManager.getDefaultSharedPreferences(contentNoteEditText.context)
                .getString("content_note_font_size", "18")
            contentNoteTextSize?.toFloat()?.let { contentNoteEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, it) }
        }

        return result
    }
}