package com.kamranullah.kamercial;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.kamranullah.kamercial.Adapters.myAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class
MainActivity extends AppCompatActivity {

    private ImageView cartImage, categorySearch;
    private TextView numberOnCart;
    private LinearLayout linearLayout;
    private AppBarLayout ourAppbar;
    private int left = 20, top = 20, right = 20, bottom = 20;
    private FloatingActionButton button;
    private Spinner categoryName;
    private SearchView searchView;
    private int width = FrameLayout.LayoutParams.MATCH_PARENT;
    //  private int beforetouchwidth = FrameLayout.LayoutParams.WRAP_CONTENT;
    private int beforetouchwidth = 90;
    private int height = 90;
    String[] categories = new String[]{"By Category", "Sports", "Fashion", "Education", "Electronics", "Food"};
    private int categoryID;
    private int numberOfCartProduct = 0;
    private List<Integer> theCartProductList;

    private boolean isFromSpinner = false;
    private boolean wificonnection = false;
    private boolean mobileconnection = false;

    RecyclerView allProductsRecycler;
    List<productModel> itemList;
    private static String get_products = "http://192.168.43.133/AndroidApi/get_all_products.php";
    private static String get_cart_products = "http://192.168.43.133/AndroidApi/get_cart_products.php";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();

        isInternetAvailable();


        ArrayAdapter<String> myAdater = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        categoryName.setAdapter(myAdater);


        cartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent cartIntent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(cartIntent);
               // Toast.makeText(MainActivity.this, "Cart is clicked", Toast.LENGTH_SHORT).show();

            }
        });

        categorySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String myCategory = categoryName.getSelectedItem().toString();

                if ("Sports".equals(myCategory)) {
                    categoryID = 1;
                   loadData(categoryID);
                    //    Toast.makeText(getApplicationContext(), "" + myCategory, Toast.LENGTH_SHORT).show();
                } else if ("Fashion".equals(myCategory)) {
                    categoryID = 2;
                    loadData(categoryID);
                    // Toast.makeText(getApplicationContext(), "" + myCategory, Toast.LENGTH_SHORT).show();
                } else if ("Education".equals(myCategory)) {
                    categoryID = 3;
                    loadData(categoryID);
                    // Toast.makeText(getApplicationContext(), "" + myCategory, Toast.LENGTH_SHORT).show();
                } else if ("Electronics".equals(myCategory)) {
                    categoryID = 4;
                    loadData(categoryID);
                    //Toast.makeText(getApplicationContext(), "" + myCategory, Toast.LENGTH_SHORT).show();
                } else if ("Food".equals(myCategory)) {
                    categoryID = 5;
                    loadData(categoryID);
                    //Toast.makeText(getApplicationContext(), "" + myCategory, Toast.LENGTH_SHORT).show();
                }
               else
                   {
                       isFromSpinner = true;
                       loadData();
                   }
            }
        });

        //To define height and width programmatically, you must define layoutParams after View's(Search View) Parent View (CollapsingToolbarLayout).

        final CollapsingToolbarLayout.LayoutParams lp = new CollapsingToolbarLayout.LayoutParams(width, height);
        //final CollapsingToolbarLayout.LayoutParams lp2 = new CollapsingToolbarLayout.LayoutParams(beforetouchwidth,height);


        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                categorySearch.setVisibility(View.GONE);
                searchView.setLayoutParams(lp);
                categoryName.setVisibility(View.GONE);
                cartImage.setVisibility(View.GONE);
                numberOnCart.setVisibility(View.GONE);

                //study on youtube::  searchView.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT));

//                Toast.makeText(MainActivity.this, "This is a toast.", Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                loadData(s);
                //Toast.makeText(MainActivity.this, "" + s, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //Toast.makeText(MainActivity.this, "" + s, Toast.LENGTH_SHORT).show();
//didn't call the moethod here cause loading too much duplicate data, cause everytime text changes the method is called and load data.
                loadData(s);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onClose() {

                //below code is also used in searchQuery sometimes
               /* searchText =  searchView.getQuery().toString();
                Toast.makeText(MainActivity.this, ""+searchText, Toast.LENGTH_SHORT).show();*/

               /*
                CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                        CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                        CoordinatorLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(left, top, right, bottom);
                searchView.setLayoutParams(params);

                searchView.setHorizontalGravity(GravityCompat.END);
                searchView.setGravity(Gravity.CENTER_VERTICAL);*/

                categorySearch.setVisibility(View.VISIBLE);
                categoryName.setVisibility(View.VISIBLE);
                cartImage.setVisibility(View.VISIBLE);
                numberOnCart.setVisibility(View.VISIBLE);
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                jumToAddProduct();

            }
        });

    }

    private void init() {
        button = findViewById(R.id.floating_button);
        searchView = (SearchView) findViewById(R.id.search_to_enter);
        categoryName = (Spinner) findViewById(R.id.category_name);
        ourAppbar = findViewById(R.id.appbar_x);
        numberOnCart = (TextView) findViewById(R.id.number_on_cart);
        cartImage = (ImageView) findViewById(R.id.cart_image);
        categorySearch = (ImageView) findViewById(R.id.go_category_search);
        ourAppbar.setAlpha(0.75f);

        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_container);
        ourAppbar.setAlpha(0.75f);
        button.setAlpha(0.75f);


        allProductsRecycler = findViewById(R.id.all_products);
        itemList = new ArrayList<>();
        theCartProductList = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        allProductsRecycler.setLayoutManager(gridLayoutManager);

    }

    private void isInternetAvailable()
    {
        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
*/
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected())
                {
                    wificonnection = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
                    mobileconnection = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;

                    if (wificonnection)
                    {

                        loadData();

                        loadDataToCart();

                    showSnackbar("WIFI CONNECTED!");
                    }
                    else if (mobileconnection)
                    {
                        loadData();

                        loadDataToCart();

                   showSnackbar("CELLULAR DATA CONNECTED!");
                    }
                }
                else
                {
                       showSnackbar("Please Check Your Internet Connection!");

                }

