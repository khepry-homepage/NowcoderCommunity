$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	let btn = this;
	let url = `${CONTEXT_PATH}/follow/changeFollowStatus`;
	$.ajax({
		url,
		type: "POST",
		dataType: "json",
		data: {
			"followeeId": $(btn).prev().val(),
			"entityType": 0,
		},
		xhrFields: {
			withCredentials: true
		},
		timeout: 5000,
		success: function (res) {
			if (res?.code != 200) {
				alert(res.msg);
				return;
			}
			window.location.reload();
		},
		error: function (err) {
			console.log(err);
			alert("关注失败, 服务器异常！", err);
		}
	})
}