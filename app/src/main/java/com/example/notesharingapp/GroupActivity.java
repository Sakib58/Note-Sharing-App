package com.example.notesharingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.notesharingapp.ui.AnnouncementFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {
    Intent intent;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group2);
        intent = getIntent();
        Toast.makeText(this, "Data: "+intent.getStringExtra("group_key"), Toast.LENGTH_SHORT).show();

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        ArrayList<String> tabList = new ArrayList<>();
        tabList.add("Announcements");
        tabList.add("Discussion");
        tabList.add("Files");
        
        prepareViewPager(viewPager,tabList);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> tabList) {
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        AnnouncementFragment announcementFragment = new AnnouncementFragment();
        DiscussionFragment discussionFragment = new DiscussionFragment();
        FilesFragment filesFragment = new FilesFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("title",tabList.get(0));
        bundle1.putString("group_key",intent.getStringExtra("group_key"));
        Bundle bundle2 = new Bundle();
        bundle2.putString("title",tabList.get(1));
        bundle2.putString("group_key",intent.getStringExtra("group_key"));
        Bundle bundle3 = new Bundle();
        bundle3.putString("title",tabList.get(2));
        bundle3.putString("group_key",intent.getStringExtra("group_key"));

        announcementFragment.setArguments(bundle1);
        discussionFragment.setArguments(bundle2);
        filesFragment.setArguments(bundle3);

        tabAdapter.addFragment(announcementFragment,tabList.get(0));
        tabAdapter.addFragment(discussionFragment,tabList.get(1));
        tabAdapter.addFragment(filesFragment,tabList.get(2));

        announcementFragment = new AnnouncementFragment();
        discussionFragment = new DiscussionFragment();
        filesFragment = new FilesFragment();

        viewPager.setAdapter(tabAdapter);

        /*for (int i=0;i<tabList.size();i++){
            Bundle bundle = new Bundle();
            bundle.putString("title",tabList.get(i));
            announcementFragment.setArguments(bundle);
            tabAdapter.addFragment(announcementFragment,tabList.get(i));
            announcementFragment = new AnnouncementFragment();
        }
        viewPager.setAdapter(tabAdapter);*/
    }

    private class TabAdapter extends FragmentPagerAdapter {
        ArrayList<String> arrayList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        public void addFragment(Fragment fragment,String title){
            arrayList.add(title);
            fragmentList.add(fragment);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        public TabAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return arrayList.get(position);
        }
    }
}