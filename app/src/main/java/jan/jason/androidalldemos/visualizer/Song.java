package jan.jason.androidalldemos.visualizer;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2019/6/2 17:58
 */
public class Song implements Parcelable {

    private String title;
    private String artist;
    private String path;
    private long duration;
    private Bitmap artwork;

    public Song(String title, String artist, String path, long duration, Bitmap artwork) {
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.duration = duration;
        this.artwork = artwork;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Bitmap getArtwork() {
        return artwork;
    }

    public void setArtwork(Bitmap artwork) {
        this.artwork = artwork;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.artist);
        dest.writeString(this.path);
        dest.writeLong(this.duration);
        dest.writeParcelable(this.artwork, flags);
    }

    protected Song(Parcel in) {
        this.title = in.readString();
        this.artist = in.readString();
        this.path = in.readString();
        this.duration = in.readLong();
        this.artwork = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
