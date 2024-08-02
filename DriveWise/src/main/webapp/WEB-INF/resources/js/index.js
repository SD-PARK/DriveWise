// Animation
AOS.init();
gsap.registerPlugin(ScrollTrigger);

let introAnimation;

function startAnimation() {
    const vw = window.innerWidth;
    const vh = window.innerHeight;

    introAnimation = gsap.timeline({ paused: true });

    introAnimation.fromTo('.img-box', { width: vw, height: vh }, {
        duration: 0.5,
        width: '1100px',
        height: '300px',
        ease: 'power2.inOut',
    }).fromTo('.menu-btn', {color:'#fff'}, {
        duration: 0.3,
        color: '#333',
    }, '-=0.3')
    .fromTo('.img-box .text-wrap', {opacity:1, display:'block'}, {
        duration: 0.3,
        opacity: 0,
        display: 'none',
    }, '-=0.3').fromTo('#intro > .text-wrap', {opacity:0, display:'none'}, {
        duration: 0.3,
        opacity: 1,
        display: 'block',
    });

    ScrollTrigger.create({
        trigger: '#intro',
        start: 'top top',
        end: 'bottom top',
        onEnter: () => introAnimation.play(),
        onLeaveBack: () => introAnimation.reverse(),
    });
}

startAnimation();

window.addEventListener('resize', () => {
    startAnimation();
    if (nav.hasClass('open'))
        $('.menu-btn').css('color', '#333');
});

// Swiper
$(document).ready(function() {
    var swiper = new Swiper('.swiper-container', {
        loop: true,
        autoplay: {
            delay: 2000,
            disableOnInteraction: false,
        },
        effect: 'slide',
        spaceBetween: 50,
        on: {
            slideChange: function() {
                updateActiveLink(this.realIndex);
            }
        }
    });

    function updateActiveLink(index) {
        $('.link-list-wrap p').removeClass('active');
        $('.link-list-wrap p').eq(index).addClass('active');
    }

    updateActiveLink(swiper.realIndex);

    $('.link-list-wrap > *').on('click', function() {
        var index = $(this).index();
        swiper.slideToLoop(index, 500);
        updateActiveLink(index);
    });
});

function scrollUp() {
    window.scrollTo({top: 0, behavior: 'smooth'});
}