package com.road_sidekiq.android.roadsidekiq.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.road_sidekiq.android.roadsidekiq.fragments.HomeFragment;
import com.road_sidekiq.android.roadsidekiq.fragments.LocationFragment;

import java.util.Locale;

/**
 * Created by rewin0087 on 4/18/15.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return HomeFragment.newInstance(position);
            case 1:
                return LocationFragment.newInstance(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return HomeFragment.TITLE;
            case 1:
                return LocationFragment.TITLE;
        }
        return null;
    }
}
