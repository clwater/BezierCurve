package clwater.beziercurve

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * Created by gengzhibo on 2018/1/17.
 */
class BezierCurveView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    data class Point(val x: Float , val y:Float)   //坐标点的数据类

    var per = 0F
    var points : MutableList<Point> = ArrayList()
    val bezierPoints : MutableList<Point> = ArrayList()
    var viewTime = 1000F

    val linePaint = Paint()
    val textPaint = Paint()
    val path = Path()
    var inRunning = true
    var isMore = false
    var drawControl = true



    var level = 0

    val charSequence = listOf(
            "P",
            "A" ,"B" ,"C" ,"D" ,"E"
            ,"F" ,"G" ,"H" ,"I" ,"J"
            ,"K" ,"L" ,"M" ,"N"
    )

    val colorSequence = listOf(
            0x7F000000,
            0xff1BFFF8,
            0xff17FF89,
            0xff25FF2F,
            0xffA7FF05,
            0xffFFE61E,
            0xffFF9B0C,
            0xffFF089F,
            0xffE524FF,
            0xff842BFF,
            0xff090BFF,
            0xff0982FF,
            0xffF8A1FF,
            0xffF7FFA7,
            0xffAAFFEC
    )




    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val heitht = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, heitht)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        initBaseTools()


        if (inRunning) {

            if (points.size > 0) {
                drawBezier(canvas, per, points)
                level = 0
            }
        }


    }


    private fun initBaseTools() {
        linePaint.color = Color.BLACK
        linePaint.strokeWidth = 5F
        linePaint.style = Paint.Style.STROKE

        textPaint.color = Color.BLACK
        textPaint.strokeWidth = 5F
        textPaint.textSize = 50F
        textPaint.style = Paint.Style.FILL


    }

    private fun  drawBezier(canvas: Canvas, per: Float, points: MutableList<Point>) {
        val inBase: Boolean
        if (level == 0){
            inBase = true
        }else{
            if (drawControl == true){
                inBase = true
            }else{
                inBase = false
            }
        }


        if (isMore){
            linePaint.color = 0x3F000000
            textPaint.color = 0x3F000000
        }else {
            linePaint.color = colorSequence[level].toInt()
            textPaint.color = colorSequence[level].toInt()
        }

        path.moveTo(points[0].x , points[0].y)



        if (points.size == 1){
            bezierPoints.add(Point(points[0].x , points[0].y))
            drawBezierPoint(bezierPoints , canvas)
            val paint = Paint()
            paint.strokeWidth = 10F
            paint.style = Paint.Style.FILL
            canvas.drawPoint(points[0].x , points[0].y , paint)
            return
        }


        val nextPoints: MutableList<Point> = ArrayList()


        for (index in 1..points.size - 1){
            path.lineTo(points[index].x , points[index].y)

            val nextPointX = points[index - 1].x -(points[index - 1].x - points[index].x) * per
            val nextPointY = points[index - 1].y -(points[index - 1].y - points[index].y) * per

            nextPoints.add(Point(nextPointX , nextPointY))
        }


        if (!(level !=0 && (per==0F || per == 1F) )) {
            if (inBase) {
                if (isMore && level != 0){
                    canvas.drawText("0:0", points[0].x, points[0].y, textPaint)
                }else {
                    canvas.drawText("${charSequence[level]}0", points[0].x, points[0].y, textPaint)
                }
                for (index in 1..points.size - 1){
                    if (isMore && level != 0){
                        canvas.drawText( "${index}:${index}" ,points[index].x , points[index].y , textPaint)
                    }else {
                        canvas.drawText( "${charSequence[level]}${index}" ,points[index].x , points[index].y , textPaint)
                    }
                }
            }
        }


        if (!(level !=0 && (per==0F || per == 1F) )) {
            if (inBase) {
                canvas.drawPath(path, linePaint)
            }
        }
        path.reset()

        level++

        drawBezier(canvas, per, nextPoints)

    }

    private fun  drawBezierPoint(bezierPoints: MutableList<Point> , canvas: Canvas) {
        val paintBse = Paint()
        paintBse.color = Color.RED
        paintBse.strokeWidth = 5F
        paintBse.style = Paint.Style.STROKE

        val path = Path()
        path.moveTo(bezierPoints[0].x , bezierPoints[0].y)

        for (index in 1..bezierPoints.size -1){
            path.lineTo(bezierPoints[index].x , bezierPoints[index].y)
        }

        canvas.drawPath(path , paintBse)

    }

    //开始动画
    fun changeView() {
        bezierPoints.clear()
        val va = ValueAnimator.ofFloat(0F, 1F)
        va.duration = viewTime.toLong()
        va.interpolator = LinearInterpolator()
        va.addUpdateListener { animation ->
            per = animation.animatedValue as Float
            invalidate()
        }
        va.start()
    }
}