# AI Court - Android Application

AI Court는 두 사용자가 특정 주제에 대해 토론(논쟁)을 진행하고, AI가 실시간으로 승률을 분석하여 최종 판결을 내리는 법정 시뮬레이션 앱입니다.

## 프로젝트 설명

### 개요
- **플랫폼**: Android (Kotlin + Jetpack Compose)
- **아키텍처**: Clean Architecture (Data / Domain / Presentation)
- **인증 방식**: Session 기반 (JSESSIONID Cookie)
- **실시간 통신**: 1초 간격 HTTP Polling

### 주요 특징
- 방 생성 및 초대 코드를 통한 참여
- 실시간 채팅 기반 토론 시스템
- AI 기반 실시간 승률 분석 게이지
- 양측 합의 시 AI 최종 판결 제공

### 기술 스택
| 분류 | 기술 |
|------|------|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| DI | Hilt |
| Network | Retrofit + OkHttp |
| Serialization | kotlinx.serialization |
| Image Loading | Coil |
| Navigation | Compose Navigation |
| State Management | StateFlow + ViewModel |

---

## 서비스 기능별 코드 설명

### 1. Entry (진입 화면)
**경로**: `presentation/entry/`

사용자가 닉네임을 입력하고 방을 생성하거나 참여할 수 있는 초기 화면입니다.

| 파일 | 설명 |
|------|------|
| `screen/EntryScreen.kt` | 진입 화면 UI Composable |
| `viewmodel/EntryViewModel.kt` | 로그인, 방 생성 로직 처리 |
| `state/EntryUiState.kt` | 닉네임, 로그인 상태, 에러 등 UI 상태 |
| `component/LogoSection.kt` | 로고 표시 컴포넌트 |
| `component/NicknameInput.kt` | 닉네임 입력 필드 |

**주요 기능**:
- 닉네임 입력 및 로그인 (세션 생성)
- 새 토론방 생성
- 참여하기 화면으로 이동

---

### 2. Join (방 참여)
**경로**: `presentation/join/`

초대 코드를 입력하여 기존 토론방에 참여하는 화면입니다.

| 파일 | 설명 |
|------|------|
| `screen/JoinScreen.kt` | 참여 화면 UI Composable |
| `viewmodel/JoinViewModel.kt` | 방 참여 로직 처리 |
| `state/JoinUiState.kt` | 초대 코드 입력 상태 |
| `component/JoinInputBox.kt` | 초대 코드 입력 컴포넌트 |
| `component/ParticipantAlreadyExistsDialogComponent.kt` | 중복 참여 에러 다이얼로그 |

**주요 기능**:
- 8자리 초대 코드 입력 (XXXX-XXXX 형식)
- 방 참여 API 호출
- 에러 처리 (이미 참여 중인 사용자 등)

---

### 3. Waiting (대기실)
**경로**: `presentation/waiting/`

상대방이 입장할 때까지 대기하는 화면입니다.

| 파일 | 설명 |
|------|------|
| `screen/WaitingScreen.kt` | 대기실 UI Composable |
| `viewmodel/WaitingViewModel.kt` | 상대방 입장 감지 로직 |
| `state/WaitingUiState.kt` | 초대 코드, 대기 상태 |
| `component/InfoBanner.kt` | 방 정보 배너 |
| `component/WaitingBox.kt` | 대기 애니메이션/상태 표시 |

**주요 기능**:
- 초대 코드 표시 및 공유
- 상대방 입장 시 채팅 화면으로 자동 이동
- 폴링을 통한 상대방 입장 감지

---

### 4. Chat (토론 채팅)
**경로**: `presentation/chat/`

실제 토론이 진행되는 메인 채팅 화면입니다.

| 파일 | 설명 |
|------|------|
| `screen/ChatScreen.kt` | 채팅 화면 UI Composable |
| `viewmodel/ChatViewModel.kt` | 메시지 송수신, 상태 관리 |
| `state/ChatUiState.kt` | 메시지 목록, 승률, 다이얼로그 상태 |
| `component/ChatBubble.kt` | 채팅 말풍선 (내 메시지/상대 메시지) |
| `component/ChatInput.kt` | 메시지 입력 필드 + 전송 버튼 |
| `component/WinRateHeader.kt` | 실시간 승률 게이지 바 |
| `component/JudgeConfirmDialog.kt` | 판결 요청 확인 다이얼로그 |
| `component/JudgeAcceptanceDialog.kt` | 판결 요청 수락/거절 다이얼로그 |

