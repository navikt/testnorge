import React from 'react'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { HodejegerenApi } from '~/service/Api'
import { ManIconItem, WomanIconItem } from '~/components/ui/icon/IconItem'
import ResultatVisningConnecter from '~/pages/soekMiniNorge/search/ResultatVisning/ResultatVisningConnecter'
import { HodejegerenResponse } from '../hodejegeren/types'
import { Feedback } from '~/components/feedback'

interface SearchResultVisningProps {
	soekOptions: string
	searchActive: boolean
	soekNummer: number
	antallResultat: number
}

export const SearchResult = (props: SearchResultVisningProps) => {
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
			dataField: 'personIdent.id',
			unique: true
		},
		{
			text: 'Type',
			width: '20',
			dataField: 'personIdent.type'
		},
		{
			text: 'Navn',
			width: '50',
			dataField: 'navn.fornavn',
			formatter: (cell: any, row: any) => {
				return row.navn.fornavn + ' ' + row.navn.slektsnavn
			}
		},
		{
			text: 'Kjønn',
			width: '20',
			dataField: 'personInfo.kjoenn'
		},
		{
			text: 'Alder',
			width: '30',
			dataField: 'personInfo.datoFoedt',
			formatter: (cell: any, row: any) => {
				const foedselsdato = new Date(row.personInfo.datoFoedt)
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
				HodejegerenApi.soek(props.soekOptions, props.antallResultat).then(response => {
					if (response.data.length > 0) {
						return response.data.map(function(res: any) {
							return res.kilder[0].data[0].innhold
						})
					}
					return null
				})
			}
			render={(data: Array<Response>) => {
				if (!data) {
					return <ContentContainer>Søket gav ingen resultater.</ContentContainer>
				}

				return (
					<div>
						<DollyTable
							data={data}
							columns={columns}
							pagination
							iconItem={(bruker: HodejegerenResponse) =>
								bruker.personInfo.kjoenn === 'M' ? <ManIconItem /> : <WomanIconItem />
							}
							onExpand={(bruker: HodejegerenResponse) => (
								<ResultatVisningConnecter personId={bruker.personIdent.id} data={bruker} />
							)}
						/>
						<Feedback
							label="Hva var negativt med din opplevelse med bruk av Søk i Mini-Norge?"
							feedbackFor="Bruk av Søk i Mini Norge"
						/>
					</div>
				)
			}}
		/>
	)
}