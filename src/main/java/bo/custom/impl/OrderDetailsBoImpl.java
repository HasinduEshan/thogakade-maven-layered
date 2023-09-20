package bo.custom.impl;

import bo.custom.OrderDetailsBo;
import dao.DaoFactory;
import dao.custom.CustomerDao;
import dao.custom.ItemDao;
import dao.custom.OrderDao;
import dao.custom.OrderDetailsDao;
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

public class OrderDetailsBoImpl implements OrderDetailsBo {
    OrderDetailsDao orderDetailsDao = DaoFactory.getDaoFactory().getDaoType(DaoFactory.DaoType.ORDER_DETAILS);
    ItemDao itemDao = DaoFactory.getDaoFactory().getDaoType(DaoFactory.DaoType.ITEM);
    OrderDao orderDao = DaoFactory.getDaoFactory().getDaoType(DaoFactory.DaoType.ORDERS);
    CustomerDao customerDao = DaoFactory.getDaoFactory().getDaoType(DaoFactory.DaoType.CUSTOMER);

    @Override
    public List<OrderDetailsDto> findOrderDetailByOrderId(String id) throws SQLException, ClassNotFoundException {
        List<OrderDetailsDto> list = new ArrayList<>();
        for (OrderDetails detail:orderDetailsDao.findOrderDetailByOrderId(id)) {
            list.add(new OrderDetailsDto(
                    detail.getOrderId(),
                    detail.getItemCode(),
                    detail.getQty(),
                    detail.getUnitPrice()
            ));
        }
        return list;
    }

    @Override
    public ItemDto findItem(String itemCode) throws SQLException, ClassNotFoundException {
        Item item = itemDao.find(itemCode);
        return new ItemDto(
                item.getCode(),
                item.getDescription(),
                item.getUnitPrice(),
                item.getQtyOnHand()
        );
    }

    @Override
    public List<OrderDto> findAllOrders() throws SQLException, ClassNotFoundException {
        List<OrderDto> list = new ArrayList<>();
        List<OrderDetailsDto> details = new ArrayList<>();

        for (Order order:orderDao.findAll()) {
            for (OrderDetails orderDetail:orderDetailsDao.findOrderDetailByOrderId(order.getId())) {
                details.add(new OrderDetailsDto(
                        orderDetail.getOrderId(),
                        orderDetail.getItemCode(),
                        orderDetail.getQty(),
                        orderDetail.getUnitPrice()
                ));
            }

            list.add(new OrderDto(
                    order.getId(),
                    order.getDate(),
                    order.getCustomerId(),
                    details
            ));
        }
        return list;
    }

    @Override
    public boolean deleteOrder(String orderId) throws SQLException, ClassNotFoundException {
        return orderDao.delete(orderId);
    }

    @Override
    public CustomerDto findCustomer(String customerId) throws SQLException, ClassNotFoundException {
        Customer customer = customerDao.find(customerId);
        return new CustomerDto(
                customer.getId(),
                customer.getName(),
                customer.getAddress(),
                customer.getSalary()
        );
    }
}
