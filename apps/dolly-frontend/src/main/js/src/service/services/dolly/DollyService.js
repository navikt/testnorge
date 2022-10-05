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
	getBestillingerFragment(fragment) {
		return Request.get(Endpoints.bestillingerFragment(fragment))
	},

	gjenopprettBestilling(bestillingId, envs) {
		return Request.post(Endpoints.gjenopprettBestilling(bestillingId, envs))
	},

	cancelBestilling(bestillingId, erOrganisasjon) {
		return Request.delete(Endpoints.removeBestilling(bestillingId, erOrganisasjon))
	},

	slettPerson(ident) {
		return Request.delete(Endpoints.slettPerson(ident))
	},

	slettPersonOgRelatertePersoner(ident, relatertPersonIdenter) {
		return Request.delete(Endpoints.slettPerson(ident)).then(() => {
			return Promise.all(
				relatertPersonIdenter.map((person) => {
					Request.delete(Endpoints.slettPerson(person.id)).catch((error) => {
						console.error(error)
					})
				})
			)
		})
	},

	importerPersoner: (gruppeId, request) => {
		return Request.post(Endpoints.gruppeBestillingImport(gruppeId), request)
	},

	importerPersonerFraPdl: (gruppeId, request) => {
		return Request.post(Endpoints.gruppeBestillingImportFraPdl(gruppeId), request)
	},

	getPersonFraPdl(ident, pdlMiljoe) {
		return Request.get(Endpoints.personoppslag(ident, pdlMiljoe))
	},
	getPersonerFraPdl(identer) {
		return Request.get(Endpoints.personoppslagMange(identer))
	},

	getArbeidsforhold(ident, miljoe) {
		return Request.get(Endpoints.getArbeidsforhold(ident, miljoe))
	},

	getPersonnavn() {
		return Request.get(Endpoints.getPersonnavn())
	},

	getTransaksjonid(system, ident, bestillingsid) {
		return Request.get(Endpoints.getTransaksjonsid(system, ident, bestillingsid))
	},

	//* Organisasjoner

	createOrganisasjonBestilling(data) {
		return Request.post(Endpoints.organisasjonBestilling(), data)
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

	getTagsForIdent(ident) {
		return Request.get(Endpoints.getIdentTags(ident))
	},

	//* Excel
	getExcelFil(groupId) {
		return Request.getExcel(Endpoints.gruppeExcelFil(groupId))
	},

	getOrgExcelFil(brukerId) {
		return Request.getExcel(Endpoints.orgExcelFil(brukerId))
	},

	importerRelatertPerson(groupId, ident, master) {
		return Request.putWithoutResponse(Endpoints.leggTilPersonIGruppe(groupId, ident, master))
	},

	slettMal(malId) {
		return Request.delete(Endpoints.malBestilling(malId))
			.then((response) => {
				if (!response.ok) {
					throw new Error(response.statusText)
				}
				return response
			})
			.catch((error) => {
				console.error(error)
				throw error
			})
	},

	endreMalNavn(malID, malNavn) {
		return Request.putWithoutResponse(Endpoints.malBestilling(malID), malNavn)
			.then((response) => {
				if (!response.ok) {
					throw new Error(response.statusText)
				}
				return response
			})
			.catch((error) => {
				console.error(error)
				throw error
			})
	},

	slettMalOrganisasjon(malId) {
		return Request.delete(Endpoints.malBestillingOrganisasjon(malId))
			.then((response) => {
				if (!response.ok) {
					throw new Error(response.statusText)
				}
				return response
			})
			.catch((error) => {
				console.error(error)
				throw error
			})
	},

	endreMalNavnOrganisasjon(malID, malNavn) {
		return Request.putWithoutResponse(Endpoints.malBestillingOrganisasjon(malID), malNavn)
			.then((response) => {
				if (!response.ok) {
					throw new Error(response.statusText)
				}
				return response
			})
			.catch((error) => {
				console.error(error)
				throw error
			})
	},

	getAlleOrganisasjonerPaaBruker(brukerId) {
		return Request.get(Endpoints.getOrganisasjoner(brukerId)).then((response) => {
			return response.data
		})
	},
}
