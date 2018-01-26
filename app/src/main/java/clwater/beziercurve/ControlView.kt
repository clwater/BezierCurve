package clwater.beziercurve

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.MotionEvent
import java.text.DecimalFormat


/**
 * Created by gengzhibo on 2018/1/22.
 */
class ControlView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    var points: MutableList<BezierCurveView.Point> = ArrayList()
    var maxPoint = 3        //最大控制点数量
    var controlIndex = 0    //当前添加的控制点
    val minLength = 100     //更改点位置时最大间隔

    var touchX = 0F         //当前屏幕触碰点的x坐标
    var touchY = 0F         //当前屏幕触碰点的y坐标


    var isMore = false      //是否是无限制模式
    var inChangePoint = true    //是否需要检查移动点
    var changePoint = false     //触摸点是否未移动
    var toFindChageCounts = false   //是否需要检测当前触碰点时候和已经绘制的点相近
    var findPointChangeIndex = -1   //触碰点和最近的已绘制点的标识
    var checkLevel = -1         //当前检测的次数 防止静止时未能检测 移动过程中检测到相近的点

    var lastPoint = BezierCurveView.Point(0F , 0F)


    val paint = Paint()
    val PointPaint = Paint()
    val textPaint = Paint()


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val heitht = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, heitht)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //初始化相关工具类
        initBaseTools()
        //绘制选择的点
        drawPoints(canvas)
        //绘制当前触碰点的坐标
        drawXY(canvas)

    }

    private fun drawXY(canvas: Canvas) {
        val df = DecimalFormat("######0.00")
        canvas.drawText("x: ${df.format(touchX)} , y: ${df.format(touchY)}" , 20F ,50F , textPaint)
    }

    private fun drawPoints(canvas : Canvas) {
        val path = Path()

        //绘制选择的点,线段及文字标识
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
    }

    private fun initBaseTools() {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5F

        PointPaint.style = Paint.Style.STROKE
        PointPaint.strokeWidth = 20F

        textPaint.color = Color.BLACK
        textPaint.strokeWidth = 5F
        textPaint.textSize = 50F
        textPaint.style = Paint.Style.FILL
    }

    //将触碰的点添加到list中
    fun addPoints(point: BezierCurveView.Point){

        if (controlIndex < maxPoint || isMore == true) {
            points.add(point)
            controlIndex++
            invalidate()
        }
    }

    //清除当前屏幕内容
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
                toFindChageCounts = true
                findPointChangeIndex = -1
                //增加点前点击的点到屏幕中
                if (controlIndex < maxPoint || isMore == true) {
                    addPoints(BezierCurveView.Point(touchX, touchY))
                }
                invalidate()
            }
            MotionEvent.ACTION_MOVE ->{
                checkLevel++
                //判断当前是否需要检测更换点坐标
                if (inChangePoint){
                    //判断当前是否长按 用于开始查找附件的点
                    if (touchX == lastPoint.x && touchY == lastPoint.y){
                        changePoint = true
                        lastPoint.x = -1F
                        lastPoint.y = -1F
                    }else{
                        lastPoint.x = touchX
                        lastPoint.y = touchY
                    }
                    //开始查找附近的点
                    if (changePoint){
                        if (toFindChageCounts){
                            findPointChangeIndex = findNearlyPoint(touchX , touchY)
                        }
                    }

                    //判断是否存在附近的点
                    if (findPointChangeIndex == -1){
                        if (checkLevel > 1){
                            changePoint = false
                        }

                    }else{
                        //更新附近的点的坐标 并重新绘制页面内容
                        points[findPointChangeIndex].x = touchX
                        points[findPointChangeIndex].y = touchY
                        toFindChageCounts = false
                        invalidate()
                    }
                }

            }
            MotionEvent.ACTION_UP ->{
                checkLevel = -1
                changePoint = false
                toFindChageCounts = false
            }

        }
        return true
    }
    //判断当前触碰的点附近是否有绘制过的点
    private fun findNearlyPoint(touchX: Float, touchY: Float): Int {
        Log.d("bsr"  , "touchX: ${touchX} , touchY: ${touchY}")
        var index = -1
        var tempLength = 100000F
        for (i in 0..points.size - 1){
            val lengthX = Math.abs(touchX - points[i].x)
            val lengthY = Math.abs(touchY - points[i].y)
            val length = Math.sqrt((lengthX * lengthX + lengthY * lengthY).toDouble()).toFloat()
            if (length < tempLength){
                tempLength = length

                if (tempLength < minLength){
                    toFindChageCounts = false
                    index = i
                }
            }
        }

        return index
    }

}