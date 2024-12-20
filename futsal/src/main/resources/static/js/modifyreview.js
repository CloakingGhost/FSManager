$("button[name=modifyReview]").on('click',modify)

async function modify(e){
    let reviewRegDate = e.currentTarget.value
    let reviewContent = e.currentTarget.className
    let area = document.getElementById('reviewarea'); // 리뷰내용foreach 도는 시작부분
    let area2 = document.getElementById('backtoreviewmodaldiv'); // 리뷰작성 및 돌아가기
    let modifyReviewUrl;
    while(area.hasChildNodes()){
        area.removeChild(area.firstChild)
    }
    while(area2.hasChildNodes()){
        area2.removeChild(area2.lastChild)
    }
    if (contextPath != null){
        modifyReviewUrl = contextPath + "/field/modifyReviewAjax?field="+writerFieldName+"&nickName="+memberNickName+"&review="+reviewContent+"&regDate="+reviewRegDate
    } else {
        modifyReviewUrl = "/field/modifyReviewAjax?field="+writerFieldName+"&nickName="+memberNickName+"&review="+reviewContent+"&regDate="+reviewRegDate
    }
    let option = {
        method: "post"
    }
    try {
        let res = await fetch(modifyReviewUrl, option);
        let result = await res.json();
        console.log("결과 : " + result[0].review)
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
            "<textarea id='modifyReviewContent' placeholder='"+reviewContent+"' value='"+reviewContent+"' style='width:800px; height:200px; resize: none;'>" + "</textarea>" +
            "<div>" +
            "<button type='button' class='"+reviewContent+"' id='realmodify' name='realmodify' value='" + reviewRegDate + "'>" +
            "<i class=\"bi bi-check-circle\" style='float: right; font-size: 20px; margin-right: 10px;'></i>" +
            "</button>" +
            "<button type='button' class='"+reviewContent+"' id='deleteReview' name='deleteReview' value='" + reviewRegDate + "'>" +
            "<i class='bi bi-trash' style='float: right; font-size: 20px; margin-right: 5px;'></i>" +
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
                    "<p>" + result[j].review + "</p>" +
                    "<div>" +
                    "<button type='button' class='"+result[j].review+"' id='modifyReview' name='modifyReview' value='" + result[j].regDate + "'>" +
                    "<i class='bi bi-pencil-square' style='float: right; font-size: 20px; margin-right: 10px;'></i>" +
                    "</button>" +
                    "<button type='button' class='"+result[j].review+"' id='deleteReview' name='deleteReview' value='" + result[j].regDate + "'>" +
                    "<i class='bi bi-trash' style='float: right; font-size: 20px; margin-right: 5px;'></i>" +
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
                    "<p>" + result[j].review + "</p>" +
                    "</div>" +
                    "</div>" +
                    "</li>" +
                    "</ul>"
                area.innerHTML += ul4;
            }
            $("button[name='deleteReview']").on('click',remove)
            $("button[name='modifyReview']").on('click',modify)
            $("button[id='realmodify']").on('click',realmodify)
        }
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
    let afterModifyArea = document.getElementById('reviewarea'); // 리뷰내용foreach 도는 시작부분
    let afterModifyArea2 = document.getElementById('backtoreviewmodaldiv'); // 리뷰내용foreach 도는 시작부분

    let realmodifyReviewUrl;
    while(afterModifyArea.hasChildNodes()){
        afterModifyArea.removeChild(afterModifyArea.firstChild)
    }
    while(afterModifyArea2.hasChildNodes()){
        afterModifyArea2.removeChild(afterModifyArea2.firstChild)
    }
    if (contextPath != null){
        realmodifyReviewUrl = contextPath + "/field/realModifyReviewAjax?field="+writerFieldName+"&nickName="+memberNickName+"&review="+aftermodifyreview+"&regDate="+reviewRegDate
    } else {
        realmodifyReviewUrl = "/field/realModifyReviewAjax?field="+writerFieldName+"&nickName="+memberNickName+"&review="+aftermodifyreview+"&regDate="+reviewRegDate
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
                    "<p>" + realmodifyresult[j].review + "</p>" +
                    "<div>" +
                    "<button type='button' class='"+realmodifyresult[j].review+"' id='modifyReview' name='modifyReview' value='" + realmodifyresult[j].regDate + "'>" +
                    "<i class='bi bi-pencil-square' style='float: right; font-size: 20px; margin-right: 10px;'></i>" +
                    "</button>" +
                    "<button type='button' class='"+realmodifyresult[j].review+"' id='deleteReview' name='deleteReview' value='" + realmodifyresult[j].regDate + "'>" +
                    "<i class='bi bi-trash' style='float: right; font-size: 20px; margin-right: 5px;'></i>" +
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
                    "<p>" + realmodifyresult[j].review + "</p>" +
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
        afterModifyArea2.append(textarea2)
        afterModifyArea2.append(btn5)
        afterModifyArea2.append(btn6)


    }catch (err){
        console.log("realmodify fetch err : " + err)
    }

}

