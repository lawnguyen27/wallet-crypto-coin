package vn.sparkminds.template.service;

import vn.sparkminds.template.entity.Coin;
import vn.sparkminds.template.entity.Network;

public interface NetworkService {
    Network resolveNetworkByCoin(Coin coin);
}
