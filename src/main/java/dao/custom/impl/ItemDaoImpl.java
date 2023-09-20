package dao.custom.impl;

import dao.custom.ItemDao;
import entity.Item;
import util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDaoImpl implements ItemDao {
    @Override
    public boolean save(Item entity) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute(
                "INSERT INTO item VALUES(?,?,?,?)",
                entity.getCode(),
                entity.getDescription(),
                entity.getUnitPrice(),
                entity.getQtyOnHand()
        );
    }

    @Override
    public boolean update(Item entity) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute(
                "UPDATE item SET description=? , unitPrice=?, qtyOnHand=? WHERE code=?",
                entity.getDescription(),
                entity.getUnitPrice(),
                entity.getQtyOnHand(),
                entity.getCode()
        );
    }

    @Override
    public boolean delete(String code) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute(
                "DELETE FROM item WHERE code=?",
                code
        );
    }

    @Override
    public List<Item> findAll() throws SQLException, ClassNotFoundException {
        List<Item> list = new ArrayList<>();

        ResultSet resultSet = CrudUtil.execute(
                "SELECT * FROM item"
        );

        while (resultSet.next()) {
            list.add(new Item(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getDouble(3),
                    resultSet.getInt(4)
            ));
        }
        return list;
    }

    @Override
    public String findLastId() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute(
                "SELECT code FROM item ORDER BY code DESC LIMIT 1"
        );

        return resultSet.next()? resultSet.getString(1):null;
    }

    @Override
    public Item find(String code) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute(
                "SELECT * FROM item WHERE code=?",
                code
        );
        return resultSet.next()? new Item(
                resultSet.getString(1),
                resultSet.getString(2),
                resultSet.getDouble(3),
                resultSet.getInt(4)
        ):null;
    }
}
