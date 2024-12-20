$("button[name=deleteReview]").on('click',remove)
async function remove(e){
    let reviewRegDate = e.currentTarget.value
    let reviewContent = e.currentTarget.className
    let deletereviewconfirmchk = confirm("삭제 하시겠습니까 ? ")
    if(deletereviewconfirmchk == true){
        console.log("삭제해당리뷰작성일자 : " + reviewRegDate)
        console.log("삭제해당리뷰내용 : " + reviewContent)
        let area = document.getElementById('reviewarea'); // 리뷰내용foreach 도는 시작부분
        let deleteReviewUrl;

        while(area.hasChildNodes()){
            area.removeChild(area.firstChild)
        }
        if (contextPath != null){
            deleteReviewUrl = contextPath + "/field/deleteReviewAjax?field="+writerFieldName+"&nickName="+memberNickName+"&regDate="+reviewRegDate
        } else {
            deleteReviewUrl = "/field/deleteReviewAjax?field="+writerFieldName+"&nickName="+memberNickName+"&regDate="+reviewRegDate
        }
        let option = {
            method: "post"
        }
        try{
            let res = await fetch(deleteReviewUrl, option);
            let result = await res.json();
            for(let i=0; i<result.length;i++){
                if(result[i].nickName == memberNickName){
                    let afterdeletereviewul1 = "<ul class=\"community-post-list\">" +
                        "<li>" +
                        "<div class='community-post'>" +
                        "<div class='author-avator set-bg' data-setbg='img/authors/1.jpg'>" +
                        "</div>" +
                        "<div class='post-content'>" +
                        "<div style='width: 95%; margin-top: 10px;'>" +
                        "<h5 style='display: inline'>" + result[i].nickName +
                        "</h5>" +
                        "<span class='post-date'>" + result[i].regDate +
                        "</span>" +
                        "</div>" +
                        "<p>" + result[i].review + "</p>" +
                        "<div>" +
                        "<button type='button' class='"+result[i].review+"' id='modifyReview' name='modifyReview' value='" + result[i].regDate + "'>" +
                        "<i class='bi bi-pencil-square' style='float: right; font-size: 20px; margin-right: 10px;'></i>" +
                        "</button>" +
                        "<button type='button' class='"+result[i].review+"' id='deleteReview' name='deleteReview' value='" + result[i].regDate + "'>" +
                        "<i class='bi bi-trash' style='float: right; font-size: 20px; margin-right: 5px;'></i>" +
                        "</button>" +
                        "</div>" +
                        "</div>" +
                        "</div>" +
                        "</li>" +
                        "</ul>"
                    area.innerHTML += afterdeletereviewul1;
                }else{
                    let afterdeletereviewul2 = "<ul class=\"community-post-list\">" +
                        "<li>" +
                        "<div class='community-post'>" +
                        "<div class='author-avator set-bg' data-setbg='img/authors/1.jpg'>" +
                        "</div>" +
                        "<div class='post-content'>" +
                        "<div style='width: 95%; margin-top: 10px;'>" +
                        "<h5 style='display: inline'>" + result[i].nickName +
                        "</h5>" +
                        "<span class='post-date'>" + result[i].regDate +
                        "</span>" +
                        "</div>" +
                        "<p>" + result[i].review + "</p>" +
                        "</div>" +
                        "</div>" +
                        "</li>" +
                        "</ul>"
                    area.innerHTML += afterdeletereviewul2;
                }
                $("button[name='deleteReview']").on('click',remove)
                $("button[name='modifyReview']").on('click',modify)
            }
        }catch(err){
            alert("리뷰 삭제 후 err : " + err);
        }
    }else{
        alert("취소하셨습니다.")
    }
}