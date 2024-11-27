package com.example.taskmanagement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagement.Adapter.ToDoAdapter;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class RecyclerViewTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ToDoAdapter adapter;

    public RecyclerViewTouchHelper(ToDoAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false; // We don't need to handle move here
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();

        if (direction == ItemTouchHelper.RIGHT) {
            // Swipe right to delete task with confirmation dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
            builder.setTitle("Delete Task");
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> adapter.deleteTask(position));
            builder.setNegativeButton("Cancel", (dialog, i) -> adapter.notifyItemChanged(position)); // Restore item if cancelled
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (direction == ItemTouchHelper.LEFT) {
            // Swipe left to edit task
            adapter.editItems(position);  // Handle editing of the task
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        // Customize swipe backgrounds and action icons
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(adapter.getContext(), R.color.green))
                .addSwipeLeftActionIcon(R.drawable.ic_edit)
                .addSwipeRightBackgroundColor(ContextCompat.getColor(adapter.getContext(), R.color.red))
                .addSwipeRightActionIcon(R.drawable.ic_delete)
                .create()
                .decorate();

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);  // Let RecyclerView do its default drawing
    }
}
