package com.syubsyubboy.howlstagram

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    val PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //Set soft input
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        //Init storage
        storage = FirebaseStorage.getInstance()

        //Open the album
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        upload_button.setOnClickListener {
            contentUpload()
        }
    }

    private fun contentUpload() {
        var timeStamp = SimpleDateFormat("yyyyMMdd_hhmmss").format(Date())
        var imageFileName = "IMAGE_" + timeStamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        //File upload
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this, R.string.upload_success, Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                photoUri = data?.data
                addPhoto_image.setImageURI(photoUri)
            } else {
                finish()
            }
        }
    }
}
