import { addMonths, setDate } from 'date-fns'

export const genInitialAlderspensjonSoknad = {
	kravFremsattDato: null,
	iverksettelsesdato: setDate(addMonths(new Date(), 1), 1),
	uttaksgrad: 100,
	relasjoner: [{ sumAvForvArbKapPenInntekt: null }],
	inkluderAfpPrivat: false,
	afpPrivatResultat: null,
	soknad: true,
}

export const genInitialAlderspensjonVedtak = {
	kravFremsattDato: new Date(),
	iverksettelsesdato: setDate(addMonths(new Date(), 1), 1),
	saksbehandler: null,
	attesterer: null,
	uttaksgrad: 100,
	navEnhetId: null,
	soknad: false,
}
