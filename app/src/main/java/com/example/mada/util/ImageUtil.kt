package com.example.mada.util

import android.widget.ImageView
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

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

    fun ImageCarousel.changeItemWithFade(images: MutableList<CarouselItem>, position: Int, newImageRes: Int, duration: Long = 500) {
        this.animate()
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                // 2️⃣ 이미지 교체
                images[position] = CarouselItem(newImageRes)
                this.setData(images)

                // 3️⃣ fade in
                this.animate()
                    .alpha(1f)
                    .setDuration(duration)
                    .start()
            }
            .start()
    }
}