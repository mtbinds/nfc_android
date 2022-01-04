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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    NfcAdapter adapter;
    PendingIntent mPendingIntent;
    LinkedList<Student> students;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
       students.add(new Student("4231fba3e6280","Chaourar ","IMINE\n"));
       students.add(new Student("41a5522245e80","Yugurten","MERZOUK\n"));

       Gson gson = new Gson();

       String json = gson.toJson(students);

       editor.putString("students", json);


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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getTagInfo(intent);
    }

    private void getTagInfo(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] uid = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);

        String s = new BigInteger(uid).toString(16);
        afficherMessage(s);

    }


    public void afficherMessage(String s) {

       //test read shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        TextView t= findViewById(R.id.id);

        //____________
        String jsonStudents = sharedPref.getString("students","");
        Gson gson = new Gson();
        Type type = new TypeToken<LinkedList<Student>>() {}.getType();
        LinkedList<Student> students = gson.fromJson(jsonStudents, type);


        t.setText(students.toString());
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

    }



}
