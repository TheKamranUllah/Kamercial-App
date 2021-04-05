package com.kamranullah.kamercial.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.kamranullah.kamercial.CartActivity;
import com.kamranullah.kamercial.DetailActivity;
import com.kamranullah.kamercial.R;
import com.kamranullah.kamercial.RequestHandler;
import com.kamranullah.kamercial.cartModel;
import com.kamranullah.kamercial.productModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class cartAdapter extends RecyclerView.Adapter<cartAdapter.cartViewHolder> {


    private static String url_delete_cart = "http://192.168.43.133/AndroidApi/delete_product.php";
    private List<cartModel> cartList;
    private Context context;

    public cartAdapter(List<cartModel> theList, Context context) {

        this.cartList = theList;
        this.context = context;
    }


    @NonNull
    @Override
    public cartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_card_captions_layout, parent, false);

        return new cartAdapter.cartViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull cartViewHolder holder, int position) {


        final cartModel modelc = cartList.get(position);

        holder.productName.setText(modelc.getName());
        holder.productPrice.setText(modelc.getPrice());

        //Below is the method to read image from blob column and convert to bitmap

    try {

    byte[] bytesImage = Base64.decode(modelc.getImage(), Base64.DEFAULT);
    Bitmap bitmap = BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length);
    holder.productImage.setImageBitmap(bitmap);

    }
    catch (IllegalArgumentException e){e.printStackTrace();}

//        Toast.makeText(context, ""+model.getImage(), Toast.LENGTH_SHORT).show();

        holder.buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent detailActivityIntent = new Intent(context, DetailActivity.class);
                detailActivityIntent.putExtra("productID", modelc.getId());
                detailActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(detailActivityIntent);
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(context, ""+modelc.getOrderID(), Toast.LENGTH_SHORT).show();

                deleteFromCart(modelc.getOrderID());

            }
        });
    }

    private void deleteFromCart(String id)
    {
        final String theID = id;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_delete_cart, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.getString("message").equals("Product successfully Removed")){

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
            protected Map<String , String> getParams() throws  AuthFailureError {
                Map<String , String> params = new HashMap<>();

                params.put("pid", theID);

                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

        selfIntent();
    }

    private void selfIntent()
    {
        Intent selfIntent1 = new Intent(context, CartActivity.class);
        selfIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(selfIntent1);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    class cartViewHolder extends RecyclerView.ViewHolder {

        public TextView productName, productPrice;
        public ImageView productImage;
        public CardView cardView;
        public Button deleteBtn, buyNowBtn;


        public cartViewHolder(@NonNull View itemView) {
            super(itemView);

            deleteBtn = itemView.findViewById(R.id.delete_cart_button);
            productImage = itemView.findViewById(R.id.cart_info_image1);
            productName = itemView.findViewById(R.id.cart_product_name);
            productPrice = itemView.findViewById(R.id.cart_product_price);
            cardView = itemView.findViewById(R.id.cart_cardview);
            buyNowBtn = itemView.findViewById(R.id.buy_cart_button);
        }
    }
}
