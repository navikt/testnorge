import { setDate } from 'date-fns'

export const initialAlderspensjon = {
	iverksettelsesdato: setDate(new Date(), 1),
	uttaksgrad: 100,
	sivilstand: null,
	sivilstatusDatoFom: null,
}
