package com.example.medbuddy;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class UpcomingFragment extends Fragment {

    private RecyclerView recyclerView;
    private MedicationAdapter adapter;
    private List<MedicineData> medicineList;
    private DatabaseReference databaseReference;

    public UpcomingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);

        recyclerView = view.findViewById(R.id.upcomingRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        medicineList = new ArrayList<>();
        adapter = new MedicationAdapter(medicineList, getContext());  // Pass the context here
        recyclerView.setAdapter(adapter);

        fetchMedicines();

        return view;
    }

    private void fetchMedicines() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmailPath = currentUser.getEmail().replace(".", ",");
            databaseReference = FirebaseDatabase.getInstance().getReference("MedicineData").child(userEmailPath);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    medicineList.clear();
                    for (DataSnapshot medSnapshot : snapshot.getChildren()) {
                        MedicineData medicine = medSnapshot.getValue(MedicineData.class);
                        if (medicine != null) {
                            medicineList.add(medicine);
                            // Schedule notifications for each time slot
//                            if (medicine.getMorningTime() != null) {
//                                NotificationUtils.scheduleNotification(getContext(), medicine.getMedicineName(), medicine.getMorningTime(), 101); // 101 can be a unique request code
//                            }
//                            if (medicine.getAfternoonTime() != null) {
//                                NotificationUtils.scheduleNotification(getContext(), medicine.getMedicineName(), medicine.getAfternoonTime(), 102);
//                            }
//                            if (medicine.getEveningTime() != null) {
//                                NotificationUtils.scheduleNotification(getContext(), medicine.getMedicineName(), medicine.getEveningTime(), 103);
//                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle potential errors
                }
            });
        }
    }
}