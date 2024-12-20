document.getElementById("searchTeambtn").addEventListener('click', getTeams)

async function getTeams() {
    let search_tName_tArea = document.getElementById("team_search").value;
    let context_root = "/team/search?searchTeam="
    if(contextPath != null){
        context_root = contextPath + context_root;
    }
    const targetUrl = context_root + search_tName_tArea;
    const options = {
        method: "post"
    }
    try {
        let response = await fetch(targetUrl, options);
        let result = await response.json();
        let area = document.getElementById('searchteams')
        while(area.hasChildNodes()){
            area.removeChild(area.firstChild);
        }
        let div;
        if(result.length == 0){
            alert("검색된 팀이 존재하지 않습니다.")
        }
        for(let i=0;i<result.length;i++){
            div =
                '<tr>' +
                '<td class="border px-4 py-2">' +
                '<img src="' + result[i].logoPath + '" style="height: 30px;"/>' +
                '</td>' +
                '<td class="border px-4 py-2">' +
                '<a href="/team/teamInfo?tName=' + result[i].tname + '">' +
                '<span>' + result[i].tname +
                '</span>' +
                '</a>' +
                '</td>' +
                '<td class="border px-4 py-2">' +
                '<span class="ttotals">' + result[i].ttotal + '</span>' +
                '</td>' +
                '<td class="border px-4 py-2">' +
                '<span>' + result[i].tarea + '</span>' +
                '</td>' +
                '<td class="border px-4 py-2">' +
                '<span>' + result[i].tage + '</span>' +
                '</td>' +
                '<td class="border px-4 py-2">' +
                '<div style="display: inline-block;">' +
                '<a href="/team/teamInfo?tName=' + result[i].tname + '" title="Button fade blue/green" class="butn btnFade btnBlueGreen">' +
                "상세보기" +
                '</a>' +
                '</div>' +
                '</td>'
            area.innerHTML += div
        }

    }catch(err){
        alert(err);
    }
};





