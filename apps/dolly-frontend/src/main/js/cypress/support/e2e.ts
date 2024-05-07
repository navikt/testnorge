import 'cypress-react-selector'
import './commands'
import {
	aaregMock,
	ameldingMock,
	arenaMock,
	backendBestillingerMock,
	backendTransaksjonMock,
	bestillingFragmentNavigerMock,
	bestillingFragmentSearchMock,
	brregstubMock,
	brukerMalerMock,
	brukerOrganisasjonMalerMock,
	eksisterendeGruppeMock,
	gjeldendeBrukerMock,
	gjeldendeProfilMock,
	histarkMock,
	instMock,
	joarkDokumentMock,
	joarkJournalpostMock,
	kodeverkMock,
	kontoregisterMock,
	krrstubMock,
	malerMock,
	medlMock,
	miljoeMock,
	nyGruppeMock,
	oppsummeringsdokumentServiceMock,
	organisasjonerForBrukerMock,
	organisasjonFraMiljoeMock,
	paginerteGrupperMock,
	pensjonMock,
	pensjonTpMock,
	personFragmentNavigerMock,
	personFragmentSearchMock,
	sigrunstubMock,
	skjermingMock,
	tagsMock,
	tpsMessagingMock,
	udistubMock,
} from '../mocks/BasicMocks'
import { pdlBulkpersonerMock, pdlForvalterMock, pdlPersonEnkeltMock } from '../mocks/PdlMocks'

