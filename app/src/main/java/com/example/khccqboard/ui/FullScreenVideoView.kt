//package com.example.khccqboard.ui
//
//import android.content.Context
//import android.util.AttributeSet
//import android.widget.VideoView
//
//class FullScreenVideoView @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
//) : VideoView(context, attrs, defStyleAttr) {
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val width = MeasureSpec.getSize(widthMeasureSpec)
//        val height = MeasureSpec.getSize(heightMeasureSpec)
//
//        // Stretch the video to fill the entire space of the container (ignoring aspect ratio)
//        setMeasuredDimension(width, height)
//    }
//}
