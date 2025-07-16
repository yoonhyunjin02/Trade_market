document.addEventListener('DOMContentLoaded', () => {
  const container = document.getElementById('product-container');
  const sentinel  = document.getElementById('scroll-sentinel');

  let page     = 1;      // 첫 페이지(0)는 이미 서버 사이드 렌더링 됨
  const size   = 16;     // 한 번에 16개
  let loading  = false;  // 중복 호출 방지
  let ended    = false;  // 더 이상 데이터 없을 때

  const params = new URLSearchParams(window.location.search);
  const currentSort = params.get('sort') || 'views';

  const loadMore = async () => {
    if (loading || ended) return;
    loading = true;

    try {
      const res = await fetch(`/products/scroll?page=${page}&size=${size}`, {
        headers: { 'X-Requested-With': 'fetch' },  // AJAX 요청임을 명시
        credentials: 'same-origin'
      });

      // AJAX 인증 실패 시 401 → alert 띄우고 중단
      if (res.status === 401) {
        alert('로그인이 필요한 요청입니다.');
        observer.unobserve(sentinel);
        return;
      }

      // 정상 응답 아닌 경우 (500, 404 등)
      if (!res.ok) {
        console.error(`서버 응답 오류: ${res.status} ${res.statusText}`);
        observer.unobserve(sentinel);
        return;
      }

      // 혹시 리다이렉트되면 중단
      if (res.redirected) {
        console.warn('리다이렉트 감지됨 → 무한스크롤 중단');
        observer.unobserve(sentinel);
        return;
      }

      const html = await res.text();

      // 메인페이지 HTML이 내려왔으면 중단
      if (html.includes('<main') && !html.includes('product-card')) {
        console.warn('⚠ 예상치 못한 HTML(main?)이 내려옴, 중단');
        observer.unobserve(sentinel);
        return;
      }

      // 빈 fragment면 종료
      if (!html.trim()) {
        ended = true;
        observer.unobserve(sentinel);
        return;
      }

      // 받은 조각 삽입
      const temp = document.createElement('div');
      temp.innerHTML = html;
      while (temp.firstChild) {
        container.appendChild(temp.firstChild);
      }

      page += 1; // 다음 페이지 준비

    } catch (err) {
      console.error('loadMore error', err);
      observer.unobserve(sentinel); // 에러 시 중단
    } finally {
      loading = false;
    }
  };

  // 스크롤 하단 감지
  const observer = new IntersectionObserver(entries => {
    if (entries[0].isIntersecting) {
      loadMore();
    }
  }, {
    root: null,         // 뷰포트 기준
    rootMargin: '200px' // 미리 로드(옵션)
  });

  observer.observe(sentinel);
});
