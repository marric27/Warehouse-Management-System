package com.relatech.warehouse_management_system.event;

import com.relatech.warehouse_management_system.grnItem.entity.GrnItem;

public record GrnItemStateUpdatedEvent(GrnItem grnItem) {}