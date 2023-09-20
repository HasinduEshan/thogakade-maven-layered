package dao;

import dao.custom.impl.CustomerDaoImpl;
import dao.custom.impl.ItemDaoImpl;
import dao.custom.impl.OrderDaoImpl;
import dao.custom.impl.OrderDetailsDaoImpl;

public class DaoFactory {
    private static DaoFactory daoFactory;

    private DaoFactory(){

    }

    public static DaoFactory getDaoFactory(){
        return daoFactory!=null? daoFactory:(daoFactory=new DaoFactory());
    }

    public enum DaoType{
        CUSTOMER,ITEM,ORDERS,ORDER_DETAILS
    }

    public <T extends SuperDao>T getDaoType(DaoType type){
        switch (type){
            case CUSTOMER: return (T) new CustomerDaoImpl();
            case ITEM: return (T) new ItemDaoImpl();
            case ORDERS: return (T) new OrderDaoImpl();
            case ORDER_DETAILS: return (T) new OrderDetailsDaoImpl();
            default:return null;
        }
    }
}
