//便于在保存事件中使用富文本编辑对象
var editor;

$(function () {

    //富文本编辑器 用于商品详情编辑
    const { createEditor, createToolbar } = window.wangEditor

    const editorConfig = {
        // 初始化 MENU_CONF 属性
        MENU_CONF: {},
        placeholder: '从这里开始...',
        onChange(editor) {
            const html = editor.getHtml()
            console.log('editor content', html)
            // 也可以同步到 <textarea>
        }
    }
    editorConfig.MENU_CONF['uploadImage'] = {
        server: '/admin/goods/uploadImage',
        fieldName: 'file',
        // 单个文件的最大体积限制，默认为 2M
        maxFileSize: 4 * 1024 * 1024,
        base64LimitSize: 5 * 1024,
        // 最多可上传几个文件，默认为 100
        maxNumberOfFiles: 5,
        // 超时时间5s
        timeout: 5 * 1000,
        // 选择文件时的类型限制，默认为 ['image/!*']
        allowedFileTypes: ['image/!*'],

        onBeforeUpload(file) {
            console.log('onBeforeUpload', file)

            return file // will upload this file
            // return false // prevent upload
        },
        // 上传进度的回调函数
        onProgress(progress) {
            // progress 是 0-100 的数字
            console.log('progress', progress)
        },
        // 单个文件上传成功之后
        onSuccess(file, res) {
            console.log(`${file.name} 上传成功`, res)
        },
        // 单个文件上传失败
        onFailed(file, res) {
            console.log(`${file.name} 上传失败`, res)
        },
        // 上传错误，或者触发 timeout 超时
        onError(file, err, res) {
            console.log(`${file.name} 上传出错`, err, res)
        },

        // 隐藏插入网络图片的功能
        // 设置编辑区域高度为 640px
    }

    //这个editor是function外定义的变量
    editor = createEditor({
        selector: '#editor-container',
        html: $(".editor-text").val(),
        config: editorConfig,
        mode: 'default', // or 'simple'
    })


    const toolbarConfig = {}

    const toolbar = createToolbar({
        editor,
        selector: '#toolbar-container',
        config: toolbarConfig,
        mode: 'default', // or 'simple'
    })

    //图片上传插件初始化 用于商品预览图上传
    new AjaxUpload('#uploadGoodsCoverImg', {
        action: '/admin/upload/file',
        name: 'file',
        autoSubmit: true,
        responseType: "json",
        onSubmit: function (file, extension) {
            if (!(extension && /^(jpg|jpeg|png|gif)$/.test(extension.toLowerCase()))) {
                Swal.fire({
                    text: "只支持jpg、png、gif格式的文件！",
                    icon: "error",iconColor:"#f05b72",
                });
                return false;
            }
        },
        onComplete: function (file, r) {
            if (r != null && r.code == 0) {
                $("#goodsCoverImg").attr("src", r.data);
                $("#goodsCoverImg").attr("style", "width: 128px;height: 128px;display:block;");
                return false;
            } else {
                Swal.fire({
                    text: r.message,
                    icon: "error",iconColor:"#f05b72",
                });
            }
        }
    });
});

