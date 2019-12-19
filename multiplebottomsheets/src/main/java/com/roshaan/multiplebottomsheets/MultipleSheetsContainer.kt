package com.roshaan.multiplebottomsheets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * MultipleSheetsContainer must have layout_height="match_parent" or defined in dp, wrap_content is not supported
 * Default minimumSheetsHeightDifference is 30dp
 * Default sheetsCount is 3
 * Default sheetsCornerRadius is 0dp
 * lower sheet will be added in last , lower sheet will have minimum heights, lower sheet will have heighest index
 * bottom sheets should be attached to the bottom of the screen i,e no view should be placed below bottom sheets
 *  */
class MultipleSheetsContainer : RelativeLayout {

    private var sheetsCount: Int = DEFAULT_SHEETS_COUNT
    //   Values IN DP
    private var minimumSheetsHeightDifference: Int =
        DEFAULT_MINIMUM_SHEETS_HEIGHT_DIFFERENCE
    private var sheetsTopCornerRadius: Float =
        DEFAULT_SHEETS_TOP_CORNER_RADIUS


    private val sheets = mutableListOf<CustomSheet>()
    private val screenMaxHeight = context.getResources().getDisplayMetrics().heightPixels;
    private var areSheetsAdded = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.MultipleSheetsContainer, 0, 0
        )
        sheetsCount =
            typedArray.getInt(
                R.styleable.MultipleSheetsContainer_sheetsCount,
                DEFAULT_SHEETS_COUNT
            )
        minimumSheetsHeightDifference =
            typedArray.getDimension(
                R.styleable.MultipleSheetsContainer_sheetsHeightDifference,
                pxFromDp(
                    context,
                    DEFAULT_MINIMUM_SHEETS_HEIGHT_DIFFERENCE.toFloat()
                ).toFloat()
            ).toInt()

        sheetsTopCornerRadius = typedArray.getDimension(
            R.styleable.MultipleSheetsContainer_sheetsTopCornerRadius,
            DEFAULT_SHEETS_TOP_CORNER_RADIUS
        )

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

