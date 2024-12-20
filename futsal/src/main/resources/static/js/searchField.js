function convertURL(url) {
    return contextPath ? contextPath + url : url;
}

let inputBtn = document.getElementById('searchField')
if (inputBtn) {
    inputBtn.addEventListener('keyup', function (e) {
        keyupEvt(e)
    })
}

function keyupEvt(e) {
    let key = e.key || e.keyCode;
    if (key === 'Enter' || key === 13) {
        e.preventDefault();
        document.getElementById('searchFieldButton').click();
    }
}

let pageCount;
document.getElementById("searchFieldButton").addEventListener('click', function (e) {
    pageCount = 0;
    getFields();
    pageCount++
})
let mynickname;
try {
    mynickname = document.getElementById("memberNickName").value
} catch (err) {
    console.log("로그인 안해서 나는 오류 : " + err)
    mynickname = null;
}

async function getFields() {
    let fName = document.getElementById("searchField").value;
    console.log("검색한 문자열 : " + fName)
    const context_root = "/field/search?searchField="
    let targetUrl = context_root + fName;

    const options = {
        method: "post"
    }

    try {
        let result = await fetch(convertURL(targetUrl), options);
        let res = await result.json();
        console.log("res", res)

        let area = document.getElementById("findFields");
        while (area.hasChildNodes()) {
            area.removeChild(area.firstChild);
        }
        // let size = res.nafields.length
        // let size = res.length
        let size = res.numberOfElements
        for (let i = 0; i < size; i++) {
            // let item = res.nafields[i]
            // let item = res[i]
            let item = res.content[i]
            console.log(item.fname + " // " + item.likeListCount)

            let div1 = document.createElement('div');
            div1.className = "col-md-3 mt-3 p-3"
            div1.id = "findFieldsStart"
            let div2 = document.createElement('div');
            div2.className = "recent-game-item"
            let div3 = document.createElement('div');
            div3.className = "rgi-thumb"
            div3.style.backgroundImage = `url(${item.fpic1})`;
            div3.style.backgroundSize = "cover";
            let div4 = document.createElement('div');
            div4.className = "rgi-content"
            let h5 = document.createElement('h5');
            let a1 = document.createElement('a');
            a1.className = "likeFName"
            a1.href = "field/click?fName=" + item.fname;
            a1.style = 'white-space: nowrap; overflow: hidden; text-overflow: ellipsis;'
            let span1 = document.createElement('span');
            span1.innerText = item.fname;

            let p1 = document.createElement('p');
            p1.innerText = "주소 : "
            let span2 = document.createElement('span');
            span2.innerText = item.faddress;

            let div5 = document.createElement('div')
            div5.className = "rgi-extra"
            let div6 = document.createElement('div')
            div6.className = "rgi-star"
            div6.id = "rgi-starid"

            let input1 = document.createElement('input')
            input1.type = "hidden"
            input1.name = "fNames"
            input1.value = item.fname

            div6.append(input1)

            let div7 = document.createElement('div')
            div7.id = "fieldheart"
            div7.className = "rgi-heart"
            div7.name = "fieldheart"
            let rspan1 = document.createElement('span')
            rspan1.style = "margin-left: 15px;"
            rspan1.className = "likelistcount"
            rspan1.innerText = item.likeListCount

            let rspan2 = document.createElement('span')
            rspan2.style = "margin-left: 15px;"
            rspan2.className = "likelistcount"
            rspan2.innerText = "0"
            let rbtn1 = document.createElement('button')
            rbtn1.type = "button"
            rbtn1.className = "heartbrk"
            rbtn1.name = "likeFieldArray"
            rbtn1.value = item.fname
            rbtn1.style = "border:0; cursor: pointer; background-color: #fff; width: 37px;"
            let img1 = document.createElement('img')
            img1.src = convertURL("/img/icons/heart1.png")
            rbtn1.append(img1)
            let rbtn2 = document.createElement('button')
            rbtn2.type = "button"
            rbtn2.className = "heartbrk"
            rbtn2.name = "likeFieldArray"
            rbtn2.value = item.fname
            rbtn2.style = "border:0; cursor: pointer; background-color: #fff; width: 37px;"
            let img2 = document.createElement('img')
            img2.src = convertURL("/img/icons/heart2.png")
            rbtn2.append(img2)

            let reviewcount = "";
            !item.reviews ?
                reviewcount = "<span style='margin-left: 6px;'>0</span>" :
                reviewcount = `<span style='margin-left: 6px;'>${item.reviews.length}</span>`

            let div61 =
                "<img type='button' id='fieldreview' class='fieldreivew' src='https://i.ibb.co/w7jZ3kw/image.png' style='cursor:pointer;'>" +
                "<div class='reviewmodal' style='position: fixed; top: 0%; left: 0%; background: rgba(0, 0, 0, 0.8); display: none'>" +
                "<div class='review_modal_content' style='position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%);'>" +
                "<div class='container' style='height: 80%; overflow-x: hidden; overflow-y: auto'>" +
                "<div class='row'>" +
                "<div class='col-md-6 text-lg-right'>" +
                "</div>" +
                "</div>" +
                "<div id='reviewarea' name='reviewarea'>"
            let div62 = "";
            // if (!res.nafields[i].reviews?.length) {
            // if (!res[i].reviews?.length) {
            if (!res.content[i].reviews?.length) {
                div62 = ""
            } else {
                // let size = res.nafields[i].reviews?.length
                // let size = res[i].reviews?.length
                let size = res.content[i].reviews?.length
                for (let j = 0; j < size; j++) {
                    // let item = res.nafields[i].reviews[j]
                    // let item = res[i].reviews[j]
                    let item = res.content[i].reviews[j]
                    if (mynickname == item.nickName) {
                        div62 +=
                            "<ul class=community-post-list>" +
                            "<li>" +
                            "<div class='community-post'>" +
                            "<div class='author-avator set-bg' data-setbg=" + convertURL('/img/authors/1.jpg') + "></div>" +
                            "<div class='post-content'>" +
                            "<div style='width: 95%; margin-top: 10px;'>" +
                            "<h5 style='display: inline'>" + item.nickName + "</h5>&nbsp;" +
                            "<span class='post-date'>" + item.regDate + "</span>" +
                            "</div>" +
                            "<p style='white-space: pre-wrap;'>" + item.review + "</p>" +
                            "<div>" +
                            "<button type='button' class='" + item.review + "' id='modifyReview' name='modifyReview' value='" + item.regDate + "' style='cursor: pointer;'>" +
                            "<img src='https://i.ibb.co/SPYLSG3/trash.png' style='float: right; margin-right: 10px; font-size: 20px;'/>" +
                            "</button>" +
                            "<button type='button' class='" + item.review + "' id='deleteReview' name='deleteReview' value='" + item.regDate + "' style='cursor: pointer;'>" +
                            "<img src='https://i.ibb.co/5vHn8bd/write.png' style='float: right; margin-right: 5px; font-size: 20px;'/>" +
                            "</button>" +
                            "</div>" +
                            "</div>" +
                            "</div>" +
                            "</li>" +
                            "</ul>"
                    } else {
                        div62 +=
                            "<ul class=community-post-list>" +
                            "<li>" +
                            "<div class='community-post'>" +
                            "<div class='author-avator set-bg' data-setbg=" + convertURL('/img/authors/1.jpg') + "></div>" +
                            "<div class='post-content'>" +
                            "<div style='width: 95%; margin-top: 10px;'>" +
                            "<h5 style='display: inline'>" + item.nickName + "</h5>" + "&nbsp;" +
                            "<span class='post-date'>" + item.regDate + "</span>" +
                            "</div>" +
                            "<p style='white-space: pre-wrap;'>" + item.review + "</p>" +
                            "</div>" +
                            "</div>" +
                            "</li>" +
                            "</ul>"
                    }
                }
            }
            let div63 = "</div>" +
                "</div>" +
                "<div id='backtoreviewmodaldiv' name='backtoreviewmodaldiv'>" +
                "<textarea class='review_write' name='writerReview' id='writerReview'></textarea>" +
                "<button type='button' class='btn btn-warning' name='reviewbutton' id='reviewbutton'>리뷰작성</button>" +
                "<button type='button' style='margin-left: -84px; margin-top: -55px;' class='btn btn-warning' id='backtomain' name='backtomain'>돌아가기</button>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>"

            div6.innerHTML += (reviewcount + div61 + div62 + div63)

            // if (!res.nafields[i].likeListCount) {
            // if (!res[i].likeListCount) {
            if (!res.content[i].likeListCount) {
                div7.append(rspan2)
                div7.append(rbtn2)
            } else {
                // if (res.nafields[i].likeList.indexOf(mynickname) == -1) {
                // if (res[i].likeList.indexOf(mynickname) == -1) {
                if (res.content[i].likeList.indexOf(mynickname) == -1) {
                    div7.append(rspan1)
                    div7.append(rbtn2)
                } else {
                    div7.append(rspan1)
                    div7.append(rbtn1)
                }
            }

            if (!mynickname) {
                p1.append(span2);
                a1.append(span1)
                h5.append(a1)
                div4.append(h5)
                div4.append(p1)
                div2.append(div3)
                div2.append(div4)
                div1.append(div2)
                area.append(div1)

            } else {
                div5.append(div6)
                div5.append(div7)
                div4.append(div5)
                p1.append(span2);
                a1.append(span1)
                h5.append(a1)
                div4.append(h5)
                div4.append(p1)
                div2.append(div3)
                div2.append(div4)
                div1.append(div2)
                area.append(div1)
            }
        }
        let likeFieldArray = document.querySelectorAll("button[name='likeFieldArray']")
        /*console.log("likeFieldArray : " + likeFieldArray)
              console.log("구장검색 후 구장 좋아요 버튼 갯수(구장갯수) : " + likeFieldArray.length)*/
        size = likeFieldArray.length
        for (let i = 0; i < size; i++) {
            likeFieldArray[i].addEventListener("click", function (e) {
                likefieldmain(e, i)
            })
        }

        size = fadeinmodals.length
        for (let i = 0; i < size; i++) {
            funcs[i] = Modal(i);// 원하는 Modal 수만큼 Modal 함수를 호출해서 funcs 함수에 정의합니다.
            funcs[i]();         // 원하는 Modal 수만큼 funcs 함수를 호출합니다.
        }
        window.onclick = function (event) { // Modal 영역 밖을 클릭하면 Modal을 닫습니다.
            if (event.target.className == "reviewmodal") event.target.style.display = "none";
        };
    } catch (e) {
        console.log(e);
    }
}

