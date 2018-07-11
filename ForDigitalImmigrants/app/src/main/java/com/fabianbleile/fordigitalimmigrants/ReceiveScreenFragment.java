package com.fabianbleile.fordigitalimmigrants;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fabianbleile.fordigitalimmigrants.Adapter.ContactListRecyclerViewAdapter;
import com.fabianbleile.fordigitalimmigrants.data.Contact;
import com.fabianbleile.fordigitalimmigrants.data.ContactListViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReceiveScreenFragment extends Fragment implements View.OnLongClickListener {

    public static ContactListViewModel viewModel;
    private ContactListRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;

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

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itemcontact_list, container, false);


        Context context = view.getContext();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

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
        Contact contactModel = (Contact) view.getTag();
        viewModel.deleteItem(contactModel);

        return true;
    }

    public static void onFileIncome(Contact contact){
        viewModel.addItem(contact);
    }
}
