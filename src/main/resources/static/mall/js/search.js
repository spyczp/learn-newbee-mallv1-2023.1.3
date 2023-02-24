
$(function () {

    //alert($("#myKeyword").val())

    //showGoodsInfoList(1, 5)
    showResult(1, 5);

    //给分页的a标签添加点击事件
    /**
     * 1.拿到页码
     *      点击上一页和下一页则返回 当前页码-1 或 当前页码+1
     *      这里需要判断是否还有上一页和下一页
     *      没有上一页，则把上一页里的a标签替换为span标签，下一页也一样
     * 2.修改当前页码属性为新的页码<li class="page-item active" aria-current="page"><a></a><li>
     * 3.调用showResult函数
     */
    $("#paginationUl").on("click", "a", function(){
        //alert($(this).text());
        let pageNum = $(this).text();
        if(pageNum === "上一页"){

        }
    });
})

/*
*
* 需要展示查询出来的商品列表
* 和分页组件
* 1.需要条件参数：当前页码、每页显示条数固定、排序值、顺序倒序、关键字、分类id
* 2.到后端查询数据
* 3.展示
* */
function showResult(pageNum, pageSize) {
    //判断分页参数是否合法
    if($.trim(pageNum) === "" || $.trim(pageSize) === ""){
        alert("分页数据为空")
        return;
    }
    //拿到排序参数
    let column = $(".list div[class='active']").attr("orderBy");
    let order = $(".list div[class='active']").attr("order");

    //拿到关键字，如果有的话
    let goodsName = $.trim($("#myKeyword").val());
    //拿到分类id，如果有的话
    let categoryId = $.trim($("#myCategoryId").val());

    if(goodsName === "" && categoryId === ""){
        alert("关键字和分类信息均为空");
        return;
    }

    let url = "/goods/showGoodsInfoList";
    let data = {}

    if(goodsName !== ""){
        data = {
            "pageNum": pageNum,
            "pageSize": pageSize,
            "column": column,
            "order": order,
            "goodsName": goodsName,
            "categoryId": null,
        }
    }

    if(categoryId !== ""){
        data = {
            "pageNum": pageNum,
            "pageSize": pageSize,
            "column": column,
            "order": order,
            "keyword": null,
            "categoryId": categoryId,
        }
    }

    //发起请求，获取分页数据
    $.ajax({
        url: url,
        data: data,
        type: "get",
        dataType: "json",
        success: function (result) {
            if(result.code == "0"){
                //请求成功
                //构造商品表
                let html = "";
                //表数据为空时，用空图片填充页面
                if(result.data.list.length == 0){
                    html += '<img style="margin-top: 16px;padding: 16px 20px;" src="/mall/image/null-content.png">';
                    return;
                }
                $.each(result.data.list, function (i, o) {
                    html += '<div className="item_card_frame">';
                    html += '<div className="item_card"><a href="/goods/detail/' + o.goodsId + '" target="_blank"><img src="' + o.goodsCoverImg + '" alt="'+ o.goodsName + '"></a></div>';
                    html += '<div className="item_brand"><a href="/goods/detail/' + o.goodsId + '" target="_blank">'+ o.goodsName + '</a></div>';
                    html += '<div className="item_sub_intro">' + o.goodsIntro + '</div>';
                    html += '<div className="item_price">'+ o.sellingPrice + '.00元</div>';
                    html += '</div>';
                });
                //拼接后的字符串作为 html标签 填充到页面中
                $(".main").html(html);

                //总页数大于0时，再展示分页组件
                if(result.data.totalPage > 0){
                    //构建分页组件
                    let paginationHtml = "";
                    paginationHtml += '<li className="page-item disabled">';
                    paginationHtml += '<a class="page-link">上一页</a>';
                    paginationHtml += '</li>';
                    paginationHtml += '<li className="page-item active" aria-current="page">';
                    paginationHtml += '<a class="page-link" href="#">1</a>';
                    paginationHtml += '</li>';

                    let totalPage = result.data.totalPage;

                    /**
                     * 总页数大于7，分页组件展示： 上一页 【1】 2 3 4 5 6 7 ... 下一页
                     * 总页数小于7，例如5，分页组件展示： 上一页 【1】 2 3 4 5 下一页
                     */
                    if(totalPage > 7){
                        for(let i = 2; i <= 7; i++){
                            paginationHtml += '<li className="page-item"><a className="page-link" href="#">'+ i +'</a></li>';
                        }
                        paginationHtml += '<li className="page-item">...</li>';
                    }else{
                        for(let i = 2; i <= totalPage; i++){
                            paginationHtml += '<li className="page-item"><a className="page-link" href="#">'+ i +'</a></li>';
                        }
                    }
                    paginationHtml += '<li className="page-item">';
                    paginationHtml += '<a className="page-link" href="#">下一页</a>';
                    paginationHtml += '</li>';
                    $("#paginationUl").html(paginationHtml);
                }
            }
        }
    })
}

