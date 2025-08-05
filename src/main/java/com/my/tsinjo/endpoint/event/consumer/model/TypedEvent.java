package com.my.tsinjo.endpoint.event.consumer.model;

import com.my.tsinjo.PojaGenerated;
import com.my.tsinjo.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
