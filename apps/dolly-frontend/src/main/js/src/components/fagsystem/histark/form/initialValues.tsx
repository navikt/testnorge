import { getYear } from 'date-fns'

export const initialHistark = {
	tittel: '',
	antallSider: -1,
	skanner: '',
	skannested: 'FREDRIKSTAD',
	skanningsTidspunkt: new Date(),
	temakoder: [],
	enhetsnavn: '',
	enhetsnummer: '',
	startYear: getYear(new Date()) - 1,
	endYear: getYear(new Date()),
	fysiskDokument: '',
}
