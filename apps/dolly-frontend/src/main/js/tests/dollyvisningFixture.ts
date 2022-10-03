// noinspection TypeScriptValidateTypes

import { RequestMock } from 'testcafe'
import { ReactSelector, waitForReact } from 'testcafe-react-selectors'
import {
	backendBestillingerMock,
	gjeldendeBrukerMock,
	gjeldendeGruppeMock,
	gjeldendeProfilMock,
	kodeverkMock,
	malerMock,
	miljoeMock,
	nyGruppeMock,
} from './util/TestcafeMocks'
import { pdlBulkpersonerMock, pdlForvalterMock } from './util/TestcafePdlMocks'

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
const tags = new RegExp(/\/tags$/)
const kodeverk = new RegExp(/\/kodeverk\//)
const brregStub = new RegExp(/testnav-brregstub/)
const bestillingMaler = new RegExp(/\/bestilling\/malbestilling/)

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
	.onRequestTo(tags)
	.respond({}, 200)
	.onRequestTo(brregStub)
	.respond({}, 200)
	.onRequestTo(kodeverk)
	.respond(kodeverkMock, 200)
	.onRequestTo(remainingCallsResponseOk)
	.respond([], 200)

fixture`Hovedside`.page`http://localhost:3000`.requestHooks(cookieMock).beforeEach(async () => {
	await waitForReact()
})

test('Gå inn på testgruppe og åpne en ident med data i alle fagsystem', async (testController) => {
	await testController
		.click(ReactSelector('TableRow').withKey('1'))
		.click(ReactSelector('TableRow').withKey('12345678912'))
		.wait(5000)
		.expect(ReactSelector('AppError').exists)
		.eql(false, 'ErrorBoundary utløst, en komponent kaster Error under visning av ident')
})
