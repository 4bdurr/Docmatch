package com.example.dogmatch2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private ArrayList<Doctor> doctorList;
    private ArrayAdapter<Doctor> arrayAdapter;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        TextView welcomeText = findViewById(R.id.idUser);
        welcomeText.setText(email);

        // Initialize swipe pager data
        doctorList = new ArrayList<>();
        doctorList.add(new Doctor("Dr. John Doe", "Cardiologist", 1.2, "https://betanews.com/wp-content/uploads/2011/11/Doctor-e1321803527823.jpg"));
        doctorList.add(new Doctor("Dr. Jane Smith", "Nurologist", 0.5, "https://cdn.pixabay.com/photo/2017/03/14/03/20/woman-2141808_1280.jpg"));
        doctorList.add(new Doctor("Dr. Emily Davis", "Pediatrician", 3.1, "https://cdn.pixabay.com/photo/2020/11/02/22/34/doctor-5708102_960_720.jpg"));
        doctorList.add(new Doctor("Dr. Emilia", "Psychiatrist", 1.7, "https://cdn.pixabay.com/photo/2017/09/06/20/36/doctor-2722941_1280.jpg"));
        doctorList.add(new Doctor("Dr. Michael Brown", "General Practitioner", 2.7, "https://cdn.pixabay.com/photo/2020/11/03/15/32/doctor-5710159_640.jpg"));
        doctorList.add(new Doctor("Dr. Sarah Wilson", "Dermatologist", 1.6, "https://cdn.pixabay.com/photo/2023/09/20/0737/doctor-8264060_640.jpg"));
        doctorList.add(new Doctor("Dr. Robert Taylor", "Orthopedic Surgeon", 4.0, "https://cdn.pixabay.com/photo/2017/01/19/18/35/surgeon-1993031_640.jpg"));
        doctorList.add(new Doctor("Dr. Linda White", "General Practitioner", 2.3, "https://cdn.pixabay.com/photo/2020/04/15/13/51/coronavirus-5046681_640.jpg"));
        doctorList.add(new Doctor("Dr. James Harris", "Oncologist", 0.3, "https://cdn.pixabay.com/photo/2013/11/28/10/02/man-219928_640.jpg"));
        doctorList.add(new Doctor("Dr. Patricia Mooren", "Endocrinologist", 1.3, "https://cdn.pixabay.com/photo/2021/02/09/06/44/doctor-5997500_640.jpg"));
        doctorList.add(new Doctor("Dr. David Clark", "Ophthalmologist", 6.3, "https://cdn.pixabay.com/photo/2020/11/02/19/52/doctor-5707722_640.jpg"));
        doctorList.add(new Doctor("Dr. Anna Martinez", "Gastroenterologist", 4.5, "https://cdn.pixabay.com/photo/2013/11/24/10/47/woman-216988_640.jpg"));
        doctorList.add(new Doctor("Dr. William Jackson", "Urologist", 3.1, "https://cdn.pixabay.com/photo/2016/02/08/23/36/isolated-1188036_960_720.png"));
        doctorList.add(new Doctor("Dr. Elizabeth Perez", "Rheumatologist", 4.9, "https://cdn.pixabay.com/photo/2017/05/23/17/12/doctor-2337835_960_720.jpg"));
        doctorList.add(new Doctor("Dr. Christopher Lee", "ENT Specialist", 4.7, "https://cdn.pixabay.com/photo/2020/06/20/15/30/woman-doctor-5321351_960_720.jpg"));
        doctorList.add(new Doctor("Dr. Barbara King", "Psychiatrist", 6.1, "https://cdn.pixabay.com/photo/2017/03/20/00/51/dr-2157993_960_720.jpg"));
        doctorList.add(new Doctor("Dr. Thomas Scott", "Nephrologist", 3.1, "https://cdn.pixabay.com/photo/2017/01/29/21/16/nurse-2019420_960_720.jpg"));
        // Add more doctors as needed
        // Sort doctors by distance
        Collections.sort(doctorList, Comparator.comparingDouble(Doctor::getDistance));
        arrayAdapter = new DoctorAdapter(this, R.layout.item_doctor, doctorList);

        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                doctorList.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Add more doctors dynamically if needed
                Log.d("LIST", "notified");
                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                // Handle scroll progress if necessary
            }
        });

        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
                BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
                bottomNavigationView.setSelectedItemId(R.id.bottom_chat);
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        // Bottom navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                return true;
            } else if (itemId == R.id.bottom_maps) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_chat) {
                startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_user) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });
    }
}
