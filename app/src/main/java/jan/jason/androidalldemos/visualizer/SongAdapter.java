package jan.jason.androidalldemos.visualizer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jan.jason.androidalldemos.R;
import jan.jason.androidalldemos.utils.LogUtils;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2019/6/2 18:02
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<Song> songList;
    private long[] songListArray;

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView artwork;

        TextView title;

        TextView artist;

        LinearLayout itemRoot;

        public ViewHolder(View view) {
            super(view);
            this.itemRoot=view.findViewById(R.id.item_visualizer_root);
            this.artwork = view.findViewById(R.id.item_visualizer_artwork);
            this.title = view.findViewById(R.id.item_visualizer_title);
            this.artist = view.findViewById(R.id.item_visualizer_artist);

            this.itemRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mContext!=null){
                        Intent intent=new Intent(mContext,VisualizerMusicDetailActivity2.class);
                        intent.putExtra("position",getAdapterPosition());
                        LogUtils.d("这里的position="+getAdapterPosition());
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }

    public SongAdapter(Context context,ArrayList<Song> songList) {
        this.songList = songList;
        this.mContext=context;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.artwork.setImageBitmap(song.getArtwork());
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_visualizer_list,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
