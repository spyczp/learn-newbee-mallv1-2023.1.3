$(function () {
    //选择 第一层的分类，然后触发 改变 事件，展示第二层分类
    $("#levelOne").on("change", function () {
        let parentId = $(this).val()
        if(parentId == null || parentId == ""){
            alert("未选择分类");
            return;
        }
        //alert("第一层分类id:" + parentId);
        $.ajax({
            url: "/admin/categories/listForSelect",
            data: {
                parentId: parentId
            },
            type: "get",
            dataType: "json",
            success: function (r) {
                if(r.code == '0'){
                    let html = "<option></option>";
                    for(i = 0; i < r.data.length; i++){
                        html += '<option value="' + r.data[i].categoryId + '">' + r.data[i].categoryName + '</option>';
                    }
                    //展示第二层分类
                    $("#levelTwo").html(html);
                    //清空第三层分类
                    $("#levelThree").html("");
                }
            }
        })
    });

    //选择 第二层的分类，然后触发 改变 事件，展示第三层分类
    $("#levelTwo").on("change", function () {
        let parentId = $(this).val()
        if(parentId == null || parentId == ""){
            alert("未选择分类");
            return;
        }
        //alert("第二层分类id:" + parentId);
        $.ajax({
            url: "/admin/categories/listForSelect",
            data: {
                parentId: parentId
            },
            type: "get",
            dataType: "json",
            success: function (r) {
                if(r.code == '0'){
                    let html = "<option></option>";
                    for(i = 0; i < r.data.length; i++){
                        html += '<option value="' + r.data[i].categoryId + '">' + r.data[i].categoryName + '</option>';
                    }
                    $("#levelThree").html(html);
                }
            }
        })
    });
})