async function backtomodal(e){
    let area = document.getElementById('reviewarea'); // 리뷰내용foreach 도는 시작부분
    let area2 = document.getElementById('backtoreviewmodaldiv'); // 리뷰작성 및 돌아가기
    let modifyReviewUrl;
    while(area.hasChildNodes()){
        area.removeChild(area.firstChild)
    }
    while(area2.hasChildNodes()){
        area2.removeChild(area2.lastChild)
    }
    if (contextPath != null){
        backToReviewModalUrl = contextPath + "/field/backToModalAjax?field="+writerFieldName
    } else {
        backToReviewModalUrl = "/field/backToModalAjax?field="+writerFieldName
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
                    "<p>" + backtomodalresult[i].review + "</p>" +
                    "<div>" +
                    "<button type='button' class='"+backtomodalresult[i].review+"' id='modifyReview' name='modifyReview' value='" + backtomodalresult[i].regDate + "'>" +
                    "<i class='bi bi-pencil-square' style='float: right; font-size: 20px; margin-right: 10px;'></i>" +
                    "</button>" +
                    "<button type='button' class='"+backtomodalresult[i].review+"' id='deleteReview' name='deleteReview' value='" + backtomodalresult[i].regDate + "'>" +
                    "<i class='bi bi-trash' style='float: right; font-size: 20px; margin-right: 5px;'></i>" +
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
                    "<p>" + backtomodalresult[i].review + "</p>" +
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
        btn4.id = "backtofield"
        btn4.innerText="돌아가기"
        btn4.style="margin-top:-60px; margin-left:-85px;"
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
    let area = document.getElementById('reviewarea'); // 리뷰내용foreach 도는 시작부분
    let area2 = document.getElementById('backtoreviewmodaldiv'); // 리뷰작성 및 돌아가기
    let writerReview = document.getElementById('writerReview'); // 작성하는 후기(textarea)
    let reviewmodifyingUrl;
    while(area.hasChildNodes()){
        area.removeChild(area.firstChild)
    }
    while(area2.hasChildNodes()){
        area2.removeChild(area2.lastChild)
    }
    if (contextPath != null){
        reviewmodifyingUrl = contextPath + "/field/insertReviewModifying?field="+writerFieldName+"&name="+memberNickName+"&review="+writerReview.value
    } else {
        reviewmodifyingUrl = "/field/insertReviewModifying?field="+writerFieldName+"&name="+memberNickName+"&review="+writerReview.value
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
                    "<p>" + reviewModifyingresult[i].review + "</p>" +
                    "<div>" +
                    "<button type='button' class='"+reviewModifyingresult[i].review+"' id='modifyReview' name='modifyReview' value='" + reviewModifyingresult[i].regDate + "'>" +
                    "<i class='bi bi-pencil-square' style='float: right; font-size: 20px; margin-right: 10px;'></i>" +
                    "</button>" +
                    "<button type='button' class='"+reviewModifyingresult[i].review+"' id='deleteReview' name='deleteReview' value='" + reviewModifyingresult[i].regDate + "'>" +
                    "<i class='bi bi-trash' style='float: right; font-size: 20px; margin-right: 5px;'></i>" +
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
                    "<p>" + reviewModifyingresult[i].review + "</p>" +
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
        btn6.id = "backtofield"
        btn6.innerText="돌아가기"
        btn6.style="margin-top:-60px; margin-left:-85px;"
        btn6.addEventListener('click',function (){
            $('.reviewmodal').fadeOut()
        })
        area2.append(textarea3)
        area2.append(btn5)
        area2.append(btn6)
    }catch (err){
        console.log("reviewmodifying fetch err : " + err)
    }

}