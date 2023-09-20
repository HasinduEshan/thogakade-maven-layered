package dao.custom.impl;

import dao.custom.CustomerDao;
import entity.Customer;
import util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoImpl implements CustomerDao {
    @Override
    public boolean save(Customer entity) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute(
                "INSERT INTO customer VALUES(?,?,?,?)",
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getSalary()
        );
    }

    @Override
    public boolean update(Customer entity) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute(
                "UPDATE customer SET name=? , address=?, salary=? WHERE id=?",
                entity.getName(),
                entity.getAddress(),
                entity.getSalary(),
                entity.getId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute(
                "DELETE FROM customer WHERE id=?",
                id
        );
    }

    @Override
    public List<Customer> findAll() throws SQLException, ClassNotFoundException {
        ArrayList<Customer> list = new ArrayList<>();

        ResultSet resultSet = CrudUtil.execute(
                "SELECT * FROM customer"
        );

        while (resultSet.next()) {
            list.add(new Customer(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getDouble(4)
            ));
        }

        return list;
    }

    @Override
    public String findLastId() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute(
                "SELECT id FROM customer ORDER BY id DESC LIMIT 1"
        );
        return resultSet.next()? resultSet.getString(1):null;
    }

    @Override
    public Customer find(String id) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute(
                "SELECT * FROM customer WHERE id=?",
                id
        );
        return resultSet.next()? new Customer(
                resultSet.getString(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getDouble(4)
        ):null;
    }
}
