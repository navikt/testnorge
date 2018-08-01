// TODO: lag config for denne
const uri = 'http://localhost:8080/api/v1'

const groupBase = `${uri}/gruppe`
const teamBase = `${uri}/team`
const brukerBase = `${uri}/bruker`
const bestillingBase = `${uri}/bestilling`

class DollyEndpoints {
	static gruppe() {
		return groupBase
	}

	static gruppeById(gruppeId) {
		return `${groupBase}/${gruppeId}`
	}

	static gruppeByUser(userId) {
		return `${groupBase}?navIdent=${userId}`
	}

	static gruppeByTeam(teamId) {
		return `${groupBase}?teamId=${teamId}`
	}

	static gruppeAttributter(gruppeId) {
		return `${groupBase}/${gruppeId}/attributter`
	}

	static gruppeIdenter(gruppeId) {
		return `${groupBase}/${gruppeId}/identer`
	}

	static gruppeBestilling(gruppeId) {
		return `${groupBase}/${gruppeId}/bestilling`
	}

	static gruppeBestillingStatus(gruppeId) {
		return `${groupBase}/${gruppeId}/bestillingStatus`
	}

	static team() {
		return teamBase
	}

	static teamByUser(userId) {
		return `${teamBase}?navIdent=${userId}`
	}

	static teamById(teamId) {
		return `${teamBase}/${teamId}`
	}

	static teamAddMember(teamId) {
		return `${teamBase}/${teamId}/leggTilMedlemmer`
	}

	static teamRemoveMember(teamId) {
		return `${teamBase}/${teamId}/fjernMedlemmer`
	}

	static bruker() {
		return brukerBase
	}

	static brukerById(brukerId) {
		return `${brukerBase}/brukerId`
	}

	static currentBruker() {
		return `${brukerBase}/current`
	}

	static bestillingStatus(bestillingId) {
		return `${bestillingBase}/${bestillingId}`
	}
}

export default DollyEndpoints
