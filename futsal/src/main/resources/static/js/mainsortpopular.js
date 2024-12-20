function convertURL(url) {
    let targetURL;
    contextPath ? targetURL = contextPath + url : targetURL = url;
    return targetURL;
}
let popular = document.getElementById('mainpopularhover')
let nickname;
try{
    nickname = document.getElementById("memberNickName").value
}catch (err){
    console.log("로그인 안해서 나는 오류 : " + err)
    nickname = null;
}
popular.addEventListener('click', sortpopular)
let contextfath = null;
if(contextPath != null){
    contextfath = contextPath;
}else{
    contextfath = null;
}

async function sortpopular(e) {
    let heart1 = "/img/icons/heart1.png";
    let heart2 = "/img/icons/heart2.png"
    if(contextPath != null){
        heart1 = contextPath + heart1
        heart2 = contextPath + heart2
    }
    let area = document.getElementById('findFields')
    let sortarea = document.getElementById('sortstandard')

    let sortpopularUrl = "/sortpopularAjax?"
    if (contextPath != null) {
        sortpopularUrl = contextPath + sortpopularUrl
    }
    while (sortarea.hasChildNodes()) {
        sortarea.removeChild(sortarea.firstChild)
    }
    let option = {method: "post"}
    try {
        let res = await fetch(sortpopularUrl, option);
        let result = await res.json();
        while (area.hasChildNodes()) {
            area.removeChild(area.firstChild)
        }

        for (let i = 0; i < result.length; i++) {
            let div1 = "<div class='col-md-3 mt-3 p-3' id='findFieldsStart'>" +
                "<div class='recent-game-item'>" +
                "<div class='rgi-thumb' style='background-image: url(" + result[i].fpic1 + "); background-size: cover;'></div>" +
                "<div class='rgi-content'>" +
                "<h5>" +
                "<a class='likeFName' style=\"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;\" href='" + convertURL("/field/click?fName=") +  result[i].fname + "'>" +
                "<span>" + result[i].fname + "</span>" +
                "</a>" +
                "</h5>" +
                "<p>" + '주소' + " : " + "<span>" + result[i].faddress + "</span>" + "</p>" +
                "<div class='rgi-extra'>" +
                "<div class='rgi-star' style='background-color: white;'>" +
                "<input type='hidden' value='" + result[i].fname + "' name='fNames'>"
            let reviewcount = "";
            if (!result[i].reviews) {
                reviewcount =
                    "<span style='margin-left: 6px;'>" + 0 + "</span>"
            } else {
                reviewcount =
                    "<span style='margin-left: 6px;'>" + result[i].reviews.length + "</span>"
            }
            let div2 =
                "<img type='button' id='fieldreview' class='fieldreivew' src='https://i.ibb.co/w7jZ3kw/image.png' style='cursor:pointer;'>" +
                "<div class='reviewmodal' + style='position: fixed; top: 0%; left: 0%; background: rgba(0, 0, 0, 0.8); display: none'>" +
                "<div class='review_modal_content' style='position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%);'>" +
                "<div class='container' style='height: 80%; overflow-x: hidden; overflow-y: auto'>" +
                "<div class='row'>" +
                "<div class='col-md-6 text-lg-right'>" +
                "</div>" +
                "</div>" +
                "<div id='reviewarea' name='reviewarea'>"
            let div3 = ""
            if (result[i].reviews?.length == 0 || result[i].reviews?.length == undefined) {}
            else {
                for (let j = 0; j < result[i].reviews?.length; j++) {
                    if(mynickname == result[i].reviews[j].nickName){
                        div3 += "<ul class=community-post-list>" +
                            "<li>" +
                            "<div class='community-post'>" +
                            "<div class='author-avator set-bg' data-setbg='img/authors/1.jpg'></div>" +
                            "<div class='post-content'>" +
                            "<div style='width: 95%; margin-top: 10px;'>" +
                            "<h5 style='display: inline'>" + result[i].reviews[j].nickName + "</h5>" + "&nbsp;" +
                            "<span class='post-date'>" + result[i].reviews[j].regDate + "</span>" +
                            "</div>" +
                            "<p style='white-space: pre-wrap;'>" + result[i].reviews[j].review + "</p>" +
                            "<div>" +
                            "<button type='button' class='" + result[i].reviews[j].review + "' id='modifyReview' name='modifyReview' value='" + result[i].reviews[j].regDate + "' style='cursor: pointer;'>" +
                            "<img src='https://i.ibb.co/SPYLSG3/trash.png' style='float: right; margin-right: 10px; font-size: 20px;'/>" +
                            "</button>" +
                            "<button type='button' class='" + result[i].reviews[j].review + "' id='deleteReview' name='deleteReview' value='" + result[i].reviews[j].regDate + "' style='cursor: pointer;'>" +
                            "<img src='https://i.ibb.co/5vHn8bd/write.png' style='float: right; margin-right: 5px; font-size: 20px;'/>" +
                            "</button>" +
                            "</div>" +
                            "</div>" +
                            "</div>" +
                            "</li>" +
                            "</ul>"
                    }else{
                        div3 += "<ul class=community-post-list>" +
                            "<li>" +
                            "<div class='community-post'>" +
                            "<div class='author-avator set-bg' data-setbg='img/authors/1.jpg'></div>" +
                            "<div class='post-content'>" +
                            "<div style='width: 95%; margin-top: 10px;'>" +
                            "<h5 style='display: inline'>" + result[i].reviews[j].nickName + "</h5>" + "&nbsp;" +
                            "<span class='post-date'>" + result[i].reviews[j].regDate + "</span>" +
                            "</div>" +
                            "<p style='white-space: pre-wrap;'>" + result[i].reviews[j].review + "</p>" +
                            "</div>" +
                            "</div>" +
                            "</li>" +
                            "</ul>"
                    }
                }
            }
            let div4 = "</div>" +
                "</div>" +
                "<div id='backtoreviewmodaldiv' name='backtoreviewmodaldiv'>" +
                "<textarea class='review_write' name='writerReview' id='writerReview'></textarea>" +
                "<button type='button' class='btn btn-warning' name='reviewbutton' id='reviewbutton'>" + '리뷰작성' + "</button>" +
                "<button type='button' style='margin-left: -84px; margin-top: -55px;' class='btn btn-warning' id='backtomain' name='backtomain'>" + '돌아가기' + "</button>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "<div id='fieldheart' name='fieldheart' class='rgi-heart'>"
            let likecount = "";
            if (result[i].likeListCount == undefined || result[i].likeListCount == 0) {
                likecount = "<span style='margin-left: 15px;' class='likelistcount'>" + '0' + "</span>"
            } else {
                likecount = "<span style='margin-left: 15px;' class='likelistcount'>" + result[i].likeListCount + "</span>"
            }
            let likeheart = "";
            if (result[i].likeListCount == undefined || result[i].likeListCount == 0) {
                likeheart =
                    "<button type='button' class='heartbrk' name='likeFieldArray' style='border: 0; cursor: pointer; background-color: #fff;width: 37px;' value='" + result[i].fname + "'>" +
                    "<img src='" + heart2 + "'>" +
                    "</button>"
            } else if (result[i].likeList.includes(nickname)) {
                likeheart =
                    "<button type='button' class='heartbrk' name='likeFieldArray' style='border: 0; cursor: pointer; background-color: #fff;width: 37px;' value='" + result[i].fname + "'>" +
                    "<img src='" + heart1 + "'>" +
                    "</button>"
            } else {
                likeheart =
                    "<button type='button' class='heartbrk' name='likeFieldArray' style='border: 0; cursor: pointer; background-color: #fff;width: 37px;' value='" + result[i].fname + "'>" +
                    "<img src='" + heart2 + "'>" +
                    "</button>"
            }
            let div5 = "</div>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>"

            if(mynickname == null){
                area.innerHTML += div1
            }else{
                area.innerHTML += (div1 + reviewcount + div2 + div3 + div4 + likecount + likeheart + div5)
            }

        } //for문 끝
        let sortareadiv = "<h5 class='mainpopularhover' id='mainpopularhover' style='font-weight: bold'>" + '인기' + "</h5>" + "&nbsp;|&nbsp;" +
            "<h5 class='mainlikehover' id='mainlikehover'>" + '찜' + "</h5>" + "&nbsp;|&nbsp;" +
            "<h5 class='mainreviewhover' id='mainreviewhover'>" + '리뷰' + "</h5>"
        sortarea.innerHTML += sortareadiv

        $('#mainpopularhover').on('click', sortpopular)
        $('#mainlikehover').on('click', sortlike)
        $('#mainreviewhover').on('click', sortreview)
        // $('img[class=fieldreivew]').on('click',function() {
        //     $("div[class=reviewmodal]").style.display = "block";
        // })

        let likeFieldArray = $("button[name='likeFieldArray']")

        for (let i = 0; i < likeFieldArray.length; i++) {
            console.log("likeFieldArray버튼의 길이 : " + likeFieldArray.length)
            $(likeFieldArray[i]).on('click', likefieldmain)

            async function likefieldmain(e) {
                let likelistcount = $(".likelistcount")[i]
                let fieldname = e.currentTarget.value
                console.log("몇번째 likeFieldArray : " + i)
                console.log("몇번째 likelistcount : " + i)

                likelistcount = likelistcount.innerText * 1
                let area = document.getElementsByName("fieldheart")
                console.log("좋아요 갯수 누르기전 결과 : " + likelistcount)
                console.log("좋아요 갯수타입 누르기전  : " + typeof (likelistcount))
                console.log("좋아요 누르기전 해당구장이름  : " + fieldname)

                while (area[i].hasChildNodes()) {
                    area[i].removeChild(area[i].firstChild)
                }
                let likefieldUrl = "/member/likeFieldAjax?field=" + fieldname
                if (contextPath != null) {
                    likefieldUrl = contextPath + likefieldUrl
                }
                let option = {method: "post"}

                try {
                    let res = await fetch(likefieldUrl, option);
                    let result = await res.text();
                    console.log("result : " + result)
                    let div = document.createElement('div')
                    let span = document.createElement('span')
                    span.className = "likelistcount"
                    span.style = "margin-left:15px;"
                    let btn = document.createElement('button')
                    btn.type = "button"
                    btn.className = "heartbrk"
                    btn.name = "likeFieldArray"
                    btn.value = fieldname
                    btn.style = "border:0; cursor: pointer; background-color: #fff; width: 37px;"
                    let img = document.createElement('img')
                    if (result == "success") {
                        likelistcount += 1
                        span.innerText = likelistcount
                        img.src = heart1
                    } else if (result == "fail") {
                        likelistcount -= 1
                        span.innerText = likelistcount
                        img.src = heart2
                    }
                    btn.addEventListener('click', likefieldmain)
                    div.append(span)
                    btn.append(img)
                    area[i].append(div)
                    area[i].append(btn)
                    console.log("좋아요 숫자 누른 후 결과 : " + likelistcount)
                    console.log("좋아요 갯수타입 누르기후  : " + typeof (likelistcount))

                } catch (err) {
                    console.log("좋아요js - err : " + err)
                }
            }
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
    }catch (err){
        console.log("mainsortpopular fetch 후 : " + err)
    }
}