const miljoer = new RegExp(/\/miljoer/)
const arenaMiljoer = new RegExp(/testnav-arena-forvalteren-proxy\/api\/v1\/miljoe/)
const current = new RegExp(/current/)
const bilde = new RegExp(/testnorge-profil-api\/api\/v1\/profil\/bilde$/)
const profil = new RegExp(/\/profil\/bilde/)
const hentGrupper = new RegExp(/api\/v1\/gruppe\?pageNo/)
const histark = new RegExp(/testnav-histark-proxy\/api\//)
const personFragmentSearch = new RegExp(/\/testnav-pdl-forvalter\/api\/v1\/identiteter\?fragment/)
const bestillingFragmentSearch = new RegExp(
	/\/dolly-backend\/api\/v1\/bestilling\/soekBestilling\?fragment/,
)
const personFragmentNaviger = new RegExp(/dolly-backend\/api\/v1\/ident\/naviger\/12345678912/)
const dollySoekIdenter = new RegExp(/dolly-backend\/api\/v1\/elastic\/identer/)
const bestillingFragmentNaviger = new RegExp(/dolly-backend\/api\/v1\/bestilling\/naviger\/1/)
const hentGruppeEn = new RegExp(/\/api\/v1\/gruppe\/1/)
const hentGruppeTo = new RegExp(/\/api\/v1\/gruppe\/2/)
const hentGruppeBestilling = new RegExp(/dolly-backend\/api\/v1\/bestilling\/gruppe\/1/)
const pdlPersonBolk = new RegExp(/\/api\/v2\/personer\/identer/)
const pdlPersonEnkelt = new RegExp(/person-service\/api\/v2\/personer\/ident/)
const tpsMessaging = new RegExp(/testnav-tps-messaging-service\/api\/v1\/personer/)
const pdlForvalter = new RegExp(/testnav-pdl-forvalter\/api\/v1\/personer/)
const lagNyGruppe = new RegExp(/api\/v1\/gruppe$/)
const kontoregister = new RegExp(/testnav-kontoregister-person-proxy\/api/)
const backendTransaksjon = new RegExp(/dolly-backend\/api\/v1\/transaksjonid/)
const tags = new RegExp(/\/tags$/)
const kodeverk = new RegExp(/testnav-kodeverk-service\/api\/v1\/kodeverk\//)
const dokarkivMiljoer = new RegExp(/testnav-dokarkiv-proxy\/rest\/miljoe/)
const aareg = new RegExp(/testnav-aareg-proxy\/q1\/api\/v1\/arbeidstaker/)
const amelding = new RegExp(/oppsummeringsdokument-service\/api\/v1\/oppsummeringsdokumenter/)
const arena = new RegExp(/testnav-arena-forvalteren-proxy\/q1\/arena/)
const inst = new RegExp(/testnav-inst-proxy\/api\/v1\/ident/)
const skjerming = new RegExp(/dolly-backend\/api\/v1\/skjerming/)
const pensjon = new RegExp(/testnav-pensjon-testdata-facade-proxy\/api\/v1\/inntekt/)
const pensjonMiljoer = new RegExp(/testnav-pensjon-testdata-facade-proxy\/api\/v1\/miljo/)
const pensjonTp = new RegExp(/testnav-pensjon-testdata-facade-proxy\/api\/v1\/tp(.*?)q1/)
const krrstub = new RegExp(/testnav-krrstub-proxy\/api\/v2/)
const udistub = new RegExp(/testnav-udistub-proxy\/api\/v1/)
const brregstub = new RegExp(/testnav-brregstub/)
const medl = new RegExp(/testnav-medl-proxy/)
const sigrunstub = new RegExp(/testnav-sigrunstub-proxy\/api\/v1\/lignetinntekt/)
const alleMaler = new RegExp(/dolly-backend\/api\/v1\/malbestilling$/)
const brukerMaler = new RegExp(/dolly-backend\/api\/v1\/malbestilling\?brukerId/)
const oppsummeringsdokService = new RegExp(
	/oppsummeringsdokument-service\/api\/v1\/oppsummeringsdokumenter/,
)
const brukerOrganisasjonMaler = new RegExp(
	/dolly-backend\/api\/v1\/organisasjon\/bestilling\/malbestilling\?/,
)
const joarkDokJournalpost = new RegExp(/testnav-joark-dokument-service\/api\/v2\/journalpost/)
const joarkDokDokument = new RegExp(/dokumentType=ORIGINAL/)
const organisasjonFraMiljoe = new RegExp(
	/testnav-organisasjon-forvalter\/api\/v2\/organisasjoner\/framiljoe/,
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
	cy.intercept({ method: 'GET', url: personFragmentSearch }, personFragmentSearchMock)
	cy.intercept({ method: 'GET', url: bestillingFragmentSearch }, bestillingFragmentSearchMock)
	cy.intercept({ method: 'GET', url: personFragmentNaviger }, personFragmentNavigerMock)
	cy.intercept({ method: 'GET', url: bestillingFragmentNaviger }, bestillingFragmentNavigerMock)
	cy.intercept({ method: 'GET', url: hentGrupper }, paginerteGrupperMock)
	cy.intercept({ method: 'GET', url: histark }, histarkMock)
	cy.intercept({ method: 'GET', url: hentGruppeEn }, eksisterendeGruppeMock)
	cy.intercept({ method: 'GET', url: hentGruppeTo }, nyGruppeMock)
	cy.intercept({ method: 'GET', url: hentGruppeBestilling }, backendBestillingerMock)
	cy.intercept({ method: 'POST', url: lagNyGruppe }, { statusCode: 201, body: nyGruppeMock })
	cy.intercept({ method: 'GET', url: pdlPersonBolk }, pdlBulkpersonerMock)
	cy.intercept({ method: 'GET', url: pdlPersonEnkelt }, pdlPersonEnkeltMock)
	cy.intercept({ method: 'GET', url: pdlForvalter }, pdlForvalterMock)
	cy.intercept({ method: 'POST', url: kontoregister }, kontoregisterMock)
	cy.intercept({ method: 'GET', url: tags }, tagsMock)
	cy.intercept({ method: 'GET', url: backendTransaksjon }, backendTransaksjonMock)
	cy.intercept({ method: 'GET', url: brukerMaler }, brukerMalerMock)
	cy.intercept({ method: 'GET', url: oppsummeringsdokService }, oppsummeringsdokumentServiceMock)
	cy.intercept({ method: 'GET', url: alleMaler }, malerMock)
	cy.intercept({ method: 'GET', url: brukerOrganisasjonMaler }, brukerOrganisasjonMalerMock)
	cy.intercept({ method: 'GET', url: brregstub }, brregstubMock)
	cy.intercept({ method: 'GET', url: medl }, medlMock)
	cy.intercept({ method: 'GET', url: joarkDokJournalpost }, joarkJournalpostMock)
	cy.intercept({ method: 'GET', url: joarkDokDokument }, joarkDokumentMock)
	cy.intercept({ method: 'GET', url: krrstub }, krrstubMock)
	cy.intercept({ method: 'GET', url: aareg }, aaregMock)
	cy.intercept({ method: 'GET', url: amelding }, ameldingMock)
	cy.intercept({ method: 'GET', url: arena }, arenaMock)
	cy.intercept({ method: 'GET', url: tpsMessaging }, tpsMessagingMock)
	cy.intercept({ method: 'GET', url: skjerming }, skjermingMock)
	cy.intercept({ method: 'GET', url: inst }, instMock)
	cy.intercept({ method: 'GET', url: pensjon }, pensjonMock)
	cy.intercept({ method: 'GET', url: pensjonMiljoer }, miljoeMock)
	cy.intercept({ method: 'GET', url: pensjonTp }, pensjonTpMock)
	cy.intercept({ method: 'GET', url: sigrunstub }, sigrunstubMock)
	cy.intercept({ method: 'GET', url: udistub }, udistubMock)
	cy.intercept({ method: 'GET', url: kodeverk }, kodeverkMock)
	cy.intercept({ method: 'GET', url: dokarkivMiljoer }, ['q1', 'q2'])
	cy.intercept({ method: 'POST', url: dollySoekIdenter }, ['12345678912'])
	cy.intercept({ method: 'GET', url: arenaMiljoer }, ['q1', 'q2', 'q4'])
	cy.intercept({ method: 'GET', url: organisasjonFraMiljoe }, organisasjonFraMiljoeMock)
	cy.intercept({ method: 'GET', url: organisasjonerForBruker }, organisasjonerForBrukerMock)
})
