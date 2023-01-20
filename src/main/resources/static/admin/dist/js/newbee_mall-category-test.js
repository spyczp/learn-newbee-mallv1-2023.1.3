/*
  点击左侧栏“分类管理”，跳转到分类管理页面
  发起异步请求，展示分类列表。默认展示1级分类
  为了进行层级的切换，来展示不同层级、不同分类名称的下一层分类列表，
  把分类名称改为用a标签显示，目的是给a标签添加点击事件，从而展示下级分类列表
*   1.把分类名称的标签改为a标签
*   2.点击a标签能触发点击事件
*   3.点击事件：（隐藏标签用来保存点击后的状态）
*       1.拿到点击a标签所在行的categoryId、categoryLevel、parentId
*       2.把id为parentId标签的值保存到id为oldParentId的标签中
        3.把categoryId保存到id为parentId的隐藏标签中
        4.
        5.把parentId保存到id为backParentId的隐藏标签中

* */

$(function () {

    var categoryLevel = $("#categoryLevel").val();
    var parentId = $("#parentId").val();

    //展示分类列表，里面包含了分页插件
    $("#jqGrid").jqGrid({
        url: '/admin/categories/list?categoryLevel=' + categoryLevel + '&parentId=' + parentId,
        datatype: "json",
        colModel: [
            {label: 'id', name: 'categoryId', index: 'categoryId', width: 50, key: true, hidden: true},
            {label: '分类名称', name: 'categoryName', index: 'categoryName', width: 240, formatter:changeLabelToA},
            {label: '排序值', name: 'categoryRank', index: 'categoryRank', width: 120},
            {label: '添加时间', name: 'createTime', index: 'createTime', width: 120},
            {label: '父id', name: 'parentId', index: 'parentId', hidden: true},
            {label: '层级', name: 'categoryLevel', index: 'categoryLevel', hidden: true},
        ],
        height: 560,
        rowNum: 10,
        rowList: [10, 20, 50],
        styleUI: 'Bootstrap',
        loadtext: '信息读取中...',
        rownumbers: false,
        rownumWidth: 20,
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
            page: "pageNum",
            rows: "pageSize",
            order: "order",
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });

    $(window).resize(function () {
        $("#jqGrid").setGridWidth($(".card-body").width());
    });

    //给分类名称这个a标签添加单击事件，展示下一层分类列表。
    $("#jqGrid").on("click", "a[name='changeLevelA']", function () {
        //alert($(this).attr("categoryLevel") + "===" + $(this).attr("parentId") + "===" + $(this).attr("categoryId"))
        if(parseInt($(this).attr("categoryLevel")) > 2){
            Swal.fire({
                text: "没有下一级分类了",
                icon: "warning",iconColor:"#dea32c",
            });
            return;
        }

        let categoryLevel = parseInt($(this).attr("categoryLevel")) + 1;
        let parentId = $(this).attr("categoryId");

        /**
         * 点击第一层分类
         * #oldParentId保存0
         * #backParentId保存0（第一层的父id）
         * #parentId保存点击的第一层分类的id
         *
         * 点击第二层分类
         * #oldParentId保存0（第一层的父id）
         * #backParentId保存点击的第一层分类的id
         * #parentId保存点击的第二层分类的id
         *
         * 点击第三层分类提示没有下一层分类了
         */
        $("#categoryLevel").val(categoryLevel);
        //alert($("#backParentId").val() + "===" + $("#parentId").val())
        $("#oldParentId").val($("#backParentId").val());
        $("#backParentId").val($("#parentId").val());
        $("#parentId").val(parentId);

        //刷新分类列表
        $("#jqGrid").jqGrid('setGridParam', {
            url: '/admin/categories/list?categoryLevel=' + categoryLevel + '&parentId=' + parentId,
            mtype: "get",
            datatype: "json",
        }).trigger("reloadGrid");
    })
});

/*把分类名称转换成a标签，用于点击后跳转到下一层级
    *
    * */
function changeLabelToA(cellValue, option, rowObject) {
    // console.log(rowObject)
    // console.log(rowObject.categoryId)
    return "<a href='#' name='changeLevelA' categoryLevel='"+ rowObject.categoryLevel +"' parentId='"+ rowObject.parentId +"' categoryId='"+ rowObject.categoryId +"'>" + cellValue + "</a>";
}

/**
 * 展示分类列表，里面包含了分页插件
 */
function showCategoryList() {

}

/**
 * jqGrid重新加载
 */
function reload() {
    var page = $("#jqGrid").jqGrid('getGridParam', 'page');
    $("#jqGrid").jqGrid('setGridParam', {
        page: page
    }).trigger("reloadGrid");
}

