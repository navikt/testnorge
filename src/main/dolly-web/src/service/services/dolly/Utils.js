export const NormalizeTeamListForDropdown = ({ data }) => ({
	options: data.map(team => ({ value: team.id, label: team.navn }))
})

export const NormalizeKodeverkForDropdown = ({ data }) => ({
	options: data.koder.map(kode => ({ value: kode.navn, label: kode.term }))
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
