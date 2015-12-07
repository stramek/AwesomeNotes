package pl.xdcodes.stramek.awesomenotes.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import pl.xdcodes.stramek.awesomenotes.AddEditNote;
import pl.xdcodes.stramek.awesomenotes.Config;
import pl.xdcodes.stramek.awesomenotes.MainActivity;
import pl.xdcodes.stramek.awesomenotes.R;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    @SuppressWarnings("unsused")
    private static final String TAG = "ViewHolder";

    public TextView noteText;
    public View selectedOverlay;
    public View important;

    private ClickListener listener;

    public ViewHolder(View itemView, ClickListener listener) {
        super(itemView);

        noteText = (TextView) itemView.findViewById(R.id.noteText);
        selectedOverlay = itemView.findViewById(R.id.selected_overlay);
        important = itemView.findViewById(R.id.important);

        this.listener = listener;

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if ((listener != null) && (SelectableAdapter.getSelectedItemCount() == 0)) {
            Intent intent = new Intent(Config.context, AddEditNote.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra("status", MainActivity.EDIT_NOTE);
            intent.putExtra("position", getAdapterPosition());
            intent.putExtra("id", Adapter.getNote(getAdapterPosition()).getId());
            Config.context.startActivityForResult(intent, 2);
        } else if(listener != null) {
            listener.onItemClicked(getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (listener != null) {
            return listener.onItemLongClicked(getAdapterPosition());
        }
        return false;
    }

    public interface ClickListener {
        void onItemClicked(int position);
        boolean onItemLongClicked(int position);
    }
}

