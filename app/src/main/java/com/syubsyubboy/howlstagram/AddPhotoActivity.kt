package com.syubsyubboy.howlstagram

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.syubsyubboy.howlstagram.model.ContentDTO
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    val TAG = this::class.java.name

    val PICK_IMAGE_FROM_ALBUM = 0

    var photoUri: Uri? = null

    var storage: FirebaseStorage? = null
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //Set soft input
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        //Init storage
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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
        storageRef?.putFile(photoUri!!)?.continueWithTask {
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener {
            var contentDTO = ContentDTO().apply {
                uid = auth?.currentUser?.uid
                timestamp = System.currentTimeMillis()
                userId = auth?.currentUser?.email
                imageUrl = it.toString()
                explain = desc_editText.text.toString()
            }

            firestore?.collection("images")?.document()?.set(contentDTO)
            setResult(Activity.RESULT_OK)

            finish()
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
