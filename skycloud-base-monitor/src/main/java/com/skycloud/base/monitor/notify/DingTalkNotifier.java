/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.skycloud.base.monitor.notify;

import java.util.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.notify.AbstractStatusChangeNotifier;
import reactor.core.publisher.Mono;

/**
 * @author
 */
public class DingTalkNotifier extends AbstractStatusChangeNotifier {

    private static final String DEFAULT_MESSAGE = "*#{instance.registration.name}* (#{instance.id}) is *#{event.statusInfo.status}**";

    private final SpelExpressionParser parser = new SpelExpressionParser();

    private RestTemplate restTemplate = new RestTemplate();

    /**
     * 可配置信息模板
     */
    private Expression message;

    @Getter
    @Setter
    private String webhookToken;

    @Getter
    @Setter
    private String atMobiles;

    @Getter
    @Setter
    private String msgtype = "markdown";

    @Getter
    @Setter
    private String title = "服务告警";

    @Getter
    @Setter
    private boolean isAtAll = false;


    public DingTalkNotifier(InstanceRepository repository) {
        super(repository);
        this.message = parser.parseExpression(DEFAULT_MESSAGE, ParserContext.TEMPLATE_EXPRESSION);
    }

    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        return Mono.fromRunnable(() -> restTemplate.postForEntity(webhookToken, createMessage(event, instance), Void.class));
    }


    private HttpEntity<Map<String, Object>> createMessage(InstanceEvent event, Instance instance) {
        Map<String, Object> messageJson = markdownMessage(this.title, this.getMessage(event, instance));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new HttpEntity<>(messageJson, headers);
    }

    public Map<String, Object> markdownMessage(String title, String text) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("msgtype", this.msgtype);
        Map<String, String> contentMap = new HashMap<>(2);
        contentMap.put("title", title);
        contentMap.put("text", text);
        map.put(this.msgtype, contentMap);

        Map<String, Object> at = new HashMap<>(2);
        at.put("atMobiles", getMobiles());
        at.put("isAtAll", isAtAll);
        map.put("at", at);
        return map;
    }

    private List<String> getMobiles() {
        List<String> mobiles = null;
        if (this.atMobiles != null && this.atMobiles.length() != 0) {
            mobiles = Arrays.asList(this.atMobiles.split(","));
        }
        return mobiles;
    }


    private String getMessage(InstanceEvent event, Instance instance) {
        Map<String, Object> root = new HashMap<>();
        root.put("event", event);
        root.put("instance", instance);
        root.put("lastStatus", getLastStatus(event.getInstance()));
        StandardEvaluationContext context = new StandardEvaluationContext(root);
        context.addPropertyAccessor(new MapAccessor());
        return message.getValue(context, String.class);
    }


    public Expression getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = this.parser.parseExpression(message, ParserContext.TEMPLATE_EXPRESSION);
    }


}