async function likefieldmain(e, i) {
    let arr_likelistcount = document.querySelectorAll("span.likelistcount")
    let likelistcount = arr_likelistcount[i]
    let fieldname;
    e.target.src ? fieldname = e.target.parentElement.value : fieldname = e.currentTarget.value;
    // console.log("몇번째인덱스 likeFieldArray : " + i)
    likelistcount = likelistcount.innerHTML * 1
    let area = document.getElementsByClassName("rgi-heart")
    /*    console.log("좋아요 갯수 누르기전 결과 : " + likelistcount)
           console.log("좋아요 갯수타입 누르기전  : " + typeof (likelistcount))
           console.log("좋아요 누르기전 해당구장이름  : " + fieldname)*/

    while (area[i].hasChildNodes()) {
        area[i].removeChild(area[i].firstChild)
    }
    let likefieldUrl = "/member/likeFieldAjax?field=" + fieldname
    let option = {method: "post"}

    try {
        let res = await fetch(convertURL(likefieldUrl), option);
        let result = await res.text();
        console.log("result : " + result)
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
            img.src = convertURL("/img/icons/heart1.png")
        } else if (result == "fail") {
            likelistcount -= 1
            span.innerText = likelistcount
            img.src = convertURL("/img/icons/heart2.png")
        }
        btn.addEventListener('click', function () {
            likefieldmain(e, i)
        })
        btn.append(img)
        area[i].append(span)
        area[i].append(btn)
        /*console.log("좋아요 숫자 누른 후 결과 : " + likelistcount)
              console.log("좋아요 갯수타입 누르기후  : " + typeof (likelistcount))*/
    } catch (err) {
        console.log("좋아요js - err : ", err)
    }
}

