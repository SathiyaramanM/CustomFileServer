package ai.sahaj.gurukul

import ai.sahaj.gurukul.constants.ProtocolVerbs
import ai.sahaj.gurukul.operations.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Closeable
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

class Server : Closeable {
    private val clientSockets = mutableListOf<Socket>()
    private lateinit var serverSocket: ServerSocket

    fun init(port: Int) {
        serverSocket = ServerSocket(port)
        println("Tcp File Server started at port: $port")
        runBlocking {
            launch {
                while (true) {
                    val clientSocket = serverSocket.accept()
                    clientSockets.add(clientSocket)
                    println("New Client connected: ${clientSocket.inetAddress.hostAddress}")
                    launch(Dispatchers.IO) {
                        handleClient(clientSocket)
                    }
                }
            }
        }
    }

    private fun handleClient(clientSocket: Socket) {
        clientSocket.use {
            val inputStream = clientSocket.getInputStream()
            val streamReader = InputStreamReader(inputStream)
            while (true) {
                val firstLine = readLine(streamReader).split(" ")
                val name = firstLine[0]
                if(name != "ABC") {
                    throw Exception("Invalid Request Protocol")
                }
                val verb = firstLine[1]
                val operation = when (verb) {
                    ProtocolVerbs.LIST -> ListOperation(inputStream)
                    ProtocolVerbs.EXPLAIN -> ExplainOperation(inputStream)
                    ProtocolVerbs.UPLOAD -> UploadOperation(inputStream)
                    ProtocolVerbs.DOWNLOAD -> DownloadOperation(inputStream)
                    ProtocolVerbs.DELETE -> DeleteOperation(inputStream)
                    ProtocolVerbs.HELP -> HelpOperation(inputStream)
                    else -> throw Exception("Invalid Verb")
                }
                operation.perform()
                break
            }
            inputStream.close()
        }
    }

    private fun readLine(inputStreamReader: InputStreamReader): String {
        val stringBuilder = StringBuilder()
        while(true){
            val characterCode = inputStreamReader.read()
            if(characterCode == -1) { // EOS
                throw Exception("Stream ended abruptly without a newline character")
            }
            if (characterCode == 10 || characterCode == 13) {
                break
            }
        }
        return stringBuilder.toString().trim()
    }

    override fun close() {
        for (client in clientSockets) {
            client.close()
        }
        serverSocket.close()
    }
}