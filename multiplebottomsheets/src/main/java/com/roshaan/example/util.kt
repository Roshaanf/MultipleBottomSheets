package com.roshaan.example

import android.content.Context

inline fun dpFromPx(context: Context, px: Float) =
    (px / context.getResources().getDisplayMetrics().density).toInt()

inline fun pxFromDp(context: Context, dp: Float) =
    (dp * context.getResources().getDisplayMetrics().density).toInt()
