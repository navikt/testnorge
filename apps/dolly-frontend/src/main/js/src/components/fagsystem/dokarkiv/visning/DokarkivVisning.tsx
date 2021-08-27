import React from 'react'
import {Dokument, Journalpost} from '~/service/services/JoarkDokumentService'
import {TitleValue} from '~/components/ui/titleValue/TitleValue'
import {DollyFieldArray} from '~/components/ui/form/fieldArray/DollyFieldArray'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'

type Props = {
	journalpost: Journalpost
}

export default ({ journalpost }: Props) => (
	<>
		<TitleValue title="Tittel" value={journalpost.tittel} />
		<TitleValue title="Kanal" value={journalpost.kanal} />
		<TitleValue title="Tema" value={journalpost.tema} />

		<SubOverskrift label="Avsender" iconKind="dokarkiv" />
		<TitleValue title="Type" value={journalpost.avsenderMottaker?.type} />
		<TitleValue title="ID" value={journalpost.avsenderMottaker?.id} />
		<TitleValue title="Navn" value={journalpost.avsenderMottaker?.navn} />

		<TitleValue title="Journalpost-ID" value={journalpost.journalpostId} />
		<DollyFieldArray header={'Vedlegg'} data={journalpost.dokumenter} nested>
			{(dokument: Dokument, idx: number) => (
				<div key={idx} className="person-visning_content">
					<TitleValue title="Tittel" value={dokument.tittel} />
					<TitleValue title="Dokumentinfo-ID" value={dokument.dokumentInfoId} />
				</div>
			)}
		</DollyFieldArray>
	</>
)
