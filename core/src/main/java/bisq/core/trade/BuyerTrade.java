/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.core.trade;

import bisq.core.btc.wallet.BtcWalletService;
import bisq.core.offer.Offer;
import bisq.core.trade.protocol.BuyerProtocol;

import bisq.network.p2p.NodeAddress;

import bisq.common.handlers.ErrorMessageHandler;
import bisq.common.handlers.ResultHandler;
import bisq.common.storage.Storage;

import org.bitcoincashj.core.Coin;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public abstract class BuyerTrade extends Trade {
    BuyerTrade(Offer offer,
               Coin tradeAmount,
               Coin txFee,
               Coin takerFee,
               boolean isCurrencyForTakerFeeBtc,
               long tradePrice,
               NodeAddress tradingPeerNodeAddress,
               @Nullable NodeAddress arbitratorNodeAddress,
               Storage<? extends TradableList> storage,
               BtcWalletService btcWalletService) {
        super(offer, tradeAmount, txFee, takerFee, isCurrencyForTakerFeeBtc, tradePrice,
                tradingPeerNodeAddress, arbitratorNodeAddress, storage, btcWalletService);
    }

    BuyerTrade(Offer offer,
               Coin txFee,
               Coin takerFee,
               boolean isCurrencyForTakerFeeBtc,
               @Nullable NodeAddress arbitratorNodeAddress,
               Storage<? extends TradableList> storage,
               BtcWalletService btcWalletService) {
        super(offer, txFee, takerFee, isCurrencyForTakerFeeBtc, arbitratorNodeAddress, storage, btcWalletService);
    }

    public void onFiatPaymentStarted(ResultHandler resultHandler, ErrorMessageHandler errorMessageHandler) {
        checkArgument(tradeProtocol instanceof BuyerProtocol, "Check failed:  tradeProtocol instanceof BuyerProtocol");
        ((BuyerProtocol) tradeProtocol).onFiatPaymentStarted(resultHandler, errorMessageHandler);
    }

    @Override
    public Coin getPayoutAmount() {
        checkNotNull(getTradeAmount(), "Invalid state: getTradeAmount() = null");

        return getOffer().getBuyerSecurityDeposit().add(getTradeAmount());
    }

}
