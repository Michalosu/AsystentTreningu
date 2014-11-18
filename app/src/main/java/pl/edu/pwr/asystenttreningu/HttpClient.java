package pl.edu.pwr.asystenttreningu;

/**
 * Created by michalos on 02.10.14.
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;


public class HttpClient {

    private String url;
    private String filePath;
    private int serverResponseCode = 0;
    private String serverResponseMessage;

    public int getServerResponseCode() {
        return serverResponseCode;
    }

    public void setServerResponseCode(int serverResponseCode) {
        this.serverResponseCode = serverResponseCode;
    }

    public String getServerResponseMessage() {
        return serverResponseMessage;
    }

    public void setServerResponseMessage(String serverResponseMessage) {
        this.serverResponseMessage = serverResponseMessage;
    }

    public HttpClient(String url, String filePath) {
        this.url = url;
        this.filePath = filePath;
    }

    public void connectAndSend(){

        String filePath = this.filePath;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(this.filePath);

        if (!sourceFile.isFile()) {

            Log.e("uploadFile", "Source File not exist :" + this.filePath);

        }
        else
        {
            try {

                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(this.url);

                // Otwarcie połączenia HTTP
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", filePath);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=uploaded_file;filename="
                        + filePath + "" + lineEnd);

                dos.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                InputStreamReader in = new InputStreamReader((InputStream) conn.getContent());
                BufferedReader buff = new BufferedReader(in);
                serverResponseMessage = buff.readLine();

                if(serverResponseCode == 200){

                    Log.d("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);

                    if(serverResponseMessage == "User does not exist"){
                        throw new UserDoesNotExists("User does not exist");
                    }

                }else{
                    throw new ServerException("Server problem.");
                }


                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }


        } // End else block

    }




}
