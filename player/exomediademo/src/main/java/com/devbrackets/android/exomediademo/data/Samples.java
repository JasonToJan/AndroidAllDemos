package com.devbrackets.android.exomediademo.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Samples {
    @NonNull
    private static final List<Sample> audioSamples;
    @NonNull
    private static final List<Sample> videoSamples;

    static {
        String audioImage = "https://ia902708.us.archive.org/3/items/count_monte_cristo_0711_librivox/Count_Monte_Cristo_1110.jpg?cnt=0";

        //Audio items
        audioSamples = new LinkedList<>();
        audioSamples.add(new Sample("幻听", "http://dl.stream.qqmusic.qq.com/M500003MUDkf30pS5B.mp3?vkey=065593401B57CBF4273E17BDA3672B565F392C5E5D301CB3347F646EFE350B5AAB0229CDA7CED8AB7BE54467ECCDED5B58839CB6E3506642&guid=5150825362&fromtag=1", audioImage));
        audioSamples.add(new Sample("素颜", "http://dl.stream.qqmusic.qq.com/M500004Gq0xE1YC8xp.mp3?vkey=33A75F1D2ECA17D60AB9ABEFCC19797D98762AAFC0874729F435878C4942610083F2B284BBB1A861D34A01740A1842D6CAEC15313F3921CB&guid=5150825362&fromtag=1", audioImage));
        audioSamples.add(new Sample("灰色头像", "http://antiserver.kuwo.cn/anti.s?useless=/resource/&format=mp3&rid=MUSIC_909773&response=res&type=convert_url&", audioImage));
        audioSamples.add(new Sample("有何不可", "http://antiserver.kuwo.cn/anti.s?useless=/resource/&format=mp3&rid=MUSIC_455880&response=res&type=convert_url&", audioImage));


        //Video items
        videoSamples = new ArrayList<>();
        videoSamples.add(new Sample("梦想努力超越", "https://mp4.vjshi.com/2019-04-09/5ff238878d99c8dfc82782ccf01233e9.mp4"));
        videoSamples.add(new Sample("红色党政人物", "https://mp4.vjshi.com/2019-04-15/77ac7124284745f4e7973a195e0eae5d.mp4"));
        videoSamples.add(new Sample("HLS - Sintel by Blender", "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"));
        videoSamples.add(new Sample("MKV - Android Screens", "http://storage.googleapis.com/exoplayer-test-media-1/mkv/android-screens-lavf-56.36.100-aac-avc-main-1280x720.mkv"));
        videoSamples.add(new Sample("MP4 (VP9) - Google Glass", "http://demos.webmproject.org/exoplayer/glass.mp4"));
        videoSamples.add(new Sample("MPEG DASH - Sintel by Blender", "https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd"));
        videoSamples.add(new Sample("MPEG DASH - Big Buck Bunny by Blender, Live", "https://wowzaec2demo.streamlock.net/live/bigbuckbunny/manifest_mpm4sav_mvtime.mpd"));
        videoSamples.add(new Sample("Smooth Stream - Caminandes: Llama Drama by Blender", "http://amssamples.streaming.mediaservices.windows.net/634cd01c-6822-4630-8444-8dd6279f94c6/CaminandesLlamaDrama4K.ism/manifest"));
        videoSamples.add(new Sample("Smooth Stream - Tears of Steel Teaser by Blender", "http://amssamples.streaming.mediaservices.windows.net/3d7eaff9-39fa-442f-81cc-f2ea7db1797e/TearsOfSteelTeaser.ism/manifest"));
        videoSamples.add(new Sample("WEBM - Big Buck Bunny", "http://dl1.webmfiles.org/big-buck-bunny_trailer.webm"));
        videoSamples.add(new Sample("WEBM - Elephants Dream", "http://dl1.webmfiles.org/elephants-dream.webm"));
    }

    @NonNull
    public static List<Sample> getAudioSamples() {
        return audioSamples;
    }

    @NonNull
    public static List<Sample> getVideoSamples() {
        return videoSamples;
    }

    /**
     * A container for the information associated with a
     * sample media item.
     */
    public static class Sample {
        @NonNull
        private String title;
        @NonNull
        private String mediaUrl;
        @Nullable
        private String artworkUrl;

        public Sample(@NonNull String title, @NonNull String mediaUrl) {
            this(title, mediaUrl, null);
        }

        public Sample(@NonNull String title, @NonNull String mediaUrl, @Nullable String artworkUrl) {
            this.title = title;
            this.mediaUrl = mediaUrl;
            this.artworkUrl = artworkUrl;
        }

        @NonNull
        public String getTitle() {
            return title;
        }

        @NonNull
        public String getMediaUrl() {
            return mediaUrl;
        }

        @Nullable
        public String getArtworkUrl() {
            return artworkUrl;
        }
    }
}
