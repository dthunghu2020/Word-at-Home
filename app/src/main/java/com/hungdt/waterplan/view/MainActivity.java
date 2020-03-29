package com.hungdt.waterplan.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.hungdt.waterplan.KEY;
import com.hungdt.waterplan.R;
import com.hungdt.waterplan.database.DBHelper;
import com.hungdt.waterplan.model.Plant;
import com.hungdt.waterplan.view.adater.PlantAdapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView imgSetting;
    private CircleImageView imgPlus, imgWater, imgFertilizer, imgCut, imgSpray;
    private RecyclerView rcvPlan;
    private PlantAdapter plantAdapter;

    private static final int REQUEST_CODE_ADD_PLANT = 100;
    private static final int REQUEST_CODE_EDIT_PLANT = 101;
    private static final int REQUEST_CODE_DELETE_PLANT = 102;

    private int positionSave;

    private List<Plant> plants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();

        plants.addAll(DBHelper.getInstance(this).getAllPlant());
        plantAdapter = new PlantAdapter(this,plants);
        rcvPlan.setLayoutManager(new LinearLayoutManager(this));
        rcvPlan.setAdapter(plantAdapter);

        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });

        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddPlanActivity.class);
                intent.putExtra(KEY.TYPE,KEY.TYPE_CREATE);
                startActivityForResult(intent,REQUEST_CODE_ADD_PLANT);
            }
        });

        imgWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });

        imgFertilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });

        imgCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });

        imgSpray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });

        plantAdapter.setOnPlantItemClickListener(new PlantAdapter.OnPlantItemClickListener() {
            @Override
            public void OnItemClicked(int position) {
                positionSave = position;
                Bundle bundle = new Bundle();
                Intent intent = new Intent(MainActivity.this,AddPlanActivity.class);
                bundle.putSerializable(KEY.PLANT,plants.get(position));
                intent.putExtra(KEY.TYPE,KEY.TYPE_EDIT);
                intent.putExtras(bundle);
                startActivityForResult(intent,REQUEST_CODE_EDIT_PLANT);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_PLANT){
            if(resultCode == Activity.RESULT_CANCELED){
                Plant plant = DBHelper.getInstance(this).getLastPlant();
                plants.add(plant);
                plantAdapter.notifyItemChanged(plants.size()-1);
            }
        }
        if(requestCode == REQUEST_CODE_EDIT_PLANT){
            //xoa
            if(resultCode == Activity.RESULT_OK){
                plants.remove(positionSave);
                plantAdapter.notifyDataSetChanged();
            }
            //sua
            if (resultCode == Activity.RESULT_CANCELED) {
                Plant plant = DBHelper.getInstance(this).getOnePlant(plants.get(positionSave).getPlantID());
                plants.set(positionSave,plant);
                plantAdapter.notifyItemChanged(positionSave);
            }
        }
    }

    private void initView() {
        imgSetting = findViewById(R.id.imgSetting);
        imgPlus = findViewById(R.id.imgPlus);
        imgWater = findViewById(R.id.imgWater);
        imgFertilizer = findViewById(R.id.imgFertilizer);
        imgCut = findViewById(R.id.imgCut);
        imgSpray = findViewById(R.id.imgSpray);
        rcvPlan = findViewById(R.id.rcvPlan);
    }
}
