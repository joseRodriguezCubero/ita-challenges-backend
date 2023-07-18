package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface IChallengeService {

    Mono<GenericResultDto<ChallengeDto>> getChallengeById(String id);
    Mono<GenericResultDto<String>> removeResourcesByUuid(String id);
    Mono<GenericResultDto<ChallengeDto>> getAllChallenges();

    Flux<ChallengeDocument> getChallengesByLanguagesAndLevel(Set<String> languageNames, String level);
}
