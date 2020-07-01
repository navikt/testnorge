import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import JournalpostidVisning from '~/components/journalpostid/journalpostidVisning'

interface DokarkivVisning {
	data: Array<Dokarkiv>
	ident: string
}

type Dokarkiv = {
	dokumenter: Array<Dokument>
	journalfoerendeEnhet?: string
	kanal: string
	tema: string
	tittel: string
}

type Dokument = {
	brevkode: string
	tittel: string
}

type Bestilling = {
	dokarkiv?: Array<Dokarkiv>
}

export const DokarkivVisning = ({ data, ident }: DokarkivVisning) => {
	// Viser foreløpig bestillingsdata
	if (!data || data.length < 1 || !data[0]) return null

	return (
		<div>
			<SubOverskrift label="Dokumenter" iconKind="dokarkiv" />
			{data.map((dokument, idx) => {
				return (
					<div className="person-visning_content" key={idx}>
						<TitleValue title="Kanal" value={dokument.kanal} />
						<TitleValue title="Brevkode" value={dokument.dokumenter[0].brevkode} />
						<TitleValue title="Tittel" value={dokument.dokumenter[0].tittel} />
						<TitleValue title="Tema" value={dokument.tema} />
						<TitleValue title="Journalførende enhet" value={dokument.journalfoerendeEnhet} />
					</div>
				)
			})}
			<JournalpostidVisning system="DOKARKIV" ident={ident} />
		</div>
	)
}

DokarkivVisning.filterValues = (bestillinger: Array<Bestilling>) => {
	if (!bestillinger) return null
	return bestillinger.map((bestilling: any) => bestilling.dokarkiv)
}
