import { arrayToString } from '@/utils/DataFormatter'

const uri = `/dolly-backend/api/v1`
const kodeverkUri = `/testnav-kodeverk-service/api/v1`
const personUri = '/person-service/api/v2'
const dollySearchUrl = '/testnav-dolly-search-service/api/v1'

const groupBase = `${uri}/gruppe`
const tagsBase = `${uri}/tags`
const identBase = `${uri}/ident`
const brukerBase = `${uri}/bruker`
const kodeverkBase = `${kodeverkUri}/kodeverk`
const bestillingBase = `${uri}/bestilling`
const personoppslagBase = `${personUri}/personer`
const organisasjonBase = `${uri}/organisasjon`
const teamBase = `${uri}/team`

export default class DollyEndpoints {
	static gruppe() {
		return groupBase
	}

	static gruppeById(gruppeId) {
		return `${groupBase}/${gruppeId}`
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

	static gruppeBestillingImportFraPdl(gruppeId) {
		return `${groupBase}/${gruppeId}/bestilling/importfrapdl`
	}

	static laasGruppe(gruppeId) {
		return `${groupBase}/${gruppeId}/laas`
	}

	static sendGruppeTags(gruppeId) {
		return `${tagsBase}/gruppe/${gruppeId}`
	}

	static getIdentTags(ident) {
		return `${tagsBase}/ident/${ident}`
	}

	static organisasjonBestilling() {
		return `${organisasjonBase}/bestilling`
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

	static bestillinger(gruppeId) {
		return `${bestillingBase}/gruppe/${gruppeId}`
	}

	static bestillingerFragment(fragment) {
		return `${bestillingBase}/soekBestilling?fragment=${fragment}`
	}

	static bestillingStatus(bestillingId) {
		return `${bestillingBase}/${bestillingId}`
	}

	static gjenopprettBestilling(bestillingId, envs) {
		return `${bestillingBase}/gjenopprett/${bestillingId}?miljoer=${envs}`
	}

	static removeBestilling(bestillingId, erOrganisasjon) {
		return `${bestillingBase}/stop/${bestillingId}?organisasjonBestilling=${erOrganisasjon}`
	}

	static personoppslag(ident, pdlMiljoe = null) {
		return `${personoppslagBase}/ident/${ident}${pdlMiljoe ? '?pdlMiljoe=' + pdlMiljoe : ''}`
	}

	static personoppslagMange(identer) {
		return `${personoppslagBase}/identer?identer=${arrayToString(identer).replaceAll(' ', '')}`
	}

	//TESTPERSON-CONTROLLER
	static slettPerson(ident) {
		return `${identBase}/${ident}`
	}

	static slettBestilling(bestillingId) {
		return `${bestillingBase}/${bestillingId}`
	}

	static gjenopprettPerson(ident, miljoer) {
		return `${identBase}/gjenopprett/${ident}${miljoer}`
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

	static navigerTilIdent(ident) {
		return `${identBase}/naviger/${ident}`
	}

	static navigerTilBestilling(bestillingId) {
		return `${bestillingBase}/naviger/${bestillingId}`
	}

	static ordre(ident) {
		return `${identBase}/ident/${ident}/ordre`
	}

	static kobleIdenter(ident) {
		return `${identBase}/${ident}/relasjon`
	}

	static getTransaksjonsid(system, ident, bestillingsid) {
		if (bestillingsid) {
			return `${uri}/transaksjonid?system=${system}&ident=${ident}&bestillingId=${bestillingsid}`
		} else return `${uri}/transaksjonid?ident=${ident}&system=${system}`
	}

	static leggTilPaaGruppe(gruppeId) {
		return `${groupBase}/${gruppeId}/leggtilpaagruppe`
	}

	static leggTilPersonIGruppe(gruppeId, ident, master) {
		return `${groupBase}/${gruppeId}/ident/${ident}?master=${master}`
	}

	static flyttPersonerTilGruppe(gruppeId, identer) {
		return `${groupBase}/${gruppeId}/identer/${arrayToString(identer).replaceAll(' ', '')}`
	}

	static endreTilknytningGruppe(gruppeId, brukerId) {
		return `${groupBase}/${gruppeId}/tilknytning/${brukerId}`
	}

	static malBestillingMedId(malId, malNavn) {
		return `${uri}/malbestilling/id/${malId}?malNavn=${malNavn}`
	}

	static malBestillingMedBestillingId(bestillingId, malNavn) {
		return `${uri}/malbestilling?bestillingId=${bestillingId}&malNavn=${malNavn}`
	}

	static opprettMalFraPerson(ident, malNavn) {
		return `${uri}/malbestilling/ident/${ident}?malNavn=${malNavn}`
	}

	static organisasjonMalBestillingMedBestillingId(bestillingId, malNavn) {
		return `${organisasjonBase}/bestilling/malbestilling?bestillingId=${bestillingId}&malNavn=${malNavn}`
	}

	static malBestillingOrganisasjon(malId, malNavn) {
		return `${organisasjonBase}/bestilling/malbestilling/${malId}?malNavn=${malNavn}`
	}

	static personerSearch(registre) {
		return `${dollySearchUrl}/personer${registre ? registre : ''}`
	}

	static opprettTeam() {
		return teamBase
	}

	static redigerTeam(teamId) {
		return `${teamBase}/${teamId}`
	}

	static leggTilFjernBrukerFraTeam(teamId, brukerId) {
		return `${teamBase}/${teamId}/medlem/${brukerId}`
	}

	static slettTeam(teamId) {
		return `${teamBase}/${teamId}`
	}

	static setRepresentererTeam(teamId) {
		return `${brukerBase}/representererTeam/${teamId}`
	}

	static fjernRepresentererTeam() {
		return `${brukerBase}/representererTeam`
	}

	static lagreSoek(soekType: string) {
		return `${uri}/soek?soekType=${soekType}`
	}
}
