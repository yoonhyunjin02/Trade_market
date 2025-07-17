document.addEventListener('DOMContentLoaded', () => {
    const container = document.getElementById('product-container');
    const sentinel  = document.getElementById('scroll-sentinel');

    let page     = 1;      // 첫 페이지(0)는 SSR로 렌더링됨
    const size   = 16;     // 한 번에 불러올 개수
    let loading  = false;  // 중복 호출 방지
    let ended    = false;  // 더 이상 데이터 없으면 true

    // 현재 URL의 모든 필터 파라미터 유지
    const baseParams = new URLSearchParams(window.location.search);

    async function loadMore() {
        if (loading || ended) return;
        loading = true;

        // 필터 파라미터 유지 + page/size 추가
        const params = new URLSearchParams(baseParams);
        params.set("page", page);
        params.set("size", size);

        try {
            const res = await fetch(`/products/scroll?${params.toString()}`);
            if (!res.ok) {
                console.error("서버 오류 :", res.status);
                observer.unobserve(sentinel);
                return;
            }

            const html = await res.text();
            if (!html.trim()) {
                // 더 이상 로드할 데이터 없음 → 중단
                ended = true;
                observer.unobserve(sentinel);
                return;
            }

            // 받아온 HTML을 상품 컨테이너에 추가
            const temp = document.createElement('div');
            temp.innerHTML = html;
            container.append(...temp.childNodes);

            page += 1;
        } catch (err) {
            console.error("loadMore error", err);
            observer.unobserve(sentinel);
        } finally {
            loading = false;
        }
    }

    // sentinel 감지되면 loadMore 실행
    const observer = new IntersectionObserver(entries => {
        if (entries[0].isIntersecting) loadMore();
    }, {
        root: null,
        rootMargin: '200px'
    });

    observer.observe(sentinel);

    // 더보기 / 접기 버튼
    const toggleButtons = document.querySelectorAll(".toggle-btn");

    toggleButtons.forEach(button => {
        button.addEventListener("click", (e) => {
            e.preventDefault();  // form 전송(새로고침) 막기!

            const targetId = button.dataset.target;
            const container = document.getElementById(targetId);

            // 숨겨진 항목 찾기
            const hiddenItems = container.querySelectorAll(".hidden-item");

            // 현재 상태 확인
            const firstItem = hiddenItems[0];
            const isHidden = firstItem && (window.getComputedStyle(firstItem).display === "none");

            hiddenItems.forEach(item => {
                item.style.display = isHidden ? "block" : "none";
            });

            // 버튼 텍스트 변경
            button.textContent = isHidden ? "접기" : "더보기";
        });
    });
});