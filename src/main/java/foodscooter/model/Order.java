package foodscooter.model;

import java.time.LocalDateTime;

public class Order {
  private int oid;
  private int customerId;
  private int riderId;

  private float totalCost;
  private float deliveryFee;
  private String paymentType;

  private String location;

  private LocalDateTime orderTime;
  private LocalDateTime departureTime;
  private LocalDateTime restaurantArrivalTime;
  private LocalDateTime restaurantDepartureTime;
  private LocalDateTime deliveryTime;

  //TODO see if need
  public Order() {

  }

  public Order(
    int oid,
    int customerId,
    int riderId,
    float totalCost,
    float deliveryFee,
    String paymentType,
    String location,
    LocalDateTime orderTime,
    LocalDateTime departureTime,
    LocalDateTime restaurantArrivalTime,
    LocalDateTime restaurantDepartureTime,
    LocalDateTime deliveryTime) {
    this.oid = oid;
    this.customerId = customerId;
    this.riderId = riderId;
    this.totalCost = totalCost;
    this.deliveryFee = deliveryFee;
    this.paymentType = paymentType;
    this.location = location;
    this.orderTime = orderTime;
    this.departureTime = departureTime;
    this.restaurantArrivalTime = restaurantArrivalTime;
    this.restaurantDepartureTime = restaurantDepartureTime;
    this.deliveryTime = deliveryTime;
  }

  public int getOid() {
    return oid;
  }

  public void setOid(int oid) {
    this.oid = oid;
  }

  public int getCustomerId() {
    return customerId;
  }

  public void setCustomerId(int customerId) {
    this.customerId = customerId;
  }

  public int getRiderId() {
    return riderId;
  }

  public void setRiderId(int riderId) {
    this.riderId = riderId;
  }

  public float getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(float totalCost) {
    this.totalCost = totalCost;
  }

  public float getDeliveryFee() {
    return deliveryFee;
  }

  public void setDeliveryFee(float deliveryFee) {
    this.deliveryFee = deliveryFee;
  }

  public String getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(String paymentType) {
    this.paymentType = paymentType;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public LocalDateTime getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(LocalDateTime orderTime) {
    this.orderTime = orderTime;
  }

  public LocalDateTime getDepartureTime() {
    return departureTime;
  }

  public void setDepartureTime(LocalDateTime departureTime) {
    this.departureTime = departureTime;
  }

  public LocalDateTime getRestaurantArrivalTime() {
    return restaurantArrivalTime;
  }

  public void setRestaurantArrivalTime(LocalDateTime restaurantArrivalTime) {
    this.restaurantArrivalTime = restaurantArrivalTime;
  }

  public LocalDateTime getRestaurantDepartureTime() {
    return restaurantDepartureTime;
  }

  public void setRestaurantDepartureTime(LocalDateTime restaurantDepartureTime) {
    this.restaurantDepartureTime = restaurantDepartureTime;
  }

  public LocalDateTime getDeliveryTime() {
    return deliveryTime;
  }

  public void setDeliveryTime(LocalDateTime deliveryTime) {
    this.deliveryTime = deliveryTime;
  }
}