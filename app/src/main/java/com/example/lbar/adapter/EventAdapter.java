package com.example.lbar.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lbar.R;
import com.example.lbar.helpClasses.Event;
import com.example.lbar.helpClasses.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.example.lbar.MainActivity.reference;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> mEvents;
    private Context mContext;

    public EventAdapter(Context mContext, List<Event> mEvents) {
        this.mEvents = mEvents;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.event_item, parent, false);
        return new EventAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Event event = mEvents.get(position);

        DatabaseReference ref1 = reference.child(event.getEv_author_id()).child("us_name");
        DatabaseReference ref2 = reference.child(event.getEv_author_id()).child("image");

        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.authorName.setText(snapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Glide.with(mContext).load(snapshot.getValue().toString()).into(holder.authorImage);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        holder.eventHeader.setText(event.getEv_header());
        holder.eventText.setText(event.getEv_text());
        if (event.getEv_image().equals("none")){
            holder.eventText.setPadding(0, 0, 0, 16);
            holder.eventImage.setVisibility(View.GONE);
            Log.d("picture bug", "marker");
        } else {
            holder.eventImage.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(event.getEv_image()).into(holder.eventImage);
            Log.d("picture existing", event.getEv_image() + "  " + position);
        }

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView authorName;
        public ImageView authorImage;
        public TextView eventHeader;
        public TextView eventText;
        public ImageView eventImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            authorName = itemView.findViewById(R.id.event_item_author_name);
            authorImage = itemView.findViewById(R.id.event_item_author_img);

            eventHeader = itemView.findViewById(R.id.event_item_header_txt);
            eventText = itemView.findViewById(R.id.event_item_text);
            eventImage = itemView.findViewById(R.id.event_item_image);
        }
    }
}
