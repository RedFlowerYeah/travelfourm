$(function(){
    $("#feedbackBtn").click(sendFeedBack);
});
function sendFeedBack() {

    //发送AJAX请求时，将csrf令牌设置到请求的消息头中
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e,xhr,options) {
        xhr.setRequestHeader(header,token);
    })

    var title = $("#title").val()
    var reason = $("#reason").val()

    $.post(
        CONTEXT_PATH + "/discuss/feedbackDiscussPost",
        {"title": title, "reason": reason},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                alert("提交申诉成功，请耐心等待管理员审核结果！");
                setTimeout(function () {
                    location.href = CONTEXT_PATH + "/index";
                },2000);
            } else {
                alert(data.msg);
            }
        });
}