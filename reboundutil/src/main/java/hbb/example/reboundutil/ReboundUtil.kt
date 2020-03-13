package hbb.example.reboundutil

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import java.lang.NullPointerException


/**
 * @author HuangJiaHeng
 * @date 2020/2/26.
 */
class ReboundUtil {

    companion object{

        /**
         * 工具原型集合
         * */
        private var utils = HashMap<Activity,ReboundUtil>()

        /**
         * 可滑动类型
         * 即界面里含有可滑动元素
         * */
        const val SLIDING = 1

        /**
         * 不可滑动类型
         * */
        const val NON_SLIDING = 2

        /**
         * 头部底部View限制滑动
         * limit 下滑距离受限制，不超过Headerview高度
         * normal 下滑距离不受限制，默认效果
         * */
        const val HEADER_REBOUND_LIMIT = 3
        const val BOTTOM_REBOUND_LIMIT = 4
        const val HEADER_REBOUND_NORMAL = 5
        const val BOTTOM_REBOUND_NORMAL = 6

        /**
         * 回弹类型
         * */
        const val REBOUND_HEADER = 7
        const val REBOUND_BOTTOM = 8
        const val REBOUND_START = 9
        const val REBOUND_END = 10
        const val REBOUND_ING = 11

        @Synchronized
        fun getUtil(activity:Activity):ReboundUtil{
            if (utils.keys.contains(activity)){
                return utils[activity]!!
            }
            var util = ReboundUtil()
            utils[activity] = util
            return util
        }
    }
    /**
     * 界面布局
     * contentLayout 界面根布局
     * rootLayout 界面的底部布局
     * reboundLayout 回弹布局
     * headerView 头部View
     * bootomView 底部View
     * */
    private var contentLayout:ViewGroup?=null
    private var rootLayout:View?=null
    private var reboundLayout: ReboundLayout?=null
    private var headertView: View?=null
    private var bottomView:View?=null

    /**
     * 根布局大小
     * */
    private var rootHeight:Int = 0

    /**
     * 高度监听器
     * */
    private lateinit var gListener:ViewTreeObserver.OnGlobalLayoutListener

    /**
     * 是否能够添加组件
     * 判断reboundlayout是否已经加载完毕，加载完毕后再添加头部底部
     * */
    private var rootPostFinish = false

    /**
     * 滑动模式
     * */
    private var headerBoundType = HEADER_REBOUND_NORMAL
    private var bottomBoundType = BOTTOM_REBOUND_NORMAL

    /**
     * recyclerview滑动监听
     * */
    private var recyclerViewOnScrollListener: RecyclerView.OnScrollListener?=null


    /**
     * 回弹间隔
     * */
    private var reboundDuration:Int = 1000

    /**
     * 设置回弹动作类别
     * NORMAL  滑动距离不限制
     * LIMIT   滑动距离不超过头（底）部组件
     * */
    fun setBoundType(headerType:Int,bottomType:Int):ReboundUtil{
        reboundLayout?.setBoundType(headerType,bottomType)
        return this
    }

    /**
     * 添加回弹
     * */
    fun addRebound(activity: Activity, type:Int,scrollView: View?=null):ReboundUtil{
        when(type){
            SLIDING -> {
                if (scrollView is RecyclerView){
                    addSliDingRebound(activity,scrollView)
                }else if (scrollView is ScrollView){
                    addSliDingRebound(activity,scrollView)
                }else if (scrollView is NestedScrollView){
                    addSliDingRebound(activity,scrollView)
                }else{

                }
            }
            NON_SLIDING -> addNormalRebound(activity)
        }
        return this
    }

    /**
     * 设置回弹头部
     * */
    fun setHeadBoundView(headertView: View):ReboundUtil{
        if (reboundLayout==null){
            throw NullPointerException("Please call addRebound first!")
        }
        this.headertView = headertView
        if (rootPostFinish){
            reboundLayout?.let {
                it?.addView(headertView,0)
                gListener = ViewTreeObserver.OnGlobalLayoutListener{
                    if (headertView?.height>0){
                        it?.setHeaderHeight(headertView?.measuredHeight)
                        headertView.viewTreeObserver.removeOnGlobalLayoutListener(gListener)
                    }
                }
                headertView.viewTreeObserver.addOnGlobalLayoutListener(gListener)
            }
        }
        return this
    }

    /**
     * 设置回弹头部
     * */
    fun setHeadBoundView(id: Int,layoutInflater:LayoutInflater):ReboundUtil{
        if (reboundLayout == null){
            throw NullPointerException("Please call addRebound first!")
        }
        this.headertView = layoutInflater.inflate(id,reboundLayout,false)
        if (rootPostFinish){
            reboundLayout.let {
                it?.addView(headertView,0)
                headertView!!.post {
                    it?.setHeaderHeight(headertView!!.height)
                }
            }
        }
        return this
    }

    /**
     * 设置回弹底部
     * */
    fun setBottomBoundView(bottomView: View):ReboundUtil{
        if (reboundLayout==null){
            throw NullPointerException("Please call addRebound first!")
        }
        this.bottomView = bottomView
        if (rootPostFinish){
            reboundLayout.let{
                it?.addView(bottomView,reboundLayout!!.childCount)
                gListener = ViewTreeObserver.OnGlobalLayoutListener{
                    if (bottomView?.height>0){
                        it?.setBottomHeight(bottomView?.height)
                        bottomView.viewTreeObserver.removeOnGlobalLayoutListener(gListener)
                    }
                }
                bottomView.viewTreeObserver.addOnGlobalLayoutListener(gListener)
            }
        }
        return this
    }

