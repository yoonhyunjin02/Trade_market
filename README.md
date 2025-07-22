<div align="center">
  <h1>🥕 당근 마켓 클론 코딩 프로젝트</h1>
</div>


## 📍 목차

1. [프로젝트 소개](#1-프로젝트-소개)
2. [팀원 소개 및 역할](#2-팀원-소개-및-역할)
3. [ERD 구조](#3-erd)
4. [페이지 구성](#4-페이지-구성)
5. [페이지별 주요 상세 기능](#5-페이지별-주요-상세-기능)
6. [디렉토리 구조](#6-디렉토리-구조)
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

### [노션 페이지에서 전체 페이지 모아보기](https://www.notion.so/2372233de69380e496c6c9c118c7d6f4?source=copy_link)

<details>
<summary>header</summary>
  <img width="721" height="723" alt="Image" src="https://github.com/user-attachments/assets/95af19b1-f0d1-4cb7-9791-3d5343d1cc24" />
</details>

<details>
<summary>footer</summary>
  <img width="716" height="288" alt="Image" src="https://github.com/user-attachments/assets/727d7acc-3416-41e3-b3c9-234fd823a0aa" />
</details>

<details>
<summary>login</summary>
  <img width="721" height="592" alt="Image" src="https://github.com/user-attachments/assets/ee0d3ee5-665d-4f43-a347-bc839e9301c0" />
</details>

<details>
<summary>register</summary>
  <img width="721" height="574" alt="Image" src="https://github.com/user-attachments/assets/88e7e51e-c12d-4a4d-a29d-6fd75672f8d5" />
</details>

<details>
<summary>main</summary>
  <img width="725" height="635" alt="Image" src="https://github.com/user-attachments/assets/042ec653-fbc7-4a8e-a3f9-cd18d5ece9bf" />
  <img width="720" height="1016" alt="Image" src="https://github.com/user-attachments/assets/28cbc259-97d5-469d-abd3-5bb4894ab064" />
</details>

<details>
<summary>trade</summary>
  <img width="720" height="574" alt="Image" src="https://github.com/user-attachments/assets/bd9ebc9d-9520-4c2d-a6d9-8b0d87b78ada" />
  <img width="735" height="499" alt="Image" src="https://github.com/user-attachments/assets/73a9a3bd-3aaa-4dc1-aacd-745a74c277c3" />
  <img width="778" height="2906" alt="Image" src="https://github.com/user-attachments/assets/f144ae0c-eb51-460f-b881-98d3580bcc1c" />
</details>

<details>
<summary>trade-post</summary>
<img width="716" height="880" alt="Image" src="https://github.com/user-attachments/assets/86fe871b-bc18-4510-8cc8-9c624df089e6" />
<img width="708" height="724" alt="Image" src="https://github.com/user-attachments/assets/19ae923f-9c7e-4ae5-af51-417c95854e66" />
<img width="719" height="318" alt="Image" src="https://github.com/user-attachments/assets/2477a928-0104-438d-b294-9f9667b04cdc" />
</details>

<details>
<summary>trade-write</summary>
<img width="721" height="774" alt="Image" src="https://github.com/user-attachments/assets/5d055e2b-fbb5-43f9-b8b8-e8f54e24cda0" />
<img width="724" height="870" alt="Image" src="https://github.com/user-attachments/assets/21722290-710e-40f3-9548-2d3b2a5fbb43" />
<img width="683" height="616" alt="Image" src="https://github.com/user-attachments/assets/9bad7a96-d386-4262-b2d8-ea4f414db503" />
<img width="757" height="1063" alt="Image" src="https://github.com/user-attachments/assets/09a4554f-b7c0-47b7-a1a9-6142eb7a9b6f" />
<img width="738" height="1163" alt="Image" src="https://github.com/user-attachments/assets/0d1b344e-8ac7-4c7f-b93d-7dfac2eb2201" />
<img width="706" height="638" alt="Image" src="https://github.com/user-attachments/assets/bc991004-53f9-4316-8af6-30813cdc3a4e" />
<img width="715" height="742" alt="Image" src="https://github.com/user-attachments/assets/15ac5fe7-9d1a-4226-a2ea-d1b6bd7cac56" />
</details>

<details>
<summary>search</summary>
<img width="712" height="915" alt="Image" src="https://github.com/user-attachments/assets/0630ad7f-6b9b-46d8-92b4-a2550a06a13e" />
<img width="660" height="797" alt="Image" src="https://github.com/user-attachments/assets/79d292ce-e979-4858-b962-2e5b7ebf05bd" />
<img width="667" height="343" alt="Image" src="https://github.com/user-attachments/assets/c396706b-a41d-46d6-af99-2e8360e90d98" />
</details>

<details>
<summary>chat</summary>
<img width="718" height="577" alt="Image" src="https://github.com/user-attachments/assets/525862de-ec97-420a-bcf8-0821ded200c8" />
<img width="722" height="583" alt="Image" src="https://github.com/user-attachments/assets/ec9392fe-54fe-47b8-bacf-46520e3128ba" />
<img width="717" height="612" alt="Image" src="https://github.com/user-attachments/assets/3edf7348-9deb-4b87-b855-0c0d7e3e0584" />
<img width="691" height="868" alt="Image" src="https://github.com/user-attachments/assets/0793e493-b722-49b9-924b-ed45c2590714" />
</details>

<details>
<summary>chatBot</summary>
<img width="721" height="753" alt="Image" src="https://github.com/user-attachments/assets/10152083-d0d9-4564-88ba-508480568ab4" />
<img width="694" height="638" alt="Image" src="https://github.com/user-attachments/assets/c4093a0b-2760-488d-8f27-55798199731c" />
<img width="695" height="702" alt="Image" src="https://github.com/user-attachments/assets/60c12255-a374-4b0d-961d-047991dd32af" />
</details>

<details>
<summary>location</summary>
<img width="722" height="713" alt="Image" src="https://github.com/user-attachments/assets/a8259d18-4f2b-4022-ae4d-845afe7328ba" />
<img width="724" height="904" alt="Image" src="https://github.com/user-attachments/assets/074a67fb-01c8-4033-b72a-2d774e3c0266" />
<img width="705" height="968" alt="Image" src="https://github.com/user-attachments/assets/dbe33aa1-a3d7-4f4b-8bfa-7ecd98d01c52" />
</details>

<details>
<summary>mypage</summary>
<img width="722" height="774" alt="Image" src="https://github.com/user-attachments/assets/a0c4dc74-7028-4c4e-9f4f-b1ffd07e92a4" />
<img width="720" height="673" alt="Image" src="https://github.com/user-attachments/assets/5f395a78-d118-4c13-a051-e357cb870e6c" />
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
  - lombok, STOMP 사용 안함

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
  
  프로젝트를 진행하면서 JavaSpring의 기본적인 구조와 흐름을 경험할 수 있었습니다. AWS, 웹소켓처럼 생소한 기술을 접할 때, 당황하지 않고 침착하게 익혀나가는 자세의 중요성을 배운 것 같습니다.
    
  팀 프로젝트에서 팀장을 맡게 되었는데, 처음이라 부족한 점이 많았던 것 같습니다. 다음에 다시 팀장을 맡게 된다면 더 넓은 시야로 프로젝트를 바라보고, 체계적인 계획을 만들어보고 싶습니다.
    
  인상깊은 기능은 역시 AWS인 것 같습니다. 로컬에서 실행할 때와는 많은 부분에서 신경써야 할것도 많았기 때문에, 프로젝트에 대한 스스로의 이해도를 다시 한번 시험하는 것 같았습니다.
  

- 김진욱
  
  이번 프로젝트를 통해 다양한 기술 스택을 직접 써보며 새로운 걸 많이 배울 수 있었습니다.
  스프링, AWS, 웹소켓은 처음 써보는 기술이었지만, 실제로 적용해보면서 기본적인 개념과 사용법은 익힐 수 있었습니다.
  아직은 미숙하지만, 어느 정도 감을 잡는 데 도움이 되었던 것 같습니다.
    
  팀원들과의 소통도 잘 이루어져 협업 과정이 즐거웠고, 전체적인 프로젝트 진행도 매끄러웠습니다. 
    
  이번 경험을 바탕으로 다음 프로젝트는 더 잘해보고 싶습니다. 많이 배우고 성장할 수 있었던 값진 시간이었습니다.
  

- 이수완
  
  이번 프로젝트에서 가장 큰 도전은 WebSocket을 활용한 실시간 채팅 시스템 구현이었습니다.
  단순한 메시지 전송으로 생각했던 기능이 실제로는 세션 관리, 연결 끊김 처리, 브로드캐스팅 등 복잡한 요소들을 포함하고 있어 예상보다 훨신 많은 학습이 필요했습니다.

  바닐라 JavaScript로 실시간 채팅 UI를 구현하며 프론트엔드 상태 관리의 복잡성을 경험했고, 특히 채팅 목록과 메시지 동기화 과정에서 UI 일관성 유지의 중요성을 깨달았습니다.

  또한 GlobalExceptionHandler를 통한 일관된 예외 처리 구현으로 사용자 경험 개선의 중요성을 배웠습니다.

  문제가 발생했을 때 문제 내용을 팀원들과 지속적으로 공유하고 모두가 같이 문제를 해결하려고 해서 좋은 경험이었습니다.

  전체적으로 기술적 성장뿐만 아니라 협업을 통한 문제 해결 능력까지 향상시킬 수 있었던 의미 있는 경험이었습니다.
  

- 윤현진
  - 아직 안적음