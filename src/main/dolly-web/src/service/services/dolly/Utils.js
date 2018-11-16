import DataFormatter from '~/utils/DataFormatter'

//liste over koder som må eksludert pga ingen støtte i TPSF/dolly
const _excludeList = ['NULL', 'GLAD']

export const NormalizeTeamListForDropdown = ({ data }) => ({
	options: data.map(team => ({ value: team.id, label: team.navn }))
})

export const NormalizeKodeverkForDropdown = ({ data }) => ({
	options: data.koder.filter(val => !_excludeList.includes(val.value)).map(kode => ({
		value: kode.value,
		label: DataFormatter.kodeverkLabel(kode.label)
	}))
})

export const NormalizeBrukerListForDropdown = (data, teamMembers) => {
	const options = data.reduce((filtered, bruker) => {
		if (!teamMembers.includes(bruker.navIdent)) {
			filtered.push({ value: bruker.navIdent, label: bruker.navIdent })
		}
		return filtered
	}, [])
	return { options }
}

export default {
	NormalizeTeamListForDropdown,
	NormalizeBrukerListForDropdown,
	NormalizeKodeverkForDropdown
}
