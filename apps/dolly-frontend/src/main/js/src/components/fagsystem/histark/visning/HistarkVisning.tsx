import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString, formatDate } from '@/utils/DataFormatter'
import Button from '@/components/ui/button/Button'
import { useTransaksjonsid } from '@/utils/hooks/useTransaksjonsid'
import JoarkDokumentService from '@/service/services/JoarkDokumentService'

type HistarkDokument = {
	ident: string
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

export default ({ dokument, ident }: HistarkDokument) => {
	const { transaksjonsid } = useTransaksjonsid('HISTARK', ident)
	const transaksjon = transaksjonsid?.[0]?.transaksjonId
	if (!dokument) {
		return null
	}
	return (
		<div>
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
			{transaksjon?.map((transaksjon: { dokumentInfoId: number }) => (
				<Button
					key={transaksjon.dokumentInfoId}
					className="flexbox--align-center csv-eksport-btn"
					kind="file-new-table"
					onClick={() => JoarkDokumentService.hentHistarkPDF(transaksjon.dokumentInfoId)}
				>
					HENT DOKUMENT #{transaksjon.dokumentInfoId}
				</Button>
			))}
		</div>
	)
}
