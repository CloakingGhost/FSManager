/* =================================
------------------------------------
   Game Warrior Template
   Version: 1.0
 ------------------------------------
 ====================================*/


'use strict';


$(window).on('load', function () {
	/*------------------
        Preloder
    --------------------*/
	$(".loader").fadeOut();
	$("#preloder").delay(400).fadeOut("slow");

});

(function ($) {

	/*------------------
        Navigation
    --------------------*/
	$('.nav-switch').on('click', function (event) {
		$('.main-menu').slideToggle(400);
		event.preventDefault();
	});


	/*------------------
        Background Set
    --------------------*/
	// $('.set-bg').each(function() {
	//     var bg = $(this).data('setbg');
	//     $(this).css('background-image', 'url(' + bg + ')');
	// });


	/*------------------
        Hero Slider
    --------------------*/
	$('.hero-slider').owlCarousel({
		loop: true,
		nav: false,
		dots: true,
		mouseDrag: false,
		animateOut: 'fadeOut',
		animateIn: 'fadeIn',
		items: 1,
		autoplay: true
	});
	var dot = $('.hero-slider .owl-dot');
	dot.each(function () {
		var index = $(this).index() + 1;
		if (index < 10) {
			/*         $(this).html('0').append(index);
                        $(this).append('<span>.</span>');*/
			$(this).append(index).css({"font-size": "15px", "padding-top": "5px", "font-weight": "550"});
		} else {
			$(this).html(index);
			$(this).append('<span>.</span>');
		}
	});


	/*------------------
        News Ticker
    --------------------*/
	$('.news-ticker').marquee({
		duration: 10000,
		//gap in pixels between the tickers
		//gap: 200,
		delayBeforeStart: 0,
		direction: 'left',
		duplicated: true
	});

})(jQuery);