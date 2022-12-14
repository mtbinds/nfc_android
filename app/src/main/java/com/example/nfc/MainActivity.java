package com.example.nfc;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
//import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
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
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;

import com.example.nfc.models.Exam;
import com.example.nfc.models.Student;
import com.example.nfc.models.StudentTime;
import com.example.nfc.pdf.PDFUtility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, PDFUtility.OnDocumentClose{

    NfcAdapter adapter;
    PendingIntent mPendingIntent;
    LinkedList<Student> students;
    LinkedList<Exam> exams;
    Spinner examSpinner;


    //_________
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String TAG = MainActivity.class.getSimpleName();
    private AppCompatEditText rowCount;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //pdf



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
        students.add(new Student("4231fba3e6280", "Chaourar ", "IMINE"));
        students.add(new Student("41a5522245e80", "Yugurten", "MERZOUK"));


        //create list of exams
        exams = new LinkedList<>();


        String pattern = "yyyMMdd-HHmmss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String androidStartDate = simpleDateFormat.format(new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 2)));
        String androidEndDate = simpleDateFormat.format(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 2)));


        exams.add(new Exam("Android", androidStartDate, androidEndDate));
        exams.add(new Exam("Graph", this.getDate("20220107-123000"), this.getDate("20220107-143000")));
        exams.add(new Exam("Statistics", this.getDate("20220108-123000"), this.getDate("20220108-143000")));
        exams.add(new Exam("Machine Learning", this.getDate("20220129-123000"), this.getDate("20220129-143000")));


        Gson gson = new Gson();
        String json = gson.toJson(students);
        editor.putString("students", json);

        String jsonExams = gson.toJson(exams);
        editor.putString("exams", jsonExams);


        editor.apply();


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDate(String s){
        DateTimeFormatter f3 = DateTimeFormatter.ofPattern(s) ;
        String end_date1 = LocalDateTime.now().format(f3);
        return  end_date1;
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

        //make liner invisible
        LinearLayout linearinfo = findViewById(R.id.linear1);
        linearinfo.setVisibility(View.INVISIBLE);

        //test read shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();


        Gson gson = new Gson();
        //get students
        String jsonStudents = sharedPref.getString("students", "");
        Type type = new TypeToken<LinkedList<Student>>() {
        }.getType();
        LinkedList<Student> students = gson.fromJson(jsonStudents, type);

        //get exams
        String jsonExams = sharedPref.getString("exams", "");
        Type typeExams = new TypeToken<LinkedList<Exam>>() {
        }.getType();
        LinkedList<Exam> exams = gson.fromJson(jsonExams, typeExams);


        //get spinner Exam
        Exam examNow = exams.stream().filter(e -> e.getModule().equals(examSpinner.getSelectedItem().toString())).collect(Collectors.toCollection(LinkedList::new)).get(0);


        // Getting the string

        String dateStartExam = examNow.getStart_date();
        long dateStart = this.dateToMiliseconds(dateStartExam);

        String dateEndExam = examNow.getEnd_date();
        long dateEnd = this.dateToMiliseconds(dateEndExam);

        long currentTime = System.currentTimeMillis();

        //get current date
        String pattern = "yyyMMdd-HHmmss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String scanDate = simpleDateFormat.format(new Date(System.currentTimeMillis()));


        if (dateStart <= currentTime && currentTime <= dateEnd) {


            boolean find = false;
            for (int i = 0; i < students.size(); i++) {
                //if the student exists, then add him to the exam's students list
                Student student = students.get(i);
                if (s.equals(student.getId())) {
                    find = true;

                    //add the student to the list


                    //first look if this student has scaned before
                    Student studentScan = examNow.getStudentsTime().stream().filter(e -> e.getId().equals(s)).findFirst().orElse(null);

                    Log.i("first", "first");
                    //the first scan
                    if (studentScan == null) {
                        examNow.getStudentsTime().add(new StudentTime(student.getId(), student.getFirstName(), student.getName(), scanDate, ""));
                        Log.i("first", "first");
                        Toast.makeText(this, student.getFullName()+ " : 1", Toast.LENGTH_LONG).show();

                    } else {

                        Log.i("second", "second");
                        //modify this student endTime
                        LinkedList<StudentTime> studentsTime = examNow.getStudentsTime();
                        int index = IntStream.range(0, studentsTime.size())
                                .filter(j -> studentsTime.get(j).getId().equals(s))
                                .findFirst()
                                .orElse(-1);

                        //the second scan must be 10 min after teh first
                        if (System.currentTimeMillis() > this.dateToMiliseconds(studentsTime.get(index).getDateIn()) + (1000*60)) {
                            if (studentsTime.get(index).getDateOut().equals("")) {
                                Toast.makeText(this, student.getFullName()+ " : 2", Toast.LENGTH_LONG).show();
                                studentsTime.get(index).setDateOut(scanDate);
                                //set StudentsTimeList
                                examNow.setStudentsTime(studentsTime);
                            } else {//scan 3

                                Toast.makeText(this, studentsTime.get(index).getFullName() + " has scanned twice!!", Toast.LENGTH_LONG).show();

                            }
                        }else{
                            Toast.makeText(this, studentsTime.get(index).getFullName() + "   wait minimum 1 minute!!", Toast.LENGTH_LONG).show();

                        }


                    }


                    break;
                }
            }


            //if the student doesn't exist, ther enter full name
            if (find == false) {
                Toast.makeText(this, "Student not found!  ", Toast.LENGTH_LONG).show();


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

                        if (firstName.length() <= 2 || name.length() <= 2) {
                            Toast.makeText(MainActivity.this, "please enter your name ", Toast.LENGTH_LONG).show();
                        } else {
                            //add the student to out database
                            students.add(new Student(s, firstName, name));
                            String json = gson.toJson(students);
                            editor.putString("students", json);
                            editor.apply();
                            linearinfo.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "student added successfully ", Toast.LENGTH_LONG).show();

                            //add student to exam list
                            examNow.getStudentsTime().add(new StudentTime(s, firstName, name, scanDate, ""));


                        }


                    }
                });

            }//end if find

        }  else if(currentTime < dateStart) {

            //on peu commencer ?? scanner a partir de la date du debut de l'examen
            // ( on consid??re que l'examen commence 30 min apres la date du debut choisi pour avoir le temps de scanner les etudiant avant le debut de l'examen)
            //exemple : si un examen commence ?? 10h00, date d??but sera donc 9h30
            java.util.Date date = new SimpleDateFormat("yyyyMMdd-HHmmss")
                    .parse(dateStartExam);
            Toast.makeText(MainActivity.this, "You can not scan for this exam till :\n " + date, Toast.LENGTH_LONG).show();

        } else{
                Toast.makeText(MainActivity.this,"This exam is in the past.", Toast.LENGTH_LONG).show();
            }



        //set this Exam inside  examsLinkedList
        int index = IntStream.range(0, exams.size())
                .filter(j -> exams.get(j).getModule().equals(examSpinner.getSelectedItem().toString()))
                .findFirst()
                .orElse(-1);


        exams.set(index, examNow);
        //refresh shared Preferences by changing exams
