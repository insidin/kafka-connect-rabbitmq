/**
 * Copyright © 2020 Jan Uyttenhove (jan@insidin.com)
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

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.Headers;

public interface SourceMessageConverter {

  Object value(String consumerTag, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] body);

  Schema valueSchema();

  Object key(AMQP.BasicProperties basicProperties);

  Schema keySchema();

  Headers headers(String consumerTag, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] body);

}
