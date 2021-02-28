package com.example.recyclerview;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private final String TAG = "MyAdapter";
    private String[] packages;
    private String[] items;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String ASWITCH = "aSwitch";
    Context context;

    public MyAdapter(String[] items, String[] packages, Context context) {
        this.context = context;
        this.items = items;
        this.packages = packages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        //saveData(holder);
        final String title = items[position];
        final String packagename = packages[position];

        updateView(holder,position);
        saveData(holder,position);

        if (holder.aSwitch.isChecked())
            holder.text.setText(title);
        else
            holder.text.setText("BLOCKED");
        Drawable appicon = null;
        try {
            appicon = context.getPackageManager().getApplicationIcon(packagename);
        } catch (PackageManager.NameNotFoundException e) {
           // Toast.makeText(context, "Icon Not found!", Toast.LENGTH_SHORT).show();
        }
        holder.imageView.setImageDrawable(appicon);
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (items[position] != null)
                    Toast.makeText(context, items[position], Toast.LENGTH_SHORT).show();
            }
        });
        holder.aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(holder,position);
               if (holder.aSwitch.isChecked()) {

                    holder.text.setText(items[position]);
                    Toast.makeText(context, "App Track Activated", Toast.LENGTH_SHORT).show();
                } else {

                    holder.text.setText("BLOCKED");
                    Toast.makeText(context, "App Track Blocked", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView imageView;
        Switch aSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.textview);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            aSwitch = (Switch) itemView.findViewById(R.id.button);
        }
    }

    public void saveData(ViewHolder holder,int i) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ASWITCH+i, holder.aSwitch.isChecked());
        editor.apply();
    }
    public void updateView(ViewHolder holder,int i){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        holder.aSwitch.setChecked(sharedPreferences.getBoolean(ASWITCH+i,true));
    }

}
