package no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResourceDTO {

    private String identifier;
    private String minLength;
    private String version;
    private Map<String, String> title;
    private Map<String, String> description;
    private Map<String, String> rightDescription;
    private String homepage;
    private String status;
    private List<String> spatial;
    private List<ContactPoint> contactPoints;
    private List<String> produces;
    private String isPartOf;
    private List<String> thematicAreas;
    private List<ResourceReference> resourceReferences;
    private Boolean delegable;
    private Boolean visible;
    private CompetentAuthority hasCompetentAuthority;
    private List<Keyword> keywords;
    private Integer accessListMode;
    private Boolean selfIdentifiedUserEnabled;
    private Boolean enterpriseUserEnabled;
    private Integer resourceType;
    private List<ResourcePartyType> availableForType;
    private List<AuthorizationReferenceAttribute> authorizationReference;

    public Map<String, String> getTitle() {

        if (isNull(title)) {
            title = new HashMap<>();
        }
        return title;
    }

    public Map<String, String> getDescription() {

        if (isNull(description)) {
            description = new HashMap<>();
        }
        return description;
    }

    public Map<String, String> getRightDescription() {

        if (isNull(rightDescription)) {
            rightDescription = new HashMap<>();
        }
        return rightDescription;
    }

    public List<String> getSpatial() {

        if (isNull(spatial)) {
            spatial = new ArrayList<>();
        }
        return spatial;
    }

    public List<ContactPoint> getContactPoints() {

        if (isNull(contactPoints)) {
            contactPoints = new ArrayList<>();
        }
        return contactPoints;
    }

    public List<String> getProduces() {

        if (isNull(produces)) {
            produces = new ArrayList<>();
        }
        return produces;
    }

    public List<String> getThematicAreas() {

        if (isNull(thematicAreas)) {
            thematicAreas = new ArrayList<>();
        }
        return thematicAreas;
    }

    public List<ResourceReference> getResourceReferences() {

        if (isNull(resourceReferences)) {
            resourceReferences = new ArrayList<>();
        }
        return resourceReferences;
    }

    public List<Keyword> getKeywords() {

        if (isNull(keywords)) {
            keywords = new ArrayList<>();
        }
        return keywords;
    }

    public List<ResourcePartyType> getAvailableForType() {

        if (isNull(availableForType)) {
            availableForType = new ArrayList<>();
        }
        return availableForType;
    }

    public List<AuthorizationReferenceAttribute> getAuthorizationReference() {

        if (isNull(authorizationReference)) {
            authorizationReference = new ArrayList<>();
        }
        return authorizationReference;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactPoint {

        private String category;
        private String email;
        private String telephone;
        private String contactPage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResourceReference {

        private Integer referenceSource;
        private String reference;
        private Integer referenceType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetentAuthority {

        private String organization;
        private String orgcode;
        private Map<String, String> name;

        public Map<String, String> getName() {

            if (isNull(name)) {
                name = new HashMap<>();
            }
            return name;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Keyword {

        private String word;
        private String language;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorizationReferenceAttribute {

        private String id;
        private String value;
    }

    public enum ResourcePartyType {

        PrivatePerson,
        LegalEntityEnterprise,
        Company,
        BankruptcyEstate,
        SelfRegisteredUser
    }
}
