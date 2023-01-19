$(function () {
    $("#jqGrid").jqGrid({
        url: 'users/pageTest',
        datatype: "json",
        colModel: [
            {label: 'id', name: 'id', index: 'id', width: 50, hidden: true, key: true},
            {label: '登录名', name: 'name', index: 'name', sortable: false, width: 80, formatter:formatterTest},
            {label: '密码字段', name: 'password', index: 'password', sortable: false, width: 80}
        ],
        height: 485,
        rowNum: 10,
        rowList: [10, 30, 50],
        styleUI: 'Bootstrap',
        loadtext: '信息读取中...',
        rownumbers: true,
        rownumWidth: 35,
        autowidth: true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader: {
            root: "data.list",
            page: "data.currPage",
            total: "data.totalPage",
            records: "data.totalCount"
        },
        prmNames: {
            page: "currentPage",
            rows: "pageSize",
            order: "order"
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });

    $(window).resize(function () {
        $("#jqGrid").setGridWidth($(".card-body").width());
    });

    /*
    * formatter默认传递过来的参数有3个，cellValue, options, rowObject
    * cellValue: 当前行当前标签的值
    * option：是一个model，里面包含了这行各种各样的信息，可以提取本行的id
         {
            "rowId": "1",
            "colModel": {
                "label": "登录名",
                "name": "name",
                "index": "name",
                "sortable": false,
                "width": 392,
                "colmenu": true,
                "title": true,
                "lso": "",
                "hidden": false,
                "widthOrg": 80,
                "resizable": true,
                "canvas_width": 0
            },
            "gid": "jqGrid",
            "pos": 3,
            "styleUI": "Bootstrap"
        }
    * rowObject：是这行的所有数据，能用来提取这行的任意列数据
    *    {
            "id": 1,
            "name": "admin",
            "password": "e10adc3949ba59abbe56e057f20f883e"
        }
    * */
    function formatterTest(cellValue, options, rowObject) {
        // console.log(cellValue)
        // console.log(options)
        // console.log(rowObject)
        return "<a href='#' name='changeLevelA' userId='"+ rowObject.id +"'>" + cellValue + "</a>";
    }

    //拿到用户点击的某行a标签，获取它的自定义属性userId的值，并打印到控制台
    $("#jqGrid").on("click", "a[name='changeLevelA']", function () {
        console.log($(this).attr("userId"));
    })
});
