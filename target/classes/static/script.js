var shoppingCart = function () {
        var obj = {}, items = [];

        obj.AddItem = function (itemNo, quantity, price) {
            var item = {};
            item.itemNo = itemNo;
            item.quantity = quantity;
            item.price = price;

            var index = -1;
            for (var i = 0; i < items.length; i++) {
                if (items[i].itemNo === itemNo)
             index = i;
              }
             if(index != -1){
            items.splice(index, 1);
}
            items.push(item)
        };

        obj.GetAllItems = function () {
            return items;
        };

        obj.GetItemByNo = function (item) {
            for (var i = 0; i < items.length; i++) {
                if (items[i].itemNo === item)
                    return item[i];
            }
            return null;
        };

        obj.getItemsCount = function () {
            var total = 0;
            for (var i = 0; i < items.length; i++) {
                total = Number(total) + Number(items[i].quantity);
            }
            return total;
        };

        obj.CalculateTotal = function () {
            var total = 0;
            for (var i = 0; i < items.length; i++) {
                total = total + (items[i].quantity * items[i].price);
            }
            return total;
        };

        return obj;
    };

var cart = new shoppingCart();
function updateCart(val){
  const vals = val.split("-");
  var typ = vals[0];
  var price = vals[1];
  var qty = document.getElementsByName(typ)[0].value;

  cart.AddItem(typ, qty, price);
  var total = cart.CalculateTotal();
  var totalItems = cart.getItemsCount();
  document.getElementById("total").value = total.toFixed(2);

   if(document.getElementById("total").value != 0.00) {
        document.getElementById("submit").disabled = false;
        document.getElementById("submit").innerHTML = "Proceed to Checkout ("+totalItems+")";
      }else {
        document.getElementById("submit").disabled = true;
        document.getElementById("submit").innerHTML = "Proceed to PayPal";
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
