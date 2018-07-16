package com.fabianbleile.fordigitalimmigrants.Adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fabianbleile.fordigitalimmigrants.R;
import com.fabianbleile.fordigitalimmigrants.data.Contact;

import java.util.List;

public class ContactListRecyclerViewAdapter extends RecyclerView.Adapter<ContactListRecyclerViewAdapter.RecyclerViewHolder> {

    private List<Contact> contactModelList;
    public static Contact mContact;
    private View.OnLongClickListener onLongClickListener;
    private View.OnClickListener onClickListener;

    public ContactListRecyclerViewAdapter(List<Contact> contactModelList, View.OnLongClickListener onLongClickListener, View.OnClickListener onClickListener){
        this.contactModelList = contactModelList;
        this.onLongClickListener = onLongClickListener;
        this.onClickListener = onClickListener;
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
        holder.imageView.setImageResource(iconColors[mContact.getCid() % 7]);
        holder.constraintLayout.setOnClickListener(onClickListener);
        holder.constraintLayout.setOnLongClickListener(onLongClickListener);
        holder.constraintLayout.setTag(mContact);
        holder.nameTextView.setText(mContact.getName());
        holder.captitalLetterTextView.setText(mContact.getName().substring(0,1));
        holder.mView.setContentDescription(holder.captitalLetterTextView.getText());

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
        private ImageView imageView;
        private TextView captitalLetterTextView;
        private ConstraintLayout constraintLayout;
        private View mView;

        public RelativeLayout viewBackground, viewForeground;



        RecyclerViewHolder(View view) {
            super(view);

            nameTextView = (TextView) view.findViewById(R.id.name);
            imageView = (ImageView) view.findViewById(R.id.iv_contact_item);
            captitalLetterTextView = (TextView) view.findViewById(R.id.tv_capital_icon);
            constraintLayout = (ConstraintLayout) view.findViewById(R.id.constraintLayout);

            mView = view;

        }

    }

    private static final int[] iconColors = {
            R.drawable.ic_circle_primary,
            R.drawable.ic_circle_500_1,
            R.drawable.ic_circle_500_5,
            R.drawable.ic_circle_500_6,
            R.drawable.ic_circle_500_2,
            R.drawable.ic_circle_500_3,
            R.drawable.ic_circle_500_7
    };
}