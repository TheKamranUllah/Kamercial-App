<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['name']) && isset($_POST['price']) && isset($_POST['cid']) && isset($_POST['description']) && isset($_POST['image'])) {
 
    $cid = $_POST['cid'];
    $image = $_POST["image"];
    $name = $_POST['name'];
    $price = $_POST['price'];
    $description = $_POST['description'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
 //Below code was saving, image in a folder called images inside current working directory.
 //Since we are storing image in binary in a blob column, no need to store in folder on server. 
  
  /*  $target_dir = "images/";
    $imageStore = rand()."_".time().".jpeg";
    $target_dir = $target_dir."/".$imageStore;
    file_put_contents($target_dir, base64_decode($image));
*/
    // connecting to db
    $db = new DB_CONNECT();
    $con = $db->connect();
    
 
    // mysql inserting a new row
    
    $result = mysqli_query($con,"INSERT INTO products(name, price, description, image, categid) VALUES('$name', '$price', '$description', '$image', '$cid')");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Product successfully created";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>