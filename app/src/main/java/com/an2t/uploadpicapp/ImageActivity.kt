package com.an2t.uploadpicapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_image.*
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImageActivity : AppCompatActivity(), retrofit2.Callback<Response<ServiceResponse>> {


    internal var cam_photo_path = ""

    // show progress bar in app
    private val TAG = ImageActivity::class.java.getSimpleName()

    private val file_perm = 2

    internal lateinit var cam_file: File
    internal lateinit var cam_uri_photo: Uri




    lateinit var mPD: ProgressDialog




    //lateinit var mLVM: MainVM
    lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_image)

        iniProgress()
        //mLVM = ViewModelProviders.of(this).get(MainVM::class.java)
        _observe()


        buttonCamera.setOnClickListener { openCamera() }
        buttonGallery.setOnClickListener { openGallery() }
    }


    internal lateinit var mSAPI: ServiceAPI
    internal lateinit var mCD: CompositeDisposable

    private fun _observe() {


        mSAPI = Apifactory.serviceAPI
        mCD = CompositeDisposable()

    }


    //Creating image file for upload
    @Throws(IOException::class)
    private fun create_image(): File {
        @SuppressLint("SimpleDateFormat")
        val file_name = SimpleDateFormat("yyyy_mm_ss").format(Date())
        val new_name = "file_" + file_name + "_"
        val sd_directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(new_name, ".jpg", sd_directory)
    }


//    //Checking permission for storage and camera for writing and uploading images
//    fun get_file() {
//        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
//
//        //Checking for storage permission to write images for upload
//        if (ASWP_FUPLOAD && ASWP_CAMUPLOAD && !check_permission(2) && !check_permission(3)) {
//            ActivityCompat.requestPermissions(this@MainActivity, perms, file_perm)
//
//            //Checking for WRITE_EXTERNAL_STORAGE permission
//        } else if (ASWP_FUPLOAD && !check_permission(2)) {
//            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), file_perm)
//
//            //Checking for CAMERA permissions
//        } else if (ASWP_CAMUPLOAD && !check_permission(3)) {
//            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA), file_perm)
//        }
//    }
//
//    //Checking if particular permission is given or not
//    fun check_permission(permission: Int): Boolean {
//        when (permission) {
//            //1 -> return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//
//            2 -> return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//
//            3 -> return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
//        }
//        return false
//    }


    var preUrl: String = ""


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {


        if (is_exit_called) {
            onBackPressed()
        } else {


        }



        return super.onKeyDown(keyCode, event)

    }


    private fun saveImage(image: Bitmap): Uri? {
        //TODO - Should be processed in another thread
        val imagesFolder = File(cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "shared_image.png")

            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()

            uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)

        } catch (e: IOException) {
            print("IOException while trying to write file for sharing: " + e.message)
        }

        return uri
    }


    private fun shareImageUri(uri: Uri, shareContent: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, shareContent)
