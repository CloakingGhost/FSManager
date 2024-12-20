function convertURL(url) {
    return contextPath ? contextPath + url : url;
}

//board.html
function watchBoard(e) {
    let targetBoard = e.parentElement.parentElement.cells[0].innerText;
    let url = '/community/communication/' + targetBoard;
    location = convertURL(url)
}

function deleteBoard() {
    let brd = document.querySelector("input[name='dlBrd']:checked")
    let idx = brd.getAttribute('id');
    let nickName = brd.parentNode.parentElement.cells[3].textContent
    let url = '/community/fetchDeleteBoard'

    if (confirm('게시글을 삭제하시겠습니까?')) {
        fetchDeleteBoard(idx, convertURL(url), nickName);
    }

}

async function fetchDeleteBoard(idx, url, nickName) {
    let formData = new FormData()
    formData.append('idx', idx)
    formData.append('nickName', nickName)
    const option = {
        method: "POST",
        body: formData
    }
    try {
        let response = await fetch(url, option)
        let result_arr = await response.json()
        location.reload()
    } catch (e) {
        console.log(e)
    }
}

function modifyBoard() {
    let brd = document.querySelector("input[name='mdBrd']:checked");
    let idx = brd.id;
    let teamName = document.querySelector('tbody>tr').cells[1].textContent;
    let nickName = document.querySelector('tbody>tr').cells[3].textContent;
    if (!teamName) teamName = "-1";
    let url = `/community/fetchModifyBoard/${idx}/${teamName}/${nickName}`;
    location = convertURL(url);
}

let inputBtn = document.getElementById('searchInput')
if (inputBtn) {
    inputBtn.addEventListener('keyup', function (e) {
        keyupEvt(e)
    })
}

function keyupEvt(e) {
    let key = e.key || e.keyCode;
    if (key === 'Enter' || key === 13) {
        e.preventDefault();
        document.getElementById('searchBtn').click();

    }
}

let pageCount = 0;
let arr = [, , , ,]

function searchBoard(e) {
    pageCount = 0;
    let searchOption = document.querySelector("select[name='search']")
    searchOption = searchOption.options[searchOption.selectedIndex].value;
    let num = 0;
    let cUrl = convertURL(window.location.pathname);

    let url = '/community/fetchSearchBoard';
    let word = e.previousElementSibling.value.trim();
    const regExp = /^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|/\s/g]*$/;

    if (!regExp.test(word)) {
        alert('한글, 영어, 숫자만 입력 가능합니다.');
    } else {
        cUrl === convertURL('/community/board') ? num : num++;
        !word ? searchOption = 'all' : searchOption;
        if (cUrl === convertURL('/community/board') && searchOption === 'all') {
            location = convertURL('/community/board')
        } else if (cUrl === convertURL('/community/myBoard') && searchOption === 'all') {
            location = convertURL('/community/myBoard')
        } else {
            fetchSearchBoard(word, searchOption, convertURL(url), num)
        }
    }
    arr = [word, searchOption, num, undefined]
}

window.addEventListener("scroll", function () {
    const SCROLLED_HEIGHT = window.scrollY; // 스크롤 길이
    const WINDOW_HEIGHT = window.innerHeight; // 보이는 화면의 스크롤 길이
    const DOC_TOTAL_HEIGHT = document.documentElement.scrollHeight; // 스크롤의 총 길이
    const IS_BOTTOM = WINDOW_HEIGHT + SCROLLED_HEIGHT >= DOC_TOTAL_HEIGHT;
    if (pageCount && IS_BOTTOM) {
        console.log("HI!")
        arr[3] = pageCount
        addContent(arr).then(function () {
            pageCount++
        });

    }
});

async function addContent(arr) {
    let tBody = document.querySelector('tbody');
    let targetUrl = '/community/fetchSearchBoard'
    let UrlOption = targetUrl + `?word=${arr[0]}&searchResult=${arr[1]}&num=${arr[2]}&page=${arr[3]}&size=10&sort=idx,desc`
    const option = {method: 'GET'}

    let str = '';
    try {
        let response = await fetch(convertURL(UrlOption), option);
        let result = await response.json();
        const size = result.dto.numberOfElements
        for (let i = 0; i < size; i++) {
            str += '<tr>'
            str += getStr(result.dto.content, i, arr[2]);
            str += '</tr>'
        }
        tBody.innerHTML += str
    } catch (e) {
        console.log(e)
    }
}

