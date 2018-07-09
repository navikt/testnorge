export const NormalizeTeamListForDropdown = ({ data }) => ({
	options: data.map(team => ({ value: team.id, label: team.navn }))
})

export default {
	NormalizeTeamListForDropdown
}
