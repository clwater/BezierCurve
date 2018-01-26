package clwater.beziercurve

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * Created by gengzhibo on 2018/1/17.
 */
class BezierCurveView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    data class Point(var x: Float, var y:Float)   //坐标点的数据类

    var per = 0F
    var points : MutableList<Point> = ArrayList()
    val bezierPoints : MutableList<Point> = ArrayList()
    var viewTime = 1000F    //动画时间

    val linePaint = Paint()
    val textPaint = Paint()
    val path = Path()

    var inRunning = true    //是否在绘制图像
    var isMore = false      //是否是无限制控制点模式
    var drawControl = true  //是否绘制辅助线



    var level = 0
    //点层级字符集
    val charSequence = listOf(
            "P",
            "A" ,"B" ,"C" ,"D" ,"E"
            ,"F" ,"G" ,"H" ,"I" ,"J"
            ,"K" ,"L" ,"M" ,"N"
    )
    //辅助线颜色集
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
        //初始化相关工具类
        initBaseTools()

        if (inRunning && points.size > 0) {
            //绘制贝塞尔曲线
            drawBezier(canvas, per, points)
            level = 0
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

    //通过递归方法绘制贝塞尔曲线
    private fun  drawBezier(canvas: Canvas, per: Float, points: MutableList<Point>) {

        val inBase: Boolean

        //判断当前层级是否需要绘制线段
        if (level == 0 || drawControl){
            inBase = true
        }else{
            inBase = false
        }


        //根据当前层级和是否为无限制模式选择线段及文字的颜色
        if (isMore){
            linePaint.color = 0x3F000000
            textPaint.color = 0x3F000000
        }else {
            linePaint.color = colorSequence[level].toInt()
            textPaint.color = colorSequence[level].toInt()
        }

        //移动到开始的位置
        path.moveTo(points[0].x , points[0].y)

        //如果当前只有一个点
        //根据贝塞尔曲线定义可以得知此点在贝塞尔曲线上
        //将此点添加到贝塞尔曲线点集中(页面重新绘制后之前绘制的数据会丢失 需要重新回去前段的曲线路径)
        //将当前点绘制到页面中
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

        //更新路径信息
        //计算下一级控制点的坐标
        for (index in 1..points.size - 1){
            path.lineTo(points[index].x , points[index].y)

            val nextPointX = points[index - 1].x -(points[index - 1].x - points[index].x) * per
            val nextPointY = points[index - 1].y -(points[index - 1].y - points[index].y) * per

            nextPoints.add(Point(nextPointX , nextPointY))
        }

        //绘制控制点的文本信息
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

        //绘制当前层级
        if (!(level !=0 && (per==0F || per == 1F) )) {
            if (inBase) {
                canvas.drawPath(path, linePaint)
            }
        }
        path.reset()

        //更新层级信息
        level++

        //绘制下一层
        drawBezier(canvas, per, nextPoints)

    }

    //绘制前段贝塞尔曲线部分
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