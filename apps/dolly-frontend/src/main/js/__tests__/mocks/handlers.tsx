import { http, HttpResponse } from 'msw'
import { uferdigBestillingMock } from '#/mocks/BasicMocks'

const bestillinger = [uferdigBestillingMock]

export const handlers = [
	http.get('/testnav-kodeverk-service/api/v1/kodeverk/test', () => {
		return HttpResponse.json([])
	}),
	http.get('/dolly-backend/api/v1/bestilling/2', () => {
		return HttpResponse.json(bestillinger?.[0])
	}),
]
