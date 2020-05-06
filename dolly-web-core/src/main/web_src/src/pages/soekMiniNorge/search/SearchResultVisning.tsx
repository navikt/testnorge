import React from 'react'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { HodejegerenApi } from '~/service/Api'

interface SearchResultVisningProps {
	soekOptions: string
	searchActive: boolean
	soekNummer: number
}

export const SearchResultVisning = (props: SearchResultVisningProps) => {

	if (!props.searchActive) {
		return <ContentContainer>Ingen søk er gjort</ContentContainer>
	}
	if (props.soekOptions === '' && props.searchActive) {
		return <ContentContainer>Vennligst fyll inn en eller flere verdier å søke på.</ContentContainer>
	}

	const columns = [
		{
			text: 'Ident',
			width: '40',
			dataField: 'innhold.personIdent.id',
			unique: true
		},
		{
			text: 'Type',
			width: '20',
			dataField: 'innhold.personIdent.type'
		},
		{
			text: 'Navn',
			width: '50',
			dataField: 'innhold.navn.fornavn',
			formatter: (cell: any, row: any) => {
				return row.innhold.navn.fornavn + ' ' + row.innhold.navn.slektsnavn
			}
		},
		{
			text: 'Kjønn',
			width: '20',
			dataField: 'innhold.personInfo.kjoenn'
		},
		{
			text: 'Alder',
			width: '30',
			dataField: 'innhold.personInfo.datoFoedt',
			formatter: (cell: any, row: any) => {
				const foedselsdato = new Date(row.innhold.personInfo.datoFoedt)
				const diff_ms = Date.now() - foedselsdato.getTime()
				const age_dt = new Date(diff_ms)

				return Math.abs(age_dt.getUTCFullYear() - 1970)
			}
		}
	]

	return (
		<LoadableComponent
			key={props.soekNummer}
			onFetch={() =>
				HodejegerenApi.soek(props.soekOptions).then(response => {
					return response.data.length > 0 ? response.data[0].kilder[0].data : null
				})
			}
			render={(data: Array<Response>) => {
				if (!data) {
					return <ContentContainer>Søket gav ingen resultater.</ContentContainer>
				}

				return (
					<DollyTable data={data} columns={columns} pagination onExpand={() => <h1>Test</h1>} />
				)
			}}
		/>
	)
}