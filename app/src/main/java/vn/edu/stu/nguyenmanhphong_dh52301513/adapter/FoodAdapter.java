package vn.edu.stu.nguyenmanhphong_dh52301513.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.stu.nguyenmanhphong_dh52301513.R;
import vn.edu.stu.nguyenmanhphong_dh52301513.model.Food;
import vn.edu.stu.nguyenmanhphong_dh52301513.util.ImageHelper;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    public interface OnFoodActionListener {
        void onFoodClick(Food food);

        void onFoodLongClick(Food food);
    }

    private Context context;
    private ArrayList<Food> data;
    private OnFoodActionListener listener;

    public FoodAdapter(Context context, ArrayList<Food> data, OnFoodActionListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(context)
                .inflate(R.layout.item_row_food, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = data.get(position);
        holder.getTvFoodCode().setText(String.valueOf(food.getFoodCode()));
        holder.getTvFoodName().setText(food.getFoodName());
        holder.getTvCategoryName().setText(food.getCategoryName());
        ImageHelper.loadImage(holder.getIvFoodImage(), food.getImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getBindingAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    listener.onFoodClick(data.get(currentPosition));
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int currentPosition = holder.getBindingAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    listener.onFoodLongClick(data.get(currentPosition));
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
        private final ImageView ivFoodImage;
        private final TextView tvFoodCode;
        private final TextView tvFoodName;
        private final TextView tvCategoryName;

        public ImageView getIvFoodImage() {
            return ivFoodImage;
        }

        public TextView getTvFoodCode() {
            return tvFoodCode;
        }

        public TextView getTvFoodName() {
            return tvFoodName;
        }

        public TextView getTvCategoryName() {
            return tvCategoryName;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
            tvFoodCode = itemView.findViewById(R.id.tvFoodCode);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
