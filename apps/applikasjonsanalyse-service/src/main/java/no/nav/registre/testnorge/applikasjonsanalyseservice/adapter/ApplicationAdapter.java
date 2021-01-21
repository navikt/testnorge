package no.nav.registre.testnorge.applikasjonsanalyseservice.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.ApplicationInfo;
import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.Dependency;
import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.ApplicationDependencyRepository;
import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.ApplicationInfoDependencyRepository;
import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.ApplicationInfoRepository;
import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.model.ApplicationDependencyModel;
import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.model.ApplicationInfoDependencyModel;
import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.model.ApplicationInfoModel;

@Component
@AllArgsConstructor
public class ApplicationAdapter {

    private final ApplicationDependencyRepository applicationDependencyRepository;
    private final ApplicationInfoRepository applicationInfoRepository;
    private final ApplicationInfoDependencyRepository applicationInfoDependencyRepository;

    public Long save(ApplicationInfo applicationInfo) {
        ApplicationInfoModel model = applicationInfoRepository.findByNameAndClusterAndNamespace(
                applicationInfo.getName(),
                applicationInfo.getCluster(),
                applicationInfo.getNamespace()
        ).orElseGet(() -> applicationInfoRepository.save(applicationInfo.toModel()));
        List<ApplicationDependencyModel> dependencyModels = applicationInfo
                .getDependencies()
                .stream()
                .map(this::save)
                .collect(Collectors.toList());

        List<ApplicationInfoDependencyModel> applicationInfoDependencyModels = save(model, dependencyModels);
        deleteNotInListForModel(applicationInfoDependencyModels, model);
        return model.getId();
    }

    private ApplicationDependencyModel save(Dependency dependency) {
        return applicationDependencyRepository.findByNameAndClusterAndNamespace(
                dependency.getName(),
                dependency.getCluster(),
                dependency.getNamespace()
        ).orElseGet(() -> applicationDependencyRepository.save(dependency.toModel()));
    }

    private List<ApplicationInfoDependencyModel> save(ApplicationInfoModel infoModel, List<ApplicationDependencyModel> list) {
        return list.stream().map(value -> save(infoModel, value)).collect(Collectors.toList());
    }

    private ApplicationInfoDependencyModel save(ApplicationInfoModel infoModel, ApplicationDependencyModel dependencyModel) {
        return applicationInfoDependencyRepository
                .findByInfoModelAndAndDependencyModel(infoModel, dependencyModel)
                .orElseGet(() -> applicationInfoDependencyRepository.save(ApplicationInfoDependencyModel
                        .builder()
                        .infoModel(infoModel)
                        .dependencyModel(dependencyModel)
                        .build()
                ));
    }

    private void deleteNotInListForModel(List<ApplicationInfoDependencyModel> list, ApplicationInfoModel infoModel) {
        applicationInfoDependencyRepository.deleteByInfoModelAndIdNotIn(
                infoModel,
                list.stream().map(ApplicationInfoDependencyModel::getId).toArray(Long[]::new)
        );
    }
}
