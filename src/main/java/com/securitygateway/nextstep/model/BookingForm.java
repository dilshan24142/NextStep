package com.securitygateway.nextstep.model;

import java.time.LocalDate;

public class BookingForm {
    private Long stallId;
    private String customerName;
    private String email;
    private String phone;
    private String eventName;
    private LocalDate bookingDate;
    private String timeSlot;
    private Integer hours;
    private String paymentMethod;
    private String specialRequest;

    // Constructors
    public BookingForm() {}

    public BookingForm(Long stallId, String customerName, String email, String phone) {
        this.stallId = stallId;
        this.customerName = customerName;
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters
    public Long getStallId() { return stallId; }
    public void setStallId(Long stallId) { this.stallId = stallId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public Integer getHours() { return hours; }
    public void setHours(Integer hours) { this.hours = hours; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getSpecialRequest() { return specialRequest; }
    public void setSpecialRequest(String specialRequest) { this.specialRequest = specialRequest; }

    @Override
    public String toString() {
        return "BookingForm{" +
                "stallId=" + stallId +
                ", customerName='" + customerName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", eventName='" + eventName + '\'' +
                ", bookingDate=" + bookingDate +
                ", timeSlot='" + timeSlot + '\'' +
                ", hours=" + hours +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", specialRequest='" + specialRequest + '\'' +
                '}';
    }
}