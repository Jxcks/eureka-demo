package com.xxxx.service.impl;

import com.xxxx.pojo.Order;
import com.xxxx.pojo.Product;
import com.xxxx.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Override
    public Order selectOrderById(Integer id) {
        return new Order(id, "order-001", "上海", 34212D, selectProductListByLoadBalancerClient());
    }

    private List<Product> selectProductListByDiscoveryClient() {
        StringBuffer sb = new StringBuffer();

        // 获取服务列表
        List<String> serviceIds = discoveryClient.getServices();
        if (CollectionUtils.isEmpty(serviceIds)) {
            return null;
        }

        // 根据服务名获取服务
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances("service-provider");
        if (CollectionUtils.isEmpty(serviceInstances)) {
            return null;
        }

        ServiceInstance si = serviceInstances.get(0);
        sb.append("http://" + si.getHost() + ":" + si.getPort() + "/product/list");
        System.out.println(sb.toString());

        // ResponseEntity：封装了返回数据
        ResponseEntity<List<Product>> responseEntity = restTemplate.exchange(
                sb.toString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {}
        );

        return responseEntity.getBody();
    }

    private List<Product> selectProductListByLoadBalancerClient() {
        StringBuffer sb = new StringBuffer();

        // 根据服务名获取服务
        ServiceInstance si = loadBalancerClient.choose("service-provider");
        if (null == si) {
            return null;
        }

        sb.append("http://" + si.getHost() + ":" + si.getPort() + "/product/list");
        System.out.println(sb.toString());

        // ResponseEntity：封装了返回数据
        ResponseEntity<List<Product>> responseEntity = restTemplate.exchange(
                sb.toString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {}
        );

        return responseEntity.getBody();
    }

    private List<Product> selectProductListByLoadBalancedAnnotation() {
        String url = "http://service-provider/product/list";
        // ResponseEntity：封装了返回数据
        ResponseEntity<List<Product>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {}
        );

        return responseEntity.getBody();
    }
}
