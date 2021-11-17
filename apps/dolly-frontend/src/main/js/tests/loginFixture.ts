import { ClientFunction, RequestMock } from 'testcafe'
import { ReactSelector, waitForReact } from 'testcafe-react-selectors'
import { mockHeaders } from '@smartive/testcafe-utils'

const miljoer = new RegExp(/\/miljoer/)
const dollyLogg = new RegExp(/\/dolly-logg/)
const azureAuth = new RegExp(/\/oauth2\/authorization\/aad/)
const current = new RegExp(/\/current/)

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
	.respond("<script>window.location.href='http://localhost:3000/';</script>", 200)
	.onRequestTo(current)
	.respond(currentBruker, 200, mockHeaders)

fixture`Loginside`.page`http://localhost:3000/login`
	.requestHooks(cookieMock)
	.beforeEach(async () => {
		await waitForReact()
	})

test('Trykk logg inn med Nav og bli redirected til Dolly hovedside', async (testController) => {
	const getLocation = ClientFunction(() => document.location.href)

	await testController
		.click(ReactSelector('NavButton').withProps({ type: 'hoved' }))
		.expect(getLocation())
		.notContains('login')
})
