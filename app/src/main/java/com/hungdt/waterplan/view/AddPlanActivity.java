package com.hungdt.waterplan.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.hungdt.waterplan.KEY;
import com.hungdt.waterplan.R;
import com.hungdt.waterplan.database.DBHelper;
import com.hungdt.waterplan.dataset.Constant;
import com.hungdt.waterplan.model.Plant;
import com.hungdt.waterplan.model.Remind;
import com.hungdt.waterplan.view.adater.RemindAdapter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPlanActivity extends AppCompatActivity {

    private ImageView imgBack, imgDeletePlant, imgPlantAvatar;
    private CircleImageView imgGallery, imgCamera;
    private TextView txtSave;
    private EditText edtPlanName, edtPlanNote;
    private RecyclerView rcvListCareSchedule;
    private LinearLayout addCareSchedule;

    private RemindAdapter remindAdapter;

    private Plant plant;
    private List<String> checkRemind = new ArrayList<>();
    private List<Remind> reminds = new ArrayList<>();

    final Calendar calendar = Calendar.getInstance();

    private static final int CAMERA_PERMISSION_CODE = 200;
    private static final int CAMERA_REQUEST_CODE = 201;
    private static final int GALLERY_REQUEST_CODE = 202;
    String currentPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);

        initView();

        String[] listCheck = {this.getResources().getString(R.string.water),
                this.getResources().getString(R.string.fertilize),
                this.getResources().getString(R.string.spray),
                this.getResources().getString(R.string.prune)};
        checkRemind.addAll(Arrays.asList(listCheck));
        if (reminds != null) {
            for (int i = 0; i < reminds.size(); i++) {
                if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.water))) {
                    checkRemind.remove(this.getResources().getString(R.string.water));
                } else if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.fertilize))) {
                    checkRemind.remove(this.getResources().getString(R.string.fertilize));
                } else if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.spray))) {
                    checkRemind.remove(this.getResources().getString(R.string.spray));
                } else {
                    checkRemind.remove(this.getResources().getString(R.string.prune));
                }
            }
        }

        Intent intent = getIntent();
        final String type = intent.getStringExtra(KEY.TYPE);
        assert type != null;
        if (type.equals(KEY.TYPE_CREATE)) {
            imgDeletePlant.setVisibility(View.GONE);
        } else if (type.equals(KEY.TYPE_EDIT)) {
            plant = (Plant) intent.getSerializableExtra(KEY.PLANT);
            assert plant != null;
            reminds = plant.getReminds();
            edtPlanName.setText(plant.getPlantName());
            edtPlanNote.setText(plant.getPlantNote());
        }
        remindAdapter = new RemindAdapter(this, reminds);
        rcvListCareSchedule.setLayoutManager(new LinearLayoutManager(this));
        rcvListCareSchedule.setAdapter(remindAdapter);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPlanActivity.super.onBackPressed();
            }
        });

        imgDeletePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeletePlantDialog();
            }
        });


        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPlanName.getText().toString().isEmpty()) {
                    Toast.makeText(AddPlanActivity.this, "Give the plant a name!", Toast.LENGTH_SHORT).show();
                } else {
                    if (type.equals(KEY.TYPE_CREATE)) {
                        if (edtPlanNote.getText().toString().isEmpty()) {
                            DBHelper.getInstance(AddPlanActivity.this).addPlan(edtPlanName.getText().toString(), "", "");
                        } else {
                            DBHelper.getInstance(AddPlanActivity.this).addPlan(edtPlanName.getText().toString(), "", edtPlanNote.getText().toString());
                        }
                        if (reminds != null) {
                            String plantID = DBHelper.getInstance(AddPlanActivity.this).getLastPlanID();
                            for (int i = 0; i < reminds.size(); i++) {
                                Remind remind = reminds.get(i);
                                DBHelper.getInstance(AddPlanActivity.this).addRemind(plantID, remind.getRemindType(), remind.getRemindDate(), remind.getRemindTime(), remind.getCareCycle());
                            }
                            reminds.clear();
                        }
                    }
                    if (type.equals(KEY.TYPE_EDIT)) {
                        if (edtPlanNote.getText().toString().isEmpty()) {
                            DBHelper.getInstance(AddPlanActivity.this).updatePlan(plant.getPlantID(), edtPlanName.getText().toString(), "", "");
                        } else {
                            DBHelper.getInstance(AddPlanActivity.this).updatePlan(plant.getPlantID(), edtPlanName.getText().toString(), "", edtPlanNote.getText().toString());
                        }
                        DBHelper.getInstance(AddPlanActivity.this).deletePlanRemind(plant.getPlantID());
                        if (reminds != null) {
                            for (int i = 0; i < reminds.size(); i++) {
                                Remind remind = reminds.get(i);
                                DBHelper.getInstance(AddPlanActivity.this).addRemind(plant.getPlantID(), remind.getRemindType(), remind.getRemindDate(), remind.getRemindTime(), remind.getCareCycle());

                            }
                            reminds.clear();
                        }
                    }
                    //Xay ra van de khi back ko co du lieu.// tu dong add phan tu cuoi. RESULT_CANCELED nguy hiem!
                    //Camera per va hoi progress bar time notifi
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                }
            }
        });

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermissions();

            }
        });

        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

        addCareSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChoseTypeDialog();
            }
        });

        remindAdapter.setOnRemindItemClickListener(new RemindAdapter.OnRemindItemClickListener() {
            @Override
            public void OnItemDateTimeClicked(int position) {
                openEditRemindDialog(reminds.get(position), position);
            }

            @Override
            public void OnItemDeleteClicked(int position) {
                checkRemind.add(reminds.get(position).getRemindType());
                reminds.remove(position);
                remindAdapter.notifyDataSetChanged();
                addCareSchedule.setVisibility(View.VISIBLE);
            }
        });
    }

    private void askPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            Toast.makeText(this, "...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                imgPlantAvatar.setImageURI(Uri.fromFile(f));
                //Gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }
        }
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Uri contentUri = data.getData();
                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("HH:mm-dd/MM/yyyy").format(new Date());
                imgPlantAvatar.setImageURI(contentUri);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("HH:mm-dd/MM/yyyy").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.hungdt.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void openDeletePlantDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.delete_plant_dialog);

        final Button btnYes = dialog.findViewById(R.id.btnYes);
        final Button btnNo = dialog.findViewById(R.id.btnNo);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper.getInstance(AddPlanActivity.this).deletePlan(plant.getPlantID());
                if (reminds.size() > 0) {
                    Log.e("123", "size: " + reminds.size());
                    DBHelper.getInstance(AddPlanActivity.this).deletePlanRemind(plant.getPlantID());
                }
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                dialog.dismiss();
                finish();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void openChoseTypeDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.choose_type_dialog);

        final LinearLayout llWater = dialog.findViewById(R.id.llWater);
        final LinearLayout llFertilizer = dialog.findViewById(R.id.llFertilizer);
        final LinearLayout llSpray = dialog.findViewById(R.id.llSpray);
        final LinearLayout llPrune = dialog.findViewById(R.id.llPrune);

        for (int i = 0; i < reminds.size(); i++) {
            if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.water))) {
                llWater.setVisibility(View.GONE);
            } else if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.fertilize))) {
                llFertilizer.setVisibility(View.GONE);
            } else if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.spray))) {
                llSpray.setVisibility(View.GONE);
            } else {
                llPrune.setVisibility(View.GONE);
            }
        }

        llWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRemind.remove(dialog.getContext().getResources().getString(R.string.water));
                if (checkRemind.size() == 0) {
                    addCareSchedule.setVisibility(View.GONE);
                }
                reminds.add(new Remind("0", dialog.getContext().getResources().getString(R.string.water), getInstantDay(), "07:00", "5"));
                remindAdapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });

        llFertilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRemind.remove(dialog.getContext().getResources().getString(R.string.fertilize));
                if (checkRemind.size() == 0) {
                    addCareSchedule.setVisibility(View.GONE);
                }
                reminds.add(new Remind("1", dialog.getContext().getResources().getString(R.string.fertilize), getInstantDay(), "07:00", "5"));
                remindAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        llSpray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRemind.remove(dialog.getContext().getResources().getString(R.string.spray));
                if (checkRemind.size() == 0) {
                    addCareSchedule.setVisibility(View.GONE);
                }
                reminds.add(new Remind("2", dialog.getContext().getResources().getString(R.string.spray), getInstantDay(), "07:00", "5"));
                remindAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        llPrune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRemind.remove(dialog.getContext().getResources().getString(R.string.prune));
                if (checkRemind.size() == 0) {
                    addCareSchedule.setVisibility(View.GONE);
                }
                reminds.add(new Remind("3", dialog.getContext().getResources().getString(R.string.prune), getInstantDay(), "07:00", "5"));
                remindAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void openEditRemindDialog(final Remind remind, final int position) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_remind_dialog);

        final TextInputEditText edtRemindDay = dialog.findViewById(R.id.edtRemindDay);
        final TextInputEditText edtRemindTime = dialog.findViewById(R.id.edtRemindTime);
        final Button btnEdit = dialog.findViewById(R.id.btnEdit);
        final Button btnBack = dialog.findViewById(R.id.btnBack);

        edtRemindDay.setText(remind.getCareCycle());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(Constant.getTimeFormat());
        Date date;
        try {
            date = sdf.parse(remind.getRemindTime());
            assert date != null;
            edtRemindTime.setText(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        edtRemindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(edtRemindTime);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtRemindDay.getText().toString().isEmpty() || edtRemindTime.getText().toString().isEmpty()) {
                    Toast.makeText(AddPlanActivity.this, "Please enter all title!", Toast.LENGTH_SHORT).show();
                } else {
                    Remind remindEdited = new Remind(remind.getRemindID(), remind.getRemindType(), remind.getRemindDate(), edtRemindTime.getText().toString(), edtRemindDay.getText().toString());
                    reminds.set(position, remindEdited);
                    remindAdapter.notifyItemChanged(position);
                    dialog.dismiss();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showTimeDialog(final TextInputEditText edtRemindTime) {
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddPlanActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hours = String.valueOf(hourOfDay);
                String minutes = String.valueOf(minute);
                if (hourOfDay < 10) {
                    hours = "0" + hours;
                }
                if (minute < 10) {
                    minutes = "0" + minutes;
                }
                edtRemindTime.setText(hours + ":" + minutes);
            }
        }, hour, minute, android.text.format.DateFormat.is24HourFormat(AddPlanActivity.this));
        timePickerDialog.show();
    }

    private String getInstantDay() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constant.getDateFormat(), Locale.US);
        return sdf.format(calendar.getTime());
    }

    private void initView() {
        imgBack = findViewById(R.id.imgBack);
        imgDeletePlant = findViewById(R.id.imgDeletePlant);
        imgCamera = findViewById(R.id.imgCamera);
        imgPlantAvatar = findViewById(R.id.imgPlantAvatar);
        imgGallery = findViewById(R.id.imgGallery);
        txtSave = findViewById(R.id.txtSave);
        edtPlanName = findViewById(R.id.edtPlanName);
        edtPlanNote = findViewById(R.id.edtPlanNote);
        rcvListCareSchedule = findViewById(R.id.rcvListCareSchedule);
        addCareSchedule = findViewById(R.id.addCareSchedule);
    }
}
