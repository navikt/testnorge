import { RequestMock } from 'testcafe'
import { ReactSelector, waitForReact } from 'testcafe-react-selectors'
import {
	aaregMock,
	backendBestillingerMock,
	backendTransaksjonMock,
	brregstubMock,
	brukerMalerMock,
	brukerOrganisasjonMalerMock,
	gjeldendeBrukerMock,
	gjeldendeGruppeMock,
	gjeldendeProfilMock,
	instMock,
	joarkDokumentMock,
	joarkJournalpostMock,
	kodeverkMock,
	kontoregisterMock,
	krrstubMock,
	miljoeMock,
	nyGruppeMock,
	pensjonMock,
	pensjonTpMock,
	sigrunstubMock,
	skjermingMock,
	tpsMessagingMock,
	udistubMock,
} from './util/TestcafeMocks'
import { pdlBulkpersonerMock, pdlForvalterMock, pdlPersonEnkeltMock } from './util/TestcafePdlMocks'

const miljoer = new RegExp(/\/miljoer/)
const current = new RegExp(/current/)
const bilde = new RegExp(/testnorge-profil-api\/api\/v1\/profil\/bilde$/)
const profil = new RegExp(/testnorge-profil-api\/api\/v1\/profil$/)
const hentGrupper = new RegExp(/gruppe\?brukerId/)
const hentGruppe = new RegExp(/\/api\/v1\/gruppe\/1/)
const hentGruppeBestilling = new RegExp(/dolly-backend\/api\/v1\/bestilling\/gruppe\/1/)
const pdlPersonBolk = new RegExp(/\/api\/v1\/pdlperson\/identer/)
const pdlPersonEnkelt = new RegExp(/dolly-backend\/api\/v1\/pdlperson\/ident/)
const tpsMessaging = new RegExp(/testnav-tps-messaging-service\/api\/v1\/personer/)
const pdlForvalter = new RegExp(/testnav-pdl-forvalter\/api\/v1\/personer/)
const spesifikkGruppe = new RegExp(/\/gruppe$/)
const kontoregister = new RegExp(/testnav-kontoregister-person-proxy\/api/)
const backendTransaksjon = new RegExp(/dolly-backend\/api\/v1\/transaksjonid/)
const tags = new RegExp(/\/tags$/)
const kodeverk = new RegExp(/\/kodeverk\//)
const aareg = new RegExp(/dolly-backend\/api\/v1\/aareg\/arbeidsforhold/)
const inst = new RegExp(/testnav-inst-service\/api\/v1\/ident/)
const skjerming = new RegExp(/dolly-backend\/api\/v1\/skjerming/)
const pensjon = new RegExp(/testnav-pensjon-testdata-facade-proxy\/api\/v1\/inntekt/)
const pensjonMiljoer = new RegExp(/testnav-pensjon-testdata-facade-proxy\/api\/v1\/miljo/)
const pensjonTp = new RegExp(/testnav-pensjon-testdata-facade-proxy\/api\/v1\/tp(.*?)q1/)
const krrstub = new RegExp(/testnav-krrstub-proxy\/api\/v2\/sdp/)
const udistub = new RegExp(/dolly-backend\/api\/v1\/udistub/)
const brregstub = new RegExp(/testnav-brregstub/)
const sigrunstub = new RegExp(/testnav-sigrunstub-proxy\/api\/v1\/lignetinntekt/)
const brukerMaler = new RegExp(/dolly-backend\/api\/v1\/bestilling\/malbestilling\/bruker\?/)
const brukerOrganisasjonMaler = new RegExp(
	/dolly-backend\/api\/v1\/organisasjon\/bestilling\/malbestilling\/bruker\?/
)
const joarkDokJournalpost = new RegExp(/testnav-joark-dokument-service\/api\/v2\/journalpost/)
const joarkDokDokument = new RegExp(/dokumentType=ORIGINAL/)

const remainingCallsResponseOk = new RegExp(/api/)

const cookieMock = RequestMock()
	.onRequestTo(miljoer)
	.respond(miljoeMock, 200)
	.onRequestTo(hentGrupper)
	.respond([gjeldendeGruppeMock], 200)
	.onRequestTo(hentGruppe)
	.respond(gjeldendeGruppeMock, 200)
	.onRequestTo(hentGruppeBestilling)
	.respond(backendBestillingerMock, 200)
	.onRequestTo(spesifikkGruppe)
	.respond(nyGruppeMock, 201)
	.onRequestTo(current)
	.respond(gjeldendeBrukerMock, 200)
	.onRequestTo(profil)
	.respond(gjeldendeProfilMock, 200)
	.onRequestTo(bilde)
	.respond(undefined, 404)
	.onRequestTo(pdlPersonBolk)
	.respond(pdlBulkpersonerMock, 200)
	.onRequestTo(pdlPersonEnkelt)
	.respond(pdlPersonEnkeltMock, 200)
	.onRequestTo(pdlForvalter)
	.respond(pdlForvalterMock, 200)
	.onRequestTo(kontoregister)
	.respond(kontoregisterMock, 200)
	.onRequestTo(tags)
	.respond({}, 200)
	.onRequestTo(backendTransaksjon)
	.respond(backendTransaksjonMock, 200)
	.onRequestTo(brukerMaler)
	.respond(brukerMalerMock, 200)
	.onRequestTo(brukerOrganisasjonMaler)
	.respond(brukerOrganisasjonMalerMock, 200)
	.onRequestTo(brregstub)
	.respond(brregstubMock, 200)
	.onRequestTo(joarkDokJournalpost)
	.respond(joarkJournalpostMock, 200)
	.onRequestTo(joarkDokDokument)
	.respond(joarkDokumentMock, 200)
	.onRequestTo(krrstub)
	.respond(krrstubMock, 200)
	.onRequestTo(aareg)
	.respond(aaregMock, 200)
	.onRequestTo(tpsMessaging)
	.respond(tpsMessagingMock, 200)
	.onRequestTo(skjerming)
	.respond(skjermingMock, 200)
	.onRequestTo(inst)
	.respond(instMock, 200)
	.onRequestTo(pensjon)
	.respond(pensjonMock, 200)
	.onRequestTo(pensjonMiljoer)
	.respond(['q1', 'q2'], 200)
	.onRequestTo(pensjonTp)
	.respond(pensjonTpMock, 200)
	.onRequestTo(sigrunstub)
	.respond(sigrunstubMock, 200)
	.onRequestTo(udistub)
	.respond(udistubMock, 200)
	.onRequestTo(kodeverk)
	.respond(kodeverkMock, 200)
	.onRequestTo(remainingCallsResponseOk)
	.respond([], 200)

fixture`Visning`.page`http://localhost:3000`.requestHooks(cookieMock).beforeEach(async () => {
	await waitForReact()
})

test('Naviger til min side og test mal funksjonalitet', async (testController) => {
	await testController
		.click(ReactSelector('NavLink').withKey('naviger-minside'))
		.wait(1000)
		.typeText(ReactSelector('SearchField'), 'org')

	await testController
		.expect(ReactSelector('AppError').exists)
		.eql(
			false,
			'ErrorBoundary utløst, en komponent kaster Error under visning av bestillingsstatus'
		)
		.expect(
			ReactSelector('MalPanel')
				.withText('Organisasjoner')
				.findReact('ExpandButton')
				.getReact(({ props }) => props.expanded)
		)
		.eql(true, 'Maler checkbox på opprett person ble ikke expanded etter søk.')

	await testController
		.expect(ReactSelector('AppError').exists)
		.eql(
			false,
			'ErrorBoundary utløst, en komponent kaster Error under visning av bestillingsstatus'
		)
})

test('Naviger til bestilling tab ved hjelp av keyboard og åpne bestilling med tre forskjellige statuser', async (testController) => {
	await testController
		.click(ReactSelector('TableRow').withKey('1'))
		.click(ReactSelector('ToggleGroup').withText('Bestillinger'))
		.pressKey('tab')
		.pressKey('shift+tab')
		.pressKey('right')
		.pressKey('enter')
		.click(ReactSelector('TableRow').withKey('1'))
		.click(ReactSelector('TableRow').withKey('2'))
		.click(ReactSelector('TableRow').withKey('3'))

	await testController
		.expect(ReactSelector('AppError').exists)
		.eql(
			false,
			'ErrorBoundary utløst, en komponent kaster Error under visning av bestillingsstatus'
		)
})

test('Gå inn på testgruppe og åpne en ident med data i alle fagsystem', async (testController) => {
	await testController
		.click(ReactSelector('TableRow').withKey('1'))
		.click(ReactSelector('TableRow').withKey('12345678912'))
		.wait(1500)

	// Kan brukes under debugging for å se at alle feltene dukker opp, kun visuelt
	// await scrollThroughPage(testController, 90)

	await testController
		.expect(ReactSelector('AppError').exists)
		.eql(false, 'ErrorBoundary utløst, en komponent kaster Error under visning av ident')

	await testController
		.click(
			ReactSelector('PensjonVisning').findReact('MiljoTabs').withText('Pensjonsgivende inntekter')
		)
		.wait(1500)

	await testController
		.click(ReactSelector('TpVisning').findReact('MiljoTabs').withText('ORDNING'))
		.wait(1500)

	await testController
		.expect(ReactSelector('AppError').exists)
		.eql(false, 'ErrorBoundary utløst, Error under visning av TP info')

	await testController.click(ReactSelector('TpsDataVisning').findReact('DollyTooltip')).wait(1500)

	await testController
		.expect(ReactSelector('AppError').exists)
		.eql(false, 'ErrorBoundary utløst, Error under visning av TPS miljø info')

	await testController
		.click(ReactSelector('PdlPersonMiljoeInfo').findReact('DollyTooltip').withText('Q1'))
		.wait(1500)

	await testController
		.expect(ReactSelector('AppError').exists)
		.eql(false, 'ErrorBoundary utløst, Error under visning av PDL miljø info')
})
