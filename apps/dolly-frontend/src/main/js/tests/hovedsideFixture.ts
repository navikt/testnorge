// noinspection TypeScriptValidateTypes

import { ClientFunction, RequestMock } from 'testcafe'
import { ReactSelector, waitForReact } from 'testcafe-react-selectors'
import { clickAllSiblings } from './util/TestcafeUtils'
import {
	gjeldendeBrukerMock,
	gjeldendeGruppeMock,
	gjeldendeProfilMock,
	kodeverkMock,
	malerMock,
	miljoeMock,
	nyGruppeMock,
	varslingerRequestMock,
	varslingerVelkommenResponseMock,
} from './util/TestcafeMocks'

const miljoer = new RegExp(/\/miljoer/)
const dollyLogg = new RegExp(/\/dolly-logg/)
const azureAuth = new RegExp(/\/oauth2\/authorization\/aad/)
const current = new RegExp(/current/)
const bilde = new RegExp(/testnorge-profil-api\/api\/v1\/profil\/bilde$/)
const profil = new RegExp(/testnorge-profil-api\/api\/v1\/profil$/)
const hentGrupper = new RegExp(/gruppe\?brukerId/)
const hentGruppe = new RegExp(/\/api\/v1\/gruppe\/1/)
const spesifikkGruppe = new RegExp(/\/gruppe$/)
const tags = new RegExp(/\/tags$/)
const kodeverk = new RegExp(/\/kodeverk\//)
const brregStub = new RegExp(/testnav-brregstub/)
const bestillingMaler = new RegExp(/\/bestilling\/malbestilling/)
const varslinger = new RegExp(/\/varslinger/)
const ids = new RegExp(/\/ids/)

const remainingCallsResponseOk = new RegExp(/api/)

const cookieMock = RequestMock()
	.onRequestTo(miljoer)
	.respond(miljoeMock, 200)
	.onRequestTo(hentGrupper)
	.respond([gjeldendeGruppeMock], 200)
	.onRequestTo(hentGruppe)
	.respond(gjeldendeGruppeMock, 200)
	.onRequestTo(spesifikkGruppe)
	.respond(nyGruppeMock, 201)
	.onRequestTo(varslinger)
	.respond(varslingerVelkommenResponseMock, 200)
	.onRequestTo(ids)
	.respond(varslingerRequestMock, 200)
	.onRequestTo(azureAuth)
	.respond(null, 200)
	.onRequestTo(dollyLogg)
	.respond(null, 200)
	.onRequestTo(current)
	.respond(gjeldendeBrukerMock, 200)
	.onRequestTo(profil)
	.respond(gjeldendeProfilMock, 200)
	.onRequestTo(bilde)
	.respond(null, 404)
	.onRequestTo(bestillingMaler)
	.respond(malerMock, 200)
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

test('Godta varslinger, teste avbryt og opprett en gruppe', async (testController) => {
	const getLocation = ClientFunction(() => document.location.href)

	await testController.click(ReactSelector('NavButton').withText('Lukk'))

	await testController
		.click(ReactSelector('NavButton').withText('Ny gruppe'))
		.click(ReactSelector('NavButton').withText('Avbryt'))

		.click(ReactSelector('NavButton').withText('Ny gruppe'))
		.typeText(ReactSelector('FormikTextInput').withProps({ label: 'NAVN' }), 'Testcafe testing')
		.typeText(
			ReactSelector('FormikTextInput').withProps({ label: 'HENSIKT' }),
			'Automatisert testing med testcafe..'
		)
		.pressKey('enter')
		.expect(getLocation())
		.contains('gruppe/2')
})

test('Gå inn på gruppe og opprett en ny testperson', async (testController) => {
	await testController
		.click(ReactSelector('NavButton').withText('Lukk'))

		.click(ReactSelector('TableRow').withKey('1'))
		.click(ReactSelector('NavButton').withText('Opprett personer'))

		.click(ReactSelector('ToggleGroupItem').withText('Eksisterende person'))
		.click(ReactSelector('ToggleGroupItem').withText('Ny person'))

	const malerCheckbox = ReactSelector('DollyCheckbox').withProps({ name: 'aktiver-maler' })
	const brukerSelect = ReactSelector('Select').withProps({ label: 'Bruker' })
	const malerSelect = ReactSelector('Select').withProps({ label: 'Maler' })

	await testController
		.click(malerCheckbox)
		.expect(brukerSelect.getReact(({ props }) => props.disabled))
		.eql(false, 'Maler checkbox på opprett person ble ikke enablet etter klikk.')

		.click(brukerSelect)
		.click(malerSelect)

		.click(malerCheckbox)
		.expect(brukerSelect.getReact(({ props }) => props.disabled))
		.eql(true, 'Maler checkbox på opprett person ble ikke disablet etter klikk.')

		.click(ReactSelector('NavButton').withText('Start bestilling'))

	await clickAllSiblings(testController, ReactSelector('LinkButton').withText('Velg alle'))

	await testController
		.click(ReactSelector('NavButton').withText('Videre'))
		.wait(5000)
		.expect(ReactSelector('AppError').exists)
		.eql(
			false,
			'ErrorBoundary utløst, en komponent kaster Error under ident -> opprett person -> steg 2.'
		)
})
