package no.nav.udistub.provider.ws.security;

import javax.security.auth.Subject;

public class ThreadLocalSubjectHandler extends SubjectHandler {

    private static final ThreadLocal<Subject> subjectHolder = new ThreadLocal<>();

    @Override
    public Subject getSubject() {
        return subjectHolder.get();
    }

    public void setSubject(Subject subject) {
        subjectHolder.set(subject);
    }
}
