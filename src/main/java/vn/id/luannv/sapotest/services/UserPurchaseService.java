package vn.id.luannv.sapotest.services;

import vn.id.luannv.sapotest.dto.request.UserPurchaseRequest;

public interface UserPurchaseService {
    void order(UserPurchaseRequest userPurchaseRequest);
}
