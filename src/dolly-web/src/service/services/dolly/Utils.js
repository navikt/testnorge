export const NormalizeTeamListForDropdown = ({ data }) => ({
	options: data.map(team => ({ value: team.id, label: team.navn }))
})

export const NormalizeBrukerListForDropdown = ({ data }) => ({
	options: data.map(bruker => ({ value: bruker.navIdent, label: bruker.navIdent }))
})

export default {
	NormalizeTeamListForDropdown,
	NormalizeBrukerListForDropdown
}
