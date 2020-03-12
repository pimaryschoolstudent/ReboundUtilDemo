# ReboundUtilDemo
安卓下拉回弹框架

优雅快速实现界面下拉回弹
  动态重置页面布局，增加自定义可回弹的布局，使用方便，不需做任何xml界面修改
  
  导入使用：

  项目根目录下的build.gradle添加
  
  allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  app目录下的build.gradle添加 
  
  implementation 'com.github.pimaryschoolstudent:ReboundUtilDemo:1.0.0'
  
  使用教程：
      不可滑动界面（界面不包括recyclerview，srollview等滑动元素）
      ReboundUtil.getUtil(this)
          .addRebound(this,ReboundUtil.NON_SLIDING)  //无滑动元素添加
          .setResistanceFactor(0.3)  //设置阻力因子，值越小越难滑动
          .setHeadBoundView(R.layout.header_view, LayoutInflater.from(this)) //设置头部View
          .setBottomBoundView(bottomView) //设置底部View
          .setBoundType(ReboundUtil.HEADER_REBOUND_LIMIT,ReboundUtil.BOTTOM_REBOUND_NORMAL) //设置滑动方式  limit：滑动距离不超过组件距离  normal：滑动距离可超过组件距离


      
      recyclerview添加回弹效果
      ReboundUtil.getUtil(this).addRebound(this,ReboundUtil.SLIDING,recyclerView)
        .setResistanceFactor(0.3)  //设置阻力因子，值越小越难滑动
        .setHeadBoundView(R.layout.header_view, LayoutInflater.from(this)) //设置头部View
        .setBottomBoundView(bottomView) //设置底部View
        .setBoundType(ReboundUtil.HEADER_REBOUND_LIMIT,ReboundUtil.BOTTOM_REBOUND_NORMAL) //设置滑动方式  limit：滑动距离不超过组件距离  normal：滑动距离可超过组件距离
       
       注意：需要在元素发生变化时更新回弹监听
            data.add("${data.size+1}")
            adapter.notifyDataSetChanged()
            ReboundUtil.getUtil(this).updateRecyclerviewLayoutChange(recyclerView) //更新监听
