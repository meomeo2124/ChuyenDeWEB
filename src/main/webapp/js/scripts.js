/*!
* Start Bootstrap - Shop Homepage v5.0.6 (https://startbootstrap.com/template/shop-homepage)
* Copyright 2013-2023 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-shop-homepage/blob/master/LICENSE)
*/
// This file is intentionally blank
// Use this file to add JavaScript to your project

	function loadMore() {
	    var amount = document.getElementsByClassName("product-count").length; // Get the current number of products
	    $.ajax({
	        url: "/zzzz/load",
	        type: "GET", // Send it through GET method
	        data: {
	            exists: amount // Corrected from 'exits' to 'exists'
	        },
	        success: function(data) {
	            var row = document.getElementById("content");
	            row.innerHTML += data;	
	        },
	        error: function(xhr) {
	            console.error("Error loading more products:", xhr);
	            // Optionally, you can display an error message to the user
	        }
	    });
	}

// Nếu bạn lấy quantity từ 1 thẻ input có id là "quantityInput"
function updateQuantity() {
	var amount = document.getElementById("quantityInput").value; // BẠN PHẢI THÊM DÒNG NÀY ĐỂ LẤY VALUE
	$.ajax({
		url: "/zzzz/loadCart",
		type: "GET",
		data: {
			exists: amount
		},
		success: function(data) {
			var row = document.getElementById("contentUpdateQuantity");
			row.innerHTML = data; // Thường thì load cart sẽ update thay thế (innerHTML = data) chứ ko cộng dồn (+=) như loadMore
		},
		error: function(xhr) {
			console.error("Error loading cart:", xhr);
		}
	});
}
	
	
	