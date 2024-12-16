import Api from "@/api";

export default {
	getOrganisasjoner() {
		return Api.fetch('/altinn/organisasjoner', {method: 'GET'})
	}
}