**주요 기능**:
- 실시간 메시지 송수신 (1초 간격 폴링)
- AI 기반 실시간 승률 표시
- 판결(토론 종료) 요청 및 수락/거절
- 양측 합의 시 판결 화면으로 이동

---

### 5. Verdict (판결 화면)
**경로**: `presentation/verdict/`

AI가 내린 최종 판결을 표시하는 화면입니다.

| 파일 | 설명 |
|------|------|
| `screen/VerdictScreen.kt` | 판결 화면 UI Composable |
| `viewmodel/VerdictViewModel.kt` | 판결 데이터 조회 |
| `state/VerdictUiState.kt` | 판결 결과 상태 |
| `component/FinalGaugeBarComponent.kt` | 최종 점수 게이지 |
| `component/JudgmentComponent.kt` | 판결문 텍스트 표시 |

**주요 기능**:
- AI 최종 판결 조회 (승자, 점수, 판결 이유)
- 원고/피고 점수 비교 표시
- 처음으로 돌아가기

---

## 주요 코드 설명

### 1. 네트워크 레이어

#### RoomApiService.kt
**경로**: `data/api/RoomApiService.kt`

모든 REST API 엔드포인트를 정의한 Retrofit 인터페이스입니다.

```kotlin
interface RoomApiService {
    @GET("chat/poll")
    suspend fun pollChatRoom(chatRoomId: Long, lastMessageId: Long?): BaseResponse<PollResponseDto>

    @POST("login")
    suspend fun login(body: LoginRequestDto): BaseResponse<String>

    @POST("chat/room/create")
    suspend fun createChatRoom(): BaseResponse<JsonElement>

    @POST("chat/room/join")
    suspend fun joinChatRoom(body: JoinChatRoomRequestDto): BaseResponse<JsonElement>

    @POST("chat/room/{chatRoomId}/message")
    suspend fun sendMessage(chatRoomId: Long, body: SendMessageRequestDto): BaseResponse<ChatMessageDto>

    @POST("chat/room/{chatRoomId}/exit/request")
    suspend fun requestExit(chatRoomId: Long, user: Map<String, String>): BaseResponse<ExitRequestResponseDto>

    @GET("chat/room/{chatRoomId}/final-judgement")
    suspend fun getFinalJudgement(chatRoomId: Long): BaseResponse<FinalJudgementResponseDto>
}
```

#### SessionCookieJar.kt
**경로**: `data/api/SessionCookieJar.kt`

JSESSIONID 쿠키를 자동으로 저장하고 전송하는 커스텀 CookieJar입니다. OkHttp에서 세션을 유지하기 위해 사용됩니다.

---

### 2. 폴링 시스템

#### ChatRepositoryImpl.kt
**경로**: `data/repository/ChatRepositoryImpl.kt`

1초 간격 폴링을 통해 실시간으로 메시지, 승률, 방 상태를 동기화합니다.

```kotlin
class ChatRepositoryImpl @Inject constructor(
    private val roomApiService: RoomApiService
) : ChatRepository {

    private var pollingJob: Job? = null
    private var lastMessageId: Long? = null

    override fun connectToRoom(roomCode: String, userId: String, myNickname: String) {
        pollingJob = scope.launch {
            while (isActive) {
                val response = roomApiService.pollChatRoom(
                    chatRoomId = cleanCode,
                    lastMessageId = lastMessageId
                )
                // 메시지, 승률, 상태 업데이트
                delay(1000) // 1초 대기
            }
        }
    }
}
```

**핵심 동작**:
- `lastMessageId`를 추적하여 새 메시지만 가져옴 (Delta Update)
- `StateFlow`를 통해 UI에 상태 전파
- `SupervisorJob`으로 개별 폴링 실패 시에도 계속 동작

---

### 3. 네비게이션

#### Route.kt
**경로**: `presentation/navigation/Route.kt`

타입 안전한 네비게이션 경로를 정의합니다.

```kotlin
sealed class Route(val route: String) {
    data object Entry : Route("entry")
    data object Join : Route("join")

    data object Waiting : Route("waiting/{inviteCode}/{chatRoomId}/{nickname}") {
        fun createRoute(inviteCode: String, chatRoomId: Long, nickname: String) =
            "waiting/$inviteCode/$chatRoomId/${Uri.encode(nickname)}"
    }

    data object Chat : Route("chat/{roomCode}/{nickname}") {
        fun createRoute(roomCode: String, nickname: String) =
            "chat/$roomCode/${Uri.encode(nickname)}"
    }

    data object Verdict : Route("verdict/{roomCode}")
}
```