/*
* <nav aria-label="Page navigation example">
  <ul class="pagination justify-content-end">
    <li class="page-item disabled">
      <a class="page-link">Previous</a>
    </li>
    <li class="page-item"><a class="page-link" href="#">1</a></li>
    <li class="page-item"><a class="page-link" href="#">2</a></li>
    <li class="page-item"><a class="page-link" href="#">3</a></li>
    <li class="page-item">
      <a class="page-link" href="#">Next</a>
    </li>
  </ul>
</nav>
* */

//展示商品信息列表
//column是根据哪个参数排序
//order是顺序还是倒序，按价格排序时用到
function showGoodsInfoList(pageNum, pageSize) {
    //判断分页参数是否合法
    if($.trim(pageNum) === "" || $.trim(pageSize) === ""){
        alert("分页数据为空")
        return;
    }
    //拿到排序参数
    let column = $(".list div[class='active']").attr("orderBy");
    let order = $(".list div[class='active']").attr("order");

    //拿到关键字，如果有的话
    let goodsName = $.trim($("#myKeyword").val());
    //拿到分类id，如果有的话
    let categoryId = $.trim($("#myCategoryId").val());

    if(goodsName === "" && categoryId === ""){
        alert("关键字和分类信息均为空");
        return;
    }

    let url = "/goods/showGoodsInfoList";
    let data = {}

    if(goodsName !== ""){
        data = {
            "pageNum": pageNum,
            "pageSize": pageSize,
            "column": column,
            "order": order,
            "goodsName": goodsName,
            "categoryId": null,
        }
    }

    if(categoryId !== ""){
        data = {
            "pageNum": pageNum,
            "pageSize": pageSize,
            "column": column,
            "order": order,
            "keyword": null,
            "categoryId": categoryId,
        }
    }

    //发起请求，获取分页数据
    $.ajax({
        url: url,
        data: data,
        type: "get",
        dataType: "json",
        success: function (result) {
            if(result.code == "0"){
                //请求成功
                //构造商品表
                let html = "";
                //表数据为空时，用空图片填充页面
                if(result.data.list.length == 0){
                    html += '<img style="margin-top: 16px;padding: 16px 20px;" src="/mall/image/null-content.png">';
                    return;
                }
                $.each(result.data.list, function (i, o) {
                    html += '<div className="item_card_frame">';
                    html += '<div className="item_card"><a href="/goods/detail/' + o.goodsId + '" target="_blank"><img src="' + o.goodsCoverImg + '" alt="'+ o.goodsName + '"></a></div>';
                    html += '<div className="item_brand"><a href="/goods/detail/' + o.goodsId + '" target="_blank">'+ o.goodsName + '</a></div>';
                    html += '<div className="item_sub_intro">' + o.goodsIntro + '</div>';
                    html += '<div className="item_price">'+ o.sellingPrice + '.00元</div>';
                    html += '</div>';
                });
                //拼接后的字符串作为 html标签 填充到页面中
                $(".main").html(html);

                //初始化分页插件
                $("#demo_pag1").bs_pagination({
                    currentPage: pageNum, // 页码，我们提供
                    rowsPerPage: pageSize, // 每页显示的记录条数，我们提供
                    maxRowsPerPage: 20, // 每页最多显示的记录条数
                    totalPages: result.data.totalPage, // 总页数
                    totalRows: result.data.totalCount, // 总记录条数，我们提供

                    visiblePageLinks: 3, // 显示几个卡片

                    showGoToPage: true,
                    showRowsPerPage: true,
                    showRowsInfo: true,
                    showRowsDefaultInfo: true,

                    onChangePage : function(event, data){
                        showGoodsInfoList(data.currentPage , data.rowsPerPage);
                    }
                });
            }
        }
    })
}


