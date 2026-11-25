package vn.edu.stu.nguyenmanhphong_dh52301513.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.stu.nguyenmanhphong_dh52301513.R;
import vn.edu.stu.nguyenmanhphong_dh52301513.model.FoodCategory;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnCategoryActionListener {
        void onCategoryClick(FoodCategory category);

        void onCategoryLongClick(FoodCategory category);
    }

    private Context context;
    private ArrayList<FoodCategory> data;
    private OnCategoryActionListener listener;

    public CategoryAdapter(Context context, ArrayList<FoodCategory> data,
                           OnCategoryActionListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(context)
                .inflate(R.layout.item_row_category, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodCategory category = data.get(position);
        holder.getTvCategoryCode().setText(category.getCategoryCode());
        holder.getTvCategoryName().setText(category.getCategoryName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getBindingAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    listener.onCategoryClick(data.get(currentPosition));
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int currentPosition = holder.getBindingAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    listener.onCategoryLongClick(data.get(currentPosition));
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCategoryCode;
        private final TextView tvCategoryName;

        public TextView getTvCategoryCode() {
            return tvCategoryCode;
        }

        public TextView getTvCategoryName() {
            return tvCategoryName;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryCode = itemView.findViewById(R.id.tvCategoryCode);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
