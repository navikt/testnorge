import { test as base } from '@playwright/test'
import {
	aaregMock,
	afpOffentligMock,
	arenaMock,
	backendBestillingerMock,
	backendTransaksjonMock,
	bestillingFragmentNavigerMock,
	bestillingFragmentSearchMock,
	brregstubMock,
	brukerMalerMock,
	brukerOrganisasjonMalerMock,
	brukerTeamsMock,
	eksisterendeGruppeMock,
	gjeldendeAzureBrukerMock,
	gjeldendeProfilMock,
	histarkMock,
	instMock,
	joarkDokumentMock,
	joarkJournalpostMock,
	kodeverkMock,
	kontoregisterMock,
	krrstubMock,
	medlMock,
	miljoeMock,
	nyGruppeMock,
	oppsummeringsdokumentServiceMock,
	organisasjonerForBrukerMock,
	organisasjonFraMiljoeMock,
	paginerteGrupperMock,
	pensjonMock,
	pensjonPensjonsavtaleMock,
	pensjonTpMock,
	personFragmentNavigerMock,
	personFragmentSearchMock,
	skjermingMock,
	tagsMock,
	tpsMessagingMock,
	udistubMock,
} from './mocks/BasicMocks'
import { pdlBulkpersonerMock, pdlForvalterMock, pdlPersonEnkeltMock } from './mocks/PdlMocks'

type RouteInfo = {
	url: RegExp | string
	response: any
	status?: number
}

const api = new RegExp(/\/api\/v/)
const weatherApi = new RegExp(/\/weatherapi\//)
const miljoer = new RegExp(/\/miljoer/)
const arenaMiljoer = new RegExp(/testnav-arena-forvalteren-proxy\/api\/v1\/miljoe/)
const current = new RegExp(/current/)
const bilde = new RegExp(/testnorge-profil-api\/api\/v1\/profil\/bilde$/)
const profil = new RegExp(/\/profil\/bilde/)
const brukerTeams = new RegExp(/api\/v1\/bruker\/teams/)
const hentGrupper = new RegExp(/api\/v1\/gruppe\?pageNo/)
const histark = new RegExp(/testnav-dolly-proxy\/histark\/api\//)
const personFragmentSearch = new RegExp(/\/testnav-pdl-forvalter\/api\/v1\/identiteter\?fragment/)
const bestillingFragmentSearch = new RegExp(
	/\/dolly-backend\/api\/v1\/bestilling\/soekBestilling\?fragment/,
)
const personFragmentNaviger = new RegExp(/dolly-backend\/api\/v1\/ident\/naviger\/12345678912/)
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
const arena = new RegExp(/testnav-arena-forvalteren-proxy\/q1\/arena/)
const inst = new RegExp(/testnav-inst-proxy\/api\/v1\/ident/)
const skjerming = new RegExp(/dolly-backend\/api\/v1\/skjerming/)
const pensjon = new RegExp(/testnav-pensjon-testdata-facade-proxy\/api\/v1\/inntekt/)
const pensjonMiljoer = new RegExp(/testnav-pensjon-testdata-facade-proxy\/api\/v1\/miljo/)
const pensjonTp = new RegExp(/testnav-pensjon-testdata-facade-proxy\/api\/v1\/tp(.*?)q1/)
const pensjonPensjonsavtale = new RegExp(
	/testnav-pensjon-testdata-facade-proxy\/api\/v2\/pensjonsavtale\/hent/,
)
const afpOffentlig = new RegExp(/testnav-pensjon-testdata-facade-proxy\/q1\/api\/mock-oppsett/)
const krrstub = new RegExp(/testnav-krrstub-proxy\/api\/v2/)
const udistub = new RegExp(/testnav-udistub-proxy\/api\/v1/)
const brregstub = new RegExp(/testnav-brregstub/)
const medl = new RegExp(/testnav-medl-proxy/)
const brukerMaler = new RegExp(/dolly-backend\/api\/v1\/malbestilling\/brukerId/)
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

const mockRoutes: RouteInfo[] = [
	{ url: api, response: [] },
	{ url: weatherApi, status: 404, response: {} },
	{ url: current, response: gjeldendeAzureBrukerMock },
	{ url: profil, response: gjeldendeProfilMock },
	{ url: brukerTeams, response: brukerTeamsMock },
	{ url: miljoer, response: miljoeMock },
	{ url: pensjonMiljoer, response: miljoeMock },
	{ url: personFragmentSearch, response: personFragmentSearchMock },
	{ url: bestillingFragmentSearch, response: bestillingFragmentSearchMock },
	{ url: personFragmentNaviger, response: personFragmentNavigerMock },
	{ url: bestillingFragmentNaviger, response: bestillingFragmentNavigerMock },
	{ url: hentGrupper, response: paginerteGrupperMock },
	{ url: histark, response: histarkMock },
	{ url: hentGruppeEn, response: eksisterendeGruppeMock },
	{ url: hentGruppeTo, response: nyGruppeMock },
	{ url: hentGruppeBestilling, response: backendBestillingerMock },
	{ url: lagNyGruppe, response: nyGruppeMock },
	{ url: pdlPersonBolk, response: pdlBulkpersonerMock },
	{ url: pdlPersonEnkelt, response: pdlPersonEnkeltMock },
	{ url: pdlForvalter, response: pdlForvalterMock },
	{ url: kontoregister, response: kontoregisterMock },
	{ url: backendTransaksjon, response: backendTransaksjonMock },
	{ url: tags, response: tagsMock },
	{ url: brukerMaler, response: brukerMalerMock },
	{ url: oppsummeringsdokService, response: oppsummeringsdokumentServiceMock },
	{ url: brukerOrganisasjonMaler, response: brukerOrganisasjonMalerMock },
	{ url: brregstub, response: brregstubMock },
	{ url: medl, response: medlMock },
	{ url: joarkDokJournalpost, response: joarkJournalpostMock },
	{ url: joarkDokDokument, response: joarkDokumentMock },
	{ url: krrstub, response: krrstubMock },
	{ url: aareg, response: aaregMock },
	{ url: arena, response: arenaMock },
	{ url: tpsMessaging, response: tpsMessagingMock },
	{ url: skjerming, response: skjermingMock },
	{ url: inst, response: instMock },
	{ url: pensjon, response: pensjonMock },
	{ url: pensjonTp, response: pensjonTpMock },
	{ url: pensjonPensjonsavtale, response: pensjonPensjonsavtaleMock },
	{ url: afpOffentlig, response: afpOffentligMock },
	{ url: udistub, response: udistubMock },
	{ url: kodeverk, response: kodeverkMock },
	{ url: organisasjonFraMiljoe, response: organisasjonFraMiljoeMock },
	{ url: organisasjonerForBruker, response: organisasjonerForBrukerMock },
	{ url: bilde, response: {}, status: 404 },
	{ url: dokarkivMiljoer, response: ['q1', 'q2'] },
	{ url: arenaMiljoer, response: ['q1', 'q2', 'q4'] },
	{ url: '**/dolly-logg', response: [] },
]

export const test = base.extend({
	page: async ({ page, context }, use) => {
		for (const routeInfo of mockRoutes) {
			await context.addInitScript(() => {
				// @ts-ignore
				return (window.isRunningTest = true)
			})

			await page.route(routeInfo.url, async (route) => {
				await route.fulfill({
					status: routeInfo.status || 200,
					body: JSON.stringify(routeInfo.response),
					headers: { 'content-type': 'application/json' },
				})
			})
		}

		await use(page)
	},
})
export { expect } from '@playwright/test'
