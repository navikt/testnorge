import React from 'react'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'

interface SearchResultVisningProps {
	searchActive: boolean
	soekOptions: string
}

export const SearchResultVisning = ({ searchActive, soekOptions}:SearchResultVisningProps) => {
	if (!searchActive)
		return (
			<ContentContainer>
				Ingen søk er gjort.
			</ContentContainer>
		)

	if(searchActive && soekOptions===''){
		return <ContentContainer>Vennligst fyll inn en eller flere verdier å søke på.</ContentContainer>
	}

	if (searchActive) {
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
			data={[]}
			columns={columns}
			pagination
			onExpand={() => (
				<h1>Test</h1>
			)}
		/>
	)
}