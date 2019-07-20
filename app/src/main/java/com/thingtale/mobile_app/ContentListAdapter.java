package com.thingtale.mobile_app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thingtale.mobile_app.content.ContentData;

import java.util.List;

import static java.lang.Math.min;

public class ContentListAdapter extends RecyclerView.Adapter<ContentListAdapter.ContentHolder> {
    private List<ContentData> contentList;
    private ContentAdapterListener listener;
    private Context context;

    public ContentListAdapter(List<ContentData> contentList, ContentAdapterListener listener, Context context) {
        this.contentList = contentList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LinearLayout viewInflate = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.content_recycler, parent, false);
        final ContentHolder cHolder = new ContentHolder(viewInflate);

        cHolder.layout.findViewById(R.id.img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int itemPosition = (Integer) v.getTag();
                final ContentData c = contentList.get(itemPosition);

                final Dialog builder = new Dialog(v.getContext());
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                final ImageView imageView = new ImageView(v.getContext());
                builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity) v.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;
                final int size = min(height, width) * 100 / 50;
                imageView.setImageBitmap(c.getBitmap(size));

                builder.show();
            }
        });

        return cHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContentHolder holder, int position) {
        // set tag for click listener
        holder.layout.findViewById(R.id.img).setTag(position);

        ((TextView) holder.layout.findViewById(R.id.txt_isbn)).setText(contentList.get(position).getIsbn());
        ((TextView) holder.layout.findViewById(R.id.txt_book_name)).setText(contentList.get(position).getBookName());
        ((TextView) holder.layout.findViewById(R.id.txt_author)).setText(contentList.get(position).getAuthor());
        ((TextView) holder.layout.findViewById(R.id.txt_language)).setText(contentList.get(position).getLanguage());
        ((TextView) holder.layout.findViewById(R.id.txt_audio_file)).setText(contentList.get(position).getAudioFile());
        ((TextView) holder.layout.findViewById(R.id.txt_page_num)).setText(Integer.toString(contentList.get(position).getPageNum()));
        ((TextView) holder.layout.findViewById(R.id.txt_level)).setText(Integer.toString(contentList.get(position).getLevel()));
    }

    public void setContentList(List<ContentData> contentList) {
        this.contentList = contentList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public interface ContentAdapterListener {
        void onContentSelected(ContentData content);
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
