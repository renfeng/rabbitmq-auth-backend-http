/**
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License
 * at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * The Original Code is RabbitMQ HTTP authentication.
 *
 * The Initial Developer of the Original Code is VMware, Inc.
 * Copyright (c) 2018 Pivotal Software, Inc.  All rights reserved.
 */
package com.rabbitmq.examples

import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@WebMvcTest
class AuthApiTest(@Autowired val mockMvc: MockMvc) {

    // https://lists.rabbitmq.com/pipermail/rabbitmq-discuss/2010-April/006844.html
    private var length = kotlin.random.Random.nextInt(1, 3800 - 23) / 2
    private var username = RandomStringUtils.randomAlphanumeric(length)
    private var password = username

    // user
    @Test
    fun `Check authentication for external users with GET`() {
        mockMvc.perform(get("/auth/user")
                .param("username", username)
                .param("password", password))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("allow administrator management"))

    }

    @Test
    fun `Check deny for external users with GET`() {
        mockMvc.perform(get("/auth/user")
                .param("username", "guest")
                .param("password", "guest"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("deny"))
    }

    @Test
    fun `Check authentication for external users with POST`() {
        mockMvc.perform(post("/auth/user").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("username=$username&password=$password"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("allow administrator management"))
    }

    // vhost
    @Test
    fun `Check vhost for external users with GET`() {
        mockMvc.perform(get("/auth/vhost")
                .param("username", username)
                .param("vhost", "guest"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("allow"))
    }

    @Test
    fun `Check vhost for external users with POST`() {
        mockMvc.perform(post("/auth/vhost").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("username=$username&vhost=guest"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("allow"))
    }

    // resource
    @Test
    fun `Check resource_path for external users with GET`() {
        mockMvc.perform(get("/auth/resource")
                .param("username", username)
                .param("vhost", "guest")
                .param("resource", "exchange")
                .param("name", "amq.topic")
                .param("permission", "write"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("allow"))
    }

    @Test
    fun `Check resource_path for external users with POST`() {
        mockMvc.perform(post("/auth/resource").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("username=$username&vhost=guest&resource=exchange&name=amq.topic&permission=write"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("allow"))
    }

    // topic
    @Test
    fun `Check topic for external users with GET`() {
        mockMvc.perform(get("/auth/topic")
                .param("username", username)
                .param("vhost", "guest")
                .param("resource", "exchange")
                .param("name", "amq.topic")
                .param("routing_key", "a.b")
                .param("permission", "write"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("allow"))
    }

    @Test
    fun `Check topic for external users with POST`() {
        mockMvc.perform(post("/auth/topic").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("username=$username&vhost=guest&resource=exchange&name=amq.topic&permission=write&routing_key=a.b"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("allow"))
    }

    @Test
    fun `Check deny topic for external users with GET`() {
        mockMvc.perform(get("/auth/topic")
                .param("username", username)
                .param("vhost", "guest")
                .param("resource", "exchange")
                .param("name", "amq.topic")
                .param("routing_key", "b.b")
                .param("permission", "write"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("deny"))
    }
}