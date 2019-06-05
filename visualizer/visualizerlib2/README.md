
### 频谱库 使用说明书

    1.针对Activity(全屏，已配置好)
      可直接跳转：
      //第二个参数，根据VisualizerManager中的visualizerDataType自行设置
      KeepToUtils.keepToVisualizerActivity(this,0);


    2.针对Fragment(参考TestFragment)
      在自己项目中的布局，替换成VisualizerActivity
      可以传递数据，可以不传（不传为0）
      //组装传递参数，详情查看VisualizerManager中的频谱类型
      Bundle bundle = new Bundle();
      bundle.putInt(Constants.FRAGMENT_ARGUMENTS_INDEX, 0);


    3.切换频谱
      调用方法：
      //下一个频谱
      VisualizerManager.getInstance().getControlVisualizer().nextVisualizer();

      //上一个频谱
      VisualizerManager.getInstance().getControlVisualizer().previousVisualizer();

      //指定频谱
      VisualizerManager.getInstance().getControlVisualizer().someVisualizer(type);

    4.全局Session设置
       规则：频谱页的Session，通过VisualizerManager.getInstance().getSessionId()拿到。
       所以：在自己App中，哪里Session发生变化了，可重设一次，即调用一次
              VisualizerManager.getInstance().setSessionId(xx);