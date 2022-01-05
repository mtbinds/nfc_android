package com.example.nfc;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
//import android.support.v7.app.AppCompatActivity;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    NfcAdapter adapter;
    PendingIntent mPendingIntent;
    LinkedList<Student> students;
    LinkedList<Exam> exams;
    Spinner examSpinner;

   @RequiresApi(api = Build.VERSION_CODES.O)
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add exams' spinner
       // Create an ArrayAdapter using the string array and a default spinner layout
       ArrayAdapter<CharSequence> adapterShapes = ArrayAdapter.createFromResource(this,
               R.array.exams_spinner, android.R.layout.simple_spinner_item);

       examSpinner = findViewById(R.id.examsSpinner);
       spinnerParameters(examSpinner, adapterShapes);



       Intent intent;
        intent = this.getIntent();

        NfcManager manager = (NfcManager) this.getSystemService(Context.NFC_SERVICE);
        adapter = manager.getDefaultAdapter();

        if (adapter != null) {

            if (adapter.isEnabled()) {
                if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
                    Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                }
            }


            mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                    getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        }


        //sharedPreferneces
       SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
       SharedPreferences.Editor editor = sharedPref.edit();


       //create list of students
       students = new LinkedList();
       students.add(new Student("4231fba3e6280","Chaourar ","IMINE"));
       students.add(new Student("41a5522245e80","Yugurten","MERZOUK"));



       //create list of exams
       exams = new LinkedList<>();
       //String pattern = "yyyyMMdd-HHmmss";
       //SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);


       //date et heure du début de l'examen : Android
       String input = "20220105-100000" ;  // yyyymmddhhmmss
       DateTimeFormatter f = DateTimeFormatter.ofPattern(input) ;
       String start_date = LocalDateTime.now().format(f);

       //date et heure de fin de l'xamen : Android
       String input1 = "20220105-120000" ;  // yyyymmddhhmmss
       DateTimeFormatter f1 = DateTimeFormatter.ofPattern(input1) ;
       String end_date = LocalDateTime.now().format(f1); ;

       //date et heure du début de l'examen : graph
       String input2 = "20220105-123000" ;  // yyyymmddhhmmss
       DateTimeFormatter f2 = DateTimeFormatter.ofPattern(input2) ;
       String start_date1 = LocalDateTime.now().format(f2);

       //date et heure de fin de l'xamen : graph
       String input3 = "20220105-143000" ;  // yyyymmddhhmmss
       DateTimeFormatter f3 = DateTimeFormatter.ofPattern(input3) ;
       String end_date1 = LocalDateTime.now().format(f3); ;



       //String start_date = simpleDateFormat.format(new Date(System.currentTimeMillis()-(1000*60*60*2)));
       //String end_date = simpleDateFormat.format(new Date(System.currentTimeMillis()+(1000*60*60*2)));


       exams.add(new Exam("Android", start_date, end_date));
       exams.add(new Exam("Graph", start_date1, end_date1));
       Log.i("aaa", exams.toString());


       Gson gson = new Gson();
       String json = gson.toJson(students);
       editor.putString("students", json);

       String jsonExams = gson.toJson(exams);
       editor.putString("exams", jsonExams);


       editor.apply();



   }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adapter != null) {
            adapter.disableForegroundDispatch(this);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            getTagInfo(intent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getTagInfo(Intent intent) throws ParseException {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] uid = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);

        String s = new BigInteger(uid).toString(16);
        afficherMessage(s);

    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    public void afficherMessage(String s) throws ParseException {


       //test read shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();



        Gson gson = new Gson();
        //get students
        String jsonStudents = sharedPref.getString("students","");
        Type type = new TypeToken<LinkedList<Student>>() {}.getType();
        LinkedList<Student> students = gson.fromJson(jsonStudents, type);

        //get exams
        String jsonExams = sharedPref.getString("exams","");
        Type typeExams = new TypeToken<LinkedList<Exam>>() {}.getType();
        LinkedList<Exam> exams = gson.fromJson(jsonExams, typeExams);


        //Log.i("aaa", exams.toString());

        //get spinner Exam
        Exam examNow = exams.stream().filter(e -> e.getModule().equals(examSpinner.getSelectedItem().toString())).collect(Collectors.toCollection(LinkedList::new)).get(0);



        // Getting the string

        String dateStartExam  = examNow.getStart_date();
        long dateStart = this.dateToMiliseconds(dateStartExam);

        String dateEndExam = examNow.getEnd_date();
        long dateEnd = this.dateToMiliseconds(dateEndExam);

        long currentTime = System.currentTimeMillis();




    if(dateStart <= currentTime && currentTime<= dateEnd){

        boolean find = false;
        for(int i =0; i < students.size(); i++) {
            if(s.equals(students.get(i).getId())) {
                find = true;

                Toast.makeText(this, s+"   "+ students.get(i).getFullName(), Toast.LENGTH_LONG).show();
                break;
            }
        }

        if(find==false){
            Toast.makeText(this,"Student not found!  ", Toast.LENGTH_LONG).show();

            LinearLayout linearinfo = findViewById(R.id.linear1);
            linearinfo.setVisibility(View.VISIBLE);

            Button submit = findViewById(R.id.submit);

            //add the student if not exists
            submit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Do something in response to button click
                    EditText nameEdit = findViewById(R.id.nameEditText);
                    String name = nameEdit.getText().toString();

                    EditText firstNameEdit = findViewById(R.id.firstNameEditText);
                    String firstName = firstNameEdit.getText().toString();

                    if(firstName.length()<=2 || name.length()<=2){
                        Toast.makeText(MainActivity.this,"please enter your name ", Toast.LENGTH_LONG).show();
                    }else{
                        //add the student to out database
                        students.add(new Student(s,firstName, name));
                        String json = gson.toJson(students);
                        editor.putString("students", json);
                        editor.apply();
                        linearinfo.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this,"student added successfully ", Toast.LENGTH_LONG).show();
                    }


                }
            });



        }



        }else{

        //on peu commencer à scanner a partir de la date du debut de l'examen
        // ( on considére que l'examen commence 30 min apres la date du debut choisi pour avoir le temps de scanner les etudiant avant le debut de l'examen)
        //exemple : si un examen commence à 10h00, date début sera donc 9h30
        java.util.Date date = new SimpleDateFormat("yyyyMMdd-HHmmss")
                .parse(dateStartExam);
            Toast.makeText(MainActivity.this,"You can not scan for this exam till :\n " +date, Toast.LENGTH_LONG).show();

        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(),
                getResources().getStringArray(R.array.exams_spinner)[position],
                Toast.LENGTH_SHORT)
                .show();

        TextView t= findViewById(R.id.id);
        t.setText(getResources().getStringArray(R.array.exams_spinner)[position]);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void spinnerParameters(Spinner spinner, ArrayAdapter<CharSequence> adapter){
        // Take the instance of Spinner and
        // apply OnItemSelectedListener on it which
        // tells which item of spinner is clicked
        spinner.setOnItemSelectedListener(this);


        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

    }



    public long dateToMiliseconds(String dateEndExam) throws ParseException {

        String year = dateEndExam.substring(0, 4);
        String month = dateEndExam.substring(4, 6);
        String day = dateEndExam.substring(6, 8);
        String hour = dateEndExam.substring(9, 11);
        String minutes = dateEndExam.substring(11, 13);
        String miliseconds = dateEndExam.substring(13, 15);

        String sDate1=year+"/"+month+"/"+day+" "+hour+":"+minutes+":"+miliseconds;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        long date = sdf.parse(sDate1 ).getTime();

        return  date;
    }

}

