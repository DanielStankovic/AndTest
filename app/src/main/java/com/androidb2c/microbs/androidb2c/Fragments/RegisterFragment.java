package com.androidb2c.microbs.androidb2c.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidb2c.microbs.androidb2c.Activities.MainActivity;
import com.androidb2c.microbs.androidb2c.Data.Api;
import com.androidb2c.microbs.androidb2c.Data.ApiClient;
import com.androidb2c.microbs.androidb2c.Model.Customer;
import com.androidb2c.microbs.androidb2c.Model.Product;
import com.androidb2c.microbs.androidb2c.R;
import com.androidb2c.microbs.androidb2c.Utility.ApplicationConnectionStatus;
import com.androidb2c.microbs.androidb2c.Utility.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private ImageView profileImage;
    private EditText emailEt, fullNameEt, passwordEt, confirmPasswordEt, addressEt, phoneEt, cityEt;
    private Button submitBtn;
    private CheckBox termsOfUseCb;
    private TextView termsOfUseTv;

    private FragmentManager fragmentManager2;
    private FragmentTransaction fragmentTransaction2;
    private Api apiInterface;
    private Uri profileImageUri;
    private AlertDialog dialog;
    private Bitmap bitmap;
    private String profileImageString;

    private  final int CAMERA_CODE = 3;
    private  final int GALLERY_CODE = 4;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);
        fragmentManager2 = getFragmentManager();
        fragmentTransaction2 = fragmentManager2.beginTransaction();


        init(view);
        Glide.with(getActivity().getApplicationContext()).load(R.drawable.profile_image_placeholder).apply(RequestOptions.circleCropTransform()).into(profileImage);
        ((MainActivity)getActivity()).setTitle("Registracija");
        return view;
    }

    private void init(View view){
        profileImage = view.findViewById(R.id.profileImageReg);
        emailEt = view.findViewById(R.id.emailEt);
        fullNameEt = view.findViewById(R.id.fullNameEt);
        passwordEt = view.findViewById(R.id.passEt);
        confirmPasswordEt = view.findViewById(R.id.confirmPassEt);
        addressEt = view.findViewById(R.id.addressEt);
        phoneEt = view.findViewById(R.id.phoneEt);
        cityEt = view.findViewById(R.id.cityEt);
        submitBtn = view.findViewById(R.id.submitRegisterbtn);
        termsOfUseCb = view.findViewById(R.id.termsOfUseCb);
        termsOfUseTv = view.findViewById(R.id.termsOfUseTv);
        termsOfUseTv.setText(Html.fromHtml("Prihvatam <u>uslove korišćenja</u>"));

        termsOfUseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TermsOfUseFragment fragment = new TermsOfUseFragment();
                fragmentTransaction2.addToBackStack("termsOfUse");

                fragmentTransaction2.replace(R.id.myContainer, fragment);
                fragmentTransaction2.commit();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToServer();
            }
        });

        termsOfUseCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                termsOfUseCb.setError(null);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectImageDialog();
            }
        });
    }

    private void sendDataToServer() {


            if(!Utility.isValidEmail(emailEt.getText().toString()) || TextUtils.isEmpty(emailEt.getText().toString())){
                emailEt.setError(getString(R.string.error_message));
            }
            else if(TextUtils.isEmpty(fullNameEt.getText().toString())){
                fullNameEt.setError(getString(R.string.error_message));
            }
            else if(TextUtils.isEmpty(passwordEt.getText().toString())){
                passwordEt.setError(getString(R.string.error_message));
            }
            else if(passwordEt.getText().toString().length() < 6){
                passwordEt.setError("Lozinka mora sadržati šest ili više karaktera.");
            }
            else if(TextUtils.isEmpty(confirmPasswordEt.getText().toString()) || !TextUtils.equals(passwordEt.getText().toString(), confirmPasswordEt.getText().toString())){
                confirmPasswordEt.setError(getString(R.string.error_message));
            }
            else if(!termsOfUseCb.isChecked()){

                termsOfUseCb.setError("Morate prihvatiti uslove korišćenja.");

            }else{

            if(ApplicationConnectionStatus.getInstance(getActivity().getApplicationContext()).isOnline())
                resizeImage();
            else
                Utility.showNoInternetDialog(getActivity());
            }



    }


    private void showSelectImageDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.selectImageDialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.select_image_dialog, null);
        builder.setView(dialogView);

        ImageButton galleryBtn = dialogView.findViewById(R.id.galleryImageBtn);
        ImageButton cameraBtn = dialogView.findViewById(R.id.cameraImageBtn);

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                RegisterFragment.this.startActivityForResult(galleryIntent, GALLERY_CODE);

            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                RegisterFragment.this.startActivityForResult(intent, CAMERA_CODE );

            }
        });

         dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALLERY_CODE && resultCode == MainActivity.RESULT_OK) {

            profileImageUri = data.getData();

            Glide.with(getActivity().getApplicationContext()).load(profileImageUri).apply(RequestOptions.circleCropTransform()).into(profileImage);




        }else if(requestCode == CAMERA_CODE && resultCode == MainActivity.RESULT_OK){


                bitmap = (Bitmap) data.getExtras().get("data");
                Glide.with(getActivity().getApplicationContext()).load(bitmap).apply(RequestOptions.circleCropTransform()).into(profileImage);

        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        if (dialog != null)
            dialog.dismiss();
    }

    private void saveNewPhoto(Bitmap mBitmap) {
        BackgroundImageResize backgroundImageResize = new BackgroundImageResize(mBitmap);
        Uri uri = null;
        backgroundImageResize.execute(uri);

    }

    private void saveNewPhoto(Uri drinkImageUri) {

        BackgroundImageResize backgroundImageResize = new BackgroundImageResize(null);
        backgroundImageResize.execute(drinkImageUri);

    }


    private class BackgroundImageResize extends AsyncTask<Uri, Integer,String> {

        Bitmap bitmap;

        public BackgroundImageResize(Bitmap bitmap) {

            if(bitmap != null){
                this.bitmap = bitmap;
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Uri... uris) {

            if(bitmap == null){



                    //OVO PRETVARA URI OBJEKAT U BITMAP
                    //OVA LINIJA KODA NEKAD ROTIRA SLIKU
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uris[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


            }
            String imageString ="";

            imageString = getBytesFromBitmap(bitmap, 50);

            return  imageString;
        }

        @Override
        protected void onPostExecute(String imageString) {
            super.onPostExecute(imageString);


            profileImageString = imageString;


            String customerID = UUID.randomUUID().toString();


            Customer customer =  new Customer(customerID, emailEt.getText().toString().trim(), fullNameEt.getText().toString().trim(),
                    passwordEt.getText().toString().trim(), addressEt.getText().toString().trim(), phoneEt.getText().toString().trim()
                    , cityEt.getText().toString().trim(), profileImageString);

            apiInterface = ApiClient.getApiClient().create(Api.class);
            Call<List<Customer>> call = apiInterface.registerCustomer(customer);
            call.enqueue(new Callback<List<Customer>>() {
                @Override
                public void onResponse( Call<List<Customer>> call, Response<List<Customer>> response) {

                    Toast.makeText(getActivity().getApplicationContext(), response.body().get(0).getResponse(), Toast.LENGTH_SHORT).show();

                    LoginFragment fragment = new LoginFragment();
                    fragmentTransaction2.replace(R.id.myContainer, fragment);
                    fragmentTransaction2.commit();


                }

                @Override
                public void onFailure( Call<List<Customer>> call, Throwable t) {
                    Toast.makeText(getActivity().getApplicationContext(), "Došlo je do greške. Registracija nije uspešna. Pokušajte ponovo.", Toast.LENGTH_SHORT).show();
                }
            });

            String test = call.toString();
//            call.enqueue(new Callback<Customer>() {
//                @Override
//                public void onResponse(Call<Customer> call, Response<Customer> response) {
//                   Customer customer = response.body();
//                    Toast.makeText(getActivity().getApplicationContext(), "Uspesna registracija", Toast.LENGTH_SHORT).show();
//
//
//                }
//
//                @Override
//                public void onFailure(Call<Customer> call, Throwable t) {
//
//                    String response = call.toString();
//                    String trw = t.toString();
//                    Toast.makeText(getActivity().getApplicationContext(), "Došlo je do greške. Registracija nije uspešna. Pokušajte ponovo.", Toast.LENGTH_SHORT).show();
//                }
//            });


        }
    }

    private String getBytesFromBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);

        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

    }

    private void resizeImage(){
        if (bitmap == null && profileImageUri == null) {

            bitmap = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.profile_image_placeholder);
            saveNewPhoto(bitmap);

        }else   if (bitmap != null && profileImageUri == null) {
            saveNewPhoto(bitmap);
        }
        else if(bitmap == null && profileImageUri != null){

            saveNewPhoto(profileImageUri);
        }
    }
}
