package com.thingtale.mobile_app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thingtale.mobile_app.content.QRCodeData;

import java.util.List;

public class ContentListAdapter extends RecyclerView.Adapter<ContentListAdapter.ContentHolder> {
    private List<QRCodeData> contentList;
    private ContentAdapterListener listener;
    private Context context;

    public ContentListAdapter(List<QRCodeData> contentList, ContentAdapterListener listener, Context context) {
        this.contentList = contentList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout viewInflate = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.content_recycler, parent, false);
        ContentHolder cHolder = new ContentHolder(viewInflate);
        return cHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContentHolder holder, int position) {
        ((TextView) holder.layout.findViewById(R.id.txt_isbn)).setText(contentList.get(position).getIsbn());
        ((TextView) holder.layout.findViewById(R.id.txt_book_name)).setText(contentList.get(position).getBookName());
        ((TextView) holder.layout.findViewById(R.id.txt_author)).setText(contentList.get(position).getAuthor());
        ((TextView) holder.layout.findViewById(R.id.txt_language)).setText(contentList.get(position).getLanguage());
        ((TextView) holder.layout.findViewById(R.id.txt_audio_file)).setText(contentList.get(position).getAudioFile());
        ((TextView) holder.layout.findViewById(R.id.txt_page_num)).setText(Integer.toString(contentList.get(position).getPageNum()));
        ((TextView) holder.layout.findViewById(R.id.txt_level)).setText(Integer.toString(contentList.get(position).getLevel()));
    }

    public void setContentList(List<QRCodeData> contentList) {
        this.contentList = contentList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public interface ContentAdapterListener {
        void onContentSelected(QRCodeData content);
    }

    public class ContentHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;

        public ContentHolder(LinearLayout l) {
            super(l);
            layout = l;

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onContentSelected(contentList.get(getAdapterPosition()));
                }
            });
        }
    }
}
