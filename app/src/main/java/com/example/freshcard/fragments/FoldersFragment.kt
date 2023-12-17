package com.example.freshcard.fragments

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.DAO.FolderDAO
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.R
import com.example.freshcard.Structure.Folder
import com.example.freshcard.Structure.Topic
import com.example.freshcard.Structure.TopicItem
import com.example.freshcard.adapters.FolderItemAdapter
import com.example.freshcard.adapters.TopicNewHomeAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private lateinit var folderRecyclerView: RecyclerView
private lateinit var fodlerAdapter: FolderItemAdapter
private lateinit var btnAddNewFolder: Button
private var adapterData = ArrayList<Folder>()
class FoldersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_folders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       var userId = UserDAO().getUserIdShareRef(requireContext())
        folderRecyclerView = this.requireView().findViewById(R.id.folderRecyclerView)
        folderRecyclerView.layoutManager = GridLayoutManager(context, 2)
        FolderDAO().getFolderData(userId) {arr->
            Log.e("topic", "T${arr.size}")
            updateAdapter(arr)}

        btnAddNewFolder = this.requireView().findViewById(R.id.btnNewFolder)
        btnAddNewFolder.setOnClickListener{
            v->
            getNewFolderName(requireContext())
        }
    }

    fun updateAdapter(requestData: ArrayList<Folder>) {
       if(isAdded) {
           FolderDAO().setFoldersShareRef(requireContext(), requestData)
           adapterData = requestData
           fodlerAdapter = FolderItemAdapter(adapterData, requireContext())
           folderRecyclerView.adapter = fodlerAdapter
       }
    }

    private fun getNewFolderName(context: Context) {
        val builder = AlertDialog.Builder(context)
        var input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setPadding(80, 20,20,20)
        builder.setView(input)

        builder.setPositiveButton("Save") { dialog, _ ->
            val folderName = input.text.toString()
            handleAddNewFolder(folderName)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.setTitle("New folder's name")
        dialog.show()
    }

    fun handleAddNewFolder(name: String) {
        fodlerAdapter.createNewfolder(name)

    }
}