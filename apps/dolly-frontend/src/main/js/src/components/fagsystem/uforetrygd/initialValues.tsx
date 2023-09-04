import {addMonths, getDaysInMonth, setDate} from "date-fns";

const treMaanederFrem = addMonths(new Date(), 3)

export const initialUforetrygd = {
    kravFremsattDato: new Date(),
    onsketVirkningsDato: setDate(addMonths(new Date(), 1), 1),
    uforetidspunkt: setDate(addMonths(new Date(), -1), 1),
    inntektForUforhet: 550000,
    uforegrad: 100,
    minimumInntektForUforhetType: null,
    saksbehandler: null,
    attesterer: null,
    navEnhetId: null,
    barnetilleggDetaljer: null
}

export const barnetilleggDetaljer = {
    barnetilleggType: 'FELLESBARN',
    forventedeInntekterSoker: [],
    forventedeInntekterEP: [],
}

export const forventedeInntekterSoker = {
    datoFom: setDate(addMonths(new Date(), 1), 1),
    datoTom: setDate(treMaanederFrem, getDaysInMonth(new Date(treMaanederFrem))),
    inntektType	string
    Enum:
        Array [ 5 ]
    belop	integer($int32)
}