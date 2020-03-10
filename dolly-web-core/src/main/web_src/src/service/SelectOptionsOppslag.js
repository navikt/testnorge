import { useAsync } from 'react-use'
import { DollyApi } from '~/service/Api'

export const SelectOptionsOppslag = oppslag => {
	if (oppslag === 'orgnr') {
		const orgInfo = useAsync(async () => {
			const response = await DollyApi.getFasteOrgnummer()
			return response.data
		}, [])
		return orgInfo
	}
}

SelectOptionsOppslag.formatOptions = orgInfo => {
	const liste = orgInfo.value ? orgInfo.value.liste : []
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
}
