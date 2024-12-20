async function getTeamInfo() {
    let teamModal = document.getElementById("teamModal")
    let teamName = teamModal.innerText;
    let targetUrl = null;
    if (contextPath != null) {
        targetUrl = contextPath + "/team/teamInfo?tName=" + teamName;
    } else {
        targetUrl = "/team/teamInfo?tName=" + teamName;
    }
    let option = {
        method: "POST"
    }
    let res = await fetch(targetUrl, option);
    let team = await res.json();
    let div1 =
        "<div class='teamInfo_modal' style='display:none' id='teamInfo'>" +
        "<div class='teamInfo_modal_content'>" +
        "<div class='row'>" +
        "<div class='col-5'>" +
        "<img src ='" + team.logoPath + "' class='profile'/>" +
        "<h1>주장</h1>" +
        "<span>"+ team._id +"</span>" +
        "<h1>활동지역</h1>" +
        "<span>"+ team.tarea +"</span>" +
        "<h1>연령대</h1>" +
        "<span>"+ team.tage +"</span>" +
        "<h1>총 판 수</h1>" +
        "<span>"+ team.ttotal +"</span>" +
        "<h1>창단일</h1>" +
        "<span>"+ team.foundingdate +"</span>" +
        "<h1>유니폼 색</h1>" +
        "<span>"+ team.uniform +"</span>" +
        "<button id='closeBtn' class='closeBtn'>돌아가기</button>" +
        "</div>" +
        "</div>" +
        "</div>" +
        "</div>"
    document.getElementById("ddd").innerHTML += div1
    $("#teamInfo").fadeIn()
    $("#closeBtn").on('click', function (){
        $("#teamInfo").fadeOut();
    })
}


// let fservicediv = document.getElementById('fieldservice')
// let mapdiv = document.getElementById('map_n')
// function fmapopen(e){
//     if(fservicediv.style.display == 'block'){
//         fservicediv.style.display = 'none';
//         mapdiv.style.display = 'block';
//         resizeMap();
//         relayout();
//     }
// }
//
// function fmapclose(e){
//     if(fservicediv.style.display == 'none'){
//         fservicediv.style.display = 'block';
//         mapdiv.style.display = 'none';
//     }
// }
// function resizeMap() {
//     mapdiv.style.width = '75%';
//     mapdiv.style.height = '750px';
// }
