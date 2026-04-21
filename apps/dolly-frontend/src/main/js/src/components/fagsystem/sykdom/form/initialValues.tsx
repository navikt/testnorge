import { addWeeks } from 'date-fns'

export const initialValuesSykemelding = {
	type: 'VANLIG' as const,
	aktivitet: [
		{
			grad: null as unknown as number,
			reisetilskudd: false,
			fom: new Date(),
			tom: addWeeks(new Date(), 2),
		},
	],
}
