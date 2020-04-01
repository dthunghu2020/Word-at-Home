package com.hungdt.waterplan.view.adater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hungdt.waterplan.R;
import com.hungdt.waterplan.dataset.Constant;
import com.hungdt.waterplan.model.Plant;
import com.hungdt.waterplan.model.Remind;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlanHolder> {
    private LayoutInflater layoutInflater;
    private List<Plant> plants;
    private List<Remind> reminds = new ArrayList<>();
    private OnPlantItemClickListener onPlantItemClickListener;
    private Boolean cbVisible = false;

    Calendar calendar = Calendar.getInstance();

    public PlantAdapter(Context context, List<Plant> plants) {
        layoutInflater = LayoutInflater.from(context);
        this.plants = plants;
    }

    @NonNull
    @Override
    public PlanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.plant_adapter, parent, false);
        return new PlanHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final PlanHolder holder, final int position) {

        Glide
                .with(layoutInflater.getContext())
                .load(plants.get(position).getPlantImage())
                .placeholder(R.drawable.tree_default)
                .into(holder.imgPlantAvatar);

        holder.txtPlantName.setText(plants.get(position).getPlantName());
        reminds = plants.get(position).getReminds();
        setVisibleGone(holder);

        if (reminds != null) {
            Date date;
            int numberOfDay;
            int remindCircle;
            int numberDayCreate = 0;
            int numberDayInStance = 0;

            @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfDateTime = new SimpleDateFormat(Constant.getDateTimeFormat());
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfDay = new SimpleDateFormat(Constant.getDayFormat());
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfMonth = new SimpleDateFormat(Constant.getMonthFormat());
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfYear = new SimpleDateFormat(Constant.getYearFormat());
            try {
                date = sdfDateTime.parse(getInstantDateTime());
                assert date != null;
                numberDayInStance = countNumberOfDateTime(Integer.parseInt(sdfYear.format(date)), Integer.parseInt(sdfMonth.format(date)), Integer.parseInt(sdfDay.format(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < reminds.size(); i++) {
                if (reminds.get(i).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.water))) {
                    holder.clWater.setVisibility(View.VISIBLE);
                    try {
                        date = sdfDateTime.parse(reminds.get(i).getRemindCreateDT());
                        assert date != null;
                        numberDayCreate = countNumberOfDateTime(Integer.parseInt(sdfYear.format(date)), Integer.parseInt(sdfMonth.format(date)), Integer.parseInt(sdfDay.format(date)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    remindCircle = Integer.parseInt(reminds.get(i).getCareCycle());
                    numberOfDay = numberDayCreate + remindCircle - numberDayInStance;
                    holder.pbWater.setMax(remindCircle);
                    if (numberOfDay > 0) {
                        holder.pbWater.setProgress(numberOfDay);
                    } else {
                        holder.pbWater.setProgress(0);
                    }
                    holder.txtPlantWater.setText(reminds.get(i).getCareCycle() + " days");
                }
                if (reminds.get(i).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.fertilize))) {
                    holder.clFertilizer.setVisibility(View.VISIBLE);
                    try {
                        date = sdfDateTime.parse(reminds.get(i).getRemindCreateDT());
                        assert date != null;
                        numberDayCreate = countNumberOfDateTime(Integer.parseInt(sdfYear.format(date)), Integer.parseInt(sdfMonth.format(date)), Integer.parseInt(sdfDay.format(date)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    remindCircle = Integer.parseInt(reminds.get(i).getCareCycle());
                    numberOfDay = numberDayCreate + remindCircle - numberDayInStance-1;
                    holder.pbFertilizer.setMax(remindCircle);
                    if (numberOfDay > 0) {
                        holder.pbFertilizer.setProgress(numberOfDay);
                    } else {
                        holder.pbFertilizer.setProgress(0);
                    }
                    holder.txtPlantFertilizer.setText(reminds.get(i).getCareCycle() + " days");
                }

                if (reminds.get(i).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.prune))) {
                    holder.clPrune.setVisibility(View.VISIBLE);
                    try {
                        date = sdfDateTime.parse(reminds.get(i).getRemindCreateDT());
                        assert date != null;
                        numberDayCreate = countNumberOfDateTime(Integer.parseInt(sdfYear.format(date)), Integer.parseInt(sdfMonth.format(date)), Integer.parseInt(sdfDay.format(date)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    remindCircle = Integer.parseInt(reminds.get(i).getCareCycle());
                    numberOfDay = numberDayCreate + remindCircle - numberDayInStance-2;
                    holder.pbPrune.setMax(remindCircle);
                    if (numberOfDay > 0) {
                        holder.pbPrune.setProgress(numberOfDay);
                    } else {
                        holder.pbPrune.setProgress(0);
                    }
                    holder.txtPlantPrune.setText(reminds.get(i).getCareCycle() + " days");
                }

                if (reminds.get(i).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.spray))) {
                    holder.clSpray.setVisibility(View.VISIBLE);
                    try {
                        date = sdfDateTime.parse(reminds.get(i).getRemindCreateDT());
                        assert date != null;
                        numberDayCreate = countNumberOfDateTime(Integer.parseInt(sdfYear.format(date)), Integer.parseInt(sdfMonth.format(date)), Integer.parseInt(sdfDay.format(date)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    remindCircle = Integer.parseInt(reminds.get(i).getCareCycle());
                    numberOfDay = numberDayCreate + remindCircle - numberDayInStance-3;
                    holder.pbSpray.setMax(remindCircle);
                    if (numberOfDay > 0) {
                        holder.pbSpray.setProgress(numberOfDay);
                    } else {
                        holder.pbSpray.setProgress(0);
                    }
                    holder.txtPlantSpray.setText(reminds.get(i).getCareCycle() + " days");
                }

            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbVisible) {
                    plants.get(position).setTicked(!plants.get(position).isTicked());
                    holder.cbPlant.setChecked(plants.get(position).isTicked());
                }
                onPlantItemClickListener.OnItemClicked(holder.getAdapterPosition(), cbVisible);
            }
        });
    }

    private void setVisibleGone(PlanHolder holder) {
        if (!cbVisible) {
            holder.cbPlant.setVisibility(View.GONE);
        } else {
            holder.cbPlant.setVisibility(View.VISIBLE);
            holder.cbPlant.setChecked(false);
        }
        holder.clWater.setVisibility(View.GONE);
        holder.clFertilizer.setVisibility(View.GONE);
        holder.clSpray.setVisibility(View.GONE);
        holder.clPrune.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    class PlanHolder extends RecyclerView.ViewHolder {
        private CheckBox cbPlant;
        private ImageView imgPlantAvatar;
        private ProgressBar pbWater, pbFertilizer, pbPrune, pbSpray;
        private TextView txtPlantName, txtPlantWater, txtPlantFertilizer, txtPlantPrune, txtPlantSpray;
        private ConstraintLayout clWater, clFertilizer, clPrune, clSpray;

        public PlanHolder(@NonNull View itemView) {
            super(itemView);
            cbPlant = itemView.findViewById(R.id.cbPlant);
            imgPlantAvatar = itemView.findViewById(R.id.imgPlantAvatar);
            pbWater = itemView.findViewById(R.id.pbWater);
            pbFertilizer = itemView.findViewById(R.id.pbFertilizer);
            pbPrune = itemView.findViewById(R.id.pbPrune);
            pbSpray = itemView.findViewById(R.id.pbSpray);
            txtPlantName = itemView.findViewById(R.id.txtPlanName);
            txtPlantWater = itemView.findViewById(R.id.txtWater);
            txtPlantFertilizer = itemView.findViewById(R.id.txtFertilizer);
            txtPlantPrune = itemView.findViewById(R.id.txtPrune);
            txtPlantSpray = itemView.findViewById(R.id.txtSpray);
            clWater = itemView.findViewById(R.id.clWater);
            clFertilizer = itemView.findViewById(R.id.clFertilizer);
            clPrune = itemView.findViewById(R.id.clPrune);
            clSpray = itemView.findViewById(R.id.clSpray);
        }
    }

    private String getInstantDateTime() {
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat(Constant.getDateTimeFormat(), Locale.US);
        return sdf.format(calendar.getTime());
    }

    private int countNumberOfDateTime(int year, int month, int day) {
        if (month < 3) {
            year--;
            month += 12;
        }
        return 365 * year + year / 4 - year / 100 + year / 400 + (153 * month - 457) / 5 + day - 306;
    }

    public void setOnPlantItemClickListener(OnPlantItemClickListener onPlantItemClickListener) {
        this.onPlantItemClickListener = onPlantItemClickListener;
    }

    public interface OnPlantItemClickListener {
        void OnItemClicked(int position, boolean checkBox);
    }

    public void enableCheckBox() {
        cbVisible = true;
    }

    public void disableCheckBox() {
        cbVisible = false;
    }

}
