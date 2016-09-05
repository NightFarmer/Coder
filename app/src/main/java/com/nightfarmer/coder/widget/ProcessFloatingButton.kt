package com.nightfarmer.coder.widget

import android.content.Context
import android.graphics.*
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import org.w3c.dom.Attr

/**
 * Created by zhangfan on 16-9-5.
 */
class ProcessFloatingButton : FloatingActionButton {

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {

    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {

    }

    constructor(context: Context) : super(context) {

    }

    var paint = Paint()
    var strokeWidth = 4f

    var progress = 0f
        set(value) {
            field = value
            invalidate()
        }

    init {
        paint.color = Color.WHITE
        paint.strokeWidth = strokeWidth
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val oval = RectF();                     //RectF对象
        val padding = strokeWidth / 2
        oval.left = padding;                              //左边
        oval.top = padding;                                   //上边
        oval.right = width - padding;                             //右边
        oval.bottom = height - padding;                                //下边
        canvas?.drawArc(oval, -90f, 360 * progress, false, paint);    //绘制圆弧
    }


}