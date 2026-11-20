import { http, HttpResponse } from 'msw/http'
import { gjeldendeAzureBrukerMock, uferdigBestillingMock } from '#/mocks/BasicMocks'

const bestillinger = [uferdigBestillingMock]

export const handlers = [
	http.get('/testnav-kodeverk-service/api/v1/kodeverk/test', () => {
		return HttpResponse.json([])
	}),
	http.get('/dolly-backend/api/v1/bestilling/2', () => {
		return HttpResponse.json(bestillinger?.[0])
	}),
	http.get('/dolly-backend/api/v1/bruker/current', () => {
		return HttpResponse.json(gjeldendeAzureBrukerMock)
	}),
	http.get('/session/csrf', () => {
		return HttpResponse.json({
			headerName: 'X-CSRF-TOKEN',
			parameterName: '_csrf',
			token: 'test-token',
		})
	}),
	http.get('/testnav-dollystatus/api/v1/statusoversikt', () => {
		return HttpResponse.json({
			status: 'OK',
			kjoringPagaar: false,
			sistOppdatert: new Date().toISOString(),
			nesteAutomatiskeKjoring: new Date(Date.now() + 5 * 60 * 60 * 1000).toISOString(),
			nesteManuelleKjoringTidligst: null,
			problemer: [],
		})
	}),
	http.post('/testnav-dollystatus/api/v1/testkjoringer', () => {
		return HttpResponse.json({ runId: 'test-run' }, { status: 202 })
	}),
	http.post('/testnav-dollystatus/api/v1/testkjoringer/feilede', () => {
		return HttpResponse.json({ runId: 'test-run' }, { status: 202 })
	}),
]