//        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/png"
        startActivity(intent)
    }


    var tap_count = 0

    override fun onBackPressed() {
        super.onBackPressed()
    }


    var is_exit_called: Boolean = false


    fun openCamera() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED /*||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED*/) {
                val permission =
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, 222)
            } else {
                pickImageFromCamera()
            }
        } else {
            pickImageFromCamera()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            456 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(
                        this,
                        "Please enable the permission to upload photos from the gallery",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            222 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromCamera()
                } else {
                    Toast.makeText(
                        this,
                        "Please enable the permission to upload photo from the camera",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        //intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 123)
    }


    private fun pickImageFromCamera() {
        cam_file = createPhotoFile()
        cam_photo_path = cam_file.absolutePath
        var cv = ContentValues()
        cv.put(MediaStore.Images.Media.TITLE, "New Pic")
        cv.put(MediaStore.Images.Media.DESCRIPTION, "done")
        //var uri_photo = FileProvider.getUriForFile(MainActivity@this, "com.binarynumbers.gokozo.fileprovider", cam_file)
        cam_uri_photo = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)!!
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cam_uri_photo)
        startActivityForResult(intent, 111)
    }


    private fun createPhotoFile(): File {
        val name = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storeDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        try {
            cam_file = File.createTempFile(name, ".jpg", storeDir)
        } catch (e: IOException) {

        }

        //Log.e(TAG, "File " + cam_file.absolutePath)

        return cam_file
    }


    fun openGallery() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permission, 456)
            } else {
                pickImageFromGallery()
            }
        } else {
            pickImageFromGallery()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
                if (resultCode == Activity.RESULT_OK && requestCode == 123) {


            try {

                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, intent?.data)


                //val path = ImageFilePath.getPath(MainActivity@this, intent?.data)
                if (bitmap.height > 1000) {
                    bitmap = bitmap.scale(1000)
                }

                val tempUri = getImageUri()
                val path = ImageFilePath.getPath(MainActivity@ this, tempUri)


                var file = File(path)

                var contentType = ""

                if(!file.name.contains("pdf")){
                    contentType = "image/${file.name.split(".")[1]}"
                }else {
                    contentType = "application/pdf"
                }

                val propertyImage = RequestBody.create(contentType.toMediaTypeOrNull(), file)
                val p = MultipartBody.Part.createFormData("upload_pic", file.name, propertyImage)


                val p1 = RequestBody.create("text/plain".toMediaTypeOrNull(), "test-one")
                val p2 = RequestBody.create("text/plain".toMediaTypeOrNull(), "2019-02-22")
                val p3 = RequestBody.create("text/plain".toMediaTypeOrNull(), "testOne")
                val p4 = RequestBody.create("text/plain".toMediaTypeOrNull(), "testOne")
                val p5 = RequestBody.create("text/plain".toMediaTypeOrNull(), "15")

                mPD.show()


                mSAPI.fetchHomeOfferAPI(
                    p , p5 , p4 , p3 , p2 , p1
                ).enqueue(this)



            } catch (e: IOException) {
                print(e.toString())
            }


        } else if (resultCode == Activity.RESULT_OK && requestCode == 111) {


            try {

                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, cam_uri_photo)

                if (bitmap.height > 1000) {
                    bitmap = bitmap.scale(1000)
                }

                val tempUri = getImageUri()
                val path = ImageFilePath.getPath(MainActivity@ this, tempUri)

                var file = File(path)


//                var body =  MultipartBody.Builder().setType(MultipartBody.FORM)
//                  .addFormDataPart("user_id", "15")
//                  .addFormDataPart("report_title", "test")
//                  .addFormDataPart("lab_name", "test")
//                  .addFormDataPart("start_date", "2019-02-22")
//                  .addFormDataPart("report_note", "test2")
//                  .addFormDataPart("upload_pic","bluetooth.jpeg",
//                    RequestBody.create(
//                        "application/octet-stream".toMediaTypeOrNull(),
//                        file))
//                  .build()



                var contentType = ""
                if(!file.name.contains("pdf")){
                    contentType = "image/${file.name.split(".")[1]}"
                }else {
                    contentType = "application/pdf"
                }

                val propertyImage = RequestBody.create(contentType.toMediaTypeOrNull(), file)
                val p = MultipartBody.Part.createFormData("upload_pic", file.name, propertyImage)
                val p1 = RequestBody.create("text/plain".toMediaTypeOrNull(), "test-one")
                val p2 = RequestBody.create("text/plain".toMediaTypeOrNull(), "2019-02-22")
                val p3 = RequestBody.create("text/plain".toMediaTypeOrNull(), "testOne")
                val p4 = RequestBody.create("text/plain".toMediaTypeOrNull(), "testOne")
                val p5 = RequestBody.create("text/plain".toMediaTypeOrNull(), "15")

                mPD.show()
                mSAPI.fetchHomeOfferAPI(
                    p , p5 , p4 , p3 , p2 , p1
                ).enqueue(this)


            } catch (e: IOException) {
                print(e.toString())
            }
        }

    }



    private fun getImageUri(): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "CameraImage", null)
        return Uri.parse(path)
    }


    private fun onGetError(e: Throwable?) {
        mPD.dismiss()
        Toast.makeText(MainActivity@ this, "" + e.toString(), Toast.LENGTH_LONG).show()
    }



    fun Bitmap.scale(maxWidthAndHeight: Int): Bitmap {
        var newWidth = 0
        var newHeight = 0

        if (this.width >= this.height) {
            val ratio: Float = this.width.toFloat() / this.height.toFloat()

            newWidth = maxWidthAndHeight
            // Calculate the new height for the scaled bitmap
            newHeight = Math.round(maxWidthAndHeight / ratio)
        } else {
            val ratio: Float = this.height.toFloat() / this.width.toFloat()

            // Calculate the new width for the scaled bitmap
            newWidth = Math.round(maxWidthAndHeight / ratio)
            newHeight = maxWidthAndHeight
        }

        return Bitmap.createScaledBitmap(
            this,
            newWidth,
            newHeight,
            false
        )
    }


    private fun iniProgress() {
        mPD = ProgressDialog(MainActivity@ this)
        mPD.setMessage("Uploading Image...")
    }

    override fun onFailure(call: Call<Response<ServiceResponse>>, t: Throwable) {
        mPD.dismiss()
        Toast.makeText(this , ""+t.message , Toast.LENGTH_LONG).show()
    }

    override fun onResponse(
        call: Call<Response<ServiceResponse>>,
        response: Response<Response<ServiceResponse>>
    ) {
        mPD.dismiss()
        var asdas = response?.body()
        Toast.makeText(this , "Uploaded " + asdas?.body()?.msg + " ID " + asdas?.body()?.Id, Toast.LENGTH_LONG).show()
    }

}
