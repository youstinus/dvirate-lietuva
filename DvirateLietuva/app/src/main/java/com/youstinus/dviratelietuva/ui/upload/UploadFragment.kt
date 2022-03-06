package com.youstinus.dviratelietuva.ui.upload

import android.Manifest
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView

import com.youstinus.dviratelietuva.R
import android.widget.TextView
import com.youstinus.dviratelietuva.MainActivity
import java.io.File
import android.widget.Toast
import android.content.Intent
import android.graphics.BitmapFactory
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.loader.content.CursorLoader
import com.google.firebase.storage.FirebaseStorage
import com.youstinus.dviratelietuva.ui.routes.createroute.CreateRouteViewModel
import com.youstinus.dviratelietuva.utilities.FilePath


class UploadFragment : Fragment() {

    private lateinit var viewModel: UploadViewModel

    private val TAG = MainActivity::class.java.simpleName
    private val REQUEST_FILE_CODE = 200
    private val READ_REQUEST_CODE = 300
    private val SERVER_PATH = "http://192.168.43.72:8888/api/upload_files/index.php"
    var fileBrowseBtn: Button? = null
    var uploadBtn: Button? = null
    var previewImage: ImageView? = null
    var fileName: TextView? = null
    var fileUri: Uri? = null
    private var file: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upload, container, false)

        fileBrowseBtn = view.findViewById(R.id.btn_choose_file);
        uploadBtn = view.findViewById(R.id.btn_upload);
        previewImage = view.findViewById(R.id.iv_preview);
        fileName = view.findViewById(R.id.tv_file_name);

        setOnClickListeners()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(UploadViewModel::class.java)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setOnClickListeners() {
        fileBrowseBtn?.setOnClickListener { onFileBrowseClicked() }
        uploadBtn?.setOnClickListener { onFileUploadClicked() }
    }

    private fun onFileBrowseClicked() {
//check if app has permission to access the external storage.
        showFileChooserIntent();
        return
        if (ContextCompat.checkSelfPermission(
                requireActivity().parent,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {


        } else {
            //If permission is not present request for the same.
            ActivityCompat.requestPermissions(
                requireActivity().parent,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_REQUEST_CODE
            );
        }
    }

    private fun onFileUploadClicked() {
        if (file != null) {

            uploadFileFirebase()
            //val uploadAsyncTask = UploadAsyncTask(context!!)
            //uploadAsyncTask.execute()


        } else {
            Toast.makeText(
                context,
                "Please select a file first", Toast.LENGTH_LONG
            ).show()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FILE_CODE && resultCode == Activity.RESULT_OK) {
            fileUri = data?.data
            previewFile(fileUri)
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_FILE_CODE && resultCode == Activity.RESULT_OK) {
            fileUri = data.data
            previewFile(fileUri)
        }
    }*/

    /**
     * Show the file name and preview once the file is chosen
     * @param uri
     */
    private fun previewFile(uri: Uri?) {
        val filePath = FilePath.getPath(requireContext(),uri!!)//getRealPathFromURIPath(uri!!, requireActivity())
        file = File(filePath!!)
        Log.d(TAG, "Filename " + file!!.name)
        fileName?.text = file!!.name

        val cR = context?.contentResolver//contentResolver()
        val mime = cR?.getType(uri)

        //Show preview if the uploaded file is an image.
        if (mime != null && mime.contains("image")) {
            val options = BitmapFactory.Options()

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8

            val bitmap = BitmapFactory.decodeFile(filePath, options)

            previewImage?.setImageBitmap(bitmap)
        } else {
            previewImage?.setImageResource(R.drawable.ic_file)
        }

        hideFileChooser()
    }

    /**
     * Shows an intent which has options from which user can choose the file like File manager, Gallery etc
     */
    private fun showFileChooserIntent() {
        val fileManagerIntent = Intent(Intent.ACTION_GET_CONTENT)
        //Choose any file
        fileManagerIntent.type = "*/*"
        startActivityForResult(fileManagerIntent, REQUEST_FILE_CODE)

    }

    /**
     * Returns the actual path of the file in the file system
     *
     * @param contentURI
     * @param activity
     * @return
     */
    private fun getRealPathFromURIPath2(contentURI: Uri?, activity: Activity): String? {
        //return contentURI!!.path
        val cursor = activity.contentResolver.query(contentURI!!, null, null, null, null)
        val realPath: String?
        if (cursor == null) {
            realPath = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            realPath = cursor.getString(idx)
        }
        cursor?.close()

        return realPath
    }

    private fun getRealPathFromURIPath(contentURI: Uri?): String? {
        var cursor: Cursor? = null;
        try {
            val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
            cursor = requireContext().getContentResolver().query(contentURI!!, proj, null, null, null);
            val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor?.moveToFirst();
            return cursor?.getString(column_index!!);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    fun getRealPathFromURIPath3(contentUri: Uri,context: Context): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(context, contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()
        var result = ""
        if (cursor != null) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            result = cursor.getString(column_index)
            cursor.close()
        }
        return result
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        requireActivity().onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }

    fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        showFileChooserIntent()
    }

    fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Log.d(TAG, "Permission has been denied")
    }

    /**
     * Hides the Choose file button and displays the file preview, file name and upload button
     */
    private fun hideFileChooser() {
        fileBrowseBtn?.visibility = View.GONE
        uploadBtn?.visibility = View.VISIBLE
        fileName?.visibility = View.VISIBLE
        previewImage?.visibility = View.VISIBLE
    }

    /**
     * Displays Choose file button and Hides the file preview, file name and upload button
     */
    private fun showFileChooser() {
        fileBrowseBtn?.visibility = View.VISIBLE
        uploadBtn?.visibility = View.GONE
        fileName?.visibility = View.GONE
        previewImage?.visibility = View.GONE
    }

    private fun uploadFileFirebase() {
        val storageRef = FirebaseStorage.getInstance().reference
        val file = Uri.fromFile(File(fileUri!!.path!!)) //"path/to/images/rivers.jpg"
        val riversRef = storageRef.child("routes/bicycle/${file.lastPathSegment}")
        val uploadTask = riversRef.putFile(file)

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener { e ->
            // Handle unsuccessful uploads
            println(e)
            println("FAILED UPLOAD")
        }.addOnSuccessListener { up ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            println(up)
            println("GOTIT")
        }
    }

    /**
     * Background network task to handle file upload.
     */
    /*class UploadAsyncTask constructor(context: Context) :
        AsyncTask<Void, Int, String>() {

        internal var httpClient: HttpClient = DefaultHttpClient()
        private var exception: Exception? = null
        private var progressDialog: ProgressDialog? = null

        override fun doInBackground(vararg params: Void): String? {

            var httpResponse: HttpResponse? = null
            var httpEntity: HttpEntity? = null
            var responseString: String? = null

            try {
                val httpPost = HttpPost(SERVER_PATH)
                val multipartEntityBuilder = MultipartEntityBuilder.create()

                // Add the file to be uploaded
                multipartEntityBuilder.addPart("file", FileBody(file))

                // Progress listener - updates task's progress
                val progressListener = object : MyHttpEntity.ProgressListener() {
                    fun transferred(progress: Float) {
                        publishProgress(progress.toInt())
                    }
                }

                // POST
                httpPost.setEntity(
                    MyHttpEntity(
                        multipartEntityBuilder.build(),
                        progressListener
                    )
                )


                httpResponse = httpClient.execute(httpPost)
                httpEntity = httpResponse!!.getEntity()

                val statusCode = httpResponse!!.getStatusLine().getStatusCode()
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(httpEntity)
                } else {
                    responseString = "Error occurred! Http Status Code: $statusCode"
                }
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                Log.e("UPLOAD", e.getMessage())
                this.exception = e
            } catch (e: ClientProtocolException) {
                e.printStackTrace()
                Log.e("UPLOAD", e.getMessage())
                this.exception = e
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return responseString
        }

        override fun onPreExecute() {

            // Init and show dialog
            this.progressDialog = ProgressDialog(this.context)
            this.progressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            this.progressDialog!!.setCancelable(false)
            this.progressDialog!!.show()
        }

        override fun onPostExecute(result: String) {

            // Close dialog
            this.progressDialog!!.dismiss()
            Toast.makeText(
                context,
                result, Toast.LENGTH_LONG
            ).show()
            showFileChooser()
        }

        protected override fun onProgressUpdate(vararg progress: Int) {
            // Update process
            this.progressDialog!!.progress = progress[0]
        }
    }*/

    companion object {
        private val TAG =
            com.youstinus.dviratelietuva.ui.upload.UploadFragment::class.java.simpleName
        private val REQUEST_FILE_CODE = 200
        private val READ_REQUEST_CODE = 300
        private val SERVER_PATH = ""//http://192.168.43.72:8888/api/upload_files/index.php"
        fun newInstance() = UploadFragment()
    }
}
