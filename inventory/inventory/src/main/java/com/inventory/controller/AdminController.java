package com.inventory.controller;

import com.inventory.model.Dealer;
import com.inventory.repository.DealerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DealerRepository dealerRepository;

    @GetMapping("/dealers/countBySubscription")
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    public ResponseEntity<Map<Dealer.SubscriptionType, Long>> countDealersBySubscription() {
        Map<Dealer.SubscriptionType, Long> counts = new HashMap<>();
        
        long basicCount = dealerRepository.countBySubscriptionType(Dealer.SubscriptionType.BASIC);
        long premiumCount = dealerRepository.countBySubscriptionType(Dealer.SubscriptionType.PREMIUM);
        
        counts.put(Dealer.SubscriptionType.BASIC, basicCount);
        counts.put(Dealer.SubscriptionType.PREMIUM, premiumCount);
        
        return ResponseEntity.ok(counts);
    }
}
