// Animation
$('.nav-menu li').each((index, item) => {
    const subMenu = $(item).find('.sub-menu')[0];
    if (subMenu) {
        $(item).on('mouseenter', () => gsap.to(subMenu, {height: "auto", opacity: 1, duration: 0.3}));
        $(item).on('mouseleave', () => gsap.to(subMenu, {height: 0, opacity: 0, duration: 0.3}));
    }
});


const nav = $('.mobile-nav-wrap');
const logo = $('.logo');
let menuBtnColor = '#fff';
let logoColor = null;
$('.menu-btn').on('click', () => {
    const isNavOpen = nav.hasClass('open');
    const newLogoColor = 'images/logo.png';

    if (isNavOpen) {
        gsap.to(nav, {left: '100%', duration: 1, ease: "expo.out"});
        document.body.style.overflowY = 'auto';
        $('.menu-btn').css('color', menuBtnColor);
        $('.menu-btn').text('☰');
        if (logoColor !== logo.attr('src')) {
            gsap.fromTo(logo, {opacity: 1}, {opacity: 0, duration: 0.3, onComplete: () => {
                $('.logo-box a').css('position', 'absolute');
                if (logoColor) logo.attr('src', logoColor);
                gsap.fromTo(logo, {opacity: 0}, {opacity: 1, duration: 0.3});
            }});
        }
    } else {
        gsap.to(nav, {left: '0', duration: 1, ease: "expo.out"});
        document.body.style.overflowY = 'hidden';
        menuBtnColor = $('.menu-btn').css('color');
        $('.menu-btn').css('color', '#333');
        $('.menu-btn').text('×');
        logoColor = logo.attr('src');
        if (logoColor !== newLogoColor) {
            gsap.fromTo(logo, {opacity: 1}, {opacity: 0, duration: 0.3, onComplete: () => {
                logo.attr('src', newLogoColor);
                $('.logo-box a').css('position', 'fixed');
                gsap.fromTo(logo, {opacity: 0}, {opacity: 1, duration: 0.3});
            }});
        }
    }

    nav.toggleClass('open');
});
