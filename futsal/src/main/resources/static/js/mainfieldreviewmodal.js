let fadeinmodals = document.getElementsByClassName("fieldreivew") //메인에 리뷰모달여는버튼s
let fadeoutmodals = document.getElementsByName("backtomain") //메인에 리뷰모달닫는버튼s
let reviewmodals = document.getElementsByClassName("reviewmodal") //메인에 리뷰모달컨텐츠s
let memberNickName;
try{
    memberNickName = document.getElementById("memberNickName").value; // 로그인회원의 닉네임
}catch (err){
    console.log("로그인 안해서 나는 오류 : " + err)
    memberNickName = null;
}
const reviewbutton = document.getElementsByName("reviewbutton") // 리뷰작성버튼
const deleteReview = document.getElementsByName("deleteReview") //삭제버튼
const modifyReview = document.getElementsByName("modifyReview") //수정버튼
const reviewTextarea = document.getElementsByName("writerReview") //리뷰작성하는textarea

let funcs = [];

// Modal을 띄우고 닫는 클릭 이벤트를 정의한 함수
function Modal(num) {
    return function() {
        // 해당 클래스의 내용을 클릭하면 Modal을 띄웁니다.
        fadeinmodals[num].onclick =  function() {
            reviewmodals[num].style.display = "block";

            
            $("button[id=reviewbutton]").off("click").on('click',review)
            $("button[name=deleteReview]").off("click").on('click',remove)
            $("button[name=modifyReview]").off("click").on('click',modify)

            async function review(e) {
                let writerReview = document.getElementsByName('writerReview')[num]
                let writerFieldName = document.getElementsByName('fNames')[num]
                let area = document.getElementsByName('reviewarea')[num] // 리뷰내용foreach 도는 시작부분
                console.log("구장명 : " + writerFieldName.value);
                console.log("작성자 : " + memberNickName);
                console.log("리뷰내용 : " + writerReview.value);
                let form = new FormData();
                form.append("field", writerFieldName.value)
                form.append("name", memberNickName)
                form.append("review", writerReview.value)

                let insertReviewUrl;

                if (contextPath != null){
                    // insertReviewUrl = contextPath + "/field/insertReview?field="+writerFieldName.value+"&name="+memberNickName+"&review="+writerReview.value
                    insertReviewUrl = contextPath + "/field/insertReview"
                } else {
                    // insertReviewUrl = "/field/insertReview?field="+writerFieldName.value+"&name="+memberNickName+"&review="+writerReview.value
                    insertReviewUrl = "/field/insertReview"
                }
                let option = {
                    method: "post",
                    body : form
                }

                try{
                    let res = await fetch(insertReviewUrl, option);
                    let result = await res.json();
                    console.log("결과 : " + result.review); // V
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
                        "<p style='white-space: pre;'>" + result.review + "</p>" +
                        "<div>" +
                        "<button type='button' class='"+ result.review +"' id='modifyReview' name='modifyReview' value='" + result.regDate + "' style='cursor: pointer'>" +
                        "<img src='https://i.ibb.co/SPYLSG3/trash.png' style='float: right; font-size: 20px; margin-right: 10px;'/>" +
                        "</button>" +
                        "<button type='button' class='"+ result.review +"' id='deleteReview' name='deleteReview' value='" + result.regDate + "' style='cursor: pointer'>" +
                        "<img src='https://i.ibb.co/5vHn8bd/write.png' style='float: right; font-size: 20px; margin-right: 5px;'/>" +
                        "</button>" +
                        "</div>" +
                        "</div>" +
                        "</div>" +
                        "</li>" +
                        "</ul>"
                    area.innerHTML += ul;
                    writerReview.value =""
                    $("button[name='deleteReview']").on('click',remove)
                    $("button[name='modifyReview']").on('click',modify)
                }catch(err){
                    alert("리뷰 작성 후 err : " + err);
                }
            }

            async function remove(e){
                let reviewRegDate = e.currentTarget.value
                let reviewContent = e.currentTarget.className
                let writerFieldName = document.getElementsByName('fNames')[num]

                let deletereviewconfirmchk = confirm("삭제 하시겠습니까 ? ")
                if(deletereviewconfirmchk == true){
                    console.log("삭제해당리뷰작성일자 : " + reviewRegDate)
                    console.log("삭제해당리뷰내용 : " + reviewContent)
                    console.log("삭제해당구장이름 : " + writerFieldName.value)
                    let area = document.getElementsByName('reviewarea')[num] // 리뷰내용foreach 도는 시작부분
                    let deleteReviewUrl;

                    while(area.hasChildNodes()){
                        area.removeChild(area.firstChild)
                    }
                    if (contextPath != null){
                        deleteReviewUrl = contextPath + "/field/deleteReviewAjax?field="+writerFieldName.value+"&nickName="+memberNickName+"&regDate="+reviewRegDate
                    } else {
                        deleteReviewUrl = "/field/deleteReviewAjax?field="+writerFieldName.value+"&nickName="+memberNickName+"&regDate="+reviewRegDate
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
                                    "<p style='white-space: pre-wrap;'>" + result[i].review + "</p>" +
                                    "<div>" +
                                    "<button type='button' class='"+result[i].review+"' id='modifyReview' name='modifyReview' value='" + result[i].regDate + "' style='cursor: pointer'>" +
                                    "<img src='https://i.ibb.co/SPYLSG3/trash.png' style='float: right; font-size: 20px; margin-right: 10px;'/>" +
                                    "</button>" +
                                    "<button type='button' class='"+result[i].review+"' id='deleteReview' name='deleteReview' value='" + result[i].regDate + "' style='cursor: pointer'>" +
                                    "<img src='https://i.ibb.co/5vHn8bd/write.png' style='float: right; font-size: 20px; margin-right: 5px;'/>" +
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
                                    "<p style='white-space: pre-wrap;'>" + result[i].review + "</p>" +
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

            async function modify(e){
                let reviewRegDate = e.currentTarget.value
                let reviewContent = e.currentTarget.className
                let area = document.getElementsByName('reviewarea')[num] // 리뷰내용foreach 도는 시작부분
                let area2 = document.getElementsByName('backtoreviewmodaldiv')[num] // 리뷰내용foreach 도는 시작부분
                let writerFieldName = document.getElementsByName('fNames')[num]
                console.log("수정해당리뷰작성일자 : " + reviewRegDate)
                console.log("수정해당리뷰내용 : " + reviewContent)
                console.log("수정해당구장이름 : " + writerFieldName.value)

                let modifyReviewUrl;

                if (contextPath != null){
                    modifyReviewUrl = contextPath + "/field/modifyReviewAjax?field="+writerFieldName.value+"&nickName="+memberNickName+"&review="+reviewContent+"&regDate="+reviewRegDate
                } else {
                    modifyReviewUrl = "/field/modifyReviewAjax?field="+writerFieldName.value+"&nickName="+memberNickName+"&review="+reviewContent+"&regDate="+reviewRegDate
                }
                let option = {
                    method: "post"
                }
                try {
                    let res = await fetch(modifyReviewUrl, option);
                    let result = await res.json();
                    // console.log("결과 : " + result[0].review)

                    while(area.hasChildNodes()){
                        area.removeChild(area.firstChild)
                    }
                    while(area2.hasChildNodes()){
                        area2.removeChild(area2.firstChild)
                    }
                    let beforemodifyreviewul =
                        "<ul class=\"community-post-list\">" +
                        "<li>" +
                        "<div class='community-post'>" +
                        "<div class='author-avator set-bg' data-setbg='img/authors/1.jpg'>" +
                        "</div>" +
                        "<div class='post-content'>" +
                        "<div style='width: 95%; margin-top: 10px;'>" +
                        "<h5 style='display: inline'>" + memberNickName +
                        "</h5>" +
                        "<span class='post-date'>" + reviewRegDate +
                        "</span>" +
                        "</div>" +
                        "<textarea id='modifyReviewContent' name='modifyReivewContent' placeholder='"+reviewContent+"' value='"+reviewContent+"' style='width:800px; height:200px; resize: none;'>" + "</textarea>" +
                        "<div>" +
                        "<button type='button' class='"+reviewContent+"' id='realmodify' name='realmodify' value='" + reviewRegDate + "' style='cursor: pointer'>" +
                        "<img src=\"https://i.ibb.co/SPYLSG3/trash.png\" style='float: right; font-size: 20px; margin-right: 10px;'/>" +
                        "</button>" +
                        "<button type='button' class='"+reviewContent+"' id='deleteReview' name='deleteReview' value='" + reviewRegDate + "' style='cursor: pointer'>" +
                        "<img src='https://i.ibb.co/5vHn8bd/write.png' style='float: right; font-size: 20px; margin-right: 5px;'/>" +
                        "</button>" +
                        "</div>" +
                        "</div>" +
                        "</div>" +
                        "</li>" +
                        "</ul>"
                    area.innerHTML = beforemodifyreviewul;

                    for(let j=0; j<result.length;j++){
                        if(result[j].nickName == memberNickName){
                            let ul3 = "<ul class=\"community-post-list\">" +
                                "<li>" +
                                "<div class='community-post'>" +
                                "<div class='author-avator set-bg' data-setbg='img/authors/1.jpg'>" +
                                "</div>" +
                                "<div class='post-content'>" +
                                "<div style='width: 95%; margin-top: 10px;'>" +
                                "<h5 style='display: inline'>" + result[j].nickName +
                                "</h5>" +
                                "<span class='post-date'>" + result[j].regDate +
                                "</span>" +
                                "</div>" +
                                "<p style='white-space: pre-wrap;'>" + result[j].review + "</p>" +
                                "<div>" +
                                "<button type='button' class='"+result[j].review+"' id='modifyReview' name='modifyReview' value='" + result[j].regDate + "' style='cursor: pointer'>" +
                                "<img src='https://i.ibb.co/SPYLSG3/trash.png' style='float: right; font-size: 20px; margin-right: 10px;'/>" +
                                "</button>" +
                                "<button type='button' class='"+result[j].review+"' id='deleteReview' name='deleteReview' value='" + result[j].regDate + "' style='cursor: pointer'>" +
                                "<img src='https://i.ibb.co/5vHn8bd/write.png' style='float: right; font-size: 20px; margin-right: 5px;'/>" +
                                "</button>" +
                                "</div>" +
                                "</div>" +
                                "</div>" +
                                "</li>" +
                                "</ul>"
                            area.innerHTML += ul3;
                        }else{
                            let ul4 = "<ul class=\"community-post-list\">" +
                                "<li>" +
                                "<div class='community-post'>" +
                                "<div class='author-avator set-bg' data-setbg='img/authors/1.jpg'>" +
                                "</div>" +
                                "<div class='post-content'>" +
                                "<div style='width: 95%; margin-top: 10px;'>" +
                                "<h5 style='display: inline'>" + result[j].nickName +
                                "</h5>" +
                                "<span class='post-date'>" + result[j].regDate +
                                "</span>" +
                                "</div>" +
                                "<p style='white-space: pre-wrap;'>" + result[j].review + "</p>" +
                                "</div>" +
                                "</div>" +
                                "</li>" +
                                "</ul>"
                            area.innerHTML += ul4;
                        }
                    }
                    $("button[name='deleteReview']").on('click',remove)
                    $("button[name='modifyReview']").on('click',modify)
                    $("button[id='realmodify']").on('click',realmodify)
                    let textarea = document.createElement('textarea')
                    textarea.className="review_write"
                    textarea.name = "writerReview"
                    textarea.id="writerReview"
                    let btn1 = document.createElement('button')
                    btn1.type="button"
                    btn1.className="btn btn-warning"
                    btn1.name="reviewbutton"
                    btn1.id = "reviewbutton"
                    btn1.innerText="리뷰작성"
                    btn1.addEventListener('click', reviewmodifying)
                    let btn2 = document.createElement('button')
                    btn2.type="button"
                    btn2.className="btn btn-warning"
                    btn2.id = "backtoreviewmodal"
                    btn2.innerText="수정취소"
                    btn2.addEventListener('click', backtomodal)
                    area2.append(textarea)
                    area2.append(btn1)
                    area2.append(btn2)
                }catch (err){
                    console.log("modify fetch 후 err : " + err)
                }
            }

            async function realmodify(e){
                let reviewRegDate = e.currentTarget.value
                let reviewContent = e.currentTarget.className
                let aftermodifyreview = document.getElementById("modifyReviewContent").value
                console.log("수정한 내용 : " + aftermodifyreview)
                let area = document.getElementsByName('reviewarea')[num] // 리뷰내용foreach 도는 시작부분
                let area2 = document.getElementsByName('backtoreviewmodaldiv')[num] // 리뷰내용foreach 도는 시작부분
                let writerFieldName = document.getElementsByName('fNames')[num]

                let realmodifyReviewUrl;
                while(area.hasChildNodes()){
                    area.removeChild(area.firstChild)
                }
                while(area2.hasChildNodes()){
                    area2.removeChild(area2.firstChild)
                }
                if (contextPath != null){
                    realmodifyReviewUrl = contextPath + "/field/realModifyReviewAjax?field="+writerFieldName.value+"&nickName="+memberNickName+"&review="+aftermodifyreview+"&regDate="+reviewRegDate
                } else {
                    realmodifyReviewUrl = "/field/realModifyReviewAjax?field="+writerFieldName.value+"&nickName="+memberNickName+"&review="+aftermodifyreview+"&regDate="+reviewRegDate
                }
                let option = {
                    method: "post"
                }
                try{
                    let realmodifyres = await fetch(realmodifyReviewUrl, option)
                    let realmodifyresult = await realmodifyres.json()
                    console.log("리얼모디파이 fetch 후 결과 : " + realmodifyresult[0].review)

                    for(let j=0; j<realmodifyresult.length;j++){
                        if(realmodifyresult[j].nickName == memberNickName){
                            let ul3 = "<ul class=\"community-post-list\">" +
                                "<li>" +
                                "<div class='community-post'>" +
                                "<div class='author-avator set-bg' data-setbg='img/authors/1.jpg'>" +
                                "</div>" +
                                "<div class='post-content'>" +
                                "<div style='width: 95%; margin-top: 10px;'>" +
                                "<h5 style='display: inline'>" + realmodifyresult[j].nickName +
                                "</h5>" +
                                "<span class='post-date'>" + realmodifyresult[j].regDate +
                                "</span>" +
                                "</div>" +
                                "<p style='white-space: pre-wrap;'>" + realmodifyresult[j].review + "</p>" +
                                "<div>" +
                                "<button type='button' class='"+realmodifyresult[j].review+"' id='modifyReview' name='modifyReview' value='" + realmodifyresult[j].regDate + "' style='cursor: pointer'>" +
                                "<img src='https://i.ibb.co/SPYLSG3/trash.png' style='float: right; font-size: 20px; margin-right: 10px;'></i>" +
                                "</button>" +
                                "<button type='button' class='"+realmodifyresult[j].review+"' id='deleteReview' name='deleteReview' value='" + realmodifyresult[j].regDate + "' style='cursor: pointer'>" +
                                "<img src='https://i.ibb.co/5vHn8bd/write.png' style='float: right; font-size: 20px; margin-right: 5px;'></i>" +
                                "</button>" +
                                "</div>" +
                                "</div>" +
                                "</div>" +
                                "</li>" +
                                "</ul>"
                            area.innerHTML += ul3;
                        }else{
                            let ul4 = "<ul class=\"community-post-list\">" +
                                "<li>" +
                                "<div class='community-post'>" +
                                "<div class='author-avator set-bg' data-setbg='img/authors/1.jpg'>" +
                                "</div>" +
                                "<div class='post-content'>" +
                                "<div style='width: 95%; margin-top: 10px;'>" +
                                "<h5 style='display: inline'>" + realmodifyresult[j].nickName +
                                "</h5>" +
                                "<span class='post-date'>" + realmodifyresult[j].regDate +
                                "</span>" +
                                "</div>" +
                                "<p style='white-space: pre-wrap;'>" + realmodifyresult[j].review + "</p>" +
                                "</div>" +
                                "</div>" +
                                "</li>" +
                                "</ul>"
                            area.innerHTML += ul4;
                        }
                        $("button[name='deleteReview']").on('click',remove)
                        $("button[name='modifyReview']").on('click',modify)
                    }
                    let textarea2 = document.createElement('textarea')
                    textarea2.className="review_write"
                    textarea2.name = "writerReview"
                    textarea2.id="writerReview"
                    let btn5 = document.createElement('button')
                    btn5.type="button"
                    btn5.className="btn btn-warning"
                    btn5.name="reviewbutton"
                    btn5.id = "reviewbutton"
                    btn5.innerText="리뷰작성"
                    btn5.addEventListener('click', reviewmodifying)
                    let btn6 = document.createElement('button')
                    btn6.type="button"
                    btn6.className="btn btn-warning"
                    btn6.id = "backtoreviewmodal"
                    btn6.innerText="돌아가기"
                    btn6.addEventListener('click',function (){
                        $('.reviewmodal').fadeOut()
                    })
                    area2.append(textarea2)
                    area2.append(btn5)
                    area2.append(btn6)

                }catch (err){
                    console.log("realmodify fetch err : " + err)
                }

            }

            async function backtomodal(e){
                let area = document.getElementsByName('reviewarea')[num] // 리뷰내용foreach 도는 시작부분
                let area2 = document.getElementsByName('backtoreviewmodaldiv')[num] // 리뷰작성 및 돌아가기
                let writerFieldName = document.getElementsByName('fNames')[num]
                let modifyReviewUrl;
                while(area.hasChildNodes()){
                    area.removeChild(area.firstChild)
                }
                while(area2.hasChildNodes()){
                    area2.removeChild(area2.firstChild)
                }
                if (contextPath != null){
                    backToReviewModalUrl = contextPath + "/field/backToModalAjax?field="+writerFieldName.value
                } else {
                    backToReviewModalUrl = "/field/backToModalAjax?field="+writerFieldName.value
                }
                let option = {
                    method: "post"
                }
                try{
                    let backtomodalres = await fetch(backToReviewModalUrl, option)
                    let backtomodalresult = await backtomodalres.json()
                    for(let i=0; i<backtomodalresult.length;i++){
                        if(backtomodalresult[i].nickName == memberNickName){
                            let backtomodalresultul = "<ul class=\"community-post-list\">" +
                                "<li>" +
                                "<div class='community-post'>" +
                                "<div class='author-avator set-bg' data-setbg='img/authors/1.jpg'>" +
                                "</div>" +
                                "<div class='post-content'>" +
                                "<div style='width: 95%; margin-top: 10px;'>" +
                                "<h5 style='display: inline'>" + backtomodalresult[i].nickName +
                                "</h5>" +
                                "<span class='post-date'>" + backtomodalresult[i].regDate +
                                "</span>" +
                                "</div>" +
                                "<p style='white-space: pre-wrap;'>" + backtomodalresult[i].review + "</p>" +
                                "<div>" +
                                "<button type='button' class='"+backtomodalresult[i].review+"' id='modifyReview' name='modifyReview' value='" + backtomodalresult[i].regDate + "' style='cursor: pointer'>" +
                                "<img src='https://i.ibb.co/SPYLSG3/trash.png' style='float: right; font-size: 20px; margin-right: 10px;'></i>" +
                                "</button>" +
                                "<button type='button' class='"+backtomodalresult[i].review+"' id='deleteReview' name='deleteReview' value='" + backtomodalresult[i].regDate + "' style='cursor: pointer'>" +
                                "<img src='https://i.ibb.co/5vHn8bd/write.png' style='float: right; font-size: 20px; margin-right: 5px;'></i>" +
                                "</button>" +
                                "</div>" +
                                "</div>" +
                                "</div>" +
                                "</li>" +
                                "</ul>"
                            area.innerHTML += backtomodalresultul;
                        }else{
                            let backtomodalresultul2 = "<ul class=\"community-post-list\">" +
                                "<li>" +
                                "<div class='community-post'>" +
                                "<div class='author-avator set-bg' data-setbg='img/authors/1.jpg'>" +
                                "</div>" +
                                "<div class='post-content'>" +
                                "<div style='width: 95%; margin-top: 10px;'>" +
                                "<h5 style='display: inline'>" + backtomodalresult[i].nickName +
                                "</h5>" +
                                "<span class='post-date'>" + backtomodalresult[i].regDate +
                                "</span>" +
                                "</div>" +
                                "<p style='white-space: pre-wrap;'>" + backtomodalresult[i].review + "</p>" +
                                "</div>" +
                                "</div>" +
                                "</li>" +
                                "</ul>"
                            area.innerHTML += backtomodalresultul2;
                        }
                        $("button[name='deleteReview']").on('click',remove)
                        $("button[name='modifyReview']").on('click',modify)
                    }

                    let textarea2 = document.createElement('textarea')
                    textarea2.className="review_write"
                    textarea2.name = "writerReview"
                    textarea2.id="writerReview"
                    let btn3 = document.createElement('button')
                    btn3.type="button"
                    btn3.className="btn btn-warning"
                    btn3.name="reviewbutton"
                    btn3.id = "reviewbutton"
                    btn3.innerText="리뷰작성"
                    btn3.addEventListener('click', review)
                    let btn4 = document.createElement('button')
                    btn4.type="button"
                    btn4.className="btn btn-warning"
                    btn4.id = "backtomain"
                    btn4.innerText="돌아가기"
                    btn4.style="margin-top:-60px; margin-left:-84px;"
                    btn4.addEventListener('click',function (){
                        $('.reviewmodal').fadeOut()
                    })
                    area2.append(textarea2)
                    area2.append(btn3)
                    area2.append(btn4)
                }catch (err){
                    console.log("backtomodal fetch err : " + err)
                }
            }

            async function reviewmodifying(e){
                let area = document.getElementsByName('reviewarea')[num]; // 리뷰내용foreach 도는 시작부분
                let area2 = document.getElementsByName('backtoreviewmodaldiv')[num]; // 리뷰작성 및 돌아가기
                let writerReview = document.getElementsByName('writerReview')[num] // 작성하는 후기(textarea)
                let writerFieldName = document.getElementsByName('fNames')[num]
                console.log("수정중,후 작성내용 : " + writerReview.value)
                console.log("수정중,후 구장이름 : " + writerFieldName.value)

                let reviewmodifyingUrl;
                while(area.hasChildNodes()){
                    area.removeChild(area.firstChild)
                }
                while(area2.hasChildNodes()){
                    area2.removeChild(area2.firstChild)
                }
                if (contextPath != null){
                    reviewmodifyingUrl = contextPath + "/field/insertReviewModifying?field="+writerFieldName.value+"&name="+memberNickName+"&review="+writerReview.value
                } else {
                    reviewmodifyingUrl = "/field/insertReviewModifying?field="+writerFieldName.value+"&name="+memberNickName+"&review="+writerReview.value
                }
                let option = {
                    method: "post"
                }
                try{
                    let reviewModifyingres = await fetch(reviewmodifyingUrl, option)
                    let reviewModifyingresult = await reviewModifyingres.json()
                    for(let i=0; i<reviewModifyingresult.length;i++){
                        if(reviewModifyingresult[i].nickName == memberNickName){
                            //result[i].nickName result[i].review result[i].regDate
                            let reviewModifyingresultul = "<ul class=\"community-post-list\">" +
                                "<li>" +
                                "<div class='community-post'>" +
                                "<div class='author-avator set-bg' data-setbg='img/authors/1.jpg'>" +
                                "</div>" +
                                "<div class='post-content'>" +
                                "<div style='width: 95%; margin-top: 10px;'>" +
                                "<h5 style='display: inline'>" + reviewModifyingresult[i].nickName +
                                "</h5>" +
                                "<span class='post-date'>" + reviewModifyingresult[i].regDate +
                                "</span>" +
                                "</div>" +
                                "<p style='white-space: pre-wrap;'>" + reviewModifyingresult[i].review + "</p>" +
                                "<div>" +
                                "<button type='button' class='"+reviewModifyingresult[i].review+"' id='modifyReview' name='modifyReview' value='" + reviewModifyingresult[i].regDate + "' style='cursor: pointer'>" +
                                "<img src='https://i.ibb.co/SPYLSG3/trash.png' style='float: right; font-size: 20px; margin-right: 10px;'></i>" +
                                "</button>" +
                                "<button type='button' class='"+reviewModifyingresult[i].review+"' id='deleteReview' name='deleteReview' value='" + reviewModifyingresult[i].regDate + "' style='cursor: pointer'>" +
                                "<img src='https://i.ibb.co/5vHn8bd/write.png' style='float: right; font-size: 20px; margin-right: 5px;'></i>" +
                                "</button>" +
                                "</div>" +
                                "</div>" +
                                "</div>" +
                                "</li>" +
                                "</ul>"
                            area.innerHTML += reviewModifyingresultul;
                        }else{
                            let reviewModifyingresultul2 = "<ul class=\"community-post-list\">" +
                                "<li>" +
                                "<div class='community-post'>" +
                                "<div class='author-avator set-bg' data-setbg='img/authors/1.jpg'>" +
                                "</div>" +
                                "<div class='post-content'>" +
                                "<div style='width: 95%; margin-top: 10px;'>" +
                                "<h5 style='display: inline'>" + reviewModifyingresult[i].nickName +
                                "</h5>" +
                                "<span class='post-date'>" + reviewModifyingresult[i].regDate +
                                "</span>" +
                                "</div>" +
                                "<p style='white-space: pre-wrap;'>" + reviewModifyingresult[i].review + "</p>" +
                                "</div>" +
                                "</div>" +
                                "</li>" +
                                "</ul>"
                            area.innerHTML += reviewModifyingresultul2;
                        }
                        $("button[name='deleteReview']").on('click',remove)
                        $("button[name='modifyReview']").on('click',modify)
                    }

                    let textarea3 = document.createElement('textarea')
                    textarea3.className="review_write"
                    textarea3.name = "writerReview"
                    textarea3.id="writerReview"
                    let btn5 = document.createElement('button')
                    btn5.type="button"
                    btn5.className="btn btn-warning"
                    btn5.name="reviewbutton"
                    btn5.id = "reviewbutton"
                    btn5.innerText="리뷰작성"
                    btn5.addEventListener('click', review)
                    let btn6 = document.createElement('button')
                    btn6.type="button"
                    btn6.className="btn btn-warning"
                    btn6.id = "backtomain"
                    btn6.innerText="돌아가기"
                    btn6.style="margin-top:-60px; margin-left:-84px;"
                    btn6.addEventListener('click',function (){
                        $('.reviewmodal').fadeOut()
                    })
                    writerReview.value =""
                    area2.append(textarea3)
                    area2.append(btn5)
                    area2.append(btn6)
                }catch (err){
                    console.log("reviewmodifying fetch err : " + err)
                }
            }
            console.log(num);
        };

        // <span> 태그(X 버튼)를 클릭하면 Modal이 닫습니다.
        fadeoutmodals[num].onclick = function() {
            reviewmodals[num].style.display = "none";
        };
    };
}

// 원하는 Modal 수만큼 Modal 함수를 호출해서 funcs 함수에 정의합니다.
for(let i = 0; i < fadeinmodals.length; i++) {
    funcs[i] = Modal(i);
}

// 원하는 Modal 수만큼 funcs 함수를 호출합니다.
for(let j = 0; j < fadeinmodals.length; j++) {
    funcs[j]();
}

// Modal 영역 밖을 클릭하면 Modal을 닫습니다.
window.onclick = function(event) {
    if (event.target.className == "reviewmodal") {
        event.target.style.display = "none";
    }
};