//        onSizeChanged could be called multiple types like for events like keyboard open
        if (!areSheetsAdded) {
/*
*           calling addSheets here so we can get actual height of this view
*           before onSizeChanged this view's height can change
*/
            addSheets()

            areSheetsAdded = true

//            request layout won't work inside onSizeChanged because it is part of view cycle so delaying its execution
            post { requestLayout() }
        }
    }

    private fun addSheets() {


        for (i in sheetsCount downTo 1) {

//            first sheet will have maximum minimum height
            var minimumHeight =
                minimumSheetsHeightDifference * i

//            if adding first sheet then make its height = to this view's height
//            else subtract height difference from previous sheet's max height
            var maximumHeight = if (sheets.size == 0) height//this.height
            else sheets.get(sheets.size - 1).maxHeight - minimumSheetsHeightDifference


            var params = LayoutParams(
                LayoutParams.MATCH_PARENT,
                minimumHeight
            )

//            aligning sheet to the bottom of view
            params.addRule(ALIGN_PARENT_BOTTOM)

            var sheet =
                CustomSheet(this.context)
            sheet.minHeight = minimumHeight
            sheet.maxHeight = maximumHeight

            sheet.id = View.generateViewId()

            sheet.topCornerRadius = sheetsTopCornerRadius

            addView(sheet, params)
            setOnSheetTouchListener(sheet)

            sheets.add(sheet)

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnSheetTouchListener(customSheet: CustomSheet) {

        var dy = 0
        var shouldDrag = false

        customSheet.setOnTouchListener { currentSheet, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    var sheetActualCoordinatedOnScreen = intArrayOf(0, 0)
                    /**
                     *  getLocationOnScreen returns actual coordinate of view w.r.t screen
                     *  not w.r.t parent viewgroup
                     */
                    currentSheet.getLocationOnScreen(sheetActualCoordinatedOnScreen)

//                        dY represents the area which should be ignored while setting newHeight
                    dy = (event.rawY - sheetActualCoordinatedOnScreen.get(1)).toInt()


                    /**
                     *  drag effect only works if it is occured on top area of sheet height= minHeight
                     *  not using event.rawY-sheet.y, because sheet.y returns y coordinate of view
                     *  w.r.t parent viewgroup and event.rawY returns actual location of event w.r.t whole screen
                     */
                    shouldDrag =
                        if ((event.rawY.toInt() - sheetActualCoordinatedOnScreen.get(1))
                            > minimumSheetsHeightDifference
                        ) {
                            false
                        } else true
                }

                MotionEvent.ACTION_MOVE -> {


                    if (!shouldDrag)
                        return@setOnTouchListener false


                    /**
                     *  subtracting event's y value from total screen height and adding ignored area to get actual location of view
                     *  newHeight will be equal to that value only fr current sheet
                     */
                    var newHeight = (screenMaxHeight - event.rawY).toInt() + dy
//                        subtracting sheet's previous height from newHeight to be to get dragged area
                    var draggedYPixels = Math.abs(currentSheet.height - newHeight)
                    var isUpwardMotion = newHeight > currentSheet.height

//                    view should respect its minimum height
                    if (newHeight < (currentSheet as CustomSheet).minHeight) {

                        newHeight = currentSheet.minHeight
                        draggedYPixels = Math.abs(currentSheet.height - newHeight)
                        isUpwardMotion = newHeight > currentSheet.height

                    }
//                    view should respect its max height
                    else if (newHeight > currentSheet.maxHeight) {

                        newHeight = currentSheet.maxHeight
                        draggedYPixels = Math.abs(currentSheet.height - newHeight)
                        isUpwardMotion = newHeight > currentSheet.height

                    }
//                           APPLYING CALCULATIONS TO SHEETS

//                            MOVING CURRENT SHEET
                    (currentSheet.layoutParams as LayoutParams).height =
                        newHeight

//                      if it is upward motion DRAGG SHEETS ABOVE CURRENT SHEET ALSO
                    if (isUpwardMotion) {

//                                dragging each above sheet
                        for (j in 0 until sheets.indexOf(currentSheet)) {

                            var aboveSheet = sheets.get(j)
                            newHeight = aboveSheet.height + draggedYPixels

//
//                               sheet should respect its max height
                            if (newHeight > aboveSheet.maxHeight) {
                                (aboveSheet.layoutParams as LayoutParams).height =
                                    aboveSheet.maxHeight
                            } else {

//                                    moving sheet
                                (aboveSheet.layoutParams as LayoutParams).height =
                                    newHeight
                            }

                        }
                    }
//                            DOWNWARD MOTION DRAG SHEETS BELOW CURRENT SHEET ALSO
                    else {

//                                dragging each below sheet
                        for (j in sheets.indexOf(currentSheet) + 1 until sheets.size) {

                            var belowSheet = sheets.get(j)
                            newHeight = belowSheet.height - draggedYPixels

//                                sheet should respect its minimum height
                            if (newHeight < (belowSheet).minHeight) {
                                (belowSheet.layoutParams as LayoutParams).height =
                                    belowSheet.minHeight
                            } else {
//                                    moving sheet
                                (belowSheet.layoutParams as LayoutParams).height =
                                    newHeight
                            }

                        }
                    }


//                        calling reqeust layout for all sheets to make height changes visible
                    sheets.forEach { it.requestLayout() }

                    return@setOnTouchListener true
                }
            }
            true

        }
    }

    fun addFragment(index: Int, fragment: Fragment) {
        if (fragment == null)
            return
//     adding delay so fragment will be added after sheets have been rendered
        postDelayed({
            if (context is AppCompatActivity)
                (context as AppCompatActivity).supportFragmentManager
                    .beginTransaction()
                    .add(sheets.get(index).id, fragment)
                    .commit()

        }, 100)

    }

    fun addFragments(fragments: List<Fragment>) {
        if (fragments == null) return

//     adding delay so fragment will be added after sheets have been rendered
        postDelayed(Runnable {
            if (context is AppCompatActivity)
                for (i in 0 until fragments.size)
                    (context as AppCompatActivity).supportFragmentManager
                        .beginTransaction()
                        .add(sheets.get(i).id, fragments.get(i))
                        .commit()

        }, 100)

    }


    private class CustomSheet : FrameLayout {

        var maxHeight: Int = 0
        var minHeight: Int = 0

        var topCornerRadius = DEFAULT_SHEETS_TOP_CORNER_RADIUS
            set(value) {
                field = pxFromDp(context, value).toFloat()
                //        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                //      invalidate()
            }


        constructor(context: Context) : this(context, null)
        constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
        )

        override protected fun dispatchDraw(canvas: Canvas) {

//       setting corner radius
            val count = canvas.save()

            val path = Path()

            val cornerDimensions = floatArrayOf(
                topCornerRadius, //topRightCornerRadius
                topCornerRadius, //topRightCornerRadius
                topCornerRadius, //topLeftCornerRadius
                topCornerRadius, //topLeftCornerRadius
                0f, //            bottomRightCornerRadius,
                0f,//            bottomRightCornerRadius,
                0f,//            bottomLeftCornerRadius,
                0f //            bottomLeftCornerRadius
            )

            path.addRoundRect(
                RectF(0f, 0f, canvas.getWidth().toFloat(), canvas.getHeight().toFloat()),
                cornerDimensions,
                Path.Direction.CW
            )

            canvas.clipPath(path)

            super.dispatchDraw(canvas)
            canvas.restoreToCount(count)
        }

    }

}

