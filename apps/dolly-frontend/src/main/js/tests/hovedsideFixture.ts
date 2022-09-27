// noinspection TypeScriptValidateTypes

import { ClientFunction, RequestMock } from 'testcafe'
import { ReactSelector, waitForReact } from 'testcafe-react-selectors'
import { clickAllSiblings } from './util/TestcafeUtils'
import {
	gjeldendeBrukerMock,
	gjeldendeGruppeMock,
	gjeldendeProfilMock,
	nyGruppeMock,
} from './util/TestcafeMocks'

const miljoer = new RegExp(/\/miljoer/)
const dollyLogg = new RegExp(/\/dolly-logg/)
const azureAuth = new RegExp(/\/oauth2\/authorization\/aad/)
const current = new RegExp(/current/)
const profil = new RegExp(/\/profil/)
const bilde = new RegExp(/\/bilde/)
const hentGrupper = new RegExp(/gruppe\?brukerId/)
const hentGruppe = new RegExp(/\/api\/v1\/gruppe\/1/)
const spesifikkGruppe = new RegExp(/\/gruppe$/)
const tags = new RegExp(/\/tags$/)
const kodeverk = new RegExp(/\/kodeverk\//)
const bestillingGruppe = new RegExp(/\/bestilling\/gruppe/)
const organisasjonerForvalter = new RegExp(/testnav-organisasjon-forvalter/)
const organisasjonerFasteData = new RegExp(/testnav-organisasjon-faste-data-service/)
const pensjon = new RegExp(/pensjon-testdata-facade/)
const bestillingMaler = new RegExp(/\/bestilling\/malbestilling/)
const varslinger = new RegExp(/\/varslinger/)
const ids = new RegExp(/\/ids/)

const cookieMock = RequestMock()
	.onRequestTo(miljoer)
	.respond('["q1","q2","q4","q5","qx","t0","t1","t13","t2","t3","t4","t5","t6","u5"]', 200)
	.onRequestTo(hentGrupper)
	.respond([gjeldendeGruppeMock], 200)
	.onRequestTo(hentGruppe)
	.respond(gjeldendeGruppeMock, 200, {
		'content-type': 'application/json;charset=UTF-8',
	})
	.onRequestTo(spesifikkGruppe)
	.respond(nyGruppeMock, 201, {
		'content-type': 'application/json;charset=UTF-8',
	})
	.onRequestTo(varslinger)
	.respond([{ varslingId: 'VELKOMMEN_TIL_DOLLY', fom: null, tom: null }], 200)
	.onRequestTo(ids)
	.respond(['VELKOMMEN_TIL_DOLLY'], 200)
	.onRequestTo(azureAuth)
	.respond(null, 200)
	.onRequestTo(dollyLogg)
	.respond(null, 200)
	.onRequestTo(bestillingGruppe)
	.respond([], 200)
	.onRequestTo(current)
	.respond(gjeldendeBrukerMock, 200)
	.onRequestTo(profil)
	.respond(gjeldendeProfilMock, 200)
	.onRequestTo(bilde)
	.respond(null, 404)
	.onRequestTo(bestillingMaler)
	.respond({ malbestillinger: ['Cafe, Test', []] }, 200)
	.onRequestTo(tags)
	.respond({}, 200)
	.onRequestTo(organisasjonerForvalter)
	.respond({}, 200)
	.onRequestTo(organisasjonerFasteData)
	.respond([], 200)
	.onRequestTo(pensjon)
	.respond({}, 200)
	.onRequestTo(kodeverk)
	.respond({}, 200)

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
			'Saftig testing med testcafe..'
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
		.eql(false, 'Maler checkbox på opprett person ble ikke enablet etter klikk')

		.click(brukerSelect)
		.click(malerSelect)

		.click(malerCheckbox)
		.expect(brukerSelect.getReact(({ props }) => props.disabled))
		.eql(true, 'Maler checkbox på opprett person ble ikke disablet etter klikk')

		.click(ReactSelector('NavButton').withText('Start bestilling'))

	await clickAllSiblings(testController, ReactSelector('LinkButton').withText('Velg alle'))

	await testController
		.click(ReactSelector('NavButton').withText('Videre'))
		.wait(7000)
		.expect(ReactSelector('AppError').exists)
		.eql(
			false,
			'ErrorBoundary utløst, en komponent kaster Error under ident -> opprett person -> steg 2'
		)
		.wait(30000) // TODO: Midlertidig timeout, SLETT MEG!
})
