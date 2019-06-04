
### 频谱库 使用说明书

    1.在自己的音乐服务类 或者 某个Activity中 实现接口 MusicVisualizerInter

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

    2.继续在自己音乐服务类 或者 Activity的onCreate中 调用如下函数进行初始化。

        VisualizerManager.getInstance().setMusicVisualizerInter(this);

    3.在需要的地方，进行跳转。

       方法1： 跳转到默认样式
                KeepToVisualizerUtils.keepToDefaultVisualizerType(context);

       方法2： 跳转到某一个样式
                KeepToVisualizerUtils.keepToVisualizerType(activity,type);

       详情参见 KeepToVisualizerUtils.

    4.具体UI上的修改，如按钮样式，字体大小，显示上一首图标等，自行修改布局以及ActivityVisualizer即可。