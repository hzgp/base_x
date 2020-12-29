package com.jxkj.base.utils;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 * Author:zhujb
 * Date:2020/7/29
 */
public class FragmentTabAssembler {
    private ArrayList<CustomTabEntity> mTitles;
    private List<Fragment> fragments;
    private CommonTabLayout commonTabLayout;
    private ViewPager viewPager;
    private int tabItemCount;
    private FragmentManager fragmentManager;

    public FragmentTabAssembler(CommonTabLayout commonTabLayout, ViewPager viewPager) {
        this.commonTabLayout = commonTabLayout;
        this.viewPager = viewPager;
        initCallback();
        mTitles = new ArrayList<>();
        fragments = new ArrayList<>();
    }

    private void initCallback() {
        commonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int index) {
                commonTabLayout.setCurrentTab(index);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public Fragment getItem(int position) {
        return fragments == null || fragments.isEmpty() ? null : fragments.get(position);
    }

    public Fragment getCurrentItem() {
        return getItem(viewPager.getCurrentItem());
    }

    public FragmentTabAssembler append(String title, int selectIcon, int normalIcon, Fragment fragment) {
        mTitles.add(new TabEntity(title, selectIcon, normalIcon));
        fragments.add(fragment);
        tabItemCount++;
        return this;
    }

    public FragmentTabAssembler append(String title, int selectIcon, Fragment fragment) {
        mTitles.add(new TabEntity(title, selectIcon));
        fragments.add(fragment);
        tabItemCount++;
        return this;
    }

    public FragmentTabAssembler append(String title, Fragment fragment) {
        mTitles.add(new TabEntity(title));
        fragments.add(fragment);
        tabItemCount++;
        return this;
    }

    public FragmentTabAssembler append(TabEntity tabItem, Fragment fragment) {
        mTitles.add(tabItem);
        fragments.add(fragment);
        tabItemCount++;
        return this;
    }

    public FragmentTabAssembler fragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        return this;
    }

    public void assemble() {
        commonTabLayout.setTabData(mTitles);
        viewPager.setOffscreenPageLimit(tabItemCount - 1);
        viewPager.setAdapter(new CommonPagerAdapter(this));
    }

    private static class TabEntity implements CustomTabEntity {
        public String title;
        public int selectedIcon;
        public int unSelectedIcon;

        public TabEntity(String title) {
            this.title = title;
        }

        public TabEntity(String title, int selectedIcon) {
            this.title = title;
            this.selectedIcon = selectedIcon;
        }

        public TabEntity(String title, int selectedIcon, int unSelectedIcon) {
            this.title = title;
            this.selectedIcon = selectedIcon;
            this.unSelectedIcon = unSelectedIcon;
        }

        @Override
        public String getTabTitle() {
            return title;
        }

        @Override
        public int getTabSelectedIcon() {
            return selectedIcon;
        }

        @Override
        public int getTabUnselectedIcon() {
            return unSelectedIcon;
        }
    }

    private static class CommonPagerAdapter extends FragmentPagerAdapter {
        private WeakReference<FragmentTabAssembler> reference;

        public CommonPagerAdapter(FragmentTabAssembler assembler) {
            super(assembler.getSupportFragmentManager());
            reference = new WeakReference<>(assembler);
        }

        @Override
        public Fragment getItem(int i) {
            return reference.get() == null ? null : reference.get().fragments.get(i);
        }

        @Override
        public int getCount() {
            return reference.get() == null ? 0 : reference.get().tabItemCount;
        }
    }

    private FragmentManager getSupportFragmentManager() {
        if (fragmentManager != null) {
            return fragmentManager;
        }
        Context context = viewPager.getContext();
        if (context instanceof FragmentActivity) {
            return ((FragmentActivity) context).getSupportFragmentManager();
        }
        return null;
    }
}
