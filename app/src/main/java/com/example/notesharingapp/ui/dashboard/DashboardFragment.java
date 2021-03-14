package com.example.notesharingapp.ui.dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.notesharingapp.MainActivity;
import com.example.notesharingapp.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    TextView tvName,tvSID,tvEmail;
    String name,email,sid;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvName = root.findViewById(R.id.profile_name);
        tvEmail = root.findViewById(R.id.profile_email);
        tvSID = root.findViewById(R.id.profile_sid);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(MainActivity.SHARED_PREFS,0);
        name = sharedPreferences.getString(MainActivity.NAME,"Name ");
        email = sharedPreferences.getString(MainActivity.EMAIL," Email");
        sid = sharedPreferences.getString(MainActivity.SID,"SID");

        tvName.setText(name);
        tvEmail.setText(email);
        tvSID.setText(sid);


        return root;
    }
}