package com.rest.grpc.performance.user.service;

import com.rest.grpc.performance.user.model.UserDetails;
import com.udit.gen.proto.UserDetailsRequest;
import com.udit.gen.proto.UserDetailsResponse;
import com.udit.gen.proto.UserDetailsServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.HashMap;

import net.devh.boot.grpc.client.inject.GrpcClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created on 28/December/2020 By Author Eresh, Gorantla
 **/
@Service
public class UserDetailsGrpcBlockingClient {

	@GrpcClient("UserDetailsService")
	UserDetailsServiceGrpc.UserDetailsServiceBlockingStub userDetailsServiceBlockingStub;

	public Flux<Object> getUserDetailsResponse(Integer range) {
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9002).usePlaintext().build();
		this.userDetailsServiceBlockingStub = UserDetailsServiceGrpc.newBlockingStub(managedChannel);
		Map<String,UserDetails> myMap = new HashMap<>();
		return
				Flux.range(1, range)
				    .map(i -> UserDetailsRequest.newBuilder()
				                                .setCity(RandomStringUtils.randomAlphabetic(10))
				                                .setLastName(RandomStringUtils.randomAlphabetic(10))
				                                .setFirstName(RandomStringUtils.randomAlphabetic(10))
				                                .build())
				    .map(i -> {
					    UserDetailsResponse output = this.userDetailsServiceBlockingStub.generateRandomUser(i);
						myMap.put(output.getId(), new UserDetails(output));
					    return (Object) myMap ;
				    })
				    .subscribeOn(Schedulers.elastic());
	}
}
