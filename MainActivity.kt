package com.example.ffpanel

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ffpanel.databinding.ActivityMainBinding
import com.example.ffpanel.databinding.DialogAddProfileBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private lateinit var db: AppDatabase
    private var crosshairOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = AppDatabase.get(this)

        // Crosshair toggle
        b.btnToggleCrosshair.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                ))
                Toast.makeText(this, "Overlay permission do, phir wapas aa", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            crosshairOn = !crosshairOn
            val intent = Intent(this, CrosshairService::class.java)
            if (crosshairOn) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    startForegroundService(intent)
                else startService(intent)
                Toast.makeText(this, "Crosshair ON", Toast.LENGTH_SHORT).show()
            } else {
                stopService(intent)
                Toast.makeText(this, "Crosshair OFF", Toast.LENGTH_SHORT).show()
            }
        }

        // Add profile
        b.btnAddProfile.setOnClickListener { showAddDialog() }

        // Recycler
        val adapter = ProfileAdapter(emptyList()) { profile ->
            AlertDialog.Builder(this)
                .setTitle("Delete?")
                .setMessage(profile.name)
                .setPositiveButton("Delete") { _, _ ->
                    lifecycleScope.launch { db.profileDao().delete(profile) }
                }
                .setNegativeButton("Cancel", null).show()
        }
        b.recyclerProfiles.layoutManager = LinearLayoutManager(this)
        b.recyclerProfiles.adapter = adapter

        lifecycleScope.launch {
            db.profileDao().getAll().collectLatest { list ->
                b.recyclerProfiles.adapter = ProfileAdapter(list) { profile ->
                    lifecycleScope.launch { db.profileDao().delete(profile) }
                }
            }
        }
    }

    private fun showAddDialog() {
        val d = DialogAddProfileBinding.inflate(layoutInflater)
        AlertDialog.Builder(this)
            .setTitle("Naya Profile")
            .setView(d.root)
            .setPositiveButton("Save") { _, _ ->
                val p = SensitivityProfile(
                    name = d.etName.text.toString().ifBlank { "Profile" },
                    general = d.etGeneral.text.toString().toIntOrNull() ?: 100,
                    redDot = d.etRedDot.text.toString().toIntOrNull() ?: 95,
                    scope2x = d.et2x.text.toString().toIntOrNull() ?: 90,
                    scope4x = d.et4x.text.toString().toIntOrNull() ?: 80,
                    sniper = d.etSniper.text.toString().toIntOrNull() ?: 60,
                    freeLook = d.etFreeLook.text.toString().toIntOrNull() ?: 85
                )
                lifecycleScope.launch { db.profileDao().insert(p) }
            }
            .setNegativeButton("Cancel", null).show()
    }
}