function categoryAdd() {
    reset();
    $('.modal-title').html('分类添加');
    $('#categoryModal').modal('show');
}

/**
 * 管理下级分类
 */
function categoryManage() {
    var categoryLevel = parseInt($("#categoryLevel").val());
    var parentId = $("#parentId").val();
    var id = getSelectedRow();
    if (id == undefined || id < 0) {
        return false;
    }
    if (categoryLevel == 1 || categoryLevel == 2) {
        categoryLevel = categoryLevel + 1;
        window.location.href = '/admin/categories?categoryLevel=' + categoryLevel + '&parentId=' + id + '&backParentId=' + parentId;
    } else {
        Swal.fire({
            text: "无下级分类",
            icon: "warning",iconColor:"#dea32c",
        });
    }
}

/**
 * 返回上一层级
 */
function categoryBack() {
    var categoryLevel = parseInt($("#categoryLevel").val());
    var backParentId = $("#backParentId").val();
    if (categoryLevel == 2 || categoryLevel == 3) {
        categoryLevel = categoryLevel - 1;
        window.location.href = '/admin/categories?categoryLevel=' + categoryLevel + '&parentId=' + backParentId + '&backParentId=0';
    } else {
        Swal.fire({
            text: "无上级分类",
            icon: "warning",iconColor:"#dea32c",
        });
    }
}

//绑定modal上的保存按钮
$('#saveButton').click(function () {
    var categoryName = $("#categoryName").val();
    var categoryLevel = $("#categoryLevel").val();
    var parentId = $("#parentId").val();
    var categoryRank = $("#categoryRank").val();
    if (!validCN_ENString2_18(categoryName)) {
        $('#edit-error-msg').css("display", "block");
        $('#edit-error-msg').html("请输入符合规范的分类名称！");
    } else {
        var data = {
            "categoryName": categoryName,
            "categoryLevel": categoryLevel,
            "parentId": parentId,
            "categoryRank": categoryRank
        };
        var url = '/admin/categories/save';
        var id = getSelectedRowWithoutAlert();
        if (id != null) {
            url = '/admin/categories/update';
            data = {
                "categoryId": id,
                "categoryName": categoryName,
                "categoryLevel": categoryLevel,
                "parentId": parentId,
                "categoryRank": categoryRank
            };
        }
        $.ajax({
            type: 'POST',//方法类型
            url: url,
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (result) {
                if (result.resultCode == 200) {
                    $('#categoryModal').modal('hide');
                    Swal.fire({
                        text: "保存成功",
                        icon: "success",iconColor:"#1d953f",
                    });
                    reload();
                } else {
                    $('#categoryModal').modal('hide');
                    Swal.fire({
                        text: result.message,
                        icon: "error",iconColor:"#f05b72",
                    });
                }
                ;
            },
            error: function () {
                Swal.fire({
                    text: "操作失败",
                    icon: "error",iconColor:"#f05b72",
                });
            }
        });
    }
});

function categoryEdit() {
    reset();
    var id = getSelectedRow();
    if (id == null) {
        return;
    }
    var rowData = $("#jqGrid").jqGrid("getRowData", id);
    $('.modal-title').html('分类编辑');
    $('#categoryModal').modal('show');
    $("#categoryId").val(id);
    $("#categoryName").val(rowData.categoryName);
    $("#categoryRank").val(rowData.categoryRank);
}

/**
 * 分类的删除会牵涉到多级分类的修改和商品数据的修改，请谨慎删除分类数据，
 * 如果在商城页面不想显示相关分类可以通过调整rank值来调整显示顺序，
 * 不过代码我也写了一部分，如果想保留删除功能的话可以在此代码的基础上进行修改。
 */
function deleteCagegory() {

    var ids = getSelectedRows();
    if (ids == null) {
        return;
    }
    Swal.fire({
        title: "确认弹框",
        text: "确认要删除数据吗?",
        icon: "warning",iconColor:"#dea32c",
        showCancelButton: true,
        confirmButtonText: '确认',
        cancelButtonText: '取消'
    }).then((flag) => {
            if (flag.value) {
                $.ajax({
                    type: "POST",
                    url: "/admin/categories/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function (r) {
                        if (r.resultCode == 200) {
                            Swal.fire({
                                text: "删除成功",
                                icon: "success",iconColor:"#1d953f",
                            });
                            $("#jqGrid").trigger("reloadGrid");
                        } else {
                            Swal.fire({
                                text: r.message,
                                icon: "error",iconColor:"#f05b72",
                            });
                        }
                    }
                });
            }
        }
    )
    ;
}


function reset() {
    $("#categoryName").val('');
    $("#categoryRank").val(0);
    $('#edit-error-msg').css("display", "none");
}
