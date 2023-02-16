

//登录按钮触发事件
function login() {
    //对用户提交的参数进行初步验证
    var loginName = $("#loginName").val();
    if (!validPhoneNumber(loginName)) {
        Swal.fire({
            text: "请输入正确的登录名(即手机号)",
            icon: "error",iconColor:"#f05b72",
        });
        return false;
    }
    var password = $("#password").val();
    if (!validPassword(password)) {
        Swal.fire({
            text: "请输入正确的密码格式(6-20位字符和数字组合)",
            icon: "error",iconColor:"#f05b72",
        });
        return false;
    }
    var verifyCode = $("#verifyCode").val();
    if (!validLength(verifyCode, 5)) {
        Swal.fire({
            text: "请输入正确的验证码",
            icon: "error",iconColor:"#f05b72",
        });
        return false;
    }
    //验证
    var params = $("#loginForm").serialize();
    var url = '/login';
    $.ajax({
        type: 'POST',//方法类型
        url: url,
        data: params,
        success: function (result) {
            if (result.code == 0) {
                //登录成功，跳转到首页
                window.location.href = '/index';
            } else {
                Swal.fire({
                    text: result.message,
                    icon: "error",iconColor:"#f05b72",
                });
            }
            ;
        },
        error: function () {
            Swal.fire({
                text: '操作失败',
                icon: "error",iconColor:"#f05b72",
            });
        }
    });
}


//注册按钮onclick事件改为register()即可
function register() {
    var loginName = $("#loginName").val();
    if (!validPhoneNumber(loginName)) {
        Swal.fire({
            text: "请输入正确的登录名(即手机号)",
            icon: "error",iconColor:"#f05b72",
        });
        return false;
    }
    var password = $("#password").val();
    if (!validPassword(password)) {
        Swal.fire({
            text: "请输入正确的密码格式(6-20位字符和数字组合)",
            icon: "error",iconColor:"#f05b72",
        });
        return false;
    }
    var verifyCode = $("#verifyCode").val();
    if (!validLength(verifyCode, 5)) {
        Swal.fire({
            text: "请输入正确的验证码",
            icon: "error",iconColor:"#f05b72",
        });
        return false;
    }
    //验证
    var params = $("#registerForm").serialize();
    var url = '/register';
    $.ajax({
        type: 'POST',//方法类型
        url: url,
        data: params,
        success: function (result) {
            if (result.code == 0) {
                Swal.fire({
                    title: "注册成功",
                    text: "是否跳转至登录页?",
                    icon: "success",iconColor:"#1d953f",
                    showCancelButton: true,
                    confirmButtonText: '确认',
                    cancelButtonText: '取消'
                }).then((flag) => {
                        if (flag.value) {
                            window.location.href = '/login';
                        }
                    }
                )
                ;
            } else {
                Swal.fire({
                    text: result.message,
                    icon: "error",iconColor:"#f05b72",
                });
            }
            ;
        },
        error: function () {
            Swal.fire({
                text: '操作失败',
                icon: "error",iconColor:"#f05b72",
            });
        }
    });
}
