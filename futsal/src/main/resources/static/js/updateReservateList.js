function convertURL(url) {
    return contextPath ? contextPath + url : url;
}

function sendData() {
    let checkedRlList = document.querySelector("input[name='rlList']:checked");
    let data = checkedRlList.parentNode.parentNode.innerText;
    checkedRlList.setAttribute('value', data.trim());
    let datas = data.replace(/\t/gi, ",");
    let arrDatas = datas.split(",");
    acceptAway(arrDatas);
    document.forms['selectAway'].submit();
}

let modal = document.querySelector("#reservationList");

function doDisplay() {
    getComputedStyle(modal).display === 'none'
        ? modal.style.cssText = 'display:block;'
        : modal.style.cssText = 'display:none'
}

function getList(e) {
    let tr = e.closest('tr').previousElementSibling
    console.log("selectTeam : ") // tr
    console.log(tr) // tr
    try {
        let rd1 = tr.querySelector('.item1').innerHTML.trim(); // 날짜
        let rd2 = tr.querySelector('.item2').innerHTML.trim(); // 시간
        // 태그를 제외하고 모든 텍스트를 가져옴, display:none 무시함
        let rd3 = tr.querySelector('.fName').innerHTML.trim(); // 구장명
        // 3개의 변수값을 가진 어웨이로 테이블을 만들자
        makeAwayList(rd1, rd2, rd3);

    } catch (err) {
        console.log("error : ", err);
    }
}

let modalBody = document.getElementsByClassName("modal-body")[0];

async function makeAwayList(rd1, rd2, rd3) {
    console.log(rd1)
    console.log(rd2)
    console.log(rd3)
    let formData = new FormData();
    let str = "";
    formData.append("date", rd1);
    formData.append("time", rd2);
    formData.append("field", rd3);
    const options = {
        method: "post",
        body: formData,
    };
    try {
        let targetUrl = convertURL("/awayList");
        let response = await fetch(targetUrl, options);
        let rlList = await response.json();
        console.dir(rlList);
        let formUrl = convertURL('/reserveTo/updateDeleted')
        str += `<form action='${formUrl}' method='post' name='selectAway'>
        <table class='table table-striped table-hover text-center'><thead>
        <th>사용날짜</th><th>시간</th><th>구장명</th><th>팀 이름</th>
        <th>유니폼</th><th>연령대</th><th>총 판수</th><th>매너점수</th><th>선택</th>
        </thead><tbody>`;
        for (let i = rlList.length; i--;) {
            str += `<tr class='ritem${i + 1}'>
            <td>${rlList[i].date}</td><td>${rlList[i].time}</td><td>${rlList[i].field}</td>
            <td><img src='${rlList[i].logoPath}' width="75" height='20'> ${rlList[i].team}</td><td>${rlList[i].uniform}</td>
            <td>${rlList[i].tage}</td><td>${rlList[i].ttotal}</td><td>${rlList[i].tmanner}</td>
            <td><input type='radio' id='item${i + 1}' name='rlList'></td></tr>`
        }
        str += `</tbody></table>
        <button type='button' class='btn btn-warning float-end' onclick='sendData();'>선택완료</button></form>`
        doDisplay();
        modalBody.innerHTML = str;
    } catch (e) {
        console.log(e);
    }
}
let pageCount = 1;
window.addEventListener("scroll", function () {
    const SCROLLED_HEIGHT = window.scrollY; // TOP에서부터의 스크롤의 거리
    const WINDOW_HEIGHT = window.innerHeight; // 보이는 화면의 길이
    const DOC_TOTAL_HEIGHT = document.documentElement.scrollHeight; // 화면의 총 길이
    let IS_BOTTOM = WINDOW_HEIGHT + SCROLLED_HEIGHT >= DOC_TOTAL_HEIGHT + 17;
    /*    console.log(SCROLLED_HEIGHT)
        console.log(WINDOW_HEIGHT)
        console.log(DOC_TOTAL_HEIGHT + 17)
        console.log(WINDOW_HEIGHT + SCROLLED_HEIGHT)*/
    if (pageCount && IS_BOTTOM) {
        addContent("/reservation").then(function () {
            console.log(pageCount++);
            // pageCount++;;
        })
    }
});
// localhost:8080/community/fetchSearchBoard?word=크&searchResult=팀이름&num=0&page=0&size=10&sort=idx,desc
async function addContent(url) {
    let targetUrl = url// + '?&size=10&page=' + pageCount;

    console.log(targetUrl)
    const options = {
        method: "get"
    }
    try {
        // console.log(1)
        let result = await fetch(convertURL(targetUrl), options);
        // console.log(2)
        // console.log(result)
        let res = await result.json();
        // console.log(3)
        console.log("res")
        console.log(res)
    }catch (e) {
        console.log(e)
    }
}