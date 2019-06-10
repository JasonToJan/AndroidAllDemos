
### 频谱库 使用说明书
    1.简单频谱页（全屏Activity）
        KeepToUtils.keepToSimpleVisualizerActivity(activity,0);
        直接跳转到VisualizerSimpleActivity
        集成了菜单（更换图片以及修改频谱颜色功能，也可以隐藏）

        1.1 全局Session配置。
                    如果不设置，默认为0；
                    Session可能会变化的地方，通过 VisualizerManager.getInstance().setSessionId(0); 设置

        1.2 常用API
              切换频谱
              调用方法：
              //下一个频谱
              VisualizerManager.getInstance().getControlVisualizer().nextVisualizer();

              //上一个频谱
              VisualizerManager.getInstance().getControlVisualizer().previousVisualizer();

              //指定频谱
              VisualizerManager.getInstance().getControlVisualizer().someVisualizer(type);

              获取当前频谱菜单项
              VisualizerManager.getInstance().getVisualizerMenu();

              改变当前频谱背景图片
              VisualizerManager.getInstance().getVisualizerMenu().changeImageUri(selectedUri);

              改变当前频谱颜色
              VisualizerManager.getInstance().getVisualizerMenu().changeColor();

        1.3 设置显示哪几个频谱，以及频谱顺序
            直接修改VisualizerManager中的visualizerDataType 中数组顺序即可。

    2.简单频谱片段（Fragment）
        KeepToUtils.keepToVisualizerFragment(activity,0);
        参考VisualizerFragment(可直接跳转到TestFragmentActivity)
        集成了菜单（更换图片以及修改频谱颜色功能，也可以隐藏）

        2.1 全局Session配置。
            如果不设置，默认为0；
            Session可能会变化的地方，通过 VisualizerManager.getInstance().setSessionId(0); 设置

        2.2 常用API
              切换频谱
              调用方法：
              //下一个频谱
              VisualizerManager.getInstance().getControlVisualizer().nextVisualizer();

              //上一个频谱
              VisualizerManager.getInstance().getControlVisualizer().previousVisualizer();

              //指定频谱
              VisualizerManager.getInstance().getControlVisualizer().someVisualizer(type);

              获取当前频谱菜单项
              VisualizerManager.getInstance().getVisualizerMenu();

              改变当前频谱背景图片
              VisualizerManager.getInstance().getVisualizerMenu().changeImageUri(selectedUri);

              改变当前频谱颜色
              VisualizerManager.getInstance().getVisualizerMenu().changeColor();

         2.3 设置显示哪几个频谱，以及频谱顺序
             直接修改VisualizerManager中的visualizerDataType 中数组顺序即可。

    3.复杂频谱页（集成所有功能）
        KeepToUtils.keepToVisualizerActivity(activity,0);
        直接跳转到VisualizerActivity

        3.1.在自己的音乐服务类 或者 某个Activity中 实现接口 MusicVisualizerInter

              //主要实现如下方法

              /**
               * 下一首
               */
              void vi_next();

              /**
               * 上一首
               */
              void vi_prev();

              /**
               * 是否正在播放
               */
              boolean vi_isPlaying();

              /**
               * 播放或暂停
               */
              void vi_playorpause();

              /**
               * 获取音乐SessionId
               * @return
               */
              int vi_getSessionId();

              /**
               * 艺术家
               */
              String vi_artist();

              /**
               * 标题
               */
              String vi_title();

              /**
               * 服务中 歌曲信息发生变化后发送的广播字符串，上一首，下一首这种，需要更新艺术家和标题
               * @return
               */
              String vi_metachange();

              /**
               * 服务中 歌曲播放状态改变后发送的广播字符串，即播放或暂停这种，需要更新播放图标
               * @return
               */
              String vi_playstatechange();

        3.2.继续在自己音乐服务类 或者 Activity的onCreate中 调用如下函数进行初始化。

            VisualizerManager.getInstance().setMusicVisualizerInter(this);

        3.3.在需要的地方，进行跳转。

           方法1： 跳转到默认样式
                    KeepToVisualizerUtils.keepToDefaultVisualizerType(context);

           方法2： 跳转到某一个样式
                    KeepToVisualizerUtils.keepToVisualizerType(activity,type);

           详情参见 KeepToVisualizerUtils.

        3.4.具体UI上的修改，如按钮样式，字体大小，显示上一首图标等，自行修改布局以及ActivityVisualizer即可。