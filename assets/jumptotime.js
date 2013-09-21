$(document).ready(function() {
	
	/*
		TODO: Account for weekends
	*/
	
	var jumpToNewTime = false;
	
	//When the page loads, find the next stop time on the page and jump to it
	findNextTime();
	jumpToNextStop();
	
	//Determine next stop time every time the window comes back into focus
	var inFocus = true;
	$(window).blur(function() {
		inFocus = false;
	});
	
	$(window).focus(function() {
		if (!inFocus) {
			
			//If: #nextstoptime has not changed since last refresh:
				findNextTime();
				jumpToNextStop();
			
			inFocus = true;
		}
	});
	
	//If the user tries to scroll before the auto-scrolling finishes, stop it
	//$('body,html').bind('scroll mousedown DOMMouseScroll mousewheel keyup', function(e){
	$('body').bind('scroll mousedown DOMMouseScroll mousewheel keyup ontouchstart', function(e){
		if ( e.which > 0 || e.type == "mousedown" || e.type == "mousewheel"){
			$("body").stop();
	 	}
	});
	
});

function findNextTime() {
	
	var oldStopTime = $("#nextstoptime").html();
	//alert(oldStopTime);
	
	//Remove any nextstoptime ID's from last time
	$("#nextstoptime").each(function(index, element) {
        $(this).attr("id", "");
    });
	
	var now = new Date();
	//var days = new Array("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
	
	var selector;
	//If it's a weekend, only search through the weekeend table
	if (now.getDay() == 0 || now.getDay() == 6) {
		selector = "table#weekend td";
	}
	//If it's a friday, don't show nonfriday stops
	else if (now.getDay() == 5) {
		selector = "table#weekday td:not(.nofridays)";
	}
	else { selector = "td"; }
	
	$(selector).each(function(index, element) {
        
		var elemValue;
		
		//If there is actually a time in this
		if (/^[0-9]{1}$/.test($(element).html().substr(0, 1))) {
			elemValue = $(element).html().replace(/<span.*span>/g, "");
			
			//If there is no AM/PM declared, default it to PM
			if (elemValue.indexOf("AM") < 0 && elemValue.indexOf("PM") < 0) {
				elemValue = elemValue + " PM";
			}
		}
		
		if ($(element).html().toLowerCase() == "midnight") {
			elemValue = "11:59 PM";
		}
		
		if ($(element).html().toLowerCase() == "noon") {
			elemValue = "11:59 AM";
		}
		
		if (elemValue != null) {
			
			//var now = new Date();
			var elemDate = new Date(Date.parse((now.getMonth() + 1) + "/" + now.getDate() + "/" + now.getFullYear() + " " + elemValue));
			
			//If now is in pm AND elemDate is in AM, it'll be the next day
			/*if (now.getHours() >= 12 && elemDate.getHours() < 12) {
				elemDate = new Date(elemDate.getFullYear(), elemDate.getMonth(), elemDate.getDate() + 1);
			}*/
			
			//console.log("ElemDate day is " + elemDate.getDate());
			
			//console.log(elemDate.getTime() + " - " + now.getTime() + " = " + elemDate.getTime());
			
			//Find the next available time
			if (now.getTime() <= elemDate.getTime()) {
				$(element).attr("id", "nextstoptime");
				return false;
			}
		}
	
   	});
	
	if (oldStopTime == $("#nextstoptime").html()) {
		//alert("same - not adjusting");
		jumpToNewTime = false;
	}
	else { 
		//alert("different - adjusting");
		jumpToNewTime = true;
	}
}

function jumpToNextStop() {
	
	//Only if it's changed, update
	if (jumpToNewTime) {
		$("body").animate({ scrollTop: $("#nextstoptime").offset().top - 30 }, "slow");
	}
}