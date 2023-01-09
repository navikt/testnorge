/// <reference types="cypress" />

import 'cypress-react-selector'
import {
	aaregMock,
	backendBestillingerMock,
	backendTransaksjonMock,
	brregstubMock,
	brukerMalerMock,
	brukerOrganisasjonMalerMock,
	eksisterendeGruppeMock,
	gjeldendeBrukerMock,
	gjeldendeProfilMock,
	instMock,
	joarkDokumentMock,
	joarkJournalpostMock,
	kodeverkMock,
	kontoregisterMock,
	krrstubMock,
	malerMock,
	miljoeMock,
	nyGruppeMock,
	organisasjonerForBrukerMock,
	organisasjonFraMiljoeMock,
	paginerteGrupperMock,
	pensjonMock,
	pensjonTpMock,
	sigrunstubMock,
	skjermingMock,
	tpsMessagingMock,
	udistubMock,
} from '../mocks/BasicMocks'
import { pdlBulkpersonerMock, pdlForvalterMock, pdlPersonEnkeltMock } from '../mocks/PdlMocks'

const miljoer = new RegExp(/\/miljoer/)
const current = new RegExp(/current/)
const bilde = new RegExp(/testnorge-profil-api\/api\/v1\/profil\/bilde$/)
const profil = new RegExp(/\/profil\/bilde/)
const hentGrupper = new RegExp(/api\/v1\/gruppe\?pageNo/)
const hentGruppe = new RegExp(/\/api\/v1\/gruppe\/1/)
const hentGruppeBestilling = new RegExp(/dolly-backend\/api\/v1\/bestilling\/gruppe\/1/)
const pdlPersonBolk = new RegExp(/\/api\/v1\/pdlperson\/identer/)
const pdlPersonEnkelt = new RegExp(/dolly-backend\/api\/v1\/pdlperson\/ident/)
const tpsMessaging = new RegExp(/testnav-tps-messaging-service\/api\/v1\/personer/)
const pdlForvalter = new RegExp(/testnav-pdl-forvalter\/api\/v1\/personer/)
const lagNyGruppe = new RegExp(/api\/v1\/gruppe$/)
const kontoregister = new RegExp(/testnav-kontoregister-person-proxy\/api/)
const backendTransaksjon = new RegExp(/dolly-backend\/api\/v1\/transaksjonid/)
const tags = new RegExp(/\/tags$/)
const kodeverk = new RegExp(/\/v1\/kodeverk\//)
const dokarkiv = new RegExp(/testnav-dokarkiv-proxy\/internal\/miljoe/)
const aareg = new RegExp(/testnav-aaregister-proxy\/q1\/api\/v1\/arbeidstaker/)
const inst = new RegExp(/testnav-inst-service\/api\/v1\/ident/)
const skjerming = new RegExp(/dolly-backend\/api\/v1\/skjerming/)
const pensjon = new RegExp(/testnav-pensjon-testdata-facade-proxy\/api\/v1\/inntekt/)
const pensjonMiljoer = new RegExp(/testnav-pensjon-testdata-facade-proxy\/api\/v1\/miljo/)
const pensjonTp = new RegExp(/testnav-pensjon-testdata-facade-proxy\/api\/v1\/tp(.*?)q1/)
const krrstub = new RegExp(/testnav-krrstub-proxy\/api\/v2/)
const udistub = new RegExp(/dolly-backend\/api\/v1\/udistub/)
const brregstub = new RegExp(/testnav-brregstub/)
const sigrunstub = new RegExp(/testnav-sigrunstub-proxy\/api\/v1\/lignetinntekt/)
const alleMaler = new RegExp(/dolly-backend\/api\/v1\/bestilling\/malbestilling$/)
const brukerMaler = new RegExp(/dolly-backend\/api\/v1\/bestilling\/malbestilling\/bruker/)
const brukerOrganisasjonMaler = new RegExp(
	/dolly-backend\/api\/v1\/organisasjon\/bestilling\/malbestilling\/bruker\?/
)
const joarkDokJournalpost = new RegExp(/testnav-joark-dokument-service\/api\/v2\/journalpost/)
const joarkDokDokument = new RegExp(/dokumentType=ORIGINAL/)
const organisasjonFraMiljoe = new RegExp(
	/testnav-organisasjon-forvalter\/api\/v2\/organisasjoner\/framiljoe/
)
const organisasjonerForBruker = new RegExp(/dolly-backend\/api\/v1\/organisasjon\?brukerId/)

const remainingCallsResponseOk = new RegExp(/api\/v1/)

beforeEach(() => {
	cy.intercept({ method: 'PUT', url: '*' }, []).as('block_put')
	cy.intercept({ method: 'DELETE', url: '*' }, []).as('block_delete')
	cy.intercept({ method: 'POST', url: '*' }, []).as('remaining_post')
	cy.intercept({ method: 'GET', url: remainingCallsResponseOk }, []).as('remaining_get')
	cy.intercept({ method: 'GET', url: current }, gjeldendeBrukerMock).as('GjeldendeBruker')
	cy.intercept({ method: 'GET', url: profil }, gjeldendeProfilMock).as('gjeldendeProfil')
	cy.intercept({ method: 'GET', url: miljoer }, miljoeMock).as('miljoer')
	cy.intercept({ method: 'GET', url: bilde }, { statusCode: 404 }).as('bilde')
	cy.intercept({ method: 'GET', url: hentGrupper }, paginerteGrupperMock)
	cy.intercept({ method: 'GET', url: hentGruppe }, eksisterendeGruppeMock)
	cy.intercept({ method: 'GET', url: hentGruppeBestilling }, backendBestillingerMock)
	cy.intercept({ method: 'POST', url: lagNyGruppe }, { statusCode: 201, body: nyGruppeMock })
	cy.intercept({ method: 'GET', url: pdlPersonBolk }, pdlBulkpersonerMock)
	cy.intercept({ method: 'GET', url: pdlPersonEnkelt }, pdlPersonEnkeltMock)
	cy.intercept({ method: 'GET', url: pdlForvalter }, pdlForvalterMock)
	cy.intercept({ method: 'GET', url: kontoregister }, kontoregisterMock)
	cy.intercept({ method: 'GET', url: tags }, { body: {} })
	cy.intercept({ method: 'GET', url: backendTransaksjon }, backendTransaksjonMock)
	cy.intercept({ method: 'GET', url: brukerMaler }, brukerMalerMock)
	cy.intercept({ method: 'GET', url: alleMaler }, malerMock)
	cy.intercept({ method: 'GET', url: brukerOrganisasjonMaler }, brukerOrganisasjonMalerMock)
	cy.intercept({ method: 'GET', url: brregstub }, brregstubMock)
	cy.intercept({ method: 'GET', url: joarkDokJournalpost }, joarkJournalpostMock)
	cy.intercept({ method: 'GET', url: joarkDokDokument }, joarkDokumentMock)
	cy.intercept({ method: 'GET', url: krrstub }, krrstubMock)
	cy.intercept({ method: 'GET', url: aareg }, aaregMock)
	cy.intercept({ method: 'GET', url: tpsMessaging }, tpsMessagingMock)
	cy.intercept({ method: 'GET', url: skjerming }, skjermingMock)
	cy.intercept({ method: 'GET', url: inst }, instMock)
	cy.intercept({ method: 'GET', url: pensjon }, pensjonMock)
	cy.intercept({ method: 'GET', url: pensjonMiljoer }, miljoeMock)
	cy.intercept({ method: 'GET', url: pensjonTp }, pensjonTpMock)
	cy.intercept({ method: 'GET', url: sigrunstub }, sigrunstubMock)
	cy.intercept({ method: 'GET', url: udistub }, udistubMock)
	cy.intercept({ method: 'GET', url: kodeverk }, kodeverkMock)
	cy.intercept({ method: 'GET', url: dokarkiv }, [])
	cy.intercept({ method: 'GET', url: organisasjonFraMiljoe }, organisasjonFraMiljoeMock)
	cy.intercept({ method: 'GET', url: organisasjonerForBruker }, organisasjonerForBrukerMock)
})
