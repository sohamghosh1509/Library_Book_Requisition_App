package com.example.librarybookrequisition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.FieldPosition;

public class StudentsRequestsReport extends AppCompatActivity {

    RecyclerView recyclerView;
    StudentsRequestsReportAdapter adapter;
    TextView deptName;
    Button downloadBtn;
    LinearLayout layout;
    ConstraintLayout constraintLayout;
    PdfDocument document;
    Bitmap bitmap1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_requests_report);

        // Inflate the layout for this fragment
//        View view= inflater.inflate(R.layout.activity_students_requests_report, container, false);

        String dept = getIntent().getStringExtra("dept");

        //Assigning the Recyclerview to lost Items
        recyclerView = (RecyclerView) findViewById(R.id.StudentsRequestsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));



        //Firebase Recycler Options to get the data form firebase database using model class and reference
        FirebaseRecyclerOptions<Model> options =
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("StudentsRequestedBooks").child(dept), Model.class)
                        .build();

        deptName = (TextView) findViewById(R.id.deptTxt);
        deptName.setText("Department: "+ dept);

        layout = (LinearLayout) findViewById(R.id.reportLayout);
        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);

        downloadBtn = (Button) findViewById(R.id.btnDownloadReport);
        downloadBtn.setOnClickListener(view -> {
            if(checkPermission()){
                //creating the bitmap for the layout
                bitmap1 = viewToBitmap(constraintLayout, constraintLayout.getWidth(), constraintLayout.getHeight());
                createPdf(bitmap1);
            } else {
                requestPermission();
            }

        });

        //Setting adapter to RecyclerView
        adapter = new StudentsRequestsReportAdapter(options);
        recyclerView.setAdapter(adapter);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PackageManager.PERMISSION_GRANTED)
        {
            // If request is cancelled, the result arrays are empty.
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                bitmap1 = viewToBitmap(constraintLayout, constraintLayout.getWidth(), constraintLayout.getHeight());
                createPdf(bitmap1);
            }
            else
                Toast.makeText(this, "Permission to access Storage is required. Please go to Settings to grant the required permissions for this application.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission(){
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void createPdf(Bitmap bitmap) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float height = displayMetrics.heightPixels;
        float width = displayMetrics.widthPixels;
        int mheight = (int) height, mwidth = (int) width;

        /* Started creating a pdf from here....
         * */
        document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(mwidth, mheight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        bitmap = Bitmap.createScaledBitmap(bitmap, mwidth, mheight, true);
        paint.setColor(Color.BLUE);

        canvas.drawBitmap(bitmap, 0,0, paint);
        document.finishPage(page);
        createFile();

//        try {
//            /*Need to specify the path ...
//             * Lets get that from the specific method ... */
//            document.writeTo(getFileName());
//        }catch (Exception e){
//            Log.d("TAG", "createPdf: "+e.getMessage());
//        }

//        openCreatedPdf();
    }

    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "Report.pdf");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri uri= null;
            if(data != null) {
                uri = data.getData();
                if(document != null)
                {
                    ParcelFileDescriptor pfd = null;
                    try{
                        pfd = getContentResolver().
                                openFileDescriptor(uri, "w");
                        FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                        document.writeTo(fileOutputStream);
                        document.close();
                        Toast.makeText(this,"PDF saved successfully!", Toast.LENGTH_SHORT).show();
//                        openCreatedPdf(uri);
                    } catch (IOException e) {
                        try {
                            DocumentsContract.deleteDocument(getContentResolver(), uri);
                        } catch (FileNotFoundException fileNotFoundException) {
                            fileNotFoundException.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //opening the PDF
    private void openCreatedPdf(Uri uri) {
//        File pdfDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ "/PDF");
//        if(!pdfDir.exists())
//            pdfDir.mkdir();
//        File myPath = new File(pdfDir, "ReportPdf" + ".pdf");
//        Uri uri = Uri.fromFile(myPath);
//        if(myPath.exists()){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
//            intent.putExtra(intent.EXTRA_TEXT, "Open PDF");

//            Intent shareIntent = Intent.createChooser(intent, null);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
//        }
    }

    /* To get the file path for storing the pdf file
     * */
    private FileOutputStream getFileName() {
        File pdfDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ "/PDF");
        if(!pdfDir.exists())
            pdfDir.mkdir();
        File myPath = new File(pdfDir, "ReportPdf" + ".pdf");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
        } catch (Exception e){
            Log.d("TAG", "getFileName: "+ e.getMessage());
        }
        return fos;
    }

    /* This method is use to convert the view to bitmap
     * In bitmap we need to provide HEIGHT, WIDTH, COLOR ALGORITHM
     * params @v = View, @Width = width of the pdf view @height = height of the pdf view
     *  */
    private Bitmap viewToBitmap(View v, int width, int height) {
        Bitmap viewMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(viewMap);
        v.draw(canvas);
        /*process is done..  returning the bitmap*/
        return viewMap;
    }


    @Override
    public void onStart() {
        super.onStart();
        //Starts listening for data from firebase when this fragment starts
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Stops listening for data from firebase
        adapter.stopListening();
    }
}