package no.nav.registre.syntrest.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
@AllArgsConstructor
public class LockSet {
    private ReentrantLock applicationLock;
    private ReentrantLock connectedUsers;
}