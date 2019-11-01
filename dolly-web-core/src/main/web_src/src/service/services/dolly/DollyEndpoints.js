import config from '~/config'

const uri = `${config.services.dollyBackend}`

const groupBase = `${uri}/gruppe`
const teamBase = `${uri}/team`
const brukerBase = `${uri}/bruker`
const kodeverkBase = `${uri}/kodeverk`
const bestillingBase = `${uri}/bestilling`
const configBase = `${uri}/config`
const openamBase = `${uri}/openam`
const norg2Base = `${uri}/norg2`
const aaregBase = `${uri}/aareg/arbeidsforhold`
const personoppslagBase = `${uri}/personoppslag`

export default class DollyEndpoints {
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

	static gruppeBestilling(gruppeId) {
		return `${groupBase}/${gruppeId}/bestilling`
	}

	static gruppeBestillingFraEksisterendeIdenter(gruppeId) {
		return `${groupBase}/${gruppeId}/bestilling/fraidenter`
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

	static teamRemoveMember(teamId, userId) {
		return `${teamBase}/${teamId}/deleteMedlem?navIdent=${userId}`
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

	static addFavorite() {
		return `${brukerBase}/leggTilFavoritt`
	}

	static removeFavorite() {
		return `${brukerBase}/fjernFavoritt`
	}

	static kodeverkByNavn(kodeverkNavn) {
		return `${kodeverkBase}/${kodeverkNavn}`
	}

	static bestillinger(gruppeId) {
		return `${bestillingBase}/gruppe/${gruppeId}`
	}

	static bestillingStatus(bestillingId) {
		return `${bestillingBase}/${bestillingId}`
	}

	static bestillingMal() {
		return bestillingBase + '/malbestilling'
	}

	static gjenopprettBestilling(bestillingId, envs) {
		return `${bestillingBase}/gjenopprett/${bestillingId}?miljoer=${envs}`
	}

	static config() {
		return configBase
	}

	static openAmBestilling(bestillingId) {
		return `${openamBase}/bestilling/${bestillingId}?bestillingId=${bestillingId}`
	}

	static removeBestilling(bestillingId) {
		return `${bestillingBase}/stop/${bestillingId}`
	}

	static removeTestIdent(gruppeId, identId) {
		return `${groupBase}/${gruppeId}/slettTestident?ident=${identId}`
	}

	static enhetByTknr(tknr) {
		return `${norg2Base}/enhet/${tknr}`
	}

	static aareg() {
		return aaregBase
	}

	static arbeidsforholdByIdent(ident, env) {
		return `${aaregBase}?ident=${ident}&environment=${env}`
	}

	static personoppslag(ident) {
		return `${personoppslagBase}/ident/${ident}`
	}
}
