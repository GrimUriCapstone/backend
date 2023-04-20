package grimuri.backend.domain.diary.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import grimuri.backend.domain.diary.dto.DiaryMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SqsSenderService {

    @Value("${cloud.aws.sqs.endpoint}")
    private String sqsEndpoint;

    private final ObjectMapper objectMapper;
    private final AmazonSQS amazonSQS;

    /**
     * Amazon SQS에 파라미터로 넘어온 DiaryMessageDto.Generate message를 JSON 형태로 저장한다.
     * @param message diaryId, Diary Original Content, User Email
     * @return SendMessageResult
     * @throws JsonProcessingException
     */
    public SendMessageResult sendMessage(DiaryMessageDto.Generate message) throws JsonProcessingException {
        log.debug("\tGenerate Message: {}", message.toString());
        SendMessageRequest sendMessageRequest = new SendMessageRequest(sqsEndpoint, objectMapper.writeValueAsString(message));

        return amazonSQS.sendMessage(sendMessageRequest);
    }
}
