package org.banking.sequence.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.banking.sequence.model.entity.Sequence;
import org.banking.sequence.repository.SequenceRepository;
import org.banking.sequence.service.SequenceService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SequenceServiceImpl implements SequenceService {

    private final SequenceRepository sequenceRepository;

    @Override
    public Sequence create() {

        log.info("creating a account number");
        return sequenceRepository.findById(1L)
                .map(sequence -> {
                    sequence.setAccountNumber(sequence.getAccountNumber() + 1);
                    return sequenceRepository.save(sequence);
                }).orElseGet(() -> sequenceRepository.save(Sequence.builder().accountNumber(1L).build()));
    }
}