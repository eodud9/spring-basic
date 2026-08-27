# Spring Basic

Spring Boot 백엔드 기본기를 학습하기 위한 프로젝트입니다.

Spring Boot, JPA, Spring Security, JWT를 사용하여 **회원 관리부터 인증/인가, Refresh Token 관리까지** 직접 구현했습니다.

---

## 📌 프로젝트 목적

Spring Boot 기반 백엔드 개발의 기본적인 구조와 인증 시스템의 동작 원리를 이해하고 직접 구현하는 것을 목표로 합니다.

특히 단순 CRUD 구현에서 끝내지 않고 다음 내용을 단계적으로 학습했습니다.

* REST API 설계
* JPA 기반 데이터베이스 연동
* Validation 및 예외 처리
* Spring Security
* JWT 기반 인증/인가
* Access Token / Refresh Token
* Refresh Token DB 관리
* Refresh Token Rotation
* 리소스 소유권 검사
* Transaction 및 JPA 변경 감지

---

## 🛠 Tech Stack

### Backend

* Java
* Spring Boot
* Spring MVC
* Spring Data JPA
* Spring Security
* JWT
* BCrypt

### Database

* MySQL

### Build Tool

* Gradle

### Development Tool

* IntelliJ IDEA
* Postman
* Git / GitHub

---

## 🏗️ Project Structure

```text
src/main/java/com/example/spring_basic
├── config
│   └── SecurityConfig
│
├── controller
│   ├── UserController
│   └── AuthController
│
├── service
│   ├── UserService
│   ├── AuthService
│   └── CustomUserDetailsService
│
├── repository
│   ├── UserRepository
│   └── RefreshTokenRepository
│
├── entity
│   ├── User
│   └── RefreshToken
│
├── dto
│   ├── UserCreateRequest
│   ├── UserUpdateRequest
│   ├── UserResponse
│   ├── LoginRequest
│   ├── LoginResponse
│   ├── RefreshRequest
│   └── LogoutRequest
│
├── jwt
│   ├── JwtTokenProvider
│   └── JwtAuthenticationFilter
│
└── exception
    ├── GlobalExceptionHandler
    └── UserNotFoundException
```

---

## 🔐 Authentication Flow

### 1. 회원가입

```text
Client
  ↓
POST /users
  ↓
UserController
  ↓
UserService
  ↓
PasswordEncoder
  ↓
UserRepository
  ↓
User DB 저장
```

비밀번호는 BCrypt를 사용하여 암호화한 후 저장합니다.

---

### 2. 로그인

```text
Client
  ↓
POST /auth/login
  ↓
AuthService
  ↓
Email로 User 조회
  ↓
Password 검증
  ↓
Access Token 생성
+
Refresh Token 생성
  ↓
Refresh Token DB 저장
  ↓
Client에 두 Token 반환
```

응답 예시:

```json
{
  "accessToken": "...",
  "refreshToken": "..."
}
```

---

## 🎫 JWT Authentication

API 요청 시 Access Token을 다음과 같이 전달합니다.

```http
Authorization: Bearer {accessToken}
```

`JwtAuthenticationFilter`가 요청의 JWT를 확인하고 Spring Security의 `SecurityContext`에 인증 정보를 저장합니다.

```text
HTTP Request
     ↓
Authorization Header
     ↓
JwtAuthenticationFilter
     ↓
JWT 검증
     ↓
UserDetailsService
     ↓
Authentication 생성
     ↓
SecurityContext
     ↓
Controller
```

---

## 🔄 Refresh Token

Access Token이 만료되면 Refresh Token을 사용하여 새로운 Access Token을 발급합니다.

Refresh Token은 JWT 자체의 유효성뿐만 아니라 **DB에 실제로 존재하는지 확인**합니다.

```text
Refresh Token
      ↓
JWT 검증
      ↓
Refresh Token 타입 확인
      ↓
DB 조회
      ↓
User 확인
      ↓
새 Access Token 발급
```

---

## ♻️ Refresh Token Rotation

Refresh Token을 사용할 때마다 기존 Refresh Token을 폐기하고 새로운 Refresh Token을 발급합니다.

