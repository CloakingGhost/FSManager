let modal1 = document.querySelector("#mannerandresultList");
let matchresult;
let rd4;
function doDisplay1() {
    if (getComputedStyle(modal1).display == 'none') {
        modal1.style.cssText = 'display:block;';
    }else{
        modal1.style.cssText = 'display:none;';
    }
}
function openmannerandresult(e){

    let tr = e.parentElement.parentElement.parentElement.parentElement.previousElementSibling.firstElementChild.querySelector('.reservetd');
    let tr1 = e.parentElement.parentElement.parentElement.parentElement.previousElementSibling;

    let rd1 = tr.querySelector('.item1').innerHTML.trim(); // 날짜
    let rd2 = tr.querySelector('.item2').innerHTML.trim(); // 시간
    // 태그를 제외하고 모든 텍스트를 가져옴, display:none 무시함
    let rd3 = tr.querySelector('.fName').innerHTML.trim(); // 구장명
    rd4 = tr1.querySelector('.item7').innerText.trim(); // 타입
    console.log("rd4 : " + rd4)
    // 3개의 변수값을 가진 어웨이로 테이블을 만들자
    makeMannerAndResultForm(rd1, rd2, rd3, rd4);
}
async function makeMannerAndResultForm(rd1, rd2, rd3, rd4){
    let formData = new FormData();
    formData.append("date", rd1);
    formData.append("rtime", rd2);
    formData.append("fieldname", rd3);
    formData.append("reserveType", rd4);
    let modalBody = document.getElementsByClassName("modal-body1")[0];
    const options = {
        method: "post",
        body: formData,
    };
    console.log("타입 : " + rd4)
    let url = convertURL("/reserveTo/makeMannerAndResultForm")
    try{
        let res = await fetch(url,options);
        let result = await res.json();
        console.log("매너랑경기결과입력폼만드는 패치 성공 후 결과 홈팀 / 어웨이팀 / 구장 / 날짜 / 시간 : " + result.nameA + " / " + result.nameB + " / " +
            result.field + " / " + result.date + " / " + result.time)
        let form;

        if(rd4 === "홈"){
            form = '<input type="hidden" id="mfield" value="' + result.field + '">' +
                '<input type="hidden" id="mdate" value="' + result.date + '">' +
                '<input type="hidden" id="mtime" value="' + result.time + '">' +
                '<input type="hidden" id="mmyteam" value="' + result.nameA + '">' +
                '<input type="hidden" id="motherteam" value="' + result.nameB + '">' +
                '<p id="checks" class="checks"></p>' +
                '<h4 style="text-align: center;">' + result.nameB + '팀과의 경기 결과를 입력해 주세요</h4>' +
                '<div style="margin-left: 130px;">' +
                '  <input id="bbttnn1" class="bbttnn" type="button" value="승" />' +
                '  <input id="bbttnn2" class="bbttnn" type="button" value="무" />' +
                '  <input id="bbttnn3" class="bbttnn" type="button" value="패" />' +
                '</div>' + '<br>' +
                '<h4 style="text-align: center;">' + result.nameB + '팀의 매너점수를 평가해 주세요.</h4>' +
                '<div class="star-rating space-x-4 mx-auto">' +
                '  <input type="radio" id="5-stars" name="rating" value="5" v-model="ratings"/>' +
                '  <label for="5-stars" class="star pr-4">★</label>' +
                '  <input type="radio" id="4-stars" name="rating" value="4" v-model="ratings"/>' +
                '  <label for="4-stars" class="star">★</label>' +
                '  <input type="radio" id="3-stars" name="rating" value="3" v-model="ratings"/>' +
                '  <label for="3-stars" class="star">★</label>' +
                '  <input type="radio" id="2-stars" name="rating" value="2" v-model="ratings"/>' +
                '  <label for="2-stars" class="star">★</label>' +
                '  <input type="radio" id="1-star" name="rating" value="1" v-model="ratings"/>' +
                '  <label for="1-star" class="star">★</label>' +
                '</div>' +
                '<div>' +
                '  <button type="button" class="bbttnn" style="margin-left: 220px" onclick="submitmannerandresult(this)">' + '입력 완료' + '  </button>' +
                '</div>'
        }else if(rd4 === "어웨이"){
            form = '<input type="hidden" id="mfield" value="' + result.field + '">' +
                '<input type="hidden" id="mdate" value="' + result.date + '">' +
                '<input type="hidden" id="mtime" value="' + result.time + '">' +
                '<input type="hidden" id="mmyteam" value="' + result.nameA + '">' +
                '<input type="hidden" id="motherteam" value="' + result.nameB + '">' +
                '<p id="checks" class="checks"></p>' +
                '<h4 style="text-align: center;">' + result.nameB + '팀의 매너점수를 평가해 주세요.</h4>' +
                '<div class="star-rating space-x-4 mx-auto">' +
                '  <input type="radio" id="5-stars" name="rating" value="5" v-model="ratings"/>' +
                '  <label for="5-stars" class="star pr-4">★</label>' +
                '  <input type="radio" id="4-stars" name="rating" value="4" v-model="ratings"/>' +
                '  <label for="4-stars" class="star">★</label>' +
                '  <input type="radio" id="3-stars" name="rating" value="3" v-model="ratings"/>' +
                '  <label for="3-stars" class="star">★</label>' +
                '  <input type="radio" id="2-stars" name="rating" value="2" v-model="ratings"/>' +
                '  <label for="2-stars" class="star">★</label>' +
                '  <input type="radio" id="1-star" name="rating" value="1" v-model="ratings"/>' +
                '  <label for="1-star" class="star">★</label>' +
                '</div>' +
                '<div>' +
                '  <button type="button" class="bbttnn" style="margin-left: 220px" onclick="submitmannerandresult(this)">' + '입력 완료' + '  </button>' +
                '</div>'
        }
        modalBody.innerHTML = form;

        if(rd4 === "홈"){
            document.getElementById("bbttnn1").addEventListener('click',function (){
                this.style.backgroundColor ="rgb(186, 224, 250)";
                document.getElementById("bbttnn2").style.backgroundColor ="#fff";
                document.getElementById("bbttnn3").style.backgroundColor ="#fff";
                matchresult = document.getElementById("bbttnn1").value
            })


            document.getElementById("bbttnn2").addEventListener('click',function (){
                this.style.backgroundColor ="rgb(186, 224, 250)";
                document.getElementById("bbttnn1").style.backgroundColor ="#fff";
                document.getElementById("bbttnn3").style.backgroundColor ="#fff";
                matchresult = document.getElementById("bbttnn2").value
            });

            document.getElementById("bbttnn3").addEventListener('click',function (){
                this.style.backgroundColor ="rgb(186, 224, 250)";
                document.getElementById("bbttnn1").style.backgroundColor ="#fff";
                document.getElementById("bbttnn2").style.backgroundColor ="#fff";
                matchresult = document.getElementById("bbttnn3").value
            })
        }
        doDisplay1()

    }catch (err){
        console.log("매너랑경기결과입력폼만드는 패치 오류 : " + err)
    }
}


