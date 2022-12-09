 package com.example.translateitapp.ui.TranslatorFragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.translateitapp.R;
import com.example.translateitapp.interfaces.TranslatorInterface;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.CLIPBOARD_SERVICE;

public class TranslatorFragment extends Fragment {

//    private HomeViewModel homeViewModel;
    private Spinner fromSpinner, toSpinner;
    SharedPreferences LastSelect;
    SharedPreferences.Editor editor;
    private TextInputEditText sourceText;
    public ImageView mic, iconCopy, iconShare;
    private MaterialButton translationBtn;
    private TextView translationTV;

    String [] fromLanguage = {"From","English","Afrikaans","Arabic","Belarusian","Bulgarian","Bengali","Catalan","Chinese","Czech","Welsh","Danish","German","Greek","Esperanto","Spanish","Estonian","Persian","Finnish","French","Irish","Galician","Gujarati","Hebrew","Hindi","Croatian","Haitian","Hungarian","Indonesian","Icelandic","Italian","Japanese","Georgian","Kannada","Korean","Lithuanian","Latvian","Macedonian","Marathi","Malay","Maltese","Dutch","Norwegian","Polish", "Portuguese","Romanian", "Russian", "Slovak", "Slovenian", "Albanian", "Swedish" ,"Swahili", "Tamil", "Telugu" ,"Thai", "Tagalog" ,"Turkish" ,"Ukrainian" ,"Urdu", "Vietnamese"};

    String [] toLanguage = {"To","English","Afrikaans","Arabic","Belarusian","Bulgarian","Bengali","Catalan","Chinese","Czech","Welsh","Danish","German","Greek","Esperanto","Spanish","Estonian","Persian","Finnish","French","Irish","Galician","Gujarati","Hebrew","Hindi","Croatian","Haitian","Hungarian","Indonesian","Icelandic","Italian","Japanese","Georgian","Kannada","Korean","Lithuanian","Latvian","Macedonian","Marathi","Malay","Maltese","Dutch","Norwegian","Polish", "Portuguese","Romanian", "Russian", "Slovak", "Slovenian", "Albanian", "Swedish" ,"Swahili", "Tamil", "Telugu" ,"Thai", "Tagalog" ,"Turkish" ,"Ukrainian" ,"Urdu", "Vietnamese"};

    private static final int REQUEST_PERMISSION_CODE = 0 ;
    int languageCode, fromLanguageCode, toLanguageCode = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_translator, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);

        fromSpinner = root.findViewById(R.id.FromSpinner);
        toSpinner = root.findViewById(R.id.ToSpinner);
        sourceText = root.findViewById(R.id.EditSource);
        mic = root.findViewById(R.id.Mic);
        translationBtn = root.findViewById(R.id.BtnTranslation);
        translationTV = root.findViewById(R.id.TranslatedTv);

        iconCopy = root.findViewById(R.id.BtnCopy_icon);
        iconShare = root.findViewById(R.id.BtnShare_icon);
//        iconCopy.setColorFilter(getContext().getColor(R.color.purple_700));

//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
//************************************************************************************************//
//        This is the SharedPreferences Code to Save the Spinners Last Selected Value

        LastSelect = getActivity().getSharedPreferences("LastSetting", Context.MODE_PRIVATE);
        editor = LastSelect.edit();



        final int LastClickFromSpinner = LastSelect.getInt("LastClickFromSpinner", 0);
        final int LastClickToSpinner = LastSelect.getInt("LastClickToSpinner", 0);
//************************************************************************************************//

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
           fromLanguageCode = getLanguageCode(fromLanguage[i]);
           editor.putInt("LastClickFromSpinner", i).apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter fromAdapter = new ArrayAdapter(getActivity().getBaseContext(), R.layout.spinner_items, fromLanguage);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);
        fromSpinner.setSelection(LastClickFromSpinner);

        // To Language Spinner Code

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

        ArrayAdapter toAdapter = new ArrayAdapter(getActivity().getBaseContext(), R.layout.spinner_items, toLanguage);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);
        toSpinner.setSelection(LastClickToSpinner);

//************************************************************************************************************//

