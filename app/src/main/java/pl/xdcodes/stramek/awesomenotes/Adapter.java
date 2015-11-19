package pl.xdcodes.stramek.awesomenotes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Adapter extends SelectableAdapter<Adapter.ViewHolder> {

    @SuppressWarnings("unused")
    private static final String TAG = Adapter.class.getSimpleName();

    private static final int TYPE_INACTIVE = 0;
    private static final int TYPE_ACTIVE = 1;

    private static final int ITEM_COUNT = 50;
    private ArrayList<Note> notes;

    private ViewHolder.ClickListener clickListener;

    public Adapter(ViewHolder.ClickListener clickListener) {
        super();

        this.clickListener = clickListener;

        Random random = new Random();

        notes = new ArrayList<>();
        for (int i = 0; i < ITEM_COUNT; i++) {
            notes.add(new Note("Notatka " + (i + 1), "Ta notatka" , random.nextBoolean()));
        }
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
        final int layout = viewType == TYPE_INACTIVE ? R.layout.note : R.layout.note_active;

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(v, clickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Note note = notes.get(position);

        holder.title.setText(note.getTitle());
        holder.subtitle.setText(note.getSubtitle() + (note.isActive() ? " jest ważna!" : " nie jest ważna"));

        /*final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
            sglp.setFullSpan(note.isActive());
            holder.itemView.setLayoutParams(sglp);
        }*/

        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        final Note note = notes.get(position);

        return note.isActive() ? TYPE_ACTIVE : TYPE_INACTIVE;
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
            //Log.d(TAG, "Kliknales na pozycji " + getPosition());
            if (listener != null) {
                listener.onItemClicked(getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            //Log.d(TAG, "Dlugo kliknales na pozycji " + getPosition());
            if (listener != null) {
                return listener.onItemLongClicked(getPosition());
            }
            return false;
        }

        public interface ClickListener {
            void onItemClicked(int position);
            boolean onItemLongClicked(int position);
        }
    }
}