package pl.design.mrn.matned.dogmanagementapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import pl.design.mrn.matned.dogmanagementapp.ImageAdvancedFunction;
import pl.design.mrn.matned.dogmanagementapp.activity.dataactivity.edit.BreedingActivityEdit;
import pl.design.mrn.matned.dogmanagementapp.activity.dataactivity.edit.DataChoiceListActivityEdit;
import pl.design.mrn.matned.dogmanagementapp.listeners.PositionListener;
import pl.design.mrn.matned.dogmanagementapp.R;
import pl.design.mrn.matned.dogmanagementapp.dataBase.Sex;
import pl.design.mrn.matned.dogmanagementapp.dataBase.dog.DogDao;
import pl.design.mrn.matned.dogmanagementapp.dataBase.dog.DogModel;
import pl.design.mrn.matned.dogmanagementapp.dataBase.dog.Validate;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static pl.design.mrn.matned.dogmanagementapp.ImageAdvancedFunction.setImage;
import static pl.design.mrn.matned.dogmanagementapp.Statics.BREEDING;
import static pl.design.mrn.matned.dogmanagementapp.Statics.CHIP;
import static pl.design.mrn.matned.dogmanagementapp.Statics.DATE_FORMAT;
import static pl.design.mrn.matned.dogmanagementapp.Statics.LIST_ELEMENT_ACTIVITY;
import static pl.design.mrn.matned.dogmanagementapp.Statics.NOTE;
import static pl.design.mrn.matned.dogmanagementapp.Statics.OWNER;
import static pl.design.mrn.matned.dogmanagementapp.Statics.SIGN;
import static pl.design.mrn.matned.dogmanagementapp.Statics.TATTOO;
import static pl.design.mrn.matned.dogmanagementapp.TextStrings_PL.ADD_DOG_SEX;
import static pl.design.mrn.matned.dogmanagementapp.TextStrings_PL.NOT_WORKING_YET;
import static pl.design.mrn.matned.dogmanagementapp.TextStrings_PL.WRONG_DATE;

