package com.fabianbleile.fordigitalimmigrants;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fabianbleile.fordigitalimmigrants.Adapter.ContactListRecyclerViewAdapter;
import com.fabianbleile.fordigitalimmigrants.data.Contact;
import com.fabianbleile.fordigitalimmigrants.data.ContactListViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReceiveScreenFragment extends Fragment implements View.OnLongClickListener, View.OnClickListener {

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
                        new ArrayList<Contact>(), this, this);

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

    public static void onFileIncome(Contact contact){
        viewModel.addItem(contact);
    }


    // Undo functionality
    public void onItemDeleted(Contact contact) {
            // backup of removed item for undo purpose
            final Contact deletedItem = contact;

            // remove the item from recycler view
            viewModel.deleteItem(contact);

            /*
            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, contact.getName() + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    viewModel.addItem(deletedItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
             */
    }

    //creates AlertDialog for safe delete
    public void createDeleteAlertDialog(final Contact contact) {
        new AlertDialog.Builder(getContext())
                .setTitle(getActivity().getResources().getString(R.string.message_delete_info) + " " + contact.getName())
                .setCancelable(true)
                .setPositiveButton(R.string.bt_cncl, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.bt_ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        onItemDeleted(contact);
                    }
                }).show();
    }

    @Override
    public boolean onLongClick(View view) {
        Toast.makeText(getContext(), "OnLongClick funktioniert", Toast.LENGTH_SHORT).show();

        createDeleteAlertDialog((Contact) view.getTag());
        return true;
    }

    @Override
    public void onClick(View view) {
        Contact selectedContact = (Contact) view.getTag();
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra("selectedContact", (Parcelable) selectedContact);

        startActivity(intent);
    }
}
