$(function () {

    //给键盘回车添加事件，按回车触发搜索按钮
    $(window).keydown(function (e) {
        if(e.keyCode == 13){
            let keyword = $.trim($("#keyword").val());

            if(keyword && keyword != ''){
                window.location.href = 'goods/search?keyword=' + keyword;
            }
        }
    });

    $(".all-sort-list > .item").hover(function () {

    })
})

/**
 * 首页搜索按钮增加事件
 * 1.获取输入框用户填写的内容
 * 2.跳转到搜索结果页面
 */
function search() {
    //获取用户输入的关键字
    let keyword = $.trim($("#keyword").val());

    //判断关键字是否合法，合法则向后端发起请求
    if(keyword && keyword != ''){
        window.location.href = 'goods/search?keyword=' + keyword;
    }
}


let newbeeSwiper = new Swiper('.swiper-container', {
    //设置自动播放
    autoplay: {
        delay: 2000,
        disableOnInteraction: false
    },
    //设置无限循环播放
    loop: true,
    //设置圆点指示器
    pagination: {
        el: '.swiper-pagination',
    },
    //设置上下页按钮
    navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
    }
})

/*const swiper = new Swiper('.swiper', {
    //设置自动播放
    autoplay: {
        delay: 2000,
        disableOnInteraction: false
    },

    // Optional parameters
    direction: 'vertical',
    loop: true,

    // If we need pagination
    pagination: {
        el: '.swiper-pagination',
    },

    // Navigation arrows
    navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
    },

    // And if we need scrollbar
    scrollbar: {
        el: '.swiper-scrollbar',
    },
});*/