$('#saveButton').click(function () {
    var goodsId = $('#goodsId').val();
    var goodsCategoryId = $('#levelThree option:selected').val();
    var goodsName = $.trim($('#goodsName').val());
    var tag = $.trim($('#tag').val());
    var originalPrice = $.trim($('#originalPrice').val());
    var sellingPrice = $.trim($('#sellingPrice').val());
    var goodsIntro = $.trim($('#goodsIntro').val());
    var stockNum = $.trim($('#stockNum').val());
    var goodsSellStatus = $("input[name='goodsSellStatus']:checked").val();
    var goodsDetailContent = editor.getHtml();
    var goodsCoverImg = $('#goodsCoverImg')[0].src;
    if (isNull(goodsCategoryId)) {
        Swal.fire({
            text: "请选择分类",
            icon: "error",iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(goodsName)) {
        Swal.fire({
            text: "请输入商品名称",
            icon: "error",iconColor:"#f05b72",
        });
        return;
    }
    if (!validLength(goodsName, 100)) {
        Swal.fire({
            text: "商品名称过长",
            icon: "error",iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(tag)) {
        Swal.fire({
            text: "请输入商品小标签",
            icon: "error",iconColor:"#f05b72",
        });
        return;
    }
    if (!validLength(tag, 100)) {
        Swal.fire({
            text: "标签过长",
            icon: "error",iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(goodsIntro)) {
        Swal.fire({
            text: "请输入商品简介",
            icon: "error",iconColor:"#f05b72",
        });
        return;
    }
    if (!validLength(goodsIntro, 100)) {
        Swal.fire({
            text: "简介过长",
            icon: "error",iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(originalPrice) || originalPrice < 1) {
        Swal.fire({
            text: "请输入商品价格",
            icon: "error",iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(sellingPrice) || sellingPrice < 1) {
        Swal.fire({
            text: "请输入商品售卖价",
            icon: "error",iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(stockNum) || stockNum < 0) {
        Swal.fire({
            text: "请输入商品库存数",
            icon: "error",iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(goodsSellStatus)) {
        Swal.fire({
            text: "请选择上架状态",
            icon: "error",iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(goodsDetailContent)) {
        Swal.fire({
            text: "请输入商品介绍",
            icon: "error",iconColor:"#f05b72",
        });
        return;
    }
    if (!validLength(goodsDetailContent, 50000)) {
        Swal.fire({
            text: "商品介绍内容过长",
            icon: "error",iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(goodsCoverImg) || goodsCoverImg.indexOf('img-upload') != -1) {
        Swal.fire({
            text: "封面图片不能为空",
            icon: "error",iconColor:"#f05b72",
        });
        return;
    }
    var url = '/admin/goods/save';
    var swlMessage = '保存成功';
    var data = {
        "goodsName": goodsName,
        "goodsIntro": goodsIntro,
        "goodsCategoryId": goodsCategoryId,
        "tag": tag,
        "originalPrice": originalPrice,
        "sellingPrice": sellingPrice,
        "stockNum": stockNum,
        "goodsDetailContent": goodsDetailContent,
        "goodsCoverImg": goodsCoverImg,
        "goodsCarousel": goodsCoverImg,
        "goodsSellStatus": goodsSellStatus
    };
    if (goodsId > 0) {
        url = '/admin/goods/update';
        swlMessage = '修改成功';
        data = {
            "goodsId": goodsId,
            "goodsName": goodsName,
            "goodsIntro": goodsIntro,
            "goodsCategoryId": goodsCategoryId,
            "tag": tag,
            "originalPrice": originalPrice,
            "sellingPrice": sellingPrice,
            "stockNum": stockNum,
            "goodsDetailContent": goodsDetailContent,
            "goodsCoverImg": goodsCoverImg,
            "goodsCarousel": goodsCoverImg,
            "goodsSellStatus": goodsSellStatus
        };
    }
    console.log(data);
    $.ajax({
        type: 'POST',//方法类型
        url: url,
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (result) {
            if (result.code == '0') {
                Swal.fire({
                    text: swlMessage,
                    icon: "success",iconColor:"#1d953f",
                    showCancelButton: false,
                    confirmButtonColor: '#1baeae',
                    confirmButtonText: '返回商品列表',
                    confirmButtonClass: 'btn btn-success',
                    buttonsStyling: false
                }).then(function () {
                    //这个跳转页面还没有写
                    window.location.href = "/admin/goods";
                })
            } else {
                Swal.fire({
                    text: result.message,
                    icon: "error",iconColor:"#f05b72",
                });
            };
        },
        error: function () {
            Swal.fire({
                text: "操作失败",
                icon: "error",iconColor:"#f05b72",
            });
        }
    });
});

$('#cancelButton').click(function () {
    window.location.href = "/admin/goods";
});

//选择 第一层的分类，然后触发 改变 事件，展示第二层分类
$("#levelOne").on("change", function () {
    let parentId = $(this).val()
    if(parentId == null || parentId == ""){
        Swal.fire({
            text: "未选择分类",
            icon: "error",iconColor:"#f05b72",
        })
        //清空第二层分类
        $("#levelTwo").html("");
        //清空第三层分类
        $("#levelThree").html("");
        return;
    }
    //alert("第一层分类id:" + parentId);
    $.ajax({
        url: "/admin/goods/listForSelect",
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
        Swal.fire({
            text: "未选择分类",
            icon: "error",iconColor:"#f05b72",
        })
        //清空第三层分类
        $("#levelThree").html("");
        return;
    }
    //alert("第二层分类id:" + parentId);
    $.ajax({
        url: "/admin/goods/listForSelect",
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
