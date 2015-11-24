package pl.xdcodes.stramek.awesomenotes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

import pl.xdcodes.stramek.awesomenotes.adapters.Adapter;
import pl.xdcodes.stramek.awesomenotes.database.NotesDataSource;
import pl.xdcodes.stramek.awesomenotes.notes.Note;
import pl.xdcodes.stramek.awesomenotes.parse.ParseDialog;

public class MainActivity extends AppCompatActivity
        implements Adapter.ViewHolder.ClickListener,
                   SwipeRefreshLayout.OnRefreshListener,
                   ParseDialog.StatusDialogListener {

    private Adapter adapter;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;

    private NotesDataSource dataSource;

    private ParseDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionModeCallback = new ActionModeCallback();

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        dataSource = new NotesDataSource(this);
        dataSource.open();

        List<Note> values = dataSource.getAllNotes();
        adapter = new Adapter(this, values);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        int column;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            column = 3;
        } else {
            column = 2;
        }

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(column, LinearLayoutManager.VERTICAL));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this, AddNote.class);
                intent.setAction(Intent.ACTION_VIEW);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onResume() {
        dataSource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            dataSource.open();
            String resultTitle;
            String resultNote;

            if(resultCode == Activity.RESULT_OK){
                Note n;

                resultTitle = data.getStringExtra("title");
                resultNote = data.getStringExtra("note");

                n = dataSource.createNote(resultTitle, resultNote);
                adapter.addNote(n);

                adapter.notifyItemInserted(0);
                recyclerView.smoothScrollToPosition(0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
        return true;
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public void onFinishDialog(ParseDialog.status message) {
        switch (message) {
            case LOG_IN_SUCCESS:
                    Snackbar.make(recyclerView, getString(R.string.parse_logged_in), Snackbar.LENGTH_LONG).show();
                break;
            case CREATE_ACCOUNT_SUCCESS:
                    Snackbar.make(recyclerView, getString(R.string.parse_created_account), Snackbar.LENGTH_LONG).show();
                break;
            default:
        }
    }

    @Override public void onRefresh() {
        SharedPreferences prefs = this.getSharedPreferences("parse", Context.MODE_PRIVATE);
        String email = prefs.getString("lastLogin", "");
        String password = prefs.getString("lastPassword", "");

        if(isOnline()) {
            ParseUser.logInInBackground(email, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        Snackbar.make(recyclerView, getString(R.string.parse_refresh), Snackbar.LENGTH_SHORT).show();

                        // TODO Synchronizacja

                        ParseUser.logOut();
                        swipeLayout.setRefreshing(false);
                    } else {
                        dialog = new ParseDialog();
                        dialog.show(getSupportFragmentManager(), null);
                        swipeLayout.setRefreshing(false);
                    }
                }
            });
        } else {
            Snackbar.make(recyclerView, getString(R.string.no_internet), Snackbar.LENGTH_LONG).show();
            swipeLayout.setRefreshing(false);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selected_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:

                    List<Integer> items = adapter.getSelectedItems();

                    // TODO Znaleźć lepszy sposób na usuwanie kliku notek...
                    List<Note> list = adapter.getNotes();
                    int i = 0;
                    for (Note n : list) {
                        if (items.contains(i)) {
                            dataSource.deleteNote(n);
                        }
                        i++;
                    }
                    adapter.removeNotes(items);

                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            actionMode = null;
        }
    }
}
