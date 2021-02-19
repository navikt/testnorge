import Request from '~/service/services/Request'
import Endpoints from './DollyEndpoints'

export default {
	//* Grupper
	getGrupper() {
		return Request.get(Endpoints.gruppe())
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
		return Request.get(Endpoints.naviger(ident))
	},

	//* Bruker
	getBrukere() {
		return Request.get(Endpoints.bruker())
	},

	getBrukereById(brukerId) {
		return Request.get(Endpoints.brukerById())
	},

	getCurrentBruker() {
		return Request.get(Endpoints.currentBruker())
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

	//* Bestilling
	getBestillinger(gruppeId) {
		return Request.get(Endpoints.bestillinger(gruppeId))
	},

	getBestillingMaler() {
		return Request.get(Endpoints.bestillingMal())
	},

	getBestillingStatus(bestillingId) {
		return Request.get(Endpoints.bestillingStatus(bestillingId))
	},

	gjenopprettBestilling(bestillingId, envs) {
		return Request.post(Endpoints.gjenopprettBestilling(bestillingId, envs))
	},

	getConfig() {
		return Request.get(Endpoints.config())
	},

	postOpenAmBestilling(bestillingId) {
		return Request.post(Endpoints.openAmBestilling(bestillingId))
	},

	cancelBestilling(bestillingId) {
		return Request.delete(Endpoints.removeBestilling(bestillingId))
	},

	slettPerson(ident) {
		return Request.delete(Endpoints.slettPerson(ident))
	},

	importerPersoner: (gruppeId, request) => {
		return Request.post(Endpoints.gruppeBestillingImport(gruppeId), request)
	},

	//* Oppslag
	getEnhetByTknr(tknr) {
		return Request.get(Endpoints.enhetByTknr(tknr))
	},

	getPersonFraPdl(ident) {
		return Request.get(Endpoints.personoppslag(ident))
	},

	getFasteOrgnummer() {
		return Request.get(Endpoints.fasteOrgnummer())
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

	//* Joark
	getInntektsmeldingDokumentinfo(journalpostId, dokumentinfoId, miljoe) {
		return Request.get(Endpoints.inntektsmeldingDokumentinfo(journalpostId, dokumentinfoId, miljoe))
	},

	getDokarkivDokumentinfo(journalpostId, miljoe) {
		return Request.get(Endpoints.dokarkivDokumentinfo(journalpostId, miljoe))
	}
}
