package no.nav.testnav.apps.brukerservice.service.v2;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.brukerservice.domain.User;
import no.nav.testnav.apps.brukerservice.dto.BrukerDTO;
import no.nav.testnav.apps.brukerservice.exception.UserAlreadyExistsException;
import no.nav.testnav.apps.brukerservice.repository.UserEntity;
import no.nav.testnav.apps.brukerservice.repository.UserRepository;
import no.nav.testnav.apps.brukerservice.service.v1.CryptographyService;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service("userServiceV2")
@RequiredArgsConstructor
public class UserService {
    private final CryptographyService cryptographyService;
    private final UserRepository repository;
    private final GetAuthenticatedUserId getAuthenticatedUserId;

    public Mono<User> create(BrukerDTO bruker) {
        return getAuthenticatedUserId
                .call()
                .flatMap(userId -> validateCreateUser(userId, bruker.organisasjonsnummer()).then(Mono.just(userId)))
                .map(userId -> {
                    var entity = new UserEntity();
                    entity.setId(cryptographyService.createId(userId, bruker.organisasjonsnummer()));
                    entity.setOrganisasjonsnummer(bruker.organisasjonsnummer());
                    entity.setEpost(bruker.epost());
                    entity.setBrukernavn(bruker.brukernavn());
                    entity.setNew(true);
                    return entity;
                })
                .flatMap(repository::save)
                .map(User::new);

    }

    public Mono<User> getUserFromOrganisasjonsnummer(String organisasjonsnummer) {
        return getAuthenticatedUserId
                .call()
                .map(userId -> cryptographyService.createId(userId, organisasjonsnummer))
                .flatMap(this::getUser);
    }


    public Mono<User> getUser(String id) {
        return getUser(id, false);
    }

    public Mono<User> getUser(String id, boolean loggedIn) {
        return repository.findById(id).flatMap(entity -> {
            if (loggedIn) {
                entity.setNew(false);
                entity.setSistInnlogget(LocalDateTime.now());
                return repository.save(entity);
            }
            return Mono.just(entity);
        }).map(User::new);
    }

    public Mono<User> updateUser(BrukerDTO bruker) {
        return getAuthenticatedUserId
                .call()
                .map(userId -> cryptographyService.createId(userId, bruker.organisasjonsnummer()))
                .flatMap(repository::findById)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Bruker ikke funnet.")))
                .flatMap(entity -> {
                    entity.setEpost(bruker.epost());
                    return repository.save(entity);
                })
                .map(User::new);
    }

    public Mono<User> getUserByBrukernavn(String username) {
        return repository.findByBrukernavn(username).map(User::new);
    }

    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

    private Mono<Void> validateCreateUser(String userId, String representing) {
        var id = cryptographyService.createId(userId, representing);
        return repository.existsById(id)
                .doOnNext(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        throw new UserAlreadyExistsException(id);
                    }
                })
                .then();
    }

    private Mono<Void> validateUpdateUser(String userId, String representing) {
        var id = cryptographyService.createId(userId, representing);
        return repository.existsById(id)
                .doOnNext(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        throw new UserAlreadyExistsException(id);
                    }
                })
                .then();
    }

}