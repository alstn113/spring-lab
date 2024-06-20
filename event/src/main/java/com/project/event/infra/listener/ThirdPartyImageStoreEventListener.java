package com.project.event.infra.listener;

import com.project.event.domain.PostDeletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ThirdPartyImageStoreEventListener {

    @Async
    @EventListener
    public void deleteImageFile(PostDeletedEvent event) {
        String imageUrl = event.post().getImageUrl();

        // Call third party image store API
        System.out.printf("Delete image file(%s) in third party image store%n", imageUrl);
    }
}
