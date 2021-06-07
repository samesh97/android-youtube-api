package com.sba.slyoutubers;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainLayout extends AppCompatActivity
{


    AutoCompleteTextView keyword;
    Button searchButton;
    ListView mListView;
    String channelNameString;
    String channelLinkString;
    String logoLink;
    ArrayList<String> channelLogos;
    ArrayList<String> ChannelList;
    ArrayList<String> ChannelLinks;
    int Count = 0;
    ProgressDialog progressDialog;
    private AdView mAdView,mAdview2;




    private static final String[] suggestions = new String[]
            {
                    "Photoshop", "After Effects","Premiere Pro","Phone Review",
                    "Internet Money","Gaming","Food Review","Tech Review","Comedy"
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.upload)
        {
            Intent intent = new Intent(MainLayout.this,uploadLinkActivity.class);
            startActivity(intent);
        }
        if(id == R.id.rate)
        {
            Uri uri = Uri.parse("market://details?id=" + "com.sba.slyoutubers");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + "com.sba.slyoutubers")));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);










        MobileAds.initialize(this, "ca-app-pub-3538783908730049~6395378961");
        mAdView = findViewById(R.id.adView);
        mAdview2 = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdview2.loadAd(adRequest1);

        channelLogos = new ArrayList<>();
        ChannelList = new ArrayList<>();
        ChannelLinks = new ArrayList<>();

        progressDialog = new ProgressDialog(MainLayout.this);
        progressDialog.setMessage("Loading..");

        ChannelList.clear();
        channelLogos.clear();
        ChannelLinks.clear();

        mListView = (ListView) findViewById(R.id.listView);
        final MainLayout.customAdaptor customAdaptor = new MainLayout.customAdaptor();
        mListView.setAdapter(customAdaptor);

        keyword = (AutoCompleteTextView) findViewById(R.id.keyword);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,suggestions);
        keyword.setAdapter(adapter);



        searchButton = (Button) findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String keywordString = keyword.getText().toString();
                channelLogos.clear();
                ChannelList.clear();
                ChannelLinks.clear();
                progressDialog.show();
                Count = 0;


                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Youtubers").child(keywordString).child("Channels");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {

                            for(DataSnapshot dss: dataSnapshot.getChildren())
                            {
                                Count++;


                                reference.child(String.valueOf(Count)).child("channelName")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                            {
                                                channelNameString = dataSnapshot.getValue(String.class);
                                                ChannelList.add(channelNameString);
                                                mListView.invalidateViews();


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError)
                                            {
                                                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                reference.child(String.valueOf(Count)).child("ImageLink")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                            {
                                                logoLink = dataSnapshot.getValue(String.class);
                                                channelLogos.add(logoLink);
                                                mListView.invalidateViews();

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                reference.child(String.valueOf(Count)).child("ChannelLink")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                            {
                                                channelLinkString = dataSnapshot.getValue(String.class);
                                                ChannelLinks.add(channelLinkString);
                                                mListView.invalidateViews();

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }
                        else
                        {
                            Toast.makeText(MainLayout.this,"No Channels Found!",Toast.LENGTH_LONG).show();
                            mListView.invalidateViews();
                        }

                        progressDialog.hide();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {
                        Toast.makeText(MainLayout.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

            }
        });


    }
    class customAdaptor extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return channelLogos.size();
        }

        @Override
        public Object getItem(int position)
        {
            return 0;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View view = getLayoutInflater().inflate(R.layout.listview, null);
            ImageView logo = view.findViewById(R.id.logo);
            TextView ChannelName = view.findViewById(R.id.ChannelName);
            Button visit = view.findViewById(R.id.visitButton);

            //logo.setImageResource(images[position]);


            Picasso.get().load(channelLogos.get(position)).placeholder( R.mipmap.ic_loading).into(logo);
            ChannelName.setText(ChannelList.get(position));
            visit.setEnabled(true);

            visit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://www.youtube.com/"+ChannelLinks.get(position)));
                        startActivity(intent);
                    }
                    catch (Exception e)
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.youtube.com/"+ChannelLinks.get(position))));
                    }
                }
            });


            return view;



        }
    }

}
