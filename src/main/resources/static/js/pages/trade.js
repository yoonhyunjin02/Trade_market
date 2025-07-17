document.addEventListener('DOMContentLoaded', () => {
  // 무한 스크롤
  const container = document.getElementById('product-container');
  const sentinel  = document.getElementById('scroll-sentinel');

  let page     = 1;      // 첫 페이지(0)는 이미 서버 사이드 렌더링 됨
  const size   = 16;     // 한 번에 16개
  let loading  = false;  // 중복 호출 방지
  let ended    = false;  // 더 이상 데이터 없을 때

  const params = new URLSearchParams(window.location.search);
  const currentSort = params.get('sort') || 'views';
  const currentMin  = params.get('minPrice') || '';
  const currentMax  = params.get('maxPrice') || '';

  const loadMore = async () => {
    if (loading || ended) return;
    loading = true;

    try {
      const url =
        `/products/scroll?page=${page}&size=${size}` +
        `&sort=${currentSort}&minPrice=${currentMin}&maxPrice=${currentMax}`;

      const res = await fetch(url, {
        headers: { 'X-Requested-With': 'fetch' },
        credentials: 'same-origin'
      });

      if (res.status === 401) {
        alert('로그인이 필요한 요청입니다.');
        observer.unobserve(sentinel);
        return;
      }

      if (!res.ok) {
        console.error(`서버 응답 오류: ${res.status} ${res.statusText}`);
        observer.unobserve(sentinel);
        return;
      }

      if (res.redirected) {
        console.warn('리다이렉트 감지됨 → 무한스크롤 중단');
        observer.unobserve(sentinel);
        return;
      }

      const html = await res.text();

      if (!html.trim()) {
        ended = true;
        observer.unobserve(sentinel);
        return;
      }

      const temp = document.createElement('div');
      temp.innerHTML = html;
      while (temp.firstChild) {
        container.appendChild(temp.firstChild);
      }

      page += 1;

    } catch (err) {
      console.error('loadMore error', err);
      observer.unobserve(sentinel);
    } finally {
      loading = false;
    }
  };

  const observer = new IntersectionObserver(entries => {
    if (entries[0].isIntersecting) {
      loadMore();
    }
  }, {
    root: null,
    rootMargin: '200px'
  });

  observer.observe(sentinel);

  // 더보기 / 접기 버튼
  const toggleButtons = document.querySelectorAll(".toggle-btn");

    toggleButtons.forEach(button => {
      button.addEventListener("click", (e) => {
        e.preventDefault();  // ✅ form 전송(새로고침) 막기!

        const targetId = button.dataset.target;
        const container = document.getElementById(targetId);

        // 숨겨진 항목 찾기
        const hiddenItems = container.querySelectorAll(".hidden-item");

        // ✅ 현재 상태 확인
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
