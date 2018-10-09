package edu.upb.cursomovil.notapp;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
        public ImageButton play, stop;
        public long idNote;

        public ViewHolder(final View v) {
            super(v);

            toolbar = v.findViewById(R.id.cardToolbar);
            toolbar.inflateMenu(R.menu.card_toolbar_menu);
            reminder = v.findViewById(R.id.card_reminder);
            title = v.findViewById(R.id.textTitle);
            textBody = v.findViewById(R.id.card_text_body);
            play = v.findViewById(R.id.notePlay);
            stop = v.findViewById(R.id.noteStop);
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
    public void onBindViewHolder(final MyAdapter.ViewHolder holder, final int position) {
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

        //CODIGO DE EDIT Y DELETE
        holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.card_edit) {
                    //paso ID
                    System.out.println("edit ID: "+holder.idNote);
                }

                if(item.getItemId() == R.id.card_delete) {
                    NotesDbAdapter notesDbAdapter = new NotesDbAdapter(holder.itemView.getContext());
                    notesDbAdapter.open();
                    notesDbAdapter.deleteNote(holder.idNote);
                    setmDataset(notesDbAdapter.fetchAllNotes());
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                    Toast.makeText(holder.itemView.getContext(),"Note deleted", Toast.LENGTH_LONG).show();
                }

                return true;
            }

        });

        if(mDataset.getString(mDataset.getColumnIndex(NotesDbAdapter.KEY_TYPE)).equals(NotesDbAdapter.TYPE_TEXT)){
            holder.textBody.setVisibility(View.VISIBLE);
            holder.textBody.setText(mDataset.getString(mDataset.getColumnIndex(NotesDbAdapter.KEY_TEXT_BODY)));
        } else if(mDataset.getString(mDataset.getColumnIndex(NotesDbAdapter.KEY_TYPE)).equals(NotesDbAdapter.TYPE_AUDIO)) {
            holder.stop.setEnabled(false);
            holder.play.setVisibility(View.VISIBLE);
            holder.stop.setVisibility(View.VISIBLE);
            holder.stop.setAlpha(.5f);
            final String fileRoute = mDataset.getString(mDataset.getColumnIndex(NotesDbAdapter.KEY_AUDIO_PATH));
            final MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(fileRoute);
            }catch(Exception e){
            }

            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    try {
                        mediaPlayer.prepare();
                        holder.play.setEnabled(false);
                        holder.play.setAlpha(.5f);
                        holder.stop.setAlpha(1f);
                        holder.stop.setEnabled(true);
                        mediaPlayer.start();
                        Toast.makeText(v.getContext(),"Playing note", Toast.LENGTH_LONG).show();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.stop();
                                holder.play.setAlpha(1f);
                                holder.play.setEnabled(true);
                                holder.stop.setEnabled(false);
                                holder.stop.setAlpha(.5f);
                                Toast.makeText(v.getContext(),"Finished playing note", Toast.LENGTH_LONG).show();
                            }

                        });
                    } catch (Exception e) {
                        // make something
                    }
                }
            });

            holder.stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(),"Stopped playing note", Toast.LENGTH_LONG).show();
                    mediaPlayer.stop();
                    holder.stop.setEnabled(false);
                    holder.play.setEnabled(true);
                    holder.stop.setAlpha(.5f);
                    holder.play.setAlpha(1f);
                }
            });

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.getCount();
    }
}
