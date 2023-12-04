import { addMonths, setDate } from 'date-fns'

export const initialAlderspensjon = {
	kravFremsattDato: new Date(),
	iverksettelsesdato: setDate(addMonths(new Date(), 1), 1),
	saksbehandler: null,
	attesterer: null,
	uttaksgrad: 100,
	navEnhetId: null,
}
