package com.coocent.visualizerlib;

/**
 * Description: 音乐服务中的频谱接口
 *   一般在音乐服务中实现该接口
 * *
 * Creator: Wang
 * Date: 2019/6/4 20:04
 */
public interface MusicVisualizerInter {

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
     * 服务中 歌曲信息发生变化，上一首，下一首这种，需要更新艺术家和标题
     * @return
     */
    String vi_metachange();

    /**
     * 服务中 歌曲播放状态改变，即播放或暂停，需要更新播放图标
     * @return
     */
    String vi_playstatechange();
}
