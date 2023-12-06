package com.example.freshcard.DAO

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageDAO {

    private var progressDialog: ProgressDialog? = null

    private lateinit var storageReference: StorageReference
    public fun uploadImage(context: Context, imageUri: Uri): String {
        progressDialog = ProgressDialog(context)
        progressDialog!!.setTitle("Uploading File....")
        progressDialog!!.show()
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA)
        val now = Date()
        val fileName: String = formatter.format(now)
        storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
        storageReference.putFile(imageUri)
            .addOnSuccessListener(OnSuccessListener<Any?> {
                Toast.makeText(context, "Successfully Uploaded", Toast.LENGTH_SHORT)
                    .show()
                if (progressDialog!!.isShowing()) progressDialog!!.dismiss()
            }).addOnFailureListener(OnFailureListener {
                if (progressDialog!!.isShowing()) progressDialog!!.dismiss()
                Toast.makeText(context, "Failed to Upload", Toast.LENGTH_SHORT).show()
            })
        return fileName
    }

    public fun getImage(filename:String, imageView: ImageView) {
        var bitmap: Bitmap
        var storageReference = FirebaseStorage.getInstance().getReference("avatars/$filename")
        try {
            var file = File.createTempFile("tempFile", "jpg")
            storageReference.getFile(file)
                .addOnSuccessListener{
                    bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    imageView.setImageBitmap(bitmap)
                }
        }catch(e: IOException){
            e.printStackTrace()
        }
    }
}