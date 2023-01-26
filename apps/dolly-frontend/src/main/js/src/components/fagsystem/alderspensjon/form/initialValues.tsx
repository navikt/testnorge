import { addMonths, setDate } from 'date-fns'

export const initialAlderspensjon = {
	iverksettelsesdato: setDate(addMonths(new Date(), 1), 1),
	uttaksgrad: 100,
	relasjoner: [{ sumAvForvArbKapPenInntekt: null }],
}
