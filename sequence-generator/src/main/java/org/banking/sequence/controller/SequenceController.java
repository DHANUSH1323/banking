package org.banking.sequence.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.banking.sequence.model.entity.Sequence;
import org.banking.sequence.service.SequenceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sequence")
public class SequenceController {

    private final SequenceService sequenceService;


    @PostMapping
    public Sequence generateAccountNumber() {
        return sequenceService.create();
    }
}