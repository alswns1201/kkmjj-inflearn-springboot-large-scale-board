# inflearn-springboot-large-scale-board
커리큘럼  스프링부트로 직접 만들면서 배우는 대규모 시스템 설계 - 게시판

## 대규모 시스템의 정의
- 복잡한 시스템을 다루는 방법 , 병령처리 , 병목 지점 최소화 , 확장성 있는 구조

## 프로젝트 요구사항
- 게시글 조회, 생성 , 수정 ,삭제, 목록 조회
- 댓글 조회, 생성 , 삭제 , 목록 조회
  - 계층형 최대 2 depth , 무한 depth
- 게시글 좋아요  : 좋아요 수
- 게시글 조회수 : 사용자 별 10분에 1회 집계
- 인기글
  - 일 단위 상위 10건 인기글 선정
    - 매일 오전 1시 업데이트
    - 댓글수/좋아요수/조회수 기반 점수
  - 최근 N일 인기글 내역 조회
- 게시글 조회 최적화 전략
  - 게시글 단건 조회 최적화 전략
  - 게시글 목록 조회 최적화 전략
  - 캐시 최적화 전략
- `대규모 데이터와 트래픽을 다루기 위한 핵심 전략`

## 프로젝트 구조 
- kuke-board
  - service : 하위 모듈 각각 MSA 를 가진다.
    - article : 게시글 서비스
    - comment : 댓글 서비스
    - like : 좋아요 서비스
    - view : 조회수 서비스
    - hot-article : 인기글 서비스
    - article-read : 게시글 조회 서비스
  - conmmon : 하위 모듈로 개발 편의를 위한 공통 코드 관리
 
## 데이터 베이스 분산
- 저장할 데이터와 트래픽이 많아졌다면 `scale-up` 을 고려해볼수 있다. db 장비의 성능을 upgrade
- 장비를 무한정 scale-up 하는것은 한계가 있기 때문에 `scale-out` 을 고려 해본다.
  - 장비를 여러대 사용하여 수평 확장
### 샤딩(Sharding) : 데이터를 여러 데이터 베이스에 분산하여 저장하는 기술 
#### 샤드 종류 기법
-  샤딩된 각각의 데이터 단위 샤드라고 부른다.
- `수직샤딩` : 데이터를 컬럼 단위로 분할 하는 방식
  <img width="592" height="310" alt="image" src="https://github.com/user-attachments/assets/267219ef-64ae-4380-8e98-d998470ca4c3" />
- `수평샤딩`: 데이터 행 단위로 분산 저장 하는 방식 
  <img width="612" height="306" alt="image" src="https://github.com/user-attachments/assets/6a2f9d9d-d8d2-4499-b626-a65cdf2442e1" />
- `분산 기반 샤딩` : 행 단위의 범위를 지정하여 분산 저장 하는 방식
  - 예) 1~5000->1DB , 5001~6000->2DB
- `해시 기반 샤딩` : 특정 값의 해시 함수에 따라 분할 하는 기법
  - 예) hash_function = article -> article_id % 2 ? 좌측 샤드 article_id = 1 , 3, 5 ... / 우측 샤드 article_id = 2 ,4 ,6 ...
#### 물리적 / 논리적 분리 
<img width="715" height="303" alt="image" src="https://github.com/user-attachments/assets/e1b6b2e0-32c5-4e92-ae55-9c38096c98f6" />

물리적으로 shard를 늘리게 되면 client의 수정도 필요하다 client의 수정없이 DB만 유연하게 확장 하는 방법에서 부터 논리적 분리의 개념이나오기 시작했다.

<img width="582" height="298" alt="image" src="https://github.com/user-attachments/assets/720084d1-540a-4e59-8169-a3473981bf4f" />

#### 데이터 복제의 필요성
나눈 샤드에 장애가 발생했을때  문제를 해결하기 위해 데이터 복제본을 관리한다.
- Primary 주 데이터베이스에 데이터를 쓰고 Replica에 복제 데이터를 관리한다.
  - Primary/Replica , Master/Slave , Main/Standby 등 유사한 개념이 있다.


<img width="493" height="302" alt="image" src="https://github.com/user-attachments/assets/1d7f0986-0607-43c6-83d5-6c4e36610d60" />

  

