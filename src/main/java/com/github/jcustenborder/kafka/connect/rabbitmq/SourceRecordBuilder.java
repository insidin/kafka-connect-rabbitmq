/**
 * Copyright © 2017 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.rabbitmq;

import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.apache.kafka.common.utils.SystemTime;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.Headers;
import org.apache.kafka.connect.source.SourceRecord;

import java.lang.reflect.InvocationTargetException;

class SourceRecordBuilder {
  final RabbitMQSourceConnectorConfig config;
  Time time = new SystemTime();
  final SourceMessageConverter messageConverter;

  SourceRecordBuilder(RabbitMQSourceConnectorConfig config) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
    this.config = config;
    String messageConverterClassName = config.messageConverter;
    this.messageConverter = messageConverterClassName == null ?
        new MessageConverter(config) :
        (SourceMessageConverter) (Class.forName(messageConverterClassName).getConstructor(RabbitMQSourceConnectorConfig.class).newInstance(config));
  }

  SourceRecord sourceRecord(String consumerTag, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) {
    Object key = this.messageConverter.key(basicProperties);
    Schema keySchema = this.messageConverter.keySchema();
    Object value = this.messageConverter.value(consumerTag, envelope, basicProperties, bytes);
    Schema valueSchema = this.messageConverter.valueSchema();
    Headers headers = this.messageConverter.headers(consumerTag, envelope, basicProperties, bytes);
    final String topic = RabbitMQSourceConnectorConfig.KAFKA_TOPIC_TEMPLATE; //this.config.kafkaTopic.execute(RabbitMQSourceConnectorConfig.KAFKA_TOPIC_TEMPLATE, value);

    return new SourceRecord(
        ImmutableMap.of("routingKey", envelope.getRoutingKey()),
        ImmutableMap.of("deliveryTag", envelope.getDeliveryTag()),
        topic,
        null,
        keySchema,
        key,
        valueSchema,
        value,
        null == basicProperties.getTimestamp() ? this.time.milliseconds() : basicProperties.getTimestamp().getTime(),
        headers
    );
  }
}
