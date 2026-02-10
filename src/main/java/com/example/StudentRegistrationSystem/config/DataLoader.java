package com.example.StudentRegistrationSystem.config;

import com.example.StudentRegistrationSystem.entity.LostItem;
import com.example.StudentRegistrationSystem.repository.LostItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataLoader {

    @Autowired
    private LostItemRepository lostItemRepository;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            // Clear existing data (optional)
            lostItemRepository.deleteAll();

            // Create and save sample lost items
            LostItem item1 = new LostItem();
            item1.setItemName("iPhone 13");
            item1.setDescription("Black iPhone with blue case");
            item1.setLocationFound("Library");
            item1.setDateFound(LocalDateTime.of(2024, 1, 10, 14, 30, 0));
            item1.setContactEmail("john@example.com");
            item1.setClaimed(false);
            item1.setCategory("Electronics");
            lostItemRepository.save(item1);

            LostItem item2 = new LostItem();
            item2.setItemName("Student ID Card");
            item2.setDescription("Name: John Doe, ID: S001");
            item2.setLocationFound("Cafeteria");
            item2.setDateFound(LocalDateTime.of(2024, 1, 12, 9, 15, 0));
            item2.setContactEmail("jane@example.com");
            item2.setClaimed(false);
            item2.setCategory("Personal");
            lostItemRepository.save(item2);

            LostItem item3 = new LostItem();
            item3.setItemName("Laptop Charger");
            item3.setDescription("Dell 65W USB-C charger");
            item3.setLocationFound("Room 205");
            item3.setDateFound(LocalDateTime.of(2024, 1, 11, 16, 45, 0));
            item3.setContactEmail("bob@example.com");
            item3.setClaimed(true);
            item3.setCategory("Electronics");
            lostItemRepository.save(item3);

            LostItem item4 = new LostItem();
            item4.setItemName("Wallet");
            item4.setDescription("Brown leather with $50 cash");
            item4.setLocationFound("Bus Stand");
            item4.setDateFound(LocalDateTime.of(2024, 1, 9, 18, 20, 0));
            item4.setContactEmail("alice@example.com");
            item4.setClaimed(false);
            item4.setCategory("Personal");
            lostItemRepository.save(item4);

            LostItem item5 = new LostItem();
            item5.setItemName("Water Bottle");
            item5.setDescription("Blue Milton 1L bottle");
            item5.setLocationFound("Gym");
            item5.setDateFound(LocalDateTime.of(2024, 1, 13, 7, 45, 0));
            item5.setContactEmail("charlie@example.com");
            item5.setClaimed(true);
            item5.setCategory("General");
            lostItemRepository.save(item5);

            System.out.println("✅ Sample data loaded: 5 lost items");
        };
    }
}