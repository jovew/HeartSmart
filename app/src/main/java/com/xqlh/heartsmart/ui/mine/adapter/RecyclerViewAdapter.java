package com.xqlh.heartsmart.ui.mine.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.xqlh.heartsmart.R;
import com.xqlh.heartsmart.bean.MusicInfo;
import com.xqlh.heartsmart.service.MusicPlayerService;
import com.xqlh.heartsmart.sqliteHelp.DBManager;
import com.xqlh.heartsmart.utils.Constants;
import com.xqlh.heartsmart.utils.CustomAttrValueUtil;
import com.xqlh.heartsmart.utils.MyMusicUtil;

import java.util.List;

/**
 * Created by lijunyan on 2016/12/20.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
        implements SectionIndexer {

    private static final String TAG = RecyclerViewAdapter.class.getName();
    private List<MusicInfo> musicInfoList;
    private Context context;
    private DBManager dbManager;
    private OnItemClickListener onItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View swipeContent;
        LinearLayout contentLl;
        TextView musicIndex;
        TextView musicName;
        TextView musicSinger;
        TextView letterIndex;
        ImageView menuIv;
        Button deleteBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            this.swipeContent = (View) itemView.findViewById(R.id.swipemenu_layout);
            this.contentLl = (LinearLayout) itemView.findViewById(R.id.local_music_item_ll);
            this.musicName = (TextView) itemView.findViewById(R.id.local_music_name);
            this.musicIndex = (TextView) itemView.findViewById(R.id.local_index);
            this.musicSinger = (TextView) itemView.findViewById(R.id.local_music_singer);
            this.letterIndex = (TextView) itemView.findViewById(R.id.indext_head_tv);
            this.menuIv = (ImageView) itemView.findViewById(R.id.local_music_item_never_menu);
            this.deleteBtn = (Button) itemView.findViewById(R.id.swip_delete_menu_btn);
        }
    }

    public RecyclerViewAdapter(Context context, List<MusicInfo> musicInfoList) {
        this.context = context;
        this.musicInfoList = musicInfoList;
        this.dbManager = DBManager.getInstance(context);
    }


    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的item的位置
     */
    public int getPositionForSection(int section) {
        Log.i(TAG, "getPositionForSection: section = " + section);
        for (int i = 0; i < getItemCount(); i++) {
            char firstChar = musicInfoList.get(i).getFirstLetter().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    @Override
    public int getSectionForPosition(int position) {
        return musicInfoList.get(position).getFirstLetter().charAt(0);
    }

    @Override
    public int getItemCount() {
        return musicInfoList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.local_music_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MusicInfo musicInfo = musicInfoList.get(position);
        holder.musicName.setText(musicInfo.getName());
        holder.musicIndex.setText("" + (position + 1));
        holder.musicSinger.setText(musicInfo.getSinger());

        int appbg = CustomAttrValueUtil.getAttrColorValue(R.attr.colorAccent, 0xFFFA7298, context);
        int defaultTvColor = CustomAttrValueUtil.getAttrColorValue(R.attr.text_color, R.color.grey700, context);

        if (musicInfo.getId() == MyMusicUtil.getIntShared(Constants.KEY_ID)) {
            holder.musicName.setTextColor(appbg);
            holder.musicIndex.setTextColor(appbg);
            holder.musicSinger.setTextColor(appbg);
        } else {
            holder.musicName.setTextColor(defaultTvColor);
            holder.musicIndex.setTextColor(context.getResources().getColor(R.color.grey700));
            holder.musicSinger.setTextColor(context.getResources().getColor(R.color.grey700));
        }
        int section = getSectionForPosition(position);
        int firstPosition = getPositionForSection(section);
        if (firstPosition == position) {
            holder.letterIndex.setVisibility(View.VISIBLE);
            holder.letterIndex.setText("" + musicInfo.getFirstLetter());
        } else {
            holder.letterIndex.setVisibility(View.GONE);
        }
        //点击播放列表播放歌曲
        holder.contentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("lz", "onClick: 播放 " + musicInfo.getName());
                String path = dbManager.getMusicPath(musicInfo.getId());
                Log.i("lz", "播放的路径" + path);

                Intent intent = new Intent(MusicPlayerService.PLAYER_MANAGER_ACTION);
                intent.putExtra(Constants.COMMAND, Constants.COMMAND_PLAY);
                intent.putExtra(Constants.KEY_PATH, path);
                context.sendBroadcast(intent);

                MyMusicUtil.setShared(Constants.KEY_ID, musicInfo.getId());
                notifyDataSetChanged();
                if (onItemClickListener != null)
                    onItemClickListener.onContentClick(position);
            }
        });

        holder.menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onOpenMenuClick(position);
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onDeleteMenuClick(holder.swipeContent, holder.getAdapterPosition());
            }
        });
    }

    public void updateMusicInfoList(List<MusicInfo> musicInfoList) {
        this.musicInfoList.clear();
        this.musicInfoList.addAll(musicInfoList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onOpenMenuClick(int position);

        void onDeleteMenuClick(View content, int position);

        void onContentClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
