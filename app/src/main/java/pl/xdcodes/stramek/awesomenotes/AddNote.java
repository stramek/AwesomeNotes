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
    private EditText title;
    private EditText note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_note);

        title = (EditText) findViewById(R.id.title);
        note = (EditText) findViewById(R.id.note);
        title.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.length() > 0) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("title", title.getText().toString());
                    returnIntent.putExtra("note", note.getText().toString());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    Snackbar.make(v, "Aby dodać notatkę, tutuł nie może być pusty!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        //overridePendingTransition(0, 0);
    }

}