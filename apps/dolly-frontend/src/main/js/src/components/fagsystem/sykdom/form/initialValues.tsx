import { addWeeks } from 'date-fns'

export const initialValuesSykemelding = {
	aktivitet: [
		{
			grad: null as unknown as number,
			fom: new Date(),
			tom: addWeeks(new Date(), 2),
		},
	],
}
