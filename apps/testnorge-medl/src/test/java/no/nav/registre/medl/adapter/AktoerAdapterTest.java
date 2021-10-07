package no.nav.registre.medl.adapter;

import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.medl.database.repository.AktoerRepository;

@RunWith(MockitoJUnitRunner.class)
public class AktoerAdapterTest {
    @Mock
    private AktoerRepository repository;
    private AktoerAdapter aktoerAdapter;

    @Before
    public void startup() {
        aktoerAdapter = new AktoerAdapter(repository);
    }

    @Test
    public void shouldPartitionFindAllByIdRepositoryCall() {
        aktoerAdapter.filtererAktoerIder(generateListOfAktoerIder(2_200L));
        verify(repository, times(3)).findAllById(anyIterable());
    }


    @Test
    public void shouldPartitionFindAllByIdentInRepositoryCall() {
        aktoerAdapter.filtrerIdenter(generateListOfIdenter(3_200L));
        verify(repository, times(4)).findAllByIdentIn(anyList());
    }

    private List<Long> generateListOfAktoerIder(long amount) {
        List<Long> ider = new ArrayList<>();
        for (long i = 0; i < amount; i++) {
            ider.add(i);
        }
        return ider;
    }

    private List<String> generateListOfIdenter(long amount) {
        List<String> identer = new ArrayList<>();
        for (long i = 0; i < amount; i++) {
            identer.add("test" + i);
        }
        return identer;
    }

}
