package clwater.beziercurve

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    var index = 3
    var time = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        //开始绘制按钮
        start.setOnClickListener {
            if (control.controlIndex >= control.maxPoint || bezier.isMore == true) {
                bezier.points = control.points
                bezier.visibility = View.VISIBLE
                bezier.inRunning = true
                bezier.viewTime = time* 1000F
                bezier.changeView()
                control.inChangePoint = false
            }
        }

        //清除按钮
        clear.setOnClickListener{
            bezier.visibility = View.GONE
            bezier.inRunning = false
            control.clear()
        }

        //关闭曲线绘制图层
        clearBezier.setOnClickListener{
            bezier.visibility = View.GONE
            bezier.inRunning = false
            control.inChangePoint = true
        }

        //开启/关闭辅助线
        chooseAuxiliary.setOnClickListener{
            if (bezier.drawControl){
                bezier.drawControl = false
                chooseAuxiliary.text = "Open Auxiliary"
            }else{
                bezier.drawControl = true
                chooseAuxiliary.text = "Close Auxiliary"
            }
        }

        //设置seekbar监听及相关文本设置
        max_controrl.text = "index: ${index}"
        seekbar_index.progress = index
        seekbar_index.setOnSeekBarChangeListener(this)

        max_time.text = "time: ${time}"
        seekbar_time.progress = time * 1000
        seekbar_time.setOnSeekBarChangeListener(this)

        //是否开启无限制控制点绘制
        more.setOnClickListener{
            if (control.isMore == true){
                control.isMore = false
                bezier.isMore = false
                more.text = "More Points"
                linear_index.visibility = View.VISIBLE
            }else{
                control.isMore = true
                bezier.isMore = true
                more.text = "No More"
                linear_index.visibility = View.GONE
            }

            bezier.visibility = View.GONE
            bezier.inRunning = false
            control.clear()
        }
    }

    //seekbar拖动监听
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        if (seekBar?.id == R.id.seekbar_index) {
            index = progress
            if (progress < 2) {
                index = 2
            }
            max_controrl.text = "index: ${index}"
        }else if (seekBar?.id == R.id.seekbar_time){
            time = progress / 1000
            if (time < 1){
                time = 1
            }
            max_time.text = "time: ${time}"
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (seekBar?.id == R.id.seekbar_index) {
            control.maxPoint = index
            bezier.inRunning = false
            bezier.visibility = View.GONE
            control.clear()
        }
    }

}
