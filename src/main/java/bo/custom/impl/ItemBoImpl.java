package bo.custom.impl;

import bo.custom.ItemBo;
import dao.DaoFactory;
import dao.custom.ItemDao;
import dto.ItemDto;
import entity.Item;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemBoImpl implements ItemBo {
    ItemDao itemDao = DaoFactory.getDaoFactory().getDaoType(DaoFactory.DaoType.ITEM);

    @Override
    public boolean saveItem(ItemDto dto) throws SQLException, ClassNotFoundException {
        Item item = new Item(
                dto.getCode(),
                dto.getDescription(),
                dto.getUnitPrice(),
                dto.getQtyOnHand()
        );
        return itemDao.save(item);
    }

    @Override
    public boolean updateItem(ItemDto dto) throws SQLException, ClassNotFoundException {
        Item item = new Item(
                dto.getCode(),
                dto.getDescription(),
                dto.getUnitPrice(),
                dto.getQtyOnHand()
        );
        return itemDao.update(item);
    }

    @Override
    public List<ItemDto> findAllItems() throws SQLException, ClassNotFoundException {
        List<ItemDto> list = new ArrayList<>();

        for (Item item:itemDao.findAll()) {
            list.add(new ItemDto(
                    item.getCode(),
                    item.getDescription(),
                    item.getUnitPrice(),
                    item.getQtyOnHand()
            ));
        }

        return list;
    }

    @Override
    public boolean deleteItem(String code) throws SQLException, ClassNotFoundException {
        return itemDao.delete(code);
    }

    @Override
    public String generateId() throws SQLException, ClassNotFoundException {
        String id = itemDao.findLastId();

        if (id!=null){
            int num = Integer.parseInt(id.split("[P]")[1]);
            num++;
            return String.format("P%03d",num);
        }
        return "P001";

    }
}
