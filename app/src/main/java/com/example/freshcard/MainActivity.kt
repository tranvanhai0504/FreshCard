package com.example.freshcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.renderscript.Sampler.Value
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.Structure.User
import com.example.freshcard.databinding.ActivityLoginBinding
import com.example.freshcard.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import java.io.Console

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var user : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

        //get id user from intent
        var id = intent.getStringExtra("idUser")

        //check id
        if (id != null) {
            //get user by id
            getUser(id)
        }else{
            //get back to login activity
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //set up fragment
        var homeFragment = HomeFragment()
        var folderFragment = FolderFragment()
        var profileFragment = ProfileFragment()
        var rankFragment = RankFragment()

        setCurrentFragment(homeFragment)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home->setCurrentFragment(homeFragment)
                R.id.folder->setCurrentFragment(folderFragment)
                R.id.rank->setCurrentFragment(rankFragment)
                R.id.user->setCurrentFragment(profileFragment)
            }
            true
        }

        binding.fab.setOnClickListener{
            v->
            startActivity(Intent(this, AddTopic::class.java))
        }
    }

    fun getUser(id : String){
        UserDAO().getDbUser().child(id).get().addOnSuccessListener {
            user = it.getValue<User>()!!
        }
    }

    private fun setCurrentFragment(fragment : Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }
}