window.addEventListener("scroll", function () {
    const SCROLLED_HEIGHT = window.scrollY; // TOP에서부터의 스크롤의 거리
    const WINDOW_HEIGHT = window.innerHeight; // 보이는 화면의 길이
    const DOC_TOTAL_HEIGHT = document.documentElement.scrollHeight; // 화면의 총 길이
    let IS_BOTTOM = WINDOW_HEIGHT + SCROLLED_HEIGHT >= DOC_TOTAL_HEIGHT + 17;


    if (pageCount && IS_BOTTOM) {
        addContent("/field/search").then(function () {
            pageCount++;
        })
    }
});

async function addContent(url) {
    let fName = document.getElementById("searchField").value;
    console.log("검색한 문자열 : " + fName)
    let targetUrl = url + '?searchField=' + fName + '&page=' + pageCount;

    const options = {
        method: "post"
    }
    try {
        let result = await fetch(convertURL(targetUrl), options);
        let res = await result.json();
        console.log("res", res)
        let area = document.getElementById("findFields");
        let size = res.numberOfElements
        for (let i = 0; i < size; i++) {
            // let item = res.nafields[i]
            // let item = res[i]
            const item = res.content[i]

            let div1 = document.createElement('div');
            div1.className = "col-md-3 mt-3 p-3"
            div1.id = "findFieldsStart"
            let div2 = document.createElement('div');
            div2.className = "recent-game-item"
            let div3 = document.createElement('div');
            div3.className = "rgi-thumb"
            div3.style.backgroundImage = `url(${item.fpic1})`;
            div3.style.backgroundSize = "cover";
            let div4 = document.createElement('div');
            div4.className = "rgi-content"
            let h5 = document.createElement('h5');
            let a1 = document.createElement('a');
            a1.className = "likeFName"
            a1.href = "field/click?fName=" + item.fname;
            a1.style = 'white-space: nowrap; overflow: hidden; text-overflow: ellipsis;'
            let span1 = document.createElement('span');
            span1.innerText = item.fname;

            let p1 = document.createElement('p');
            p1.innerText = "주소 : "
            let span2 = document.createElement('span');
            span2.innerText = item.faddress;

            let div5 = document.createElement('div')
            div5.className = "rgi-extra"
            let div6 = document.createElement('div')
            div6.className = "rgi-star"
            div6.id = "rgi-starid"
            let input1 = document.createElement('input')
            input1.type = "hidden"
            input1.name = "fNames"
            input1.value = item.fname
            div6.append(input1)

            let div7 = document.createElement('div')
            div7.id = "fieldheart"
            div7.className = "rgi-heart"
            div7.name = "fieldheart"
            let rspan1 = document.createElement('span')
            rspan1.style = "margin-left: 15px;"
            rspan1.className = "likelistcount"
            rspan1.innerText = item.likeListCount

            let rspan2 = document.createElement('span')
            rspan2.style = "margin-left: 15px;"
            rspan2.className = "likelistcount"
            rspan2.innerText = "0"
            let rbtn1 = document.createElement('button')
            rbtn1.type = "button"
            rbtn1.className = "heartbrk"
            rbtn1.name = "likeFieldArray"
            rbtn1.value = item.fname
            rbtn1.style = "border:0; cursor: pointer; background-color: #fff; width: 37px;"
            let img1 = document.createElement('img')
            img1.src = convertURL("/img/icons/heart1.png")
            rbtn1.append(img1)
            let rbtn2 = document.createElement('button')
            rbtn2.type = "button"
            rbtn2.className = "heartbrk"
            rbtn2.name = "likeFieldArray"
            rbtn2.value = item.fname
            rbtn2.style = "border:0; cursor: pointer; background-color: #fff; width: 37px;"
            let img2 = document.createElement('img')
            img2.src = convertURL("/img/icons/heart2.png")
            rbtn2.append(img2)

            let reviewcount = "";
            !item.reviews ?
                reviewcount = "<span style='margin-left: 6px;'>0</span>" :
                reviewcount = `<span style='margin-left: 6px;'>${item.reviews.length}</span>`

            let div61 =
                "<img type='button' id='fieldreview' class='fieldreivew' src='https://i.ibb.co/w7jZ3kw/image.png' style='cursor:pointer;'>" +
                "<div class='reviewmodal' style='position: fixed; top: 0%; left: 0%; background: rgba(0, 0, 0, 0.8); display: none'>" +
                "<div class='review_modal_content' style='position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%);'>" +
                "<div class='container' style='height: 80%; overflow-x: hidden; overflow-y: auto'>" +
                "<div class='row'>" +
                "<div class='col-md-6 text-lg-right'>" +
                "</div>" +
                "</div>" +
                "<div id='reviewarea' name='reviewarea'>"
            let div62 = "";
            // if (!res.nafields[i].reviews?.length) {
            // if (!res[i].reviews?.length) {
            if (!res.content[i].reviews?.length) {
                div62 = ""
            } else {
                // let size = res.nafields[i].reviews?.length
                // let size = res[i].reviews?.length
                let size = res.content[i].reviews?.length
                for (let j = 0; j < size; j++) {
                    // let item = res.nafields[i].reviews[j]
                    // let item = res[i].reviews[j]
                    let item = res.content[i].reviews[j]
                    if (mynickname == item.nickName) {
                        div62 +=
                            "<ul class=community-post-list>" +
                            "<li>" +
                            "<div class='community-post'>" +
                            "<div class='author-avator set-bg' data-setbg=" + convertURL('/img/authors/1.jpg') + "></div>" +
                            "<div class='post-content'>" +
                            "<div style='width: 95%; margin-top: 10px;'>" +
                            "<h5 style='display: inline'>" + item.nickName + "</h5>&nbsp;" +
                            "<span class='post-date'>" + item.regDate + "</span>" +
                            "</div>" +
                            "<p style='white-space: pre-wrap;'>" + item.review + "</p>" +
                            "<div>" +
                            "<button type='button' class='" + item.review + "' id='modifyReview' name='modifyReview' value='" + item.regDate + "' style='cursor: pointer;'>" +
                            "<img src='https://i.ibb.co/SPYLSG3/trash.png' style='float: right; margin-right: 10px; font-size: 20px;'/>" +
                            "</button>" +
                            "<button type='button' class='" + item.review + "' id='deleteReview' name='deleteReview' value='" + item.regDate + "' style='cursor: pointer;'>" +
                            "<img src='https://i.ibb.co/5vHn8bd/write.png' style='float: right; margin-right: 5px; font-size: 20px;'/>" +
                            "</button>" +
                            "</div>" +
                            "</div>" +
                            "</div>" +
                            "</li>" +
                            "</ul>"
                    } else {
                        div62 +=
                            "<ul class=community-post-list>" +
                            "<li>" +
                            "<div class='community-post'>" +
                            "<div class='author-avator set-bg' data-setbg=" + convertURL('/img/authors/1.jpg') + "></div>" +
                            "<div class='post-content'>" +
                            "<div style='width: 95%; margin-top: 10px;'>" +
                            "<h5 style='display: inline'>" + item.nickName + "</h5>" + "&nbsp;" +
                            "<span class='post-date'>" + item.regDate + "</span>" +
                            "</div>" +
                            "<p style='white-space: pre-wrap;'>" + item.review + "</p>" +
                            "</div>" +
                            "</div>" +
                            "</li>" +
                            "</ul>"
                    }
                }
            }
            let div63 = "</div>" +
                "</div>" +
                "<div id='backtoreviewmodaldiv' name='backtoreviewmodaldiv'>" +
                "<textarea class='review_write' name='writerReview' id='writerReview'></textarea>" +
                "<button type='button' class='btn btn-warning' name='reviewbutton' id='reviewbutton'>리뷰작성</button>" +
                "<button type='button' style='margin-left: -84px; margin-top: -55px;' class='btn btn-warning' id='backtomain' name='backtomain'>돌아가기</button>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>"

            div6.innerHTML += (reviewcount + div61 + div62 + div63)

            // if (!res.nafields[i].likeListCount) {
            // if (!res[i].likeListCount) {
            if (!res.content[i].likeListCount) {
                div7.append(rspan2)
                div7.append(rbtn2)
            } else {
                // if (res.nafields[i].likeList.indexOf(mynickname) == -1) {
                // if (res[i].likeList.indexOf(mynickname) == -1) {
                if (res.content[i].likeList.indexOf(mynickname) == -1) {
                    div7.append(rspan1)
                    div7.append(rbtn2)
                } else {
                    div7.append(rspan1)
                    div7.append(rbtn1)
                }
            }

            if (!mynickname) {
                p1.append(span2);
                a1.append(span1)
                h5.append(a1)
                div4.append(h5)
                div4.append(p1)
                div2.append(div3)
                div2.append(div4)
                div1.append(div2)
                area.append(div1)

            } else {
                div5.append(div6)
                div5.append(div7)
                div4.append(div5)
                p1.append(span2);
                a1.append(span1)
                h5.append(a1)
                div4.append(h5)
                div4.append(p1)
                div2.append(div3)
                div2.append(div4)
                div1.append(div2)
                area.append(div1)
            }
        }
        let likeFieldArray = document.querySelectorAll("button[name='likeFieldArray']")
        /*console.log("likeFieldArray : " + likeFieldArray)
              console.log("구장검색 후 구장 좋아요 버튼 갯수(구장갯수) : " + likeFieldArray.length)*/
        size = likeFieldArray.length
        for (let i = 0; i < size; i++) {
            likeFieldArray[i].addEventListener("click", function (e) {
                likefieldmain(e, i)
            })
        }

        size = fadeinmodals.length
        for (let i = 0; i < size; i++) {
            funcs[i] = Modal(i);// 원하는 Modal 수만큼 Modal 함수를 호출해서 funcs 함수에 정의합니다.
            funcs[i]();         // 원하는 Modal 수만큼 funcs 함수를 호출합니다.
        }
        window.onclick = function (event) { // Modal 영역 밖을 클릭하면 Modal을 닫습니다.
            if (event.target.className == "reviewmodal") event.target.style.display = "none";
        };
    } catch (e) {
        console.log(e)
    }
}