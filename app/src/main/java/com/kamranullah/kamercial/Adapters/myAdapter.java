package com.kamranullah.kamercial.Adapters;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.kamranullah.kamercial.AddProductActivity;
import com.kamranullah.kamercial.DetailActivity;
import com.kamranullah.kamercial.MainActivity;
import com.kamranullah.kamercial.R;
import com.kamranullah.kamercial.RequestHandler;
import com.kamranullah.kamercial.productModel;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myAdapter extends RecyclerView.Adapter<myAdapter.myViewHolder> {

    private static String url_add_cart = "http://192.168.43.133/AndroidApi/add_to_cart.php";
    private List<productModel> itemList;
    private Context context;

    //variables to update cart
    private int numberOfCartProduct = 0;
    private List<Integer> theCartProductList;
    private static String get_cart_products = "http://192.168.43.133/AndroidApi/get_cart_products.php";


    public myAdapter(List<productModel> theList, Context context) {

        this.itemList = theList;
        this.context = context;
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_captions_layout, parent, false);

        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        final productModel model = itemList.get(position);


        holder.productName.setText(model.getName());
        holder.productPrice.setText(model.getPrice());

        //Below is the method to read image from blob column and convert to bitmap

                byte[] bytesImage = Base64.decode(model.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length);
                holder.productImage.setImageBitmap(bitmap);

//        Toast.makeText(context, ""+model.getImage(), Toast.LENGTH_SHORT).show();

        holder.buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent detailActivityIntent = new Intent(context, DetailActivity.class);
                detailActivityIntent.putExtra("productID", model.getId());
                detailActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(detailActivityIntent);
            }
        });

        holder.cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addProductToCart(model.getId(), model.getName());

            }
        });

    }



    private void addProductToCart( final String id, final String name)
    {
        final String theID = id;
        final String theName = name;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_add_cart, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.getString("message").equals("Product added to cart")){

                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(context, "Exception GGGG", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, "Failed to Data > "+error,Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String , String> getParams() throws AuthFailureError {
                Map<String , String> params = new HashMap<>();

                params.put("id", theID);
                params.put("name", theName);

                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);


    }


    public void clearApplications() {
        int size = this.itemList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                itemList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        public TextView productName, productPrice;
        public ImageView productImage;
        public CardView cardView;
        public Button cartBtn, buyNowBtn;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            cartBtn = itemView.findViewById(R.id.add_to_cart_button);
            productImage = itemView.findViewById(R.id.info_image1);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            cardView = itemView.findViewById(R.id.cardview);
            buyNowBtn = itemView.findViewById(R.id.buy_now_button);
        }
    }
}
