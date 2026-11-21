package com.bank.cards.card;

import com.bank.cards.client.ClientCardsReportDto;
import com.bank.cards.client.ClientNotFoundException;
import com.bank.cards.client.ClientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final ClientRepository clientRepository;

    public CardService(CardRepository cardRepository, CardMapper cardMapper, ClientRepository clientRepository) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
        this.clientRepository = clientRepository;
    }

    public CardDto getCard(long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        return cardMapper.toDto(card);
    }

    public List<CardDto> getCardsByClient(long clientId) {
        return getCardsByClient(clientId, null);
    }

    public List<CardDto> getCardsByClient(long clientId, CardStatus status) {
        List<Card> cards = (status == null)
                ? cardRepository.findByClientIdOrderByIdAsc(clientId)
                : cardRepository.findByClientIdAndStatusOrderByIdAsc(clientId, status);
        return cardMapper.toDtoList(cards);
    }

    public CardDetailsDto getCardDetails(long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        var client = clientRepository.findById(card.getClientId())
                .orElseThrow(() -> new CardNotFoundException(id));
        return cardMapper.toDetailsDto(card, client);
    }

    public List<CardDto> getAllCards() {
        return cardMapper.toDtoList(cardRepository.findAllByOrderByCreatedAtDescIdDesc());
    }

    public CardListItemDto getCardSummary(long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        return cardMapper.toListItemDto(card);
    }

    public ClientCardsReportDto getCardsReport(long clientId) {
        var client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
        List<CardListItemDto> cards = cardMapper.toListItemDtoList(
                cardRepository.findByClientIdOrderByIdAsc(clientId));
        int totalActive = (int) cards.stream()
                .filter(card -> card.getStatus() == CardStatus.ACTIVE)
                .count();
        return new ClientCardsReportDto(clientId, client.getFullName(), cards, totalActive);
    }

    public CardDto createCard(CreateCardRequest request) {
        Card card = new Card();
        card.setClientId(request.getClientId());
        card.setPan(request.getPan());
        card.setCardHolderName(request.getCardHolderName());
        card.setCardType(request.getCardType());
        card.setPaymentSystem(request.getPaymentSystem());
        card.setValidFrom(request.getValidFrom());
        card.setValidTo(request.getValidTo());
        card.setProductName(request.getProductName());
        card.setStatus(CardStatus.NEW);
        card.setCreatedAt(LocalDateTime.now());
        return cardMapper.toDto(cardRepository.save(card));
    }

    public CardDto updateCard(long id, UpdateCardRequest request) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        card.setProductName(request.getProductName());
        card.setValidTo(request.getValidTo());
        return cardMapper.toDto(cardRepository.save(card));
    }

    public void closeCard(long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        if (card.getStatus() == CardStatus.CLOSED) {
            throw new CardOperationException("Карта " + id + " уже закрыта");
        }
        card.setStatus(CardStatus.CLOSED);
        cardRepository.save(card);
    }
}
