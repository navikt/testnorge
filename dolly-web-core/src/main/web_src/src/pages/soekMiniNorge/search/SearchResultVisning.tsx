import React from 'react'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'

interface SearchResultVisningProps {
	personListe: any
}

export const SearchResultVisning = ({personListe}:SearchResultVisningProps) => {
	if (!personListe || personListe.kilder[0].data.length===0) {
		return <ContentContainer>Søket gav ingen resultater.</ContentContainer>
	}

	const columns = [
		{
			text: 'Ident',
			width: '20',
			dataField: 'innhold.personIdent.id',
			unique: true
		},
		{
			text: 'Type',
			width: '20',
			dataField: 'innhold.personIdent.type',
		},
		{
			text: 'Navn',
			width: '30',
			dataField: 'innhold.navn.forkortet'
		},
		{
			text: 'Kjønn',
			width: '30',
			dataField: 'innhold.personInfo.kjoenn'
		},
		{
			text: 'Alder',
			width: '30',
			dataField: 'innhold.personInfo.datoFoedt',
			formatter: (cell:any, row:any) => {
				const foedselsdato = new Date(row.innhold.personInfo.datoFoedt)
				const diff_ms = Date.now() - foedselsdato.getTime()
				const age_dt = new Date(diff_ms)

				return Math.abs(age_dt.getUTCFullYear() - 1970)
			}
		}
	]

	return (
		<DollyTable
			data={personListe.kilder[0].data}
			columns={columns}
			pagination
			onExpand={() => (
				<h1>Test</h1>
			)}
		/>
	)
}