/*
            }

        }, 3000);
*/

    }



    //method to simply load all products
    private void loadData() {

        //We clear data because when a category is search and then we want to search get back all products by selecting by_category option
        //in spinner it used to get that category twice and load all data.

           if (isFromSpinner)
            {
                    myAdapter adapter = new myAdapter(itemList, getApplicationContext());
                    adapter.clearApplications();

            }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_products, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("products");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        productModel item = new productModel(
                                o.getString("pid"),
                                o.getString("name"),
                                o.getString("price"),
                                o.getString("description"),
                                o.getString("image")

                        );
                        itemList.add(item);
                        myAdapter adapter = new myAdapter(itemList, getApplicationContext());
                        allProductsRecycler.setAdapter(adapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        RequestHandler.getInstance(MainActivity.this).addToRequestQueue(stringRequest);

    }

    private void jumToAddProduct() {
        Intent addProductIntent = new Intent(MainActivity.this, AddProductActivity.class);
        startActivity(addProductIntent);
    }

    //method to order product by categories.
    private void loadData(final int id) {

        //Below two lines of code is to remove all product or items from recyclerview.
        myAdapter adapter = new myAdapter(itemList, getApplicationContext());
        adapter.clearApplications();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_products, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("products");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        productModel item = new productModel(
                                o.getString("pid"),
                                o.getString("name"),
                                o.getString("price"),
                                o.getString("description"),
                                o.getString("image")

                        );
                        itemList.add(item);
                        myAdapter adapter = new myAdapter(itemList, getApplicationContext());
                        allProductsRecycler.setAdapter(adapter);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Category Search Failed", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("category_id", String.valueOf(id));
                return params;
            }
        };
        RequestHandler.getInstance(MainActivity.this).addToRequestQueue(stringRequest);

    }

    //Method for android search
    private void loadData(final String searchText) {

        //Below two lines of code is to remove products or item from recyclerview to avoid duplicates
        myAdapter adapter = new myAdapter(itemList, getApplicationContext());
        adapter.clearApplications();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_products, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("products");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        productModel item = new productModel(
                                o.getString("pid"),
                                o.getString("name"),
                                o.getString("price"),
                                o.getString("description"),
                                o.getString("image")

                        );
                        itemList.add(item);
                        myAdapter adapter = new myAdapter(itemList, getApplicationContext());
                        allProductsRecycler.setAdapter(adapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Search Failed", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("search_text", searchText);
                return params;
            }
        };
        RequestHandler.getInstance(MainActivity.this).addToRequestQueue(stringRequest);

    }

    private void loadDataToCart() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_cart_products, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

//TO KNOW WHAT VALUES YOU ARE GETTING TRY TO RUN PHP CODE VIA DUMMY FORM HTML FILE, YOU WILL SEE DATA IN JASON FORMATE
//INSIDE THE BROWSER CAUSE WE HAVE ECHO THE RETRIVED DATA IN JASON INSIDE THE API FILE.

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("cart_products");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject o = jsonArray.getJSONObject(i);
                        int OrderID = o.getInt("OrderID");
                        theCartProductList.add(OrderID);


                    }
                //WE ARE GETTING ROWS HERE IN ABOVE CODE AND USING LIST.SIZE METHOD THAT RETURNED NUMBER OF ROWS IN A LIST.
                       settingCartProductNumber(theCartProductList);
                    //Toast.makeText(MainActivity.this, ""+theCartProductList.size(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Cart data Failed", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("justcart", "OnlyForCart");
                return params;
            }
        };
        RequestHandler.getInstance(MainActivity.this).addToRequestQueue(stringRequest);

    }

    private void settingCartProductNumber(List<Integer> theCartProductList)
    {
        if (!theCartProductList.isEmpty())
        {
            numberOfCartProduct = theCartProductList.size();
            numberOnCart.setText(String.valueOf(numberOfCartProduct));
           // Toast.makeText(MainActivity.this, "" + numberOfCartProduct, Toast.LENGTH_SHORT).show();

        }
        else
        {
            numberOnCart.setText(String.valueOf(numberOfCartProduct));
        }

    }


    private void showSnackbar(String message)
    {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, ""+message, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(MainActivity.this, "Snackbar Closed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

}
