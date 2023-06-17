package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {
    Map<String, Order> orderMap=new HashMap<>();
    Map<String, DeliveryPartner> deliveryPartnerMap=new HashMap<>();
    Map<String,String> orderPartnerPairMap=new HashMap<>();
    Map<String, List<String>> deliveryPartnerToOrder=new HashMap<>();
    //    public OrderRepository(){
//        orderMap=new HashMap<>();
//        deliveryPartnerMap=new HashMap<>();
//        orderPartnerPairMap=new HashMap<>();
//        deliveryPartnerToOrder=new HashMap<>();
//    }
    public OrderRepository(){}
    public void addOrder(Order order) {
        orderMap.put(order.getId(),order);
    }

    public void addDeliveryPartner(String partnerId) {
//        String id=String.valueOf(partnerId);
        deliveryPartnerMap.put(partnerId,new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        if(orderMap.containsKey(orderId) && deliveryPartnerMap.containsKey(partnerId))
        orderPartnerPairMap.put(orderId,partnerId);
        // as the order is assigned to the partner then the number of Orders will also increased;
        // so increasing the number of the order in the deliveryPartner
        int noOfDelivery=deliveryPartnerMap.get(partnerId).getNumberOfOrders();
        deliveryPartnerMap.get(partnerId).setNumberOfOrders(noOfDelivery+1);
        //also add this order to the list of delivery-OrderList map
        // first check if the delivery partner had order already or not
        if(!deliveryPartnerToOrder.containsKey(partnerId)){
            deliveryPartnerToOrder.put(partnerId,new ArrayList<>());
        }
        deliveryPartnerToOrder.get(partnerId).add(orderId);

    }

    public Order getOrderById(String orderId) {
        return orderMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return deliveryPartnerMap.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        return deliveryPartnerToOrder.get(partnerId).size();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        List<String> orders=new ArrayList<>();
//        for(Order order:deliveryPartnerToOrder.get(partnerId)){
//            orders.add(order.toString());
//        }
        if(!deliveryPartnerToOrder.containsKey(partnerId))
            return orders;
        for(String s: deliveryPartnerToOrder.get(partnerId)){
            orders.add(s);
        }

        return orders;
    }

    public List<String> getAllOrders() {
//        return new ArrayList<>(orderMap.keySet());
        List<String> orders=new ArrayList<>();
        for(String orderId: orderMap.keySet()){
//            orders.add(orderId);
            orders.add(orderId);
        }
        return orders;
    }

    public Integer getCountOfUnassignedOrders() {
        int totalOrders=orderMap.size();
        int assignedOrders=orderPartnerPairMap.size();
        return totalOrders-assignedOrders;
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        String[] timeArray=time.trim().split(":");
        int givenTime=Integer.parseInt(timeArray[0])*60 + Integer.parseInt(timeArray[1]);
        int count=0;
        for(String order: deliveryPartnerToOrder.get(partnerId)){
            Order order1=orderMap.get(order);
            if(order1.getDeliveryTime()>givenTime)count++;
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        int time=0;
        for(String order: deliveryPartnerToOrder.get(partnerId)){
            int deliveryTimeOfOrder=orderMap.get(order).getDeliveryTime();
            time=Math.max(time,deliveryTimeOfOrder);
        }
        String mm=String.valueOf(time%60);
        String hh=String.valueOf(time/60);
        return hh+":"+mm;
    }

    public void deletePartnerById(String partnerId) {
        // first get all the order of the partner and then unassigned it
        // delete all the order by the partner id from the orderPartnerMap

        // removed partner from partnerMap
        deliveryPartnerMap.remove(partnerId);

        // removing all the order form orderPartnerMap
        for(String order: deliveryPartnerToOrder.get(partnerId)){
            orderPartnerPairMap.remove(order);
        }
        // finally removing the partnerId from the deliveryPartnerToOrderMap;
        deliveryPartnerToOrder.remove(partnerId);
    }

    public void deleteOrderById(String orderId) {
        Order order=orderMap.get(orderId);
        // now removing this order from the order map
        orderMap.remove(orderId);
        // removing order form orderPartnerPairMap;
        String partnerId=orderPartnerPairMap.get(orderId);
        orderPartnerPairMap.remove(orderId);
        // now removing from list of order assigned to partnerId
        deliveryPartnerToOrder.get(partnerId).remove(order);
        deliveryPartnerMap.get(partnerId).setNumberOfOrders(deliveryPartnerToOrder.get(partnerId).size());

    }
}
