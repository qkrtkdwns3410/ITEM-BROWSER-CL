# item-browser

## 프로젝트 개요

`item-browser` 는 다양한 상품을 검색하고, 주문, 회원, 상품, 장바구니의 일련의 기능의 이해를 위해 시작했습니다.
- 해당 프로젝트에서 새로운 기술과 클린코드란 무엇인지에 대해 고찰하는 기회를 가져보려고 합니다.

## 개발 환경 및 사용기술

- IDE: IntelliJ IDEA
- Language: Java 11
- Framework: Spring Boot 2.7.1
- Build Tool: Gradle
- DB: MYSQL 5.7
- Test: JUnit5
- API 문서: Swagger3 + Spring RestDocs

## Guide
1. docker-compose.yml 실행
2. boorun

# API 엔드포인트 목록

## 스웨거 URL
/docs/swagger

## 장바구니 관련 엔드포인트 (CartApiController)

### 장바구니 조회
- **GET** `/v1/api/cart/{userId}`
    - 특정 사용자의 장바구니 정보를 조회합니다.
    - `{userId}`는 사용자의 고유 식별자입니다.

### 장바구니에 상품 추가
- **POST** `/v1/api/cart`
    - 장바구니에 새로운 상품을 추가합니다.

### 장바구니 상품 수정
- **PUT** `/v1/api/cart`
    - 장바구니의 상품 수량을 수정합니다.

### 장바구니 상품 삭제
- **DELETE** `/v1/api/cart`
    - 장바구니에서 특정 상품을 삭제합니다.

## 회원 관련 엔드포인트 (MemberApiController)

### 회원가입
- **POST** `/v1/api/member/register`
    - 새로운 회원을 등록합니다.

## 주문 관련 엔드포인트 (OrderApiController)

### 주문 조회
- **GET** `/v1/api/orders/{orderId}`
    - 특정 주문의 상세 정보를 조회합니다.
    - `{orderId}`는 주문의 고유 식별자입니다.

### 주문 생성
- **POST** `/v1/api/orders`
    - 새로운 주문을 생성합니다.

### 사용자 주문 목록 조회
- **GET** `/v1/api/orders/users/{userNumber}`
    - 특정 사용자의 주문 목록을 조회합니다.
    - `{userNumber}`는 사용자의 고유 번호입니다.

### 주문 삭제
- **DELETE** `/v1/api/orders/{orderId}`
    - 특정 주문을 삭제합니다.
    - `{orderId}`는 주문의 고유 식별자입니다.

## 상품 관련 엔드포인트 (ProductApiController)

### 상품 추가
- **POST** `/v1/api/products`
    - 새로운 상품을 추가합니다.

### 상품 수정
- **PUT** `/v1/api/products/{productId}`
    - 기존 상품의 정보를 수정합니다.
    - `{productId}`는 상품의 고유 식별자입니다.

### 상품 삭제
- **DELETE** `/v1/api/products/{productId}`
    - 특정 상품을 삭제합니다.
    - `{productId}`는 상품의 고유 식별자입니다.

### 상품 상세 정보 조회
- **GET** `/v1/api/products/{productId}`
    - 특정 상품의 상세 정보를 조회합니다.
    - `{productId}`는 상품의 고유 식별자입니다.

## 로그인 관련 엔드포인트 (LoginApiController)

### 로그인
- **POST** `/login`
    - 사용자 로그인을 처리합니다.

### 토큰 갱신
- **PUT** `/refresh-token`
    - 사용자의 토큰을 갱신합니다.

---

## GANNT CHART

```mermaid
gantt
    title 프로젝트 일정
dateformat YYYY-MM-DD 
  section 개발일정
Mybatis 개발일정: active, mybatis001,  2024-01-21, 2024-02-19
JPA 개발일정: active, jpa001, 2024-02-20, 2024-03-05

```

## TEAM NOTION

    [팀 노션 바로가기](https://iodized-bassoon-8e3.notion.site/5b24052881b34fd1ac8fbcf269ad3ba0?pvs=4)
