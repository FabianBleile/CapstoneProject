package com.fabianbleile.fordigitalimmigrants.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fabianbleile.fordigitalimmigrants.R;
import com.fabianbleile.fordigitalimmigrants.data.Contact;

import java.util.List;

public class ContactListRecyclerViewAdapter extends RecyclerView.Adapter<ContactListRecyclerViewAdapter.RecyclerViewHolder> {

    private List<Contact> contactModelList;
    public static Contact mContact;
    private View.OnLongClickListener longClickListener;

    public ContactListRecyclerViewAdapter(List<Contact> contactModelList, View.OnLongClickListener longClickListener){
        this.contactModelList = contactModelList;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_itemcontact, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {

        mContact = contactModelList.get(position);
        holder.cardView.setOnLongClickListener(longClickListener);
        holder.nameTextView.setText(mContact.getName());
        holder.itemView.setTag(mContact);

    }



    @Override
    public int getItemCount() {
        return contactModelList.size();
    }



    public void notifyDataChange(List<Contact> borrowModelList) {

        this.contactModelList = borrowModelList;
        notifyDataSetChanged();

    }



    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private CardView cardView;

        public RelativeLayout viewBackground, viewForeground;



        RecyclerViewHolder(View view) {
            super(view);

            nameTextView = (TextView) view.findViewById(R.id.name);
            cardView = (CardView) view.findViewById(R.id.cardView);
            view.setTag(mContact);

        }

    }
}