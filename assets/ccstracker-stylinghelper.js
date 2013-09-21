//For optimizing with mobile: http://stackoverflow.com/questions/548892/tips-for-optimizing-a-website-for-androids-browser

function getElementsByClassName(className) {
	
	var returns = new Array();
	
	for (var i = 0; i < document.all.length; i++) {
		if (document.all(i).getAttribute("class") == className) {
			returns[returns.length] = document.all(i);
		}
	}
	
	return returns;
}

function setFontSize() {
	
	var tables = document.getElementsByTagName("table");
	
	for (var i = 0; i < tables.length; i++) {
		
		var scrollWidth = tables[i].scrollWidth;
		var clientWidth = tables[i].clientWidth;
		
		//if (screen.width != 0 && scrollWidth >= screen.width + 10) {
		if (Android.getOrientation() == 0 && tables[i].getElementsByTagName("tr")[0].getElementsByTagName("td").length > 2) {
			tables[i].style.fontSize = "13px";
		}
		else {
			tables[i].style.fontSize = "20px";
		}
		
	}
}
	
window.onload = function() {
	
	//Disable wordwrap
	var tds = document.getElementsByTagName("td");
	for (var i = 0; i < tds.length; i++) {
		tds[i].setAttribute("nowrap", "nowrap");
	}
	
	setFontSize();
	window.onresize = function() { setFontSize(); }
	
};