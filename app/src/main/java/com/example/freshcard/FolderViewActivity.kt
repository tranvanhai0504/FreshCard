package com.example.freshcard

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshcard.DAO.FolderDAO
import com.example.freshcard.DAO.TopicDAO
import com.example.freshcard.Structure.Folder
import com.example.freshcard.Structure.Topic
import com.example.freshcard.Structure.TopicInfoView
import com.example.freshcard.adapters.TopicAdapter
import com.example.freshcard.databinding.ActivityFolderViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FolderViewActivity : AppCompatActivity() {
    private  lateinit var folderId: String
    private lateinit var binding: ActivityFolderViewBinding
    private lateinit var folder: Folder
    private lateinit var adapterData: ArrayList<TopicInfoView>
    private lateinit var topicAdapter: TopicAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderViewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        folderId = intent.getStringExtra("folderId")!!

        folder = (intent.getSerializableExtra("folder") as? Folder)!!
//        CoroutineScope(Dispatchers.Main).launch{
//            var ls = TopicDAO().getListTopics(folder.idTopics) {
//
//            }
//            Log.e("topic", "-------------\n${ls} \n-----------")
//        }

        adapterData = ArrayList(emptyList<TopicInfoView>())
        binding.topicsRecyclerView.layoutManager = LinearLayoutManager(this)
        topicAdapter = TopicAdapter(adapterData, this, "folder")
        binding.topicsRecyclerView.adapter = topicAdapter
        for(id in folder.idTopics) {
            Log.e("id111", "$id")
            TopicDAO().getTopicViewById(id) {
                adapterData.add(it)
//                topicAdapter.addTopic(it)
                Log.e("id111", "11$it")
            }
        }
        binding.title.setText(folder.name)
        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.btnEditTitle.setOnClickListener{
            enableEdit()
        }

        binding.btnSaveTitle.setOnClickListener{
            FolderDAO().updateFolderTitle(folderId,binding.title.text.toString())
            disableEdit()
        }

    }


    private fun enableEdit() {
        binding.title.requestFocus()
        binding.btnEditTitle.visibility = View.INVISIBLE
        binding.btnSaveTitle.visibility = View.VISIBLE
        binding.title.isEnabled = true
    }

    private fun disableEdit() {
        binding.btnEditTitle.visibility = View.VISIBLE
        binding.btnSaveTitle.visibility = View.INVISIBLE
        binding.title.isEnabled = false
    }


    fun removeTopicFromFolder(topicId: String, ctx: Context) {
        val sharedPreferences = ctx.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        var folderId  =sharedPreferences.getString("folderId", "null").toString()
        Log.e("remove", "@${sharedPreferences.all}")
        if(topicId!="null") {
            FolderDAO().removeTopicFromFolder(topicId, folderId)
        }
    }
}