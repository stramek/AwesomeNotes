package pl.xdcodes.stramek.awesomenotes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import pl.xdcodes.stramek.awesomenotes.adapters.Adapter;

public class AddNote extends AppCompatActivity {

    private static final String TAG = "AddNote";

    private FloatingActionButton fab;
    private EditText noteText;
    private AppCompatCheckBox important;

    private int status;
    private int position;
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_note);

        fab = (FloatingActionButton) findViewById(R.id.fab_add);

        status = getIntent().getIntExtra("status", 1);
        position = getIntent().getIntExtra("position", 0);
        id = getIntent().getLongExtra("id", 0);

        noteText = (EditText) findViewById(R.id.note);

        if(status == MainActivity.EDIT_NOTE) {
            noteText.setText(Adapter.getNote(position).getNoteText());
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_done_white_24dp));
        }

        noteText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noteText.length() == 0) {
                        Snackbar.make(v, getString(R.string.no_noteText), Snackbar.LENGTH_LONG).show();
                } else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("note", noteText.getText().toString());
                    returnIntent.putExtra("important", important.isChecked());
                    returnIntent.putExtra("position", position);
                    returnIntent.putExtra("id", id);

                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note_menu, menu);

        important = (AppCompatCheckBox) menu.findItem(R.id.action_important).getActionView();
        important.setText(getString(R.string.action_important));
        important.setPadding(important.getPaddingLeft(), important.getPaddingTop(), 40, important.getPaddingBottom());

        if(status == MainActivity.EDIT_NOTE)
            important.setChecked(Adapter.getNote(position).getImportant());

        return true;
    }

}