import { setDate } from 'date-fns'

export const initialAlderspensjon = {
	iverksettelsesdato: setDate(new Date(), 1),
	uttaksgrad: 100,
	sivilstand: null,
	sivilstatusDatoFom: null,
	relasjonListe: [],
	flyktning: false,
	utvandret: false,
}

export const initialRelasjon = {
	samboerFraDato: new Date(),
	dodsdato: null,
	varigAdskilt: false,
	fnr: null,
	samlivsbruddDato: null,
	harVaertGift: false,
	harFellesBarn: false,
	sumAvForvArbKapPenInntekt: null,
	relasjonType: null,
}
