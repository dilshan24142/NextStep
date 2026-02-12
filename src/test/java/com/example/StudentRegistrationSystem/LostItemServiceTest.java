package com.example.StudentRegistrationSystem;

import com.securitygateway.nextstep.model.LostItem;
import com.securitygateway.nextstep.service.LostItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class LostItemServiceTest {

    @Autowired
    private LostItemService lostItemService;

    @Test
    void testReportLostItem() {
        LostItem item = new LostItem();
        item.setItemName("Test Laptop");
        item.setDescription("Black Dell laptop");
        item.setLocationFound("Library Building");

        LostItem savedItem = lostItemService.reportLostItem(item);

        assertNotNull(savedItem.getId());
        assertEquals("Test Laptop", savedItem.getItemName());
        assertEquals("Library Building", savedItem.getLocationFound());
    }


}