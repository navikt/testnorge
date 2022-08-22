import Request from '~/service/services/Request'

export default {
	getNavEnheter() {
		const endpoint = `/testnav-norg2-proxy/norg2/api/v1/enhet?enhetStatusListe=AKTIV&oppgavebehandlerFilter=KUN_OPPGAVEBEHANDLERE`
		return Request.get(endpoint)
	},
}
