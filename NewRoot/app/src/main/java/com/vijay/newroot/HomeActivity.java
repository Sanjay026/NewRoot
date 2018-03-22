package com.vijay.newroot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
    private TabLayout tabLayout;
    private CircleImageView navCircleImageView;
    private TextView navUsername,navmail;
    private ViewPager viewPager;
    private DatabaseReference mDatabase,DatabaseUpdate;
    private DatabaseReference mDatabaseUsers;
    private Boolean mProcessLike=false;
    public Toolbar toolbar;
    FloatingActionButton fab;
    public Context homecontext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private NavigationView navigationView;
    private ImageView navBackground;
    private StorageReference mImageStorage;
    private  AlertDialog.Builder alert,alert2;
    private AlertDialog dialog,dialog2;
    private TextView cancel,confirm,update,cancel22,CreditValue;
    private EditText newname;
    private static Uri mInvitationUrl,dynamicLinkUri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Adding toolbar to the activity

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout=(TabLayout)findViewById(R.id.tabLayout);
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }
            }
        };
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View mView=navigationView.getHeaderView(0);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        DatabaseUpdate=FirebaseDatabase.getInstance().getReference().child("version");
        mDatabase.keepSynced(true);
        mImageStorage = FirebaseStorage.getInstance().getReference().child("Profile_images");
        mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users");
        navCircleImageView = (CircleImageView) mView.findViewById(R.id.Navigation_image);
        navUsername = (TextView) mView.findViewById(R.id.Navigation_User);
        CreditValue = (TextView) mView.findViewById(R.id.creditValue);
        navmail = (TextView) mView.findViewById(R.id.navemail);
        navBackground = (ImageView) mView.findViewById(R.id.navFrameImage);
        mDatabaseUsers.keepSynced(true);
        fab=(FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),PostActivity.class));
            }
        });
        tabLayout.addTab(tabLayout.newTab().setText("Feed"));
        tabLayout.addTab(tabLayout.newTab().setText("Nature"));
        tabLayout.addTab(tabLayout.newTab().setText("Food"));
        tabLayout.addTab(tabLayout.newTab().setText("Culture"));
        tabLayout.addTab(tabLayout.newTab().setText("Heritage"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);

        //Creating our pager adapter
        Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());
        //Adding adapter to pager
        viewPager.setAdapter(adapter);
        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        homecontext=getApplicationContext();
        checkUserExist();
        setNavImage();
        navCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FullProfile.class));

            }
        });
        alert = new AlertDialog.Builder(this);
        alert2=new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View confirmdi= inflater.inflate(R.layout.dialog_layout, null);
        final View updateid=inflater.inflate(R.layout.dialog_layout2,null);
        cancel= (TextView) confirmdi.findViewById(R.id.cancel);
        cancel22= (TextView) updateid.findViewById(R.id.cancel2);
        update= (TextView) updateid.findViewById(R.id.update);
        confirm= (TextView) confirmdi.findViewById(R.id.confirm);

        newname= (EditText) confirmdi.findViewById(R.id.editdialog);

        alert.setView(confirmdi);
        alert2.setView(updateid);
        dialog=alert.create();
        dialog2=alert2.create();
        final long t=1;
        DatabaseUpdate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(((Long) dataSnapshot.getValue())>t){
                    dialog2.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        cancel22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        navUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wux62.app.goo.gl/NewRoot"));
                startActivity(browserIntent);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newusername=newname.getText().toString().trim();
                final String userid=mAuth.getCurrentUser().getUid();
                if(TextUtils.isEmpty(newusername)) {
                    Toast.makeText(getApplicationContext(), "Username must not be empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    mDatabaseUsers.child(userid).child("name").setValue(newusername);
                }
                dialog.dismiss();
                newname.setText("");
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_profile) {
                    startActivity(new Intent(getApplicationContext(),PasswordActivity.class));
                } else if (id == R.id.nav_photos) {
                    startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
                } else if(id==R.id.nav_about){
                    startActivity(new Intent(HomeActivity.this,About.class));
                }
                else if (id == R.id.nav_help) {

                }
                else if(id==R.id.nav_logout){
                    logout();
                }
                else if(id==R.id.nav_invite){
//                    Intent intent = new Intent(Intent.ACTION_SEND);
//                    intent.setType("text/plain");
//                    intent.putExtra(Intent.EXTRA_SUBJECT, "ToRoots Discover Experiences");
//                    intent.putExtra(Intent.EXTRA_TEXT,"https://wux62.app.goo.gl/NewRoot" );
//                    startActivity(intent);
                    setDynamicLink();
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String link = "https://drive.google.com/open?id=1fFvzJhWn1j23LmYh3DWcRWPheKRPj7dP/?invitedby=" + uid;

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDynamicLinkDomain("wux62.app.goo.gl")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .buildDynamicLink();

        dynamicLinkUri = dynamicLink.getUri();

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLinkUri)
                .buildShortDynamicLink();
        shortLinkTask.addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
            @Override
            public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                if (task.isSuccessful()) {

                    mInvitationUrl = task.getResult().getShortLink();
   //                 Toast.makeText(getApplicationContext(),"Link created",Toast.LENGTH_SHORT).show();

                }
                else {

                  Toast.makeText(getApplicationContext(),"This is error",Toast.LENGTH_SHORT).show();
                  Log.d("EXCEPTION OCCURS HERE",task.getException().toString());

                }
            }
        });

    }



    private void setDynamicLink(){

        String referrerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String subject = String.format("%s wants you to find your root!", referrerName);
        String invitationLink = mInvitationUrl.toString();
        String msg = "Let's find your root"+"  "
                + invitationLink;
//        String msgHtml = String.format("<p>Let's play MyExampleGame together! Use my "
//               + "<a href=\"%s\">referrer link</a>!</p>", invitationLink);

        Intent intent = new Intent(Intent.ACTION_SEND);
     //   intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
   //     intent.putExtra(Intent.EXTRA_HTML_TEXT, msgHtml);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    private void setNavImage() {
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String ref=(String)dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("image").getValue();
                Picasso.with(getApplicationContext()).load(ref).placeholder(R.drawable.newuser).fit().networkPolicy(NetworkPolicy.OFFLINE).into(navCircleImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(getApplicationContext()).load(ref).into(navCircleImageView);

                    }
                });
                String name=(String)dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("name").getValue();
                int value=dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("credit").getValue(Integer.class);
                CreditValue.setText(Integer.toString(value));
                navUsername.setText(name);
                String email=FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
                navmail.setText(email);
                Glide.with(getApplicationContext()).load(ref).centerCrop().placeholder(R.drawable.neworange).override(5,5).crossFade(1000).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                }).into(navBackground);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void checkUserExist() {

        if(mAuth.getCurrentUser()!=null) {

            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {
                        Intent setupIntent = new Intent(HomeActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);

                    }

                }

                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    }
    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_search) {
            startActivity(new Intent(this, SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }


}
