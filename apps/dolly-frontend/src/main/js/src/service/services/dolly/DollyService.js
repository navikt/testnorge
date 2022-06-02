import Request from '~/service/services/Request'
import Endpoints from './DollyEndpoints'

export default {
	getSkjerming(ident) {
		return Request.get(Endpoints.skjermingByIdent(ident))
	},

	getGruppeById(gruppeId) {
		return Request.get(Endpoints.gruppeById(gruppeId))
	},

	getGruppeByUserId(userId) {
		return Request.get(Endpoints.gruppeByUser(userId))
	},

	createGruppe(data) {
		return Request.post(Endpoints.gruppe(), data)
	},

	updateGruppe(gruppeId, data) {
		return Request.put(Endpoints.gruppeById(gruppeId), data)
	},

	deleteGruppe(gruppeId) {
		return Request.delete(Endpoints.gruppeById(gruppeId))
	},

	createBestilling(gruppeId, data) {
		return Request.post(Endpoints.gruppeBestilling(gruppeId), data)
	},

	createBestillingFraEksisterendeIdenter(gruppeId, data) {
		return Request.post(Endpoints.gruppeBestillingFraEksisterendeIdenter(gruppeId), data)
	},

	updateGruppeLaas(gruppeId, data) {
		return Request.put(Endpoints.laasGruppe(gruppeId), data)
	},

	updateGruppeSendTags(gruppeId, data) {
		return Request.post(Endpoints.sendGruppeTags(gruppeId), data)
	},

	gjenopprettGruppe(gruppeId, envs) {
		return Request.put(Endpoints.gjenopprettGruppe(gruppeId, envs))
	},

	//* Ident
	updateIdentBeskrivelse(ident, beskrivelse) {
		return Request.put(Endpoints.identBeskrivelse(ident), { beskrivelse })
	},

	updateIdentIbruk(ident, ibruk) {
		return Request.put(Endpoints.identIbruk(ident, ibruk))
	},

	createRelasjon(ident, data) {
		return Request.put(Endpoints.kobleIdenter(ident), data)
	},

	createBestillingLeggTilPaaPerson(ident, data) {
		return Request.put(Endpoints.leggTilPaaPerson(ident), data)
	},

	navigerTilPerson(ident) {
		return Request.get(Endpoints.navigerTilIdent(ident))
	},

	navigerTilBestilling(bestillingId) {
		return Request.get(Endpoints.navigerTilBestilling(bestillingId))
	},

	sendOrdre(ident) {
		return Request.post(Endpoints.ordre(ident))
	},

	addFavorite(groupId) {
		return Request.put(Endpoints.addFavorite(), { gruppeId: groupId })
	},

	removeFavorite(groupId) {
		return Request.put(Endpoints.removeFavorite(), { gruppeId: groupId })
	},

	importZIdent(ZIdenter) {
		return Request.put(Endpoints.importZIdent(ZIdenter))
	},

	//* Kodeverk
	getKodeverkByNavn(kodeverkNavn) {
		return Request.get(Endpoints.kodeverkByNavn(kodeverkNavn))
	},

	//* Udistub
	getUdiPerson(ident) {
		return Request.get(Endpoints.udiPerson(ident))
	},

	//* Bestilling
	getBestillinger(gruppeId) {
		return Request.get(Endpoints.bestillinger(gruppeId))
	},

	getBestillingerFragment(fragment) {
		return Request.get(Endpoints.bestillingerFragment(fragment))
	},

	gjenopprettBestilling(bestillingId, envs) {
		return Request.post(Endpoints.gjenopprettBestilling(bestillingId, envs))
	},

	postOpenAmBestilling(bestillingId) {
		return Request.post(Endpoints.openAmBestilling(bestillingId))
	},

	cancelBestilling(bestillingId, erOrganisasjon) {
		return Request.delete(Endpoints.removeBestilling(bestillingId, erOrganisasjon))
	},

	slettPerson(ident) {
		return Request.delete(Endpoints.slettPerson(ident))
	},

	slettPersonOgPartner(ident, _partnerident) {
		return Request.delete(Endpoints.slettPerson(ident))
	},

	importerPersoner: (gruppeId, request) => {
		return Request.post(Endpoints.gruppeBestillingImport(gruppeId), request)
	},

	importerPersonerFraPdl: (gruppeId, request) => {
		return Request.post(Endpoints.gruppeBestillingImportFraPdl(gruppeId), request)
	},

	getPersonFraPdl(ident) {
		return Request.get(Endpoints.personoppslag(ident))
	},
	getPersonerFraPdl(identer) {
		return Request.get(Endpoints.personoppslagMange(identer))
	},

	getArbeidsforhold(ident, miljoe) {
		return Request.get(Endpoints.getArbeidsforhold(ident, miljoe))
	},

	getFasteDatasettGruppe(gruppe) {
		return Request.get(Endpoints.getFasteDatasettTPSGruppe(gruppe))
	},

	getPersonnavn() {
		return Request.get(Endpoints.getPersonnavn())
	},

	getTransaksjonid(system, ident, bestillingsid) {
		return Request.get(Endpoints.getTransaksjonsid(system, ident, bestillingsid))
	},

	getDokarkivDokumentinfo(journalpostId, miljoe) {
		return Request.get(Endpoints.dokarkivDokumentinfo(journalpostId, miljoe))
	},

	//* Organisasjoner
	getOrganisasjonsnummerByUserId(userId) {
		return Request.get(Endpoints.organisasjonStatusByUser(userId))
	},

	createOrganisasjonBestilling(data) {
		return Request.post(Endpoints.organisasjonBestilling(), data)
	},

	getOrganisasjonBestillingStatus(bestillingId) {
		return Request.get(Endpoints.organisasjonStatusByBestillingId(bestillingId))
	},

	gjenopprettOrganisasjonBestilling(bestillingId, envs) {
		return Request.put(Endpoints.gjenopprettOrganisasjonBestilling(bestillingId, envs))
	},

	deleteOrganisasjonOrgnummer(orgnummer) {
		return Request.delete(Endpoints.deleteOrganisasjonOrgnummer(orgnummer))
	},

	//* Tags
	getTags() {
		return Request.get(Endpoints.getTags())
	},

	//* Excel
	getExcelFil(groupId) {
		return Request.getExcel(Endpoints.gruppeExcelFil(groupId))
	},

	importerPartner(groupId, ident) {
		return Request.putWithoutResponse(Endpoints.leggTilPersonIGruppe(groupId, ident, 'PDL'))
	},
}
