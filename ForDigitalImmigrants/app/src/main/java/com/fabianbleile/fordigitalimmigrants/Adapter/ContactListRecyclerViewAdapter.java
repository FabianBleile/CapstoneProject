package com.fabianbleile.fordigitalimmigrants.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fabianbleile.fordigitalimmigrants.R;
import com.fabianbleile.fordigitalimmigrants.data.Contact;

import java.util.List;

public class ContactListRecyclerViewAdapter extends RecyclerView.Adapter<ContactListRecyclerViewAdapter.RecyclerViewHolder> {

    private List<Contact> contactModelList;
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

        Contact contact = contactModelList.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.phonenumberTextView.setText(contact.getPhonenumber());
        holder.emailTextView.setText(contact.getEmail());
        holder.birthdayTextView.setText(contact.getBirthday());
        holder.hometownTextView.setText(contact.getHometown());
        holder.instagramTextView.setText(contact.getInstagram());
        holder.facebookTextView.setText(contact.getFacebook());
        holder.snapchatTextView.setText(contact.getSnapchat());
        holder.twitterTextView.setText(contact.getTwitter());
        holder.locationTextView.setText(contact.getLocation());
        holder.itemView.setTag(contact);
        holder.itemView.setOnLongClickListener(longClickListener);

    }



    @Override

    public int getItemCount() {
        return contactModelList.size();
    }



    public void notifyDataChange(List<Contact> borrowModelList) {

        this.contactModelList = borrowModelList;
        notifyDataSetChanged();

    }



    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView phonenumberTextView;
        private TextView emailTextView;
        private TextView birthdayTextView;
        private TextView hometownTextView;
        private TextView instagramTextView;
        private TextView facebookTextView;
        private TextView snapchatTextView;
        private TextView twitterTextView;
        private TextView locationTextView;

        



        RecyclerViewHolder(View view) {

            super(view);

            nameTextView = (TextView) view.findViewById(R.id.name);
            phonenumberTextView = (TextView) view.findViewById(R.id.phonenumber);
            emailTextView = (TextView) view.findViewById(R.id.email);
            birthdayTextView = (TextView) view.findViewById(R.id.birthday);
            hometownTextView = (TextView) view.findViewById(R.id.hometown);
            instagramTextView = (TextView) view.findViewById(R.id.instagram);
            facebookTextView = (TextView) view.findViewById(R.id.facebook);
            snapchatTextView = (TextView) view.findViewById(R.id.snapchat);
            twitterTextView = (TextView) view.findViewById(R.id.twitter);
            locationTextView = (TextView) view.findViewById(R.id.location);

        }

    }
}