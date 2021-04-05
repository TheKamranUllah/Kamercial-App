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
 
if (isset($_POST['id'])) {
 
    
    $id = $_POST["id"];


// get all products from products table
$result = mysqli_query($con,"SELECT * FROM products WHERE pid ='".$id."' ") or die(mysqli_error());
 
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

}else
{

	$response["success"] = 0;
	$response["message"] = "No Id Found";
}
?>