package com.example.mada.util

import android.widget.ImageView

object ImageUtil {
    fun ImageView.changeImageWithFade(newImageRes: Int, duration: Long = 500) {
        this.animate()
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                // 2️⃣ 이미지 교체
                this.setImageResource(newImageRes)

                // 3️⃣ fade in
                this.animate()
                    .alpha(1f)
                    .setDuration(duration)
                    .start()
            }
            .start()
    }
}