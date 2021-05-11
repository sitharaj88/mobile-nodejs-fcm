package com.delfiapps.nodejsdemo

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.delfiapps.nodejsdemo.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var _startedNodeAlready = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        button.setOnClickListener {
            requestNodeJsServer()
        }

        runScriptFromAssets()
    }

    fun runScriptFromAssets(){
        Thread { //The path where we expect the node project to be at runtime.
            val nodeDir: String =
                NodeJsApplication.appContext.getFilesDir()
                    .getAbsolutePath() + "/nodejs-project"
            //Recursively delete any existing nodejs-project.
            val nodeDirReference = File(nodeDir)
            if (nodeDirReference.exists()) {
                NodeJsUtils.deleteFolderRecursively(File(nodeDir))
            }
            //Copy the node project from assets into the application's data path.
            NodeJsUtils.copyAssetFolder(
                NodeJsApplication.appContext.getAssets(),
                "nodejs-project",
                nodeDir
            )
            startNodeWithArguments(
                arrayOf(
                    "node",
                    "$nodeDir/main.js"
                )
            )
        }.start()
    }

    private fun initNodeSerer() {
        if (!_startedNodeAlready) {
            _startedNodeAlready = true
            Thread {
                startNodeWithArguments(
                    arrayOf(
                        "node", "-e",
                        "var http = require('http'); " +
                                "var versions_server = http.createServer( (request, response) => { " +
                                "  response.end('Versions: ' + JSON.stringify(process.versions)); " +
                                "}); " +
                                "versions_server.listen(3000);"
                    )
                )
            }.start()
        }
    }

    private fun requestNodeJsServer() {
        object : AsyncTask<Void?, Void?, String?>() {
            override fun onPostExecute(result: String?) {
                sample_text.setText(result)
            }

            override fun doInBackground(vararg params: Void?): String? {
                var nodeResponse = ""
                try {
                    val localNodeServer = URL("http://localhost:3000")
                    val reader = BufferedReader(
                        InputStreamReader(localNodeServer.openStream())
                    )
                    var inputLine: String = ""

                    reader.use {
                        while (it.readLine().apply { if (!this.isNullOrEmpty()) inputLine = this } != null) {
                            nodeResponse += inputLine
                        }
                    }
                } catch (ex: Exception) {
                    nodeResponse = ex.toString()
                }
                return nodeResponse
            }
        }.execute()
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun startNodeWithArguments(arguments: Array<String?>?): Int

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
            System.loadLibrary("node");
        }
    }
}