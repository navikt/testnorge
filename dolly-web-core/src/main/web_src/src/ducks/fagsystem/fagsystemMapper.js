import _get from 'lodash/get'

export function hentPersonStatus(ident, bestillingStatus) {
	if (!bestillingStatus) return null
	let totalStatus = 'Ferdig'

	bestillingStatus.status.forEach(fagsystem => {
		_get(fagsystem, 'statuser', []).forEach(status => {
			_get(status, 'detaljert', []).forEach(miljoe => {
				_get(miljoe, 'identer', []).forEach(miljoeIdent => {
					if (miljoeIdent === ident) {
						if (status.melding !== 'OK') totalStatus = 'Avvik'
					}
				})
			})
		})
	})
	return totalStatus
}
