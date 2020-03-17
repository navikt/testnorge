import _isNil from "lodash/isNil";

export const getNavnOgFnrListe = (data) => {
    let listeMedNavnOgId = []
    data.forEach(function (value) {
        if (!_isNil(value.fornavn)) {
            const mellomnavn = !_isNil(value.mellomnavn) ? " " + value.mellomnavn : "";
            const navnOgFnr = (value.fornavn + mellomnavn + " " + value.etternavn).toUpperCase()
                + ": " + value.fnr;
            listeMedNavnOgId.push({value: navnOgFnr, label: navnOgFnr});
        }
    })
    return listeMedNavnOgId;
}

export const getNavnListe = (data) => {
    let listeMedNavn = []
    data.forEach(function (value) {
        if (!_isNil(value.fornavn)) {
            const mellomnavn = !_isNil(value.mellomnavn) ? " " + value.mellomnavn : "";
            const navn = value.fornavn + mellomnavn + " " + value.etternavn;
            listeMedNavn.push({value: navn.toUpperCase(), label: navn.toUpperCase()});
        }
    })
    return listeMedNavn;
}
