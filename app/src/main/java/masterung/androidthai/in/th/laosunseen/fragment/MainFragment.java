package masterung.androidthai.in.th.laosunseen.fragment;

        import android.content.Intent;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;

        import masterung.androidthai.in.th.laosunseen.R;
        import masterung.androidthai.in.th.laosunseen.SevrviceActivity;
        import masterung.androidthai.in.th.laosunseen.utility.MyAlert;

public class MainFragment extends Fragment{

    private  String emailString,passwordString;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        check status
        checkStatus();


        //   Register Controller
        registerController();


// login Controler

        loginControler();

    }   //Method main

    private void loginControler() {
        Button button = getActivity().findViewById(R.id.btRegister);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailEditText = getView().findViewById(R.id.txtUser);
                EditText passwordEditText = getView().findViewById(R.id.txtPw);

                emailString = emailEditText.getText().toString();
                passwordString = passwordEditText.getText().toString();
                if (emailString.isEmpty() || passwordString.isEmpty()) {
                    MyAlert myAlert = new MyAlert(getActivity());
                    myAlert.normalDialog("Have Space", "Please Fill All Blank");
                } else {
                    checkAuthen();
                }
            }
        });
    }

    private void checkAuthen() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(getActivity(),"Wellcome", Toast.LENGTH_LONG).show();
                            moveToService();

                        } else {

                            MyAlert myAlert = new MyAlert(getActivity());
                            myAlert.normalDialog("Authen Fial",
                                    "Becuase ==>"+task.getException().getMessage());

                        }


                    }
                });

    }

    private void checkStatus() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser()!=null) {      //check have value login

            moveToService();

        }
    }

    private void moveToService() {

        startActivity(new Intent(getActivity(), SevrviceActivity.class));
        getActivity().finish();

    }

    private void registerController() {
        TextView textView = getView().findViewById(R.id.txtRegister);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//              replace fragment
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contentMainFragment, new RegisterFragment())
                        .addToBackStack(null)
                        .commit();


            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }
}
