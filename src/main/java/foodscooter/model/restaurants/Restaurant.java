package foodscooter.model.restaurants;

import java.math.BigDecimal;

public class Restaurant {
  private int id;
  private String name;
  private String description;
  private BigDecimal minimumPurchase;

  public Restaurant(int id, String name, String description, BigDecimal minimumPurchase) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.minimumPurchase = minimumPurchase;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getMinimumPurchase() {
    return minimumPurchase;
  }

  public void setMinimumPurchase(BigDecimal minimumPurchase) {
    this.minimumPurchase = minimumPurchase;
  }
}
