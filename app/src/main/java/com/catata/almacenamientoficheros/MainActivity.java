package com.catata.almacenamientoficheros;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKeys;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {

    final static String LOG_TAG = "LOG_TAG";

    final int TIPO_INTERNO = 0;
    final int TIPO_INTERNO_CACHE = 1;
    final int TIPO_INTERNO_JETPACK = 2;
    final int TIPO_EXTERNO = 3;
    String filename = "myfile";
    String filenameJP = "myfileJetPack";

    File tempFile;
    File extFile;
    File extDir;

    //Elegimos el tipo de almacenamiento que queremos
    int tipo = TIPO_EXTERNO;

    EditText editText;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.etTexto);
        textView = (TextView)findViewById(R.id.textView);
    }

    public void leer(View view) {
        switch (tipo){
            case TIPO_INTERNO:{
                leerInterno(view);
                break;
            }
            case TIPO_INTERNO_CACHE:{
                leerCache();
                break;
            }

            case TIPO_INTERNO_JETPACK:{
                leerInternoJetPack();
                break;
            }

            case TIPO_EXTERNO:{
                leerExterno(view);
                break;
            }
        }
    }


    public void escribir(View view) {
        switch (tipo){
            case TIPO_INTERNO:{
                guardarInterno(view);
                break;
            }
            case TIPO_INTERNO_CACHE:{
                escribirCache();
                break;
            }

            case TIPO_INTERNO_JETPACK:{
                escribirInternoJetPack();
                break;
            }

            case TIPO_EXTERNO:{
                guardarExterno(view);
                break;
            }
        }
    }


    public void eliminar(View view) {
        switch (tipo){
            case TIPO_INTERNO:{
                eliminarInterno();
                break;
            }
            case TIPO_INTERNO_CACHE:{
                eliminarCache();
                break;
            }

            case TIPO_INTERNO_JETPACK:{
                eliminarInterJetPack();
                break;
            }

            case TIPO_EXTERNO:{
                eliminarExterno();
                break;
            }
        }
    }

    //Sin usar biblioteca de seguridad
    public void guardarInterno(View view) {
        //Nos devuelve el directorio interno de nuestra app
        //File directorioInterno = getFilesDir();

        //File file = new File(directorioInterno,"miFichero.txt");

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, MODE_PRIVATE | Context.MODE_APPEND); //MODE_PRIVATE
            outputStream.write(editText.getText().toString().getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(this, "Fallo al guardar",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        Toast.makeText(this, "Guardado",Toast.LENGTH_SHORT).show();
    }

    private void leerCache() {

        if(tempFile==null || !tempFile.exists()){
            textView.setText("No hay temporal");
            return;
        }

        FileInputStream inputStream;
        try{
            inputStream = new FileInputStream(tempFile);
            BufferedReader br = new BufferedReader( new InputStreamReader(inputStream,  StandardCharsets.UTF_8 ));

            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }

            textView.setText(sb.toString());
            br.close();
            inputStream.close();
        }catch (IOException e){
            Toast.makeText(this, "Fallo al leer",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        Toast.makeText(this, "Leido",Toast.LENGTH_SHORT).show();

    }

    public void leerInterno(View view) {
        FileInputStream inputStream;
        try{
            inputStream = openFileInput(filename);
            BufferedReader br = new BufferedReader( new InputStreamReader(inputStream,  StandardCharsets.UTF_8 ));

            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }

            textView.setText(sb.toString());
            br.close();
            inputStream.close();
        }catch (IOException e){
            Toast.makeText(this, "Fallo al leer",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        Toast.makeText(this, "Leido",Toast.LENGTH_SHORT).show();


    }





    public void leerExterno(View view) {
        if(!isExternalStorageReadable())
            return;

        if(extDir==null || !extDir.exists()){
            extDir = getPrivateStorageDir(this,"mis_documentos");
        }

        extFile = new File(extDir,filename);

        if(extFile==null || !extFile.exists()){
           textView.setText("No existe el fichero");
            return;
        }

        FileInputStream inputStream;
        try{
            inputStream = new FileInputStream(extFile);
            BufferedReader br = new BufferedReader( new InputStreamReader(inputStream,  StandardCharsets.UTF_8 ));

            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }

            textView.setText(sb.toString());
            br.close();
            inputStream.close();
        }catch (IOException e){
            Toast.makeText(this, "Fallo al leer",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        Toast.makeText(this, "Leido",Toast.LENGTH_SHORT).show();

    }

    public void guardarExterno(View view) {

        if(!isExternalStorageWritable())
            return;
        if(extDir==null || !extDir.exists()){
            extDir = getPrivateStorageDir(this,"mis_documentos");
        }

        extFile = new File(extDir,filename);

        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(extFile, true);
            outputStream.write(editText.getText().toString().getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(this, "Fallo al guardar",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            final Uri contentUri = Uri.fromFile(extFile);
            scanIntent.setData(contentUri);
            sendBroadcast(scanIntent);
        } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)));
            sendBroadcast(intent);
        }

    }

    public void eliminarInterno(){

    }

    public void eliminarExterno(){
        if(extFile!=null && extFile.exists())
            extFile.delete();

    }

    public void eliminarCache(){
        if(tempFile!=null && tempFile.exists())
            tempFile.delete();
    }



    private void eliminarInterJetPack() {
        File f = new File(getFilesDir(),filenameJP);
        if(f.exists())
            f.delete();
    }





    private void leerInternoJetPack() {

        // Although you can define your own key generation parameter specification, it's
        // recommended that you use the value specified here.
        KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;

        StringBuffer stringBuffer = new StringBuffer();
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);

            ByteArrayOutputStream byteStream;

            EncryptedFile encryptedFile = new EncryptedFile.Builder(
                    new File(getFilesDir(), filenameJP),
                    this,
                    masterKeyAlias,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();


            InputStream inputStream = encryptedFile.openFileInput();


            BufferedReader br = new BufferedReader( new InputStreamReader(inputStream,  StandardCharsets.UTF_8 ));

            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }

            textView.setText(sb.toString());

            br.close();
            inputStream.close();
        } catch (GeneralSecurityException gse) {
                // Error occurred getting or creating keyset.
                Toast.makeText(this, "Error occurred getting or creating keyset",Toast.LENGTH_SHORT).show();
            } catch (IOException ex) {
                // Error occurred opening file for writing.
                Toast.makeText(this, "Error Entrada/Salida",Toast.LENGTH_SHORT).show();
            }finally {
            String contents = stringBuffer.toString();
        }

    }


    private void escribirCache() {
        if(tempFile==null || !tempFile.exists())
            tempFile = getTempFile(this,"temp01");

        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(tempFile, true);//MODE_PRIVATE
            outputStream.write(editText.getText().toString().getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(this, "Fallo al guardar",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private void escribirInternoJetPack() {

        // Although you can define your own key generation parameter specification, it's
        // recommended that you use the value specified here.
        KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;

        try {
        String masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);

        // Creates a file with this name, or replaces an existing file
        // that has the same name. Note that the file name cannot contain
        // path separators.
            EncryptedFile encryptedFile = new EncryptedFile.Builder(
                    new File(getFilesDir(), filenameJP),
                    this,
                    masterKeyAlias,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();

            // Write to a file.
            byte[] fileContent = editText.getText().toString()
                    .getBytes(StandardCharsets.UTF_8);
            OutputStream outputStream = encryptedFile.openFileOutput();
            outputStream.write(fileContent);
            outputStream.flush();
            outputStream.close();
        } catch (GeneralSecurityException gse) {
            // Error occurred getting or creating keyset.
            Toast.makeText(this, "Error occurred getting or creating keyset",Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            // Error occurred opening file for writing.
            Toast.makeText(this, "Error Entrada/Salida",Toast.LENGTH_SHORT).show();
        }
    }


    private File getTempFile(Context context, String url) {
        // For a more secure solution, use EncryptedFile from the Security library
        // instead of File.
        File file=null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
            // Error while creating file
            Toast.makeText(this, "Error Entrada/Salida",Toast.LENGTH_SHORT).show();
        }
        return file;
    }


    private String readFile(File f){
        FileInputStream inputStream;
        String s="";
        try{
            inputStream = new FileInputStream(f);
            BufferedReader br = new BufferedReader( new InputStreamReader(inputStream,  StandardCharsets.UTF_8 ));

            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }

            s = sb.toString();
            br.close();
            inputStream.close();
        }catch (IOException e){
            Toast.makeText(this, "Fallo al leer",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return s;
    }



    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getPrivateStorageDir(Context context, String nombre) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), nombre);


        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }
}