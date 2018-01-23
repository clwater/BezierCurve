package clwater.beziercurve

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    var index = 3
    var time = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bezier.visibility = View.GONE
        start.setOnClickListener {
            if (control.controlIndex >= control.maxPoint || bezier.isMore == true) {
                bezier.points = control.points
                bezier.visibility = View.VISIBLE
                bezier.inRunning = true
                bezier.changeView()
            }
        }


        clear.setOnClickListener{
            bezier.visibility = View.GONE
            bezier.inRunning = false
            control.clear()
        }

        clearBezier.setOnClickListener{
            bezier.visibility = View.GONE
            bezier.inRunning = false

        }


        chooseHelper.setOnClickListener{
            if (bezier.drawControl){
                bezier.drawControl = false
                chooseHelper.text = "Open Helper"
            }else{
                bezier.drawControl = true
                chooseHelper.text = "Close Helper"
            }
        }

        max_controrl.text = "index: ${index}"
        seekbar_index.progress = index
        seekbar_index.setOnSeekBarChangeListener(this)

        max_time.text = "time: ${time}"
        seekbar_time.progress = time * 1000
        seekbar_time.setOnSeekBarChangeListener(this)


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

    fun updateSeek(){
        max_controrl.text = "index: ${index}"
    }


    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        if (seekBar?.id == R.id.seekbar_index) {
            index = progress
            if (progress < 2) {
                index = 2
            }
            updateSeek()
        }else if (seekBar?.id == R.id.seekbar_time){
            time = progress / 1000
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
        }else if (seekBar?.id == R.id.seekbar_time){
            bezier.viewTime = time* 1000F
        }
    }

}
