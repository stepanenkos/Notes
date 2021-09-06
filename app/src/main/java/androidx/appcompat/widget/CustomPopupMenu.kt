package androidx.appcompat.widget

import android.annotation.SuppressLint
import android.content.Context
import android.view.View

@SuppressLint("RestrictedApi")
class CustomPopupMenu(context: Context, anchor: View) : PopupMenu(context, anchor) {
    init {
        mPopup.setForceShowIcon(true)
    }
}