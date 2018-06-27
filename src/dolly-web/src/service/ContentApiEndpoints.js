// TODO: lag config for denne
const uri = 'http://localhost:8080/api/v1'

class ContentApiEndpoints {
	static getGrupper() {
		return uri + '/testgruppe'
	}

	static getGruppe(id) {
		return uri + '/testgruppe/id'
	}

	static postGruppe() {
		return uri + '/testgruppe'
	}

	static putGruppe(id) {
		return uri + '/testgruppe/id'
	}

	static getPersons() {
		return uri + '/persons'
	}

	static postPersons() {
		return uri + '/persons'
	}

	static putPersons(id) {
		return uri + '/persons/' + id
	}

	static getTeams() {
		return uri + '/teams'
	}

	static postTeams() {
		return uri + '/teams'
	}
}

export default ContentApiEndpoints
