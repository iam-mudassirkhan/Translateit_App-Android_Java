package com.example.translateitapp.ui.TextDetectorActivity;

import androidx.appcompat.app.AppCompatActivity;


import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.translateitapp.interfaces.TranslatorInterface;
import com.example.translateitapp.ui.TranslatorFragment.TranslatorFragment;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;


import com.example.translateitapp.R;

import static com.example.translateitapp.ui.TranslatorFragment.TranslatorFragment.getLanguageCode;

public class TextdetectorActivity extends AppCompatActivity {

    LinearLayout topLayout;

    private Spinner fromSpinner, toSpinner;
    SharedPreferences LastSelect;
    SharedPreferences.Editor editor;

    EditText TVData;
    Button BtnCapture, BtnCopy;
    Bitmap bitmap;
    private static final int REQUEST_CAMERA_CODE = 100;


    String [] fromLanguage = {"From","English","Afrikaans","Arabic","Belarusian","Bulgarian","Bengali","Catalan","Chinese","Czech","Welsh","Danish","German","Greek","Esperanto","Spanish","Estonian","Persian","Finnish","French","Irish","Galician","Gujarati","Hebrew","Hindi","Croatian","Haitian","Hungarian","Indonesian","Icelandic","Italian","Japanese","Georgian","Kannada","Korean","Lithuanian","Latvian","Macedonian","Marathi","Malay","Maltese","Dutch","Norwegian","Polish", "Portuguese","Romanian", "Russian", "Slovak", "Slovenian", "Albanian", "Swedish" ,"Swahili", "Tamil", "Telugu" ,"Thai", "Tagalog" ,"Turkish" ,"Ukrainian" ,"Urdu", "Vietnamese"};

    String [] toLanguage = {"To","English","Afrikaans","Arabic","Belarusian","Bulgarian","Bengali","Catalan","Chinese","Czech","Welsh","Danish","German","Greek","Esperanto","Spanish","Estonian","Persian","Finnish","French","Irish","Galician","Gujarati","Hebrew","Hindi","Croatian","Haitian","Hungarian","Indonesian","Icelandic","Italian","Japanese","Georgian","Kannada","Korean","Lithuanian","Latvian","Macedonian","Marathi","Malay","Maltese","Dutch","Norwegian","Polish", "Portuguese","Romanian", "Russian", "Slovak", "Slovenian", "Albanian", "Swedish" ,"Swahili", "Tamil", "Telugu" ,"Thai", "Tagalog" ,"Turkish" ,"Ukrainian" ,"Urdu", "Vietnamese"};

    private static final int REQUEST_PERMISSION_CODE = 0 ;
    int languageCode, fromLanguageCode, toLanguageCode = 1;

    int LastClickFromSpinner;
    int LastClickToSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textdetector);
        TVData = findViewById(R.id.textData);
        BtnCapture = findViewById(R.id.BtnCapture);
        BtnCopy = findViewById(R.id.BtnCopy);

        toSpinner = findViewById(R.id.ToSpinner);

        topLayout=findViewById(R.id.topLayout);

        LastSelect = getSharedPreferences("LastSetting", Context.MODE_PRIVATE);
        editor = LastSelect.edit();




        int LastClickFromSpinner = LastSelect.getInt("LastClickFromSpinner", 0);
         int LastClickToSpinner = LastSelect.getInt("LastClickToSpinner", 0);


        TVData.setEnabled(false);

        if (ContextCompat.checkSelfPermission(TextdetectorActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TextdetectorActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
        }

        BtnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TVData.getText().toString().trim().isEmpty()){
                    TVData.setText("");
                }

                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(TextdetectorActivity.this);

            }
        });

        BtnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String scannedText = TVData.getText().toString();
                copyToClipBoard(scannedText);
            }
        });


        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toLanguageCode = getLanguageCode(toLanguage[i]);
                editor.putInt("LastClickToSpinner", i).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter toAdapter = new ArrayAdapter(TextdetectorActivity.this, R.layout.spinner_items, toLanguage);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);
        toSpinner.setSelection(LastClickToSpinner);



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                    getTextFromImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();


                }
            }


        }


    }

    private void getTextFromImage(Bitmap bitmap2) {
        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(TextdetectorActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap2).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");


            }



            if (!stringBuilder.toString().isEmpty()){
                topLayout.setVisibility(View.VISIBLE);
            }


            TVData.setText(stringBuilder.toString());

            findViewById(R.id.translateBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (toLanguageCode == 0){
                        Toast.makeText(getApplicationContext(), "Please Select The Target language to make Translation", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (TVData.getText().toString().trim().isEmpty()){
                        Toast.makeText(getApplicationContext(),"No Text found for translation!",Toast.LENGTH_LONG);
                        return;
                    }

                    TranslatorFragment.translateText(TextdetectorActivity.this, 11, toLanguageCode, TVData.getText().toString(), new TranslatorInterface() {
                        @Override
                        public void onStart(String s) {
                            TVData.setText(s);
                        }

                        @Override
                        public void onTranslation(String s) {
                            TVData.setText(s);
                        }

                        @Override
                        public void onTranslated(String s) {
                            TVData.setText(s);
                        }
                    });




                }
            });


            TVData.setEnabled(true);
            BtnCapture.setText("Retake");
            BtnCopy.setVisibility(View.VISIBLE);

        }
    }

    private void copyToClipBoard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Copied Data", text);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(TextdetectorActivity.this, "Copied to ClipBoard", Toast.LENGTH_SHORT).show();
    }
}
