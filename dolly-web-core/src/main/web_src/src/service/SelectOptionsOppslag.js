import { useAsync } from 'react-use'
import { DollyApi } from '~/service/Api'
import _isNil from "lodash/isNil";

export const SelectOptionsOppslag = oppslag => {
	if (oppslag === 'orgnr') {
		const orgInfo = useAsync(async () => {
			const response = await DollyApi.getFasteOrgnummer()
			return response.data
		}, [])
		return orgInfo
	}else if (oppslag === 'personnavn'){
		const navnInfo = useAsync(async () => {
			const response = await DollyApi.getPersonnavn()
			return response
		}, [DollyApi.getPersonnavn])
		return navnInfo
	} else if (oppslag === 'fasteDatasettTps'){
		const datasettInfo = useAsync(async () => {
			const response = await DollyApi.getFasteDatasettTPS()
			return response
		}, [DollyApi.getFasteDatasettTPS])
		return datasettInfo
	}
}

SelectOptionsOppslag.formatOptions = (type, data) => {
	if(type === 'orgInfo'){
		const liste = data.value ? data.value.liste : []
		const options = []
		liste.length > 0 &&
		liste.forEach(org => {
			org.juridiskEnhet &&
			options.push({
				value: org.orgnr,
				label: `${org.orgnr} (${org.enhetstype}) - ${org.navn}`,
				juridiskEnhet: org.juridiskEnhet
			})
		})
		return options
	}else if(type === 'personnavn'){
		const persondata = data.value && data.value.data ? data.value.data : []
		const options = []
		persondata.length > 0 &&
		persondata.forEach(personInfo => {
			if (!_isNil(personInfo.fornavn)) {
				const mellomnavn = !_isNil(personInfo.mellomnavn) ? " " + personInfo.mellomnavn : "";
				const navn = personInfo.fornavn + mellomnavn + " " + personInfo.etternavn;
				options.push({value: personInfo.fornavn.toUpperCase(), label: navn.toUpperCase()});
			}
		})
		return options;
	}else if(type === 'navnOgFnr'){
		const persondata = data.value && data.value.data ? data.value.data : []
		const options = []
		persondata.length > 0 &&
		persondata.forEach(personInfo => {
			if (!_isNil(personInfo.fornavn)) {
				const mellomnavn = !_isNil(personInfo.mellomnavn) ? " " + personInfo.mellomnavn : "";
				const navnOgFnr = (personInfo.fornavn + mellomnavn + " " + personInfo.etternavn).toUpperCase()
					+ ": " + personInfo.fnr;
				options.push({value: personInfo.fnr, label: navnOgFnr});
			}
		})
		return options;
	}

}
