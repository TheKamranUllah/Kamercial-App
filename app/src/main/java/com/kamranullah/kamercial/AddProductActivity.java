package com.kamranullah.kamercial;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private ImageView productImage;
    private EditText productName, productPrice, productDescription;
    private Button addProduct, goBackBtn;
    public static int Gallery_Pick = 1;
    private Uri productImageURI;
    Bitmap bitmap;
    String encodedImage;
    private Spinner category;
    private int categoryID;

    private static String url_create_product = "http://192.168.43.133/AndroidApi/create_product.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

             init();

             addProduct.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     pickValues();
                 }
             });

             productImage.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {

                     Dexter.withContext(getApplicationContext())
                             .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                             .withListener(new PermissionListener() {
                                 @Override
                                 public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                     pickingImageFromUser();
                                 }

                                 @Override
                                 public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {


                                 }

                                 @Override
                                 public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                                     permissionToken.continuePermissionRequest();
                                 }
                             }).check();

                 }
             });

             goBackBtn.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {

                     goBackIntent();
                 }
             });

    }

    private void pickingImageFromUser()
    {
        Intent imageIntent = new Intent();
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        imageIntent.setType("image/*");
        startActivityForResult(imageIntent, Gallery_Pick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null)
        {
            productImageURI = data.getData();

            try {

                //converting from uri to bitmap
                InputStream inputStream = getContentResolver().openInputStream(productImageURI);
                bitmap = BitmapFactory.decodeStream(inputStream);

                convertingBitmapToByteArray(bitmap);

                //setting image on imagview.
                 productImage.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //productImage.setImageURI(productImageURI);
        }
    }

    private void convertingBitmapToByteArray(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] imageBytes = stream.toByteArray();

        //Storing byte array data in a string variable.
        encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }

    private void pickValues()
    {

        String theCategory = category.getSelectedItem().toString();
        //  Toast.makeText(getApplicationContext(), ""+theCategory, Toast.LENGTH_SHORT).show();

        switch (theCategory)
        {
            case "Sports":
                categoryID = 1;
//                Toast.makeText(getApplicationContext(), ""+theCategory, Toast.LENGTH_SHORT).show();
                break;
            case "Fashion":
                categoryID = 2;
  //              Toast.makeText(getApplicationContext(), ""+theCategory, Toast.LENGTH_SHORT).show();
                break;
            case "Education":
                categoryID = 3;
    //            Toast.makeText(getApplicationContext(), ""+theCategory, Toast.LENGTH_SHORT).show();
                break;
            case "Electronics":
                categoryID = 4;
      //          Toast.makeText(getApplicationContext(), ""+theCategory, Toast.LENGTH_SHORT).show();
                break;
            case "Food":
                categoryID = 5;
        //        Toast.makeText(getApplicationContext(), ""+theCategory, Toast.LENGTH_SHORT).show();
                break;
                default: categoryID = 1;

        }

       final String name = productName.getText().toString();
       final String price = productPrice.getText().toString();
       final String description = productDescription.getText().toString();

       if (name.isEmpty())
       {
           showSnackbar("Please Enter Product Name!");
       }
       else if(price.isEmpty())
       {
           showSnackbar("Please Enter a Price");
       }
       else if(theCategory.isEmpty())
       {
           showSnackbar("Please Choose a category!");
       }
       else if (description.isEmpty())
       {
           showSnackbar("Please Enter a Description!");
       }
       //Below is the method of how to handle the null pointer exception without the use of try catch statement.
       else if (encodedImage == null)
       {
        showSnackbar("Please Choose a Picture");
       }
      else
           {

               StringRequest stringRequest = new StringRequest(Request.Method.POST, url_create_product, new Response.Listener<String>() {
                   @Override
                   public void onResponse(String response) {

                       try{
                           JSONObject jsonObject = new JSONObject(response);
                           Toast.makeText(AddProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                           if(jsonObject.getString("message").equals("Product successfully created")){
                               startActivity(new Intent(AddProductActivity.this,MainActivity.class));
                               finish();
                           }
                       }catch (JSONException e) {
                           e.printStackTrace();
                           Toast.makeText(AddProductActivity.this, "Exception GGGG", Toast.LENGTH_SHORT).show();
                       }
                   }
               }, new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {

                       Toast.makeText(AddProductActivity.this, "Failed to Data > "+error,Toast.LENGTH_SHORT).show();
                   }
               }){
                   protected Map<String , String> getParams() throws AuthFailureError {
                       Map<String , String> params = new HashMap<>();

                       params.put("name", name);
                       params.put("price", price);
                       params.put("cid", String.valueOf(categoryID));
                       params.put("description", description);
                       params.put("image",encodedImage);

                       return params;
                   }
               };
               RequestHandler.getInstance(AddProductActivity.this).addToRequestQueue(stringRequest);

               releaseResources( );


           }

    }

    private void showSnackbar(String message)
    {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, ""+message, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(AddProductActivity.this, "Snackbar Closed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

    private void releaseResources() {


        productImage.setImageDrawable(getDrawable(R.drawable.imagesecond));
        productName.setText(" ");
        productPrice.setText(" ");
        productDescription.setText(" ");

        productName.setHint("Enter Product Name");
        productPrice.setHint("Enter Price");
        productName.setHint("Enter Description");
    }


    private void init()
    {

        category = (Spinner) findViewById(R.id.category_name);
        productImage = (ImageView) findViewById(R.id.product_image);
        productName = (EditText) findViewById(R.id.product_name);
        productPrice= (EditText) findViewById(R.id.product_price);
        productDescription = (EditText) findViewById(R.id.product_description);

        addProduct = (Button) findViewById(R.id.add_product_btn);
        goBackBtn = (Button) findViewById(R.id.go_back_btn);
    }

    private void goBackIntent()
    {
        Intent backIntent = new Intent(AddProductActivity.this, MainActivity.class);
        startActivity(backIntent);
    }
}
