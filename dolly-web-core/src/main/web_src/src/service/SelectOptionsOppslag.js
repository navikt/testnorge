import { useAsync } from 'react-use'
import { DollyApi } from '~/service/Api'
import config from '~/config'
import Api from '~/api'
import _isNil from 'lodash/isNil'

const uri = `${config.services.dollyBackend}`

export const SelectOptionsOppslag = {
	hentOrgnr: () => Api.fetchJson(`${uri}/orgnummer`, 'GET'),

	hentPersonnavn: () => {
		const navnInfo = useAsync(async () => {
			const response = await DollyApi.getPersonnavn()
			return response
		}, [DollyApi.getPersonnavn])
		return navnInfo
	},

	hentGruppe: () => {
		const datasettInfo = useAsync(async () => {
			const response = await DollyApi.getFasteDatasettGruppe('DOLLY')
			return response
		}, [DollyApi.getFasteDatasettGruppe])
		return datasettInfo
	},

	hentInntektsmeldingOptions: enumtype => Api.fetchJson(`${uri}/inntektsmelding/${enumtype}`, 'GET')
}

SelectOptionsOppslag.formatOptions = (type, data) => {
	if (type === 'personnavn') {
		const persondata = data.value && data.value.data ? data.value.data : []
		const options = []
		persondata.length > 0 &&
			persondata.forEach(personInfo => {
				if (!_isNil(personInfo.fornavn)) {
					const mellomnavn = !_isNil(personInfo.mellomnavn) ? ' ' + personInfo.mellomnavn : ''
					const navn = personInfo.fornavn + mellomnavn + ' ' + personInfo.etternavn
					options.push({ value: personInfo.fornavn.toUpperCase(), label: navn.toUpperCase() })
				}
			})
		return options
	} else if (type === 'navnOgFnr') {
		const persondata = data.value && data.value.data ? data.value.data.liste : []
		const options = []
		persondata.length > 0 &&
			persondata.forEach(personInfo => {
				if (!_isNil(personInfo.fornavn)) {
					const mellomnavn = !_isNil(personInfo.mellomnavn) ? ' ' + personInfo.mellomnavn : ''
					const navnOgFnr =
						(personInfo.fornavn + mellomnavn + ' ' + personInfo.etternavn).toUpperCase() +
						': ' +
						personInfo.fnr
					options.push({ value: personInfo.fnr, label: navnOgFnr })
				}
			})
		return options
	}
}