```text
Refresh Token A
       ↓
/auth/refresh
       ↓
Token A 검증
       ↓
Token A 삭제
       ↓
Token B 생성
       ↓
Token B DB 저장 
       ↓
Access Token B
+
Refresh Token B
```

따라서 이미 사용한 Refresh Token을 다시 사용하는 것을 방지할 수 있습니다.

---

## 🚪 Logout

로그아웃 요청이 들어오면 해당 Refresh Token을 DB에서 삭제합니다.

```text
Logout Request
      ↓
Refresh Token 추출
      ↓
DB에서 Token 삭제
      ↓
204 No Content
```

삭제된 Refresh Token으로 다시 Access Token을 발급하려고 하면 실패합니다.

---

## 🔒 Authorization

단순히 JWT가 유효하다는 것만으로 모든 사용자의 리소스를 수정할 수 있도록 하지 않고 **리소스 소유권 검사**를 적용했습니다.

예를 들어:

```text
JWT → User 7

PUT /users/1
```

인 경우,

```text
JWT 사용자 = 7
요청 대상 = 1

→ 서로 다름
→ 수정 불가
```

와 같이 자신의 리소스만 수정하거나 삭제할 수 있도록 검사합니다.

---

## 🗄️ JPA 학습 내용

### Entity Relationship

User와 RefreshToken의 관계를 `@ManyToOne`으로 구성했습니다.

```text
User
 ├── RefreshToken
 ├── RefreshToken
 └── RefreshToken
```

### Transaction

데이터 변경 작업에 `@Transactional`을 적용했습니다.

```text
@Transactional
      ↓
Transaction 시작
      ↓
Entity 변경
      ↓
Dirty Checking
      ↓
UPDATE SQL
      ↓
Transaction Commit
```

또한 DELETE 작업에서 Transaction이 필요한 이유를 직접 확인하고 해결했습니다.

### N+1

JPA 연관관계에서 발생할 수 있는 N+1 문제를 직접 확인하고 학습했습니다.

---

## ⚠️ Exception Handling

`@RestControllerAdvice`를 사용하여 전역 예외 처리를 구성했습니다.

예:

```text
UserNotFoundException
        ↓
GlobalExceptionHandler
        ↓
404 Not Found
```

Validation 오류 역시 전역적으로 처리하도록 구성했습니다.

---

## 🧪 Testing

Postman을 사용하여 주요 API를 직접 테스트했습니다.

### Authentication

* 회원가입
* 로그인
* Access Token 인증
* Refresh Token 재발급
* 잘못된 Token 검증
* Refresh Token Rotation
* 기존 Refresh Token 재사용 방지
* 로그아웃
* 로그아웃 후 Token 재사용 방지

### Authorization

* 인증된 사용자의 수정/삭제
* 다른 사용자의 리소스 접근 차단

---

## 🔑 Environment Variables

JWT Secret Key와 같은 민감한 정보는 코드에 직접 작성하지 않고 환경변수를 통해 관리합니다.

```properties
jwt.secret-key=${JWT_SECRET}
```

`.env`와 같은 민감한 설정 파일은 Git에 포함하지 않습니다.

---

## 📚 What I Learned

이 프로젝트를 통해 다음과 같은 백엔드 핵심 개념을 직접 구현하고 테스트했습니다.

* Spring Boot 프로젝트 구조
* Controller / Service / Repository 계층 분리
* DTO
* REST API
* JPA / Hibernate
* Entity 연관관계
* N+1 문제
* Transaction
* Dirty Checking
* Validation
* Global Exception Handling
* Spring Security
* Authentication / Authorization
* Password Hashing
* JWT
* Access Token / Refresh Token
* JWT Filter
* SecurityContext
* Refresh Token Persistence
* Refresh Token Rotation
* Resource Ownership

---

## 🚀 Next Step

이 프로젝트에서 학습한 Spring Boot 백엔드 기본기를 바탕으로 **React + Spring Boot + MySQL 기반의 실전 프로젝트**를 진행할 예정입니다.

```text
Spring Basic
     ↓
Spring Boot / JPA / Security 학습
     ↓
JWT Authentication
     ↓
Refresh Token Rotation
     ↓
실전 프로젝트
     ↓
React + Spring Boot + MySQL
     ↓
포트폴리오 완성
```
