package hbb.example.reboundutildemo

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import hbb.example.reboundutil.ReboundUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tool_bar.title = "ReboundUtil"

        var bottomView = TextView(this)
        var lp = LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,500)
        bottomView.background = ColorDrawable(Color.GREEN)
        bottomView.layoutParams = lp

        //添加回弹效果
        ReboundUtil.getUtil(this)
            .addRebound(this,ReboundUtil.NON_SLIDING)  //无滑动元素添加
            .setResistanceFactor(0.3)  //设置阻力因子，值越小越难滑动
            .setHeadBoundView(R.layout.header_view, LayoutInflater.from(this)) //设置头部View
            .setBottomBoundView(bottomView) //设置底部View
            .setBoundType(ReboundUtil.HEADER_REBOUND_LIMIT,ReboundUtil.BOTTOM_REBOUND_NORMAL) //设置滑动方式  limit：滑动距离不超过组件距离  normal：滑动距离可超过组件距离

        btn.setOnClickListener {
            startActivity(Intent(this,SlidingActivity::class.java))
        }

    }
}
