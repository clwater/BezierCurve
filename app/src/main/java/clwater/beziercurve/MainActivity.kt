package clwater.beziercurve

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    val activity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        bezier.visibility = View.GONE
        start.setOnClickListener {
            bezier.points = control.points
            bezier.visibility = View.VISIBLE
            bezier.changeView()
        }


        clear.setOnClickListener{
            bezier.visibility = View.GONE
            control.clear()
        }

    }

}
