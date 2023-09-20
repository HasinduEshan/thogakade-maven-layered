package dao.custom;

import dao.CrudDao;
import entity.OrderDetails;

import java.sql.SQLException;
import java.util.List;

public interface OrderDetailsDao extends CrudDao<OrderDetails,String> {
    List<OrderDetails> findOrderDetailByOrderId(String id) throws SQLException, ClassNotFoundException;
}
