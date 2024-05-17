# 예약구매(liliflora-eCommerce) 프로젝트

<br>

## 🚀 프로젝트 소개

* 마이크로서비스 아키텍처를 활용한 예약 구매 플랫폼
* 대규모 트래픽 처리 테스트를 완료한 안정적인 서비스 구축

<br>

## 📍 주요 기능

* User
  * Spring Security & JWT 를 사용한 인증 및 인가 구현
  * SMTP 와 Redis 를 활용한 이메일 인증
  * 회원가입 유효성과 이메일 인증 중복 처리
  * 개인정보를 AES 암호화하여 저장

<br>

* Product & Stock
  * 재고 모듈을 분리하여 RedisHash 저장소 구성
  * Redisson 분산락을 이용한 동시성 이슈 해결
  * 상품 및 주문과의 트랜잭션 최종성 확보

<br>

* Orders & Wishlist
  * Spring scheduler 를 활용하여 주문 상태 변경 및 관리
  * 일반 주문과 예약 주문, 주문 취소 및 반품 기능 제공

<br>

## 📝 API 명세서 & ERD

**[API 명세서](https://ruby-flyingfish-f67.notion.site/API-_-MSA-2d6ae53ede284eaa82ccfe8fd04a3c96?pvs=4)**

<br>

user-service / product-service / order-serivce

![liliflora_msa](https://github.com/jeon-jyo/liliflora_msa/assets/96943317/a568a714-1519-4413-9229-8465733d801e)

<br>

## 📌 트러블슈팅

* **[OrderItems()가 null 로 받아오는 문제](https://liliflora.tistory.com/373)**
* **[OrderStatus Enum이 안 바뀌는 문제](https://liliflora.tistory.com/397)**
* **[동시성 제어를 위한 락이 잘 안되는 문제](https://liliflora.tistory.com/399)**

<br>

## ✨ 기술적 의사결정

* **[결제진입 API에서 결제완료까지의 관리](https://liliflora.tistory.com/392)**
* **[ApiGateway와 기존의 인증인가 방식](https://liliflora.tistory.com/384)**
* **[프론트 없이 jwt 로그아웃 구현](https://liliflora.tistory.com/385)**

<br>

## 📑 기타기록

* **[재고감소의 동시성제어, 분산락 테스트](https://liliflora.tistory.com/396)**
* **[일일 기록 보드](https://ruby-flyingfish-f67.notion.site/e43d92155170485693c45853df4819d8?v=b886208c7384456294d6020df46eaac3&pvs=4)**
