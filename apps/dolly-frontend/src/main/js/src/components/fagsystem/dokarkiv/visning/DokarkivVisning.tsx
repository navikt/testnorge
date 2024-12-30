import JoarkDokumentService, {
	Dokument,
	Journalpost,
} from '@/service/services/JoarkDokumentService'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import styled from 'styled-components'
import Button from '@/components/ui/button/Button'
import { showLabel } from '@/utils/DataFormatter'

type Props = {
	journalpost: Journalpost
	miljoe: string
}

const H4 = styled.h4`
	width: 100%;
`

export default ({ journalpost, miljoe }: Props) => {
	if (!journalpost) {
		return null
	}
	return (
		<div className="person-visning_content">
			<TitleValue title="Tittel" value={journalpost.tittel} />
			<TitleValue title="Kanal" value={journalpost.kanal} />
			<TitleValue title="Tema" value={journalpost.tema} />
			<TitleValue
				title="Behandlingstema"
				value={`${journalpost.behandlingstemanavn || ''} (${journalpost.behandlingstema})`}
			/>
			<TitleValue
				title="Fagsaksystem"
				value={showLabel('fagsaksystem', journalpost.sak?.fagsaksystem)}
			/>
			<TitleValue title="Journalførende enhet" value={journalpost.journalfoerendeEnhet} />
			<TitleValue title="Sakstype" value={showLabel('sakstype', journalpost.sak?.sakstype)} />
			<TitleValue title="Fagsak-ID" value={journalpost.sak?.fagsakId} />
			<TitleValue title="Journalpost-ID" value={journalpost.journalpostId} />

			{journalpost.avsenderMottaker ? (
				<>
					<H4>Avsender</H4>
					<TitleValue title="Type" value={journalpost.avsenderMottaker.type} />
					<TitleValue title="ID" value={journalpost.avsenderMottaker.id} />
					<TitleValue title="Navn" value={journalpost.avsenderMottaker.navn} />
				</>
			) : null}

			<DollyFieldArray header={'Vedlegg'} data={journalpost.dokumenter} nested>
				{(dokument: Dokument, idx: number) => {
					return (
						<div key={idx} className="person-visning_content">
							<TitleValue title="Tittel" value={dokument.tittel} />
							<TitleValue title="Dokumentinfo-ID" value={dokument.dokumentInfoId} />
							<Button
								className="flexbox--align-center csv-eksport-btn"
								kind="file-new-table"
								onClick={() =>
									JoarkDokumentService.hentPDF(
										journalpost.journalpostId,
										dokument.dokumentInfoId,
										miljoe,
									)
								}
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
