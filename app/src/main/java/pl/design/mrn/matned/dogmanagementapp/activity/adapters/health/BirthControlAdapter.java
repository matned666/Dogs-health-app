package pl.design.mrn.matned.dogmanagementapp.activity.adapters.health;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import pl.design.mrn.matned.dogmanagementapp.R;
import pl.design.mrn.matned.dogmanagementapp.activity.dataactivity.edit.ChipActivityEdit;
import pl.design.mrn.matned.dogmanagementapp.activity.dataactivity.info.ChipActivityInfo;
import pl.design.mrn.matned.dogmanagementapp.activity.health.BirthControlActivity;
import pl.design.mrn.matned.dogmanagementapp.dataBase.dog.additionalData.Chip;
import pl.design.mrn.matned.dogmanagementapp.dataBase.health.BirthControl;
import pl.design.mrn.matned.dogmanagementapp.listeners.DataPositionListener;

import static pl.design.mrn.matned.dogmanagementapp.Statics.DATE_FORMAT;
import static pl.design.mrn.matned.dogmanagementapp.Statics.USAGE_ADD;
import static pl.design.mrn.matned.dogmanagementapp.Statics.USAGE_EDIT;


public class BirthControlAdapter extends RecyclerView.Adapter<BirthControlAdapter.ViewHolder>  {

    private List<BirthControl> birthControlList;
    private DataPositionListener dataPositionListener;
    private int selectedPosition;
    private String usage;
    private Context context;

    @SuppressLint("SimpleDateFormat")
    private DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    public BirthControlAdapter(List<BirthControl> birthControlList, Context context) {
        this.context = context;
        this.birthControlList = birthControlList;
        this.dataPositionListener = DataPositionListener.getInstance();
        this.selectedPosition = dataPositionListener.getPosition();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_birth, null);
        return new ViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BirthControl birthControl = birthControlList.get(position);
        holder.birthDate.setText(dateFormat.format(birthControl.getDateOfBirth()));
        holder.holderButton.setOnClickListener(v -> {
            selectedPosition = position;
            dataPositionListener.setPosition(position);
            dataPositionListener.setSelectedItemId(birthControl.getId());
            notifyDataSetChanged();
                Intent intent = new Intent(context, BirthControlActivity.class);
                context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if(birthControlList == null) return 0;
        else return birthControlList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ConstraintLayout holderButton;
        private TextView birthDate;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.holderButton = itemView.findViewById(R.id.birth_element_button);
            this.birthDate = itemView.findViewById(R.id.birth_element_dateOfBirth);
        }


    }

}

