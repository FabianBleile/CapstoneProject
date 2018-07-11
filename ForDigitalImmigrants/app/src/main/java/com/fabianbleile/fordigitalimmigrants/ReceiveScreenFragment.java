package com.fabianbleile.fordigitalimmigrants;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fabianbleile.fordigitalimmigrants.Adapter.ContactListRecyclerViewAdapter;
import com.fabianbleile.fordigitalimmigrants.data.Contact;
import com.fabianbleile.fordigitalimmigrants.data.ContactListViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReceiveScreenFragment extends Fragment implements View.OnLongClickListener {

    public static ContactListViewModel viewModel;
    private ContactListRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private CoordinatorLayout coordinatorLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReceiveScreenFragment() {
    }

    @SuppressWarnings("unused")
    public static ReceiveScreenFragment newInstance(int columnCount) {
        ReceiveScreenFragment fragment = new ReceiveScreenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receive_screen, container, false);


        Context context = view.getContext();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);

        recyclerViewAdapter =
                new ContactListRecyclerViewAdapter(
                        new ArrayList<Contact>(), this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(recyclerViewAdapter);

        viewModel = ViewModelProviders.of(this).get(ContactListViewModel.class);

        viewModel.getContactList().observe(ReceiveScreenFragment.this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(@Nullable List<Contact> contacts) {
                recyclerViewAdapter.notifyDataChange(contacts);
                recyclerView.smoothScrollToPosition(recyclerViewAdapter.getItemCount() + 1);
            }
        });

        return view;
    }

    @Override
    public boolean onLongClick(View view) {
        Toast.makeText(getContext(), "OnLongClick funktioniert", Toast.LENGTH_SHORT).show();

        createSettingsAlertDialog((Contact) view.getTag());
        return true;
    }

    public static void onFileIncome(Contact contact){
        viewModel.addItem(contact);
    }


    // Undo functionality
    public void onItemDeleted(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ContactListRecyclerViewAdapter.RecyclerViewHolder) {
            // get the removed item name to display it in snack bar
            List<Contact> contacts = viewModel.getContactList().getValue();
            final Contact contact = contacts.get(viewHolder.getAdapterPosition());
            String name = contact.getName();

            // backup of removed item for undo purpose
            final Contact deletedItem = contact;

            // remove the item from recycler view
            viewModel.deleteItem(contact);

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    viewModel.addItem(deletedItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    //creates AlertDialog for entering the information
    private void createSettingsAlertDialog(final Contact contact){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog_receive, null);
        dialogBuilder.setView(dialogView);

        TextView textView = (TextView) dialogView.findViewById(R.id.tv_delete_info);
        textView.setText(getResources().getText(R.string.tv_delete_info));

        dialogBuilder.setCancelable(true);
        dialogBuilder.setTitle(contact.getName());
        dialogBuilder.setPositiveButton(R.string.bt_cncl, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogBuilder.setNegativeButton(R.string.bt_ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                viewModel.deleteItem(contact);
            }
        });
        AlertDialog ad = dialogBuilder.create();
        ad.show();
    }
}