//        Log.i("imine",examNow.getStudentsTime().toString());
        Log.i("imine", exams.toString());
        String json = gson.toJson(exams);
        editor.putString("exams", json);
        editor.apply();

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(),
                getResources().getStringArray(R.array.exams_spinner)[position],
                Toast.LENGTH_SHORT)
                .show();

        TextView t = findViewById(R.id.id);
        t.setText(getResources().getStringArray(R.array.exams_spinner)[position]+" Exam checking cards:");

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void spinnerParameters(Spinner spinner, ArrayAdapter<CharSequence> adapter) {
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

        String sDate1 = year + "/" + month + "/" + day + " " + hour + ":" + minutes + ":" + miliseconds;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        long date = sdf.parse(sDate1).getTime();

        return date;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pdf(View view){

        //get spinner Exam
        Exam examNow = exams.stream().filter(e -> e.getModule().equals(examSpinner.getSelectedItem().toString())).collect(Collectors.toCollection(LinkedList::new)).get(0);

        String path = Environment.getExternalStorageDirectory().toString() + "/Documents/Examen"+examNow.getModule()+".pdf";
        Log.i("Files", "Path: " + path);
        MainActivity.verifyStoragePermissions(this);
        try
        {
            Log.e("path",path);
            PDFUtility.createPdf(this,MainActivity.this,getSampleData(),path,true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("TAG","Error Creating Pdf");
            Toast.makeText(this,"No student is in --> No PDF",Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onPDFDocumentClose(File file)
    {
        Toast.makeText(this,"Sample Pdf Created",Toast.LENGTH_SHORT).show();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<String[]> getSampleData() throws ParseException {
        /*int count = 20;
        if(!TextUtils.isEmpty(rowCount.getText()))
        {
            count = Integer.parseInt(rowCount.getText().toString());
        }*/
        //test read shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();


        Gson gson = new Gson();

        //get exams
        String jsonExams = sharedPref.getString("exams", "");
        Type typeExams = new TypeToken<LinkedList<Exam>>() {
        }.getType();
        LinkedList<Exam> exams = gson.fromJson(jsonExams, typeExams);

        //get spinner Exam
        Exam examNow = exams.stream().filter(e -> e.getModule().equals(examSpinner.getSelectedItem().toString())).collect(Collectors.toCollection(LinkedList::new)).get(0);


        List<String[]> temp = new ArrayList<>();
        for (StudentTime std:examNow.getStudentsTime())
        {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
            Date dateIn = new Date(this.dateToMiliseconds(std.getDateIn()));

            String end = "Not yet";
            if(!std.getDateOut().equals("")){
                Date dateOut = new Date(this.dateToMiliseconds(std.getDateOut()));
                end= df.format(dateOut);
            }
            temp.add(new String[] {std.getFullName(), df.format(dateIn), end, examNow.getModule()});
        }
        return  temp;
    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}

