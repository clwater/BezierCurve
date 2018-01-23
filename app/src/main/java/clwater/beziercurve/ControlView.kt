package clwater.beziercurve

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.MotionEvent


/**
 * Created by gengzhibo on 2018/1/17.
 */
class ControlView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    var points: MutableList<BezierCurveView.Point> = ArrayList()
    var maxPoint = 3
    var controlIndex = 0

    var touchX = 0F
    var touchY = 0F
    var isMore = false



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val heitht = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, heitht)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val path = Path()

        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5F

        val PointPaint = Paint()
        PointPaint.style = Paint.Style.STROKE
        PointPaint.strokeWidth = 20F

        val textPaint = Paint()
        textPaint.color = Color.BLACK
        textPaint.strokeWidth = 5F
        textPaint.textSize = 50F
        textPaint.style = Paint.Style.FILL


        if (controlIndex !=0 ){

            path.moveTo(points[0].x , points[0].y)
            canvas.drawPoint(points[0].x , points[0].y , PointPaint)
            canvas.drawText("P0" , points[0].x , points[0].y , textPaint)

            for (i in 1..points.size - 1){
                path.lineTo(points[i].x , points[i].y)
                canvas.drawPoint(points[i].x , points[i].y , PointPaint)
                canvas.drawText("P${i}" , points[i].x , points[i].y , textPaint)

            }

            canvas.drawPath(path, paint)
        }


//        canvas.drawText("x: ${touchX} , y: ${touchY}" , 100F ,100F , textPaint)


    }

    fun addPoints(point: BezierCurveView.Point){

        if (controlIndex < maxPoint || isMore == true) {

            points.add(point)
            controlIndex++

            invalidate()
        }
    }

    fun clear(){
        controlIndex = 0
        points.clear()
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

            touchX = event.x
            touchY = event.y

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (controlIndex < maxPoint || isMore == true) {
                        addPoints(BezierCurveView.Point(touchX, touchY))
                    }
                    invalidate()
                }


        }
        //true表示已处理该方法
        return true
    }

}