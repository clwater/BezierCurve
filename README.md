# Android 自定义贝塞尔曲线工具

>之前在学习贝塞尔曲线的相关内容,在查找相关资料的时候发现网上的资料重复的太多了,而且因为android的canvas只提供了quadTo,cubicTo两种方法来绘制二阶和三阶的贝塞尔曲线.在线的贝塞尔曲线绘制网站也很少,(在这提供一个[在线贝塞尔曲线](http://gitbug.top/webutils/B%C3%A9zier%20curve/%E8%B4%9D%E5%A1%9E%E5%B0%94%E6%9B%B2%E7%BA%BF%E5%9C%A8%E7%BA%BF%E5%B7%A5%E5%85%B7.html)的网站,根据网上的资料整理的),而在android手机中缺没有类似的工具,在设计或者使用贝塞尔曲线的时候增加了很多的工作,刚好在学习相关的知识,就做了一个较为完善的android端的贝塞尔曲线工具.


## 贝塞尔曲线
基本的贝塞尔曲线的知识就不多说了,有兴趣的可以参考下我之后会完成的贝塞尔曲线的记录

其实理解贝塞尔曲线十分容易,可以将其理解为一种递归的形式.根据比例系数计算当前线段中的点,得到所有点之后再按照顺序连接线段,重复以上步骤,直至只剩下一个点,此点就在贝塞尔曲线中,计算各个比例系数下的点,这些点的集合就是贝塞尔曲线.

## 基本功能
### 绘制常见的贝塞尔曲线
可以绘制常见的二阶,三阶贝塞尔曲线

![二阶贝塞尔曲线](http://ooymoxvz4.bkt.clouddn.com/18-1-30/21257830.jpg)

### 绘制多阶的贝塞尔曲线
可以绘制不常见的贝塞尔曲线

![六阶贝塞尔曲线](http://ooymoxvz4.bkt.clouddn.com/18-1-26/26593287.jpg)

### 开启/关闭辅助线
可以开启不同颜色层级的辅助线段

![开启关闭辅助线段](http://ooymoxvz4.bkt.clouddn.com/18-1-26/26642232.jpg)

### 绘制无上限制的贝塞尔曲线
突破15个关键点的限制 绘制无上限(虽然用处不大 但是开启辅助线后...迷之好玩)

![无限制下不展示辅助线的贝塞尔曲线绘制](http://ooymoxvz4.bkt.clouddn.com/18-1-26/90938658.jpg)

### 微调关键点绘制新的贝塞尔曲线
微调关键点来绘制新的贝塞尔曲线

![微调关键点绘制新的贝塞尔曲线](http://ooymoxvz4.bkt.clouddn.com/18-1-26/68892801.jpg)

### 设置贝塞尔曲线的绘制时间
设置贝塞尔曲线的绘制时间,绘制时间越长贝塞尔曲线越流畅

![不同时间长度的贝塞尔曲线绘制](http://ooymoxvz4.bkt.clouddn.com/18-1-26/24247370.jpg)

## 设计过程
设计了两个自定义view,其中一个自定义view用于收集屏幕的触摸事件,并展示添加的控制点和控制点之间的连线,实现长按屏幕拖动一定范围内最近的点.另一个自定义view用于接收控制点的参数,并根据控制点绘制贝塞尔曲线及辅助信息.

### 贝塞尔曲线绘制层
通过递归的方法,每一层中绘制当前控制点控制点之间的线段.除了第一层的样式是固定的之外,一定阶数下的辅助线段及控制点都可以被控制是否展示.而当开启无限制模式的时候,当前绘制的贝塞尔曲线的控制点没有上限,但是为了展示的效果当前模式下的辅助线段的样式都是一致的.

### 屏幕触摸事件监测层
监测屏幕的点击事件,增加控制点,除此之外,在长时间触摸屏幕后还会开启是否需要移动一定范围内最近的点移动到触碰的位置的监测.并能提供当前点的列表用于贝塞尔曲线绘制层绘制贝塞尔曲线绘制层来绘制贝塞尔曲线.

## 代码实现

### 屏幕触摸事件监测层

主要在于对屏幕的触碰事件的监测
```java
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
```

关于最近的点的检测,勾股定理就可以得到了.
```java
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
```

相对来说,主要的难点是屏幕的触碰检测,需要控制时间和是否长安后找到合适的点之后的移动.除此之外就是简单的更加触碰点添加线段就好.

### 贝塞尔曲线绘制层
主要的贝塞尔曲线是通过递归实现的
```java
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
```

除此之外,因为每次计算得到的是贝塞尔曲线的点,所以需要将这些点收集起来,并将之前收集到的所有的点绘制出来
```java
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
```

相关的代码可以[访问我的Github](https://github.com/clwater/BezierCurve.git),欢迎大家star或提出建议.
