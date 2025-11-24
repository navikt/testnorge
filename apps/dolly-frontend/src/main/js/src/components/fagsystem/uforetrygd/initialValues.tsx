import { addMonths, getDaysInMonth, setDate } from 'date-fns'

const treMaanederFrem = addMonths(new Date(), 3)

export const initialUforetrygd = {
	uforetidspunkt: setDate(addMonths(new Date(), -1), 1),
	onsketVirkningsDato: setDate(addMonths(new Date(), 1), 1),
	inntektForUforhet: 550000,
	inntektEtterUforhet: 10000,
	uforegrad: 100,
	minimumInntektForUforhetType: null,
	saksbehandler: null,
	attesterer: null,
	navEnhetId: null,
	barnetilleggDetaljer: null,
}

export const barnetilleggDetaljer = {
	barnetilleggType: 'FELLESBARN',
	forventedeInntekterSoker: [],
	forventedeInntekterEP: [],
}

export const forventedeInntekterSokerOgEP = {
	datoFom: setDate(addMonths(new Date(), 1), 1),
	datoTom: setDate(treMaanederFrem, getDaysInMonth(new Date(treMaanederFrem))),
	inntektType: 'ARBEIDSINNTEKT',
	belop: 0,
}
