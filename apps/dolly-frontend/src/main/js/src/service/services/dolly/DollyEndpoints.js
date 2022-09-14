import Formatters from '~/utils/DataFormatter'

const uri = `/dolly-backend/api/v1`

const groupBase = `${uri}/gruppe`
const tagsBase = `${uri}/tags`
const identBase = `${uri}/ident`
const brukerBase = `${uri}/bruker`
const kodeverkBase = `${uri}/kodeverk`
const udiBase = `${uri}/udistub`
const bestillingBase = `${uri}/bestilling`
const openamBase = `${uri}/openam`
const personoppslagBase = `${uri}/pdlperson`
const fasteOrgnummerBase = `${uri}/orgnummer`
const fasteDatasettBase = `${uri}/fastedatasett`
const dokarkivBase = `${uri}/dokarkiv`
const skjermingBase = `${uri}/skjerming`
const inntektsmeldingBase = `${uri}/inntektsmelding`
const organisasjonBase = `${uri}/organisasjon`

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

	static gruppeBestillingImport(gruppeId) {
		return `${groupBase}/${gruppeId}/bestilling/importFraTps`
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

	static getTags() {
		return `${tagsBase}`
	}

	static getIdentTags(ident) {
		return `${tagsBase}/ident/${ident}`
	}

	static organisasjonBestilling() {
		return `${organisasjonBase}/bestilling`
	}

	static skjermingByIdent(ident) {
		return `${skjermingBase}/${ident}`
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
		return `${personoppslagBase}/identer?identer=${Formatters.arrayToString(identer).replaceAll(
			' ',
			''
		)}`
	}

	static gruppeExcelFil(gruppeId) {
		return `${uri}/excel/gruppe/${gruppeId}`
	}

	static udiPerson(ident) {
		return `${udiBase}/${ident}`
	}

	//TESTPERSON-CONTROLLER
	static slettPerson(ident) {
		return `${identBase}/${ident}`
	}

	static gjenopprettPerson(ident) {
		return `${identBase}/gjenopprett/${ident}`
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

	static getArbeidsforhold(ident, miljoe) {
		return `${uri}/aareg/arbeidsforhold?ident=${ident}&miljoe=${miljoe}`
	}

	static leggTilPersonIGruppe(gruppeId, ident, master) {
		return `${groupBase}/${gruppeId}/ident/${ident}?master=${master}`
	}
}
