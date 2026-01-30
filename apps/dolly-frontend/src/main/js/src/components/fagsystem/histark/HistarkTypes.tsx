export type HistarkTypes = {
	dokumenter: [
		{
			temakoder: Array<string>
			enhetsnavn: string
			startYear: number
			endYear: number
			skanningsTidspunkt: Date
			skanner: string
			skannested: string
			tittel: string
		},
	]
}
