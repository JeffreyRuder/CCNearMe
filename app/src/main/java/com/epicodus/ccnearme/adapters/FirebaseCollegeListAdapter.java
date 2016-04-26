package com.epicodus.ccnearme.adapters;

import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.epicodus.ccnearme.CollegeApplication;
import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;
import com.epicodus.ccnearme.util.FirebaseRecyclerAdapter;
import com.epicodus.ccnearme.util.ItemTouchHelperAdapter;
import com.epicodus.ccnearme.util.OnStartDragListener;
import com.firebase.client.Firebase;
import com.firebase.client.Query;

import java.util.Collections;

/**
 * Created by Jeffrey on 4/1/2016.
 */
public class FirebaseCollegeListAdapter extends FirebaseRecyclerAdapter<CollegeRecyclerViewHolder, College> implements ItemTouchHelperAdapter {

    private final OnStartDragListener mDragStartListener;

    public FirebaseCollegeListAdapter(Query query, Class<College> itemClass, OnStartDragListener listener) {
        super(query, itemClass);
        mDragStartListener = listener;
    }

    @Override
    public CollegeRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.college_list_item_drag, parent, false);
        return new CollegeRecyclerViewHolder(view, getItems());
    }

    @Override
    public void onBindViewHolder(final CollegeRecyclerViewHolder holder, int position) {
        holder.bindCollege(getItem(position));
        holder.mDragIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(getItems(), fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        Firebase ref = CollegeApplication.getAppInstance().getFirebaseRef();
        Firebase college = ref.child("savedcolleges/" + ref.getAuth().getUid() + "/" + getItem(position).getId());
        college.removeValue();
    }

    @Override
    public int getItemCount() {
        return getItems().size();
    }

    @Override
    protected void itemAdded(College item, String key, int position) {

    }

    @Override
    protected void itemChanged(College oldItem, College newItem, String key, int position) {

    }

    @Override
    protected void itemRemoved(College item, String key, int position) {

    }

    @Override
    protected void itemMoved(College item, String key, int oldPosition, int newPosition) {

    }
}
