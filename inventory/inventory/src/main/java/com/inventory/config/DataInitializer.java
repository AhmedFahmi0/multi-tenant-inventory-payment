package com.inventory.config;

import com.inventory.model.Dealer;
import com.inventory.model.Dealer.SubscriptionType;
import com.inventory.model.User;
import com.inventory.model.User.Role;
import com.inventory.repository.DealerRepository;
import com.inventory.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class DataInitializer {

    private final UserRepository userRepository;
    private final DealerRepository dealerRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // Create admin user
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.GLOBAL_ADMIN)
                    .build();
            userRepository.save(admin);

            // Create test dealer
            Dealer dealer = Dealer.builder()
                    .name("Premium Auto Dealer")
                    .email("premium@example.com")
                    .subscriptionType(SubscriptionType.PREMIUM)
                    .tenantId("tenant1")
                    .build();
            dealerRepository.save(dealer);
        }
    }
}
