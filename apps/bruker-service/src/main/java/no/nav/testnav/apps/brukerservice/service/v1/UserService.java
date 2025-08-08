package no.nav.testnav.apps.brukerservice.service.v1;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.brukerservice.domain.User;
import no.nav.testnav.apps.brukerservice.exception.UserAlreadyExistsException;
import no.nav.testnav.apps.brukerservice.exception.UsernameAlreadyTakenException;
import no.nav.testnav.apps.brukerservice.repository.UserEntity;
import no.nav.testnav.apps.brukerservice.repository.UserRepository;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final CryptographyService cryptographyService;
    private final UserRepository repository;
    private final GetAuthenticatedUserId getAuthenticatedUserId;

    public Mono<User> create(String username, String representing) {
        return getAuthenticatedUserId
                .call()
                .flatMap(userId -> validateCreateUser(userId, username, representing).then(Mono.just(userId)))
                .map(userId -> {
                    var entity = new UserEntity();
                    entity.setId(cryptographyService.createId(userId, representing));
                    entity.setOrganisasjonsnummer(representing);
                    entity.setBrukernavn(username);
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

    public Mono<User> updateUsername(String id, String username) {
        return validateUpdateUser(username)
                .then(repository.findById(id).flatMap(entity -> {
                    entity.setBrukernavn(username);
                    entity.setNew(false);
                    entity.setOppdatert(LocalDateTime.now());
                    return repository.save(entity);
                }))
                .map(User::new);
    }

    public Mono<User> getUserByBrukernavn(String username) {
        return repository.findByBrukernavn(username).map(User::new);
    }

    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

    private Mono<Void> validateUpdateUser(String username) {
        return repository.existsByBrukernavn(username)
                .doOnNext(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        throw new UsernameAlreadyTakenException(username);
                    }
                })
                .then();
    }

    private Mono<Void> validateCreateUser(String userId, String username, String representing) {
        var id = cryptographyService.createId(userId, representing);
        return repository.existsById(id)
                .doOnNext(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        throw new UserAlreadyExistsException(id);
                    }
                })
                .flatMap(ignored -> repository.existsByBrukernavn(username))
                .doOnNext(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        throw new UsernameAlreadyTakenException(username);
                    }
                })
                .then();
    }

}
