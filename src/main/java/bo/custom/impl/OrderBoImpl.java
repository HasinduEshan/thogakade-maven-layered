package bo.custom.impl;

import bo.custom.OrderBo;
import dao.DaoFactory;
import dao.custom.CustomerDao;
import dao.custom.ItemDao;
import dao.custom.OrderDao;
import dao.custom.OrderDetailsDao;
import db.DBConnection;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.OrderDto;
import entity.Customer;
import entity.Item;
import entity.Order;
import entity.OrderDetails;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderBoImpl implements OrderBo {
    OrderDao orderDao = DaoFactory.getDaoFactory().getDaoType(DaoFactory.DaoType.ORDERS);
    OrderDetailsDao orderDetailsDao = DaoFactory.getDaoFactory().getDaoType(DaoFactory.DaoType.ORDER_DETAILS);
    ItemDao itemDao = DaoFactory.getDaoFactory().getDaoType(DaoFactory.DaoType.ITEM);
    CustomerDao customerDao = DaoFactory.getDaoFactory().getDaoType(DaoFactory.DaoType.CUSTOMER);

    @Override
    public boolean saveOrder(OrderDto dto) throws SQLException, ClassNotFoundException {
        DBConnection.getInstance().getConnection().setAutoCommit(false);

        Order order = new Order(
                dto.getId(),
                dto.getDate(),
                dto.getCustomerId()
        );

        boolean orderPlaced = orderDao.save(order);

        boolean isOrderPlaced = true;
        if (orderPlaced) {
            for (OrderDetailsDto detail:dto.getDetails()) {

                boolean detailSaved = orderDetailsDao.save(new OrderDetails(
                        detail.getOrderId(),
                        detail.getItemCode(),
                        detail.getQty(),
                        detail.getUnitPrice()
                ));

                if (!detailSaved){
                    isOrderPlaced = false;
                }

            }
        }else{
            isOrderPlaced = false;
        }

        if (isOrderPlaced){
//            new Alert(Alert.AlertType.INFORMATION,"Order Placed..!").show();
            DBConnection.getInstance().getConnection().commit();
//            tmList.clear();
//            tblOrder.refresh();
//            clearFields();
            return true;
        }else{
//            new Alert(Alert.AlertType.ERROR,"Something went wrong..!").show();
            DBConnection.getInstance().getConnection().rollback();
            return false;
        }
    }

    @Override
    public ItemDto findItem(String id) throws SQLException, ClassNotFoundException {
        Item item = itemDao.find(id);
        return new ItemDto(
                item.getCode(),
                item.getDescription(),
                item.getUnitPrice(),
                item.getQtyOnHand()
        );
    }

    @Override
    public CustomerDto findCustomer(String id) throws SQLException, ClassNotFoundException {
        Customer customer = customerDao.find(id);
        return new CustomerDto(
                customer.getId(),
                customer.getName(),
                customer.getAddress(),
                customer.getSalary()
        );
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
    public List<CustomerDto> findAllCustomers() throws SQLException, ClassNotFoundException {
        List<CustomerDto> list = new ArrayList<>();

        for (Customer customer:customerDao.findAll()) {
            list.add(new CustomerDto(
                    customer.getId(),
                    customer.getName(),
                    customer.getAddress(),
                    customer.getSalary()
            ));
        }

        return list;
    }

    @Override
    public String generateId() throws SQLException, ClassNotFoundException {
        String id = orderDao.findLastId();

        if (id!=null){
            int num = Integer.parseInt(id.split("[D]")[1]);
            num++;
            return String.format("D%03d",num);
        }else {
            return "D001";
        }
    }
}
