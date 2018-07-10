// TODO: lag config for denne
const uri = 'http://localhost:8080/api/v1'

// TODO: bytte /testgruppe til /gruppe når API er oppdatert
const groupBase = `${uri}/testgruppe`
const teamBase = `${uri}/team`
const brukerBase = `${uri}/bruker`

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

	static teamMedlemmer(teamId) {
		return `${teamBase}/${teamId}/medlemmer`
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
}

export default DollyEndpoints
