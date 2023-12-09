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
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
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
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FoldersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FoldersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}