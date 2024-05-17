# 예약구매(liliflora-eCommerce) 프로젝트

## 🚀 프로젝트 소개

* 마이크로서비스 아키텍처를 활용한 예약 구매 플랫폼
* 대규모 트래픽 처리 테스트를 완료한 안정적인 서비스 구축

## 📍 주요 기능

* User
  * Spring Security & JWT 를 사용한 인증 및 인가 구현
  * SMTP 와 Redis 를 활용한 이메일 인증
  * 회원가입 유효성과 이메일 인증 중복 처리
  * 개인정보를 AES 암호화하여 저장
* Product & Stock
  * 재고 모듈을 분리하여 RedisHash 저장소 구성
  * Redisson 분산락을 이용한 동시성 이슈 해결
  * 상품 및 주문과의 트랜잭션 최종성 확보
* Orders & Wishlist
  * Spring scheduler 를 활용하여 주문 상태 변경 및 관리
  * 일반 주문과 예약 주문, 주문 취소 및 반품 기능 제공

## 📝 API 명세서 & ERD

**[API 명세서](https://ruby-flyingfish-f67.notion.site/API-_-MSA-2d6ae53ede284eaa82ccfe8fd04a3c96?pvs=4)**

## 📌 트러블슈팅

🔸
🔹

## ✨ 기술적 의사결정

