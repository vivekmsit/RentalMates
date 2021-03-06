package com.example.vivek.rentalmates.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.adapters.ChatsRecyclerViewAdapter;
import com.example.vivek.rentalmates.backend.mainApi.model.Chat;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.tasks.GetChatListAsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChatsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ChatsFragment_Debug";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noChatsTextView;
    private AppData appData;
    private Context context;
    private ChatsRecyclerViewAdapter chatsRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        //Initialize RecyclerView
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.chatListRecyclerView);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        chatsRecyclerViewAdapter = new ChatsRecyclerViewAdapter(context, getFragmentManager());
        recyclerView.setAdapter(chatsRecyclerViewAdapter);

        //Initialize SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeListChats);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.white);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue, R.color.purple);
        swipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        noChatsTextView = (TextView) view.findViewById(R.id.noChatsTextView);

        updateView();

        return view;
    }

    void updateView() {
        if (appData.getChats().values().size() == 0) {
            noChatsTextView.setVisibility(View.VISIBLE);
        } else {
            noChatsTextView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "inside onRefresh");
        GetChatListAsyncTask task = new GetChatListAsyncTask(context);
        task.setAsyncTaskReceiver(new GetChatListAsyncTask.AsyncTaskReceiver() {
            @Override
            public void onAsyncTaskComplete(List<Chat> chats) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (chats == null) {
                    Toast.makeText(context, "No chats found", Toast.LENGTH_SHORT).show();
                    appData.storeChats(context, new ArrayList<Chat>());
                } else {
                    Toast.makeText(context, chats.size() + " chats found", Toast.LENGTH_SHORT).show();
                    appData.storeChats(context, chats);
                }
                chatsRecyclerViewAdapter.updateChatData();
                chatsRecyclerViewAdapter.notifyDataSetChanged();
                updateView();
            }

            @Override
            public void onAsyncTaskFailed() {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        task.execute();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(ChatsRecyclerViewAdapter.ChatListItem item);
    }


}
