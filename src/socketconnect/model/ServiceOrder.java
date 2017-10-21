package socketconnect.model;


import socketconnect.callback.OrderType;

/**
 * Created by wuzefeng on 2017/10/13.
 */

public class ServiceOrder {

    private static final String ORDER="order";

    private static final String DATA="data";

    private static final String MID="mid";


    @OrderType
    private int order;

    private String data;

    private int mid;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    

  

}
