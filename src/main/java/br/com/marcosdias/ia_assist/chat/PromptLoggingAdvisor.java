package br.com.marcosdias.ia_assist.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;

import reactor.core.publisher.Flux;

/**
 * Observabilidade do prompt final enviado ao modelo e da resposta gerada.
 *
 * Niveis de log:
 * - DEBUG: resumo compacto por chamada (conversationId, quantidade de mensagens,
 *   tamanho do prompt e da resposta em caracteres).
 * - TRACE: alem do resumo, o conteudo completo do prompt e da resposta.
 *
 * Por usar order maior que o do QuestionAnswerAdvisor (0), o prompt e visto
 * DEPOIS da augmentacao RAG, completo. No modo stream, a resposta e agregada
 * token a token e logada uma unica vez, ja completa.
 */
public class PromptLoggingAdvisor implements BaseAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(PromptLoggingAdvisor.class);

    private final int order;

    public PromptLoggingAdvisor(int order) {
        this.order = order;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        logPrompt(request);
        ChatClientResponse response = chain.nextCall(request);
        logResponse(request, response);
        return response;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        logPrompt(request);
        Flux<ChatClientResponse> responses = chain.nextStream(request);
        // agrega os tokens do stream e loga a resposta completa uma unica vez
        return new ChatClientMessageAggregator()
                .aggregateChatClientResponse(responses, aggregated -> logResponse(request, aggregated));
    }

    @Override
    public ChatClientRequest before(ChatClientRequest request, AdvisorChain chain) {
        return request;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse response, AdvisorChain chain) {
        return response;
    }

    @Override
    public int getOrder() {
        return order;
    }

    private void logPrompt(ChatClientRequest request) {
        if (!logger.isDebugEnabled()) {
            return;
        }
        Object conversationId = request.context().get(ChatMemory.CONVERSATION_ID);
        var messages = request.prompt().getInstructions();
        int totalChars = messages.stream()
                .mapToInt(m -> m.getText() == null ? 0 : m.getText().length())
                .sum();
        logger.debug("[conversationId={}] Prompt enviado ao modelo: {} mensagens, {} caracteres",
                conversationId, messages.size(), totalChars);

        if (logger.isTraceEnabled()) {
            StringBuilder prompt = new StringBuilder();
            for (Message message : messages) {
                prompt.append(System.lineSeparator())
                        .append("--- ").append(message.getMessageType()).append(" ---")
                        .append(System.lineSeparator())
                        .append(message.getText());
            }
            logger.trace("[conversationId={}] Conteudo completo do prompt:{}", conversationId, prompt);
        }
    }

    private void logResponse(ChatClientRequest request, ChatClientResponse response) {
        if (!logger.isDebugEnabled()) {
            return;
        }
        Object conversationId = request.context().get(ChatMemory.CONVERSATION_ID);
        String answer = extractText(response);
        logger.debug("[conversationId={}] Resposta recebida do modelo: {} caracteres",
                conversationId, answer.length());

        if (logger.isTraceEnabled()) {
            logger.trace("[conversationId={}] Conteudo completo da resposta:{}{}",
                    conversationId, System.lineSeparator(), answer);
        }
    }

    private String extractText(ChatClientResponse response) {
        ChatResponse chatResponse = response.chatResponse();
        if (chatResponse == null || chatResponse.getResult() == null
                || chatResponse.getResult().getOutput() == null) {
            return "(sem conteudo)";
        }
        return chatResponse.getResult().getOutput().getText();
    }
}