public class Edit_DogActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText dogNameET;
    private EditText dogRaceET;
    private EditText dogBirthDatePickerET;
    private EditText dogColorET;
    private Spinner dogSexET;
    private Button chipBtn;
    private Button tattooBtn;
    private Button signsBtn;
    private Button notesBtn;
    private Button breedingBtn;
    private Button ownerBtn;
    private Button saveDog;
    private Button cancel;
    private ImageView dogPhoto;

    private Button deleteDogBtn;
    private Button galleryPicture;
    private Bitmap bitmap;

    private String photoPath;
    private String dogName;
    private String dogRace;
    private String dogBirthDatePicker;
    private String dogColor;

    @SuppressLint("SimpleDateFormat")
    private DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    private Uri photoUri;

    private DogModel dog;
    private DogDao dogDao;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dogDao = new DogDao(this);
        dog = dogDao.findById(PositionListener.getInstance().getSelectedDogId());
        setContentView(R.layout.edit_dog);
        initializeFields_edit();
        onSavedReload(savedInstanceState);
        bitmap = null;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initializeFields_edit() {
        dogNameET = findViewById(R.id.dogNameEdit);
        dogRaceET = findViewById(R.id.dogRaceEdit);
        dogBirthDatePickerET = findViewById(R.id.dogDateOfBirthEdit);
        dogColorET = findViewById(R.id.dogColorEdit);
        dogSexET = findViewById(R.id.dogSexEdit);
        chipBtn = findViewById(R.id.chipBtnEdit);
        tattooBtn = findViewById(R.id.tattooBtnEdit);
        signsBtn = findViewById(R.id.signsBtnEdit);
        ownerBtn = findViewById(R.id.ownerBtnEdit);
        notesBtn = findViewById(R.id.notesBtnEdit);
        breedingBtn = findViewById(R.id.breedingBtnEdit);
        saveDog = findViewById(R.id.saveDogEdit);
        deleteDogBtn = findViewById(R.id.deleteDogEdit);
        cancel = findViewById(R.id.cancelButtonEdit);
        galleryPicture = findViewById(R.id.editDogDialogPictureFromGalleryEdit);
        dogPhoto = findViewById(R.id.photoEdit);
        initSpinner();
        initDatePicker();
        initOnClickListeners_edit();
        fillAllFields_edit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fillAllFields_edit() {
        dogNameET.setText(dog.getName());
        dogRaceET.setText(dog.getRace());
        dogBirthDatePickerET.setText(dateFormat.format(dog.getBirthDate()));
        dogColorET.setText(dog.getColor());
        if (dog.getDogImage() != null)
            setImage(dogPhoto, dog.getDogImage(), this, getResources());
        else {
            Drawable drawable;
            drawable = getResources().getDrawable(R.drawable.ic_stat_name);
            dogPhoto.setImageDrawable(drawable);
        }
        dogSexET.setSelection(Sex.getPosition(dog.getSex()));
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            dogPhoto.setOnClickListener(addPhoto());
        }
    }

    private void initOnClickListeners_edit() {
        galleryPicture.setOnClickListener(v ->
                Toast.makeText(this, NOT_WORKING_YET, Toast.LENGTH_SHORT).show());
        chipBtn.setOnClickListener(v -> showDial(DataChoiceListActivityEdit.class, CHIP));
        tattooBtn.setOnClickListener(v -> showDial(DataChoiceListActivityEdit.class, TATTOO));
        signsBtn.setOnClickListener(v -> showDial(DataChoiceListActivityEdit.class, SIGN));
        ownerBtn.setOnClickListener(v -> showDial(DataChoiceListActivityEdit.class, OWNER));
        notesBtn.setOnClickListener(v -> showDial(DataChoiceListActivityEdit.class, NOTE));
        breedingBtn.setOnClickListener(v -> showDial(BreedingActivityEdit.class, BREEDING));
        saveDog.setOnClickListener(changeActivityOnClick_saveEdit());
        deleteDogBtn.setOnClickListener(changeActivityOnClick_deleteEdit());
        cancel.setOnClickListener(v -> finish());
    }

    public void showDial(Class clazz, String el) {
        Intent intent = new Intent(Edit_DogActivity.this, clazz);
        intent.putExtra(LIST_ELEMENT_ACTIVITY, el);
        startActivity(intent);
    }


    private View.OnClickListener changeActivityOnClick_deleteEdit() {
        return v -> {
            showAlertButton();
        };
    }    protected void showAlertButton(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Czy na pewno skasować?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Tak",
                (dialog, id) -> {
                    deleteRecord();
                    finish();
                });
        builder1.setNegativeButton(
                "Nie",
                (dialog, id) -> dialog.cancel());
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void deleteRecord() {
        dogDao.remove(dog);
        setResult(202);
        finish();
    }


    private View.OnClickListener changeActivityOnClick_saveEdit() {
        return v -> {
            if (validation()) {
                getDogModelFromFields();
                dogDao.update(dog);
                setResult(200);
                finish();
            }
        };
    }

    private void getDogModelFromFields() {
        dog.setName(dogNameET.getText().toString());
        dog.setRace(dogRaceET.getText().toString());
        try {
            dog.setBirthDate(dateFormat.parse(dogBirthDatePickerET.getText().toString()));
        } catch (ParseException e) {
            Toast.makeText(this, WRONG_DATE, Toast.LENGTH_SHORT).show();
        }
        dog.setColor(dogColorET.getText().toString());
        dog.setSex(Sex.valueOf(dogSexET.getSelectedItem().toString()));
        dog.setDogImage(photoPath);
    }


    private void onSavedReload(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            dogName = savedInstanceState.getString("NAME");
            if (dogName != null) dogNameET.setText(dogName);
            dogRace = savedInstanceState.getString("RACE");
            if (dogRace != null) dogRaceET.setText(dogName);
            dogBirthDatePicker = savedInstanceState.getString("BIRTH_DATE");
            if (dogBirthDatePicker != null) dogBirthDatePickerET.setText(dogName);
            dogColor = savedInstanceState.getString("COLOR");
            if (dogColor != null) dogColorET.setText(dogName);
            int dogSex = 0;
            try {
                dogSex = savedInstanceState.getInt("SEX");
            } catch (NullPointerException ignored) {
            }
            dogSexET.setSelection(dogSex);
            if (photoPath != null) {
                photoUri = FileProvider.getUriForFile(Edit_DogActivity.this, "pl.design.mrn.matned.dogmanagementapp.fileprovider", new File(photoPath));
                showImage();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("NAME", dogName);
        outState.putString("RACE", dogRace);
        outState.putString("BIRTH_DATE", dogBirthDatePicker);
        outState.putString("COLOR", dogColor);
        outState.putInt("SEX", dogSexET.getSelectedItemPosition());
        outState.putString("PHOTO_PATH", photoPath);
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
        dateFormat.setTimeZone(cal.getTimeZone());
        dogBirthDatePickerET.setText(dateFormat.format(cal.getTime()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                showImage();
            }
        }
    }


    private void showImage() {
        try {
            bitmap = ImageAdvancedFunction.handleSamplingAndRotationBitmap(Edit_DogActivity.this, photoUri);
            Drawable d = new BitmapDrawable(getResources(), bitmap);
            dogPhoto.setImageDrawable(d);
            dogPhoto.setBackgroundResource(R.drawable.roundcornerimageframeblack);

        } catch (IOException e) {
            Log.d("myLog", "Except : " + e.toString());
        }

    }

    private View.OnClickListener addPhoto() {
        return v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                photoFile = createImageFile();

                photoPath = photoFile.getAbsolutePath();
                photoUri = FileProvider.getUriForFile(Edit_DogActivity.this, "pl.design.mrn.matned.dogmanagementapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        };
    }


    private boolean validation() {
        checkET(dogNameET);
        checkET(dogRaceET);
        checkET(dogColorET);
        checkSpinner(dogSexET);
        checkDate(dogBirthDatePickerET);
        return checkET(dogNameET) && checkET(dogRaceET) && checkET(dogColorET) && checkSpinner(dogSexET) && checkDate(dogBirthDatePickerET);
    }

    private boolean checkDate(EditText et) {
        if (!Validate.dateFormat(et.getText().toString())) {
            et.setBackgroundResource(R.drawable.roundcornerstextred);
            return false;
        } else {
            et.setBackgroundResource(R.drawable.roundcornerstext);
            return true;
        }
    }

    private boolean checkSpinner(Spinner et) {
        if (!Validate.selectedSexIn(et)) {
            et.setBackgroundResource(R.drawable.roundcornerstextred);
            return false;
        } else {
            et.setBackgroundResource(R.drawable.roundcornerstext);
            return true;
        }
    }

    private boolean checkET(EditText et) {
        if (!Validate.notEmpty(et.getText().toString())) {
            et.setBackgroundResource(R.drawable.roundcornerstextred);
            return false;
        } else {
            et.setBackgroundResource(R.drawable.roundcornerstext);
            return true;
        }
    }

    private void initDatePicker() {
        dogBirthDatePickerET.setOnClickListener(v -> datePickDialog());
        dogBirthDatePickerET.setRawInputType(InputType.TYPE_NULL);
        dogBirthDatePickerET.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) datePickDialog();
        });
    }

    private void initSpinner() {
        String[] sexes = new String[3];
        sexes[0] = ADD_DOG_SEX;
        sexes[1] = Sex.PIES.name();
        sexes[2] = Sex.SUKA.name();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.drpdn, sexes);
        dogSexET.setAdapter(adapter);
    }

    private void datePickDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(Edit_DogActivity.this, Edit_DogActivity.this, year, month, day);
        datePicker.show();
    }


    private File createImageFile() {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "dogPhoto_" + timeStamp + "_";
        File storageDir = getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
        File image = null;
        image = new File(storageDir.getParent() + imageFileName + ".jpg");
        return image;
    }

}
