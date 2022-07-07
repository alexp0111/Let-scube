package com.example.lbar.adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lbar.R;
import com.example.lbar.helpClasses.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.lbar.MainActivity.reference;

import org.w3c.dom.Text;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> mEvents;
    private View dialogView;
    private Context mContext;
    private FirebaseUser fUser;

    public EventAdapter(Context mContext, List<Event> mEvents, View dialogView) {
        this.mEvents = mEvents;
        this.mContext = mContext;
        this.dialogView = dialogView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.event_item, parent, false);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        return new EventAdapter.ViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Event event = mEvents.get(position);


        if (event.getEv_liked_users().contains(fUser.getUid())) {
            Log.d("ARBUZ", position + " ");
            holder.eventLikes.setChecked(true);
        }

        DatabaseReference ref1 = reference.child(event.getEv_author_id()).child("us_name");
        DatabaseReference ref2 = reference.child(event.getEv_author_id()).child("image");
        DatabaseReference evRef = FirebaseDatabase
                .getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
                .getReference("Events");

        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.authorName.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Glide.with(mContext).load(snapshot.getValue().toString()).into(holder.authorImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.eventHeader.setText(event.getEv_header());
        holder.eventText.setText(event.getEv_text());

        if (event.getEv_image().equals("none")) {
            holder.eventText.setPadding(0, 0, 0, 16);
            holder.eventImage.setVisibility(View.GONE);
            Log.d("picture bug", "marker");
        } else {
            holder.eventImage.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(event.getEv_image()).into(holder.eventImage);
            Log.d("picture existing", event.getEv_image() + "  " + position);
        }

        holder.authorImage.setOnClickListener(view -> {
            ImageView imageView = dialogView.findViewById(R.id.dialog_img_only);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);

            if (dialogView.getParent() != null) {
                ((ViewGroup) dialogView.getParent()).removeView(dialogView);
            }
            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Glide.with(mContext).load(snapshot.getValue().toString()).into(imageView);
                    builder.setView(dialogView);
                    builder.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });

        // Likes
        Log.d("EVENT_ADAPTER", event.getEv_likes() + " ");
        holder.eventNumLikes.setText(String.valueOf(event.getEv_liked_users().size()));

        holder.eventLikes.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            holder.eventLikes.setClickable(false);
            List<String> tmp_list = new ArrayList<>();
            evRef.child(event.getEv_id()).child("ev_liked_users").get()
                    .addOnCompleteListener(task1 -> {
                        // Get actual data
                        for (DataSnapshot snapshot : task1.getResult().getChildren()) {
                            String liked_id = snapshot.getValue().toString();
                            tmp_list.add(liked_id);
                        }
                    }).addOnCompleteListener(task2 -> {
                        // Refresh data
                        if (isChecked) {
                            // DB stuff
                            if (!tmp_list.contains(fUser.getUid())) tmp_list.add(fUser.getUid());

                            evRef.child(event.getEv_id()).child("ev_liked_users").setValue(tmp_list)
                                    .addOnCompleteListener(task12 -> {
                                        holder.eventNumLikes.setText(String.valueOf(tmp_list.size()));
                                        holder.eventLikes.setClickable(true);
                                    });
                        } else {
                            tmp_list.remove(fUser.getUid());

                            evRef.child(event.getEv_id()).child("ev_liked_users").setValue(tmp_list)
                                    .addOnCompleteListener(task1 -> {
                                        holder.eventNumLikes.setText(String.valueOf(tmp_list.size()));
                                        holder.eventLikes.setClickable(true);
                                    });
                        }
                    });
        });

        // Comments
        holder.eventNumComments.setText("in dev...");
        holder.eventComments
                .setOnClickListener(view ->
                        Snackbar.make(view, "Currently unavailable",
                                        BaseTransientBottomBar.LENGTH_SHORT).

                                show());
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

        public MaterialCheckBox eventLikes;
        public TextView eventNumLikes;
        public LinearLayout eventComments;
        public TextView eventNumComments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            authorName = itemView.findViewById(R.id.event_item_author_name);
            authorImage = itemView.findViewById(R.id.event_item_author_img);

            eventHeader = itemView.findViewById(R.id.event_item_header_txt);
            eventText = itemView.findViewById(R.id.event_item_text);
            eventImage = itemView.findViewById(R.id.event_item_image);


            eventLikes = itemView.findViewById(R.id.event_likes_cb);
            eventNumLikes = itemView.findViewById(R.id.event_likes_txt);

            eventComments = itemView.findViewById(R.id.event_comments_ll);
            eventNumComments = itemView.findViewById(R.id.event_comments_txt);
        }
    }
}
