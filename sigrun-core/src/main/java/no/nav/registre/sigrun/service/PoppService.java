package no.nav.registre.sigrun.service;

import no.nav.registre.sigrun.consumer.PoppSyntRestConsumer;
import no.nav.registre.sigrun.domain.Grunnlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class PoppService {

    @Autowired
    private PoppSyntRestConsumer poppSyntRestConsumer;

    public List<Map<String, Object>> getPoppMeldinger(String[] fnrs) throws InterruptedException, ExecutionException {
        CompletableFuture<List<Map<String, Object>>> result = poppSyntRestConsumer.getPoppMeldingerFromSyntRest(fnrs);
        List<Map<String, Object>> unpackedResult = result.get();
        unpackedResult = castTypes(unpackedResult);
        poppSyntRestConsumer.sendDataToSigrunstub(unpackedResult);
        return unpackedResult;
    }

    public List<Map<String, Object>> castTypes(List<Map<String, Object>> meldinger){
        for (Map<String, Object> map : meldinger){
            for (String key : map.keySet()){
                if (key.equals("grunnlag")){
                    Grunnlag grunnlag = new Grunnlag(map.get(key));
                    map.put(key, grunnlag.getGrunnlag());
                }
            }
        }
        return meldinger;
    }
}
