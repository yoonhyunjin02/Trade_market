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

> 당근 마켓 클론 페이지 구현

- 📅 진행 기간: 2025년 7월 3일 ~ 2025년 7월 23일
- 🎯 주요 기능
    - 회원가입/로그인 및 소셜 로그인
    - 상품 조회, 등록, 수정, 삭제
    - 상품 조회 필터(거래 상태, 카테고리, 가격 등)
    - 챗봇 서비스(재미나이 활용)
    - 채팅 기능
    - Google Maps API로 위치 인증 기능
- 🦉 팀명: 올빼미 : 밤늦게까지 코딩 중..
- 📚[Notion](https://www.notion.so/2252233de69380d0bd52de0b615160ec?source=copy_link)
- 📬[배포]()
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

피그마 사진 추가

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

## 5. 페이지별 주요 기능
- 로그인(login)
  <br>
  로그인 페이지 이미지 넣기


- 회원가입(register)
  <br>
  회원가입 페이지 이미지 넣기


- 메인(main)
  <br>
  페이지 이미지 넣기


- 상품(trade)
  <br>
  페이지 이미지 넣기


- 상품 상세(trad-post)
  <br>
  페이지 이미지 넣기


- 상품 등록(wirte)
  <br>
  페이지 이미지 넣기


- 검색(search)
  <br>
  페이지 이미지 넣기


- 일반 채팅(chat)
  <br>
  페이지 이미지 넣기


- 챗봇 채팅(chatBot)
  <br>
  페이지 이미지 넣기


- 위치(location)
  <br>
  페이지 이미지 넣기


- 프로필(mypage)
  <br>
  페이지 이미지 넣기

## 6. 디렉토리 구조(아직 다 안함)
```
TradeMarket/
├── src/
│   └── main/
│       ├── java/com/example/trade_market/
│       │   ├── config/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── entity/
│       │   ├── repository/
│       │   ├── service/
│       │   │   └── impl/
│       │   └── util/
│       └── resources/
│           ├── static/
│           │   ├── css/
│           │   │   ├── common/
│           │   │   └── pages/
│           │   ├── js/
│           │   │   ├── common/
│           │   │   └── pages/
│           │   └── images/
│           │       ├── icons/
│           │       └── logo/
│           └── templates/
│               ├── fragments/
│               └── pages/
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
  - 아직 안적음
  
- 김진욱
  - 아직 안적음
  
- 이수완
  - 아직 안적음
  
- 윤현진
  - 아직 안적음