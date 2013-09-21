/*$(document).ready(function() {
	
	$("body").prepend('<table id="floatingheader" ></table>'); //Styled in CSS
	
	$(window).scroll(function() {
		updateFloaters();
	});
	
	function updateFloaters() {
		
		var currentFloater;
		
		//Decide which floater is the current floater
		jQuery.each($(".floating"), function() {
			
			if ($(this).offset().top < $(window).scrollTop()) {
				currentFloater = $(this);
			}
			
		});
		
		if (currentFloater != null) {
			$("#floatingheader").html(currentFloater.clone());
			$("#floatingheader").css("height", currentFloater.outerHeight());
			$("#floatingheader").css("font-size", currentFloater.css("font-size"));
			Android.printToConsole("Changing HTML; table#floatingheader's height is: " + $("#floatingheader").height());
		}
		else {
			$("#floatingheader").html("");
			$("#floatingheader").css("height", 0);
		}
	}
		
});*/

//Same as above, but only works for one floater 
/*$(document).ready(function() {
	
	//prepare the browser to show the hidden floater by filling it with data, then removing it
	$("#floater").html($("#tobefloated").clone());
	$("#floater").css("top", "-30px");
	$("#flaoter").css("height", "30px");
	setTimeout(function() {
		$("#floater").html("");
		$("#floater").css("top", "0");
		$("#flaoter").css("height", "0");
	}, 1000);

	
	$(window).scroll(function() { 
		
		//If the trigger point is higher than the top of the window...
		if ($("#tobefloated").offset().top < $(window).scrollTop()) {
			
			//fill the floater table with the contents of the tobefloated table
			$("#floater").html($("#tobefloated").clone());
			
			//strip id from the cloned tr in the floated table
			$("#floater tr").attr("id", "");
			
			//set the floater's hieght to normal
			$("#floater").css("height", "30px");
		}
		
		//Otherwise, remove everything from the floater table and set its hieght to 0
		else {
			
			$("#floater").html("");
			$("#floater").css("height", "0");
		}
	});
});*/