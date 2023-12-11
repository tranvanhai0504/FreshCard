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
            var intent = Intent(this, AddTopic::class.java)
            intent.putExtra("edit", "false")
            startActivity(intent)
        }
    }

    fun getContext(myF:(Context)-> Unit) {
        myF(this)
    }

    fun getUser(id : String){
        UserDAO().getDbUser().child(id).get().addOnSuccessListener {
            Log.e("user", "${it}")
            var pass = it.child("password").getValue(String::class.java)
            var phoneNumber = it.child("phoneNumber").getValue(String::class.java)
            var fullName = it.child("fullName").getValue(String::class.java)
            var lastAccess = it.child("lastAccess").getValue(Long::class.java)
            var avatar = it.child("avatar").getValue(String::class.java)
            var email = it.child("email").getValue(String::class.java)
            var bookMarks = it.child("bookmarkedTopics").getValue() as MutableMap<String, Boolean>?
            if(bookMarks == null){
                bookMarks = HashMap()
            }
            var learningTopics = ArrayList(emptyList<LearningTopic>())
            for( item in it.child("learningTopics").children) {
                var idTopic = item.child("idTopic").getValue(String::class.java)
                var idLearned1 = item.child("idLearned").getValue()
                var idLearned:ArrayList<String>? = idLearned1 as? ArrayList<String>
                var idLearning1 = item.child("idLearning").getValue()
                var idLearning:ArrayList<String>? = idLearning1  as? ArrayList<String>
                var idChecked1 = item.child("idChecked").getValue()
                var idChecked:ArrayList<String>?  = idChecked1  as? ArrayList<String>
                if(idLearned==null) {
                    idLearned = ArrayList(emptyList<String>())
                }
                if(idLearning==null) {
                    idLearning = ArrayList(emptyList<String>())
                }
                if(idChecked==null) {
                    idChecked = ArrayList(emptyList<String>())
                }

                learningTopics.add(LearningTopic(idTopic!!, idChecked, idLearning, idLearned))
            }
            user = User(pass, fullName,avatar,email,phoneNumber,bookMarks,lastAccess, learningTopics)
        }
    }

    private fun setCurrentFragment(fragment : Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }
}