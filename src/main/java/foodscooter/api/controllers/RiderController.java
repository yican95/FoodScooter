package foodscooter.api.controllers;

import foodscooter.model.orders.Order;
import foodscooter.model.users.rider.FullTimeSchedule;
import foodscooter.model.users.rider.PartTimeShift;
import foodscooter.model.users.rider.RiderType;
import foodscooter.model.users.rider.SalaryInfo;
import foodscooter.model.users.rider.Rider;
import foodscooter.repositories.JdbcRidersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RiderController extends BaseController {
  private final JdbcRidersRepository riderRepository;

  @Autowired
  public RiderController(
    JdbcRidersRepository riderRepository
  ) {
    this.riderRepository = riderRepository;
  }

  @GetMapping("/riders")
  public List<Rider> getAllRiders() {
    return riderRepository.getAll();
  }

  //TODO
  @GetMapping("/rider/{drid}/riderInfo")
  public Rider getRiderInfo(@PathVariable int drid) {
    boolean isFullTime = riderRepository.checkFullTime(drid);
    boolean isPartTime = riderRepository.checkPartTime(drid);
    if (!(isFullTime ^ isPartTime)) {
      // return error
      return null;
    } else if (isFullTime) {
      return new Rider(drid, RiderType.FULL_TIME);
    } else {
      return new Rider(drid, RiderType.PART_TIME);
    }
  }

  @GetMapping("/rider/{drid}/partTimeOrders")
  public List<Order> getPartTimeRiderOrders(@PathVariable int drid) {
    return riderRepository.getPartTimeOrders(drid);
  }

  @GetMapping("/rider/{drid}/fullTimeOrders")
  public List<Order> getFullTimeRiderOrders(@PathVariable int drid) {
    FullTimeSchedule schedule = riderRepository.getFullTimeSchedule(drid);
    String dayStr = "";
    String shift1Str = "";
    String shift2Str = "";
    switch (schedule.getDayOption()) {
      case 1:
        dayStr = "(1, 2, 3, 4, 5)";
        break;
      case 2:
        dayStr = "(2, 3, 4, 5, 6)";
        break;
      case 3:
        dayStr = "(3, 4, 5, 6, 7)";
        break;
      case 4:
        dayStr = "(1, 4, 5, 6, 7)";
        break;
      case 5:
        dayStr = "(1, 2, 5, 6, 7)";
        break;
      case 6:
        dayStr = "(1, 2, 3, 6, 7)";
        break;
      case 7:
        dayStr = "(1, 2, 3, 4, 7)";
        break;
      default:
        dayStr = ""; // error
    }
    switch (schedule.getShiftOption()) {
      case 1:
        shift1Str = "BETWEEN TIME '10:00:00' AND '14:00:00'";
        shift2Str = "BETWEEN TIME '15:00:00' AND '19:00:00'";
        break;
      case 2:
        shift1Str = "BETWEEN TIME '11:00:00' AND '15:00:00'";
        shift2Str = "BETWEEN TIME '16:00:00' AND '20:00:00'";
        break;
      case 3:
        shift1Str = "BETWEEN TIME '12:00:00' AND '16:00:00'";
        shift2Str = "BETWEEN TIME '17:00:00' AND '21:00:00'";
        break;
      case 4:
        shift1Str = "BETWEEN TIME '13:00:00' AND '17:00:00'";
        shift2Str = "BETWEEN TIME '18:00:00' AND '20:00:00'";
        break;
      default:
    }
    return riderRepository.getFullTimeOrders(dayStr, shift1Str, shift2Str);
  }

  @GetMapping("/rider/{drid}/SalaryInfo")
  public SalaryInfo getRiderSalary(@PathVariable int drid) {
    int baseSalary = riderRepository.getBaseSalary(drid);
    boolean isFullTime = riderRepository.checkFullTime(drid);
    boolean isPartTime = riderRepository.checkPartTime(drid);
    if (!(isFullTime ^ isPartTime)) {
      // return error
      return null;
    } else if (isFullTime) {
      return riderRepository.getSummaryCurrentMonth(drid, baseSalary);
    } else {
      return riderRepository.getSummaryCurrentWeek(drid, baseSalary);
    }
  }

  @GetMapping("/rider/{drid}/orderSummary")
  public List<Order> getRiderSummary(@PathVariable int drid) {
    return riderRepository.getOrderSummary(drid);
  }

  @GetMapping("/rider/{drid}/acceptedOrders")
  public List<Order> getAcceptedOrders(@PathVariable int drid) {
    return riderRepository.getAcceptedOrders(drid);
  }

  @PutMapping("/rider/{drid}/acceptOrder")
  public ResponseEntity<?> acceptOrder(@PathVariable int drid, @RequestBody int oid) {
    try {
      riderRepository.acceptOrder(drid, oid);
    } catch (DataAccessException e) {
      return ResponseEntity.status(409).body(e.getMessage());
    }
    return ResponseEntity.ok(riderRepository.getAcceptedOrders(drid));
  }

  @PutMapping("/rider/{drid}/doneOrder")
  public List<Order> doneOrder(@PathVariable int drid, @RequestBody int oid) {
    riderRepository.doneOrder(drid, oid);
    return riderRepository.getOrderSummary(drid);
  }
}
