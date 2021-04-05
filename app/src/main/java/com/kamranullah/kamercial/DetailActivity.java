package com.kamranullah.kamercial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.kamranullah.kamercial.Adapters.myAdapter;
import com.karumi.dexter.Dexter;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class DetailActivity extends AppCompatActivity {

    int productNumber = 1;
    private RoundedImageView roundedImageView;
    private TextView pName, pPrice, pDescription;
    private Button backBtn, buyBtn;
    private ImageButton plusBtn, minusBtn;
    private TextView productTV;

    private String theID;
    private static String get_product = "http://192.168.43.133/AndroidApi/get_details_product.php";
    List<productModel> itemList;
    Context context;

    private String name, price, description, image;
    private int numberOfProductSelected = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        theID = getIntent().getExtras().get("productID").toString();

        //Toast.makeText(this, "The ID IS: "+theID, Toast.LENGTH_SHORT).show();

        init();

        loadData();

     backBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             backBTN();
         }
     });

     buyBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             bottomSheetMethod();
         }
     });

     plusBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

             if (numberOfProductSelected < 10)
             {
                 numberOfProductSelected++;

                 productTV.setText(String.valueOf(numberOfProductSelected));
             }
             else
                 {
                     Toast.makeText(DetailActivity.this, "Can not buy more thant "+numberOfProductSelected+" at once!", Toast.LENGTH_SHORT).show();
                 }
         }
     });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (numberOfProductSelected > 1)
                {
                    numberOfProductSelected--;
                    productTV.setText(String.valueOf(numberOfProductSelected));
                }
                else
                {
                    Toast.makeText(DetailActivity.this, "Can not buy less thant "+numberOfProductSelected, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void init()
    {
        roundedImageView = (RoundedImageView) findViewById(R.id.detail_product_image);
        pName = (TextView) findViewById(R.id.detail_product_name);
        pPrice = (TextView) findViewById(R.id.detail_product_price);
        pDescription = (TextView) findViewById(R.id.detail_product_description);

        backBtn = (Button) findViewById(R.id.go_back_btn2);
        buyBtn = (Button) findViewById(R.id.buy_btn);
        itemList = new ArrayList<>();

        plusBtn = findViewById(R.id.add_image_button);
        minusBtn = findViewById(R.id.minus_image_button);
        productTV = findViewById(R.id.number_of_item_tv);
    }

    private void loadData() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,get_product, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("products");

                    //The below get a value from php code and compare it with a string.
//                    if(jsonObject.getString("message").equals("No products found"))
//                    {
//                        Toast.makeText(DetailActivity.this, "No Products Found", Toast.LENGTH_SHORT).show();
//                    }
//                    else if (jsonObject.getString("message").equals("No Id Found"))
//                    {
//                        Toast.makeText(DetailActivity.this, "No ID FOUND", Toast.LENGTH_SHORT).show();
//                    }


                    for (int i = 0; i<jsonArray.length(); i++){


                        JSONObject o = jsonArray.getJSONObject(i);
                        name = o.getString("name");
                        price = o.getString("price");
                        description = o.getString("description");
                        image = o.getString("image");

                        pName.setText(name);
                        pPrice.setText(price);
                        pDescription.setText(description);
                        stringToBitmap(image);
//                        Toast.makeText(DetailActivity.this, ""+name, Toast.LENGTH_SHORT).show();

                    }
                }catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(DetailActivity.this, "Failed",Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String , String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", theID);
                return params;
            }
        };
        RequestHandler.getInstance(DetailActivity.this).addToRequestQueue(stringRequest);

    }

    public void stringToBitmap(String str) {
        byte[] bytesImage = Base64.decode(str, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length);
        roundedImageView.setImageBitmap(bitmap);
        // using glide
      //  Glide.with(this).load(bytesImage).asBitmap().into(ivImage);
    }

    public void backBTN()
    {
        startActivity(new Intent(DetailActivity.this, MainActivity.class));
    }

    private void bottomSheetMethod()
    {

        final String  shareProduct= name +"" +price+" "+" For more detail search the product on Kamercial App!";


        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetailActivity.this, R.style.BottomSheetStyle);

         final View bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.bottom_sheet, (LinearLayout)findViewById(R.id.bottom_sheet_container));


        TextView bProductName = bottomSheetView.findViewById(R.id.product_name);
        bProductName.setText(name);

        TextView bProductPrice = bottomSheetView.findViewById(R.id.product_price);
        bProductPrice.setText(price);

        TextView bProductDescription = bottomSheetView.findViewById(R.id.product_description);
        bProductDescription.setText(description);

        RoundedImageView bProductImage = bottomSheetView.findViewById(R.id.product_image);

        byte[] bytesImage = Base64.decode(image, Base64.DEFAULT);
        final Bitmap bitmap = BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length);
        bProductImage.setImageBitmap(bitmap);

        //if you use int value a textview is a string resourceNotFoundException will be thrown.
        TextView numberofProduct = bottomSheetView.findViewById(R.id.number_of_item_tv);
        numberofProduct.setText(String.valueOf(numberOfProductSelected));

        LinearLayout shareProductLayout = bottomSheetView.findViewById(R.id.share_product_layout);
        shareProductLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // Toast.makeText(DetailActivity.this, "Share", Toast.LENGTH_SHORT).show();
/*

                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, name);
                whatsappIntent.setType("text/plain");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, price);
                whatsappIntent.setType("image/jpeg");
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, bitmap);
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                whatsappIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                try {
                    //context.startActivity(whatsappIntent);

                    context.startActivity(Intent.createChooser(whatsappIntent, "Select an App to share With:"));

                } catch (android.content.ActivityNotFoundException ex) {

                    Toast.makeText(getApplicationContext(), "No supported app exists!", Toast.LENGTH_SHORT).show();
                } catch (RuntimeException e){e.printStackTrace();}
*/


                Intent productShareIntent = new Intent(Intent.ACTION_SEND);
                productShareIntent.setType("text/plain");
                productShareIntent.putExtra(Intent.EXTRA_TEXT, shareProduct);
                startActivity(Intent.createChooser(productShareIntent, "Select An App To Share With:"));
            }
        });

        bottomSheetView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(DetailActivity.this, "Dismiss", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetView.findViewById(R.id.buy_order_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showSnackbar("Congrats your order is recieved");
                bottomSheetDialog.dismiss();
            }
        });



       /* bottomSheetView.findViewById(R.id.add_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (productNumber >= 10)
                {
                    Toast.makeText(DetailActivity.this, "Can not buy more than 10 at once", Toast.LENGTH_SHORT).show();
                }else
                {
                    productNumber++;
                    bottomSheetView.findViewById(R.id.number_of_item_tv).setContentDescription(String.valueOf(productNumber));

                }
            }
        });

        bottomSheetView.findViewById(R.id.minus_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (productNumber == 0)
                {
                    Toast.makeText(DetailActivity.this, "Can not decreasd", Toast.LENGTH_SHORT).show();
                }else
                    {

                        productNumber--;
                        bottomSheetView.findViewById(R.id.number_of_item_tv).setContentDescription(String.valueOf(productNumber));

                    }
            }
        });*/

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void showSnackbar(String message)
    {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, ""+message, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(DetailActivity.this, "Snackbar Closed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

}