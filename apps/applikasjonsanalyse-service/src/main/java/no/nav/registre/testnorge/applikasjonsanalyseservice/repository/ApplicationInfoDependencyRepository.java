package no.nav.registre.testnorge.applikasjonsanalyseservice.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.model.ApplicationDependencyModel;
import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.model.ApplicationInfoDependencyModel;
import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.model.ApplicationInfoModel;

public interface ApplicationInfoDependencyRepository extends CrudRepository<ApplicationInfoDependencyModel, Long> {
    Optional<ApplicationInfoDependencyModel> findByInfoModelAndAndDependencyModel(ApplicationInfoModel infoModel, ApplicationDependencyModel dependencyModel);

    List<ApplicationInfoDependencyModel> findByInfoModel(ApplicationInfoModel model);

    List<ApplicationInfoDependencyModel> findByInfoModelAndIdNotIn(ApplicationInfoModel model, List<Long> ids);

}