async function fetchSearchBoard(word, searchOption, url, num) {
    let tBody = document.getElementById('contentTable');
    let butnns = document.getElementById('butnns');
    let formdata = new FormData();
    let str = '';
    let btn_str = '';
    let cUrl = convertURL(window.location.pathname);
    let boardURL = '/community/board';
    cUrl === convertURL(boardURL) ? boardURL = convertURL('/community/myBoard') : boardURL;
    formdata.append('num', num);
    formdata.append('word', word);
    formdata.append('searchResult', searchOption);
    const option = {
        method: 'POST',
        body: formdata
    }
    try {
        let response = await fetch(convertURL(url), option);
        let result_arr = await response.json();
        if (!num && result_arr.dto.empty) {
            str = `<td colspan='7'>( ${searchOption} ) 검색결과가 존재하지 않습니다.</td>`
        } else if (num && result_arr.dto.empty) {
            str = `<td colspan='9'>( ${searchOption} ) 검색결과가 존재하지 않습니다.</td>`
        } else {
            const size = result_arr.dto.numberOfElements
            for (let i = 0; i < size; i++) {
                str += '<tr>'
                str += getStr(result_arr.dto.content, i, num);
                str += '</tr>'
            }
        }
        if (num) {
            btn_str = "<a href=" + convertURL(boardURL) + " title='Button fade blue/green' class='butnn'>뒤로가기</a><a href='#' title='Button fade blue/green' class='butnn mx-1' onclick='modifyBoard()'>수정하기</a><a href='#' title='Button fade blue/green' class='butnn' onclick='deleteBoard()'>삭제하기</a>";
        } else {
            btn_str = "<a href=" + convertURL(boardURL) + " title='Button fade blue/green' class='butnn'>내글보기</a><a href=" + convertURL('/community/write') + " title='Button fade blue/green' class='butnn ml-1'>작성하기</a>"
        }
        !pageCount ? tBody.innerHTML = str : tBody.innerHTML += str;
        butnns.innerHTML = btn_str;
        pageCount++
    } catch (e) {
        console.log(e)
    }
}

let getStr = function (result_arr, result, num) {
    let str = `<td>${result_arr[result].idx}</td>
               <td>${result_arr[result].tname}</td>
               <td><span onclick="watchBoard(this)">${result_arr[result].title}</span>
                   ${!result_arr[result].reply ? '<span>(0)</span>' : `<span>(${result_arr[result].reply.length})</span>`}
               </td>
               <td>${result_arr[result].writer}</td>
               <td>${result_arr[result].uniform}</td>
               <td>${result_arr[result].views}</td>
               <td>${result_arr[result].regDate}</td>`;

    if (num) {
        str += `<td><input type="radio" name="mdBrd" id="${result_arr[result].idx}"></td><td><input type="radio" name="dlBrd" id="${result_arr[result].idx}"></td>`
    }

    return str;
}


//write.html
function boardSubmit() {
    let btnName = document.querySelector("button.px-3").textContent.trim()
    let selectForm = document.getElementsByName("rsvFld")[0]
    let optionList = selectForm.options[selectForm.selectedIndex]
    let optionItem = optionList.text.trim().split(" | ")
    let form = document.forms[0]
    optionList.setAttribute("value", optionItem)


    let content1 = document.getElementById("title").value.trim()
    let content2 = document.getElementById("message").value.trim()

    if (!content1) {
        alert("제목을 입력해주세요")
    } else if (!content2) {
        alert("내용을 입력해주세요")
    } else {
        alert("작성 완료")
        if (btnName[0] === "작") {
            form.action = convertURL("/community/doWrite")
            form.submit()
        } else {
            form.action = convertURL("/community/modifyBoard")
            form.submit()
        }
    }
}

//communication.html
let teamInfo = document.getElementsByClassName("teamInfo")[0]
let moreInfo = document.getElementById("moreInfo")
if (teamInfo) {
    teamInfo.style.display = 'none';
    moreInfo.addEventListener('click', function () {
        if (getComputedStyle(teamInfo).display === 'none') {
            moreInfo.innerText = "팀정보 닫기";
            teamInfo.setAttribute("style", "display:block");
        } else {
            moreInfo.innerText = "팀정보 보기";
            teamInfo.setAttribute("style", "display:none");
        }
    })
}
let mes = document.getElementsByName("message")[0]; // 댓글 내용
let wrt = document.getElementsByName("writer")[0]; // 댓글 작성자 == nickName
let bNo = document.getElementsByName("bNo")[0]; // 게시글 번호
let replies = document.getElementById("replies"); // 댓글 전체를 감싼 div#id, 댓글 새로고침 때 사용

// 수정 버튼 눌렀을 때
function modify(e) {

    if (document.getElementById("cancel")) {
        document.getElementById("cancel").click()
    }
    let str = ""
    let parentNode = e.parentNode
    let idx = parentNode.lastElementChild.value
    let arr = [, , ,];
    arr[0] = parentNode.firstElementChild
    arr[1] = arr[0].nextElementSibling
    arr[2] = arr[1].nextElementSibling

    str += "<div class='mr-2' style='border: 2px solid #73685d; border-radius: 3px; display: inline-block;'>"
    str += "<div class='nickName'><strong>" + arr[0].textContent + "</strong></div>"
    str += "<textarea form='replyFrm' name='editReply' cols='125' rows='3' maxlength='300'"
    str += "style='resize: none; overflow: auto; border:0; padding: 5px; white-space: pre-wrap;'"
    str += "placeholder='댓글을 남겨 주세요.'>" + arr[1].textContent + "</textarea>"
    str += "<div class='comDate'>" + arr[2].textContent + "</div>"
    str += "</div><span>"
    str += "<input class='btnn mr-1' type='button' value='수정' onclick='checkModify(this)'>"
    str += "<input class='btnn' type='button' id='cancel' value='취소' onclick='cancelReply(this)'>"
    str += "<hr><input type='hidden' name='idx' value='" + idx + "'></span>"

    parentNode.innerHTML = str;


}

