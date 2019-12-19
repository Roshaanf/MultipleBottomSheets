# Multiple Bottom Sheets
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

MultipleBottomSheets is an Android library to implement multiple nested bottom sheets in Android.

![](https://github.com/Roshaanf/MultipleBottomSheets/blob/master/CurveSheetDemo.gif)   ![](https://github.com/Roshaanf/MultipleBottomSheets/blob/master/DefaultSheetDemo.gif) 

## Download

Add jcenter() in project level build.gradle.

```` groovy
repositories {
      jcenter()
}
````

Add dependency in app level build.gradle.

``` groovy
dependencies{
      implementation 'com.roshaan.multiplebottomsheets:multiplebottomsheets:1.0.0'
}
```

## Usage
``` xml

<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.roshaan.multiplebottomsheets.MultipleSheetsContainer
        android:id="@+id/sheetContainer"
        android:layout_width="0dp"
        android:layout_height="490dp"
        app:sheetsCount="5"
        app:sheetsHeightDifference="50dp"
        app:sheetsTopCornerRadius="20dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"/>

</androidx.constraintlayout.widget.ConstraintLayout>

```

* **sheetsCount**: Number of sheets to display, default value is 3.
* **sheetsHeightDifference** Defines the height difference between sheets, default value is 30dp.
* **sheetsTopCornerRadius** Defines the top corner radius for each sheet, default value is 0dp.
* Maximum expanded height for the first sheet is equal to the height of MultipeSheetsContainer, each below sheet's height is calculated by subtracting minimumSheetsHeightDifference from the height of the sheet above it. 
* Minimum collpased height for the first sheet is calculated by this formula (sheetsCount * sheetsHeightDifference).

Associate fragment with each sheet using code.

``` kotlin
        sheetContainer.addFragment(0, Sheet1Fragment())
        sheetContainer.addFragment(1, Sheet2Fragment())
        sheetContainer.addFragment(2, Sheet3Fragment())
        sheetContainer.addFragment(3, Sheet4Fragment())
        sheetContainer.addFragment(4, Sheet5Fragment())
```

## Limitations
* Currently MultipleSheetsContainer supports layout_height="match_parent" or explicitly defined height, layout_height="wrap_content" is not supported. 
* MultipleSheetsContainer must be attached to the bottom of the root layout.


