package com.pzy.smart_manufacture_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private SwitchCompat rememberSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // 设置Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // 设置导航抽屉
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        
        // 初始化ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 
            R.string.navigation_drawer_open, 
            R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        
        // 导航菜单点击事件
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == R.id.nav_tasks) {
                startActivity(new Intent(this, TasksActivity.class));
            } else if (id == R.id.nav_alerts) {
                startActivity(new Intent(this, AlertsActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.nav_settings) {
                // 当前就在设置页面
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
            }
            // 添加异常记录菜单项的处理
            else if (id == R.id.nav_records) {
                startActivity(new Intent(this, RecordsActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        rememberSwitch = findViewById(R.id.remember_switch);
        
        // 读取当前设置
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean rememberCredentials = prefs.getBoolean("remember_credentials", false);
        rememberSwitch.setChecked(rememberCredentials);
        
        // 设置开关监听器
        rememberSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("remember_credentials", isChecked);
            editor.apply();
    
            if (!isChecked) {
                // 如果关闭记住密码，清除保存的密码
                editor.remove("saved_username");
                editor.remove("saved_password");
                editor.apply();
                Toast.makeText(SettingsActivity.this, "已清除保存的登录信息", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingsActivity.this, "已启用自动填充功能", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 同步抽屉状态
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    // 处理菜单项点击
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}