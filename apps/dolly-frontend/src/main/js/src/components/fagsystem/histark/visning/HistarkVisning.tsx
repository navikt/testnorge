import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString, formatDate } from '@/utils/DataFormatter'
import Button from '@/components/ui/button/Button'
import JoarkDokumentService from '@/service/services/JoarkDokumentService'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'

type HistarkDokument = {
	transaksjon: any
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

export default ({ dokument, transaksjon }: HistarkDokument) => {
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
			<DollyFieldArray header={'Vedlegg'} data={[transaksjon]} nested>
				{(transaksjon: { dokumentInfoId: number }, idx: number) => {
					return (
						<div key={idx} className="person-visning_content">
							<TitleValue title="Filnavn" value={dokument.filnavn} />
							<TitleValue title="Dokumentinfo-ID" value={transaksjon.dokumentInfoId} />
							<Button
								className="flexbox--align-center csv-eksport-btn"
								kind="file-new-table"
								onClick={() => JoarkDokumentService.hentHistarkPDF(transaksjon.dokumentInfoId)}
							>
								VIS PDF
							</Button>
						</div>
					)
				}}
			</DollyFieldArray>
		</div>
	)
}
