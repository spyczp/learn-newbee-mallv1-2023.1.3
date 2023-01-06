$(function () {
    /*
    * 修改用户信息
    * 1.给修改按钮添加单击事件
    *   1.获取用户信息：loginUserName, nickName, loginUserId(从session域中获得)
    *       需要判断用户输入的数据是否为空,是否合法。
    *   2.向后端发起异步请求，把用户信息提交到后端。
    *   3.后端修改数据库中的用户数据。
    * */
    $("#updateUserNameButton").click(function () {
        //$(this).attr("disabled", "true");
        let loginUserName = $.trim($("#loginUserName").val());
        let nickName = $.trim($("#nickName").val());
        if(validUserNameForUpdate(loginUserName, nickName)){
            let params = $("#userNameForm").serialize();
            $.ajax({
                url: "/admin/profile/nameUpdate",
                data: params,
                type: "post",
                dataType: "json",
                success: function (result) {
                    //$(this).attr("disabled",false);
                    console.log(result)
                    if(result.code == 0){
                        alert("修改用户信息成功")
                    }else{
                        alert("修改用户信息失败")
                    }
                }
            })
        }else {
            //$("#updateUserNameButton").attr("disabled",false);
        }
    })

    /**
     * 修改密码，判断和上面类似
     */
    $("#updatePasswordButton").click(function () {
        // $("#updatePasswordButton").attr("disabled",true);
        let originalPassword = $.trim($("#originalPassword").val());
        let newPassword = $.trim($("#newPassword").val());
        if(validPasswordForUpdate(originalPassword, newPassword)){
            let params = $("#userPasswordForm").serialize();
            $.ajax({
                url: "/admin/profile/passwordUpdate",
                data: params,
                type: "post",
                dataType: "json",
                success: function (result) {
                    // $("#updatePasswordButton").attr("disabled",false);
                    console.log(result)
                    if(result.code == 0){
                        alert("修改密码成功")
                        window.location.href = "/admin/login";
                    }else{
                        alert("修改密码失败")
                    }
                }
            })
        }else{
            //$("#updatePasswordButton").attr("disabled",false);
        }
    })
})

/**
 * 名称验证
 */
function validUserNameForUpdate(loginUserName, nickName) {
    if(isNull(loginUserName) || loginUserName.trim().length < 1){
        $("#updateUserName-info").css("display", "block");
        $("#updateUserName-info").html("请输入登陆名称！");
        return false;
    }
    if(isNull(nickName) || nickName.trim().length < 1){
        $("#updateUserName-info").css("display", "block");
        $("#updateUserName-info").html("请输入昵称！");
        return false;
    }
    if(!validUserName(loginUserName)){
        $('#updateUserName-info').css("display", "block");
        $('#updateUserName-info').html("请输入符合规范的登录名！");
        return false;
    }
    if(!validCN_ENString2_18(nickName)){
        $('#updateUserName-info').css("display", "block");
        $('#updateUserName-info').html("请输入符合规范的昵称！");
        return false;
    }
    return true;
}

/**
 * 密码验证
 */
function validPasswordForUpdate(originalPassword, newPassword) {
    if(isNull(originalPassword) || originalPassword.trim().length < 1){
        $('#updatePassword-info').css("display", "block");
        $('#updatePassword-info').html("请输入原始密码！");
        return false;
    }
    if(isNull(newPassword) || newPassword.trim().length < 1){
        $('#updatePassword-info').css("display", "block");
        $('#updatePassword-info').html("请输入新密码！");
        return false;
    }
    if(!validPassword(newPassword)){
        $('#updatePassword-info').css("display", "block");
        $('#updatePassword-info').html("请输入符合规范的密码！");
        return false;
    }
    return true;
}
