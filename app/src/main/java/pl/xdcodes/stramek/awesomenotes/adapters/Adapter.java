package pl.xdcodes.stramek.awesomenotes.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import pl.xdcodes.stramek.awesomenotes.R;
import pl.xdcodes.stramek.awesomenotes.notes.Note;

public class Adapter extends SelectableAdapter<Adapter.ViewHolder> {

    @SuppressWarnings("unused")
    private static final String TAG = Adapter.class.getSimpleName();

    private static final int NOT_IMPORTANT = 0;
    private static final int IMPORTANT = 1;

    private LinkedList<Note> notes;

    private ViewHolder.ClickListener clickListener;

    public Adapter(ViewHolder.ClickListener clickListener, List<Note> list) {
        super();

        this.clickListener = clickListener;

        notes = new LinkedList<>();

        for(Note n : list) {
            notes.addFirst(new Note(n.getId(), n.getTitle(), n.getNoteText(), false));
        }
    }

    public LinkedList getNotes() {
        return notes;
    }

    public void addNote(Note note) {
        notes.addFirst(note);
    }

    public void removeNote(int position) {
        notes.remove(position);
        notifyItemRemoved(position);
    }

    public void removeNotes(List<Integer> positions) {
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeNote(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeNote(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }

                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }
    }

    private void removeRange(int positionStart, int itemCount) {

        for (int i = 0; i < itemCount; ++i) {
            notes.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final int layout = viewType == NOT_IMPORTANT ? R.layout.note : R.layout.note_active;

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(v, clickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Note note = notes.get(position);

        holder.title.setText(note.getTitle());
        holder.subtitle.setText(note.getNoteText());

        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        final Note note = notes.get(position);

        return note.getImportant() ? IMPORTANT : NOT_IMPORTANT;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        @SuppressWarnings("unsused")
        private final String TAG = ViewHolder.class.getSimpleName();

        TextView title;
        TextView subtitle;
        View selectedOverlay;

        private ClickListener listener;

        public ViewHolder(View itemView, ClickListener listener) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);

            this.listener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
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
}
