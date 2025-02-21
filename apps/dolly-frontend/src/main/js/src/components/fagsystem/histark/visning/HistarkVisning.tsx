import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString, formatDate } from '@/utils/DataFormatter'
import Button from '@/components/ui/button/Button'
import JoarkDokumentService from '@/service/services/JoarkDokumentService'

type HistarkDokument = {
	idx: number
	dokumentInfoId: any
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

export default ({ dokument, dokumentInfoId, idx }: HistarkDokument) => {
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
			<div style={{ width: '-webkit-fill-available' }}>
				<h3>Vedlegg</h3>
			</div>
			<div
				className="person-visning_content"
				style={{ backgroundColor: idx % 2 === 0 ? '#f7f7f7' : 'white', padding: '10px' }}
			>
				<TitleValue title="Filnavn" value={dokument.filnavn} />
				<TitleValue title="Dokumentinfo-ID" value={dokumentInfoId} />
				<Button
					style={{ marginLeft: 'auto', marginBottom: 'auto' }}
					className="csv-eksport-btn"
					kind="file-new-table"
					onClick={() => JoarkDokumentService.hentHistarkPDF(dokumentInfoId)}
				>
					VIS PDF
				</Button>
			</div>
		</div>
	)
}