async function submitmannerandresult(e){

    let field = document.querySelector('#mfield').value
    let date = document.querySelector('#mdate').value
    let time = document.querySelector('#mtime').value
    let myteam = document.querySelector('#mmyteam').value
    let otherteam = document.querySelector('#motherteam').value
    let a = document.querySelectorAll("input[name='rating']")
    let mannerscore;
    let b;
    for (let i = 0; i < a.length; i++) {
        if(a[i].checked == true){
            b = a[i].value
        }
    }
    mannerscore = parseInt(b)
    console.log(" 매너점수 : " + mannerscore) //v
    console.log("승패결과 : " + matchresult)//v
    if(mannerscore == undefined){
        $('#checks').text('매너점수를 입력해주세요');
        $('#checks').css('color', 'red');
    }
    if(rd4 === "홈") {
        if (matchresult == undefined) {
            $('#checks').text('경기 결과를 입력해주세요');
            $('#checks').css('color', 'red');
        }
    }


    let url = convertURL('/reserveTo/submitmannerandresult')
    let formData = new FormData();
    formData.append("field", field);
    formData.append("date", date);
    formData.append("time", time);
    formData.append("myteam", myteam);
    formData.append("otherteam", otherteam);
    formData.append("mannerscore", mannerscore);
    formData.append("matchresult", matchresult);


    const options = {
        method: "post",
        body: formData,
    };

    try{
        await fetch(url, options)
        console.log("submitmannerandresult 패치 성공 ")
        window.location.href = convertURL("/reservation")

    }catch (err){
        console.log("submitmannerandresult 패치 오류 : " + err)
    }



}