import { runningE2ETest } from '@/service/services/Request'

export const initialDokarkiv = {
	tittel: runningE2ETest() ? 'test' : '',
	tema: runningE2ETest() ? 'test ' : '',
	sak: {
		sakstype: 'GENERELL_SAK',
		fagsaksystem: '',
		fagsakId: '',
	},
	kanal: 'SKAN_IM',
	ferdigstill: true,
	journalfoerendeEnhet: undefined,
	dokumenter: [
		{
			tittel: runningE2ETest() ? 'test' : '',
			brevkode: runningE2ETest() ? 'test' : '',
		},
	],
}

export const initialDigitalInnsending = {
	tittel: '',
	tema: '',
	kanal: 'NAV_NO',
	sak: {
		sakstype: 'GENERELL_SAK',
		fagsaksystem: '',
		fagsakId: '',
	},
	ferdigstill: true,
	avsenderMottaker: {
		id: '',
		navn: '',
		idType: '',
	},
	journalfoerendeEnhet: undefined,
	dokumenter: [
		{
			tittel: '',
			brevkode: '',
		},
	],
}
