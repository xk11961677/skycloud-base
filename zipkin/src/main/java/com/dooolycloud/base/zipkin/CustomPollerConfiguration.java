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
//package com.skycloud.base.zipkin;
//
//import org.springframework.cloud.sleuth.Sampler;
//import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
//import org.springframework.cloud.sleuth.stream.StreamSpanReporter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.integration.scheduling.PollerMetadata;
//import org.springframework.scheduling.support.PeriodicTrigger;
//
///**
// *
// */
//@Configuration
//public class CustomPollerConfiguration {
//	/**
//	 * Custom poller poller metadata.
//	 *
//	 * @return the poller metadata
//	 */
//	@Bean(name = StreamSpanReporter.POLLER)
//	PollerMetadata customPoller() {
//		PollerMetadata poller = new PollerMetadata();
//		poller.setMaxMessagesPerPoll(500);
//		poller.setTrigger(new PeriodicTrigger(5000L));
//		return poller;
//	}
//
//	@Bean
//	public Sampler defaultSampler() {
//		return  new AlwaysSampler();
//	}
//}
