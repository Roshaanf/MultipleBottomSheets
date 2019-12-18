package com.roshaan.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Context
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.DisplayMetrics


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sheetContainer.addFragment(0, BlankFragment())
        sheetContainer.addFragment(1, BlankFragment2())
        sheetContainer.addFragment(2, BlankFragment3())
        sheetContainer.addFragment(3, BlankFragment4())
        sheetContainer.addFragment(4, BlankFragment5())
    }
}
