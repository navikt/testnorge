import React from 'react'
import JoarkDokumentService, {Dokument, Journalpost} from '~/service/services/JoarkDokumentService'
import {TitleValue} from '~/components/ui/titleValue/TitleValue'
import {DollyFieldArray} from '~/components/ui/form/fieldArray/DollyFieldArray'
import styled from 'styled-components'

type Props = {
	journalpost: Journalpost
}

const H4 = styled.h4`
	width: 100%;
`

export default ({ journalpost }: Props) => (
	<div className="person-visning_content">
		<TitleValue title="Tittel" value={journalpost.tittel} />
		<TitleValue title="Kanal" value={journalpost.kanal} />
		<TitleValue title="Tema" value={journalpost.tema} />
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
				console.log('dokument: ', dokument) //TODO - SLETT MEG

				JoarkDokumentService.hentPDF(
					journalpost.journalpostId,
					dokument.dokumentInfoId,
					'q2', //TODO FIX: bestilling.miljoe,
					'ARKIV'
				)
				return (
					<div key={idx} className="person-visning_content">
						{/*{kapplah && kapplah.value && <a href={kapplah.value}>Download</a>}*/}
						<TitleValue title="Tittel" value={dokument.tittel} />
						<TitleValue title="Dokumentinfo-ID" value={dokument.dokumentInfoId} />
					</div>
				)
			}}
		</DollyFieldArray>
	</div>
)
