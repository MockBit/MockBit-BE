# MockBit - 비트코인 선물 거래 모의 투자 플랫폼

**개발 기간**: 2025.01 ~ 현재

**기술 스택**:

- **Frontend**: React
- **Backend**: Spring Boot, Gradle, Hibernate, Redis, Kafka, Grafana, Prometheus
- **Deploy**: AWS EC2, Docker

---

## 프로젝트 개요

### 왜 만들었나요?

2024년 가을, 비트코인과 주식 시장이 뜨거웠던 시기에 재테크에 첫 발을 내디뎠습니다. 선물 거래에 매력을 느꼈지만, 높은 리스크에 비해 경험을 쌓을 방법이 마땅치 않았습니다. 기존 모의 투자 플랫폼은 UI/UX가 불편하고 기능이 제한적이어서 답답함을 느꼈고, 결국 제가 원하는 플랫폼을 직접 만들기로 했습니다. 단순히 개인적인 니즈를 넘어, 트레이딩을 안전하게 연습하고 싶은 사람들과 이 가치를 공유하고자 했습니다.

---

## 주요 기능

- **롱/숏 선물 거래**: 동일 시간대 반대 포지션 진입 제한으로 리스크 관리.
- **시장가/지정가 거래**: 실시간 시장가 거래와 유연한 지정가 주문(수정/취소 포함).
- **실시간 포지션 조회**: WebSocket으로 평균 거래량, 레버리지, 수익률, 청산 금액 제공.
- **자동 청산**: 비트코인 가격 변동 시 모든 포지션 실시간 점검 및 청산 처리.
- **원화 충전**: 잔고 5,000원 미만 시 1,000만 원 자동 충전으로 사용자 경험 개선.

[![hits](https://myhits.vercel.app/api/hit/https%3A%2F%2Fgithub.com%2FMockBit%2FMockBit-BE?color=green&label=hits&size=small)](https://myhits.vercel.app)
