window.onload = function (){
    let regex = new RegExp("(.*?)\.(exe|sh|zip|alz)$");
    let maxSize = 5242880;
    let area = document.getElementById("logoImage")
    let area2 = document.getElementById("introduceteam")
    let textarea = document.getElementById('introduce')

    // 모든 조건 통과
    function checkExtension(fileName, fileSize){
        if(fileSize >= maxSize){
            alert("파일 사이즈가 초과하였습니다.(업로드 제한 5MB)");
            return false;
        }
        // 정규식에 맞지 않을 경우
        if(regex.test(fileName)){
            alert("이미지 종류 파일만 업로드 할 수 있습니다.");
            return false;
        }
        return true;
    }
    document.getElementById("uploadBtn").addEventListener('click', saveTeamInfo)

    async function saveTeamInfo(){
        let introduce = document.getElementById("introduce").value
        let cloneObj = document.getElementsByClassName('profile')
        let url = "/team/saveAsync"
        if (contextPath != null){
            url = contextPath + url;
        }
        let formData = new FormData();
        let inputFile = document.getElementsByName("logo");
        console.log(inputFile[0].files[0])

        if(introduce == "" || introduce == undefined || introduce == "undefined"){
            introduce = document.getElementById('realintroduce').innerText
        }
        let files = inputFile[0].files;
        formData.append("uploadFile", files[0])
        formData.append("introduce", introduce)

        const options = {
            method: "post",
            body: formData
        }

        if(files[0] != null){
            // 정규식 체크
            for (let i = 0; i < files.length; i++) {
                if (!checkExtension(files[i].name, files[i].size)) {
                    alert('이미지 파일만 선택할 수 있습니다.');
                    return false;
                } else {
                    try {
                        let response = await fetch(url, options)
                        let team = await response.json();
                        console.log("팀정보수정결과의 엠뷸럼 : " + team.logoPath)
                        console.log("팀정보수정결과의 팀소개 : " + team.teamInfo)
                        while (area.hasChildNodes()) {
                            area.removeChild(area.firstChild);
                        }
                        while (area2.hasChildNodes()) {
                            area2.removeChild(area2.firstChild);
                        }
                        let img1 = document.createElement('img')
                        img1.src = team.logoPath
                        img1.className = "profile"
                        area.append(img1)

                        let span = '<p style="color: #5bc4f4; white-space: pre-wrap;" class="card-text">' + team.teamInfo + '</p>'
                        area2.innerHTML += span
                    } catch (err) {
                        alert(err)
                    }
                }
            }

        }
        else {
            alert("로고 파일을 선택해주세요.");
        }

    }
}