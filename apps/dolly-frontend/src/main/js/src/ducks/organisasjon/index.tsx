import { createActions } from 'redux-actions'
import { DollyApi } from '~/service/Api'
import { handleActions } from '../utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'
import { Organisasjon } from '~/service/services/organisasjonforvalter/types'
import { getAdresseWithAdressetype } from '~/components/fagsystem/brregstub/form/partials/EgneOrganisasjoner'
import { LOCATION_CHANGE } from 'redux-first-history'

const getJuridiskEnhet = (orgnr: string, enheter: Organisasjon[]) => {
	for (const enhet of enheter) {
		if (enhet.underenheter && enhet.underenheter.length > 0) {
			for (const underenhet of enhet.underenheter) {
				if (underenhet.organisasjonsnummer === orgnr) {
					return enhet.organisasjonsnummer
				}
				const juridisk: string = getJuridiskEnhet(orgnr, enhet.underenheter)
				if (juridisk !== '') {
					return juridisk
				}
			}
		}
	}
	return ''
}

export const actions = createActions(
	{
		getOrganisasjonerPaaBruker: DollyApi.getAlleOrganisasjonerPaaBruker,
	},
	{
		prefix: 'organisasjon',
	}
)

const initialState = {
	bestillinger: null as [],
	organisasjoner: null as Organisasjon[],
	egneOrganisasjoner: null as Organisasjon[],
}

const addAlleVirksomheter = (virksomheter: Organisasjon[], organisasjoner: Organisasjon[]) => {
	for (let org of organisasjoner) {
		virksomheter.push(org)
		if (org.underenheter && org.underenheter.length > 0) {
			addAlleVirksomheter(virksomheter, org.underenheter)
		}
	}
}

export default handleActions(
	{
		[LOCATION_CHANGE](state: any, action: any) {
			return initialState
		},
		[onSuccess(actions.getOrganisasjonerPaaBruker)](
			state: { egneOrganisasjoner: any[] },
			action: { payload: Organisasjon[] }
		) {
			const response = action.payload
			if (response.length === 0) {
				state.egneOrganisasjoner = []
			} else {
				const egneOrg: Organisasjon[] = []
				addAlleVirksomheter(egneOrg, response)

				state.egneOrganisasjoner = egneOrg.map((org: Organisasjon) => {
					const fAdresser = getAdresseWithAdressetype(org.adresser, 'FADR')
					const pAdresser = getAdresseWithAdressetype(org.adresser, 'PADR')
					const juridiskEnhet = getJuridiskEnhet(org.organisasjonsnummer, response)
					return {
						value: org.organisasjonsnummer,
						label: `${juridiskEnhet ? '   ' : ''}${org.organisasjonsnummer} (${org.enhetstype}) - ${
							org.organisasjonsnavn
						} ${juridiskEnhet ? '' : '   '}`,
						orgnr: org.organisasjonsnummer,
						navn: org.organisasjonsnavn,
						enhetstype: org.enhetstype,
						forretningsAdresse: fAdresser.length > 0 ? fAdresser[0] : null,
						postAdresse: pAdresser.length > 0 ? pAdresser[0] : null,
						juridiskEnhet: juridiskEnhet,
					}
				})
			}
		},
	},
	initialState
)
