import React from 'react'
import _get from 'lodash/get'
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
	if (!data || data.length < 1) return null
	return (
		<div>
			<SubOverskrift label="Dokumenter" iconKind="dokarkiv" />
			{data.map((dokument, idx) => {
				if (!dokument) return null
				return (
					<div className="person-visning_content" key={idx}>
						<TitleValue title="Kanal" value={_get(dokument, 'dokarkiv.kanal')} />
						<TitleValue
							title="Brevkode"
							value={_get(dokument, 'dokarkiv.dokumenter[0].brevkode')}
						/>
						<TitleValue title="Tittel" value={_get(dokument, 'dokarkiv.dokumenter[0].tittel')} />
						<TitleValue title="Tema" value={_get(dokument, 'dokarkiv.tema')} />
						<TitleValue
							title="Journalførende enhet"
							value={_get(dokument, 'dokarkiv.journalfoerendeEnhet')}
						/>
					</div>
				)
			})}
			<JournalpostidVisning system="DOKARKIV" ident={ident} />
		</div>
	)
}

DokarkivVisning.filterValues = (bestillinger: Array<Bestilling>) => {
	if (!bestillinger) return null
	return bestillinger.filter((bestilling: any) => bestilling.dokarkiv)
}
