import React from 'react'
import DollyTable from '~/components/ui/dollyTable/DollyTable'

export const SearchResultVisning = (personListe: any) => {

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
			width: '10',
			dataField: 'alder'
		}
	]


	return (
		//@ts-ignore
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