import { LOCATION_CHANGE } from 'connected-react-router'
import { createActions } from 'redux-actions'
import { DollyApi, OrgforvalterApi } from '~/service/Api'
import { handleActions } from '../utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'
import { Organisasjon } from '~/service/services/organisasjonforvalter/types'
import { Dispatch } from 'redux'

const getJuridiskEnhet = (orgnr: string, enheter: Organisasjon[]) => {
	for (let enhet of enheter) {
		if (enhet.underenheter && enhet.underenheter.length > 0) {
			for (let underenhet of enhet.underenheter) {
				if (underenhet.organisasjonsnummer === orgnr) return enhet.organisasjonsnummer
			}
		}
	}
	return ''
}

export const actions = createActions(
	{
		getOrganisasjonBestilling: DollyApi.getOrganisasjonsnummerByUserId,
		getOrganisasjoner: OrgforvalterApi.getOrganisasjonerInfo,
		getOrganisasjonerPaaBruker: OrgforvalterApi.getAlleOrganisasjonerPaaBruker,
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

export default handleActions(
	{
		[LOCATION_CHANGE](state: any, action: any) {
			return initialState
		},
		[onSuccess(actions.getOrganisasjonBestilling)](
			state: { bestillinger: [] },
			action: { payload: { data: [] } }
		) {
			state.bestillinger = action.payload.data
		},
		[onSuccess(actions.getOrganisasjoner)](
			state: { organisasjoner: [] },
			action: { payload: { data: [] } }
		) {
			state.organisasjoner = action.payload.data
		},
		// @ts-ignore
		[onSuccess(actions.getOrganisasjonerPaaBruker)](
			state: { egneOrganisasjoner: any[] },
			action: { payload: Organisasjon[] }
		) {
			const response = action.payload
			if (response.length === 0) return []
			state.egneOrganisasjoner = []
			// state.egneOrganisasjoner = response.map((org: Organisasjon) => {
			// 	const fAdresser = getAdresseWithAdressetype(org.adresser, 'FADR')
			// 	const pAdresser = getAdresseWithAdressetype(org.adresser, 'PADR')
			//
			// 	return {
			// 		value: org.organisasjonsnummer,
			// 		label: `${org.organisasjonsnummer} (${org.enhetstype}) - ${org.organisasjonsnavn}`,
			// 		orgnr: org.organisasjonsnummer,
			// 		navn: org.organisasjonsnavn,
			// 		enhetstype: org.enhetstype,
			// 		forretningsAdresse: fAdresser.length > 0 ? fAdresser[0] : null,
			// 		postAdresse: pAdresser.length > 0 ? pAdresser[0] : null,
			// 		juridiskEnhet: getJuridiskEnhet(org.organisasjonsnummer, response),
			// 	}
			// })
		},
	},
	initialState
)

export const fetchOrganisasjoner = (dispatch: Dispatch) => async (brukerId: string) => {
	const { data } = await actions.getOrganisasjonBestilling(brukerId).payload
	let orgNumre: string[] = []
	data.forEach((org: { ferdig: boolean; organisasjonNummer: string }) => {
		if (org.ferdig && org.organisasjonNummer !== 'NA') return orgNumre.push(org.organisasjonNummer)
	})
	dispatch(actions.getOrganisasjoner(orgNumre))
}
