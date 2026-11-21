package com.bank.cards.card;

import com.bank.cards.client.Client;
import com.bank.cards.client.ClientMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(componentModel = "spring", uses = ClientMapper.class)
public interface CardMapper {

    CardDto toDto(Card card);

    List<CardDto> toDtoList(List<Card> cards);

    @Mapping(target = "id", source = "card.id")
    @Mapping(target = "client", source = "client")
    CardDetailsDto toDetailsDto(Card card, Client client);

    @Mapping(target = "maskedPan", source = "pan", qualifiedByName = "maskPan")
    @Mapping(target = "expired",   source = "validTo", qualifiedByName = "isExpired")
    @Mapping(target = "daysToExpiry", source = "validTo", qualifiedByName = "daysUntil")
    @Mapping(target = "statusDisplayName", source = "status",  qualifiedByName = "statusDisplayName")
    @Mapping(target = "validFromText",     source = "validFrom", qualifiedByName = "formatDate")
    CardListItemDto toListItemDto(Card card);

    @Named("maskPan")
    default String maskPan(String pan) {
        if (pan == null || pan.length() < 4) return "****";
        return "**** " + pan.substring(pan.length() - 4);
    }

    @Named("isExpired")
    default boolean isExpired(LocalDate validTo) {
        return validTo != null && validTo.isBefore(LocalDate.now());
    }

    @Named("daysUntil")
    default long daysUntil(LocalDate validTo) {
        if (validTo == null) return 0L;
        return ChronoUnit.DAYS.between(LocalDate.now(), validTo);
    }
    @Named("statusDisplayName")
    default String statusDisplayName(CardStatus status) {
        if (status == null) return null;
        return switch (status) {
            case NEW     -> "Новая";
            case ACTIVE  -> "Активна";
            case BLOCKED -> "Заблокирована";
            case EXPIRED -> "Истекла";
            case CLOSED  -> "Закрыта";
        };
    }

    @Named("formatDate")
    default String formatDate(LocalDate date) {
        return date == null ? null : date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    List<CardListItemDto> toListItemDtoList(List<Card> cards);
}