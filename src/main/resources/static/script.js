
var items = [];
var clicked = false;
var shoppingCart = function() {
	var obj = {};

	if (sessionStorage.getItem("sessionItems") != null) {
		items = JSON.parse(sessionStorage.getItem("sessionItems"));
	}

	obj.AddItem = function(itemNo, quantity, price) {
		var item = {};
		item.itemNo = itemNo;
		item.quantity = quantity;
		item.price = price;

		var index = -1;
		for (var i = 0; i < items.length; i++) {
			if (items[i].itemNo === itemNo)
				index = i;
		}
		if (index != -1) {
			items.splice(index, 1);
		}
		items.push(item)
		sessionStorage.setItem("sessionItems", JSON.stringify(items));
	};

	obj.GetAllItems = function() {
		return items;
	};

	obj.GetItemByNo = function(item) {
		for (var i = 0; i < items.length; i++) {
			if (items[i].itemNo === item)
				return item[i];
		}
		return null;
	};

	obj.getItemsCount = function() {
		var total = 0;
		for (var i = 0; i < items.length; i++) {
			if (items[i].quantity != 0) {
				total = Number(total) + Number(items[i].quantity);
			}
		}
		return total;
	};

	obj.CalculateTotal = function() {
		var total = 0;
		for (var i = 0; i < items.length; i++) {
			if (items[i].quantity != 0) {
				total = total + (items[i].quantity * items[i].price);
			}
		}
		return total;
	};

	return obj;
};

var cart = new shoppingCart();
function updateCart(val) {
	const vals = val.split("-");
	var typ = vals[0];
	var price = vals[1];
	var qty = document.getElementsByName(typ)[0].value;

	cart.AddItem(typ, qty, price);
	var total = cart.CalculateTotal();
	var totalItems = cart.getItemsCount();

	sessionStorage.removeItem("sessionTotal");
	sessionStorage.setItem("sessionTotal", total.toFixed(2));

	document.getElementById("total").value = sessionStorage.getItem("sessionTotal");



	if (document.getElementById("total").value != 0.00) {
		document.getElementById("submit").disabled = false;
		document.getElementById("submit").innerHTML = "Proceed to Checkout (" + totalItems + ")";
	} else {
		document.getElementById("submit").disabled = true;
		document.getElementById("submit").innerHTML = "Proceed to Checkout";
	}

}

function myFunction() {
	var x = document.getElementById("myTopnav");
	if (x.className === "topnav") {
		x.className += " responsive";
	} else {
		x.className = "topnav";
	}
}

window.onload = function() {



	if (window.performance.getEntriesByType) {
		if (window.performance.getEntriesByType("navigation")[0].type === "reload") {
			sessionStorage.clear();
		}
	}

	if (window.location.href.includes("success")) {
		sessionStorage.clear();
	}

	document.getElementById("total").value = sessionStorage.getItem("sessionTotal");
	if (document.getElementById("total").value != 0.00) {
		document.getElementById("submit").disabled = false;
		document.getElementById("submit").innerHTML = "Proceed to Checkout (" + cart.getItemsCount() + ")";
	} else {
		document.getElementById("submit").disabled = true;
		document.getElementById("submit").innerHTML = "Proceed to Checkout";
	}
};

/*function goToPay() {
	clicked = true;
}

window.onbeforeunload = function(e) {
	e.preventDefault();
	if (clicked == false) {
		sessionStorage.removeItem("sessionTotal");
	}
};*/