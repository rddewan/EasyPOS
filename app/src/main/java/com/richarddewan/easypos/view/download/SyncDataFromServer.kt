package com.richarddewan.easypos.view.download

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.MaterialDialog
import com.opencsv.CSVReader
import com.richarddewan.easypos.R

import com.richarddewan.easypos.model.entity.ProductEntity
import com.richarddewan.easypos.view.setting.SettingsActivity
import com.richarddewan.easypos.viewmodel.SyncDataFromServerViewModel
import kotlinx.android.synthetic.main.activity_sync_data_from_server.*
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class SyncDataFromServer : AppCompatActivity() {
    private val TAG = "SyncDataFromServer"
    private val IMPORT_DIRECTORY = "/EasyPOS/Import/"
    private var connection: HttpURLConnection? = null
    private var STATUS_CODE: Int = 0
    private var csvProduct:String = "POSItem.csv";

    private var progressBar:ProgressBar? = null
    private var counter = 0
    private var getData = 0
    private var product_file_url: String? = null
    private var IMAGE_URL: String? = null
    private var webURL: String? = null
    private  var sharedPreferences:SharedPreferences? = null
    var nextLine: Array<String>? = null
    private var syncDataFromServerViewModel: SyncDataFromServerViewModel? = null

     var handler = Handler(Handler.Callback { msg ->
        when (msg.what) {
            1 -> {
                counter = 0
                getData = msg.arg1
                when (getData) {
                    1 -> cbProduct.setChecked(false)
                }
                txtLog.setTextColor(resources.getColor(R.color.primaryColor))
                txtLog.text = msg.obj as String
            }
            101 -> {
                counter++
                txtLog.setTextColor(resources.getColor(R.color.colorPrimary))
                txtLog.text = msg.obj as String
                txtCounter.setText("Total Record : $counter")
                txtCounter.setTextColor(resources.getColor(R.color.colorPrimary))
                txtStatus.setText(null)
                progressBar?.setVisibility(View.GONE)
            }
            201 -> {
                progressBar?.setVisibility(View.VISIBLE)
                txtStatus.setTextColor(resources.getColor(R.color.colorPrimary))
                txtStatus.setText(msg.obj as String)
                txtStatus.setGravity(Gravity.CENTER)
            }
            400 -> {
                txtLog.setTextColor(resources.getColor(R.color.red))
                txtLog.text = msg.obj as String
            }
        }
        false
    })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync_data_from_server)

        progressBar = findViewById(R.id.progressBar)
        //
        syncDataFromServerViewModel = ViewModelProviders.of(this).get(SyncDataFromServerViewModel::class.java)

        //
        getSharedPref()

        downloadButtonClickEvent()

    }

    fun getSharedPref(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        webURL = sharedPreferences?.getString("server_address","")
        if (webURL.equals("")){
            openSettingPrefActivity()
        }
        //url
        product_file_url = webURL + "download/$csvProduct"
        IMAGE_URL = webURL + "download/ProductImages/"
    }

    fun openSettingPrefActivity() {
        MaterialDialog(this).show {
            icon(R.drawable.stop)
            title(R.string.server_empty_title)
            message(R.string.server_empty_msg)
            cancelable(false)
            cornerRadius(16f)
            positiveButton(R.string.yes) {
                //call delete function
                val intent = Intent(applicationContext, SettingsActivity::class.java)
                startActivity(intent)
            }
            negativeButton(R.string.disagree) {
                dismiss()
            }
        }
    }

    fun downloadButtonClickEvent(){
        btnDownload.setOnClickListener(View.OnClickListener {
            if (cbProduct.isChecked){
              DownloadItems(this,handler).execute(product_file_url)
            }
        })
    }

    @SuppressLint("StaticFieldLeak")
    inner class DownloadItems(val context: Context, val handler:Handler) : AsyncTask<String,String,String>(){
        var STATUS:String? = "SUCCESS"

        override fun onPreExecute() {
            super.onPreExecute()
            //prgDialog.show();
            //prgDialog.show();
            progressBar?.setProgress(0)
            //send msg to handler
            val message1 = Message()
            message1.what = 201
            val data:String = "Downloading Customer..Please Wait"
            message1.obj = data
            handler.sendMessage(message1)
        }

        override fun doInBackground(vararg p0: String?): String? {

            try {
                var count = 0
                val dir:File  = File(Environment.getExternalStorageDirectory(),IMPORT_DIRECTORY)
                if (!dir.exists()){
                    val mkdir = dir.mkdir()
                    if (!mkdir) {
                        Log.e(TAG, "Directory creation failed.")
                    }
                }

                val fileName = File(dir,csvProduct)

                val url:URL = URL(p0[0])
                connection = url.openConnection() as HttpURLConnection?
                connection?.setRequestMethod("GET")
                connection?.setReadTimeout(1000 * 30)
                connection?.setConnectTimeout(1000 * 60);
                connection?.connect()
                STATUS_CODE = connection!!.responseCode

                if (STATUS_CODE == 200){
                    val lengthOfFile:Int = connection!!.contentLength
                    val inputStream:InputStream = BufferedInputStream(url.openStream(),8192)
                    val outputStream: FileOutputStream = FileOutputStream(fileName)

                    val data = ByteArray(1024)
                    var total: Long = 0

                    while ({ count = inputStream.read(data); count }() != -1) {
                        total += count.toLong()
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress("" + (total * 100) / lengthOfFile)
                        // writing data to file
                        outputStream.write(data, 0, count)
                    }

                    // flushing output
                    outputStream.flush()
                    // closing streams
                    outputStream.close()
                    inputStream.close()
                }
                else {
                    val msg = connection!!.responseMessage
                    val message = Message()
                    message.what = 201
                    message.obj = STATUS_CODE.toString() + " : $msg"
                    handler.sendMessage(message)
                }
            }
            catch (er:Exception){
                Log.e(TAG,er.message)
                STATUS = er.message
            }


            return STATUS

        }

        override fun onProgressUpdate(vararg values: String?) {
            progressBar?.setProgress(Integer.parseInt(values[0]!!))
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            //prgDialog.dismiss();
            //send message
            if (result.equals("SUCCESS")){
                val message = Message()
                message.what = 201
                val  data:String = "Download Product Successful"
                message.obj = data
                handler.sendMessage(message)
                //import product
                ImportItems(handler).execute()
            }
            else {
                val message = Message()
                message.what = 400
                message.obj = result
                handler.sendMessage(message)
            }

        }

    }

    @SuppressLint("StaticFieldLeak")
    inner class ImportItems(val handler: Handler) : AsyncTask<Void,String,String>(){
        var STATUS:String? = "SUCCESS"

        override fun onPreExecute() {
            super.onPreExecute()
        }
        override fun doInBackground(vararg p0: Void?): String? {
            try {
                val filePath = File(Environment.getExternalStorageDirectory(),IMPORT_DIRECTORY)
                val fileImport = File(filePath,csvProduct)
                val fileReader = FileReader(fileImport)
                val reader = CSVReader(fileReader)

                try {
                    syncDataFromServerViewModel?.deleteProductTable()

                } catch (er: Exception) {
                    Log.e(TAG, er.message)

                }

                try {
                    while ({ nextLine = reader.readNext();nextLine}() != null) {
                        //send msg
                        val message = Message()
                        message.what = 101
                        val data = "Importing Product..Please Wait"
                        message.obj = data
                        handler.sendMessage(message)

                        //insert to db
                        val product_id = nextLine?.get(0)
                        val item_id = nextLine?.get(1)
                        val item_name = nextLine?.get(2)
                        val barcode = nextLine?.get(3)
                        val image = IMAGE_URL + nextLine?.get(1) + ".jpg"

                        val productEntity = ProductEntity(product_id!!,item_id!!,item_name!!,barcode!!,image)
                        syncDataFromServerViewModel?.addProduct(productEntity)

                    }


                } catch (er: Exception) {
                    Log.e(TAG, er.message.toString())
                    STATUS = er.message
                }

            }
            catch (er:Exception){
                Log.e(TAG,er.message.toString())
                STATUS = er.message
            }

            return STATUS
        }

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: String?) {
            //send message
            if (result.equals("SUCCESS")){
                val message3 = Message()
                message3.what = 1
                val data = "Importing Product Successful"
                message3.arg1 = 1
                message3.obj = data
                handler.sendMessage(message3)
            }
            else {
                val message3 = Message()
                message3.what = 400
                message3.obj = result
                handler.sendMessage(message3)
            }

        }
    }

    override fun onRestart() {
        super.onRestart()
        getSharedPref()
    }
}
