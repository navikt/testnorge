import Request from "~/service/services/Request";

const kontoregisterUrl = '/testnav-kontoregister-person-proxy/kontoregister/api/kontoregister/v1/hent-konto'

export default {
    hentKonto(ident: string) {
        return Request.post(kontoregisterUrl, {"kontohaver": ident}).then((response) => {
            if (response != null) {
                return response
            }
        })
    }
}