//        Mic Button Code to Translate Voice

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                 intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something to Translate");

                try {
                    startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                }
                catch
                (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

//************************************************************************************************************//
//        Translation Button Code to translate language from Source Language to target Language

        translationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translationTV.setVisibility(view.VISIBLE);
                iconCopy.setVisibility(view.VISIBLE);
                iconShare.setVisibility(view.VISIBLE);

                translationTV.setText("");
                if (sourceText.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Please Enter Text to Translate", Toast.LENGTH_SHORT).show();
                }
                else if (fromLanguageCode == 0){
                    Toast.makeText(getActivity(), "Please Select Source language", Toast.LENGTH_SHORT).show();
                }

                else if (toLanguageCode == 0){
                    Toast.makeText(getActivity(), "Please Select The Target language to make Translation", Toast.LENGTH_LONG).show();
                }

                else {
                    translateText(getActivity(), fromLanguageCode, toLanguageCode, sourceText.getText().toString(), new TranslatorInterface() {
                        @Override
                        public void onStart(String s) {
                            translationTV.setText(s);
                        }

                        @Override
                        public void onTranslation(String s) {
                            translationTV.setText(s);
                        }

                        @Override
                        public void onTranslated(String s) {
                            translationTV.setText(s);
                        }
                    });
                }

            }
        });

        iconCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (translationTV.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Text Not Found", Toast.LENGTH_SHORT).show();
                }

                else {
                    String TranslatedText = translationTV.getText().toString();
                    copyToClipBoard(TranslatedText);
                }

            }
        });

        iconShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (translationTV.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Nothing to Share!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = translationTV.getText().toString();
//                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
             }
        });

        return root;
    }

    public static void translateText(Activity activity,int fromLanguageCode, int toLanguageCode, String source, TranslatorInterface callBack) {
       callBack.onStart("Downloading Model. Please Wait...");
        //translationTV.setText("Downloading Model. Please Wait...");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();
        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                callBack.onTranslation("Translation...");
               // translationTV.setText("Translation...");

                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {


                      //  translationTV.setText(s);
                        callBack.onTranslated(s);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Failed to Translate! try again", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Failed to Download Model!! Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_CODE){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            sourceText.setText(result.get(0));
        }
    }

    //  String [] fromLanguage = {"From",
    //{"Afrikaans","Arabic","Belarusian","Bulgarian",
    // "Bengali","Catalan","Chinese","Czech","Welsh","Danish","German",
    // "Greek","English","Esperanto","Spanish","Estonian","Persian",
    // "Finnish","French","Irish","Galician","Gujarati","Hebrew","Hindi",
    // "Croatian","Haitian","Hungarian","Indonesian","Icelandic","Italian",
    // "Japanese","Georgian","Kannada","Korean","Lithuanian","Latvian",
    // "Macedonian","Marathi","Malay","Maltese","Dutch","Norwegian","Polish",
    // "Portuguese","Romanian", "Russian", "Slovak", "Slovenian", "Albanian",
    // "Swedish" ,"Swahili", "Tamil", "Telugu" ,"Thai", "Tagalog" ,"Turkish" ,
    // "Ukrainian" ,"Urdu", "Vietnamese"};
    public static int getLanguageCode(String language) {
        int languageCode = 0;
        switch (language){

            case "English":
                languageCode = FirebaseTranslateLanguage.EN;
                break;

            case "Afrikaans":
                languageCode = FirebaseTranslateLanguage.AF;
                break;

            case "Arabic":
                languageCode = FirebaseTranslateLanguage.AR;
                break;


            case "Belorussian":
                languageCode = FirebaseTranslateLanguage.BE;
                break;

            case "Bulgarian":
                languageCode = FirebaseTranslateLanguage.BG;
                break;

            case "Bengali":
                languageCode = FirebaseTranslateLanguage.BN;
                break;

            case "Catalan":
                languageCode = FirebaseTranslateLanguage.CA;
                break;

            case "Czech":
                languageCode = FirebaseTranslateLanguage.CS;
                break;

            case "Walsh":
                languageCode = FirebaseTranslateLanguage.CY;
                break;

            case "Danish":
                languageCode = FirebaseTranslateLanguage.DA;
                break;

            case "German":
                languageCode = FirebaseTranslateLanguage.DE;
                break;

            case "Greek":
                languageCode = FirebaseTranslateLanguage.EL;
                break;

            case "Esperanto":
                languageCode = FirebaseTranslateLanguage.EO;
                break;

            case "Spanish":
                languageCode = FirebaseTranslateLanguage.ES;
                break;

            case "Estonian":
                languageCode = FirebaseTranslateLanguage.ET;
                break;

            case "Persian":
                languageCode = FirebaseTranslateLanguage.FA;
                break;

            case "Finnish":
                languageCode = FirebaseTranslateLanguage.FI;
                break;

            case "French":
                languageCode = FirebaseTranslateLanguage.FR;
                break;

            case "Irish":
                languageCode = FirebaseTranslateLanguage.GA;
                break;

            case "Galician":
                languageCode = FirebaseTranslateLanguage.GL;
                break;

            case "Gujarati":
                languageCode = FirebaseTranslateLanguage.GU;
                break;

            case "Hebrew":
                languageCode = FirebaseTranslateLanguage.HE;
                break;

            case "Hindi":
                languageCode = FirebaseTranslateLanguage.HI;
                break;

            case "Croatian":
                languageCode = FirebaseTranslateLanguage.HR;
                break;

            case "Haitian":
                languageCode = FirebaseTranslateLanguage.HT;
                break;

            case "Hungarian":
                languageCode = FirebaseTranslateLanguage.HU;
                break;

            case "Indonesian":
                languageCode = FirebaseTranslateLanguage.ID;
                break;

            case "Icelandic":
                languageCode = FirebaseTranslateLanguage.IS;
                break;

            case "Italian":
                languageCode = FirebaseTranslateLanguage.IT;
                break;

            case "Japanese":
                languageCode = FirebaseTranslateLanguage.JA;
                break;

            case "Georgian":
                languageCode = FirebaseTranslateLanguage.KA;
                break;

            case "Kannada":
                languageCode = FirebaseTranslateLanguage.KN;
                break;

            case "Korean":
                languageCode = FirebaseTranslateLanguage.KO;
                break;

            case "Lithuanian":
                languageCode = FirebaseTranslateLanguage.LT;
                break;

            case "Latvian":
                languageCode = FirebaseTranslateLanguage.LV;
                break;

            case "Macedonian":
                languageCode = FirebaseTranslateLanguage.MK;
                break;

            case "Marathi":
                languageCode = FirebaseTranslateLanguage.MR;
                break;

            case "Malay":
                languageCode = FirebaseTranslateLanguage.MS;
                break;

            case "Maltese":
                languageCode = FirebaseTranslateLanguage.MT;
                break;

            case "Dutch":
                languageCode = FirebaseTranslateLanguage.NL;
                break;

            case "Norwegian":
                languageCode = FirebaseTranslateLanguage.NO;
                break;

            case "Polish":
                languageCode = FirebaseTranslateLanguage.PL;
                break;

            case "Portuguese":
                languageCode = FirebaseTranslateLanguage.PT;
                break;

            case "Romanian":
                languageCode = FirebaseTranslateLanguage.RO;
                break;

            case "Russian":
                languageCode = FirebaseTranslateLanguage.RU;
                break;

            case "Slovak":
                languageCode = FirebaseTranslateLanguage.SK;
                break;

            case "Slovenian":
                languageCode = FirebaseTranslateLanguage.SL;
                break;

            case "Albanian":
                languageCode = FirebaseTranslateLanguage.SQ;
                break;

            case "Swedish":
                languageCode = FirebaseTranslateLanguage.SV;
                break;

            case "Swahili":
                languageCode = FirebaseTranslateLanguage.SW;
                break;

            case "Tamil":
                languageCode = FirebaseTranslateLanguage.TA;
                break;

            case "Telugu":
                languageCode = FirebaseTranslateLanguage.TE;
                break;

            case "Thai":
                languageCode = FirebaseTranslateLanguage.TH;
                break;

            case "Tagalog":
                languageCode = FirebaseTranslateLanguage.TL;
                break;

            case "Turkish":
                languageCode = FirebaseTranslateLanguage.TR;
                break;

            case "Ukrainian":
                languageCode = FirebaseTranslateLanguage.UK;
                break;

            case "Urdu":
                languageCode = FirebaseTranslateLanguage.UR;
                break;

            case "Vietnamese":
                languageCode = FirebaseTranslateLanguage.VI;
                break;

            case "Chinese":
                languageCode = FirebaseTranslateLanguage.ZH;
                break;





            default: languageCode = 0;

        }
        return languageCode;
    }

    private void copyToClipBoard(String text){
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Copied Data", text);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(getActivity(), "Copied to ClipBoard", Toast.LENGTH_SHORT).show();
    }
}