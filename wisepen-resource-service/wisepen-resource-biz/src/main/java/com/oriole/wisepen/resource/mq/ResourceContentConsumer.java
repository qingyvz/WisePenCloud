package com.oriole.wisepen.resource.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oriole.wisepen.document.api.domain.mq.DocumentReadyMessage;
import com.oriole.wisepen.note.api.domain.mq.NoteSnapshotMessage;
import com.oriole.wisepen.resource.service.ISearchSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.oriole.wisepen.document.api.constant.MqTopicConstants.TOPIC_DOCUMENT_READY;
import static com.oriole.wisepen.note.api.constant.MqTopicConstants.TOPIC_NOTE_SNAPSHOT;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceContentConsumer {

    private final ISearchSyncService searchSyncService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = TOPIC_DOCUMENT_READY, groupId = "wisepen-document-ready-group",
            properties = {"value.deserializer=org.apache.kafka.common.serialization.StringDeserializer"})
    public void onDocumentReady(String payload) throws JsonProcessingException {
        DocumentReadyMessage message = objectMapper.readValue(payload, DocumentReadyMessage.class);
        log.info("documentReady received topic={} resourceId={} contentLength={}",
                TOPIC_DOCUMENT_READY, message.getResourceId(), message.getContent() != null ? message.getContent().length() : 0);
        try {
            searchSyncService.syncResourceContent(message.getResourceId(), message.getContent());
        } catch (Exception e) {
            log.error("documentReady consume failed topic={} resourceId={}", TOPIC_DOCUMENT_READY, message.getResourceId(), e);
        }
    }

    @KafkaListener(topics = TOPIC_NOTE_SNAPSHOT, groupId = "wisepen-note-snapshot-group",
            properties = {"value.deserializer=org.apache.kafka.common.serialization.StringDeserializer"})
    public void onNoteSnapshot(String payload) throws JsonProcessingException {
        NoteSnapshotMessage message = objectMapper.readValue(payload, NoteSnapshotMessage.class);
        log.info("noteSnapshot received topic={} resourceId={} contentLength={}",
                TOPIC_NOTE_SNAPSHOT, message.getResourceId(), message.getPlainText() != null ? message.getPlainText().length() : 0);

        try {
            if ("FULL".equals(message.getType())) {
                searchSyncService.syncResourceContent(message.getResourceId(), message.getPlainText());
            }
        } catch (Exception e) {
            log.error("noteSnapshot consume failed topic={} resourceId={}", TOPIC_NOTE_SNAPSHOT, message.getResourceId(), e);
        }
    }
}
