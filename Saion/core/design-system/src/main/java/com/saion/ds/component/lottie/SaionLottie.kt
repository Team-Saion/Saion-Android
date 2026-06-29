package com.saion.ds.component.lottie

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.saion.core.designsystem.R
import com.saion.ds.theme.SaionTheme

/**
 * 디자인 시스템에서 사용할 로티 애니메이션 소스를 표현합니다.
 *
 * Lottie 라이브러리의 `LottieCompositionSpec`를 외부로 노출하지 않고,
 * 디자인 시스템 계층에서 필요한 입력 형태만 제공합니다.
 */
@Immutable
sealed interface SaionLottieSource {
    /** `res/raw` 아래에 있는 로티 JSON 리소스를 사용합니다. */
    @Immutable
    data class RawRes(val resId: Int) : SaionLottieSource

    /** `assets` 경로에 있는 로티 파일을 사용합니다. */
    @Immutable
    data class Asset(val assetName: String) : SaionLottieSource
}

/**
 * 디자인 시스템 기본값으로 로티 애니메이션을 렌더링합니다.
 *
 * 예시:
 * ```kotlin
 * SaionLottie(
 *     source = SaionLottieSource.RawRes(R.raw.loading_animation_light),
 * )
 * ```
 *
 * @param source 애니메이션을 불러올 로티 소스입니다.
 * @param modifier 컴포저블에 적용할 Compose [Modifier]입니다.
 * @param isPlaying `true`이면 애니메이션을 재생하고 `false`이면 현재 프레임에서 멈춥니다.
 * @param restartOnPlay 재생을 다시 시작할 때 처음 프레임부터 재생할지 여부입니다.
 * @param iterations 애니메이션 반복 횟수입니다. 기본값은 [Int.MAX_VALUE]입니다.
 * @param speed 재생 속도 배수입니다. `1f`가 기본 속도입니다.
 * @param alignment 애니메이션 콘텐츠를 배치할 정렬 기준입니다.
 * @param contentScale 애니메이션 콘텐츠를 주어진 영역에 맞출 방식입니다.
 */
@Composable
fun SaionLottie(
    source: SaionLottieSource,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    restartOnPlay: Boolean = true,
    iterations: Int = Int.MAX_VALUE,
    speed: Float = 1f,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val composition by rememberLottieComposition(spec = source.toCompositionSpec())

    LottieAnimation(
        composition = composition,
        modifier = modifier,
        isPlaying = isPlaying,
        restartOnPlay = restartOnPlay,
        speed = speed,
        iterations = iterations,
        alignment = alignment,
        contentScale = contentScale,
    )
}

private fun SaionLottieSource.toCompositionSpec(): LottieCompositionSpec = when (this) {
    is SaionLottieSource.Asset -> LottieCompositionSpec.Asset(assetName = assetName)
    is SaionLottieSource.RawRes -> LottieCompositionSpec.RawRes(resId = resId)
}

@Preview(showBackground = true)
@Composable
private fun SaionLottiePreview() {
    SaionTheme {
        Box(
            modifier = Modifier
                .background(color = SaionTheme.colors.background.default)
                .padding(12.dp),
        ) {
            SaionLottie(
                source = SaionLottieSource.RawRes(resId = R.raw.loading_animation_dark),
            )
        }
    }
}
