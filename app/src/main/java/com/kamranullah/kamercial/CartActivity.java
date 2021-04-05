package com.kamranullah.kamercial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kamranullah.kamercial.Adapters.cartAdapter;
import com.kamranullah.kamercial.Adapters.myAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cart_items;
    private List<cartModel> cartList;
    private static String get_cart_products = "http://192.168.43.133/AndroidApi/get_cart_products.php";
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        init();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Your Cart");

        loadData();
    }

    private void init() {

        mToolbar = findViewById(R.id.cart_appbar_layout);
        cart_items = findViewById(R.id.cart_recyclerview);
        cartList = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        cart_items.setLayoutManager(gridLayoutManager);

    }

    private void loadData() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_cart_products, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

              //  Toast.makeText(getApplicationContext(), "Show something", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("products");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        cartModel item = new cartModel(
                                o.getString("pid"),
                                o.getString("name"),
                                o.getString("price"),
                                o.getString("description"),
                                o.getString("image"),
                                o.getString("OrderID")

                        );
                        cartList.add(item);
                        cartAdapter adapter = new cartAdapter(cartList, getApplicationContext());
                        cart_items.setAdapter(adapter);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(CartActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        RequestHandler.getInstance(CartActivity.this).addToRequestQueue(stringRequest);

    }

}
