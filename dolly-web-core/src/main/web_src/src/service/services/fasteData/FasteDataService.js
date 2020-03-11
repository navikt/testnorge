import Request from '~/service/services/Request'
import ConfigService from '~/service/Config'

const getFasteDataBaseUrl = () => ConfigService.getDatesourceUrl('fasteData')

export default {
    getFasteDataTps() {
        const endpoint = getFasteDataBaseUrl() + '/faste-data/tps';
        const resultat = Request.getWithoutCredentials(endpoint);

        let fnrListe = [];
        for (let persondata in resultat){
            fnrListe = fnrListe + persondata.fnr;
        }
        return fnrListe;
    }
}
