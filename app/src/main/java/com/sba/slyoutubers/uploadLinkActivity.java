package com.sba.slyoutubers;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class uploadLinkActivity extends AppCompatActivity
{

    private EditText link;
    private Button submit;
    private String linkAddress;
    DatabaseReference upload;
    private int Count = 0;
    AutoCompleteTextView keyword;
    private ArrayList<String> channelLinks;
    ProgressDialog progressDialog;
    boolean status = false;
    int sizeofArrayList;
    int id;

    private static final String[] suggestions = new String[]
            {
                    "Photoshop", "After Effects","Premiere Pro","Phone Review",
                    "Internet Money","Gaming","Food Review","Tech Review","Comedy"
            };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_link);




        keyword = (AutoCompleteTextView) findViewById(R.id.keyword);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,suggestions);
        keyword.setAdapter(adapter);

        link = (EditText) findViewById(R.id.uploadLink);
        submit = (Button) findViewById(R.id.uploadButton);

        channelLinks = new ArrayList<>();

        progressDialog = new ProgressDialog(uploadLinkActivity.this);
        progressDialog.setMessage("Uploading..");


        upload = FirebaseDatabase.getInstance().getReference().child("UploadedLinks");




    }
    public void upload(View view)
    {
        Random r = new Random();
        id = r.nextInt(1000000 - 1) + 1;
        linkAddress = link.getText().toString();
        Count = 0;
        channelLinks.clear();
        progressDialog.show();
        sizeofArrayList =0;
        status = false;
        final String keywordString = keyword.getText().toString();

        if(linkAddress.isEmpty())
        {
            progressDialog.hide();
            Toast.makeText(uploadLinkActivity.this,"Enter Channel Link First",Toast.LENGTH_SHORT).show();

        }
        else
        {

            upload.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    upload.child(String.valueOf(id)).child("link").setValue(linkAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                    upload.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                            upload.child(String.valueOf(id)).child("Category").setValue(keywordString).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(uploadLinkActivity.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(uploadLinkActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                                                    }
                                                    progressDialog.hide();
                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                            }
                            else
                            {
                                Toast.makeText(uploadLinkActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                                progressDialog.hide();
                            }

                        }
                    });
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {
                    Toast.makeText(uploadLinkActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();

                }


            });
        }

   }
}

