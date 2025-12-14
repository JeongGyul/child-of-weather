# 🌦️ 날씨의 아이 (Child of Weather)

> **실시간 날씨 기반 활동 추천 및 경로 찾기 서비스**

**Child of Weather**는 공공데이터포털의 기상청 API를 활용하여 사용자의 위치 기반 실시간 날씨 정보를 제공하고, 날씨 상태에 따라 적절한 활동을 추천해 주는 웹 애플리케이션입니다. 네이버 지도와 검색 API를 연동하여 장소 검색 및 경로 탐색 기능을 제공합니다.

---

## 📅 프로젝트 개요

- **프로젝트명**: Child of Weather
- **개발 기간**: 2025.11 ~ 2025.12
- **주요 기능**: 날씨 조회, 활동 추천, 장소 검색 및 경로 안내, 회원 관리
- **개발 목표**: MVC 패턴을 준수한 서블릿/JSP 기반의 웹 애플리케이션 구축

---

## 🛠️ 기술 스택 (Tech Stack)

### **Backend**
- **Language**: Java 17+
- **Framework/Library**: Jakarta EE (Servlet/JSP), Apache Tomcat 10.1
- **Database**: MariaDB (JDBC)
- **Build Tool**: Maven

### **Frontend**
- **Language**: HTML5, CSS3, JavaScript (ES6)
- **Template Engine**: JSP (Jakarta Server Pages)
- **Communication**: Fetch API (AJAX)

### **External APIs**
- **Weather**: 기상청 단기예보 조회 서비스 (공공데이터포털)
- **Map/Location**: 
  - **Naver Maps API v3** (Geocoding, Driving Route) - 지도 표시 및 경로 탐색
  - **Naver Search API** (Local Search) - 장소명(예: 서울역) 검색 및 주소 변환

---

## 📂 프로젝트 구조 (Project Structure)

현재 프로젝트는 **MVC (Model-View-Controller)** 아키텍처를 따르고 있으며, 기능별로 패키지가 명확하게 분리되어 있습니다.

```text
child-of-weather
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.childofweather
│   │   │       ├── controller      # 클라이언트 요청을 처리하는 서블릿 (Controller)
│   │   │       │   ├── main        # 메인/대시보드
│   │   │       │   ├── member      # 로그인, 회원가입, 마이페이지
│   │   │       │   ├── activity    # 활동 추천 및 CRUD
│   │   │       │   ├── weather     # 날씨 정보 조회
│   │   │       │   ├── admin       # 관리자 페이지
│   │   │       │   └── route       # 경로 안내
│   │   │       ├── service         # 비즈니스 로직 처리 (Service Layer)
│   │   │       │   ├── provider    # 외부 API 호출 로직 (WeatherProvider)
│   │   │       │   └── ...
│   │   │       ├── dao             # 데이터베이스 접근 객체 (DAO)
│   │   │       ├── dto             # 데이터 전송 객체 (DTO)
│   │   │       ├── filter          # 보안 및 인증 처리 (AuthenticationFilter)
│   │   │       └── util            # 유틸리티 (DB연결, JSON파싱, API 설정 등)
│   │   │
│   │   └── webapp
│   │       ├── WEB-INF
│   │       │   └── views           # JSP 페이지 (외부 접근 차단)
│   │       │       ├── activity
│   │       │       ├── admin
│   │       │       ├── common      # 헤더 등 공통 컴포넌트
│   │       │       ├── main
│   │       │       ├── member
│   │       │       └── route
│   │       └── index.jsp           # 엔트리 포인트
└── pom.xml                         # Maven 의존성 관리
```

---

## ✨ 주요 기능 (Key Features)

### 1. 회원 서비스 (Member Service)
- **회원가입/로그인**: 일반 사용자 및 관리자(`ADMIN`) 권한 분리
- **보안**: `AuthenticationFilter`를 통한 중앙 집중식 인증 관리 (로그인 체크 중복 제거)
- **마이페이지**: 개인정보 수정 및 로그아웃

### 2. 날씨 및 활동 추천 (Weather & Activity)
- **실시간 날씨**: 사용자의 현재 위치의 날씨(기온, 강수확률 등) 조회
- **활동 추천**: 날씨 데이터를 분석하여 '맑음', '비', '눈' 등의 상태에 맞는 활동 추천

### 3. 장소 검색 및 경로 탐색 (Route & Search)
- **장소 검색**: "서울역", "강남역" 등 장소명 입력 시 **Naver Search API**를 통해 정확한 도로명 주소로 변환
- **경로 안내**: 출발지와 목적지의 주소를 기반으로 최적의 이동 경로를 지도에 시각화

### 4. 관리자 모드 (Admin)
- **대시보드**: 회원 현황 및 전체적인 사이트 통계 확인
- **접근 제어**: 관리자 페이지 접근 시 권한 검증 및 일반 사용자 차단

---

## 👥 팀원 소개 (Team Members)

| 이름 (Name) | 역할 (Role) | 담당 업무 (Responsibilities) | GitHub |
|:---:|:---:|:---|:---:|
| **김준혁** | **Team Leader** <br> Backend | • **기상청 API 연동**: 현재 위치 기반 실시간 날씨 조회 기능 구현 <br> • **추천 알고리즘**: 날씨 상태(맑음, 비 등)에 따른 맞춤형 활동 추천 로직 개발 <br> • **스케줄링**: 사용자 등록 활동의 최적 시간대 추천 기능 구현 <br> • **환경 설정**: Maven 빌드 시스템 도입 및 프로젝트 의존성 관리 | [@ddo0122](https://github.com/ddo0122) |
| **최정규** | **Core Developer** <br> Backend / Infra | • **아키텍처 설계**: 전체 MVC 프로젝트 구조 설계 및 코드 리팩토링 <br> • **보안/인증**: `Filter` 기반 인증/인가 시스템 및 관리자(Admin) 기능 구현 <br> • **DevOps**: 라즈베리파이를 활용한 공용 DB 서버 구축 및 배포 환경 조성 <br> • **Maintainer**: 코드 리뷰, Git 충돌 해결 및 `main` 브랜치 병합 관리 | [@JeongGyul](https://github.com/jeonggyul) |
| **심민식** | **Developer** <br> Backend / Frontend | • **지도 API 연동**: 네이버 지도/검색 API를 활용한 장소 검색 및 시각화 <br> • **기능 구현**: 활동 등록/삭제(CRUD) 기능 개발 및 DB 스키마 설계 <br> • **Frontend**: 웹사이트 전반의 페이지 레이아웃 및 UI 구현 <br> • **유틸리티**: API Key 보안 분리 및 `ApiConfig` 설정 클래스 구현 | [@minsik1014](https://github.com/minsik1014) |

---

## ⚙️ 설치 및 실행 방법 (Getting Started)

### 1. 환경 설정
- **Java**: JDK 17 이상
- **Server**: Apache Tomcat 10.1 (Jakarta EE 지원 필수)
- **Database**: MariaDB

### 2. 실행
- 프로젝트 Clone 후 `src/main/java/com/childofweather/util/JdbcConnectUtil.java`의 DB 정보를 본인의 환경에 맞게 수정합니다.
- `ApiConfig.java` 등에서 API Key(기상청, 네이버 지도/검색)를 설정합니다.
- Tomcat 서버를 구동하여 `http://localhost:8080/ChildOfWeather`로 접속합니다.

---

## 📝 License

This project is licensed under the MIT License.
