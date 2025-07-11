function goToPageInput() {
    const input = document.getElementById('pageInput');
    const page = parseInt(input.value, 10);
    const max = parseInt(input.getAttribute('max'), 10);

    if (!page || page < 1 || page > max) {
        alert("1부터 " + max + " 사이의 페이지를 입력해주세요.");
        return false;
    }

    const params = new URLSearchParams(window.location.search);
    params.set('page', page - 1); // Spring pageable은 0부터 시작
    window.location.href = '/products/search?' + params.toString();
    return false;
}