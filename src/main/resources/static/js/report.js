/**
 * report页面下拉框事件
 */
var CONTEXT_PATH="/travelfourm";
$(function () {
    $("#selectCityId").change(function () {
        var cityId = $("#selectCityId").val()
        var url = CONTEXT_PATH + '/report/cityId/' + cityId
        window.location.href = url
    })
})