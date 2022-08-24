package com.frame.base.utils;

import android.content.Context;
import android.view.View;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

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
    private View[] views;
    private int currentTab;

    public FragmentTabAssembler(CommonTabLayout commonTabLayout, ViewPager viewPager) {
        this.commonTabLayout = commonTabLayout;
        this.viewPager = viewPager;
        initCallback();
        mTitles = new ArrayList<>();
        fragments = new ArrayList<>();
    }

    public FragmentTabAssembler(CommonTabLayout commonTabLayout, View[] views) {
        this.commonTabLayout = commonTabLayout;
        this.views = views;
        initViewChange();
        mTitles = new ArrayList<>();
    }

    private void initCallback() {
        commonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                Fragment current = getCurrentItem();
                if (current instanceof IObserverFragment)
                    ((IObserverFragment) current).hideChange(true);
                viewPager.setCurrentItem(position);
                current = getItem(position);
                if (current instanceof IObserverFragment)
                    ((IObserverFragment) current).hideChange(false);

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

    private void initViewChange() {
        commonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                views[currentTab].setVisibility(View.GONE);
                views[position].setVisibility(View.VISIBLE);
                currentTab = position;
            }

            @Override
            public void onTabReselect(int position) {
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

    public FragmentTabAssembler append(String title) {
        mTitles.add(new TabEntity(title));
        tabItemCount++;
        return this;
    }

    public FragmentTabAssembler fragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        return this;
    }

    public FragmentTabAssembler assemble() {
        commonTabLayout.setTabData(mTitles);
        viewPager.setOffscreenPageLimit(tabItemCount - 1);
        viewPager.setAdapter(new CommonPagerAdapter(this));
        return this;
    }

    public void buildViews() {
        commonTabLayout.setTabData(mTitles);
    }

    public void show(int currentTab) {
        //commonTabLayout.setCurrentTab(currentTab);
        Fragment current =getCurrentItem();
        if (current instanceof IObserverFragment)
            ((IObserverFragment) current).hideChange(true);
        viewPager.setCurrentItem(currentTab);
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
