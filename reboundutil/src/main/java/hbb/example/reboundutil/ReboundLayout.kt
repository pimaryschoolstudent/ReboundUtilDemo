package hbb.example.reboundutil


import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.Scroller


/**
 * @author HuangJiaHeng
 * @date 2020/2/27.
 */
class ReboundLayout : LinearLayout {

    /**
     * 滑动辅助
     * */
    private var mScroller: Scroller
    /**
     * 默认阻力因子
     * */
    private var ResistanceFactor = 0.4
    /**
     * 开始滑动Y轴位置
     * */
    private var startY = 0
    private var startScollY = 0

    /**
     * 头部View高度
     * */
    private var headerHeight:Int =0
    private var bottomHeight:Int =0

    /**
     * 是否拦截滑动事件
     * 是否滑动到顶
     * 是否滑动到底
     * */
    private var isInterceptEvent:Boolean = false
    private var isScrollTop:Boolean = false
    private var isScrollBottom:Boolean = false

    /**
     * recyclerview是否可以滑动
     * */
    private var isStartCanScroll:Boolean = false

    /**
     * 滑动模式
     * */
    private var headerBoundType = ReboundUtil.HEADER_REBOUND_NORMAL
    private var bottomBoundType = ReboundUtil.BOTTOM_REBOUND_NORMAL

    constructor(context: Context, attributeSet: AttributeSet?=null):super(context,attributeSet){
        mScroller = Scroller(context)
        orientation = VERTICAL
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!isInterceptEvent){
            return super.onInterceptTouchEvent(ev)
        }else{
            when(ev?.action){
                MotionEvent.ACTION_MOVE->{
                    var endY = ev.y.toInt()
                    if (isScrollTop){
                        if (startY-endY<=0){
                            return true
                        } else{
                            if (isStartCanScroll){
                                this.isScrollBottom = false
                                this.isScrollTop = false
                                this.isInterceptEvent = false
                            }
                        }
                    }
                    if (isScrollBottom){
                        if (startY-endY>=0){
                            return true
                        } else{
                            if(isStartCanScroll){
                                this.isScrollBottom = false
                                this.isScrollTop = false
                                this.isInterceptEvent = false
                            }
                        }
                    }
                }
                MotionEvent.ACTION_DOWN->{
                    startY = ev.y.toInt()
                }
                MotionEvent.ACTION_UP->{}
            }
            return super.onInterceptTouchEvent(ev)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event!!.action){
            MotionEvent.ACTION_DOWN ->{
                startY = event.y.toInt()
            }
            MotionEvent.ACTION_UP ->{
                mScroller.startScroll(0,scrollY,0,-(scrollY-startScollY),1000)
            }
            MotionEvent.ACTION_MOVE->{
                if (!mScroller.isFinished){
                    mScroller.abortAnimation()
                }
                var endY = event.y.toInt()

                if (isStartCanScroll){
                    if (isScrollTop && (endY - startY<0)){
                        scrollTo(0,headerHeight)
                        return true
                    }else if (isScrollBottom && (endY-startY>0)){
                        scrollTo(0,headerHeight)
                        return true
                    }
                }

                scrollTo(0,headerHeight+((startY-endY)*ResistanceFactor).toInt())

                if (headerBoundType == ReboundUtil.HEADER_REBOUND_LIMIT){
                    if (startY<endY){
                        if (scrollY<=0){
                            scrollTo(0,0)
                        }
                    }
                }

                if (bottomBoundType == ReboundUtil.BOTTOM_REBOUND_LIMIT){
                    if (startY>endY){
                        if (scrollY >=bottomHeight+headerHeight){
                            scrollTo(0,bottomHeight+headerHeight)
                        }
                    }
                }

            }
        }
        postInvalidate()
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.currY)
            postInvalidate()
        }
    }

    /**
     * 设置阻力因子
     * 越小越难滑动
     */
    fun setResistanceFactor(f: Double){
        var tag = f
        if (tag<0){
            tag = 0.01
        }else if (tag>1){
            tag =1.0
        }
        ResistanceFactor = tag
    }

    /**
     * 设置头部View高度
     * 添加headerView后一定要设置，防止错乱
     * 默认值为0
     * */
    fun setHeaderHeight(headerHeight:Int){
        this.headerHeight = headerHeight
        startScollY = headerHeight
        scrollTo(0,headerHeight)
        postInvalidate()
    }

    /**
     * 设置底部View高度
     * 默认值为0
     * */
    fun setBottomHeight(bottomHeight:Int){
        this.bottomHeight = bottomHeight
        startScollY = headerHeight
        scrollTo(0,headerHeight)
        postInvalidate()
    }

    /**
     * 滑动界面拦截事件开关
     * */
    fun setIsInterceptEvent(b:Boolean){
        this.isInterceptEvent = b
    }

    fun setIsScrollTop(b:Boolean){
        this.isScrollTop = b
    }

    fun setIsScrollBottom(b:Boolean){
        this.isScrollBottom = b
    }

    fun setIsStartCanScroll(b:Boolean){
        this.isStartCanScroll = b
    }

    /**
     * 设置回弹动作类别
     * NORMAL  滑动距离不限制
     * LIMIT   滑动距离不超过头（底）部组件
     * */
    fun setBoundType(headerType:Int,bottomType:Int){
        headerBoundType = if (headerType == ReboundUtil.HEADER_REBOUND_LIMIT){
            ReboundUtil.HEADER_REBOUND_LIMIT
        }else{
            ReboundUtil.HEADER_REBOUND_NORMAL
        }

        bottomBoundType = if(bottomType == ReboundUtil.BOTTOM_REBOUND_LIMIT){
            ReboundUtil.BOTTOM_REBOUND_LIMIT
        }else{
            ReboundUtil.BOTTOM_REBOUND_NORMAL
        }
    }

}
