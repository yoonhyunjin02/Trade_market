<div align="center">
  <h1>🥕 당근 마켓 클론 코딩 프로젝트</h1>
</div>


## 📍 목차

1. [프로젝트 소개](#1-프로젝트-소개)
2. [팀원 소개 및 역할](#2-팀원-소개-및-역할)
3. [ERD 구조](#3-erd)
4. [페이지 구성](#4-페이지-구성)
5. [페이지별 주요 기능](#5-페이지별-주요-기능)
6. [디렉토리 구조](#6-디렉토리-구조아직-다-안함)
7. [브랜치 전략 및 컨벤션](#7-브랜치-전략-및-컨벤션)
8. [사용 기술 및 도구](#8-사용-기술-및-도구)
9. [최종 회고](#9-최종-회고)


## 1. 프로젝트 소개

#### [오르미 11기 백엔드 양성과정 - 백엔드 프로젝트]

> 챗봇 상담사가 있는 중고 거래 당근 마켓 클론 페이지 구현

- 📅 진행 기간: 2025년 7월 3일 ~ 2025년 7월 23일(14일)


- 🎯 주요 기능
    - 회원가입/로그인 및 소셜 로그인
    - 상품 조회, 등록, 수정, 삭제
    - 상품 페이지 무한 스크롤, 페이지네이션
    - 상품 조회 필터(거래 상태, 카테고리, 가격, 위치)
    - 챗봇 서비스(Gemini 활용)
    - 실시간 채팅
    - Google Maps API로 위치 인증 기능
    

- 🦉 팀명: 올빼미 : 밤늦게까지 코딩 중..


- 📚[Notion](https://www.notion.so/2252233de69380d0bd52de0b615160ec?source=copy_link)


- 📬[배포](http://54.180.153.218:8080)


- 📢[발표 자료]()
---

## 2. 팀원 소개 및 역할
<table>
  <tr>
    <td align="center" width="150px">
      <a href="https://github.com/sungyeonkim27" target="_blank">
        <img src="https://avatars.githubusercontent.com/u/192389552?v=4" alt="김영롱 프로필" /></a>
    </td>
    <td align="center" width="150px">
      <a href="https://github.com/Jaykim98z" target="_blank">
        <img src="https://avatars.githubusercontent.com/u/99804318?v=4"
        alt="김진욱 프로필" /></a>
    </td>
    <td align="center" width="150px">
      <a href="https://github.com/SooowanLee" target="_blank">
        <img src="https://avatars.githubusercontent.com/u/87759519?v=4"
        alt="이수완 프로필" /></a>
    </td>
    <td align="center" width="150px">
      <a href="https://github.com/yoonhyunjin02" target="_blank">
        <img src="https://avatars.githubusercontent.com/u/97629676?v=4"
        alt="윤현진 프로필" /></a>
    </td>

  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/sungyeonkim27" target="_blank">김성연(팀장)</a>
    </td>
    <td align="center">
      <a href="https://github.com/Jaykim98z" target="_blank">김진욱</a>
    </td>
    <td align="center">
      <a href="https://github.com/SooowanLee" target="_blank">이수완</a>
    </td>
    <td align="center">
      <a href="https://github.com/yoonhyunjin02" target="_blank">윤현진</a>
    </td>
  </tr>
</table>

### 📆 페이지별 역할 분담 및 기간별 작업 요약
<table>
  <tr>
    <td align="center">
      김성연
    </td>
    <td align="center">
      김진욱
    </td>
    <td align="center">
      이수완
    </td>
    <td align="center">
      윤현진
    </td>
  </tr>
  <tr>
    <td align="center">
      소셜 로그인, 지도
    </td>
    <td align="center">
      상품 상세조회, 상품 등록, 프로필
    </td>
    <td align="center">
      로그인, 회원가입, 일반 채팅
    </td>
    <td align="center">
      메인, 상품 전체조회, 상품 검색, 챗봇 채팅
    </td>
  </tr>
</table>

<img width="10155" height="4768" alt="Image" src="https://github.com/user-attachments/assets/db1b211a-4659-47bc-9ac1-d60ddcec9c3c" />

## 3. ERD
<img width="1273" height="777" alt="Image" src="https://github.com/user-attachments/assets/b98eb097-c294-42ae-925d-fb44557854eb" />

## 4. 페이지 구성

- 로그인(login)
  <br>
  <img width="930" height="1117" alt="Image" src="https://github.com/user-attachments/assets/3738c9df-89e3-4995-9e22-80c4a6a591ce" />
  - 로그인 시 헤더
    <img width="923" height="160" alt="Image" src="https://github.com/user-attachments/assets/05c7808a-627f-4eea-b288-2d3c3c67a017" />


- 회원가입(register)
  <br>
  <img width="930" height="1157" alt="Image" src="https://github.com/user-attachments/assets/f030e5ee-e302-427a-a59f-87d14838cd2a" />


- 메인(main)
  <br>
  <img width="930" height="3733" alt="Image" src="https://github.com/user-attachments/assets/a6559397-aee4-46ad-a0ca-1ade84ee18d1" />


- 상품(trade)
  <br>
  <img width="1920" height="1032" alt="Image" src="https://github.com/user-attachments/assets/a913fd18-22d4-4c3a-a9f8-54004ee63371" />


- 상품 상세(trad-post)
  <br>
  <img width="1892" height="1544" alt="Image" src="https://github.com/user-attachments/assets/4fa3e93a-cdc1-43da-becb-db9e6ca3e8da" />


- 상품 등록(wirte)
  <br>
  <img width="1892" height="1648" alt="Image" src="https://github.com/user-attachments/assets/638a57bc-7d4c-46bc-8ead-00a22853f40c" />


- 검색(search)
  <br>
  <img width="1892" height="1149" alt="Image" src="https://github.com/user-attachments/assets/f1802daf-2d92-4434-9e7f-1b58a62f7ff3" />

- 채팅
  <br>
  <img width="1892" height="1069" alt="Image" src="https://github.com/user-attachments/assets/01d475ce-9f5c-462c-98a8-7d543ee01b93" />


- 일반 채팅(chat)
  <br>
  <img width="1892" height="1069" alt="Image" src="https://github.com/user-attachments/assets/e2602e0b-7e21-4be6-843f-10edcfe5a4af" />


- 챗봇 채팅(chatBot)
  <br>
  <img width="1892" height="1069" alt="Image" src="https://github.com/user-attachments/assets/60b8db9a-f84c-4a2f-a7b6-7854a2dac3cf" />


- 위치(location)
  <br>
  <img width="1892" height="1049" alt="Image" src="https://github.com/user-attachments/assets/23fc0496-14dd-40f2-b304-c14fcfdfab6f" />


- 프로필(mypage)
  <br>
  <img width="1892" height="1471" alt="Image" src="https://github.com/user-attachments/assets/9741bb86-e0f2-4b3d-8c03-354e99bbd9f7" />


## 5. 페이지별 주요 상세 기능
<details>
<summary>header</summary>

- 비로그인시<br>
<img width="941" height="69" alt="Image" src="https://github.com/user-attachments/assets/2174d309-f74e-497a-b512-0ece92276ff1" />
  - 중고거래 페이지만 접속할 수 있으므로, 해당 페이지 이동 버튼만 남김
  - 게시물의 제목과 동네로 검색할 수 있는 검색창
  

- 로그인시<br>
<img width="937" height="237" alt="Image" src="https://github.com/user-attachments/assets/e06f52aa-4486-4720-990b-e1cf53b8610a" />
  - 거래 글쓰기할 때 필요한 동네인증 페이지로 바로 갈 수 있는 토글 버튼 생김
  - 닉네임과 함께 채팅 페이지, 프로필 페이지로 이동 가능
  - 로그아웃 버튼을 누르면 메인 페이지로 이동
  
</details>

<details>
<summary>login</summary>


<img width="533" height="573" alt="Image" src="https://github.com/user-attachments/assets/e34c66a0-c184-4b81-bbcf-00f0fd655959" />

- 로컬 로그인
- 소셜 OAuth 구글 로그인 구현
- 로그인 성공 시 main 페이지로 이동
- 회원가입 페이지로 바로 이동 가능
- 로그인이 필요한 페이지에서 로그인 페이지로 이동했을 경우,
  로그인 후 마지막에 있었던 페이지로 다시 이동

- 로그인 실패시 예외처리<br>
<img width="526" height="606" alt="Image" src="https://github.com/user-attachments/assets/8972d206-da8e-48db-9d9d-c8845a84e6f5" />
  - 아이디나 비밀번호가 틀렸을 때 "사용자명 또는 비밀번호가 올바르지 않습니다." 문구 출력
</details>

<details>
<summary>register</summary>

- 회원가입 실패시 예외처리<br>
<img width="612" height="699" alt="Image" src="https://github.com/user-attachments/assets/fe49e231-61f7-4d85-b95e-fac2d880ce8b" />
- 로그인 페이지로 바로 이동 가능
- 
- 
</details>

<details>
<summary>main</summary>
  <br>
  <img width="908" height="573" alt="Image" src="https://github.com/user-attachments/assets/76b78e00-dca2-4cda-ae9d-a9ee1caa9771" />
- 2번째 세션 인기매물 보기 버튼 -> 상품 페이지로 이동
<img width="622" height="635" alt="Image" src="https://github.com/user-attachments/assets/edb32c99-6eb7-41bb-8d49-87482949bf3e" />
- 4번째 세션 중고거래 인기 매물 -> 조회순 정렬
  인기매물 더보기 버튼 -> 상품 페이지로 이동
</details>

<details>
<summary>trade</summary>
- 무한 스크롤 기능 구현
- 필터바
  - 거래 가능한 물건만 모아보기
  - 게시물 정보(조회수, 채팅수, 최신순) 정렬
  - 가격순(낮은 순, 높은 순, 지정한 범위 순)
  - 카테고리 모아보기
  - 위치 모아보기

</details>

<details>
<summary>trade-post</summary>
- s3에 사진 저장
- 채팅하기 버튼(자신이 올린 게시물일 경우 버튼 안 보임)
희망 거래 장소 지도로 띄워줌
- 자신이 작성한 게시물일 경우 수정하기 / 삭제하기 버튼 보임
</details>

<details>
<summary>trade-write</summary>
- 동네 인증을 필수적으로 해야 작성 가능
- 사진 미리보기
- 필수 값 안 적었을 때 예외처리
- 거래 희망 장소 지도 API 기능으로 지도에 찍으면 위치가 자동으로 적힘
</details>

<details>
<summary>search</summary>
- 페이지네이션 구현
- 밑에 숫자 input으로 원하는 페이지로 바로 이동 가능
</details>

<details>
<summary>chat</summary>
- 채팅이 없을 경우 상품 둘러보기 버튼으로 상품 페이지로 이동
- 웹소켓으로 실시간 통신 가능
- 구매자일 경우 헤더에 "채팅방 나가기"버튼과 토글 활성화
- 판매자일 경우 "거래하기" 버튼 보임 -> 거래완료됨 -> 거래 완료 되면 온도 5도 상승
</details>

<details>
<summary>chatBot</summary>
상단에 FAQ 4개 버튼으로 구성
대화를 끝내고 싶을 때 대화 종료 및 초기화 버튼이 있음
당근 마켓과 관련된 내용을 챗봇에게 질문하면 프롬프트 체이닝 기술로
사용자의 질문이 먼저 카테고리를 분류하는 챗에게 가서 특화된 챗으로 보내줌, 특화된 챗을 찾지 못했을 경우에는 모든 내용이 전부 적혀있는 챗으로 보내줌
</details>

<details>
<summary>location</summary>
- 지도 API 사용
- 검색창에 위치 적으면 자동 완성
- 내가 선택한 위치와 실제 위치가 동일한지 판단 후 동일하면 동네 등록 완료
</details>

<details>
<summary>mypage</summary>
- 닉네임, 아이디, 위치, 온도를 볼 수 있음
- 소개와 나이, 성별을 등록할 수 있음
- 내가 판매중인 상품을 모아볼 수 있음
- 판매중인 상품이 없을 경우 등록하기 버튼으로 게시물 글쓰기로 바로 이동 가능
</details>

## 6. 디렉토리 구조
```
TradeMarket/
├── src/
│   └── main/
│       ├── java/com/owl/trade_market/
│       │   ├── config/
│       │   │   ├── auth/
│       │   │   ├── exception/
│       │   │   └── handler/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── entity/
│       │   ├── repository/
│       │   ├── security/
│       │   ├── service/
│       │   │   └── impl/
│       │   └── util/
│       └── resources/
│           ├── faq/
│           ├── prompts/
│           ├── static/
│           │   ├── css/
│           │   │   ├── common/
│           │   │   └── pages/
│           │   ├── images/
│           │   │   ├── icons/
│           │   │   └── logo/
│           │   └── js/
│           │       └── pages/
│           └── templates/
│               ├── fragments/
│               └── pages/
├── test/
├── target/
├── .env
├── .gitattributes
├── .gitignore
├── HELP.md
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## 7. 브랜치 전략 및 컨벤션
### 🔹 브랜치 전략
초기에는 기능별로 분리했으나, 브랜치 관리에 자원이 많이 든다는 피드백 후 이름으로 변경함<br>
커밋 주기는 짧게 하고, 미완성인 상태의 코드는 병합하지 않음
<table>
  <tr>
    <td align="center">
      브랜치
    </td>
    <td align="center">
      설명
    </td>
  </tr>
  <tr>
    <td align="center">
      main
    </td>
    <td align="center">
      각자 구현이 끝나면 merge하는 브랜치 + 배포 브랜치
    </td>
  </tr>
  <tr>
    <td align="center">
      name
    </td>
    <td align="center">
      sungyeon, jinwook, soowan, hyunjin : 각자 이름
    </td>
  </tr>
</table>

### 🔸 프로젝트 컨벤션 (이미지 넣기)
- 패키지 컨벤션<br>
  <img width="731" height="141" alt="Image" src="https://github.com/user-attachments/assets/98795f09-43ef-4396-b3dd-9cbf532c1183" />


- 클래스, 메서드, 변수, 상수<br>
  <img width="1664" height="717" alt="Image" src="https://github.com/user-attachments/assets/79b186a6-f8a9-4e33-9b01-0a9864ba2246" />


- 코드 스타일<br>
  - 들여쓰기: 4칸 스페이스(TAB)
  - 한 줄 최대: 120자
  - 중괄호: K&R 스타일
  - 문장종료: 반드시 세미콜론을 사용


- 의존성<br>
  - lombok 사용 안함

### 🔸 깃허브 컨벤션
- 커밋 컨벤션<br>
  <img width="712" height="510" alt="Image" src="https://github.com/user-attachments/assets/19473b43-22e7-4529-8d8f-63ed532c264d" />


- merge 컨벤션<br>
  <img width="716" height="222" alt="Image" src="https://github.com/user-attachments/assets/eab05242-1bad-44ce-a218-5d6235940da8" />


- 이슈 컨벤션<br>
  <img width="714" height="567" alt="Image" src="https://github.com/user-attachments/assets/5fc99dfb-1e07-4949-ae65-35bd2fdf7f16" />


## 8. 사용 기술 및 도구

### 🔹 Frontend

<div style="display: flex; flex-wrap: wrap; gap: 8px;">
  <img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white">
  <img src="https://img.shields.io/badge/css-1572B6?style=for-the-badge&logo=css3&logoColor=white">
  <img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
</div>

---

### 🔹 Backend

<div style="display: flex; flex-wrap: wrap; gap: 8px;">
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/spring%20boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
</div>

---

### 🔹 Database

<div style="display: flex; flex-wrap: wrap; gap: 8px;">
  <img src="https://img.shields.io/badge/postgresql-4169E1?style=for-the-badge&logo=postgresql&logoColor=white">
</div>

---

### 🔹 Infrastructure / Deployment

<div style="display: flex; flex-wrap: wrap; gap: 8px;">
  <img src="https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white">
  <img src="https://img.shields.io/badge/AWS%20S3-569A31?style=for-the-badge&logo=amazonaws&logoColor=white">
</div>

---

### 🔹 Collaboration

<div style="display: flex; flex-wrap: wrap; gap: 8px;">
  <a href="https://github.com/yoonhyunjin02/youtube-clone-frontend" target="_blank">
    <img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=Github&logoColor=white">
  </a>
  <img src="https://img.shields.io/badge/figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white">
</div>

---

### 🔹 Communication

<div style="display: flex; flex-wrap: wrap; gap: 8px;">
  <img src="https://img.shields.io/badge/notion-FEFEFE?style=for-the-badge&logo=notion&logoColor=black">
  <img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white">
</div>

---

### 🔹 Development Tools

<div style="display: flex; flex-wrap: wrap; gap: 8px;">
  <img src="https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white">
</div>

## 9. 최종 회고
- 김성연
  - 프로젝트를 진행하면서 JavaSpring의 기본적인 구조와 흐름을 경험할 수 있었습니다. AWS, 웹소켓처럼 생소한 기술을 접할 때, 당황하지 않고 침착하게 익혀나가는 자세의 중요성을 배운 것 같습니다.
    
    팀 프로젝트에서 팀장을 맡게 되었는데, 처음이라 부족한 점이 많았던 것 같습니다. 다음에 다시 팀장을 맡게 된다면 더 넓은 시야로 프로젝트를 바라보고, 체계적인 계획을 만들어보고 싶습니다.
    
    인상깊은 기능은 역시 AWS인 것 같습니다. 로컬에서 실행할 때와는 많은 부분에서 신경써야 할것도 많았기 때문에, 프로젝트에 대한 스스로의 이해도를 다시 한번 시험하는 것 같았습니다.
  
- 김진욱
  - 이번 프로젝트를 통해 다양한 기술 스택을 직접 써보며 새로운 걸 많이 배울 수 있었습니다.
    스프링, AWS, 웹소켓은 처음 써보는 기술이었지만, 실제로 적용해보면서 기본적인 개념과 사용법은 익힐 수 있었습니다.
    아직은 미숙하지만, 어느 정도 감을 잡는 데 도움이 되었던 것 같습니다.
    
    팀원들과의 소통도 잘 이루어져 협업 과정이 즐거웠고, 전체적인 프로젝트 진행도 매끄러웠습니다. 
    
    이번 경험을 바탕으로 다음 프로젝트는 더 잘해보고 싶습니다. 많이 배우고 성장할 수 있었던 값진 시간이었습니다.
  
- 이수완
  - 아직 안적음
  
- 윤현진
  - 아직 안적음