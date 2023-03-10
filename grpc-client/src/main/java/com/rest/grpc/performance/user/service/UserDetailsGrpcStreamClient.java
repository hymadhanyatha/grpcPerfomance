package com.rest.grpc.performance.user.service;

import com.rest.grpc.performance.user.model.UserDetails;
import com.udit.gen.proto.UserDetailsResponse;
import com.udit.gen.proto.UserDetailsRequest;
import com.udit.gen.proto.UserDetailsServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.util.HashMap;
import net.devh.boot.grpc.client.inject.GrpcClient;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created on 28/December/2020 By Author Eresh, Gorantla
 **/
@Service
public class UserDetailsGrpcStreamClient {

	@GrpcClient("UserDetailsService")
	private UserDetailsServiceGrpc.UserDetailsServiceStub stub;
	Map<String,UserDetails> myMap = new HashMap<>();

	public Flux<Object> generateUserStreamResponse(Integer range){
		myMap = new HashMap<>();
		DirectProcessor<Object> processor = DirectProcessor.create();
		StreamObserver<UserDetailsResponse> observer = new StreamObserverImpl(processor.sink());
		StreamObserver<UserDetailsRequest> inputStreamObserver = this.stub.generateRandomUserStream(observer);
		return Flux.range(1, range)
		           .map(i -> UserDetailsRequest.newBuilder()
		                                       .setCity(RandomStringUtils.randomAlphabetic(10))
		                                       .setLastName(RandomStringUtils.randomAlphabetic(10))
		                                       .setFirstName(RandomStringUtils.randomAlphabetic(10))
		                                       .build())
		           .doOnNext(inputStreamObserver::onNext)
		           .zipWith(processor, (a, b) -> b)
		           .doOnComplete(inputStreamObserver::onCompleted)
		           .subscribeOn(Schedulers.elastic());
	}

	private class StreamObserverImpl implements StreamObserver<UserDetailsResponse> {

		final FluxSink<Object> sink;

		public StreamObserverImpl(FluxSink<Object> sink) {
			this.sink = sink;
		}

		@Override
		public void onNext(UserDetailsResponse output) {
			myMap.put(output.getId(), new UserDetails(output));
			this.sink.next(myMap);
		}

		@Override
		public void onError(Throwable throwable) {
			this.sink.error(throwable);
		}

		@Override
		public void onCompleted() {
			this.sink.complete();
		}
	}
}
