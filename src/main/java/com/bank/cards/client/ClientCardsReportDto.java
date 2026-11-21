package com.bank.cards.client;

import com.bank.cards.card.CardListItemDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ClientCardsReportDto {

    private final Long clientId;
    private final String clientName;
    private final List<CardListItemDto> cards;
    private final int totalActive;
}
