const uri = `/dolly-backend/api/v1`

const groupBase = `${uri}/gruppe`
const identBase = `${uri}/ident`
const brukerBase = `${uri}/bruker`
const kodeverkBase = `${uri}/kodeverk`
const bestillingBase = `${uri}/bestilling`
const configBase = `${uri}/config`
const openamBase = `${uri}/openam`
const personoppslagBase = `${uri}/pdlperson`
const fasteOrgnummerBase = `${uri}/orgnummer`
const fasteDatasettBase = `${uri}/fastedatasett`
const dokarkivBase = `${uri}/dokarkiv`
const inntektsmeldingBase = `${uri}/inntektsmelding`
const organisasjonBase = `${uri}/organisasjon`

export default class DollyEndpoints {
	static gruppe() {
		return groupBase
	}

	static gruppePaginert(page = 0, pageSize = 10) {
		return `${groupBase}/page/${page}?pageSize=${pageSize}`
	}

	static gruppeById(gruppeId) {
		return `${groupBase}/${gruppeId}`
	}

	static gruppeByIdPaginert(gruppeId, pageNo = 0, pageSize = 10) {
		if (!gruppeId) return null
		return `${groupBase}/${gruppeId}/page/${pageNo}?pageSize=${pageSize}`
	}

	static gruppeByUser(userId) {
		return `${groupBase}?brukerId=${userId}`
	}

	static gruppeBestilling(gruppeId) {
		return `${groupBase}/${gruppeId}/bestilling`
	}

	static gruppeBestillingFraEksisterendeIdenter(gruppeId) {
		return `${groupBase}/${gruppeId}/bestilling/fraidenter`
	}

	static gruppeBestillingImport(gruppeId) {
		return `${groupBase}/${gruppeId}/bestilling/importFraTps`
	}

	static laasGruppe(gruppeId) {
		return `${groupBase}/${gruppeId}/laas`
	}

	static gruppeBestillingStatus(gruppeId) {
		return `${groupBase}/${gruppeId}/bestillingStatus`
	}

	static organisasjonBestilling() {
		return `${organisasjonBase}/bestilling`
	}

	static organisasonStatusByBestillingId(bestillingId) {
		return `${organisasjonBase}/bestilling?bestillingId=${bestillingId}`
	}

	static organisasonStatusByUser(userId) {
		return `${organisasjonBase}/bestillingsstatus?brukerId=${userId}`
	}

	static gjenopprettOrganisasjonBestilling(bestillingId, envs) {
		return `${organisasjonBase}/gjenopprett/${bestillingId}?miljoer=${envs}`
	}

	static deleteOrganisasjonOrgnummer(orgnummer) {
		return `${organisasjonBase}/bestilling/${orgnummer}`
	}

	static gjenopprettGruppe(gruppeId, envs) {
		return `${groupBase}/${gruppeId}/gjenopprett?miljoer=${envs}`
	}

	static bruker() {
		return brukerBase
	}

	static brukerById(brukerId) {
		return `${brukerBase}/${brukerId}`
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

	static importZIdent(ZIdenter) {
		return `${brukerBase}/migrer?navIdenter=${ZIdenter}`
	}

	static kodeverkByNavn(kodeverkNavn) {
		return `${kodeverkBase}/${kodeverkNavn}`
	}

	static dokarkivDokumentinfo(journalpostId, env) {
		return `${dokarkivBase}/${journalpostId}?miljoe=${env}`
	}

	static bestillinger(gruppeId) {
		return `${bestillingBase}/gruppe/${gruppeId}`
	}

	static bestillingStatus(bestillingId) {
		return `${bestillingBase}/${bestillingId}`
	}

	static bestillingMal() {
		return `${bestillingBase}/malbestilling`
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

	static personoppslag(ident) {
		return `${personoppslagBase}/ident/${ident}`
	}

	//TESTPERSON-CONTROLLER
	static slettPerson(ident) {
		return `${identBase}/${ident}`
	}

	static identBeskrivelse(ident) {
		return `${identBase}/${ident}/beskrivelse`
	}

	static leggTilPaaPerson(ident) {
		return `${identBase}/${ident}/leggtilpaaperson`
	}

	static identIbruk(ident, ibruk) {
		return `${identBase}/${ident}/ibruk?iBruk=${ibruk}`
	}

	static naviger(ident) {
		return `${identBase}/naviger/${ident}`
	}

	static kobleIdenter(ident) {
		return `${identBase}/${ident}/relasjon`
	}

	static getFasteDatasettTPSGruppe(gruppe) {
		return `${fasteDatasettBase}/tps/${gruppe}`
	}

	static getPersonnavn() {
		return `${uri}/personnavn`
	}

	static getTransaksjonsid(system, ident, bestillingsid) {
		if (bestillingsid) {
			return `${uri}/transaksjonid?system=${system}&ident=${ident}&bestillingId=${bestillingsid}`
		} else return `${uri}/transaksjonid?ident=${ident}&system=${system}`
	}
}
