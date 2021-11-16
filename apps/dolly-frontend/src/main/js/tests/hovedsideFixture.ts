import { ClientFunction, RequestMock } from 'testcafe'
import { ReactSelector, waitForReact } from 'testcafe-react-selectors'
import { mockHeaders } from '@smartive/testcafe-utils'

const headereJson = {
	'content-type': 'application/json; charset=UTF-8',
	'access-control-allow-origin': 'http://localhost:3000',
	'access-control-allow-credentials': 'true',
	'strict-transport-security': 'max-age=15724800; includeSubDomains',
}

const headereText = Object.apply({}, headereJson)

headereText['content-type'] = 'text/html; charset=UTF-8'

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

const cookieMock = RequestMock()
	.onRequestTo(miljoer)
	.respond(
		'["q1","q2","q4","q5","qx","t0","t1","t13","t2","t3","t4","t5","t6","u5"]',
		200,
		headereJson
	)
	.onRequestTo(dollyLogg)
	.respond(null, 200, headereText)
	.onRequestTo(hentGrupper)
	.respond(
		'[{"id":1,"navn":"Testytest","hensikt":"Testing av testytest","opprettetAv":{"brukerId":"1234-5678-12","brukernavn":"Cafe, Test","epost":"Testcafe@nav.no"},"sistEndretAv":{"brukerId":"1234-5678-12","brukernavn":"Cafe, Test","epost":"Testcafe@nav.no"},"datoEndret":"1980-01-12","antallIdenter":0,"antallIBruk":0,"erEierAvGruppe":true,"favorittIGruppen":false,"erLaast":false}]',
		200,
		headereText
	)
	.onRequestTo(spesifikkGruppe)
	.respond(
		'{"id":1,"navn":"Testytest","hensikt":"Testing av testytest","opprettetAv":{"brukerId":"1234-5678-12","brukernavn":"Cafe, Test","epost":"Testcafe@nav.no"},"sistEndretAv":{"brukerId":"1234-5678-12","brukernavn":"Cafe, Test","epost":"Testcafe@nav.no"},"datoEndret":"1980-01-12","antallIdenter":0,"antallIBruk":0,"erEierAvGruppe":true,"favorittIGruppen":false,"erLaast":false,"identer":[]}',
		200,
		headereText
	)
	.onRequestTo(varslinger)
	.respond('[{"varslingId":"VELKOMMEN_TIL_DOLLY","fom":null,"tom":null}]', 200, headereText)
	.onRequestTo(ids)
	.respond('["VELKOMMEN_TIL_DOLLY"]', 200, headereText)
	.onRequestTo(azureAuth)
	.respond("<script>window.location.href='http://localhost:3000/';</script>", 200, headereText)
	.onRequestTo(dollyLogg)
	.respond(null, 200, headereJson)
	.onRequestTo(bestillingGruppe)
	.respond('[]', 201, headereJson)
	.onRequestTo(current)
	.respond(
		'{"brukerId":"1234-5678-12","brukernavn":"Cafe, Test","brukertype":"BASIC","epost":"testcafe@nav.no","favoritter":[]}',
		200,
		mockHeaders
	)
	.onRequestTo(profil)
	.respond(
		'{"visningsNavn":"Cafe, Test","epost":"Testcafe@nav.no","avdeling":"1234 Testytest","organisasjon":"TESTCAFE","type":"Testbruker"}',
		200,
		mockHeaders
	)
	.onRequestTo(bilde)
	.respond(null, 404, mockHeaders)

const noNewVarslingerMock = RequestMock().onRequestTo(varslinger).respond('[]', 200, headereText)

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
