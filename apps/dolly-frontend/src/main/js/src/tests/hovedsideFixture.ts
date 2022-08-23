import { ClientFunction, RequestMock } from 'testcafe'
import { ReactSelector, waitForReact } from 'testcafe-react-selectors'

const miljoer = new RegExp(/\/miljoer/)
const dollyLogg = new RegExp(/\/dolly-logg/)
const azureAuth = new RegExp(/\/oauth2\/authorization\/aad/)
const current = new RegExp(/\/current/)
const profil = new RegExp(/\/profil/)
const bilde = new RegExp(/\/bilde/)
const hentGrupper = new RegExp(/pageSize/)
const spesifikkGruppe = new RegExp(/\/gruppe$/)
const bestillingGruppe = new RegExp(/\/bestilling\/gruppe/)
const bestillingMaler = new RegExp(/\/bestilling\/malbestilling/)
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
}

const nyGruppe = {
	id: 2,
	navn: 'Testcafe testing',
	hensikt: 'Saftig testing med testcafe..',
	opprettetAv: gjeldendeBruker,
	sistEndretAv: gjeldendeBruker,
	datoEndret: '1990-01-12',
	antallIdenter: 0,
	antallIBruk: 0,
	erEierAvGruppe: true,
	favorittIGruppen: false,
	erLaast: false,
	identer: [],
	tags: [],
}

const gjeldendeGruppe = {
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
	tags: [],
}

const cookieMock = RequestMock()
	.onRequestTo(miljoer)
	.respond('["q1","q2","q4","q5","qx","t0","t1","t13","t2","t3","t4","t5","t6","u5"]', 200)
	.onRequestTo(hentGrupper)
	.respond([gjeldendeGruppe], 200)
	.onRequestTo(spesifikkGruppe)
	.respond(nyGruppe, 201, {
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
	.respond(gjeldendeBruker, 200)
	.onRequestTo(profil)
	.respond(gjeldendeProfil, 200)
	.onRequestTo(bilde)
	.respond(null, 404)
	.onRequestTo(bestillingMaler)
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
			'Saftig testing med testcafe..'
		)
		.pressKey('enter')
		.expect(getLocation())
		.contains('gruppe/2')
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
