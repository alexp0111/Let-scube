package com.example.lbar.adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lbar.R;
import com.example.lbar.fragments.mainMenuFragments.eventFragments.CommentsFragment;
import com.example.lbar.helpClasses.Comment;
import com.example.lbar.helpClasses.Event;
import com.example.lbar.helpClasses.Liker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
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
    private List<Liker> mLikers;
    private List<Comment> mComments;
    private View dialogView;
    private Context mContext;
    private FirebaseUser fUser;

    public EventAdapter(Context mContext, List<Event> mEvents, List<Liker> mLikers, List<Comment> mComments, View dialogView) {
        this.mEvents = mEvents;
        this.mLikers = mLikers;
        this.mComments = mComments;
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

        DatabaseReference ref1 = reference.child(event.getEv_author_id()).child("us_name");
        DatabaseReference ref2 = reference.child(event.getEv_author_id()).child("image");
        DatabaseReference likeRef = FirebaseDatabase
                .getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
                .getReference("Likes");

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

        // // Likes

        // number
        holder.eventNumLikes.setText(String.valueOf(numOfLikesInEvent(mLikers, event.getEv_id())));

        // checkbox preset
        if (likedByUser(mLikers, event.getEv_id(), fUser.getUid())) {
            holder.eventLikes.setChecked(true);
        }

        // checkbox listener
        holder.eventLikes.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            holder.eventLikes.setClickable(false);
            // Refresh data
            if (isChecked) {
                // DB stuff
                String key = likeRef.push().getKey();
                Liker liker = new Liker(key, fUser.getUid(), event.getEv_id());
                mLikers.add(liker);

                if (key != null)
                    likeRef.child(key).setValue(liker).addOnCompleteListener(task -> {
                        holder.eventNumLikes.setText(String.valueOf(numOfLikesInEvent(mLikers, event.getEv_id())));
                        holder.eventLikes.setClickable(true);
                    });
            } else {
                for (int i = 0; i < mLikers.size(); i++) {
                    if (mLikers.get(i).getLike_event_id().equals(event.getEv_id())
                            && mLikers.get(i).getLike_user_id().equals(fUser.getUid())) {
                        likeRef.child(mLikers.get(i).getLike_id()).removeValue();
                        mLikers.remove(i);
                        i--;
                    }
                }
                holder.eventNumLikes.setText(String.valueOf(numOfLikesInEvent(mLikers, event.getEv_id())));
                holder.eventLikes.setClickable(true);
            }
        });

        // Comments
        holder.eventNumComments.setText(String.valueOf(numOfCommentsInEvent(mComments, event.getEv_id())));
        holder.eventComments
                .setOnClickListener(view -> {
                    CommentsFragment fragment = new CommentsFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("header", event.getEv_header());
                    bundle.putString("text", event.getEv_text());
                    bundle.putString("ev_id", event.getEv_id());
                    bundle.putString("us_id", event.getEv_author_id());
                    bundle.putString("img", event.getEv_image());

                    fragment.setArguments(bundle);

                    try {
                        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.to_right, R.anim.from_right)
                                .replace(R.id.fragment_container,
                                        fragment).commit();
                    } catch (Exception D) {
                        Toast.makeText(mContext, R.string.sww, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int numOfCommentsInEvent(List<Comment> tmp_list, String ev_id) {
        if (tmp_list == null || tmp_list.size() == 0 || ev_id == null) return 0;
        int counter = 0;
        for (int i = 0; i < tmp_list.size(); i++){
            if (tmp_list.get(i).getComment_event_id().equals(ev_id)) counter++;
        }
        return counter;
    }

    private boolean likedByUser(List<Liker> tmp_list, String ev_id, String us_id) {
        if (tmp_list == null || tmp_list.size() == 0 || ev_id == null || us_id == null)
            return false;
        for (int i = 0; i < tmp_list.size(); i++) {
            if (tmp_list.get(i).getLike_event_id().equals(ev_id)
                    && tmp_list.get(i).getLike_user_id().equals(us_id)) return true;
        }
        return false;
    }

    private int numOfLikesInEvent(List<Liker> tmp_list, String ev_id) {
        if (tmp_list == null || tmp_list.size() == 0 || ev_id == null) return 0;
        int counter = 0;
        for (int i = 0; i < tmp_list.size(); i++)
            if (tmp_list.get(i).getLike_event_id().equals(ev_id)) counter++;
        return counter;
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public MaterialCardView cardView;

        public TextView authorName;
        public ImageView authorImage;
        public TextView eventHeader;
        public TextView eventText;
        public ImageView eventImage;

        public MaterialCheckBox eventLikes;
        public TextView eventNumLikes;
        public LinearLayout eventComments;
        public TextView eventNumComments;

        public TextView tmpAboba;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.event_mcv);

            authorName = itemView.findViewById(R.id.event_item_author_name);
            authorImage = itemView.findViewById(R.id.event_item_author_img);

            eventHeader = itemView.findViewById(R.id.event_item_header_txt);
            eventText = itemView.findViewById(R.id.event_item_text);
            eventImage = itemView.findViewById(R.id.event_item_image);


            eventLikes = itemView.findViewById(R.id.event_likes_cb);
            eventNumLikes = itemView.findViewById(R.id.event_likes_txt);

            eventComments = itemView.findViewById(R.id.event_comments_ll);
            eventNumComments = itemView.findViewById(R.id.event_comments_txt);

            tmpAboba = itemView.findViewById(R.id.tmp_text_aboba);
        }
    }
}
