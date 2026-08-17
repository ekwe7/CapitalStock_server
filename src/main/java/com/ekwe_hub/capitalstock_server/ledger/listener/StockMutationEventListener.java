package com.ekwe_hub.capitalstock_server.ledger.listener;

import com.ekwe_hub.capitalstock_server.common.events.StockMutationEvent;
import com.ekwe_hub.capitalstock_server.ledger.dto.request.AppendStockMutationLedgerBlockRequest;
import com.ekwe_hub.capitalstock_server.ledger.service.CryptographicLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class StockMutationEventListener {

    private final CryptographicLedgerService cryptographicLedgerService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStockMutationEvent(StockMutationEvent event) {
        AppendStockMutationLedgerBlockRequest request = new AppendStockMutationLedgerBlockRequest(
                event.merchantId(),
                event.productId(),
                event.eventType(),
                event.quantityChange(),
                event.resultingBalance()
        );
        cryptographicLedgerService.appendInventoryMutationLedgerBlock(request);
    }
}
