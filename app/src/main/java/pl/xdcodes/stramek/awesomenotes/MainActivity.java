package pl.xdcodes.stramek.awesomenotes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import pl.xdcodes.stramek.awesomenotes.adapters.Adapter;
import pl.xdcodes.stramek.awesomenotes.adapters.ViewHolder;
import pl.xdcodes.stramek.awesomenotes.database.NotesDataSource;
import pl.xdcodes.stramek.awesomenotes.notes.Note;
import pl.xdcodes.stramek.awesomenotes.parse.NoteParse;
import pl.xdcodes.stramek.awesomenotes.parse.ParseDialog;

public class MainActivity extends AppCompatActivity
        implements ViewHolder.ClickListener,
                   SwipeRefreshLayout.OnRefreshListener,
                   ParseDialog.StatusDialogListener {

    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";

    private Adapter adapter;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;

    private NotesDataSource dataSource;

    private ParseDialog dialog;

    public static int ADD_NOTE = 1;
    public static int EDIT_NOTE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Config.context = this;

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
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(getNumberOfColumns(), LinearLayoutManager.VERTICAL));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditNote.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("status", ADD_NOTE);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivityForResult(intent, 1);
            }
        });
    }

    private int getNumberOfColumns() {
        float scaleFactor = getResources().getDisplayMetrics().density * 150;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        return (int) ((float) width / scaleFactor);
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

    public void addNote(String resultNote, boolean important) {
        dataSource.open();
        final Note n;

        n = dataSource.createNote(resultNote, important);
        adapter.addNote(n);

        if(isOnline()) {
            SharedPreferences prefs = this.getSharedPreferences("parse", Context.MODE_PRIVATE);
            String email = prefs.getString("lastLogin", "");
            String password = prefs.getString("lastPassword", "");

            ParseUser.logInInBackground(email, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        NoteParse np = new NoteParse(n);
                        np.setACL(new ParseACL(ParseUser.getCurrentUser()));
                        np.saveInBackground();
                    }
                }
            });
        }
        recyclerView.smoothScrollToPosition(0);
    }

    public void editNote(long id, int position, final String resultNote, final boolean important) {
        dataSource.open();
        dataSource.editNote(id, resultNote, important);
        adapter.editNote(position, resultNote, important);

        if(isOnline()) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("NoteParse");
            query.whereEqualTo("id", id);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        for (ParseObject edit : parseObjects) {
                            edit.put("noteText", resultNote);
                            edit.put("important", important);
                            edit.saveInBackground();
                        }
                    }
                }
            });
        }
        recyclerView.smoothScrollToPosition(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                addNote(data.getStringExtra("note"), data.getBooleanExtra("important", false));
            }
        } else if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK) {
                editNote(data.getLongExtra("id", 0), data.getIntExtra("position", 0),
                         data.getStringExtra("note"), data.getBooleanExtra("important", false));
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

        if (id == R.id.action_synchronization) {
            dialog = new ParseDialog();
            dialog.show(getSupportFragmentManager(), null);
            return true;
        }

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
        int count = Adapter.getSelectedItemCount();

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
            case NO_INTERNET_CONNECTION:
                Snackbar.make(recyclerView, getString(R.string.no_internet), Snackbar.LENGTH_LONG).show();
                break;
            default:
        }
    }

    @Override
    public void onRefresh() {
        if(isOnline()) {
            SharedPreferences prefs = this.getSharedPreferences("parse", Context.MODE_PRIVATE);
            String email = prefs.getString("lastLogin", "");
            String password = prefs.getString("lastPassword", "");

            ParseUser.logInInBackground(email, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        dataSource.deleteAllNotes();
                        adapter.removeAllNotes();

                        ParseQuery<NoteParse> query = new ParseQuery<>("NoteParse");
                        query.orderByAscending("_created_at");
                        try {
                            List<NoteParse> list = query.find();
                            dataSource.open();
                            for (NoteParse n : list) {
                                Note note = dataSource.createNote(n);
                                adapter.addNote(note);
                            }
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
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
        private static final String TAG = "ActionModeCallback";

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
                    List<Note> list = Adapter.getNotes();

                    int i = 0;
                    for (Note n : list) {
                        if (items.contains(i)) {
                            dataSource.deleteNote(n);
                            deleteNote(n.getId());
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

        private void deleteNote(long id) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("NoteParse");
            query.whereEqualTo("id", id);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        for (ParseObject delete : parseObjects) {
                            delete.deleteInBackground();
                        }
                    }
                }
            });
        }
    }
}
