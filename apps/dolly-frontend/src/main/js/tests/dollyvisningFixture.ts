// noinspection TypeScriptValidateTypes

import { RequestMock } from 'testcafe'
import { ReactSelector, waitForReact } from 'testcafe-react-selectors'
import {
	aaregMock,
	backendBestillingerMock,
	backendTransaksjonMock,
	brregstubMock,
	gjeldendeBrukerMock,
	gjeldendeGruppeMock,
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
	pensjonMock,
	sigrunstubMock,
	skjermingMock,
	udistubMock,
} from './util/TestcafeMocks'
import { pdlBulkpersonerMock, pdlForvalterMock } from './util/TestcafePdlMocks'
import { scrollThroughPage } from './util/TestcafeUtils'

const miljoer = new RegExp(/\/miljoer/)
const current = new RegExp(/current/)
const profil = new RegExp(/\/profil/)
const bilde = new RegExp(/\/bilde/)
const hentGrupper = new RegExp(/gruppe\?brukerId/)
const hentGruppe = new RegExp(/\/api\/v1\/gruppe\/1/)
const hentGruppeBestilling = new RegExp(/dolly-backend\/api\/v1\/bestilling\/gruppe\/1/)
const pdlPersonBolk = new RegExp(/\/api\/v1\/pdlperson\/identer/)
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
const krrstub = new RegExp(/testnav-krrstub-proxy\/api\/v2\/sdp/)
const udistub = new RegExp(/dolly-backend\/api\/v1\/udistub/)
const brregstub = new RegExp(/testnav-brregstub/)
const sigrunstub = new RegExp(/testnav-sigrunstub-proxy\/api\/v1\/lignetinntekt/)
const bestillingMaler = new RegExp(/\/bestilling\/malbestilling/)
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
	.respond(null, 404)
	.onRequestTo(bestillingMaler)
	.respond(malerMock, 200)
	.onRequestTo(pdlPersonBolk)
	.respond(pdlBulkpersonerMock, 200)
	.onRequestTo(pdlForvalter)
	.respond(pdlForvalterMock, 200)
	.onRequestTo(kontoregister)
	.respond(kontoregisterMock, 200)
	.onRequestTo(tags)
	.respond({}, 200)
	.onRequestTo(backendTransaksjon)
	.respond(backendTransaksjonMock, 200)
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
	.onRequestTo(skjerming)
	.respond(skjermingMock, 200)
	.onRequestTo(inst)
	.respond(instMock, 200)
	.onRequestTo(pensjon)
	.respond(pensjonMock, 200)
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

	await scrollThroughPage(testController, 60)

	await testController
		.expect(ReactSelector('AppError').exists)
		.eql(false, 'ErrorBoundary utløst, en komponent kaster Error under visning av ident')
})
