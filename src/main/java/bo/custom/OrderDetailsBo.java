package bo.custom;

import bo.SuperBo;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.OrderDto;
import entity.OrderDetails;

import java.sql.SQLException;
import java.util.List;

public interface OrderDetailsBo extends SuperBo {

    List<OrderDetailsDto> findOrderDetailByOrderId(String id) throws SQLException, ClassNotFoundException;

    ItemDto findItem(String itemCode) throws SQLException, ClassNotFoundException;

    List<OrderDto> findAllOrders() throws SQLException, ClassNotFoundException;

    boolean deleteOrder(String orderId) throws SQLException, ClassNotFoundException;

    CustomerDto findCustomer(String customerId) throws SQLException, ClassNotFoundException;
}
