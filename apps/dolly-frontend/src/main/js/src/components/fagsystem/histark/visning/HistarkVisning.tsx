import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString, formatDate } from '@/utils/DataFormatter'

type HistarkDokument = {
	dokument: {
		antallSider: number
		enhetsNr: string
		enhetsNavn: string
		temaKodeSet: string[]
		fnrEllerOrgnr: string
		startaar: 2019
		sluttaar: 2023
		skanningstidspunkt: Date
		filnavn: string
		skanner: string
		skannerSted: string
		inneholderKlage: boolean
		sjekksum: string
	}
}

export default ({ dokument }: HistarkDokument) => {
	if (!dokument) {
		return null
	}
	return (
		<div className="person-visning_content">
			<TitleValue
				title="Temakoder"
				value={dokument?.temaKodeSet && arrayToString(dokument?.temaKodeSet)}
			/>
			<TitleValue title="Enhetsnavn" value={dokument.enhetsNavn} />
			<TitleValue title="Enhetsnummer" value={dokument.enhetsNr} />
			<TitleValue title="Startår" value={dokument.startaar} />
			<TitleValue title="Sluttår" value={dokument.sluttaar} />
			<TitleValue
				title="Skanningstidspunkt"
				value={dokument?.skanningstidspunkt && formatDate(dokument.skanningstidspunkt)}
			/>
			<TitleValue title="Skanner" value={dokument.skanner} />
			<TitleValue title="Skannested" value={dokument.skannerSted} />
			<TitleValue title="Filnavn" value={dokument.filnavn} />
		</div>
	)
}