**네비게이션 플로우**:
```
Entry ─┬─→ Waiting ─→ Chat ─→ Verdict ─→ Entry
       └─→ Join ────────↗
```

---

### 4. 의존성 주입 (DI)

#### NetworkModule.kt
**경로**: `di/NetworkModule.kt`

네트워크 관련 의존성을 제공합니다.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton
    fun provideSessionCookieJar(): SessionCookieJar = SessionCookieJar()

    @Provides @Singleton
    fun provideOkHttpClient(cookieJar: SessionCookieJar): OkHttpClient =
        OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(HttpLoggingInterceptor())
            .build()

    @Provides @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://objection.malmo.io.kr/")
            .client(okHttpClient)
            .build()
}
```

#### RepositoryModule.kt
**경로**: `di/RepositoryModule.kt`

Repository 인터페이스와 구현체를 바인딩합니다.

| Interface | Implementation |
|-----------|----------------|
| AuthRepository | AuthRepositoryImpl |
| RoomRepository | RoomRepositoryImpl |
| ChatRepository | ChatRepositoryImpl |
| FinalVerdictRepository | FinalVerdictRepositoryImpl |

---

### 5. 도메인 모델

#### ChatMessage.kt
**경로**: `domain/model/ChatMessage.kt`

채팅 메시지의 핵심 도메인 모델입니다.

```kotlin
data class ChatMessage(
    val id: String,
    val roomCode: String,
    val senderId: String,
    val senderNickname: String,
    val content: String,
    val timestamp: Long,
    val isMyMessage: Boolean = false
)
```

#### ChatRoomStatus.kt
**경로**: `domain/model/ChatRoomStatus.kt`

채팅방 상태를 나타내는 Enum입니다.

| 상태 | 설명 |
|------|------|
| ALIVE | 토론 진행 중 |
| REQUEST_FINISH | 한쪽이 종료 요청 |
| REQUEST_ACCEPT | 상대방이 종료 수락 |
| DONE | 토론 종료 (판결 가능) |

---

### 6. 공통 컴포넌트

#### WinRateGaugeBar.kt
**경로**: `core/component/WinRateGaugeBar.kt`

양측 승률을 시각적으로 표시하는 게이지 바 컴포넌트입니다.

#### CourtButton.kt
**경로**: `core/component/CourtButton.kt`

앱 전체에서 사용되는 커스텀 버튼 컴포넌트입니다.

#### Resource.kt
**경로**: `core/util/Resource.kt`

비동기 작업 결과를 래핑하는 Sealed Class입니다.

```kotlin
sealed class Resource<T> {
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val message: String) : Resource<T>()
    class Loading<T> : Resource<T>()
}
```

---

## 빌드 및 실행

```bash
# 프로젝트 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 디바이스에 설치
./gradlew installDebug

# Release APK 생성
./gradlew assembleRelease
```

## SDK 설정

| 항목 | 값 |
|------|-----|
| minSdk | 24 (Android 7.0) |
| targetSdk | 36 |
| compileSdk | 36 |

---

## 프로젝트 구조

```
app/src/main/java/com/survivalcoding/ai_court/
├── AiCourtApplication.kt          # Hilt Application 진입점
├── MainActivity.kt                # Single Activity (Compose Host)
├── core/
│   ├── component/                 # 공통 UI 컴포넌트
│   └── util/                      # 유틸리티 클래스
├── data/
│   ├── api/                       # Retrofit API 인터페이스
│   ├── model/                     # DTO (Request/Response)
│   └── repository/                # Repository 구현체
├── di/                            # Hilt DI 모듈
├── domain/
│   ├── model/                     # 도메인 모델
│   └── repository/                # Repository 인터페이스
├── presentation/
│   ├── entry/                     # 진입 화면
│   ├── join/                      # 방 참여 화면
│   ├── waiting/                   # 대기실 화면
│   ├── chat/                      # 채팅 화면
│   ├── verdict/                   # 판결 화면
│   └── navigation/                # 네비게이션 설정
└── ui/theme/                      # Compose 테마 설정
```
