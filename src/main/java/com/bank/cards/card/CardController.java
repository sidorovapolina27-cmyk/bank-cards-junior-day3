package com.bank.cards.card;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/api/cards")
    public List<CardDto> getAllCards() {
        return cardService.getAllCards();
    }

    @GetMapping("/api/cards/{id}")
    public CardDto getCard(@PathVariable long id) {
        return cardService.getCard(id);
    }

    @GetMapping("/api/clients/{clientId}/cards")
    public List<CardDto> getClientCards(@PathVariable long clientId,
                                        @RequestParam(required = false) CardStatus status) {
        return cardService.getCardsByClient(clientId, status);
    }

    @PostMapping("/api/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public CardDto createCard(@RequestBody CreateCardRequest request) {
        return cardService.createCard(request);
    }

    @PutMapping("/api/cards/{id}")
    public CardDto updateCard(@PathVariable long id, @RequestBody UpdateCardRequest request) {
        return cardService.updateCard(id, request);
    }

    @DeleteMapping("/api/cards/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeCard(@PathVariable long id) {
        cardService.closeCard(id);
    }
}
