function handleReplyExpand(id) {
    let list = document.getElementById(id);
    if (list.children?.length > 1) return;
    let className = list.className;
    if (className.indexOf("hidden") < 0) {
        list.className = className.replace("show", "hidden");
    } else {
        list.className = className.replace("hidden", "show");
    }
}

function replyPost(domId, entityType, entityId, targetId = 0) {
    let url = `${CONTEXT_PATH}/discuss/postComment`;
    let input = document.getElementById(domId);
    $.ajax({
        url,
        type: 'POST',
        dataType: 'json',
        data: {
            entityType,
            entityId,
            targetId,
            content: input.value,
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
            alert("发布失败, 服务器异常！", err);
        }
    })
}
function changeLikeStatus(dom, entityType, entityId) {
    let url = `${CONTEXT_PATH}/like/changeStatus`;
    $.ajax({
        url,
        type: 'POST',
        dataType: 'json',
        data: {
            entityType,
            entityId
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
            $(dom).children("b").text(res.isLike ? '已赞' : '赞');
            $(dom).children("i").text(res.likeCount);
        },
        error: function (err) {
            console.log(err);
        }
    })
}