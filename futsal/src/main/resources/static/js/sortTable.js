function convertURL(url) {
    return contextPath ? contextPath + url : url;
}

function sortTable(n) {
    let table, rows, switching, o, x, y, shouldSwitch, dir, switchcount = 0;
    table = document.getElementById("teamtable");
    switching = true;
    dir = "asc";

    while (switching) {
        switching = false;
        rows = table.getElementsByTagName("TR");
        for (o = 1; o < (rows.length - 1); o++) {
            shouldSwitch = false;
            x = rows[o].getElementsByTagName("TD")[n];
            y = rows[o + 1].getElementsByTagName("TD")[n];
            console.log("x : " + x);

            if (dir == "asc") {
                if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
                    shouldSwitch = true;
                    break;
                }
            } else if (dir == "desc") {
                if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
                    shouldSwitch = true;
                    break;
                }
            }
        }
        if (shouldSwitch) {
            rows[o].parentNode.insertBefore(rows[o + 1], rows[o]);
            switching = true;
            switchcount++;
        } else {
            if (switchcount == 0 && dir == "asc") {
                dir = "desc";
                switching = true;
            }
        }
    }
}

async function sortTableTotal() {
    let area = document.getElementById('searchteams')
    let total1 = document.getElementsByClassName('ttotals')[0].innerText
    let ttt = document.getElementsByClassName('ttotals')
    let total2 = ttt[ttt.length - 1].innerText


    let url = convertURL("/team/sortTotal?total1=" + total1 + "&total2=" + total2)
    let option = {method: "post"}
    try {
        let res = await fetch(url, option)
        let result = await res.json();
        while (area.hasChildNodes()) {
            area.removeChild(area.firstChild)
        }

        let div;
        for (let i = 0; i < result.length; i++) {
            div =
                '<tr>' +
                '<td class="border px-4 py-2">' +
                '<img src="' + result[i].logoPath + '" style="height: 30px;"/>' +
                '</td>' +
                '<td class="border px-4 py-2">' +
                '<a href=' + convertURL(`/team/teamInfo?tName=${result[i].tname}`) + '>' +
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
                '<a href='+convertURL(`/team/teamInfo?tName=${result[i].tname}`)+ "title='Button fade blue/green' class='butn btnFade btnBlueGreen'>" +
                "상세보기" +
                '</a>' +
                '</div>' +
                '</td>'
            area.innerHTML += div
        }
    } catch (err) {
        console.log("판수대로 정렬하는 중 오류 : " + err)
    }
}