// 댓글수정 중 취소버튼 누르면 복구
function cancelReply(e) {
    let nodes = e.parentNode.previousElementSibling.childNodes
    let idx = e.parentNode.lastElementChild.value
    let str = ""
    str += "<div class='nickName'>" + nodes[0].textContent + "</div>"
    str += "<div class='댓글내용' style='white-space: pre-wrap;'>" + nodes[1].textContent + "</div>"
    str += "<div class='comDate'>" + nodes[2].textContent + "</div>"
    str += "<input class='btnn mr-1' type='button' value='삭제' onclick='deleteReply(this)'>"
    str += "<input class='btnn' type='button' value='수정' onclick='modify(this)'>"
    str += "<hr><input type='hidden' name='idx' value='" + idx + "'>"
    e.parentNode.parentNode.innerHTML = str
}

//댓글 작성
function writeReply() {
    let url = "/community/fetchReply"
    if (!mes.value.trim()) {
        alert("내용을 작성해주세요")
    } else if (confirm("작성하시겠습니까?")) {
        addReply(mes.value, wrt.value, bNo.value, convertURL(url));
        mes.value = ""
    }
}

//댓글 삭제
function deleteReply(e) {
    let url = "/community/deleteReply"
    let idx = e.parentNode.lastElementChild.value

    if (confirm("해당 댓글을 삭제하시겠습니까?")) {
        deleteReplyFetch(bNo.value, idx, convertURL(url))
    }
}

//댓글 삭제 fetch
async function deleteReplyFetch(bNo, idx, url) {
    let targetUrl = url;
    let formData = new FormData()
    formData.append("idx", idx);
    formData.append("bNo", bNo);

    const option = {
        method: "POST",
        body: formData
    }
    try {
        let response = await fetch(targetUrl, option)
        let result = await response.json()

        updateReply(replies, result)
    } catch (err) {
        console.log(err)
    }
}

// 댓글 수정 중 수정버튼 누를 때
function checkModify(e) {
    let url = '/community/modifyReply'
    let editReply = document.getElementsByName('editReply')[0]
    let idx = e.parentNode.lastElementChild.value
    if (confirm('댓글을 수정하시겠습니까?')) {
        if (!editReply.value) {
            alert("댓글의 내용을 입력해주세요")
        } else {
            modifyReply(bNo.value, idx, editReply.value, convertURL(url));
        }
    }
}

// 댓글 수정 중 수정버튼 누를 때 fetch
async function modifyReply(bNo, idx, editReply, url) {

    let targetUrl = url;
    let formData = new FormData()
    formData.append("idx", idx);
    formData.append("bNo", bNo);
    formData.append("editReply", editReply);

    const option = {
        method: "POST",
        body: formData
    }
    try {
        let response = await fetch(targetUrl, option)
        let result = await response.json()
        updateReply(replies, result)

    } catch (err) {
        console.log(err)
    }
}

//댓글 추가
async function addReply(message, writer, bNo, url) {
    let targetUrl = url;
    let formData = new FormData();
    formData.append("message", message);
    formData.append("writer", writer);
    formData.append("bNo", bNo);

    const options = {
        method: "POST",
        body: formData,
    };

    try {
        let response = await fetch(targetUrl, options);
        let result = await response.json()

        updateReply(replies, result)
    } catch (err) {
        console.log(err);
    }
}

//fetch 함수에 사용, 댓글 새로고침
function updateReply(node, result) {
    let str = "";
    for (let i = 0; i < result.length; i++) {
        str += "<div class='reply'>"
        str += "<div class='nickName'>" + result[i].writer + "</div>"
        str += "<div class='댓글내용' style='white-space: pre-wrap;'>" + result[i].message + "</div>"
        str += "<div class='comDate'>" + result[i].regDate + "</div>"
        if (wrt.value === result[i].writer) {
            str += "<input class='btnn mr-1' type='button' value='삭제' onclick='deleteReply(this)'>"
            str += "<input class='btnn' type='button' value='수정' onclick='modify(this)'>"
        }
        str += "<hr><input type='hidden' name='idx' value='" + result[i].idx + "'>"
        str += "</div>"
    }
    node.innerHTML = str;
}

let reserveBtn = document.querySelector('#reserveBtn')

function reserveBtnOn(e) {
    let fieldName = e.target.getAttribute('fName')
    let url = "/field/click?fName="+fieldName;
    location = convertURL(url);
}

if(reserveBtn)reserveBtn.addEventListener('click', reserveBtnOn)

