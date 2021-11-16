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

const cookieMock = RequestMock()
	.onRequestTo(miljoer)
	.respond('["t4"]', 200, headereJson)
	.onRequestTo(dollyLogg)
	.respond(null, 200, headereText)
	.onRequestTo(azureAuth)
	.respond("<script>window.location.href='http://localhost:3000/';</script>", 200, headereText)
	.onRequestTo(current)
	.respond(
		'{"brukerId":"1234-5678-12","brukernavn":"Cafe, Test","brukertype":"BASIC","epost":"testcafe@nav.no","favoritter":[]}',
		200,
		mockHeaders
	)

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
