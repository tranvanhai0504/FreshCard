package com.example.freshcard

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.Structure.LearningTopic
import com.example.freshcard.Structure.User
import com.example.freshcard.databinding.ActivityMainBinding
import com.example.freshcard.fragments.FolderFragment
import com.example.freshcard.fragments.HomeFragment
import com.example.freshcard.fragments.ProfileFragment
import com.example.freshcard.fragments.RankFragment
import com.google.firebase.database.GenericTypeIndicator

class MainActivity : AppCompatActivity() {
    companion object {
        var user = User()
    }

    private lateinit var binding: ActivityMainBinding
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
            val sharedPreferences = applicationContext.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("idUser", id)
            editor.apply()
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
            var gti = object : GenericTypeIndicator<List<LearningTopic>>(){}

            val password = it.child("password").value.toString()
            val fullname = it.child("fullName").value.toString()
            val avatar = it.child("avatar").value.toString()
            val email = it.child("email").value.toString()
            val phone = it.child("phoneNumber").value.toString()
            val bookmarkTopics = it.child("bookmarkedTopics").value as MutableMap<String, Boolean>
            var learningTopics = it.child("learningTopics").getValue(gti) as ArrayList<LearningTopic>?
            if(learningTopics == null){
                learningTopics = ArrayList<LearningTopic>()
            }
            val lastAccess = it.child("lastAccess").value as Long

            user = User(password, fullname, avatar, email, phone, bookmarkTopics, lastAccess, learningTopics)
        }
    }

    private fun setCurrentFragment(fragment : Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }
}