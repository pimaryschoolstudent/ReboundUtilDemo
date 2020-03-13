package hbb.example.reboundutildemo

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hbb.example.reboundutil.ReboundLayout
import hbb.example.reboundutil.ReboundUtil
import kotlinx.android.synthetic.main.activity_sliding.*

/**
 * @author HuangJiaHeng
 * @date 2020/3/11.
 */
class SlidingActivity :AppCompatActivity(){

    private var data  = arrayListOf("1","2","3")
    private lateinit var adapter:SimpleAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sliding)
        adapter = SimpleAdapter(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter =adapter

        tool_bar.title = "ReboundUtil"

        btn_add.setOnClickListener {
            data.add("${data.size+1}")
            adapter.notifyDataSetChanged()
            ReboundUtil.getUtil(this).updateRecyclerviewLayoutChange(recyclerView) //更新监听
        }

        btn_remove.setOnClickListener {
            data.removeAt(data.size-1)
            adapter.notifyDataSetChanged()
            ReboundUtil.getUtil(this).updateRecyclerviewLayoutChange(recyclerView) //更新监听
        }

        var bottomView = TextView(this)
        var lp = LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,500)
        bottomView.background = ColorDrawable(Color.GREEN)
        bottomView.layoutParams = lp

        ReboundUtil.getUtil(this).addRebound(this,ReboundUtil.SLIDING,recyclerView)
            .setResistanceFactor(0.3)  //设置阻力因子，值越小越难滑动
            .setHeadBoundView(R.layout.header_view, LayoutInflater.from(this)) //设置头部View
            .setBottomBoundView(bottomView) //设置底部View
            .setBoundType(ReboundUtil.HEADER_REBOUND_LIMIT,ReboundUtil.BOTTOM_REBOUND_NORMAL) //设置滑动方式  limit：滑动距离不超过组件距离  normal：滑动距离可超过组件距离
            .setReboundDuration(3000)
            .setOnReboundListener(object : ReboundLayout.OnReboundListener{
                override fun onRebound(type: Int, state: Int) {

                    if (type == ReboundUtil.REBOUND_HEADER){
                        Log.d("rebound","顶部回弹")
                    }else if (type == ReboundUtil.REBOUND_BOTTOM){
                        Log.d("rebound","底部回弹")
                    }

                    if (state == ReboundUtil.REBOUND_START){
                        Log.d("rebound","开始回弹")
                    }
                    if (state == ReboundUtil.REBOUND_ING){
                        Log.d("rebound","回弹中")
                    }
                    if (state == ReboundUtil.REBOUND_END){
                        Log.d("rebound","回弹结束")
                    }

                }

            })
    }

    inner class SimpleAdapter(var context: Context) : RecyclerView.Adapter<ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var itemView = LayoutInflater.from(context).inflate(R.layout.item_simple,parent,false)
            return ViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.getView<TextView>(R.id.item_tv).text = data[position]
        }

    }
    class ViewHolder(private var v: View) : RecyclerView.ViewHolder(v){
        fun <T : View> getView(id:Int):T{
            return v.findViewById(id)
        }
    }
}