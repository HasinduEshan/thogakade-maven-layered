package bo;

import bo.custom.OrderBo;
import bo.custom.impl.CustomerBoImpl;
import bo.custom.impl.ItemBoImpl;
import bo.custom.impl.OrderBoImpl;

public class BoFactory {
    private static BoFactory boFactory;

    private BoFactory(){

    }

    public static BoFactory getInstance() {
        return boFactory!=null? boFactory : (boFactory = new BoFactory());
    }

    public enum BoType{
        CUSTOMER,ITEM,ORDERS,ORDER_DETAILS
    }

    public <T extends SuperBo>T getBoType(BoType type){
        switch (type){
            case CUSTOMER: return (T) new CustomerBoImpl();
            case ITEM: return (T) new ItemBoImpl();
            case ORDERS: return (T) new OrderBoImpl();
            default: return null;
        }
    }
}

