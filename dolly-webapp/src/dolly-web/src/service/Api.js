import Request from './Request'
import Endpoints from './ContentApiEndpoints'

class Api {
	static getGrupper() {
		return Request.get(Endpoints.getGrupper())
	}

	static getGruppe(id) {
		return Request.get(Endpoints.getGrupper(id))
	}

	static postGruppe(gruppe) {
		return Request.post(Endpoints.postGruppe(), gruppe)
	}

	static updateGruppe(id, gruppe) {
		return Request.put(Endpoints.putGruppe(id), gruppe)
	}

	static getPersons() {
		return Request.get(Endpoints.getPersons())
	}

	static postPersons(personer) {
		return Request.post(Endpoints.postPersons(), personer)
	}

	static updatePerson(id, person) {
		return Request.put(Endpoints.putPersons(id), person)
	}

	static getTeams() {
		return Request.get(Endpoints.getTeams())
	}

	static postTeam(team) {
		return Request.post(Endpoints.postTeams(), team)
	}
}

export default Api
