import Request from '../../Request'
import Endpoints from './DollyEndpoints'
import Utils from './Utils'
import { request } from 'http'

export default class DollyService {
	// UTILS
	static Utils = Utils

	// Grupper
	static getGrupper() {
		return Request.get(Endpoints.gruppe())
	}

	static getGruppeById(gruppeId) {
		return Request.get(Endpoints.gruppeById(gruppeId))
	}

	static getGruppeByUserId(userId) {
		return Request.get(Endpoints.gruppeByUser(userId))
	}

	static getGruppeByTeamId(teamId) {
		return Request.get(Endpoints.gruppeByTeam(teamId))
	}

	static createGruppe(data) {
		return Request.post(Endpoints.gruppe(), data)
	}

	static updateGruppe(gruppeId, data) {
		return Request.put(Endpoints.gruppeById(gruppeId), data)
	}

	static updateGruppeAttributter(gruppeId, data) {
		return Request.put(Endpoints.gruppeAttributter(gruppeId), data)
	}

	static updateGruppeIdenter(gruppeId, data) {
		return Request.put(Endpoints.gruppeIdenter(gruppeId, data))
	}

	static getBestillingStatus(gruppeId, data) {
		return Request.get(Endpoints.gruppeBestillingStatus(gruppeId), data)
	}

	static createBestilling(gruppeId, data) {
		return Request.post(Endpoints.gruppeBestilling(gruppeId), data)
	}

	// Team
	static getTeams() {
		return Request.get(Endpoints.team())
	}

	static getTeamsByUserId(userId) {
		return Request.get(Endpoints.teamByUser(userId))
	}

	static getTeamById(teamId) {
		return Request.get(Endpoints.teamById(teamId))
	}

	static createTeam(data) {
		return Request.post(Endpoints.team(), data)
	}

	static updateTeam(teamId, data) {
		return Request.put(Endpoints.teamById(teamId), data)
	}

	static deleteTeam(teamId) {
		return Request.delete(Endpoints.teamById(teamId))
	}

	static addTeamMedlemmer(teamId, userArray) {
		return Request.put(Endpoints.teamAddMember(teamId), userArray)
	}

	static removeTeamMedlemmer(teamId, userArray) {
		return Request.put(Endpoints.teamRemoveMember(teamId), userArray)
	}

	// Bruker
	static getBrukere() {
		return Request.get(Endpoints.bruker())
	}

	static getBrukereById(brukerId) {
		return Request.get(Endpoints.brukerById())
	}

	static getCurrentBruker() {
		return Request.get(Endpoints.currentBruker())
	}

	// Bestilling
	static getBestillingStatus(bestillingId) {
		return Request.get(Endpoints.bestillingStatus(bestillingId))
	}
}
