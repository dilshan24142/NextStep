package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.model.Stall;
import com.securitygateway.nextstep.repository.StallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/stalls")
public class StallController {

    @Autowired
    private StallRepository stallRepository;

    // ✅ Home page for stalls (/stalls, /stalls/, /stalls/list)
    @GetMapping({"", "/", "/list"})
    public String listStalls(Model model) {
        List<Stall> stalls = stallRepository.findAll();

        // If no stalls in database, add sample data
        if (stalls.isEmpty()) {
            initializeSampleData();
            stalls = stallRepository.findAll();
        }

        model.addAttribute("stalls", stalls);
        model.addAttribute("totalStalls", stalls.size());
        return "stalls"; // stalls.html
    }

    // ✅ Test endpoint
    @GetMapping("/test")
    @ResponseBody
    public String testEndpoint() {
        long count = stallRepository.count();
        return "StallController working! Total stalls: " + count;
    }

    // ✅ Redirect to booking form using stallId as PATH variable
    @GetMapping("/book-stall")
    public String redirectToBooking(@RequestParam("id") Long stallId) {
        return "redirect:/stalls/booking/" + stallId;
    }

    // ✅ Show booking form
    @GetMapping("/booking/{stallId}")
    public String showBookingForm(@PathVariable("stallId") Long stallId, Model model) {

        Stall stall = stallRepository.findById(stallId).orElse(null);

        if (stall == null) {
            return "redirect:/stalls/list?error=Stall+Not+Found";
        }

        model.addAttribute("stall", stall);
        return "booking-form"; // booking-form.html
    }

    // ✅ Handle booking confirmation
    @PostMapping("/booking/confirm")
    public String confirmBooking(
            @RequestParam Long stallId,
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam(required = false) String eventName,
            @RequestParam String bookingDate,
            @RequestParam String timeSlot,
            @RequestParam int hours,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String specialRequests,
            Model model) {

        Stall stall = stallRepository.findById(stallId).orElse(null);
        if (stall == null) {
            return "redirect:/stalls/list?error=Stall+Not+Found";
        }

        BigDecimal totalAmount = stall.getPricePerHour().multiply(BigDecimal.valueOf(hours));

        model.addAttribute("stall", stall);
        model.addAttribute("fullName", fullName);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("eventName", eventName);
        model.addAttribute("bookingDate", bookingDate);
        model.addAttribute("timeSlot", timeSlot);
        model.addAttribute("hours", hours);
        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("specialRequests", specialRequests);
        model.addAttribute("totalAmount", totalAmount);

        return "booking-confirmation";
    }

    // ✅ Initialize sample data
    private void initializeSampleData() {
        try {
            Stall stall1 = new Stall();
            stall1.setName("Food Stall");
            stall1.setCategory("Food");
            stall1.setDescription("Delicious snacks and beverages for all occasions");
            stall1.setLocation("Main Ground - Area A");
            stall1.setCapacity(100);
            stall1.setPricePerHour(BigDecimal.valueOf(1500.00));
            stall1.setAvailableFrom("09:00");
            stall1.setAvailableTo("21:00");
            stall1.setAvailable(true);
            stallRepository.save(stall1);

            Stall stall2 = new Stall();
            stall2.setName("Book Stall");
            stall2.setCategory("Education");
            stall2.setDescription("Academic books, novels, and stationery items");
            stall2.setLocation("Library Block - First Floor");
            stall2.setCapacity(60);
            stall2.setPricePerHour(BigDecimal.valueOf(800.00));
            stall2.setAvailableFrom("10:00");
            stall2.setAvailableTo("18:00");
            stall2.setAvailable(true);
            stallRepository.save(stall2);

            Stall stall3 = new Stall();
            stall3.setName("Tech Gadgets");
            stall3.setCategory("Technology");
            stall3.setDescription("Mobile accessories, headphones, and tech gadgets");
            stall3.setLocation("Tech Zone - Block B");
            stall3.setCapacity(50);
            stall3.setPricePerHour(BigDecimal.valueOf(1800.00));
            stall3.setAvailableFrom("10:00");
            stall3.setAvailableTo("19:00");
            stall3.setAvailable(true);
            stallRepository.save(stall3);

            String[] categories = {"Food", "Education", "Technology", "Clothing", "Arts", "Sports"};
            String[] locations = {"Main Ground", "Block A", "Block B", "Block C", "Park Area", "Auditorium"};

            for (int i = 4; i <= 10; i++) {
                Stall stall = new Stall();
                stall.setName("Stall " + i);
                stall.setCategory(categories[i % categories.length]);
                stall.setDescription("Description for stall " + i);
                stall.setLocation(locations[i % locations.length]);
                stall.setCapacity(30 + (i * 10));
                stall.setPricePerHour(BigDecimal.valueOf(500 + (i * 200)));
                stall.setAvailableFrom("09:00");
                stall.setAvailableTo("20:00");
                stall.setAvailable(true);
                stallRepository.save(stall);
            }

            System.out.println("✅ Sample stalls initialized successfully!");

        } catch (Exception e) {
            System.out.println("Error initializing sample data: " + e.getMessage());
        }
    }
}
