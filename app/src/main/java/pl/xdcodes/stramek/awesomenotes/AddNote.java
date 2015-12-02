package pl.xdcodes.stramek.awesomenotes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class AddNote extends AppCompatActivity {

    private FloatingActionButton fab;
    private EditText noteText;

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
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}