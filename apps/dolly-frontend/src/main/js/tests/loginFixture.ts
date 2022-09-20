import { ClientFunction, RequestMock } from 'testcafe'
import { ReactSelector, waitForReact } from 'testcafe-react-selectors'

const miljoer = new RegExp(/\/miljoer/)
const dollyLogg = new RegExp(/\/dolly-logg/)
const azureAuth = new RegExp(/\/oauth2\/authorization\/aad/)
const current = new RegExp(/\/current/)
const varslinger = new RegExp(/\/varslinger/)

const currentBruker = {
	brukerId: '1234-5678-12',
	brukernavn: 'Cafe, Test',
	brukertype: 'BASIC',
	epost: 'testcafe@nav.no',
	favoritter: [],
}

const cookieMock = RequestMock()
	.onRequestTo(miljoer)
	.respond('["t4"]', 200)
	.onRequestTo(dollyLogg)
	.respond(null, 200)
	.onRequestTo(azureAuth)
	.respond("<script>window.location.href='http://localhost:3000';</script>", 200)
	.onRequestTo(current)
	.respond(currentBruker, 200)
	.onRequestTo(varslinger)
	.respond([], 200)

fixture`Loginside`.page`http://localhost:3000/login`
	.requestHooks(cookieMock)
	.beforeEach(async () => {
		await waitForReact()
	})

test('Trykk logg inn med Nav og bli redirected til Dolly hovedside', async (testController) => {
	const getLocation = ClientFunction(() => document.location.href)

	await testController
		.click(ReactSelector('NavButton').withText('Logg inn med NAV epost'))
		.expect(getLocation())
		.notContains('login')
})
