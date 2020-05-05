import React from 'react'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'

interface SearchResultVisningProps {
	personListe: Array<any>
	searchActive: boolean
}

export const SearchResultVisning = ({personListe, searchActive= false}:SearchResultVisningProps) => {

	if (!personListe || personListe.length === 0)
		return (
			<ContentContainer>
				Trykk på søk.
			</ContentContainer>
		)

	if (personListe.length <= 0 ) {
		return <ContentContainer>Søket gav ingen resultater.</ContentContainer>
	}

	const columns = [
		{
			text: 'Ident',
			width: '20',
			dataField: 'personIdent.id',
			unique: true
		},
		{
			text: 'Type',
			width: '20',
			dataField: 'personIdent.type',
		},
		{
			text: 'Navn',
			width: '30',
			dataField: 'navn.forkortet'
		},
		{
			text: 'Kjønn',
			width: '30',
			dataField: 'personInfo.kjoenn'
		},
		{
			text: 'Alder',
			width: '30',
			dataField: 'personInfo.datoFoedt',
			formatter: (cell:any, row:any) => {
				const foedselsdato = new Date(row.personInfo.datoFoedt)
				const diff_ms = Date.now() - foedselsdato.getTime()
				const age_dt = new Date(diff_ms)

				return Math.abs(age_dt.getUTCFullYear() - 1970)
			}
		}

	]


	return (
		<DollyTable
			data={personListe}
			columns={columns}
			pagination
			onExpand={() => (
				<h1>Test</h1>
			)}
		/>
	)
}