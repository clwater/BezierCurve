package clwater.beziercurve

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * Created by gengzhibo on 2018/1/17.
 */
class BezierCurveView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    data class Point(val x: Float , val y:Float)   //坐标点的数据类

    var per = 0.5F
    val points : MutableList<Point> = ArrayList()


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val heitht = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, heitht)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

//        canvas.translate(0F , height.toFloat())
//        canvas.scale(1F , -1F)

        points.add(Point(200F , 100F))
        points.add(Point(150F , 200F))
        points.add(Point(300F , 100F))

        drawBezier(canvas ,  per , points ,true)

    }

    private fun  drawBezier(canvas: Canvas, per: Float, points: MutableList<Point>, inBase: Boolean) {
        val paint = Paint()
        paint.color = Color.BLACK
        paint.strokeWidth = 5F
        paint.textSize = 20F

        val path = Path()

        path.moveTo(points[0].x , points[0].y)
        canvas.drawText( "0",points[0].x , points[0].y , paint)

        if (points.size == 1){
            return
        }


        val nextPoints: MutableList<Point> = ArrayList()


        for (index in 1..points.size - 1){
            path.lineTo(points[index].x , points[index].y)
            canvas.drawText( index.toString() ,points[index].x , points[index].y , paint)

            val nextPointX = points[index - 1].x -(points[index - 1].x - points[index].x) * per
            val nextPointY = points[index - 1].y -(points[index - 1].y - points[index].y) * per

            nextPoints.add(Point(nextPointX , nextPointY))
        }

        paint.style = Paint.Style.STROKE
        canvas.drawPath(path , paint)

        drawBezier(canvas, per, nextPoints, inBase)
    }
}