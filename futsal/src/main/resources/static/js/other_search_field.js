const contextPath = document.getElementById('contextPathHolder').getAttribute('data-contextPath');


let searchFieldButton = document.querySelector("#searchFieldButton")
searchFieldButton.addEventListener('click', function () {
    let searchField = document.getElementById("searchField")
    console.log("searchField2 : " + searchField.value);
    let form = document.createElement('form');
    form.setAttribute('method', 'post');
    form.setAttribute('action', convertURL('/main'))
    let searchField1 = document.createElement('input');
    searchField1.setAttribute('type', 'hidden');
    searchField1.setAttribute('name', 'searchField');
    searchField1.setAttribute('value', searchField.value);
    form.appendChild(searchField1);
    document.body.appendChild(form);
    form.submit();
})
let mynickname = document.getElementById("memberNickName");
if (mynickname) {
    mynickname = mynickname.value
}