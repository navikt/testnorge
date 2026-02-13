import Request from '@/service/services/Request'
import Endpoints from './DollyEndpoints'

export default {
	getGruppeById(gruppeId) {
		return Request.get(Endpoints.gruppeById(gruppeId))
	},

	createGruppe(data) {
		return Request.post(Endpoints.gruppe(), data)
	},

	updateGruppe(gruppeId, data) {
		const valgtGruppe = gruppeId || data.gruppeId
		return Request.put(Endpoints.gruppeById(valgtGruppe), data)
	},

	endreTilknytningGruppe(gruppeId, brukerId) {
		return Request.put(Endpoints.endreTilknytningGruppe(gruppeId, brukerId))
	},

	deleteGruppe(gruppeId) {
		return Request.delete(Endpoints.gruppeById(gruppeId))
	},

	createBestilling(gruppeId, data) {
		const valgtGruppe = gruppeId || data.gruppeId
		return Request.post(Endpoints.gruppeBestilling(valgtGruppe), data)
	},

	createBestillingFraEksisterendeIdenter(gruppeId, data) {
		const valgtGruppe = gruppeId || data.gruppeId
		return Request.post(Endpoints.gruppeBestillingFraEksisterendeIdenter(valgtGruppe), data)
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

	createBestillingLeggTilPaaPerson(ident, data) {
		return Request.put(Endpoints.leggTilPaaPerson(ident), data)
	},

	createBestillingLeggTilPaaGruppe(gruppeId, data) {
		const valgtGruppe = gruppeId || data.gruppeId
		return Request.put(Endpoints.leggTilPaaGruppe(valgtGruppe), data)
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

	//* Bestilling
	getBestillingerFragment(fragment) {
		return Request.get(Endpoints.bestillingerFragment(fragment))
	},

	//* Gruppe
	getGrupperFragment(fragment) {
		return Request.get(Endpoints.grupperFragment(fragment))
	},

	gjenopprettBestilling(bestillingId, envs) {
		return Request.post(Endpoints.gjenopprettBestilling(bestillingId, envs))
	},

	slettBestilling(bestillingId) {
		return Request.delete(Endpoints.slettBestilling(bestillingId))
	},

	cancelBestilling(bestillingId, erOrganisasjon) {
		return Request.delete(Endpoints.removeBestilling(bestillingId, erOrganisasjon))
	},

	slettPerson(ident) {
		return Request.delete(Endpoints.slettPerson(ident))
	},

	gjenopprettPerson(ident, miljoer) {
		return Request.post(Endpoints.gjenopprettPerson(ident, miljoer))
	},

	opprettMalFraPerson(ident, malNavn) {
		return Request.post(Endpoints.opprettMalFraPerson(ident, malNavn))
	},

	importerPersonerFraPdl: (gruppeId, request) => {
		const valgtGruppe = gruppeId || request.gruppeId
		return Request.post(Endpoints.gruppeBestillingImportFraPdl(valgtGruppe), request)
	},

	getAktoerFraPdl(aktoerId: string) {
		if (aktoerId.length !== 13) {
			return null
		}
		return Request.get(Endpoints.personoppslag(aktoerId))
	},
	getPersonerFraPdl(identer) {
		return Request.get(Endpoints.personoppslagMange(identer))
	},

	//* Team
	opprettTeam(data) {
		return Request.post(Endpoints.opprettTeam(), data)
	},

	redigerTeam(teamId, data) {
		return Request.put(Endpoints.redigerTeam(teamId), data)
	},

	leggTilBrukerITeam(teamId, brukerId) {
		return Request.post(Endpoints.leggTilFjernBrukerFraTeam(teamId, brukerId))
	},

	fjernBrukerFraTeam(teamId, brukerId) {
		return Request.delete(Endpoints.leggTilFjernBrukerFraTeam(teamId, brukerId))
	},

	slettTeam(teamId) {
		return Request.delete(Endpoints.slettTeam(teamId))
	},

	setRepresentererTeam(teamId) {
		return Request.put(Endpoints.setRepresentererTeam(teamId))
	},

	fjernRepresentererTeam() {
		return Request.delete(Endpoints.fjernRepresentererTeam())
	},

	lagreSoek(data, soekType) {
		return Request.post(Endpoints.lagreSoek(soekType), data)
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

	getTagsForIdent(ident) {
		return Request.get(Endpoints.getIdentTags(ident))
	},

	importerRelatertPerson(groupId, ident, master) {
		return Request.putWithoutResponse(Endpoints.leggTilPersonIGruppe(groupId, ident, master))
	},

	flyttPersonerTilGruppe(gruppeId, ident) {
		return Request.putWithoutResponse(Endpoints.flyttPersonerTilGruppe(gruppeId, ident))
	},

	slettMal(malId) {
		return Request.delete(Endpoints.malBestillingMedId(malId))
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
		return Request.putWithoutResponse(Endpoints.malBestillingMedId(malID, malNavn))
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

	lagreMalFraBestillingId(bestillingId, malNavn) {
		return Request.post(Endpoints.malBestillingMedBestillingId(bestillingId, malNavn)).catch(
			(error) => {
				console.error(error)
				throw error
			},
		)
	},

	lagreOrganisasjonMalFraBestillingId(bestillingId, malNavn) {
		return Request.post(
			Endpoints.organisasjonMalBestillingMedBestillingId(bestillingId, malNavn),
		).catch((error) => {
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
		return Request.putWithoutResponse(Endpoints.malBestillingOrganisasjon(malID, malNavn))
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

	personerSearch(request) {
		const getRegistreRequest = () => {
			if (!request?.registreRequest || request?.registreRequest.length === 0) {
				return null
			}
			let url = ''
			request?.registreRequest?.forEach((type, idx) =>
				idx === 0 ? (url += `?registreRequest=${type}`) : (url += `&registreRequest=${type}`),
			)
			return url
		}
		const registre = getRegistreRequest()
		return Request.post(Endpoints.personerSearch(registre), request)
			.then((response) => {
				return response
			})
			.catch((error) => {
				console.error(error)
				throw error
			})
	},
}
