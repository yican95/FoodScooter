package foodscooter.repositories;

import foodscooter.model.orders.CustomerOrderOptions;
import foodscooter.model.users.customer.CustomerProfile;
import foodscooter.repositories.specifications.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcCustomersRepository implements CustomersRepository {
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public JdbcCustomersRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public CustomerProfile getProfile(int customerId) {
    List<String> recentDeliveryLocations = jdbcTemplate.query(
      "SELECT deliverylocation "
      + "FROM CustomerRecentDeliveryLocations "
      + "WHERE cid = ?;",
      new Object[]{ customerId },
      (rs, rowNum) -> rs.getString(1)
    );

    return jdbcTemplate.queryForObject(
      "SELECT U.username, U.password, C.creditcardnumber, C.rewardpoints "
      + "FROM Customers C JOIN Users U ON C.cid = U.uid "
      + "WHERE cid = ?;",
      new Object[] { customerId },
      (((rs, rowNum) -> new CustomerProfile(
          rs.getString(1),
          rs.getString(2),
          rs.getString(3),
          rs.getInt(4),
          recentDeliveryLocations)
      ))
    );
  }

  @Override
  public CustomerOrderOptions getOrderOptions(int customerId) {
    Optional<Integer> rewardPoints = jdbcTemplate.queryForObject(
      "SELECT rewardpoints "
      + "FROM Customers "
      + "WHERE cid = ?;",
      new Object[] { customerId },
      (rs, rowNum) -> Optional.of(rs.getInt(1)));

    List<String> recentDeliveryLocations = jdbcTemplate.query(
      "SELECT deliverylocation "
      + "FROM CustomerRecentDeliveryLocations "
      + "WHERE cid = ?;",
      new Object[] { customerId },
      (rs, rowNum) -> rs.getString(1)
    );

    return new CustomerOrderOptions(rewardPoints.get(), recentDeliveryLocations);
  }

  @Override
  public void putProfile(int customerId, CustomerProfile profile) {
    jdbcTemplate.update(
      "UPDATE Users "
      + "SET username = ?,"
      + "password = ? "
      + "WHERE uid = ?;"
      + "UPDATE Customers "
      + "SET creditcardnumber = ? "
      + "WHERE cid = ?",
      profile.getUsername(),
      profile.getPassword(),
      customerId,
      profile.getCreditCardNumber(),
      customerId);
  }
}
