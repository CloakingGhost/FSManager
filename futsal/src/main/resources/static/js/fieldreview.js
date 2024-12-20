const memberNickName = document.getElementById("memberNickName").value; // 로그인회원의 닉네임
const reviewbutton = document.getElementById("reviewbutton") // 리뷰작성버튼
const writerFieldName = document.getElementById('fName').value; // 구장명
const area = document.getElementById('reviewarea'); // 리뷰내용foreach 도는 시작부분
const deleteReview = document.getElementsByName("deleteReview") //삭제버튼
const modifyReview = document.getElementsByName("modifyReview") //수정버튼

$('#fieldreview').on('click', function () {
    $('.reviewmodal').fadeIn()
})
$('#backtofield').on('click', function () {
    $('.reviewmodal').fadeOut()
})
// Modal 영역 밖을 클릭하면 Modal을 닫습니다.
window.onclick = function(event) {
    if (event.target.className == "reviewmodal") {
        event.target.style.display = "none";
    }
};
reviewbutton.addEventListener('click', review)

async function review(e) {
    let writerReview = document.getElementById('writerReview'); // 작성하는 후기(textarea)
    let reviewRegDate = deleteReview.value
    let reviewContent = deleteReview.className
    console.log("구장명 : " + writerFieldName);
    console.log("작성자 : " + memberNickName);
    console.log("리뷰내용 : " + writerReview.value);
    let insertReviewUrl;

    if (contextPath != null){
        insertReviewUrl = contextPath + "/field/insertReview?field="+writerFieldName+"&name="+memberNickName+"&review="+writerReview.value
    } else {
        insertReviewUrl = "/field/insertReview?field="+writerFieldName+"&name="+memberNickName+"&review="+writerReview.value
    }
    let option = {
        method: "post"
    }

    try{
        let res = await fetch(insertReviewUrl, option);
        let result = await res.json();
        console.log("결과 : " + result.regDate); // V
        let ul = "<ul class=\"community-post-list\">" +
            "<li>" +
            "<div class='community-post'>" +
            "<div class='author-avator set-bg' data-setbg='img/authors/1.jpg'>" +
            "</div>" +
            "<div class='post-content'>" +
            "<div style='width: 95%; margin-top: 10px;'>" +
            "<h5 style='display: inline'>" + result.nickName +
            "</h5>" +
            "<span class='post-date'>" + result.regDate +
            "</span>" +
            "</div>" +
            "<p>" + result.review + "</p>" +
            "<div>" +
            "<button type='button' class='"+ result.review +"' id='modifyReview' name='modifyReview' value='" + result.regDate + "'>" +
            "<i class='bi bi-pencil-square' style='float: right; font-size: 20px; margin-right: 10px;'></i>" +
            "</button>" +
            "<button type='button' class='"+ result.review +"' id='deleteReview' name='deleteReview' value='" + result.regDate + "'>" +
            "<i class='bi bi-trash' style='float: right; font-size: 20px; margin-right: 5px;'></i>" +
            "</button>" +
            "</div>" +
            "</div>" +
            "</div>" +
            "</li>" +
            "</ul>"
        area.innerHTML += ul;
        document.getElementById("writerReview").value =""
        $("button[name='deleteReview']").on('click',remove)
        $("button[name='modifyReview']").on('click',modify)
    }catch(err){
        alert("리뷰 작성 후 err : " + err);
    }

}