    /**
     * 设置回弹底部
     * */
    fun setBottomBoundView(id: Int,layoutInflater:LayoutInflater):ReboundUtil{
        if (reboundLayout==null){
            throw NullPointerException("Please call addRebound first!")
        }
        this.bottomView = layoutInflater.inflate(id,reboundLayout,false)
        if (rootPostFinish){
            reboundLayout.let {
                it?.addView(bottomView,reboundLayout!!.childCount)
                headertView!!.post {
                    it?.setHeaderHeight(headertView!!.height)
                }
            }
        }
        return this
    }

    /**
     * 不可滑动界面重构
     * */
    private fun addNormalRebound(activity: Activity){
        contentLayout = activity.window.decorView.findViewById(android.R.id.content)
        rootLayout = contentLayout?.getChildAt(0)
        reboundLayout = ReboundLayout(activity)
        reboundLayout!!.layoutParams = contentLayout?.layoutParams
            rootLayout?.post {
                rootHeight = rootLayout!!.height
                rootLayout?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,rootHeight)
                reboundLayout?.layoutParams!!.height = rootHeight
                contentLayout?.removeView(rootLayout)
                reboundLayout?.addView(rootLayout)
                contentLayout?.addView(reboundLayout)

                rootPostFinish =true

                if (headertView!=null){
                    setHeadBoundView(headertView!!)
                }

                if (bottomView!=null){
                    setBottomBoundView(bottomView!!)
                }
        }
    }

    /**
     * 可滑动界面重构
     * */
    private fun reSetSliDingRebound(activity: Activity,scrollView:View){

        contentLayout = scrollView.parent as ViewGroup
        rootLayout = scrollView
        reboundLayout = ReboundLayout(activity)

        var tartIndex = contentLayout!!.indexOfChild(scrollView)

        reboundLayout!!.layoutParams = rootLayout!!.layoutParams
        rootLayout?.post {
            rootHeight = rootLayout!!.height
            reboundLayout?.layoutParams!!.height = rootHeight
            contentLayout?.removeView(rootLayout)
            reboundLayout?.addView(rootLayout,LinearLayout.LayoutParams.MATCH_PARENT,rootHeight)
            contentLayout?.addView(reboundLayout,tartIndex)

            rootPostFinish =true

            if (headertView!=null){
                setHeadBoundView(headertView!!)
            }

            if (bottomView!=null){
                setBottomBoundView(bottomView!!)
            }
        }
    }

    /**
     * recyclerview添加回弹
     * */
    private fun addSliDingRebound(activity: Activity,recyclerView: RecyclerView){

        reSetSliDingRebound(activity,recyclerView)

        updateRecyclerviewLayoutChange(recyclerView)
    }

    private fun addSliDingRebound(activity: Activity,scrollView: ScrollView){
//        addNormalRebound(activity)

    }

    private fun addSliDingRebound(activity: Activity,nestedScrollView: NestedScrollView){
//        addNormalRebound(activity)
//        nestedScrollView.setOnScrollChangeListener {
//                v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
//            Log.e(TAG,"scrollX:$scrollX\t scrollY:$scrollY oldScrollX:$oldScrollX oldScrollY:$oldScrollY")
//        }
    }

    /**
     * 设置阻力因子
     * */
    fun setResistanceFactor(double: Double):ReboundUtil{
        reboundLayout?.setResistanceFactor(double)
        return this
    }

    /**
     * 更新滑动布局
     * */
    fun updateRecyclerviewLayoutChange(recyclerView: RecyclerView){
        if (recyclerView.adapter!=null){
            recyclerView.adapter!!.notifyDataSetChanged()
        }
        recyclerView.post {
            var iscanScroll = recyclerView.canScrollVertically(1)
            reboundLayout?.setIsStartCanScroll(iscanScroll)

            if (iscanScroll){
                reboundLayout?.setIsInterceptEvent(false)

                if (recyclerViewOnScrollListener!=null){
                    recyclerView.removeOnScrollListener(recyclerViewOnScrollListener!!)
                }
                recyclerViewOnScrollListener = object :RecyclerView.OnScrollListener(){
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if(!recyclerView.canScrollVertically(1)) {
                            reboundLayout?.setIsScrollBottom(true)
                            reboundLayout?.setIsInterceptEvent(true)
                        }else if(!recyclerView.canScrollVertically(-1)){
                            reboundLayout?.setIsScrollTop(true)
                            reboundLayout?.setIsInterceptEvent(true)
                        }else{
                            reboundLayout?.setIsScrollBottom(false)
                            reboundLayout?.setIsScrollBottom(false)
                            reboundLayout?.setIsInterceptEvent(false)
                        }
                        super.onScrolled(recyclerView, dx, dy)
                    }
                }

                if (!recyclerView.canScrollVertically(-1)){
                    reboundLayout?.setIsScrollTop(true)
                    reboundLayout?.setIsScrollBottom(false)
                    reboundLayout?.setIsInterceptEvent(true)
                }
                recyclerView.addOnScrollListener(recyclerViewOnScrollListener!!)
            }else{
                reboundLayout?.setIsScrollTop(true)
                reboundLayout?.setIsScrollBottom(true)
                reboundLayout?.setIsInterceptEvent(true)
            }

        }
    }

    /**
     * 设置回弹间隔
     * */
    fun setReboundDuration(duration:Int):ReboundUtil{
        this.reboundDuration = duration
        reboundLayout?.setReboundDuration(duration)
        return this
    }

    /**
     * 设置监听
     * */
    fun setOnReboundListener(listener: ReboundLayout.OnReboundListener):ReboundUtil{
        reboundLayout?.setOnReboundListener(listener)
        return this
    }

}