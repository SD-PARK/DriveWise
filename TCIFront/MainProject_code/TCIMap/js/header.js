// Animation
$('.nav-menu li').each((index, item) => {
    const subMenu = $(item).find('.sub-menu')[0];
    if (subMenu) {
        $(item).on('mouseenter', () => gsap.to(subMenu, {height: "auto", opacity: 1, duration: 0.3}));
        $(item).on('mouseleave', () => gsap.to(subMenu, {height: 0, opacity: 0, duration: 0.3}));
    }
});


const nav = $('.mobile-nav-wrap');
let menuBtnColor = '#fff';
$('.menu-btn').on('click', () => {
    const isNavOpen = nav.hasClass('open');

    if (isNavOpen) {
        gsap.to(nav, {left: '100%', duration: 1, ease: "expo.out" });
        document.body.style.overflowY = 'auto';
        $('.menu-btn').css('color', menuBtnColor);
        $('.menu-btn').text('☰');
        $('.logo-box a').css('position', 'absolute');
    } else {
        gsap.to(nav, {left: '0', duration: 1, ease: "expo.out" });
        document.body.style.overflowY = 'hidden';
        menuBtnColor = $('.menu-btn').css('color');
        $('.menu-btn').css('color', '#333');
        $('.menu-btn').text('×');
        setTimeout(() => $('.logo-box a').css('position', 'fixed'), 300);
    }

    nav.toggleClass('open');
});