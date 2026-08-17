package com.ekwe_hub.capitalstock_server.procurement.dto.request;

public record ScanInwardBarcodeRequest(
    String scannedBarcode,
    int scannedQuantityIncrement
) {}
