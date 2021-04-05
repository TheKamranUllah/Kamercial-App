<?php
 
/*
 * Following code will list all the products
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
$con = $db->connect();
 
if (isset($_POST['justcart'])) {

$result = mysqli_query($con,"SELECT * FROM cart_products") or die();

// check for empty result
if (mysqli_num_rows($result) > 0) {
    // looping through all results
    // products node

    $response["cart_products"] = array();
 
    while ($row = mysqli_fetch_array($result)) {
        // temp user array
        $product = array();
        $product["OrderID"] = $row["OrderID"];
        $product["p_name"] = $row["p_name"];
        $product["product_ID"] = $row["product_ID"];
    
 
        // push single product into final response array
        array_push($response["cart_products"], $product);
    }
    // success
    $response["success"] = 1;
 
    // echoing JSON response
    echo json_encode($response);
}
 else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No products found";
 
    // echo no users JSON
    echo json_encode($response);
}

    
}
else
{
    $result = mysqli_query($con,
     "SELECT p.pid, p.name, p.price, p.description, p.image, cp.OrderID FROM products AS p INNER JOIN cart_products AS cp ON cp.product_ID = p.pid");

    // check for empty result
if (mysqli_num_rows($result) > 0) {
    // looping through all results
    // products node
    $response["products"] = array();
 
    while ($row = mysqli_fetch_array($result)) {
        // temp user array
        $product = array();
        $product["pid"] = $row["pid"];
        $product["name"] = $row["name"];
        $product["price"] = $row["price"];
        $product["description"] = $row["description"];
        $product["image"] = $row["image"];
        $product["OrderID"] =$row["OrderID"];
      
 
        // push single product into final response array
        array_push($response["products"], $product);
    }
    // success
    $response["success"] = 1;
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No products found";
 
    // echo no users JSON
    echo json_encode($response);
}

}


?>