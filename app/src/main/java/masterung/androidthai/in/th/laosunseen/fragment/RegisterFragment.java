package masterung.androidthai.in.th.laosunseen.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import masterung.androidthai.in.th.laosunseen.MainActivity;
import masterung.androidthai.in.th.laosunseen.R;
import masterung.androidthai.in.th.laosunseen.utility.MyAlert;
import masterung.androidthai.in.th.laosunseen.utility.UserModel;

public class RegisterFragment extends Fragment {

    //    Explicit
    private Uri uri;
    private ImageView imageView;
    private boolean aBoolean = true;
    private  String nameString,emailString,passwordString,uidString,pathURLString,myPostString;
    private ProgressDialog progressDialog;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Create Toolbar
        createToolbar();

//        Photo Controller
        photoController();

    }   // Main Class

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_register, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.itemUpload) {
            uploadProcess();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void uploadProcess() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uplaod Value Process");
        progressDialog.setMessage("Please Wait Few Minus");
        progressDialog.show();

        EditText nameEditText = getView().findViewById(R.id.edtName);
        EditText emailEditText = getView().findViewById(R.id.edtEmail);
        EditText passwordEditText = getView().findViewById(R.id.edtPassword);

//        Get Value From EditText
        nameString = nameEditText.getText().toString().trim();
         emailString = emailEditText.getText().toString().trim();
         passwordString = passwordEditText.getText().toString().trim();

//        Check Choose Photo
        if (aBoolean) {
//            Non Choose Photo
            MyAlert myAlert = new MyAlert(getActivity());
            myAlert.normalDialog("Non Choose Photo",
                    "Please Choose Photo");
        } else if (nameString.isEmpty() || emailString.isEmpty() || passwordString.isEmpty()) {

//            Have Space
            MyAlert myAlert = new MyAlert(getActivity());
            myAlert.normalDialog("Have Space",
                    "Please Fill All Every Blank");

        } else {

//            No Space
            uploadPhotoToFirebase();
            createAuthentication();

        }


    }

    private void createAuthentication() {

        Log.wtf("8AugV2", "CreateAuthentication Work");
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) { //task get value work from firebase
                        if (task.isSuccessful()) {
                            //Log.d("8AugV1", "Successful");
                            uidString = firebaseAuth.getCurrentUser().getUid();
                            Log.wtf("8AugV2", "uidSring ==> "+uidString);

                        } else {
                            MyAlert myAlert = new MyAlert(getActivity());
                            myAlert.normalDialog("Cannot Register","Becuase "+task.getException().getMessage());
                            Log.wtf("8AugV2", "Messege =="+task.getException().getMessage());
                            progressDialog.dismiss();
                        }
                    }
                });


    }

    private void uploadPhotoToFirebase() {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference storageReference1 = storageReference.child("Avata/" + nameString);


        storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Success Upload Photo", Toast.LENGTH_SHORT).show();

                filePathURLPhoto();
                creatPost();
                createDatabase();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Cannot Upload Photo", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });



    }   // uploadPhoto

    private void createDatabase() {  //database firebase

        UserModel userModel = new UserModel(uidString,nameString,emailString,pathURLString,myPostString);


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference()
                .child("User");
        databaseReference.child(uidString).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Register Success", Toast.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contentMainFragment, new ServiceFragement())
                        .commit();
            }
        });

    }

    private void creatPost() {

        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add("Hello");
        myPostString =stringArrayList.toString();
        Log.d("9Augv1", "mypost==>" + myPostString);

    }

    private void filePathURLPhoto() {   //get path photo fron firebase
        try {
            Log.d("9Augv1", "pathURL --> start");
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();
            final String[] urlStrings = new String[1];

            storageReference.child("Avata").child(nameString).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    urlStrings[0] = uri.toString();
                    pathURLString = urlStrings[0];
                    Log.d("9Augv1", "pathURL ==>" + pathURLString);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("9Augv1", "e Error ==>" + e.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   //end find path

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {

            uri = data.getData();
            aBoolean = false;

            try {

                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 400, 300, true);
                imageView.setImageBitmap(bitmap1);

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(getActivity(), "Please Choose Photo", Toast.LENGTH_SHORT).show();
        }

    }

    private void photoController() {
        imageView = getView().findViewById(R.id.imvPhoto);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Please Choose App"), 1);

            }
        });
    }

    private void createToolbar() {
        Toolbar toolbar = getView().findViewById(R.id.toolBarRegister);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Register");
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("Please Choose Photo and Fill All Blank");
        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }
}