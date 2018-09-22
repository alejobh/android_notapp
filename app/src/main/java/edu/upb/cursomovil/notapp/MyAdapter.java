package edu.upb.cursomovil.notapp;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


    private Cursor mDataset;

    public void setmDataset(Cursor mDataset) {
        this.mDataset = mDataset;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView reminder;
        public TextView title;
        public TextView textBody;
        public Toolbar toolbar;
        public long idNote;

        public ViewHolder(View v) {
            super(v);

            toolbar = v.findViewById(R.id.cardToolbar);
            toolbar.inflateMenu(R.menu.card_toolbar_menu);

            //CODIGO DE EDIT Y DELETE

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if(item.getItemId() == R.id.card_edit) {
                        //paso ID
                        System.out.println("edit ID: "+idNote);
                    }

                    if(item.getItemId() == R.id.card_delete) {

                    }

                    return true;
                }
            });

            reminder = v.findViewById(R.id.card_reminder);
            title = v.findViewById(R.id.textTitle);
            textBody = v.findViewById(R.id.card_text_body);
        }
    }

    public MyAdapter(Cursor myDataset) {
        mDataset = myDataset;
    }



    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_note, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        mDataset.moveToPosition(position);

        holder.idNote = mDataset.getLong(mDataset.getColumnIndex(NotesDbAdapter.KEY_ROWID));
        holder.title.setText(mDataset.getString(mDataset.getColumnIndex(NotesDbAdapter.KEY_TITLE)));

        String reminderText = mDataset.getString(mDataset.getColumnIndex(NotesDbAdapter.KEY_REMINDER));
        if(!reminderText.equals("")) {
            holder.reminder.setText("Reminder: " + reminderText);
            holder.reminder.setVisibility(View.VISIBLE);
        } else {
            holder.reminder.setVisibility(View.GONE);
        }
        holder.textBody.setText(mDataset.getString(mDataset.getColumnIndex(NotesDbAdapter.KEY_TEXT_BODY)));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.getCount();
    }
}
