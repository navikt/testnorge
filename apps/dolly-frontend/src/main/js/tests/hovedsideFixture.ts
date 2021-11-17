import { ClientFunction, RequestMock } from 'testcafe'
import { ReactSelector, waitForReact } from 'testcafe-react-selectors'
import { mockHeaders } from '@smartive/testcafe-utils'

const miljoer = new RegExp(/\/miljoer/)
const dollyLogg = new RegExp(/\/dolly-logg/)
const azureAuth = new RegExp(/\/oauth2\/authorization\/aad/)
const current = new RegExp(/\/current/)
const profil = new RegExp(/\/profil/)
const bilde = new RegExp(/\/bilde/)
const hentGrupper = new RegExp(/\/gruppe\?/)
const spesifikkGruppe = new RegExp(/\/gruppe/)
const bestillingGruppe = new RegExp(/\/bestilling\/gruppe/)
const varslinger = new RegExp(/\/varslinger/)
const ids = new RegExp(/\/ids/)

const gjeldendeProfil = {
	visningsNavn: 'Cafe, Test',
	epost: 'Testcafe@nav.no',
	avdeling: '1234 Testytest',
	organisasjon: 'TESTCAFE',
	type: 'Testbruker',
}

const gjeldendeBruker = {
	brukerId: '1234-5678-12',
	brukernavn: 'Cafe, Test',
	brukertype: 'BASIC',
	epost: 'testcafe@nav.no',
	favoritter: [],
}

const gruppeHentet = {
	id: 1,
	navn: 'Testytest',
	hensikt: 'Testing av testytest',
	opprettetAv: gjeldendeBruker,
	sistEndretAv: gjeldendeBruker,
	datoEndret: '1980-01-12',
	antallIdenter: 0,
	antallIBruk: 0,
	erEierAvGruppe: true,
	favorittIGruppen: false,
	erLaast: false,
	identer: [],
}
const cookieMock = RequestMock()
	.onRequestTo(miljoer)
	.respond('["q1","q2","q4","q5","qx","t0","t1","t13","t2","t3","t4","t5","t6","u5"]', 200)
	.onRequestTo(hentGrupper)
	.respond('[]', 200)
	.onRequestTo(spesifikkGruppe)
	.respond(gruppeHentet, 200)
	.onRequestTo(varslinger)
	.respond('[{"varslingId":"VELKOMMEN_TIL_DOLLY","fom":null,"tom":null}]', 200)
	.onRequestTo(ids)
	.respond('["VELKOMMEN_TIL_DOLLY"]', 200)
	.onRequestTo(azureAuth)
	.respond(null, 200)
	.onRequestTo(dollyLogg)
	.respond(null, 200)
	.onRequestTo(bestillingGruppe)
	.respond(spesifikkGruppe, 201)
	.onRequestTo(current)
	.respond(gjeldendeBruker, 200, mockHeaders)
	.onRequestTo(profil)
	.respond(gjeldendeProfil, 200, mockHeaders)
	.onRequestTo(bilde)
	.respond(null, 404, mockHeaders)

fixture`Hovedside`.page`http://localhost:3000`.requestHooks(cookieMock).beforeEach(async () => {
	await waitForReact()
})

test('Godta varslinger og opprett en gruppe', async (testController) => {
	const getLocation = ClientFunction(() => document.location.href)

	await testController.click(ReactSelector('NavButton').withText('Lukk'))

	await testController
		.click(ReactSelector('NavButton').withText('Ny gruppe'))
		.typeText(ReactSelector('FormikTextInput').withProps({ label: 'NAVN' }), 'Testcafe testing')
		.typeText(
			ReactSelector('FormikTextInput').withProps({ label: 'HENSIKT' }),
			'Saftig testing med testcafe..'
		)
		.click(ReactSelector('NavButton').withText('Opprett og gå til gruppe'))

		.expect(getLocation())
		.contains('gruppe/1')
})

test('Gå inn på gruppe og opprett en ny testperson', async (testController) => {
	await testController
		.click(ReactSelector('NavButton').withText('Lukk'))

		.click(ReactSelector('TableColumn').withKey('0'))
		.typeText(ReactSelector('FormikTextInput').withProps({ label: 'NAVN' }), 'Testcafe testing')
		.typeText(
			ReactSelector('FormikTextInput').withProps({ label: 'HENSIKT' }),
			'Saftig testing med testcafe..'
		)
		.click(ReactSelector('NavButton').withText('Opprett og gå til gruppe'))
})
