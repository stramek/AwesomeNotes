package pl.xdcodes.stramek.awesomenotes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class AddNote extends AppCompatActivity {

    private FloatingActionButton fab;
    private EditText noteText;
    private AppCompatCheckBox important;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_note);

        noteText = (EditText) findViewById(R.id.note);
        noteText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noteText.length() == 0) {
                        Snackbar.make(v, getString(R.string.no_noteText), Snackbar.LENGTH_LONG).show();
                } else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("note", noteText.getText().toString());
                    returnIntent.putExtra("important", important.isChecked());
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

        return true;
    }

}