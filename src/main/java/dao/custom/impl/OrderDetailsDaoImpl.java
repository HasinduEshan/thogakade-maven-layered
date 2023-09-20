package dao.custom.impl;

import dao.custom.OrderDetailsDao;
import entity.OrderDetails;
import util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailsDaoImpl implements OrderDetailsDao {
    @Override
    public boolean save(OrderDetails entity) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute(
                "INSERT INTO orderdetail VALUES(?,?,?,?)",
                entity.getOrderId(),
                entity.getItemCode(),
                entity.getQty(),
                entity.getUnitPrice()
        );
    }

    @Override
    public boolean update(OrderDetails entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String s) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<OrderDetails> findAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public String findLastId() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public OrderDetails find(String s) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public List<OrderDetails> findOrderDetailByOrderId(String id) throws SQLException, ClassNotFoundException {
        List<OrderDetails> list = new ArrayList<>();

        ResultSet resultSet = CrudUtil.execute(
                "SELECT * FROM orderdetail WHERE orderId=?",
                id
        );

        while (resultSet.next()) {

            list.add(new OrderDetails(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getInt(3),
                    resultSet.getDouble(4)
            ));
        }
        return list;
    }
}
