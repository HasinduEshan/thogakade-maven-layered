package bo.custom;

import bo.SuperBo;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDto;

import java.sql.SQLException;
import java.util.List;

public interface OrderBo extends SuperBo {
    boolean saveOrder(OrderDto dto) throws SQLException, ClassNotFoundException;
    ItemDto findItem(String id) throws SQLException, ClassNotFoundException;
    CustomerDto findCustomer(String id) throws SQLException, ClassNotFoundException;
    List<ItemDto> findAllItems() throws SQLException, ClassNotFoundException;
    List<CustomerDto> findAllCustomers() throws SQLException, ClassNotFoundException;
    String generateId() throws SQLException, ClassNotFoundException;
}
